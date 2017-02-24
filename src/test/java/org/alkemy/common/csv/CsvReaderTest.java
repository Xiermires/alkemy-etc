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
package org.alkemy.common.csv;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.alkemy.Alkemy;
import org.alkemy.parse.impl.AbstractAlkemyElement;
import org.alkemy.util.Nodes.TypifiedNode;
import org.alkemy.visitor.AlkemyNodeVisitor.Entry;
import org.alkemy.visitor.impl.AlkemyPreorderReader;
import org.junit.Test;

public class CsvReaderTest
{
    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String EXAMPLE = "0,1.2,2.3,12345678902,4" + NEW_LINE + "9,1.65,7f,12345678901,5";

    @Test
    public void testCsvReader() throws IOException
    {
        // Simulate the whole csv is a file process (although we only need an Iterator<String>)
        final BufferedReader reader = new BufferedReader(new InputStreamReader(
                new ByteArrayInputStream(EXAMPLE.getBytes("UTF-8"))));

        final AlkemyPreorderReader<TestClass, String> anv = new AlkemyPreorderReader<>(true, true, false);
        final TypifiedNode<TestClass, ? extends AbstractAlkemyElement<?>> node = Alkemy.nodes().get(TestClass.class);
        final CsvReader aev = new CsvReader();

        final List<TestClass> tcs = new ArrayList<>();
        for (Entry<TestClass, String> entry : anv.peekIterable(aev, node, reader.lines().iterator()))
        {
            if (entry.peekNext() != null)
            {
                aev.update(entry.peekNext());
            }
            if (entry.result() != null)
            {
                tcs.add(entry.result());
            }
        }

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

        reader.close();
    }
}
