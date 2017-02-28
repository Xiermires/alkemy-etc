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
import org.alkemy.util.Measure;
import org.alkemy.visitor.impl.SingleTypeReader;
import org.junit.Test;

public class TaggedElementTest
{
    @Test
    public void testStaticTags()
    {
        final Properties props = new Properties();
        Alkemy.mature(TestClass.class, new FunctionOnTaggedSlow<>((a, b) -> props.put(a, b)));

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
        Alkemy.mature(TestClass.class,
                new FunctionOnTaggedSlow<TestClass>((a, b) -> props.put(a, b)).dynamicVariables(dynParams()));

        assertThat(props, hasEntry("id10", 5));
        assertThat(props, hasEntry("aaa.bbb.ccc.ddd.eee", 6));
    }

    @Test
    public void testStaticTagsFast()
    {
        final Properties props = new Properties();
        Alkemy.mature(TestClass.class, new FunctionOnTaggedSlow<>((a, b) -> props.put(a, b)));

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
        Alkemy.mature(TestClass.class,
                new FunctionOnTaggedFast<TestClass>((a, b) -> props.put(a, b)).dynamicVariables(dynParams()));

        assertThat(props, hasEntry("id10", 5));
        assertThat(props, hasEntry("aaa.bbb.ccc.ddd.eee", 6));
    }

    @Test
    public void performanceTaggedSlowVersion() throws Throwable
    {
        final Properties props = new Properties();
        final TestClass tc = new TestClass();

        final SingleTypeReader<TestClass, ?> anv = Alkemy.reader(TestClass.class).preorder(0);
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
     
        final SingleTypeReader<TestClass,  ?> anv = Alkemy.reader(TestClass.class).preorder(0);
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
