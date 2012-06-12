package com.atlassian.hamcrest;

import static org.hamcrest.Matchers.nullValue;

import java.util.Map;

import org.hamcrest.Matcher;

/**
 * Top level {@code MatcherFactory} used for doing the {@code deeplyEqualTo} comparison.  Creates a matcher in based
 * on this criteria:
 * 
 * <ol>
 * <li>If the expected value is null, returns the {@link org.hamcrest.Matchers#nullValue()} matcher.</li>
 * <li>If the expected value matches any of the field matcher factory keys, the {@code MatcherFactory} is used.</li>
 * <li>Otherwise, a matcher which compares objects reflectively is returned.</li>
 * </ol>
 */
final class ReflectiveObjectMatcherFactory implements MatcherFactory
{
    private final MatcherFactory fallbackFactory = new ReflectiveEqualFactory();
    private final Iterable<Map<Matcher<Class<?>>, MatcherFactory>> fieldMatcherFactories;

    ReflectiveObjectMatcherFactory(Iterable<Map<Matcher<Class<?>>, MatcherFactory>> fieldMatcherFactories)
    {
        this.fieldMatcherFactories = fieldMatcherFactories;
    }
    
    public <T> Matcher<? super T> newEqualMatcher(T expected, MatcherFactory baseMatcherFactory, DisjointSet<Object> equiv)
    {
        if (expected == null)
        {
            return nullValue();
        }
        for (Map<Matcher<Class<?>>, MatcherFactory> factories : fieldMatcherFactories)
        {
            for (Map.Entry<Matcher<Class<?>>, MatcherFactory> entry : factories.entrySet())
            {
                if (entry.getKey().matches(expected.getClass()))
                {
                    return entry.getValue().newEqualMatcher(expected, baseMatcherFactory, equiv);
                }
            }
        }            
        return fallbackFactory.newEqualMatcher(expected, baseMatcherFactory, equiv);
    }
}