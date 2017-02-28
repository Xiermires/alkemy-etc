# alkemy-commons

Some tools using Alkemy but not suitable for the main lib.

--------
Overview
--------

Applying the Alkemy lib to some cases.


--------
Examples
--------

A couple of examples.

1. SettingStore: One approach to code a setting store.

The test class :

```java
public class TestClass
{
    @Setting("{&os}.{&app}.foo")
    int foo;
    
    @Setting("{&os}.bar")
    int bar;
    
    @Setting("lorem.ipsum.dolor")
    int lorem;
}
```

Test case showing the usage.

```java
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
	
	// Simple provider impl.
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
```

Here the SettingHandler class which contains element visitor, AlkemyLeaf and AlkemyElement.

```java
public class SettingHandler
{
    public static <R> R load(R r, Provider provider)
    {
        return Alkemy.mature(r, new SettingLoader(provider));
    }

    public static <R> R load(R r, Map<String, String> variables, Provider provider)
    {
        return Alkemy.mature(r, variables, new SettingLoader(provider));   
    }

    public static <R> R persist(R r, Provider provider)
    {
        return Alkemy.mature(r, new SettingPersister(provider));
    }

    public static <R> R persist(R r, Map<String, String> variables, Provider provider)
    {
        return Alkemy.mature(r, variables, new SettingPersister(provider));
    }

    static abstract class AbstractSetting implements AlkemyElementVisitor<Map<String, String>, SettingElement>
    {
        protected final Provider provider;

        protected AbstractSetting(Provider provider)
        {
            this.provider = provider;
        }

        @Override
        public SettingElement map(AlkemyElement e)
        {
            return new SettingElement(e);
        }

        @Override
        public boolean accepts(Class<?> type)
        {
            return Setting.class == type;
        }
    }

    static class SettingPersister extends AbstractSetting
    {
        SettingPersister(Provider provider)
        {
            super(provider);
        }

        @Override
        public void visit(SettingElement e, Object parent)
        {
            provider.setValue(e.key, e.get(parent));
        }

        @Override
        public void visit(SettingElement e, Object parent, Map<String, String> variables)
        {
            provider.setValue(DynamicVariable.replaceFast(e.key, variables), e.get(parent));
        }
    }

    static class SettingLoader extends AbstractSetting
    {
        SettingLoader(Provider provider)
        {
            super(provider);
        }

        @Override
        public void visit(SettingElement e, Object parent)
        {
            e.set(provider.getValue(e.key, e.type()), parent);
        }

        @Override
        public void visit(SettingElement e, Object parent, Map<String, String> variables)
        {
            e.set(provider.getValue(DynamicVariable.replaceFast(e.key, variables), e.type()), parent);
        }
    }

    public static class SettingElement extends AbstractAlkemyElement<SettingElement>
    {
        private final static Pattern p = Pattern.compile("\\{&(.+?)\\}");

        final String key;
        final boolean dynamic;

        protected SettingElement(AbstractAlkemyElement<?> other)
        {
            super(other);

            final Setting setting = other.desc().getAnnotation(Setting.class);
            Assertions.nonNull(setting); // shouldn't have been accepted.

            key = setting.value();
            dynamic = DynamicVariable.isDynamic(key, p);
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD })
    @AlkemyLeaf
    public static @interface Setting
    {
        String value();
    }
}
```

2. BitMask: Silly bitmask class to map bitmask operations.

The test class :

```java
public class TestMp3Frame
{
    @Bits(shift = 21, bitCount = 11)
    int framSync;
    
    @Bits(shift = 19, bitCount = 2)
    int version;
    
    @Bits(shift = 17, bitCount = 2)
    int layer;
    
    @Bits(shift = 12, bitCount = 4)
    int bitrate;
    
    @Bits(shift = 10, bitCount = 2)
    int samplerate;
    
    @Bits(shift = 9)
    int padding;
}
```

The test using it. A mp3 frame header parser :

```java    
    @Test
    public void testMp3Frame()
    {
        final long header = BitMask.bytesToLong(new byte[] { -1, -5, -112, 0 });
        final TestMp3Frame frame = Alkemy.mature(TestMp3Frame.class, new LongMaskVisitor<>(), header);

        assertThat(frame.framSync, is(2047));
        assertThat(frame.version, is(3));
        assertThat(frame.layer, is(1));
        assertThat(frame.bitrate, is(9));
        assertThat(frame.samplerate, is(0));
        assertThat(frame.padding, is(0));
    }
```

Finally the visitor class :

```java
public class LongMaskVisitor<R> implements AlkemyElementVisitor<Long, BitMask>
{
    @Override
    public BitMask map(AlkemyElement e)
    {
        return new BitMask(e);
    }

    @Override
    public void visit(BitMask element, Object parent, Long parameter)
    {
        element.set(parameter >>> element.offset & (2 << element.bitCount - 1) -1, parent); 
    }

    @Override
    public boolean accepts(Class<?> type)
    {
        return Bits.class == type;
    }
}
```




