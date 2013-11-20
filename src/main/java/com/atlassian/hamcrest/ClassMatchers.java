package com.atlassian.hamcrest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Matchers useful when dealing with {@link Class}es.
 */
public final class ClassMatchers
{
    /**
     * Creates and returns a {@code Matcher} which checks if the type being passed in is an array.
     * 
     * @return {@code Matcher} which checks if the type being passed in is an array
     */
    public static Matcher<Class<?>> isArray()
    {
        return IS_ARRAY;
    }

    private static final Matcher<Class<?>> IS_ARRAY = new IsArray();
    
    private static final class IsArray extends TypeSafeMatcher<Class<?>>
    {
        @Override
        protected boolean matchesSafely(Class<?> c)
        {
            return c.isArray();
        }

        public void describeTo(Description description)
        {
            description.appendText("is an array");
        }
    }

}
