package com.atlassian.hamcrest;

import static com.atlassian.hamcrest.Functions.cache;

import java.util.Map;
import java.util.Set;

import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import com.google.common.base.Function;
import com.google.common.collect.Sets;

/**
 * {@link org.hamcrest.Matcher} for {@code Map}s.  It is composed of {@code Matcher}s for each key and each value in the expected map.
 * The key/value matchers are created from the {@code MatcherFactory} provided when the MapDeepIsEqualMatcher is created.
 * The matchers are also cached for future use.
 *
 * This matcher operates on the expected map's entrySet in a fashion identical to how {@code com.atlassian.hamcrest.SetDeepIsEqualMatcher}
 * operates on generic sets.  For a given map entry, the key and value must both match in order for the map entry to be considered to match.
 *
 * @param <M> represents the type of things this matcher matches (ie, Maps).  Used only to make the compiler happy
 *           with {@code com.atlassian.hamcrest.MatcherFactories.MapDeepIsMatcherFactory}.
 * @param <K> represents the type of keys in the maps this matches (not strictly needed, but helps make this file's code nicer)
 * @param <V> represents the type of values in the maps this matches (not strictly needed, but helps make this file's code nicer)
 */
//TODO: there is probably too much copy/paste between this and ListDeepIsEqualMatcher and ArrayDeepIsEqualMatcher
class MapDeepIsEqualMatcher<M, K, V> extends DiagnosingMatcher<M>
{
    private final int expectedSize;
    private final Iterable<Matcher<Map.Entry<K, V>>> matchers;

    public MapDeepIsEqualMatcher(Map<K, V> expected, MatcherFactory baseMatcherFactory, DisjointSet<Object> equiv)
    {
        this.expectedSize = expected.size();
        Set<Map.Entry<K, V>> entrySet = expected.entrySet();
        this.matchers = createEntryMatchers(entrySet, cache(toMatchers(baseMatcherFactory, equiv)));
    }

    private Iterable<Matcher<Map.Entry<K, V>>> createEntryMatchers(Set<Map.Entry<K, V>> entries, Function<Object,Matcher<?>> cache) {
        Set<Matcher<Map.Entry<K, V>>> entryMatchers = Sets.newHashSet();
        for (Map.Entry<K, V> entry : entries)
        {
            Matcher<K> keyMatcher = (Matcher<K>) cache.apply(entry.getKey());
            Matcher<V> valueMatcher = (Matcher<V>) cache.apply(entry.getValue());
            entryMatchers.add(new EntryDiagnosingMatcher<K, V>(keyMatcher, valueMatcher));
        }
        return entryMatchers;
    }

    private Function<Object, Matcher<?>> toMatchers(final MatcherFactory matcherFactory, final DisjointSet<Object> equiv)
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


        if (! (actual instanceof Map<?, ?>))
        {
            mismatchDescription.appendText("not a Map, but a ")
                    .appendText(actual.getClass().getName());
            return false;
        }

        @SuppressWarnings("unchecked") //just checked this above
                Map<K, V> actualAsMap = (Map<K, V>) actual;
        if (expectedSize != actualAsMap.size())
        {
            mismatchDescription.appendText("size should be ")
                    .appendText(String.valueOf(expectedSize))
                    .appendText(", but is ")
                    .appendValue(actualAsMap.size());
            return false;
        }

        Set<Matcher<Map.Entry<K, V>>> unsatisfiedMatchers = Sets.newHashSet();
        Set<Map.Entry<K, V>> unmatchingElements = Sets.newHashSet(actualAsMap.entrySet());

        lookForMatches(actualAsMap, unsatisfiedMatchers, unmatchingElements);

        if (!unsatisfiedMatchers.isEmpty())
        {
            describeUnsatisfiedMatchers(mismatchDescription, unsatisfiedMatchers);
            if (!unmatchingElements.isEmpty())
            {
                mismatchDescription.appendText(", and it ");
            }

        }
        if (!unmatchingElements.isEmpty())
        {
            describeUnmatchedElements(mismatchDescription, unmatchingElements);
        }
        return unsatisfiedMatchers.isEmpty() && unmatchingElements.isEmpty();
    }

    private void lookForMatches(Map<K, V> actualMap, Set<Matcher<Map.Entry<K,V>>> unsatisfiedMatchers, Set<Map.Entry<K,V>> unmatchingElements) {
        for (Matcher<Map.Entry<K, V>> matcher : matchers)
        {
            boolean mismatchFound = true;
            for (Map.Entry<?, ?> element : actualMap.entrySet())
            {
                if (matcher.matches(element))
                {
                    mismatchFound = false;
                    unmatchingElements.remove(element);
                }
            }
            if (mismatchFound)
            {
                unsatisfiedMatchers.add(matcher);
            }
        }
    }

    private void describeUnmatchedElements(Description mismatchDescription, Set<Map.Entry<K,V>> unmatchingElements) {
        mismatchDescription.appendText("contains these unmatched elements: ");
        boolean first = true;
        for (Map.Entry<K,V> element : unmatchingElements)
        {
            if (first)
                mismatchDescription.appendText("[");
            else
            {
                mismatchDescription.appendText(", ");
            }
            first = false;
            mismatchDescription.appendText(element.toString());
        }
        mismatchDescription.appendText("]");
    }

    private void describeUnsatisfiedMatchers(Description mismatchDescription, Set<Matcher<Map.Entry<K,V>>> unsatisfiedMatchers) {
        mismatchDescription.appendText("does not match these: ");

        boolean first = true;
        for (Matcher<?> matcher : unsatisfiedMatchers)
        {
            if (first)
                mismatchDescription.appendText("[");
            else
            {
                mismatchDescription.appendText(", ");
            }
            first = false;
            mismatchDescription.appendText(matcher.toString());
        }
        mismatchDescription.appendText("]");
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
            desc.appendDescriptionOf(matcher);
            index++;
        }
        desc.appendText("]");
    }

    private static class EntryDiagnosingMatcher<K, V> extends TypeSafeDiagnosingMatcher<Map.Entry<K, V>> {
        private Matcher<K> keyMatcher;
        private Matcher<V> valueMatcher;

        public EntryDiagnosingMatcher(Matcher<K> keyMatcher, Matcher<V> valueMatcher) {

            this.keyMatcher = keyMatcher;
            this.valueMatcher = valueMatcher;
        }

        @Override
        protected boolean matchesSafely(Map.Entry<K, V> entry, Description description) {
            boolean matches = true;
            if (!keyMatcher.matches(entry.getKey()))
            {
                matches = false;
                description.appendText("key mismatch:");
                keyMatcher.describeMismatch(entry.getKey(), description);
            }
            if (!valueMatcher.matches(entry.getValue()))
            {
                matches = false;
                description.appendText("value mismatch:");
                valueMatcher.describeMismatch(entry.getValue(), description);
            }
            return matches;
        }

        public void describeTo(Description description) {
            description.appendText("key: ");
            keyMatcher.describeTo(description);
            description.appendText("; ");
            description.appendText("value: ");
            valueMatcher.describeTo(description);
        }


    }
}