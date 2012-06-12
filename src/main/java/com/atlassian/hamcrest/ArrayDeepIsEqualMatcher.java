package com.atlassian.hamcrest;

import static com.google.common.collect.Iterables.size;
import static com.google.common.collect.Iterables.transform;

import java.lang.reflect.Array;

import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Matcher;

import com.google.common.base.Function;
import static com.atlassian.hamcrest.Functions.cache;

/**
 * {@link Matcher} for arrays.  It is composed of {@code Matcher}s for each element in the array  The element
 * matchers are created from the {@code MatcherFactory} provided when the ArrayDeepIsEqualMatcher is created.
 * The element matchers are also lazily created and cached for future use.
 * 
 * @param <T> type of the elements in the array
 */
class ArrayDeepIsEqualMatcher<T> extends DiagnosingMatcher<T>
{
    private final int expectedSize;
    private final Iterable<Matcher<?>> matchers;
    private final DisjointSet<Object> equiv;

    public ArrayDeepIsEqualMatcher(Iterable<?> expected, MatcherFactory baseMatcherFactory, DisjointSet<Object> equiv)
    {
        this.expectedSize = size(expected);
        this.matchers = transform(expected, cache(toMatchers(baseMatcherFactory)));
        this.equiv = equiv;
    }

    private Function<Object, Matcher<?>> toMatchers(final MatcherFactory matcherFactory)
    {
        return new Function<Object, Matcher<?>>()
        {
            public Matcher<?> apply(Object element)
            {
                return matcherFactory.newEqualMatcher(element, matcherFactory, equiv);
            }
        };
    }

    @Override
    protected boolean matches(Object actual, Description mismatchDescription)
    {
        if (expectedSize != Array.getLength(actual))
        {
            // TODO can we do something better? try and figure out missing elements and their position maybe?
            describeTo(mismatchDescription);
            return false;
        }
        boolean mismatchFound = false;
        int index = 0;
        for (Matcher<?> matcher : matchers)
        {
            Object element = Array.get(actual, index);
            if (matcher.matches(element))
            {
                index++;
                continue;
            }
            if (!mismatchFound)
            {
                mismatchDescription.appendText("[");
                mismatchFound = true;
            }
            else
            {
                mismatchDescription.appendText(", ");
            }
            mismatchDescription.appendText("[").appendValue(index).appendText("] => ");
            matcher.describeMismatch(element, mismatchDescription);
            index++;
        }
        if (mismatchFound)
        {
            mismatchDescription.appendText("]");
        }

        return !mismatchFound;
    }

    public void describeTo(Description desc)
    {
        desc.appendText("[");
        int index = 0;
        for (Matcher<?> matcher : matchers)
        {
            if (index > 0)
            {
                desc.appendText(", ");
            }
            desc.appendText("[")
                .appendValue(index)
                .appendText("] => ")
                .appendDescriptionOf(matcher);
            index++;
        }
    }
}