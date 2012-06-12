package com.atlassian.hamcrest;

import static com.atlassian.hamcrest.DeepIsEqual.deeplyEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import org.hamcrest.Description;
import org.hamcrest.StringDescription;
import org.junit.Test;

import com.atlassian.hamcrest.DeepIsEqualPrimitiveFieldsTest.AllPrimitives;

public class DeepIsEqualTest
{
    private static final Simple ONE = new Simple(1, "One");
    private static final Simple COPY_OF_ONE = new Simple(1, "One");
    private static final Simple TWO = new Simple(2, "Two");
    private static final Simple COPY_OF_TWO = new Simple(2, "Two");
    
    private static final StringsHolder HELLO_WORLD = new StringsHolder("Hello", "World");
    private static final StringsHolder COPY_OF_HELLO_WORLD = new StringsHolder("Hello", "World");
    private static final StringsHolder GOODBYE_WORLD = new StringsHolder("Goodbye", "World");

    @Test
    public void assertThatNullIsEqualToNull()
    {
        assertThat(null, is(deeplyEqualTo(null)));
    }
    
    @Test
    public void assertThatNullIsNotEqualToNonNull()
    {
        assertThat(null, is(not(deeplyEqualTo(ONE))));
    }
    
    @Test
    public void assertThatSubTypeIsNotEqualToSuperType()
    {
        assertThat(ONE, is(not(deeplyEqualTo((Simple) new SimpleSub(1, "One", "One more thing")))));
    }
    
    @Test
    public void assertThatSubtypesWithDifferentValuesForParentFieldsAreNotEqual()
    {
        assertThat(new SimpleSub(1, "One", "One more thing"), is(not(deeplyEqualTo(new SimpleSub(2, "One", "One more thing")))));
    }
    
    @Test
    public void assertThatDeepIsEqualMatchesSimpleObjects()
    {
        assertThat(ONE, is(deeplyEqualTo(COPY_OF_ONE)));
    }
    
    @Test
    public void assertThatDeepIsEqualDoesNotMatchDifferentSimpleObjects()
    {
        assertThat(ONE, is(not(deeplyEqualTo(TWO))));
    }

    @Test
    public void assertThatDeepIsEqualDescribesExpectedSimpleObject()
    {
        Description description = new StringDescription().appendDescriptionOf(deeplyEqualTo(TWO));
        assertThat(description.toString(), is(equalTo("{number is <2>, name is \"Two\"}")));
    }
    
    @Test
    public void assertThatDeepIsEqualDescribesMismatchOfSimpleObjects()
    {
        Description description = new StringDescription();
        is(deeplyEqualTo(TWO)).describeMismatch(ONE, description);
        assertThat(description.toString(), is(equalTo("{number was <1>, name was \"One\"}")));
    }

    @Test
    public void assertThatDeepIsEqualMatchesAllPrimitiveTypes()
    {
        AllPrimitives p1 = new AllPrimitives();
        AllPrimitives p2 = new AllPrimitives();
        assertThat(p1, is(deeplyEqualTo(p2)));
    }
    
    @Test
    public void assertThatDeepIsEqualMatchesArrayOfPrimitivesField()
    {
        assertThat(HELLO_WORLD, is(deeplyEqualTo(COPY_OF_HELLO_WORLD)));
    }
    
    @Test
    public void assertThatDeepIsEqualDoesNotMatchArrayOfPrimitivesFieldWithDifferentValues()
    {
        assertThat(HELLO_WORLD, is(not(deeplyEqualTo(GOODBYE_WORLD))));
    }
    
    @Test
    public void assertThatDeepIsEqualMatchesArrayOfCompositeObjectsField()
    {
        SimpletonsHolder s1 = new SimpletonsHolder(ONE, TWO);
        SimpletonsHolder s2 = new SimpletonsHolder(COPY_OF_ONE, COPY_OF_TWO);
        assertThat(s1, is(deeplyEqualTo(s2)));
    }
    
    @Test
    public void assertThatDeepIsEqualDoesNotMatchArrayOfCompositeObjectsFieldWithDifferentValues()
    {
        SimpletonsHolder s1 = new SimpletonsHolder(ONE, TWO);
        SimpletonsHolder s2 = new SimpletonsHolder(TWO);
        assertThat(s1, is(not(deeplyEqualTo(s2))));
    }

    @Test
    public void assertThatDeepIsEqualMatchesCompositeObjects()
    {
        Composite c1 = new Composite(ONE, HELLO_WORLD);
        Composite c2 = new Composite(COPY_OF_ONE, COPY_OF_HELLO_WORLD);
        assertThat(c1, is(deeplyEqualTo(c2)));
    }
    
    @Test
    public void assertThatDeepIsEqualDoesNotMatchCompositeObjectsWithDifferentValues()
    {
        Composite c1 = new Composite(ONE, HELLO_WORLD);
        Composite c2 = new Composite(TWO, COPY_OF_HELLO_WORLD);
        assertThat(c1, is(not(deeplyEqualTo(c2))));
    }

    @Test
    public void assertThatDeepIsEqualMatchesObjectsWithArraysOfArrays()
    {
        Matrix identity = new Matrix(new int[][] { { 1, 0 }, { 0, 1 } });
        Matrix copyOfIdentity = new Matrix(new int[][] { { 1, 0 }, { 0, 1 } });
        assertThat(identity, is(deeplyEqualTo(copyOfIdentity)));
    }

    @Test
    public void assertThatDeepIsEqualMatchesCyclicObjects()
    {
        Cyclic cyclic1 = new Cyclic(3);
        cyclic1.cycle = cyclic1;
        Cyclic cyclic2 = new Cyclic(3);
        cyclic2.cycle = cyclic2;
        assertThat(cyclic2, is(deeplyEqualTo(cyclic1)));
    }

    @Test
    public void assertThatDeepIsEqualDoesNotMatchDistinctCyclicObjects()
    {
        Cyclic cyclic1 = new Cyclic(3);
        cyclic1.cycle = cyclic1;
        Cyclic cyclic2 = new Cyclic(4);
        cyclic2.cycle = cyclic2;
        assertThat(cyclic2, is(not(deeplyEqualTo(cyclic1))));
    }

    static class Cyclic
    {
       Cyclic cycle;
       final int value;

        Cyclic(int value)
        {
            this.value = value;
        }
    }

    static class Simple
    {
        final int number;
        final String name;
        
        Simple(int number, String name)
        {
            this.number = number;
            this.name = name;
        }
    }
    
    static class SimpleSub extends Simple
    {
        final String desc;

        SimpleSub(int number, String name, String desc)
        {
            super(number, name);
            this.desc = desc;
        }
    }
    
    static class StringsHolder
    {
        final String[] strings;

        public StringsHolder(String... strings)
        {
            this.strings = strings;
        }
    }
    
    static class Matrix
    {
        final int[][] values;
        
        public Matrix(int[][] values)
        {
            this.values = values;
        }
    }
    
    static class SimpletonsHolder
    {
        final Simple[] simpletons;
        
        public SimpletonsHolder(Simple... simpletons)
        {
            this.simpletons = simpletons;
        }
    }
    
    static class Composite
    {
        final Simple simple;
        final StringsHolder message;
        
        public Composite(Simple simple, StringsHolder message)
        {
            this.simple = simple;
            this.message = message;
        }
    }
}
