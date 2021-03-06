package com.atlassian.hamcrest;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

import java.util.Iterator;

/**
 * A persistent array implementation based on diffs and re-rooting.
 */
public final class DiffPersistentArray<E> implements PersistentArray<E> {

    private static abstract class BaseArray<E>
    {
        abstract E get(int index);
        abstract int size();
    }

    private static class DirectArray<E> extends BaseArray<E>
    {
        private Object[] elementData;

        private DirectArray(int size, Function<Integer, E> initFun)
        {
            this(size);
            for (int i = 0; i < size; i++)
            {
                this.elementData[i] = initFun.apply(i);
            }
        }


        private DirectArray(int size)
        {
            assert size > 0;
            this.elementData = new Object[size];
        }

        int size()
        {
            return elementData.length;
        }

        @SuppressWarnings("unchecked")
        E get(int index)
        {
            assert index >= 0;
            return (E)elementData[index];
        }

        void set(int index, E value)
        {
            elementData[index] = value;
        }

        void resize(int newSize)
        {
            assert (newSize > elementData.length);
            Object[] newElementData = new Object[newSize];
            System.arraycopy(elementData, 0, newElementData, 0, elementData.length);
            elementData = newElementData;
        }

        void resize(int newSize, Function<Integer, E> initFun)
        {
            int oldSize = elementData.length;
            resize(newSize);
            for (int i = oldSize; i < newSize; i++)
            {
                elementData[i] = initFun.apply(i);
            }
        }
    }

    private static class DiffArray<E> extends BaseArray<E>
    {
        private final E value;
        private final int index;
        private final DiffPersistentArray<E> base;

        private DiffArray(DiffPersistentArray<E> base, int index, E value)
        {
            assert index >= 0;
            this.base = base;
            this.index = index;
            this.value = value;
        }

        int size()
        {
            return base.size();
        }

        E get(int index)
        {
            assert index >= 0;
            if (this.index == index)
            {
                return value;
            }
            else
            {
                return base.get(index);
            }
        }
    }

    private BaseArray<E> array;
    private int size;

    public DiffPersistentArray(int size)
    {
        sizeCheck(size);
        array = new DirectArray<E>(size);
        this.size = size;
    }

    public DiffPersistentArray(int size, Function<Integer, E> initFun)
    {
        sizeCheck(size);
        array = new DirectArray<E>(size, initFun);
        this.size = size;
    }

    private void sizeCheck(int size) {
        Preconditions.checkArgument(size > 0, "Size must be > 0");
    }

    private DiffPersistentArray(BaseArray<E> array, int size)
    {
        this.array = array;
        this.size = size;
    }

    public E get(int index)
    {
        Preconditions.checkElementIndex(index, size);
        reroot();
        return array.get(index);
    }

    public int size()
    {
        return size;
    }

    @SuppressWarnings("unchecked")
    public Iterator<E> iterator()
    {
        reroot();
        assert(array instanceof DirectArray);
        return Arrays.<E>asList((E[]) ((DirectArray)array).elementData).iterator();
    }

    public PersistentArray<E> set(int index, E value)
    {
        Preconditions.checkElementIndex(index, size);
        reroot();
        assert(array instanceof DirectArray);
        DirectArray<E> original = (DirectArray<E>)array;
        E originalE = original.get(index);
        original.set(index, value);
        DiffPersistentArray<E> result = new DiffPersistentArray<E>(original, size);
        array = new DiffArray<E>(result, index, originalE);
        return result;
    }

    public PersistentArray<E> resize(int newSize)
    {
        reroot();
        resizeDirectArrayIfNecessary(newSize, null);
        return new DiffPersistentArray<E>(array, newSize);
    }

    public PersistentArray<E> resize(int newSize, Function<Integer, E> initFun)
    {
        reroot();
        resizeDirectArrayIfNecessary(newSize, initFun);
        return new DiffPersistentArray<E>(array, newSize);
    }

    private void resizeDirectArrayIfNecessary(int newSize, Function<Integer, E> initFun) {
        if (array.size() < newSize)
        {
            assert (array instanceof DirectArray);
            DirectArray<E> directArray = (DirectArray<E>) array;
            if (initFun == null)
                directArray.resize(newSize);
            else
                directArray.resize(newSize, initFun);
        }
    }

    private void reroot()
    {
        if (array instanceof DiffArray)
        {
            DiffArray<E> diff = (DiffArray<E>)array;
            DiffPersistentArray<E> newIndirect = diff.base;
            newIndirect.reroot();

            assert(newIndirect.array instanceof DirectArray);

            DirectArray<E> newDirectArray = (DirectArray<E>) newIndirect.array;

            E originalE = newDirectArray.get(diff.index);
            newDirectArray.set(diff.index, diff.value);
            array = newDirectArray;
            newIndirect.array = new DiffArray<E>(this, diff.index, originalE);
        }
    }

    //copy/pasted from java.util.AbstractCollection
    /**
     * Returns a string representation of this array.  The string
     * representation consists of a list of the array's elements in the
     * order they are returned by its iterator, enclosed in square brackets
     * (<tt>"[]"</tt>).  Adjacent elements are separated by the characters
     * <tt>", "</tt> (comma and space).  Elements are converted to strings as
     * by {@link String#valueOf(Object)}.
     *
     * @return a string representation of this collection
     */
    @Override
    public String toString() {
        Iterator<E> i = iterator();
        if (! i.hasNext())
            return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (;;) {
            E e = i.next();
            sb.append(e == this ? "(this Collection)" : e);
            if (! i.hasNext())
                return sb.append(']').toString();
            sb.append(", ");
        }
    }
}
