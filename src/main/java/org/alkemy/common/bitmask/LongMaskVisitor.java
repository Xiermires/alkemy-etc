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

import org.alkemy.common.bitmask.BitMask.Bits;
import org.alkemy.parse.impl.AbstractAlkemyElement.AlkemyElement;
import org.alkemy.util.Assertions;
import org.alkemy.visitor.AlkemyElementVisitor;

public class LongMaskVisitor implements AlkemyElementVisitor<BitMask>
{
    @Override
    public BitMask map(AlkemyElement e)
    {
        return new BitMask(e);
    }

    @Override
    public void visit(BitMask element, Object parent, Object... args)
    {
        Assertions.ofSize(args, 1);
        Assertions.ofListedType(args[0], Long.class);
        final Long l = (Long) (args[0]);
        element.set(l >>> element.offset & (2 << element.bitCount - 1) -1, parent); 
    }

    @Override
    public boolean accepts(Class<?> type)
    {
        return Bits.class == type;
    }
}
