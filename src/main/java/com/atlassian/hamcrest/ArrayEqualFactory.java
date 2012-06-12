package com.atlassian.hamcrest;

import static com.atlassian.hamcrest.Arrays.asList;

import java.util.Arrays;

import org.hamcrest.Matcher;

/**
 * A {@code MatcherFactory} which creates a {@link Matcher} for array objects.  The returned {@code Matcher} is
 * an instance of {@link ArrayDeepIsEqualMatcher}.
 * 
 * <p>Note: This factory is a bit painful to look at because of the crappy way generics, arrays, and primitives
 * all interact in Java.  If the array is an array of primitive values, we need a big gnarly 
 * if/else if/ad nauseum block to handle each primitive array type separately.
 */
final class ArrayEqualFactory implements MatcherFactory
{
    public <T> Matcher<? super T> newEqualMatcher(T expected, MatcherFactory baseMatcherFactory, DisjointSet<Object> equiv)
    {
        Iterable<?> elements;
        
        // cast to appropriate array type
        Class<?> elementType = expected.getClass().getComponentType();
        if (!elementType.isPrimitive())
        {
            elements = Arrays.asList((Object[]) expected);
        }
        else
        {
            if (boolean.class.equals(elementType))
            {
                elements = asList((boolean[]) expected);
            }
            else if (byte.class.equals(elementType))
            {
                elements = asList((byte[]) expected);
            }
            else if (short.class.equals(elementType))
            {
                elements = asList((short[]) expected);
            }
            else if (int.class.equals(elementType))
            {
                elements = asList((int[]) expected);
            }
            else if (long.class.equals(elementType))
            {
                elements = asList((long[]) expected);
            }
            else if (float.class.equals(elementType))
            {
                elements = asList((float[]) expected);
            }
            else if (double.class.equals(elementType))
            {
                elements = asList((double[]) expected);
            }
            else if (char.class.equals(elementType))
            {
                elements = asList((char[]) expected);
            }
            else
            {
                throw new InternalError("Umm... did you add a new primitive type to Java or something?");
            }
        }
        return new ArrayDeepIsEqualMatcher<T>(elements, baseMatcherFactory, equiv);
    }
}