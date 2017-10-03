/*******************************************************************************
 * Copyright (c) 2017, Xavier Miret Andres <xavier.mires@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package org.alkemy.etc.bitmask;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.alkemy.annotations.AlkemyLeaf;
import org.alkemy.common.parse.impl.VisitableAlkemyElement;
import org.alkemy.util.Assertions;

public class BitMask extends VisitableAlkemyElement
{
    private static long[] lmask = new long[] { 0xFF00000000000000l, //
            0x00FF000000000000l, //
            0x0000FF0000000000l, //
            0x000000FF00000000l, //
            0x00000000FF000000l, //
            0x0000000000FF0000l, //
            0x000000000000FF00l, //
            0x00000000000000FFl };

    static long bytesToLong(Object arg)
    {
        Assertions.ofListedType(arg, byte[].class, Integer.class, Long.class);

        if (arg instanceof byte[])
        {
            final byte[] bb = (byte[]) arg;
            Assertions.lessEqualThan(bb.length, Long.BYTES);
            return shifAndMask(bb);
        }
        else if (arg instanceof Integer)
        {
            return ((Number) arg).longValue();
        }
        else if (arg instanceof Long)
        {
            return (Long) arg;
        }
        throw new RuntimeException("Invalid type"); // should never happen
    }

    private static long shifAndMask(byte[] bytes)
    {
        long l = 0;
        for (int i = bytes.length; i > 0; i--)
        {
            final int shift = 8 * (i - 1);
            final long mask = lmask[8 - i];
            l = l | ((bytes[bytes.length - i] << shift) & mask);
        }
        return l;
    }

    final int offset;
    final int bitCount;

    protected BitMask(VisitableAlkemyElement other)
    {
        super(other);

        final Bits bit = other.desc().getAnnotation(Bits.class);
        Assertions.nonNull(bit);
        Assertions.greaterEqualThan(bit.shift(), 0);
        Assertions.greaterEqualThan(bit.bitCount(), 0);

        offset = bit.shift();
        bitCount = bit.bitCount();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD })
    @AlkemyLeaf(Bits.class)
    public @interface Bits
    {
        int shift(); 

        int bitCount() default 1;
    }
}