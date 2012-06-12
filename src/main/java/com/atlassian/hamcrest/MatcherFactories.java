package com.atlassian.hamcrest;

import static org.hamcrest.Matchers.is;

import org.hamcrest.Matcher;

/**
 * Utility {@link MatcherFactory}s.
 */
public final class MatcherFactories
{
    /**
     * A {@code MatcherFactory} which always creates and returns the Hamcrest {@code Matcher} corresponding to
     * {@code is(equalTo(expected))}.
     * @return the {@code Matcher} corresponding to {@code is(equalTo(expected))}
     */
    public static MatcherFactory isEqual()
    {
        return IsEqualMatcherFactory.INSTANCE;
    }
    
    private static enum IsEqualMatcherFactory implements MatcherFactory
    {
        INSTANCE;
        
        public <T> Matcher<? super T> newEqualMatcher(T expected, MatcherFactory baseMatcherFactory, DisjointSet<Object> equiv)
        {
            return is(org.hamcrest.Matchers.equalTo(expected));
        }
    }

}
