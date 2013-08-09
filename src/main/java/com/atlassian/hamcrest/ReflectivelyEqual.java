package com.atlassian.hamcrest;

import static com.atlassian.hamcrest.Arrays.asList;
import static com.atlassian.hamcrest.Predicates.isStatic;
import static com.atlassian.hamcrest.Predicates.isTransient;
import static com.atlassian.hamcrest.Functions.cache;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Predicates.or;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.Iterator;

import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Matcher;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

/**
 * {@code Matcher} that does the work of reflectively comparing fields of objects.  It lazily creates, and caches,
 * the matchers for the fields declared by type of expected value.  When doing the matching, it iterates over the
 * field {@code Matcher}s, extracts the field value from the actual object being compared against, and checks if the
 * field {@code Matcher} matches the corresponding actual field value.
 */
class ReflectivelyEqual<T> extends DiagnosingMatcher<T>
{
    /**
     * Matcher which checks the type of the actual value against the type of the expected value.  If they don't match
     * exactly then the whole match should fail.
     */
    private final Matcher<?> typeMatcher;

    private final Iterable<ReflectivelyEqual.FieldMatcher> fieldMatchers;
    private final DisjointSet<Object> equiv;
    private final T expected;
    
    public ReflectivelyEqual(T expected, MatcherFactory baseMatcherFactory, DisjointSet<Object> equiv)
    {
        this.fieldMatchers = transform(matchableFieldsOf(expected.getClass()), cache(toValueMatchers(expected, baseMatcherFactory)));
        this.equiv = equiv;
        this.expected = expected;
        this.typeMatcher = is(equalTo(expected.getClass()));
    }

    @Override
    protected boolean matches(Object actual, Description mismatchDescription)
    {
        if (actual == null)
        {
            mismatchDescription.appendValue(null);
            return false;
        }
        if (!typeMatcher.matches(actual.getClass()))
        {
            typeMatcher.describeMismatch(actual.getClass(), mismatchDescription);
            return false;
        }
        if (equiv.equivalent(actual, expected))
        {
            return true;
        }
        // AHAM-1 : Recursively assume that the objects are equal when comparing their fields
        equiv.union(actual, expected);
        boolean mismatchFound = false;
        for (ReflectivelyEqual.FieldMatcher fieldMatcher : fieldMatchers)
        {
            Field field = fieldMatcher.field;
            Matcher<?> matcher = fieldMatcher.matcher;
            Object actualFieldValue = get(field, actual);
            
            if (matcher.matches(actualFieldValue))
            {
                continue;
            }
            if (!mismatchFound)
            {
                mismatchDescription.appendText("{");
                mismatchFound = true;
            }
            else
            {
                mismatchDescription.appendText(", ");
            }
            mismatchDescription.appendText(field.getName()).appendText(" ");
            matcher.describeMismatch(actualFieldValue, mismatchDescription);
        }
        
        if (mismatchFound)
        {
            mismatchDescription.appendText("}");
        }
        equiv.deunion();
        return !mismatchFound;
    }

    public void describeTo(Description desc)
    {
        desc.appendText("{");
        for (Iterator<ReflectivelyEqual.FieldMatcher> it = fieldMatchers.iterator(); it.hasNext(); )
        {
            ReflectivelyEqual.FieldMatcher fieldMatcher = it.next();
            desc.appendText(fieldMatcher.field.getName())
                .appendText(" ")
                .appendDescriptionOf(fieldMatcher.matcher);
            if (it.hasNext())
            {
                desc.appendText(", ");
            }
        }
        desc.appendText("}");
    }
    
    /**
     * Recursively builds the list of all fields that are matchable in a class.  Matchable fields are all fields
     * declared by the class and its superclasses that are neither static nor transient.
     * 
     * @param cls class to get all the matchable fields of
     * @return all fields that we can use to match objects
     */
    private Iterable<Field> matchableFieldsOf(Class<?> cls)
    {
        if (cls == null)
        {
            return ImmutableList.of();
        }
        Field[] fields = cls.getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);
        return concat(matchableFieldsOf(cls.getSuperclass()), filter(asList(fields), not(or(isTransient(), isStatic()))));
    }
    
    /**
     * Returns a {@code Function} which can be used to turn {@code Field}s into {@code FieldMatcher}s.
     *  
     * @param expected the expected object value to extract the field value from
     * @param matcherFactory factory to use to create matchers for the fields
     * @return {@code Function} which can be used to turn {@code Field}s into {@code FieldMatcher}s
     */
    private Function<Field, ReflectivelyEqual.FieldMatcher> toValueMatchers(final T expected, final MatcherFactory matcherFactory)
    {
        return new Function<Field, ReflectivelyEqual.FieldMatcher>()
        {
            public ReflectivelyEqual.FieldMatcher apply(Field f)
            {
                return new FieldMatcher(f, matcherFactory.newEqualMatcher(get(f, expected), matcherFactory, equiv));
            }
        };
    };
    
    /**
     * Container for a {@link Field} and the {@link Matcher} which should be used when comparing values of the field.
     */
    private static class FieldMatcher
    {
        final Field field;
        final Matcher<?> matcher;
        
        FieldMatcher(Field field, Matcher<?> matcher)
        {
            this.field = field;
            this.matcher = matcher;
        }
    }
    
    private static Object get(Field field, Object actual) throws InternalError
    {
        try
        {
            return field.get(actual);
        }
        catch (IllegalAccessException e)
        {
            // this can't happen. Would get a Security exception instead
            // throw an error in case the impossible happens.
            throw new InternalError("Unexpected IllegalAccessException");
        }
    }
}