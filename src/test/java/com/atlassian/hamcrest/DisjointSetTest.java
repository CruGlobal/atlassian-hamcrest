package com.atlassian.hamcrest;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class DisjointSetTest
{
    @Test
    public void assertThatBasicUnionOperationsLeadToEquivalentValues()
    {
        DisjointSet<String> set = new DisjointSet<String>();
        set.union("foo","bar");
        assert(set.equivalent("foo","bar"));
    }

    @Test
    public void assertThatSingleUndoOperationLeadsToNonEquivalentValues()
    {
        DisjointSet<String> set = new DisjointSet<String>();
        set.union("foo","bar");
        set.deunion();
        assert(!set.equivalent("foo", "bar"));
    }

    @Test
    public void assertThatEquivalencesAreTransitive()
    {
        DisjointSet<String> set = new DisjointSet<String>();
        set.union("foo", "bar");
        set.union("bar", "baz");
        assert(set.equivalent("foo", "baz"));
    }

    @Test
    public void assertThatResizingTheSetPreservesEquivalentValues()
    {
        DisjointSet<String> set = new DisjointSet<String>(1);
        set.union("foo", "bar");
        set.union("bar", "baz");
        assert(set.equivalent("foo", "baz"));        
    }

    @Test
    public void assertThatUndoAfterResizingLeadsToNonEquivalentValues()
    {
        DisjointSet<String> set = new DisjointSet<String>(1);
        set.union("foo", "bar");
        set.union("bar", "baz");
        set.deunion();
        set.deunion();
        assert(!set.equivalent("foo", "baz"));
    }


    @Test
    public void assertThatToStringWithNoElementsLooksLikeAnEmptySet()
    {
        DisjointSet<String> set = new DisjointSet<String>();
        assertThat(set.toString(), is(equalTo("[]")));
    }

    @Test
    public void assertThatToStringWithTwoEquivalentElementsLooksLikeASetOfOneSet()
    {
        DisjointSet<String> set = new DisjointSet<String>();
        set.union("foo", "bar");
        assertThat(set.toString(), anyOf(
            is(equalTo("[[foo, bar]]")),
            is(equalTo("[[bar, foo]]"))
            ));
    }

    @Test
    public void assertThatToStringWithTwoEquivalentElementsAndOneNonEquivalentElementLooksLikeASetOfTwoSets()
    {
        DisjointSet<String> set = new DisjointSet<String>();
        set.union("foo", "bar");
        set.equivalent("foo", "baz");
        assertThat(set.toString(), anyOf(
            is(equalTo("[[foo, bar], [baz]]")),
            is(equalTo("[[bar, foo], [baz]]")),
            is(equalTo("[[baz], [foo, bar]]")),
            is(equalTo("[[baz], [bar, foo]]"))
        ));
    }

}
