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

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.alkemy.util.Assertions;

public class DynamicVariable
{
    private DynamicVariable()
    {
    }

    public static boolean isDynamic(String raw, Pattern parameterKey)
    {
        Assertions.noneNull(raw, parameterKey);
        return parameterKey.matcher(raw).find();
    }

    /**
     * Given a parameterKey such as the following: <code>Pattern.compile("\\{&(.+?)\\}")</code>, <br>
     * the method will substitute any s = (.+?) group with the result of parameters.get(s).
     * <p>
     * If any group is left unresolved, a partially resolved string is returned.
     * <p>
     * Example:
     * <p>
     * regex: \\{&(.+?)\\} <br>
     * raw : "{&prefix}.bbb.{&infix}.ddd.{&suffix}" <br>
     * parameters : { { "prefix: "aaa" }, { "infix": "ccc" }, { "suffix": "eee" } } <br>
     * DynamicLabel.solve(raw, parameters, Pattern.compile(regex)) = "aaa.bbb.ccc.ddd.eee"
     */
    // This is elegant and allows user parameter definition but so slow... 
    public static String replace(String raw, Map<String, String> parameters, Pattern parameterKey)
    {
        Assertions.noneNull(raw, parameterKey, parameters);

        final Matcher matcher = parameterKey.matcher(raw);
        final StringBuffer sb = new StringBuffer();
        int s = 0;
        while (matcher.find())
        {
            final String paramKey = matcher.group(1);
            final String replacement = parameters.get(paramKey);
            matcher.appendReplacement(sb, replacement == null ? paramKey : replacement);
            s = matcher.end();
        }
        sb.append(raw.substring(s));
        return sb.toString();
    }

    // fix {&key} definition (which user won't change anyway). It is faster (~ 2.3x) but ugly. 
    public static String replaceFast(String raw, Map<String, String> parameters)
    {
        Assertions.noneNull(raw, parameters);

        int i = 0;
        int j = 0;
        StringBuilder sb = new StringBuilder();
        i = raw.indexOf("{&", i);
        while (i != -1)
        {
            if (i > j)
            {
                sb.append(raw.substring(j, i));
            }
            j = raw.indexOf("}", i);
            sb.append(parameters.get(raw.substring(i + 2, j)));
            i = raw.indexOf("{&", j++);
        }

        if (j < raw.length())
        {
            sb.append(raw.substring(j));
        }
        return sb.toString();
    }
}
