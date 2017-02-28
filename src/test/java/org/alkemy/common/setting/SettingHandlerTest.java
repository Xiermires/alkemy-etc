package org.alkemy.common.setting;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Properties;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class SettingHandlerTest
{
    @Test
    public void testSettingHandler()
    {
        final Properties props = new Properties();

        // Setting keys - values
        props.put("win.zip.foo", 1);
        props.put("lnx.zip.foo", 2);
        props.put("win.bar", 1);
        props.put("lnx.bar", 2);
        props.put("lorem.ipsum.dolor", 3);

        // Load win settings.
        final TestClass tcw = SettingHandler.load(new TestClass(), ImmutableMap.of("os", "win", "app", "zip"), new IntProvider(
                props));
        assertThat(tcw.bar, is(1));
        assertThat(tcw.foo, is(1));
        assertThat(tcw.lorem, is(3));

        // Load lnx settings.
        final TestClass tcl = SettingHandler.load(new TestClass(), ImmutableMap.of("os", "lnx", "app", "zip"), new IntProvider(
                props));
        assertThat(tcl.bar, is(2));
        assertThat(tcl.foo, is(2));
        assertThat(tcl.lorem, is(3));
        
        tcw.foo = 4; 
        tcw.bar = 5;
        tcw.lorem = 6;
        
        // Persist win settings
        SettingHandler.persist(tcw, ImmutableMap.of("os", "win", "app", "zip"), new IntProvider(
                props));
        
        assertThat(props.get("win.zip.foo"), is(4));
        assertThat(props.get("win.bar"), is(5));
        assertThat(props.get("lorem.ipsum.dolor"), is(6));
        
        tcl.foo = 7;
        tcl.bar = 8;
        tcl.lorem = 9;
        
        // Persist lnx settings
        SettingHandler.persist(tcl, ImmutableMap.of("os", "lnx", "app", "zip"), new IntProvider(
                props));
        
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
