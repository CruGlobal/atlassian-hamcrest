package com.atlassian.hamcrest;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class DiffPersistentArrayTest {

    @Test
    public void assertThatSettingAnElementUpdatesValueInNewCopy()
    {
        PersistentArray<String> array1 = new DiffPersistentArray<String>(3);
        PersistentArray<String> array2 = array1.set(0, "foo");

        assertEquals("foo", array2.get(0));
    }

    @Test
    public void assertThatSettingAnElementLeavesOriginalArrayUnchanged() {
        PersistentArray<String> array1 = new DiffPersistentArray<String>(3);
        array1.set(0, "foo");

        assertNull(array1.get(0));
    }

    @Test
    public void assertThatMultipleSetsUpdateValuesInNewCopies()
    {
        PersistentArray<String> array1 = new DiffPersistentArray<String>(3);
        PersistentArray<String> array2 = array1.set(0, "foo");
        PersistentArray<String> array3 = array1.set(0, "bar");

        assertNull(array1.get(0));
        assertEquals("foo", array2.get(0));
        assertEquals("bar", array3.get(0));
    }

    @Test
    public void assertThatGettingUnrelatedElementFromDiffArrayReturnsCorrectValue()
    {
        PersistentArray<String> array1 = new DiffPersistentArray<String>(3);
        PersistentArray<String> array2 = array1.set(0, "foo");

        assertNull(array2.get(1));
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void assertThatGettingANegativeIndexThrowsAnException()
    {
        PersistentArray<String> array1 = new DiffPersistentArray<String>(3);

        array1.get(-1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void assertThatCreatingANegativeSizeArrayThrowsAnException()
    {
        new DiffPersistentArray<String>(-2);
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void assertThatSettingANegativeIndexThrowsAnException()
    {
        PersistentArray<String> array1 = new DiffPersistentArray<String>(3);
        array1.set(-1, "baz");
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void assertThatGettingANegativeIndexOnDiffArrayThrowsAnException()
    {
        PersistentArray<String> array1 = new DiffPersistentArray<String>(3);
        PersistentArray<String> array2 = array1.set(2, "baz");
        array2.get(-1);
    }

    @Test
    public void assertThatResizePreservesValues()
    {
        PersistentArray<String> array1 = new DiffPersistentArray<String>(1);
        PersistentArray<String> array2 = array1.set(0, "foo");
        PersistentArray<String> array3 = array2.resize(32);
        assertEquals("foo", array3.get(0));        
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void assertThatResizePreservesOriginalValues()
    {
        PersistentArray<String> array1 = new DiffPersistentArray<String>(1);
        array1.resize(32);
        assertEquals(array1.size(), 1);
        array1.set(12, "bum");
    }

    @Test
    public void assertThatResizeCanBePerformedMultipleTimes()
    {
        PersistentArray<String> array1 = new DiffPersistentArray<String>(1);
        array1.resize(32);
        assertEquals(array1.size(), 1);
        array1.resize(32);
        assertEquals(array1.size(), 1);
    }

    @Test
    public void assertThatToStringWorksWithZeroOneEmptySlotAndTwoElements()
    {
        PersistentArray<String> array = new DiffPersistentArray<String>(3)
            .set(1, "foo")
            .set(2, "bar");
        assertEquals("[null, foo, bar]", array.toString());
    }
}
