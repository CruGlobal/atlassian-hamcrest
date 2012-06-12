package com.atlassian.hamcrest;

import static java.util.Arrays.*;
import static com.atlassian.hamcrest.DeepIsEqual.deeplyEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class DeepIsEqualPrimitiveFieldsTest
{
    private final AllPrimitives actual;
    private final AllPrimitives expected;

    @Parameters
    public static Collection<Object[]> data()
    {
        return asList(new Object[][]
        {
                { new AllPrimitives(), new AllPrimitives().with(false) },
                { new AllPrimitives(), new AllPrimitives().with((byte) 10) },
                { new AllPrimitives(), new AllPrimitives().with((short) 20) },
                { new AllPrimitives(), new AllPrimitives().with(30) },
                { new AllPrimitives(), new AllPrimitives().with((long) 40) },
                { new AllPrimitives(), new AllPrimitives().with(50.0f) },
                { new AllPrimitives(), new AllPrimitives().with(60.0) },
                { new AllPrimitives(), new AllPrimitives().with('z') },
                { new AllPrimitives(), new AllPrimitives().with("World") }                
        });
    }
    
    public DeepIsEqualPrimitiveFieldsTest(AllPrimitives actual, AllPrimitives expected)
    {
        this.actual = actual;
        this.expected = expected;
    }
    
    @Test
    public void assertThatDeepIsEqualDoesNotMatches()
    {
        assertThat(actual, is(not(deeplyEqualTo(expected))));
    }

    static class AllPrimitives
    {
        final boolean b;
        final byte by;
        final short s;
        final int i;
        final long l;
        final float f;
        final double d;
        final char c;
        final String str;

        public AllPrimitives()
        {
            this(true, (byte) 1, (short) 2, 3, 4L, 5.0f, 6.0, 'a', "Hello");
        }
        
        public AllPrimitives(boolean b, byte by, short s, int i, long l, float f, double d, char c, String str)
        {
            this.b = b;
            this.by = by;
            this.s = s;
            this.i = i;
            this.l = l;
            this.f = f;
            this.d = d;
            this.c = c;
            this.str = str;
        }
        
        AllPrimitives with(boolean b)
        {
            return new AllPrimitives(b, by, s, i, l, f, d, c, str);
        }

        AllPrimitives with(byte by)
        {
            return new AllPrimitives(b, by, s, i, l, f, d, c, str);
        }

        AllPrimitives with(short s)
        {
            return new AllPrimitives(b, by, s, i, l, f, d, c, str);
        }

        AllPrimitives with(int i)
        {
            return new AllPrimitives(b, by, s, i, l, f, d, c, str);
        }

        AllPrimitives with(long l)
        {
            return new AllPrimitives(b, by, s, i, l, f, d, c, str);
        }

        AllPrimitives with(float f)
        {
            return new AllPrimitives(b, by, s, i, l, f, d, c, str);
        }

        AllPrimitives with(double d)
        {
            return new AllPrimitives(b, by, s, i, l, f, d, c, str);
        }

        AllPrimitives with(char c)
        {
            return new AllPrimitives(b, by, s, i, l, f, d, c, str);
        }

        AllPrimitives with(String str)
        {
            return new AllPrimitives(b, by, s, i, l, f, d, c, str);
        }
    }
}
