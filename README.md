# alkemy
From Al"ch"emy. "Purify, mature and perfect certain objects."

v0.7 (still dev.)

--------
Overview
--------

Applying the Alkemy lib to some cases.


--------
Examples
--------

A couple of examples.

1. CsvReader: One approach to code a Csv read mapper.

The test class :

```java
public class TestClass
{
    @Index(0)
    int a;
    
    @Index(1)
    double b;
    
    @Index(2)
    float c;
    
    @Index(3)
    long d;
    
    @Index(4)
    int e;
}
```

A test class showing how-to object map csv lines.

```java

    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String EXAMPLE = "0,1.0,2.0,12345678902,4" + NEW_LINE + "9,1.0,7f,12345678901,5";

    @Test
    public void testCsvReader() throws IOException
    {
        // Simulate the whole csv is a file process (although we only need an Iterator<String>)
        final BufferedReader reader = new BufferedReader(new InputStreamReader(
                new ByteArrayInputStream(EXAMPLE.getBytes("UTF-8"))));

        final CsvReader aev = new CsvReader();
        final Consumer<String> before = s -> aev.update(s); // this is called before processing each line

        final Alkemist alkemist = new AlkemistBuilder().visitor(aev).build(AlkemistBuilder.STANDARD_WRITE); 
        final List<TestClass> tcs = new ArrayList<>();
        for (TestClass tc : alkemist.iterable(TestClass.class, before, reader.lines().iterator()))
        {
            tcs.add(tc);
        }
        assertThat(tcs.size(), is(2));
        
        assertThat(tcs.get(0).a, is(0));
        assertThat(tcs.get(0).b, is(1d));
        assertThat(tcs.get(0).c, is(2f));
        assertThat(tcs.get(0).d, is(12345678902l));
        assertThat(tcs.get(0).e, is(4));
        
        assertThat(tcs.get(1).a, is(9));
        assertThat(tcs.get(1).b, is(1d));
        assertThat(tcs.get(1).c, is(7f));
        assertThat(tcs.get(1).d, is(12345678901l));
        assertThat(tcs.get(1).e, is(5));
        
        reader.close();
    }
```

Here the visitor class.

```java

public class CsvReader extends IndexedElementVisitor
{
    String[] line;
    final TypedValueFromString tvfs = new TypedValueFromString(f -> line[f]);
    
    public void update(String line)
    {
        this.line = line.split(",");
    }
    
    @Override
    public void visitArgs(IndexedElement e, Object parent, Object... args)
    {
        e.set(tvfs.getValue(e), parent);
    }
}
```

2. BitMask: Silly bitmask class to map bitmask operations instead of writing the logic every time.

The test class :

```java
public class TestMp3Frame
{
    @Bits(pos = 21, count = 11)
    int framSync;
    
    @Bits(pos = 19, count = 2)
    int version;
    
    @Bits(pos = 17, count = 2)
    int layer;
    
    @Bits(pos = 12, count = 4)
    int bitrate;
    
    @Bits(pos = 10, count = 2)
    int samplerate;
    
    @Bits(pos = 9)
    int padding;
}
```

The test using it. A mp3 frame header parser :

```java    
    @Test
    public void testMp3Frame()
    {
        final Alkemist alkemist = new AlkemistBuilder().visitor(new LongMaskVisitor()).build(AlkemistBuilder.STANDARD_WRITE);
        final TestMp3Frame frame = alkemist.process(TestMp3Frame.class, BitMask.asLong(new byte[] { -1, -5, -112, 0 })); // Valid mp3 header (as int) : -290816

        assertThat(frame.framSync, is(2047));
        assertThat(frame.version, is(3));
        assertThat(frame.layer, is(1));
        assertThat(frame.bitrate, is(9));
        assertThat(frame.samplerate, is(0));
        assertThat(frame.padding, is(0));
    }
```

Finally the visitor class and the bitwise logic :

```java
public class LongMaskVisitor implements AlkemyElementVisitor<BitMask>
{
    @Override
    public BitMask map(AlkemyElement e)
    {
        return new BitMask(e);
    }

    @Override
    public void visitArgs(BitMask element, Object parent, Object... args)
    {
        Assertions.ofSize(args, 1);
        Assertions.ofListedType(args[0], Long.class);
        final Long l = (Long) (args[0]);
        element.set(l >>> element.offset & (2 << element.bitCount - 1) -1, parent); 
    }

    @Override
    public boolean accepts(Class<?> type)
    {
        return Bits.class == type;
    }
}

public class BitMask extends AbstractAlkemyElement<BitMask>
{
    private static long[] lmask = new long[] { 0xFF00000000000000l, //
            0x00FF000000000000l, //
            0x0000FF0000000000l, //
            0x000000FF00000000l, //
            0x00000000FF000000l, //
            0x0000000000FF0000l, //
            0x000000000000FF00l, //
            0x00000000000000FFl };

    static long asLong(Object arg)
    {
        Assertions.ofListedType(arg, byte[].class, Integer.class, Long.class);

        if (arg instanceof byte[])
        {
            final byte[] bb = (byte[]) arg;
            Assertions.lessEqualThan(bb.length, Long.BYTES);
            return shifAndMask(bb);
        }
        else if (arg instanceof Integer)
        {
            return ((Number) arg).longValue();
        }
        else if (arg instanceof Long)
        {
            return (Long) arg;
        }
        throw new RuntimeException("Invalid type"); // should never happen
    }

    private static long shifAndMask(byte[] bytes)
    {
        long l = 0;
        for (int i = bytes.length; i > 0; i--)
        {
            final int shift = 8 * (i - 1);
            final long mask = lmask[8 - i];
            l = l | ((bytes[bytes.length - i] << shift) & mask);
        }
        return l;
    }

    final int offset;
    final int bitCount;

    protected BitMask(AbstractAlkemyElement<?> other)
    {
        super(other);

        final Bits bit = other.desc().getAnnotation(Bits.class);
        Assertions.exists(bit);
        Assertions.greaterEqualThan(bit.pos(), 0);
        Assertions.greaterEqualThan(bit.count(), 0);

        offset = bit.pos();
        bitCount = bit.count();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD })
    @AlkemyLeaf(Bits.class)
    public @interface Bits
    {
        int pos(); // offset

        int count() default 1;
    }
}
```




