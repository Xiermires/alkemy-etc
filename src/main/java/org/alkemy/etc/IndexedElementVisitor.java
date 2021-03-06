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

import org.alkemy.annotations.AlkemyLeaf;
import org.alkemy.common.parse.impl.VisitableAlkemyElement;
import org.alkemy.common.visitor.AlkemyElementVisitor;
import org.alkemy.etc.IndexedElementVisitor.IndexedElement;

public abstract class IndexedElementVisitor<P> implements AlkemyElementVisitor<P, IndexedElement>
{
    @Override
    public IndexedElement map(VisitableAlkemyElement e)
    {
        return new IndexedElement(e);
    }

    @Override
    public boolean accepts(Class<?> type)
    {
        return Index.class == type;
    }

    public static class IndexedElement extends VisitableAlkemyElement
    {
        private final int index;

        protected IndexedElement(VisitableAlkemyElement ae)
        {
            super(ae);
            index = ae.desc().getAnnotation(Index.class).value();
        }

        public int getIndex()
        {
            return index;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD })
    @AlkemyLeaf(Index.class)
    public @interface Index
    {
        int value();
    }
}
