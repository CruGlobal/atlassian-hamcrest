package com.atlassian.hamcrest;

import com.google.common.base.Function;

import java.util.Map;
import java.util.IdentityHashMap;
import java.util.Stack;

/**
 * A disjoint-set data structure that permits stack-like deunion operations. Initially, it is assumed that all elements
 * are singletons. Elements are combined into a single set by the {@link #union} operation. Elements can
 * be queried to determine if they are in the same set by the {@link #equivalent} operation. {@link DisjointSet}s
 * also support stack-like {@link #deunion} operations. This allows the data structure to be efficiently "rolled back" to any previous
 * state in time, which can be useful in backtracking algorithms.
 *
 * This implementation is backed by a {@link PersistentArray}. It includes a path compression optimization, but currently does not
 * do union-by-rank.
 *
 * @param <E>
 */
public class DisjointSet<E>
{
    private final Map<E, Integer> elementMap = new IdentityHashMap<E, Integer>();
    private PersistentArray<Integer> backingArray;
    private final Stack<PersistentArray<Integer>> versions;
    private static final int DEFAULT_SIZE = 32;

    private enum IdentityInitializerFunction implements Function<Integer, Integer>
    {
        INSTANCE;

        public Integer apply(Integer i)
        {
            return i;
        }
    }

    /**
     * Creates a new {@code DisjointSet} with the default number of elements
     */
    public DisjointSet()
    {
        this(DEFAULT_SIZE);
    }

    /**
     * Creates a new {@code DisjointSet} with the given expected size. This parameter will be used to construct the initial
     * backing array, so it be chosen appropriately. 
     * @param expectedSize
     */
    public DisjointSet(int expectedSize)
    {
        if (expectedSize <= 0)
        {
            throw new IllegalArgumentException("Expected size must be > 0");
        }
        backingArray = new DiffPersistentArray<Integer>(expectedSize, IdentityInitializerFunction.INSTANCE);
        versions = new Stack<PersistentArray<Integer>>();
    }

    /**
     * Place two elements in the same set
     * @param e1 The first element to be unioned
     * @param e2 The second element to be unioned
     */
    public void union(E e1, E e2)
    {
        Integer i1 = find(e1);
        Integer i2 = find(e2);

        if (!i1.equals(i2))
        {
            versions.push(backingArray);
            backingArray = backingArray.set(i1, i2);
        }
    }

    /**
     * Undoes the last {@code i} {@link #union} operations
     * @param toUndo the number of union operations to undo
     */
    public void deunion(int toUndo)
    {
        if (toUndo < 0)
        {
            throw new IllegalArgumentException("Number of deunion operations must be >= 0");
        }
        else if (toUndo > versions.size())
        {
            throw new IllegalArgumentException("Cannot undo more than " + versions.size() + " union operations");
        }
        for (int i = 0; i < toUndo; i++)
        {
            backingArray = versions.pop();
        }
    }

    /**
     * Undoes the last {@link #union} operation
     */
    public void deunion()
    {
        deunion(1);
    }

    /**
     * Determine whether two elements are in the same set
     * @param e1 The first element
     * @param e2 The second element
     * @return true if the two elements are in the same set, false otherwise
     */
    public boolean equivalent(E e1, E e2)
    {
        Integer i1 = find(e1);
        Integer i2 = find(e2);

        return i1.equals(i2);
    }

    private Integer find(E elt)
    {
        Integer result = elementMap.get(elt);
        if (result == null)
        {
            result = elementMap.size();
            elementMap.put(elt, result);
            if (backingArray.size() <= result)
            {
                backingArray = backingArray.resize(backingArray.size() * 2, IdentityInitializerFunction.INSTANCE);
            }
        }


        Integer originalResult = result;
        while (!result.equals(backingArray.get(result)))
        {
            result = backingArray.get(result);
        }

        // Path compression
        if (! originalResult.equals(result))
        {
            backingArray = backingArray.set(originalResult, result);
        }

        return result;
    }
}
