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
