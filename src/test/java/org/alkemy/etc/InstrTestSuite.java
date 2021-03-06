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

import org.alkemy.InstrumentClassWithLambdas;
import org.alkemy.InstrumentClassWithLambdas.Instr;
import org.alkemy.InstrumentClassWithLambdas.InstrumentableLambdaClasses;
import org.alkemy.instr.AlkemizerCTF;
import org.junit.runner.RunWith;

@RunWith(InstrumentClassWithLambdas.class)
@InstrumentableLambdaClasses(//
testClassNames = { "org.alkemy.etc.IndexedElementTest", //
        "org.alkemy.etc.TaggedElementTest", //
        "org.alkemy.etc.bitmask.LongMaskVisitorTest", //
        "org.alkemy.etc.csv.CsvReaderTest", //
        "org.alkemy.etc.setting.SettingStoreTest", //
        "org.alkemy.etc.model.DataModelTest"
}, //
instrs = @Instr(classNames = { "org.alkemy.etc.TestClass", //
        "org.alkemy.etc.bitmask.TestClass", //
        "org.alkemy.etc.bitmask.TestMp3Frame", //
        "org.alkemy.etc.csv.TestClass", //
        "org.alkemy.etc.setting.TestClass", //
        "org.alkemy.etc.setting.InnerTestClass", //
        "org.alkemy.etc.model.Person", //
        "org.alkemy.etc.model.Address"
}, ctf = AlkemizerCTF.class))
public class InstrTestSuite
{
}