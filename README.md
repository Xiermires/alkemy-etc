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
    private static final String EXAMPLE = "0,1.2,2.3,12345678902,4" + NEW_LINE + "9,1.65,7f,12345678901,5";

    @Test
    public void testCsvReader() throws IOException
    {
        // Simulate the whole csv is a file process (although we only need an Iterator<String>)
        final BufferedReader reader = new BufferedReader(new InputStreamReader(
                new ByteArrayInputStream(EXAMPLE.getBytes("UTF-8"))));

        final CsvReader aev = new CsvReader();

        final List<TestClass> tcs = reader.lines().map(l -> l.split(",")).map(l -> Alkemy.mature(TestClass.class, aev, l))
                .collect(Collectors.toList());

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
    final TypedValueFromStringArray tvfs = new TypedValueFromStringArray();

    @Override
    public void visit(IndexedElement e, Object parent, String[] parameter)
    {
        e.set(tvfs.getValue(e, parameter), parent);
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




