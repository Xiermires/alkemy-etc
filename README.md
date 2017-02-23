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

        final AlkemyPreorderReader<TestClass, String> anv = new AlkemyPreorderReader<>(true, true, false);
        final Node<? extends AbstractAlkemyElement<?>> node = Alkemy.nodes().get(TestClass.class);
        final CsvReader aev = new CsvReader();
        
        final List<TestClass> tcs = new ArrayList<>();
        for (Entry<TestClass, String> entry : anv.iterable(aev, node, reader.lines().iterator(), TestClass.class))
        {
            tcs.add(entry.result());
            aev.update(entry.peekNext());
        }

        assertThat(tcs.size(), is(2));

        assertThat(tcs.get(0).a, is(0));
        assertThat(tcs.get(0).b, is(1.2d));
        assertThat(tcs.get(0).c, is(2.3f));
        assertThat(tcs.get(0).d, is(12345678902l));
        assertThat(tcs.get(0).e, is(4));

        assertThat(tcs.get(1).a, is(9));
        assertThat(tcs.get(1).b, is(1.65d));
        assertThat(tcs.get(1).c, is(7f));
        assertThat(tcs.get(1).d, is(12345678901l));
        assertThat(tcs.get(1).e, is(5));

        reader.close();
    }
```

Here the visitor class.

```java
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
```

2. BitMask: Silly bitmask class to map bitmask operations instead of writing the logic every time.

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
        final LongMaskVisitor<Long> aev = new LongMaskVisitor<>();
        final AlkemyPreorderReader<TestMp3Frame, Long> anv = new AlkemyPreorderReader<>(true, true, false);
        final TestMp3Frame frame = anv.accept(aev, Alkemy.nodes().get(TestMp3Frame.class), BitMask.bytesToLong(new byte[] { -1, -5, -112, 0 }), TestMp3Frame.class);

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
        Assertions.greaterEqualThan(bit.bitCount(), 0);

        offset = bit.pos();
        bitCount = bit.bitCount();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD })
    @AlkemyLeaf(Bits.class)
    public @interface Bits
    {
        int shift(); 

        int bitCount() default 1;
    }
}
```




