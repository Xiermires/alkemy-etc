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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.alkemy.annotations.AlkemyLeaf;
import org.alkemy.common.parse.impl.VisitableAlkemyElement;
import org.alkemy.common.visitor.AlkemyElementVisitor;
import org.alkemy.etc.TaggedElementVisitor.TaggedElement;

public abstract class TaggedElementVisitor<P> implements AlkemyElementVisitor<P, TaggedElement>
{
    protected final Pattern p;
    protected Map<String, String> dynamicVariables = new HashMap<>();

    public TaggedElementVisitor()
    {
        this("\\{&(.+?)\\}");
    }

    public TaggedElementVisitor(String dynParamPattern)
    {
        this.p = Pattern.compile(dynParamPattern);
    }

    public TaggedElementVisitor<P> dynamicVariables(Map<String, String> dynamicVariables)
    {
        this.dynamicVariables = dynamicVariables;
        return this;
    }

    @Override
    public TaggedElement map(VisitableAlkemyElement e)
    {
        final TaggedElement te = new TaggedElement(e);
        if (te.isDynamic == null)
        {
            te.isDynamic = DynamicVariable.isDynamic(te.raw, p);
        }
        return te;
    }

    @Override
    public boolean accepts(Class<?> type)
    {
        return Tag.class == type;
    }

    // A fluent version of the TaggedElementVisitor
    public static abstract class FluentTaggedElementVisitor<P> extends TaggedElementVisitor<P> implements
            AlkemyElementVisitor<P, TaggedElement>
    {
        @Override
        public FluentTaggedElementVisitor<P> dynamicVariables(Map<String, String> dynamicVariables)
        {
            this.dynamicVariables = dynamicVariables;
            return this;
        }
    }

    protected static class TaggedElement extends VisitableAlkemyElement
    {
        public final String raw;
        public Boolean isDynamic = null;

        protected TaggedElement(VisitableAlkemyElement ae)
        {
            super(ae);
            raw = ae.desc().getAnnotation(Tag.class).value();
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD })
    @AlkemyLeaf(Tag.class)
    public @interface Tag
    {
        String value() default "";
    }
}
