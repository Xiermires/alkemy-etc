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

import org.alkemy.etc.IndexedElementVisitor.Index;
import org.alkemy.etc.TaggedElementVisitor.Tag;

public class TestClass
{
    @Index(0)
    int i0 = 4;

    @Index(1)
    int i1 = 3;

    @Index(2)
    int i2 = 2;

    @Index(3)
    int i3 = 1;

    @Index(4)
    int i4 = 0;

    @Tag("id0")
    int i5 = 4;

    @Tag("id1")
    int i6 = 3;

    @Tag("id2")
    int i7 = 2;

    @Tag("id3")
    int i8 = 1;

    @Tag("id4")
    int i9 = 0;

    @Tag("{&dyn1}")
    int i10 = 5;

    @Tag("{&prefix}.bbb.{&infix}.ddd.{&suffix}")
    int i11 = 6;
}
