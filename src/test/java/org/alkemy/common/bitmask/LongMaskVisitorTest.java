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

import static org.alkemy.visitor.impl.AbstractTraverser.INCLUDE_NULL_BRANCHES;
import static org.alkemy.visitor.impl.AbstractTraverser.INSTANTIATE_NODES;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.alkemy.Alkemy;
import org.alkemy.visitor.impl.AlkemyPreorderReader;
import org.junit.Test;

public class LongMaskVisitorTest
{
    @Test
    public void testMaskOneBit()
    {
        final LongMaskVisitor<Long> aev = new LongMaskVisitor<>();
        final AlkemyPreorderReader<TestClass, Long> anv = new AlkemyPreorderReader<>(INCLUDE_NULL_BRANCHES | INSTANTIATE_NODES);
        final TestClass tc15 = anv.accept(aev, Alkemy.nodes().get(TestClass.class), BitMask.bytesToLong(new byte[] { 15 }));

        assertThat(tc15.a, is(1));
        assertThat(tc15.b, is(1));
        assertThat(tc15.c, is(1));
        assertThat(tc15.d, is(1));

        final TestClass tc8 = anv.accept(aev, Alkemy.nodes().get(TestClass.class), 8l);

        assertThat(tc8.a, is(1));
        assertThat(tc8.b, is(0));
        assertThat(tc8.c, is(0));
        assertThat(tc8.d, is(0));

        final TestClass tc13 = anv.accept(aev, Alkemy.nodes().get(TestClass.class), 13l);

        assertThat(tc13.a, is(1));
        assertThat(tc13.b, is(1));
        assertThat(tc13.c, is(0));
        assertThat(tc13.d, is(1));
    }

    @Test
    public void testMp3Frame()
    {
        final LongMaskVisitor<Long> aev = new LongMaskVisitor<>();
        final AlkemyPreorderReader<TestMp3Frame, Long> anv = new AlkemyPreorderReader<>(INCLUDE_NULL_BRANCHES | INSTANTIATE_NODES);
        final TestMp3Frame frame = anv.accept(aev, Alkemy.nodes().get(TestMp3Frame.class), BitMask.bytesToLong(new byte[] { -1, -5, -112, 0 }));

        assertThat(frame.framSync, is(2047));
        assertThat(frame.version, is(3));
        assertThat(frame.layer, is(1));
        assertThat(frame.bitrate, is(9));
        assertThat(frame.samplerate, is(0));
        assertThat(frame.padding, is(0));
    }
}
