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
package org.alkemy.common.csv;

import org.alkemy.common.IndexedElementVisitor;
import org.alkemy.common.util.TypedValueFromString;

public class CsvReader extends IndexedElementVisitor<String>
{
    String[] line;
    final TypedValueFromString tvfs = new TypedValueFromString(f -> line[f]);
    
    public void update(String line)
    {
        if (line == null)
        {
            this.line = null;
        }
        else
        {
            this.line = line.split(",");
        }
    }
    
    @Override
    public void visit(IndexedElement e, Object parent, String parameter)
    {
        if (line == null)
        {
            update(parameter);
        }
        e.set(tvfs.getValue(e), parent);
    }
}
