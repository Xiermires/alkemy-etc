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

import static org.alkemy.common.visitor.impl.AbstractTraverser.INSTANTIATE_NODES;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.alkemy.common.AlkemyCommon;
import org.alkemy.common.AlkemyCommon.SingleTypeReader;
import org.junit.Test;

public class LongMaskVisitorTest
{
    @Test
    public void testMaskOneBit()
    {
        final LongMaskVisitor aev = new LongMaskVisitor();
        final SingleTypeReader<TestClass, Long> reader = AlkemyCommon.reader(TestClass.class, Long.class).preorder(INSTANTIATE_NODES);

        final TestClass tc15 = reader.create(aev, BitMask.bytesToLong(new byte[] { 15 }));

        assertThat(tc15.a, is(1));
        assertThat(tc15.b, is(1));
        assertThat(tc15.c, is(1));
        assertThat(tc15.d, is(1));

        final TestClass tc8 = reader.create(aev, 8l);

        assertThat(tc8.a, is(1));
        assertThat(tc8.b, is(0));
        assertThat(tc8.c, is(0));
        assertThat(tc8.d, is(0));

        final TestClass tc13 = reader.create(aev, 13l);

        assertThat(tc13.a, is(1));
        assertThat(tc13.b, is(1));
        assertThat(tc13.c, is(0));
        assertThat(tc13.d, is(1));
    }

    @Test
    public void testMp3Frame()
    {
        final Long header = BitMask.bytesToLong(new byte[] { -1, -5, -112, 0 });
        final TestMp3Frame frame = AlkemyCommon.reader(TestMp3Frame.class, Long.class)//
                .preorder(INSTANTIATE_NODES).create(new LongMaskVisitor(), header);

        assertThat(frame.framSync, is(2047));
        assertThat(frame.version, is(3));
        assertThat(frame.layer, is(1));
        assertThat(frame.bitrate, is(9));
        assertThat(frame.samplerate, is(0));
        assertThat(frame.padding, is(0));
    }
}
