package com.atlassian.hamcrest;

import java.util.concurrent.ConcurrentMap;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.MapMaker;

/**
 * Helpful {@link Function}s.
 */
final class Functions
{

    /**
     * Returns a {@code Function} which caches the result of another function.  This is useful when used in concert
     * with {@link Iterables#transform(Iterable, Function)} when applying the function is expensive and will always
     * return the same value.
     * 
     * @param <F> type of the input value
     * @param <T> type of the output value
     * @param f function whose results should be cached
     * @return {@code Function} which caches the result of another function
     * @see http://code.google.com/p/google-collections/issues/detail?id=190
     */
    static <F, T> Function<F, T> cache(Function<F, T> f)
    {
        return new CachingFunction<F, T>(f);
    }

    private static final class CachingFunction<F, T> implements Function<F, T>
    {
        private final ConcurrentMap<F, T> cache = new MapMaker().makeMap();
        private final Function<F, T> function;
        
        public CachingFunction(Function<F, T> function)
        {
            this.function = function;
        }

        public T apply(F from)
        {
            if (cache.containsKey(from))
            {
                return cache.get(from);
            }
            T computedValue = function.apply(from);
            T cachedValue = cache.putIfAbsent(from, computedValue);
            return cachedValue == null ? computedValue : cachedValue;
        }
    }

}
