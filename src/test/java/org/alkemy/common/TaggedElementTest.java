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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiFunction;

import org.alkemy.Alkemy;
import org.alkemy.common.TaggedElementVisitor.FluentTaggedElementVisitor;
import org.alkemy.parse.impl.AbstractAlkemyElement;
import org.alkemy.util.Measure;
import org.alkemy.util.Nodes.TypifiedNode;
import org.alkemy.visitor.impl.AlkemyPreorderReader.FluentAlkemyPreorderReader;
import org.junit.Test;

public class TaggedElementTest
{
    @Test
    public void testStaticTags()
    {
        final Properties props = new Properties();
        new FluentAlkemyPreorderReader<TestClass>(0).accept(new FunctionOnTaggedSlow<>((a, b) -> props.put(a, b)),
                Alkemy.nodes().get(TestClass.class), new TestClass());

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
        new FluentAlkemyPreorderReader<TestClass>(0).accept(
                new FunctionOnTaggedSlow<TestClass>((a, b) -> props.put(a, b)).dynamicVariables(dynParams()),
                Alkemy.nodes().get(TestClass.class), new TestClass());

        assertThat(props, hasEntry("id10", 5));
        assertThat(props, hasEntry("aaa.bbb.ccc.ddd.eee", 6));
    }

    @Test
    public void testStaticTagsFast()
    {
        final Properties props = new Properties();
        new FluentAlkemyPreorderReader<TestClass>(0).accept(new FunctionOnTaggedSlow<>((a, b) -> props.put(a, b)),
                Alkemy.nodes().get(TestClass.class), new TestClass());

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
        new FluentAlkemyPreorderReader<TestClass>(0).accept(
                new FunctionOnTaggedFast<TestClass>((a, b) -> props.put(a, b)).dynamicVariables(dynParams()),
                Alkemy.nodes().get(TestClass.class), new TestClass());

        assertThat(props, hasEntry("id10", 5));
        assertThat(props, hasEntry("aaa.bbb.ccc.ddd.eee", 6));
    }

    @Test
    public void performanceTaggedSlowVersion() throws Throwable
    {
        final Properties props = new Properties();
        final TestClass tc = new TestClass();
        final FluentAlkemyPreorderReader<TestClass> anv = new FluentAlkemyPreorderReader<>(0);
        final FluentTaggedElementVisitor<TestClass> aev = new FunctionOnTaggedSlow<TestClass>((a, b) -> props.put(a, b))
                .dynamicVariables(dynParams());
        final TypifiedNode<TestClass, ? extends AbstractAlkemyElement<?>> node = Alkemy.nodes().get(TestClass.class);

        System.out.println("Handle 5e6 tagged elements (slow version): " + Measure.measure(() ->
        {
            for (int i = 0; i < 1000000; i++)
            {
                anv.accept(aev, node, tc);
            }
        }) / 1000000 + " ms");
    }

    @Test
    public void performanceTaggedFastVersion() throws Throwable
    {
        final Properties props = new Properties();
        final TestClass tc = new TestClass();
        final FluentAlkemyPreorderReader<TestClass> anv = new FluentAlkemyPreorderReader<>(0);
        final FluentTaggedElementVisitor<TestClass> aev = new FunctionOnTaggedFast<TestClass>((a, b) -> props.put(a, b))
                .dynamicVariables(dynParams());
        final TypifiedNode<TestClass, ? extends AbstractAlkemyElement<?>> node = Alkemy.nodes().get(TestClass.class);
        
        System.out.println("Handle 5e6 tagged elements (fast version): " + Measure.measure(() ->
        {
            for (int i = 0; i < 1000000; i++)
            {
                anv.accept(aev, node, tc);
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

    public class FunctionOnTaggedSlow<P> extends FluentTaggedElementVisitor<P>
    {
        private final BiFunction<String, Object, Object> f;

        public FunctionOnTaggedSlow(BiFunction<String, Object, Object> f)
        {
            this.f = f;
        }

        @Override
        public void visit(TaggedElement e, Object parent)
        {
            f.apply(e.isDynamic ? DynamicTag.replace(e.raw, dynamicVariables, p) : e.raw, e.get(parent));
        }
    }

    public class FunctionOnTaggedFast<R> extends FluentTaggedElementVisitor<R>
    {
        private final BiFunction<String, Object, Object> f;

        public FunctionOnTaggedFast(BiFunction<String, Object, Object> f)
        {
            this.f = f;
        }

        @Override
        public void visit(TaggedElement e, Object parent)
        {
            f.apply(e.isDynamic ? DynamicTag.replaceFast(e.raw, dynamicVariables) : e.raw, e.get(parent));
        }
    }
}
