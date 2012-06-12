package com.atlassian.hamcrest;

import org.hamcrest.Matcher;

/**
 * Creates new instances of the {@link ReflectivelyEqual} matcher.
 */
final class ReflectiveEqualFactory implements MatcherFactory
{
    public <T> Matcher<? super T> newEqualMatcher(T expected, MatcherFactory baseMatcherFactory, DisjointSet<Object> equiv)
    {
        return new ReflectivelyEqual<T>(expected, baseMatcherFactory, equiv);
    }
}