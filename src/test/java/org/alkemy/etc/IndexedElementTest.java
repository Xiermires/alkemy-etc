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
package org.alkemy.etc;

import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertThat;

import java.util.Properties;
import java.util.function.BiFunction;

import org.alkemy.common.AlkemyCommon;
import org.alkemy.common.parse.impl.VisitableAlkemyElement;
import org.alkemy.common.visitor.impl.AlkemyPreorderReader;
import org.alkemy.util.Measure;
import org.alkemy.util.Nodes.TypedNode;
import org.junit.Test;

public class IndexedElementTest
{
    @Test
    public void testIndexedElement()
    {
        final Properties m = new Properties();
        final TestClass tc = new TestClass();
        new AlkemyPreorderReader<TestClass, Object>(0).accept(new FunctionOnIndexed<TestClass>((a, b) -> m.put(a, b)),
                AlkemyCommon.rootNode(TestClass.class), tc);

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
        final TypedNode<TestClass, ? extends VisitableAlkemyElement> node = AlkemyCommon.rootNode(TestClass.class);

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
