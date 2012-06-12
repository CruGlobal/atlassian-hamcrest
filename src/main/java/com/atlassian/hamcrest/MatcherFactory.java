package com.atlassian.hamcrest;

import org.hamcrest.Matcher;

/**
 * Used when creating {@link Matcher}s for the object fields being compared.
 */
public interface MatcherFactory
{
    /**
     * Creates and returns a {@code Matcher} to be used for the expected value.
     * 
     * @param <T> type of the values to match
     * @param expected the expected value
     * @param baseMatcherFactory the base {@code MatcherFactory} which can be used when recursively building a matcher
     * @param equiv a disjoint set of equivalence classes computed during the recursive match
     * @return {@code Matcher} to be used for the expected value
     */
    <T> Matcher<? super T> newEqualMatcher(T expected, MatcherFactory baseMatcherFactory, DisjointSet<Object> equiv);
}