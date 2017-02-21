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
package org.alkemy.common.util;

import java.util.function.Function;

import org.alkemy.common.IndexedElementVisitor.IndexedElement;
import org.alkemy.util.AbstractAlkemyValueProvider;
import org.alkemy.util.Assertions;

public class TypedValueFromString extends AbstractAlkemyValueProvider<IndexedElement>
{
    private Function<Integer, String> provider;

    public TypedValueFromString(Function<Integer, String> provider)
    {
        this.provider = provider;
    }

    @Override
    public Double getDouble(IndexedElement e)
    {
        return Double.parseDouble(provider.apply(e.getIndex()));
    }

    @Override
    public Float getFloat(IndexedElement e)
    {
        return Float.parseFloat(provider.apply(e.getIndex()));
    }

    @Override
    public Long getLong(IndexedElement e)
    {
        return Long.parseLong(provider.apply(e.getIndex()));
    }

    @Override
    public Integer getInteger(IndexedElement e)
    {
        return Integer.parseInt(provider.apply(e.getIndex()));
    }

    @Override
    public Short getShort(IndexedElement e)
    {
        return Short.parseShort(provider.apply(e.getIndex()));
    }

    @Override
    public Byte getByte(IndexedElement e)
    {
        return Byte.parseByte(provider.apply(e.getIndex()));
    }

    @Override
    public Character getChar(IndexedElement e)
    {
        final String s = provider.apply(e.getIndex());
        Assertions.isTrue(s.length() == 1, String.format("The string '%s' can't be converted to char.", s));
        return Character.valueOf(s.charAt(0));
    }

    @Override
    public Boolean getBoolean(IndexedElement e)
    {
        return Boolean.valueOf(provider.apply(e.getIndex()));
    }

    @Override
    public Object getObject(IndexedElement e)
    {
        throw new UnsupportedOperationException("not implemented.");
    }
}
