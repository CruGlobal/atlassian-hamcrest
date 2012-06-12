package com.atlassian.hamcrest;

import com.google.common.base.Function;

/**
 * Interface for persistent arrays.
 * @param <E> the type of elements
 */
public interface PersistentArray<E> extends Iterable<E>
{
    public E get(int index);
    public PersistentArray<E> set(int index, E value);
    public int size();
    public PersistentArray<E> resize(int newSize);
    public PersistentArray<E> resize(int newSize, Function<Integer, E> initFun);
}
