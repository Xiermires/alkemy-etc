/*******************************************************************************
 * Copyright (c) 2017, Xavier Miret Andres <xavier.mires@gmail.com>
 *
 * Permission to use, copy, modify, and/or distribute this software for any 
 * purpose with or without fee is hereby granted, provided that the above 
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES 
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALLIMPLIED WARRANTIES OF 
 * MERCHANTABILITY  AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR 
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES 
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN 
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF 
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package org.alkemy.common.bitmask;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.alkemy.annotations.AlkemyLeaf;
import org.alkemy.parse.impl.AbstractAlkemyElement;
import org.alkemy.util.Assertions;

public class BitMask extends AbstractAlkemyElement<BitMask>
{
    final int offset;
    final int bitCount;

    protected BitMask(AbstractAlkemyElement<?> other)
    {
        super(other);

        final Bits bit = other.desc().getAnnotation(Bits.class);
        Assertions.exists(bit);
        Assertions.greaterEqualThan(bit.pos(), 0);
        Assertions.greaterEqualThan(bit.count(), 0);

        offset = bit.pos();
        bitCount = bit.count();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD })
    @AlkemyLeaf(Bits.class)
    public @interface Bits
    {
        int pos(); // offset

        int count() default 1;
    }

    private static long[] lmask = new long[] { 0xFF00000000000000l, //
            0x00FF000000000000l, //
            0x0000FF0000000000l, //
            0x000000FF00000000l, //
            0x00000000FF000000l, //
            0x0000000000FF0000l, //
            0x000000000000FF00l, //
            0x00000000000000FFl };

    static long asLong(Object arg)
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
}