package com.atlassian.hamcrest;

import java.util.AbstractList;
import java.util.List;

/**
 * Utility methods for handling arrays.
 */
final class Arrays
{
    private Arrays() {}
    
    static <T> List<T> asList(T... elements)
    {
        return java.util.Arrays.asList(elements);
    }
    
    /**
     * Returns the array as a {@code List}
     *  
     * @param array array to wrap as a list
     * @return array as a {@code List}
     */
    static List<Boolean> asList(boolean[] array)
    {
        return new BooleanList(array);
    }
    
    private static final class BooleanList extends AbstractList<Boolean>
    {
        private final boolean[] array;

        public BooleanList(boolean[] array)
        {
            this.array = array;
        }

        @Override
        public Boolean get(int index)
        {
            return array[index];
        }

        @Override
        public int size()
        {
            return array.length;
        }
    }

    /**
     * Returns the array as a {@code List}
     *  
     * @param array array to wrap as a list
     * @return array as a {@code List}
     */
    static List<Byte> asList(byte[] array)
    {
        return new ByteList(array);
    }
    
    private static final class ByteList extends AbstractList<Byte>
    {
        private final byte[] array;

        public ByteList(byte[] array)
        {
            this.array = array;
        }

        @Override
        public Byte get(int index)
        {
            return array[index];
        }

        @Override
        public int size()
        {
            return array.length;
        }
    }

    /**
     * Returns the array as a {@code List}
     *  
     * @param array array to wrap as a list
     * @return array as a {@code List}
     */
    static List<Short> asList(short[] array)
    {
        return new ShortList(array);
    }
    
    private static final class ShortList extends AbstractList<Short>
    {
        private final short[] array;

        public ShortList(short[] array)
        {
            this.array = array;
        }

        @Override
        public Short get(int index)
        {
            return array[index];
        }

        @Override
        public int size()
        {
            return array.length;
        }
    }

    /**
     * Returns the array as a {@code List}
     *  
     * @param array array to wrap as a list
     * @return array as a {@code List}
     */
    static List<Integer> asList(int[] array)
    {
        return new IntegerList(array);
    }
    
    private static final class IntegerList extends AbstractList<Integer>
    {
        private final int[] array;

        public IntegerList(int[] array)
        {
            this.array = array;
        }

        @Override
        public Integer get(int index)
        {
            return array[index];
        }

        @Override
        public int size()
        {
            return array.length;
        }
    }
    
    /**
     * Returns the array as a {@code List}
     *  
     * @param array array to wrap as a list
     * @return array as a {@code List}
     */
    static List<Long> asList(long[] array)
    {
        return new LongList(array);
    }
    
    private static final class LongList extends AbstractList<Long>
    {
        private final long[] array;

        public LongList(long[] array)
        {
            this.array = array;
        }

        @Override
        public Long get(int index)
        {
            return array[index];
        }

        @Override
        public int size()
        {
            return array.length;
        }
    }
    
    /**
     * Returns the array as a {@code List}
     *  
     * @param array array to wrap as a list
     * @return array as a {@code List}
     */
    static List<Float> asList(float[] array)
    {
        return new FloatList(array);
    }
    
    private static final class FloatList extends AbstractList<Float>
    {
        private final float[] array;

        public FloatList(float[] array)
        {
            this.array = array;
        }

        @Override
        public Float get(int index)
        {
            return array[index];
        }

        @Override
        public int size()
        {
            return array.length;
        }
    }
    
    /**
     * Returns the array as a {@code List}
     *  
     * @param array array to wrap as a list
     * @return array as a {@code List}
     */
    static List<Double> asList(double[] array)
    {
        return new DoubleList(array);
    }
    
    private static final class DoubleList extends AbstractList<Double>
    {
        private final double[] array;

        public DoubleList(double[] array)
        {
            this.array = array;
        }

        @Override
        public Double get(int index)
        {
            return array[index];
        }

        @Override
        public int size()
        {
            return array.length;
        }
    }

    /**
     * Returns the array as a {@code List}
     *  
     * @param array array to wrap as a list
     * @return array as a {@code List}
     */
    static List<Character> asList(char[] array)
    {
        return new CharacterList(array);
    }
    
    private static final class CharacterList extends AbstractList<Character>
    {
        private final char[] array;

        public CharacterList(char[] array)
        {
            this.array = array;
        }

        @Override
        public Character get(int index)
        {
            return array[index];
        }

        @Override
        public int size()
        {
            return array.length;
        }
    }
}
