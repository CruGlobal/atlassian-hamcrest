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

    /**
     * Returns a {@code Matcher} which will match any class that is assignable to the given type.
     * 
     * @param to type that must be assignable to
     * @return {@code Matcher} which will match any class that is assignable to the given type
     */
    public static final Matcher<Class<?>> isAssignableTo(final Class<?> to)
    {
        return new IsAssignableTo(to);
    };
    
    private static final class IsAssignableTo extends TypeSafeMatcher<Class<?>>
    {
        private final Class<?> to;

        public IsAssignableTo(Class<?> to)
        {
            this.to = to;
        }

        @Override
        protected boolean matchesSafely(Class<?> c)
        {
            return to.isAssignableFrom(c);
        }

        public void describeTo(Description description)
        {
            description.appendText("is assignable to ").appendValue(to);
        }
    }
}
