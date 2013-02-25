package com.atlassian.hamcrest;

import static org.hamcrest.Matchers.is;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.hamcrest.Matcher;

import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public static MatcherFactory setIsDeeplyEqual()
    {
        return SetDeepIsMatcherFactory.INSTANCE;
    }

    public  static MatcherFactory listIsDeeplyEqual()
    {
        return ListDeepIsMatcherFactory.INSTANCE;
    }

    public static Map<Matcher<Class<?>>, MatcherFactory> collectionHandlingMatcherFactories()
    {
        Map<Matcher<Class<?>>, MatcherFactory> factories = Maps.newHashMap();
        factories.put(ClassMatchers.isAssignableTo(Set.class), setIsDeeplyEqual());
        factories.put(ClassMatchers.isAssignableTo(List.class), listIsDeeplyEqual());
        return factories;
    }

    private static enum IsEqualMatcherFactory implements MatcherFactory
    {
        INSTANCE;
        
        public <T> Matcher<? super T> newEqualMatcher(T expected, MatcherFactory baseMatcherFactory, DisjointSet<Object> equiv)
        {
            return is(org.hamcrest.Matchers.equalTo(expected));
        }
    }

    private static enum SetDeepIsMatcherFactory implements MatcherFactory
    {
        INSTANCE;

        public <T> Matcher<? super T> newEqualMatcher(T expected, MatcherFactory baseMatcherFactory, DisjointSet<Object> equiv)
        {
            return new SetDeepIsEqualMatcher<T>((Set<?>) expected, baseMatcherFactory, equiv);
        }
    }


    private static enum ListDeepIsMatcherFactory implements MatcherFactory
    {
        INSTANCE;

        public <T> Matcher<? super T> newEqualMatcher(T expected, MatcherFactory baseMatcherFactory, DisjointSet<Object> equiv)
        {
            return new ListDeepIsEqualMatcher<T>((List<?>) expected, baseMatcherFactory, equiv);
        }
    }


}
