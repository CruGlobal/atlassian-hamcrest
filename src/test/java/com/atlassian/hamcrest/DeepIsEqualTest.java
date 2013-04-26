package com.atlassian.hamcrest;

import static com.atlassian.hamcrest.DeepIsEqual.deeplyEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;

import com.atlassian.hamcrest.DeepIsEqualPrimitiveFieldsTest.AllPrimitives;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DeepIsEqualTest
{
    private static final Simple ONE = new Simple(1, "One");
    private static final Simple COPY_OF_ONE = new Simple(1, "One");
    private static final Simple TWO = new Simple(2, "Two");
    private static final Simple THREE = new Simple(3, "Three");
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
    public void assertThatDeepIsEqualWithSetMatcherDoesMatchesSetOfCompositeObjects()
    {
        SimpletonSetHolder s1 = new SimpletonSetHolder(ONE, TWO);
        SimpletonSetHolder s2 = new SimpletonSetHolder(TWO, ONE);
        assertThat(s1, isDeeplyEqualToHandlingSets(s2));
    }

    @Test
    public void assertThatDeepIsEqualWithSetMatcherDoesNotMatchSetOfCompositeObjectsWhenSizeDoesNotMatch()
    {
        SimpletonSetHolder s1 = new SimpletonSetHolder(ONE, TWO);
        SimpletonSetHolder s2 = new SimpletonSetHolder(TWO);
        assertThat(s1, is(not(isDeeplyEqualToHandlingSets(s2))));
    }

    @Test
    public void assertThatDeepIsEqualWithSetMatcherDoesNotMatchSetOfCompositeObjectsWhenSizeMatchesButAMatcherDoesNotMatchAndAnElementIsUnmatched()
    {
        SimpletonSetHolder s1 = new SimpletonSetHolder(ONE, TWO);
        SimpletonSetHolder s2 = new SimpletonSetHolder(TWO, THREE);
        assertThat(s1, is(not(isDeeplyEqualToHandlingSets(s2))));
    }

    @Test
    public void assertThatDeepIsEqualWithSetMatcherDoesNotMatchSetOfCompositeObjectsWhenSizeMatchesAndAllElementsAreMatchedButAMatcherDoesNotMatch()
    {
        SimpletonSetHolder s1 = new SimpletonSetHolder(ONE, COPY_OF_ONE);
        SimpletonSetHolder s2 = new SimpletonSetHolder(ONE, TWO);

        assertThat(s1, is(not(isDeeplyEqualToHandlingSets(s2))));
    }

    @Test
    public void assertThatDeepIsEqualWithSetMatcherDoesNotMatchSetOfCompositeObjectsWhenSizeMatchesAndAMatcherDoesNotMatchButAllElementsAreMatched()
    {
        SimpletonSetHolder s1 = new SimpletonSetHolder(ONE, TWO);
        SimpletonSetHolder s2 = new SimpletonSetHolder(ONE, COPY_OF_ONE);

        assertThat(s1, is(not(isDeeplyEqualToHandlingSets(s2))));
    }

    @Test
    public void assertThatDeepIsEqualWithSetMatcherDescribesMimsatchOfSetOfCompositeObjectsWhenSizeDoesNotMatch()
    {
        SimpletonSetHolder s1 = new SimpletonSetHolder(ONE, TWO);
        SimpletonSetHolder s2 = new SimpletonSetHolder(TWO);

        Description description = new StringDescription();
        is(isDeeplyEqualToHandlingSets(s2)).describeMismatch(s1, description);
        assertThat(description.toString(), is(equalTo("{simpletons size should be 1, but is <2>}")));
    }

    @Test
    public void assertThatDeepIsEqualWithSetMatcherDescribesMimsatchOfSetOfCompositeObjectsWhenSizeMatchesButAMatcherDoesNotMatchAndAnElementIsUnmatched()
    {
        SimpletonSetHolder s1 = new SimpletonSetHolder(ONE, TWO);
        SimpletonSetHolder s2 = new SimpletonSetHolder(TWO, THREE);

        Description description = new StringDescription();
        is(isDeeplyEqualToHandlingSets(s2)).describeMismatch(s1, description);
        assertThat(description.toString(), containsString("simpletons does not match these: [{number is <3>, name is \"Three\"}], and it contains these unmatched elements: "));
    }

    @Test
    public void assertThatDeepIsEqualWithSetMatcherDescribesMimsatchOfSetOfCompositeObjectsWhenSizeMatchesAndAllElementsAreMatchedButAMatcherDoesNotMatch()
    {
        SimpletonSetHolder s1 = new SimpletonSetHolder(ONE, COPY_OF_ONE);
        SimpletonSetHolder s2 = new SimpletonSetHolder(ONE, TWO);

        Description description = new StringDescription();
        is(isDeeplyEqualToHandlingSets(s2)).describeMismatch(s1, description);
        assertThat(description.toString(), containsString("simpletons does not match these: [{number is <2>, name is \"Two\"}]"));
    }

    @Test
    public void assertThatDeepIsEqualWithSetMatcherDescribesMimsatchOfSetOfCompositeObjectsWhenSizeMatchesAndAMatcherDoesNotMatchButAllElementsAreMatched()
    {
        SimpletonSetHolder s1 = new SimpletonSetHolder(ONE, TWO);
        SimpletonSetHolder s2 = new SimpletonSetHolder(ONE, COPY_OF_ONE);

        Description description = new StringDescription();
        is(isDeeplyEqualToHandlingSets(s2)).describeMismatch(s1, description);
        assertThat(description.toString(), containsString("simpletons contains these unmatched elements: "));
    }


    private <T> Matcher<? super T> isDeeplyEqualToHandlingSets(T s2) {
        return deeplyEqualTo(s2, ImmutableMap.of(
                ClassMatchers.isAssignableTo(Set.class), MatcherFactories.setIsDeeplyEqual()));
    }


    @Test
    public void assertThatDeepIsEqualWithMapMatcherDoesMatchesSetOfCompositeObjects()
    {
        SimpletonMapHolder s1 = new SimpletonMapHolder(ImmutableMap.<Object, Simple>builder()
                .put("one", ONE)
                .put("two", TWO)
        );
        SimpletonMapHolder s2 = new SimpletonMapHolder(ImmutableMap.<Object, Simple>builder()
                .put("two", TWO)
                .put("one", ONE)
        );
        assertThat(s1, isDeeplyEqualToHandlingMaps(s2));
    }

    @Test
    public void assertThatDeepIsEqualWithMapMatcherDoesNotMatchMapOfCompositeObjectsWhenSizeDoesNotMatch()
    {
        SimpletonMapHolder s1 = new SimpletonMapHolder(ImmutableMap.<Object, Simple>builder()
                .put("one", ONE)
                .put("two", TWO)
        );
        SimpletonMapHolder s2 = new SimpletonMapHolder(ImmutableMap.<Object, Simple>builder()
                .put("two", TWO)
        );
        assertThat(s1, is(not(isDeeplyEqualToHandlingMaps(s2))));
    }

    @Test
    public void assertThatDeepIsEqualWithMapMatcherDoesNotMatchMapOfCompositeObjectsWhenSizeMatchesButAMatcherDoesNotMatchAndAnElementIsUnmatched()
    {
        SimpletonMapHolder s1 = new SimpletonMapHolder(ImmutableMap.<Object, Simple>builder()
                .put("one", ONE)
                .put("two", TWO)
        );
        SimpletonMapHolder s2 = new SimpletonMapHolder(ImmutableMap.<Object, Simple>builder()
                .put("two", TWO)
                .put("three", THREE)
        );
        assertThat(s1, is(not(isDeeplyEqualToHandlingMaps(s2))));
    }


    @Test
    public void assertThatDeepIsEqualWithMapMatcherDoesNotMatchMapOfCompositeObjectsWhenSizeMatchesAndKeysMatchButAValueMatcherDoesNotMatchAndAnValueIsUnmatched()
    {
        SimpletonMapHolder s1 = new SimpletonMapHolder(ImmutableMap.<Object, Simple>builder()
                .put(ONE, ONE)
                .put(TWO, TWO)
        );
        SimpletonMapHolder s2 = new SimpletonMapHolder(ImmutableMap.<Object, Simple>builder()
                .put(ONE, ONE)
                .put(TWO, THREE)
        );
        assertThat(s1, is(not(isDeeplyEqualToHandlingMaps(s2))));
    }

    @Test
    public void assertThatDeepIsEqualWithMapMatcherDoesNotMatchMapOfCompositeObjectsWhenSizeMatchesAndAllElementsAreMatchedButAMatcherDoesNotMatch()
    {
        SimpletonMapHolder s1 = new SimpletonMapHolder(ImmutableMap.<Object, Simple>builder()
                .put(ONE, ONE)
                .put(COPY_OF_ONE, COPY_OF_ONE)
        );
        SimpletonMapHolder s2 = new SimpletonMapHolder(ImmutableMap.<Object, Simple>builder()
                .put(ONE, ONE)
                .put(TWO, TWO)
        );

        assertThat(s1, is(not(isDeeplyEqualToHandlingMaps(s2))));
    }

    @Test
    public void assertThatDeepIsEqualWithMapMatcherDoesNotMatchMapOfCompositeObjectsWhenSizeMatchesAndAMatcherDoesNotMatchButAllElementsAreMatched()
    {
        SimpletonMapHolder s1 = new SimpletonMapHolder(ImmutableMap.<Object, Simple>builder()
                .put(ONE, ONE)
                .put(TWO, TWO)
        );
        SimpletonMapHolder s2 = new SimpletonMapHolder(ImmutableMap.<Object, Simple>builder()
                .put(ONE, ONE)
                .put(COPY_OF_ONE, COPY_OF_ONE)
        );

        assertThat(s1, is(not(isDeeplyEqualToHandlingMaps(s2))));
    }


    @Test
    public void assertThatDeepIsEqualWithMapMatcherDescribesMimsatchOfMapOfCompositeObjectsWhenSizeDoesNotMatch()
    {
        SimpletonMapHolder s1 = new SimpletonMapHolder(ImmutableMap.<Object, Simple>builder()
                .put("one", ONE)
                .put("two", TWO)
        );
        SimpletonMapHolder s2 = new SimpletonMapHolder(ImmutableMap.<Object, Simple>builder()
                .put("two", TWO)
        );

        Description description = new StringDescription();
        is(isDeeplyEqualToHandlingMaps(s2)).describeMismatch(s1, description);
        assertThat(description.toString(), is(equalTo("{simpletons size should be 1, but is <2>}")));
    }

    @Test
    public void assertThatDeepIsEqualWithMapMatcherDescribesMimsatchOfMapOfCompositeObjectsWhenSizeMatchesButAMatcherDoesNotMatchAndAnElementIsUnmatched()
    {
        SimpletonMapHolder s1 = new SimpletonMapHolder(ImmutableMap.<Object, Simple>builder()
                .put("one", ONE)
                .put("two", TWO)
        );
        SimpletonMapHolder s2 = new SimpletonMapHolder(ImmutableMap.<Object, Simple>builder()
                .put("two", TWO)
                .put("three", THREE)
        );

        Description description = new StringDescription();
        is(isDeeplyEqualToHandlingMaps(s2)).describeMismatch(s1, description);
        assertThat(description.toString(), containsString("simpletons does not match these: [key: is \"three\"; value: {number is <3>, name is \"Three\"}], and it contains these unmatched elements: [one="));
    }


    @Test
    public void assertThatDeepIsEqualWithMapMatcherDescribesMismatchOfMapOfCompositeObjectsWhenSizeMatchesAndKeysMatchButAValueMatcherDoesNotMatchAndAnValueIsUnmatched()
    {
        SimpletonMapHolder s1 = new SimpletonMapHolder(ImmutableMap.<Object, Simple>builder()
                .put(ONE, ONE)
                .put(TWO, TWO)
        );
        SimpletonMapHolder s2 = new SimpletonMapHolder(ImmutableMap.<Object, Simple>builder()
                .put(ONE, ONE)
                .put(TWO, THREE)
        );

        Description description = new StringDescription();
        is(isDeeplyEqualToHandlingMaps(s2)).describeMismatch(s1, description);
        assertThat(description.toString(), containsString("simpletons does not match these: [key: {number is <2>, name is \"Two\"}; value: {number is <3>, name is \"Three\"}], and it contains these unmatched elements: ["));
    }

    @Test
    public void assertThatDeepIsEqualWithMapMatcherDescribesMimsatchOfMapOfCompositeObjectsWhenSizeMatchesAndAllElementsAreMatchedButAMatcherDoesNotMatch()
    {

        SimpletonMapHolder s1 = new SimpletonMapHolder(ImmutableMap.<Object, Simple>builder()
                .put(ONE, ONE)
                .put(COPY_OF_ONE, COPY_OF_ONE)
        );
        SimpletonMapHolder s2 = new SimpletonMapHolder(ImmutableMap.<Object, Simple>builder()
                .put(ONE, ONE)
                .put(TWO, TWO)
        );

        Description description = new StringDescription();
        is(isDeeplyEqualToHandlingMaps(s2)).describeMismatch(s1, description);
        assertThat(description.toString(), containsString("simpletons does not match these: [key: {number is <2>, name is \"Two\"}; value: {number is <2>, name is \"Two\"}]"));
    }

    @Test
    public void assertThatDeepIsEqualWithMapMatcherDescribesMimsatchOfMapOfCompositeObjectsWhenSizeMatchesAndAMatcherDoesNotMatchButAllElementsAreMatched()
    {
        SimpletonMapHolder s1 = new SimpletonMapHolder(ImmutableMap.<Object, Simple>builder()
                .put(ONE, ONE)
                .put(TWO, TWO)
        );
        SimpletonMapHolder s2 = new SimpletonMapHolder(ImmutableMap.<Object, Simple>builder()
                .put(ONE, ONE)
                .put(COPY_OF_ONE, COPY_OF_ONE)
        );

        Description description = new StringDescription();
        is(isDeeplyEqualToHandlingMaps(s2)).describeMismatch(s1, description);
        assertThat(description.toString(), containsString("simpletons contains these unmatched elements: "));
    }

    private <T> Matcher<? super T> isDeeplyEqualToHandlingMaps(T s2) {
        return deeplyEqualTo(s2, ImmutableMap.of(
                ClassMatchers.isAssignableTo(Map.class), MatcherFactories.mapIsDeeplyEqual()));
    }



    @Test
    public void assertThatDeepIsEqualWithListMatcherDoesMatchListOfCompositeObjects()
    {
        SimpletonListHolder s1 = new SimpletonListHolder(ONE, TWO);
        SimpletonListHolder s2 = new SimpletonListHolder(ONE, TWO);
        assertThat(s1, isDeeplyEqualToHandlingLists(s2));
    }

    @Test
    public void assertThatDeepIsEqualWithListMatcherDoesNotMatchListOfCompositeObjectsWhenSizeDoesNotMatch()
    {
        SimpletonListHolder s1 = new SimpletonListHolder(ONE, TWO);
        SimpletonListHolder s2 = new SimpletonListHolder(ONE);

        assertThat(s1, is(not(isDeeplyEqualToHandlingLists(s2))));
    }


    @Test
    public void assertThatDeepIsEqualWithListMatcherDoesNotMatchListOfCompositeObjectsWhenSizeMatchesButAMatcherDoesNotMatch()
    {
        SimpletonListHolder s1 = new SimpletonListHolder(ONE, TWO);
        SimpletonListHolder s2 = new SimpletonListHolder(ONE, THREE);

        assertThat(s1, is(not(isDeeplyEqualToHandlingLists(s2))));
    }

    @Test
    public void assertThatDeepIsEqualWithListMatcherDescribesMimsatchOfSetOfCompositeObjectsWhenSizeDoesNotMatch()
    {
        SimpletonListHolder s1 = new SimpletonListHolder(ONE, TWO);
        SimpletonListHolder s2 = new SimpletonListHolder(TWO);

        Description description = new StringDescription();
        is(isDeeplyEqualToHandlingLists(s2)).describeMismatch(s1, description);
        assertThat(description.toString(), is(equalTo("{simpletons size should be 1, but is <2>}")));
    }

    @Test
    public void assertThatDeepIsEqualWithListMatcherDescribesMimsatchOfListOfCompositeObjectsWhenSizeMatchesButAMatcherDoesNotMatch()
    {
        SimpletonListHolder s1 = new SimpletonListHolder(ONE, TWO);
        SimpletonListHolder s2 = new SimpletonListHolder(ONE, THREE);

        Description description = new StringDescription();
        is(isDeeplyEqualToHandlingLists(s2)).describeMismatch(s1, description);
        assertThat(description.toString(), is(equalTo("{simpletons [[<1>] => {number was <2>, name was \"Two\"}]}")));
    }


    private <T> Matcher<? super T> isDeeplyEqualToHandlingLists(T s2) {
        return deeplyEqualTo(s2, ImmutableMap.of(
                ClassMatchers.isAssignableTo(List.class), MatcherFactories.listIsDeeplyEqual()));
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

    static class SimpletonSetHolder
    {
        final Set<Simple> simpletons;

        public SimpletonSetHolder(Simple... simpletons)
        {
            this.simpletons = ImmutableSet.copyOf(Arrays.asList(simpletons));
        }
    }

    static class SimpletonMapHolder
    {
        final Map<Object, Simple> simpletons;

        public SimpletonMapHolder(ImmutableMap.Builder<Object, Simple> builder)
        {
            this.simpletons = builder.build();
        }
    }

    static class SimpletonListHolder
    {
        final List<Simple> simpletons;

        public SimpletonListHolder(Simple... simpletons)
        {
            this.simpletons = ImmutableList.copyOf(Arrays.asList(simpletons));
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
