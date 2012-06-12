package com.atlassian.hamcrest;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.google.common.base.Predicate;

/**
 * Utility {@link Predicate}s.
 */
final class Predicates
{
    /**
     * A {@code Predicate} which evaluates to {@code true} iff the {@code Field} it is being applied to has the 
     * {@code transient} modifier.
     */
    static Predicate<Field> isTransient()
    {
        return IsTransient.INSTANCE;
    }

    private enum IsTransient implements Predicate<Field>
    {
        INSTANCE;
        
        public boolean apply(Field f)
        {
            return Modifier.isTransient(f.getModifiers());
        }
        
        @Override
        public String toString()
        {
            return "isTransient";
        }
    };
    
    /**
     * A {@code Predicate} which evaluates to {@code true} iff the {@code Field} it is being applied to has the 
     * {@code static} modifier.
     */
    static Predicate<Field> isStatic()
    {
        return IsStatic.INSTANCE;
    }
    
    private enum IsStatic implements Predicate<Field>
    {
        INSTANCE;
        
        public boolean apply(Field f)
        {
            return Modifier.isStatic(f.getModifiers());
        }
        
        @Override
        public String toString()
        {
            return "isStatic";
        }
    }
}
