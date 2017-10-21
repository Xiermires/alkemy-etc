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
package org.alkemy.etc.setting;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Properties;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class SettingStoreTest
{
    @Test
    public void testSettingStore()
    {
        final Properties props = new Properties();

        // Setting keys - values
        props.put("win.zip.foo", 1);
        props.put("lnx.zip.foo", 2);
        props.put("win.bar", 1);
        props.put("lnx.bar", 2);
        props.put("lorem.ipsum.dolor", 3);

        final IntProvider intProvider = new IntProvider(props);

        final SettingStore store = new SettingStore(intProvider);

        // Load win settings.
        final TestClass tcw = store.read(TestClass.class, ImmutableMap.of("os", "win", "app", "zip"));
        assertThat(tcw.bar, is(1));
        assertThat(tcw.foo, is(1));
        assertThat(tcw.inner.lorem, is(3));

        // Load lnx settings.
        final TestClass tcl = store.read(TestClass.class, ImmutableMap.of("os", "lnx", "app", "zip"));
        assertThat(tcl.bar, is(2));
        assertThat(tcl.foo, is(2));
        assertThat(tcl.inner.lorem, is(3));

        tcw.foo = 4;
        tcw.bar = 5;
        tcw.inner.lorem = 6;

        // Persist win settings
        store.write(tcw, ImmutableMap.of("os", "win", "app", "zip"));

        assertThat(props.get("win.zip.foo"), is(4));
        assertThat(props.get("win.bar"), is(5));
        assertThat(props.get("lorem.ipsum.dolor"), is(6));

        tcl.foo = 7;
        tcl.bar = 8;
        tcl.inner.lorem = 9;

        // Persist lnx settings
        store.write(tcl, ImmutableMap.of("os", "lnx", "app", "zip"));

        assertThat(props.get("lnx.zip.foo"), is(7));
        assertThat(props.get("lnx.bar"), is(8));
        assertThat(props.get("lorem.ipsum.dolor"), is(9));
    }

    static class IntProvider implements Provider
    {
        final Properties props;

        IntProvider(Properties props)
        {
            this.props = props;
        }

        @Override
        public Object getValue(String key, Class<?> type)
        {
            return props.get(key);
        }

        @Override
        public Object setValue(String key, Object value)
        {
            return props.put(key, value);
        }
    }
}
