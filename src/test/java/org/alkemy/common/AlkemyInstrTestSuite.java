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

import org.alkemy.InstrumentClassWithLambdas;
import org.alkemy.InstrumentClassWithLambdas.Instr;
import org.alkemy.InstrumentClassWithLambdas.InstrumentableLambdaClasses;
import org.alkemy.parse.impl.AlkemizerCTF;
import org.junit.runner.RunWith;

@RunWith(InstrumentClassWithLambdas.class)
@InstrumentableLambdaClasses(//
testClassNames = { "org.alkemy.common.IndexedElementTest", //
        "org.alkemy.common.TaggedElementTest", //
        "org.alkemy.common.bitmask.LongMaskVisitorTest", //
        "org.alkemy.common.csv.CsvReaderTest", //
        "org.alkemy.common.setting.SettingHandlerTest"
}, //
instrs = @Instr(classNames = { "org.alkemy.common.TestClass", //
        "org.alkemy.common.bitmask.TestClass", //
        "org.alkemy.common.bitmask.TestMp3Frame", //
        "org.alkemy.common.csv.TestClass", //
        "org.alkemy.common.setting.TestClass"
}, ctf = AlkemizerCTF.class))
public class AlkemyInstrTestSuite
{
}