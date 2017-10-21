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
package org.alkemy.etc.csv;

import static org.alkemy.common.visitor.impl.AbstractTraverser.INSTANTIATE_NODES;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import org.alkemy.common.AlkemyCommon;
import org.alkemy.common.AlkemyCommon.SingleTypeReader;
import org.junit.Test;

public class CsvReaderTest
{
    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String EXAMPLE = "0,1.2,2.3,12345678902,4" + NEW_LINE + "9,1.65,7f,12345678901,5";

    @Test
    public void testCsvReader() throws IOException
    {
        // Simulate the whole csv is a file process (although we only need an Iterator<String>)
        final BufferedReader buffer = new BufferedReader(new InputStreamReader(
                new ByteArrayInputStream(EXAMPLE.getBytes("UTF-8"))));

        final CsvReader aev = new CsvReader();
        final SingleTypeReader<TestClass, String[]> reader = AlkemyCommon.reader(TestClass.class, String[].class)//
                .preorder(INSTANTIATE_NODES);

        final List<TestClass> tcs = buffer.lines().map(l -> l.split(",")).map(l -> reader.create(aev, l)).collect(
                Collectors.toList());

        assertThat(tcs.size(), is(2));

        assertThat(tcs.get(0).a, is(0));
        assertThat(tcs.get(0).b, is(1.2d));
        assertThat(tcs.get(0).c, is(2.3f));
        assertThat(tcs.get(0).d, is(12345678902l));
        assertThat(tcs.get(0).e, is(4));

        assertThat(tcs.get(1).a, is(9));
        assertThat(tcs.get(1).b, is(1.65d));
        assertThat(tcs.get(1).c, is(7f));
        assertThat(tcs.get(1).d, is(12345678901l));
        assertThat(tcs.get(1).e, is(5));

        buffer.close();
    }
}
