package com.atlassian.hamcrest;

import static com.atlassian.hamcrest.ClassMatchers.isArray;
import static com.atlassian.hamcrest.MatcherFactories.isEqual;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * A matcher that does a deep equals comparison of objects using reflection.  This allows testers to compare arbitrary
 * objects, whether they implement the {@link Object#equals} method or not.  It only tries to match two objects of the 
 * exact same type.  If one type is a sub-type of another, the match will fail.  The actual and expected objects are
 * compared by reflectively finding the types' declared, non-static, non-transient fields and the declared, non-static,
 * non-transient fields of its parents.  There is a core set of matchers used to compare the values of primitive field
 * types and arrays.  If the field is an array, then each element of the array is compared.
 * 
 * <p>A tester can also provide a way to create custom {@code Matcher}s for field types.  To provide custom matchers
 * for certain field types, you pass in a {@code Map} that indicates the type of the field you want to use a custom
 * matcher for and how to create the custom matcher.  Hamcrest {@link Matcher}s are used to indicate the field type,
 * so you can use {@code equalTo} to match types exactly or create your own to match subclasses, etc.  Once a matching
 * type is found, a {@code Matcher} is created using the {@link MatcherFactory}.
 * 
 * <p>As an example of how this works, you can register a {@code MatcherFactory} which will be used when the field type
 * is a {@link java.security.Key}.  For this example, we really want a type matcher that checks if the type of the value
 * implements the {@code Key} interface, because it will never match exactly.  To do that, we'll use the 
 * {@link Matchers#typeCompatibleWith(Class)} matcher and we'll say we want keys to match exactly using the in-built
 * {@link MatcherFactories#isEqual()} {@code MatcherFactory}.
 * 
 * <pre><code>
 *     Map<Matcher<Class<?>>, MatcherFactory> matcherFactories = new HashMap<Matcher<Class<?>>, MatcherFactory>();
 *     matcherFactories.put(typeCompatibleWith(Key.class), isEqual());
 *     assertThat(actualValue, is(deeplyEqualTo(expectedValue, matcherFactories)));
 * </code></pre>
 * 
 * Because that isn't exactly the clearest code, it is highly recommended that you create utility methods that
 * encapsulate the creation of the the {@code MatcherFactory} maps and registration of them to make your tests easier
 * to read.
 */
public class DeepIsEqual<T> extends DiagnosingMatcher<T>
{
    /**
     * The main matcher for the objects, which will be composed of other matchers for the fields of complex objects.
     */
    private final Matcher<? super T> valueMatcher;
    
    private DeepIsEqual(T expected, MatcherFactory matcherFactory)
    {
        if (expected == null)
        {
            valueMatcher = nullValue();
        }
        else
        {
            valueMatcher = matcherFactory.newEqualMatcher(expected, matcherFactory, new DisjointSet<Object>());
        }
    }

    /**
     * Checks that the type of {@code actual} matches the type of the expected value and then that the composite object
     * values are equal.
     */
    @Override
    protected boolean matches(Object actual, Description mismatchDescription)
    {
        if (!valueMatcher.matches(actual))
        {
            valueMatcher.describeMismatch(actual, mismatchDescription);
            return false;
        }
        return true;
    }

    public void describeTo(Description description)
    {
        CycleBreakingDescription cycleBreakingDescription = new CycleBreakingDescription(new IndentingDescription(description));
        cycleBreakingDescription.appendDescriptionOf(valueMatcher);
        cycleBreakingDescription.flushDescription();
    }
    
    /**
     * Returns a {@code Matcher} which compares two objects reflectively.
     * 
     * @param <T> type of the objects to compare
     * @param operand the expected value
     * @return {@code Matcher} which compares two objects reflectively
     */
    @Factory
    public static <T> Matcher<? super T> deeplyEqualTo(T operand)
    {
        return deeplyEqualTo(operand, ImmutableMap.<Matcher<Class<?>>, MatcherFactory>of());
    }

    /**
     * Returns a {@code Matcher} which compares 2 objects reflectively and uses the custom {@code MatcherFactory}s to
     * determine how to match certain types of fields.
     * 
     * @param <T> type of the objects to compare
     * @param operand the expected value
     * @param extraMatcherFactories {@code MatcherFactory}s to use for the fields with types matching the key {@code Matcher}
     * @return {@code Matcher} which compares 2 objects reflectively
     * @see DeepIsEqual
     */
    @Factory
    public static <T> Matcher<? super T> deeplyEqualTo(T operand, Map<Matcher<Class<?>>, MatcherFactory> extraMatcherFactories)
    {
        return new DeepIsEqual<T>(
            operand, new CachingMatcherFactory(
            new ReflectiveObjectMatcherFactory(
                ImmutableList.of(
                    extraMatcherFactories,
                    Primitives.FACTORIES,
                    MatcherFactories.collectionHandlingMatcherFactories()))));
    }

    private static final class Primitives
    {
        /**
         * {@code MatcherFactory}s to use for primitive types, arrays, and well-known value types.
         */
        private static final Map<Matcher<Class<?>>, MatcherFactory> FACTORIES = Collections.unmodifiableMap(
            new HashMap<Matcher<Class<?>>, MatcherFactory>()
            {{
                put(Matchers.<Class<?>>equalTo(Boolean.class), isEqual());
                put(Matchers.<Class<?>>equalTo(Byte.class), isEqual());
                put(Matchers.<Class<?>>equalTo(Short.class), isEqual());
                put(Matchers.<Class<?>>equalTo(Integer.class), isEqual());
                put(Matchers.<Class<?>>equalTo(Long.class), isEqual());
                put(Matchers.<Class<?>>equalTo(Float.class), isEqual());
                put(Matchers.<Class<?>>equalTo(Double.class), isEqual());
                put(Matchers.<Class<?>>equalTo(Character.class), isEqual());

                put(Matchers.<Class<?>>equalTo(String.class), isEqual());
                put(Matchers.<Class<?>>equalTo(BigDecimal.class), isEqual());
                put(Matchers.<Class<?>>equalTo(BigInteger.class), isEqual());

                put(isArray(), new ArrayEqualFactory());
            }}
        );

    }
}
