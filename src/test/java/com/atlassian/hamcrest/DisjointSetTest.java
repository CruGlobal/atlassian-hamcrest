package com.atlassian.hamcrest;

import org.junit.Test;

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
}
