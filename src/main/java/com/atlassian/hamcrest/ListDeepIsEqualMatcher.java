package com.atlassian.hamcrest;

import com.google.common.base.Function;
import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Matcher;

import java.util.List;

import static com.atlassian.hamcrest.Functions.cache;
import static com.google.common.collect.Iterables.size;
import static com.google.common.collect.Iterables.transform;

/**
 * {@link org.hamcrest.Matcher} for lists.  It is composed of {@code Matcher}s for each element in the array  The element
 * matchers are created from the {@code MatcherFactory} provided when the ListDeepIsEqualMatcher is created.
 * The element matchers are also lazily created and cached for future use.
 *
 * @param <T> type of the elements in the array
 */
class ListDeepIsEqualMatcher<T> extends DiagnosingMatcher<T>
{
    private final int expectedSize;
    private final Iterable<Matcher<?>> matchers;
    private final DisjointSet<Object> equiv;

    public ListDeepIsEqualMatcher(Iterable<?> expected, MatcherFactory baseMatcherFactory, DisjointSet<Object> equiv)
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
        if (actual == null)
        {
            mismatchDescription.appendText("is null");
            return false;
        }


        if (! (actual instanceof List<?>))
        {
            mismatchDescription.appendText("not a List, but a ")
                    .appendText(actual.getClass().getName());
            return false;
        }

        @SuppressWarnings("unchecked") //just checked this above
                List<T> actualAsList = (List<T>) actual;
        if (expectedSize != actualAsList.size())
        {
            // TODO can we do something better? try and figure out missing elements and their position maybe?
            mismatchDescription.appendText("size should be ")
                    .appendText(String.valueOf(expectedSize))
                    .appendText(", but is ")
                    .appendValue(actualAsList.size());
            return false;
        }
        boolean mismatchFound = false;
        int index = 0;
        for (Matcher<?> matcher : matchers)
        {
            Object element = actualAsList.get(index);
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