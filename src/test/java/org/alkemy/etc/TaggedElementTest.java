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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiFunction;

import org.alkemy.common.AlkemyCommon;
import org.alkemy.common.AlkemyCommon.SingleTypeReader;
import org.alkemy.etc.DynamicVariable;
import org.alkemy.etc.TaggedElementVisitor;
import org.alkemy.util.Measure;
import org.junit.Test;

public class TaggedElementTest
{
    @Test
    public void testStaticTags()
    {
        final Properties props = new Properties();
        AlkemyCommon.mature(TestClass.class, new FunctionOnTaggedSlow<>((a, b) -> props.put(a, b)));

        assertThat(props, hasEntry("id0", 4));
        assertThat(props, hasEntry("id1", 3));
        assertThat(props, hasEntry("id2", 2));
        assertThat(props, hasEntry("id3", 1));
        assertThat(props, hasEntry("id4", 0));
    }

    @Test
    public void testDynamicTags()
    {
        final Properties props = new Properties();
        AlkemyCommon.mature(TestClass.class,
                new FunctionOnTaggedSlow<TestClass>((a, b) -> props.put(a, b)).dynamicVariables(dynParams()));

        assertThat(props, hasEntry("id10", 5));
        assertThat(props, hasEntry("aaa.bbb.ccc.ddd.eee", 6));
    }

    @Test
    public void testStaticTagsFast()
    {
        final Properties props = new Properties();
        AlkemyCommon.mature(TestClass.class, new FunctionOnTaggedSlow<>((a, b) -> props.put(a, b)));

        assertThat(props, hasEntry("id0", 4));
        assertThat(props, hasEntry("id1", 3));
        assertThat(props, hasEntry("id2", 2));
        assertThat(props, hasEntry("id3", 1));
        assertThat(props, hasEntry("id4", 0));
    }

    @Test
    public void testDynamicTagsFast()
    {
        final Properties props = new Properties();
        AlkemyCommon.mature(TestClass.class,
                new FunctionOnTaggedFast<TestClass>((a, b) -> props.put(a, b)).dynamicVariables(dynParams()));

        assertThat(props, hasEntry("id10", 5));
        assertThat(props, hasEntry("aaa.bbb.ccc.ddd.eee", 6));
    }

    @Test
    public void performanceTaggedSlowVersion() throws Throwable
    {
        final Properties props = new Properties();
        final TestClass tc = new TestClass();

        final SingleTypeReader<TestClass, ?> anv = AlkemyCommon.reader(TestClass.class).preorder(0);
        final TaggedElementVisitor<?> aev = new FunctionOnTaggedSlow<>((a, b) -> props.put(a, b))
                .dynamicVariables(dynParams());

        System.out.println("Handle 5e6 tagged elements (slow version): " + Measure.measure(() ->
        {
            for (int i = 0; i < 1000000; i++)
            {
                anv.accept(aev, tc);
            }
        }) / 1000000 + " ms");
    }

    @Test
    public void performanceTaggedFastVersion() throws Throwable
    {
        final Properties props = new Properties();
        final TestClass tc = new TestClass();
     
        final SingleTypeReader<TestClass,  ?> anv = AlkemyCommon.reader(TestClass.class).preorder(0);
        final TaggedElementVisitor<TestClass> aev = new FunctionOnTaggedFast<TestClass>((a, b) -> props.put(a, b))
                .dynamicVariables(dynParams());

        System.out.println("Handle 5e6 tagged elements (fast version): " + Measure.measure(() ->
        {
            for (int i = 0; i < 1000000; i++)
            {
                anv.accept(aev, tc);
            }
        }) / 1000000 + " ms");
    }

    private Map<String, String> dynParams()
    {
        final Map<String, String> dynParams = new HashMap<>();
        dynParams.put("dyn1", "id10");
        dynParams.put("prefix", "aaa");
        dynParams.put("infix", "ccc");
        dynParams.put("suffix", "eee");
        return dynParams;
    }

    public class FunctionOnTaggedSlow<P> extends TaggedElementVisitor<P>
    {
        private final BiFunction<String, Object, Object> f;

        public FunctionOnTaggedSlow(BiFunction<String, Object, Object> f)
        {
            this.f = f;
        }

        @Override
        public void visit(TaggedElement e, Object parent)
        {
            f.apply(e.isDynamic ? DynamicVariable.replace(e.raw, dynamicVariables, p) : e.raw, e.get(parent));
        }
    }

    public class FunctionOnTaggedFast<R> extends TaggedElementVisitor<R>
    {
        private final BiFunction<String, Object, Object> f;

        public FunctionOnTaggedFast(BiFunction<String, Object, Object> f)
        {
            this.f = f;
        }

        @Override
        public void visit(TaggedElement e, Object parent)
        {
            f.apply(e.isDynamic ? DynamicVariable.replaceFast(e.raw, dynamicVariables) : e.raw, e.get(parent));
        }
    }
}
