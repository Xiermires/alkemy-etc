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
package org.alkemy.common;

import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertThat;

import java.util.Properties;
import java.util.function.BiFunction;

import org.alkemy.Alkemy;
import org.alkemy.parse.impl.AbstractAlkemyElement;
import org.alkemy.util.Measure;
import org.alkemy.util.Nodes.TypifiedNode;
import org.alkemy.visitor.impl.AlkemyPreorderReader;
import org.junit.Test;

public class IndexedElementTest
{
    @Test
    public void testIndexedElement()
    {
        final Properties m = new Properties();
        final TestClass tc = new TestClass();
        new AlkemyPreorderReader<TestClass, Object>(0).accept(new FunctionOnIndexed<TestClass>((a, b) -> m.put(a, b)),
                Alkemy.nodes().get(TestClass.class), tc);

        assertThat(m, hasEntry(0, 4));
        assertThat(m, hasEntry(1, 3));
        assertThat(m, hasEntry(2, 2));
        assertThat(m, hasEntry(3, 1));
        assertThat(m, hasEntry(4, 0));
    }

    @Test
    public void performanceIndexed() throws Throwable
    {
        final Properties m = new Properties();
        
        final TestClass tc = new TestClass();
        final AlkemyPreorderReader<TestClass, Object> anv = new AlkemyPreorderReader<>(0);
        final FunctionOnIndexed<TestClass> aev = new FunctionOnIndexed<>((a, b) -> m.put(a, b));
        final TypifiedNode<TestClass, ? extends AbstractAlkemyElement<?>> node = Alkemy.nodes().get(TestClass.class);

        System.out.println("Handle 5e6 indexed elements: " + Measure.measure(() ->
        {
            for (int i = 0; i < 1000000; i++)
            {
                anv.accept(aev, node, tc);
            }
        }) / 1000000 + " ms");
    }

    public class FunctionOnIndexed<P> extends IndexedElementVisitor<P>
    {
        private BiFunction<Integer, Object, Object> f;

        public FunctionOnIndexed(BiFunction<Integer, Object, Object> f)
        {
            this.f = f;
        }

        @Override
        public void visit(IndexedElement e, Object parent)
        {
            f.apply(e.getIndex(), e.get(parent));
        }
    }
}
