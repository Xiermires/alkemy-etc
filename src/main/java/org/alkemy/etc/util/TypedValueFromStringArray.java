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
package org.alkemy.etc.util;

import org.alkemy.common.util.AbstractAlkemyValueProvider;
import org.alkemy.etc.IndexedElementVisitor.IndexedElement;
import org.alkemy.util.Assertions;

public class TypedValueFromStringArray extends AbstractAlkemyValueProvider<IndexedElement, String[]>
{
    @Override
    public Double getDouble(IndexedElement e, String[] p)
    {
        return Double.parseDouble(p[e.getIndex()]);
    }

    @Override
    public Float getFloat(IndexedElement e, String[] p)
    {
        return Float.parseFloat(p[e.getIndex()]);
    }

    @Override
    public Long getLong(IndexedElement e, String[] p)
    {
        return Long.parseLong(p[e.getIndex()]);
    }

    @Override
    public Integer getInteger(IndexedElement e, String[] p)
    {
        return Integer.parseInt(p[e.getIndex()]);
    }

    @Override
    public Short getShort(IndexedElement e, String[] p)
    {
        return Short.parseShort(p[e.getIndex()]);
    }

    @Override
    public Byte getByte(IndexedElement e, String[] p)
    {
        return Byte.parseByte(p[e.getIndex()]);
    }

    @Override
    public Character getChar(IndexedElement e, String[] p)
    {
        final String s = p[e.getIndex()];
        Assertions.isTrue(s.length() == 1, "The string '%s' can't be converted to char.", s);
        return Character.valueOf(s.charAt(0));
    }

    @Override
    public Boolean getBoolean(IndexedElement e, String[] p)
    {
        return Boolean.valueOf(p[e.getIndex()]);
    }

    @Override
    public Object getObject(IndexedElement e, String[] p)
    {
        throw new UnsupportedOperationException("not implemented.");
    }
}
