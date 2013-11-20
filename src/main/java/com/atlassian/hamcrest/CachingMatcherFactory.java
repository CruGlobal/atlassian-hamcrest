package com.atlassian.hamcrest;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.hamcrest.Matcher;

/**
 * @author Matt Drees
 */
public class CachingMatcherFactory implements MatcherFactory {

    private MatcherFactory actualFactory;

    private Cache<Object, Object> cache;

    public CachingMatcherFactory(MatcherFactory actualFactory) {

        cache = CacheBuilder.newBuilder()
            .weakKeys()
            .build();

        this.actualFactory = actualFactory;
    }

    @Override
    public <T> Matcher<? super T> newEqualMatcher(
        final T expected, final MatcherFactory baseMatcherFactory, final DisjointSet<Object> equiv) {

        if (expected == null)
            return actualFactory.newEqualMatcher(expected, this, equiv);

        Object cachedMatcher = cache.getIfPresent(expected);
        if (cachedMatcher != null)
            return (Matcher<? super T>) cachedMatcher;
        else
        {
            Matcher<? super T> matcher =
                actualFactory.newEqualMatcher(expected, this, equiv);
            cache.put(expected, matcher);
            return matcher;
        }
    }

}
