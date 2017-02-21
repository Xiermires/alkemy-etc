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

import org.alkemy.Alkemist;
import org.alkemy.AlkemistBuilder;
import org.alkemy.util.Measure;
import org.junit.Test;

public class TaggedElementTest
{
    @Test
    public void testStaticTags()
    {
        final Properties props = new Properties();
        final Alkemist alkemist = new AlkemistBuilder().visitor(new FunctionOnTaggedSlow((a, b) -> props.put(a, b))).build();
        final TestClass tc = new TestClass();

        alkemist.process(tc);

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
        final Map<String, String> dynParams = new HashMap<>();
        dynParams.put("dyn1", "id10");
        dynParams.put("prefix", "aaa");
        dynParams.put("infix", "ccc");
        dynParams.put("suffix", "eee");
        final TaggedElementVisitor aev = new FunctionOnTaggedSlow((a, b) -> props.put(a, b)).dynamicVariables(dynParams);
        final Alkemist alkemist = new AlkemistBuilder().visitor(aev).build();
        final TestClass tc = new TestClass();

        alkemist.process(tc);

        assertThat(props, hasEntry("id10", 5));
        assertThat(props, hasEntry("aaa.bbb.ccc.ddd.eee", 6));
    }

    @Test
    public void testStaticTagsFast()
    {
        final Properties props = new Properties();
        final Alkemist alkemist = new AlkemistBuilder().visitor(new FunctionOnTaggedFast((a, b) -> props.put(a, b))).build();
        final TestClass tc = new TestClass();

        alkemist.process(tc);

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
        final Map<String, String> dynParams = new HashMap<>();
        dynParams.put("dyn1", "id10");
        dynParams.put("prefix", "aaa");
        dynParams.put("infix", "ccc");
        dynParams.put("suffix", "eee");
        final TaggedElementVisitor aev = new FunctionOnTaggedFast((a, b) -> props.put(a, b)).dynamicVariables(dynParams);
        final Alkemist alkemist = new AlkemistBuilder().visitor(aev).build();
        final TestClass tc = new TestClass();

        alkemist.process(tc);

        assertThat(props, hasEntry("id10", 5));
        assertThat(props, hasEntry("aaa.bbb.ccc.ddd.eee", 6));
    }

    @Test
    public void performanceTaggedSlowVersion() throws Throwable
    {
        final Properties props = new Properties();
        final Map<String, String> dynParams = new HashMap<>();
        dynParams.put("dyn1", "id10");
        dynParams.put("prefix", "aaa");
        dynParams.put("infix", "ccc");
        dynParams.put("suffix", "eee");
        final Alkemist alkemist = new AlkemistBuilder().visitor(
                new FunctionOnTaggedSlow((a, b) -> props.put(a, b)).dynamicVariables(dynParams)).build();
        final TestClass tc = new TestClass();

        System.out.println("Handle 5e6 tagged elements (slow version): " + Measure.measure(() ->
        {
            for (int i = 0; i < 1000000; i++)
            {
                alkemist.process(tc);
            }
        }) / 1000000 + " ms");
    }

    @Test
    public void performanceTaggedFastVersion() throws Throwable
    {
        final Properties m = new Properties();
        final Map<String, String> dynParams = new HashMap<>();
        dynParams.put("dyn1", "id10");
        dynParams.put("prefix", "aaa");
        dynParams.put("infix", "ccc");
        dynParams.put("suffix", "eee");
        final Alkemist alkemist = new AlkemistBuilder().visitor(
                new FunctionOnTaggedFast((a, b) -> m.put(a, b)).dynamicVariables(dynParams)).build();
        final TestClass tc = new TestClass();

        System.out.println("Handle 5e6 tagged elements (fast version): " + Measure.measure(() ->
        {
            for (int i = 0; i < 1000000; i++)
            {
                alkemist.process(tc);
            }
        }) / 1000000 + " ms");
    }

    public class FunctionOnTaggedSlow extends TaggedElementVisitor
    {
        private final BiFunction<String, Object, Object> f;

        public FunctionOnTaggedSlow(BiFunction<String, Object, Object> f)
        {
            this.f = f;
        }

        @Override
        public void visitArgs(TaggedElement e, Object parent, Object... args)
        {
            f.apply(e.isDynamic ? DynamicTag.replace(e.raw, dynamicVariables, p) : e.raw, e.get(parent));
        }
    }

    public class FunctionOnTaggedFast extends TaggedElementVisitor
    {
        private final BiFunction<String, Object, Object> f;

        public FunctionOnTaggedFast(BiFunction<String, Object, Object> f)
        {
            this.f = f;
        }

        @Override
        public void visitArgs(TaggedElement e, Object parent, Object... args)
        {
            f.apply(e.isDynamic ? DynamicTag.replaceFast(e.raw, dynamicVariables) : e.raw, e.get(parent));
        }
    }
}
