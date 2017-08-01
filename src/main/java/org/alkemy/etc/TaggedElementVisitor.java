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
