# alkemy-etc

The [alkemy-core](https://github.com/Xiermires/alkemy-core) provides an object mapping framework to allow creating your own mapping and injection strategies, but it doesn't provide any real-use examples. 

This small project aims to exemplify the usage of the framework by creating some injection scenarios. Code supporting these use cases can be found within the repository.

--------
Settings
--------

Let's build a small application to map settings. If you have lots of settings they are usually a pain because they are "typeless" and require type conversion between the "unknown" received, and the desired type. 

Let's imagine that we can access our settings through the following provider, what is behind is unimportant, it can be a properties files, system variables, JPA entities, key-value stores or whatever comes to mind.

```java
public interface Provider
{
    /**
     * Gets a value from the provider.
     */
    Object getValue(String key);
    
    /**
     * Sets a value into the provider.
     */
    Object setValue(String key, Object value);
}
```

Additionally we have the following classes which measure the temperature of some device and alert a recipient whenever the max allowed temperature has been reached.

```java
public class TemperatureChecker 
{
     private static final String MAX_ALLOWED_IN_CENTIGRADS_KEY = "maxTemperatureCentigrads";
     private static final float MAX_ALLOWED_IN_CENTIGRADS = -10f;
     
     private static final String DEFAULT_RECIPIENT_KEY = "recipient";
     private static final String DEFAULT_RECIPIENT = "all.staff@xxx.xx";

     float max = MAX_ALLOWED_IN_CENTIGRADS; 
     
     String recipient = DEFAULT_RECIPIENT;
}
```

Now we can code something in this direction.

```java
public class TemperatureChecker 
{
     // ...
     
     public TemperatureChecker(Provider provider) 
     {
         final Object _max = provider.get(MAX_ALLOWED_IN_CENTIGRADS_KEY);
         if (_max instanceof Float)
             max = ((Float) _max).floatValue();
         
         final Object _recipient = provider.get(DEFAULT_RECIPIENT_KEY);
         if (_recipient instanceof String)
             recipient = (String) _recipient;
     }
}
```

We can improve the code by writing some utility class with a conversor like...

```java
    public static <T> T convert(Object raw, T defau1t, Class<T> dest) 
    {
        if (dest.instanceOf(raw))
            return (T) raw;
        else
            return defau1t;
    }
```

and end up with...

```java
public class TemperatureChecker 
{
     // ...
     
     public TemperatureChecker(Provider provider) 
     {
         max = convert(provider.get(MAX_ALLOWED_IN_CENTIGRADS_KEY), MAX_ALLOWED_IN_CENTIGRADS, Float.class); 
         recipient = convert(provider.get(DEFAULT_RECIPIENT_KEY), DEFAULT_RECIPIENT, String.class);
     }
}
```

since we are not using the constants are private and we used them once, we remove them for a resulting class. 

```java
public class TemperatureChecker 
{
     float max = -10f; 
     String recipient = "all.staff@xxx.xx";
     
     public TemperatureChecker(Provider provider) 
     {
         max = convert(provider.get("maxTemperatureCentigrads"), max, Float.class); 
         recipient = convert(provider.get("recipient"), recipient, String.class);
     }
}
```

but we still need to apply the conversions every time we add a new setting.

Alternatively let's look at the following class.

```java
public class TemperatureChecker
{
    @Setting("maxTemperatureCentigrads")
    float max = -10f;
    
    @Setting("recipient")
    String recipient = "all.staff@xxx.xx";
    
    public TemperatureChecker(Provider provider) 
    {
        new SettingsStore(p).read(this); // read from provider
    }
}
```

No matter how many new settings we add, the type, default value and the setting key are all part of the variable declaration and the SettingsStore class will parse it, request the provider for the key and parse the value if possible, or ignore it if invalid.

Additionally, we can add some extra functionality to our provider so that it can work with parameterized keys. For instance the following class supports parameterized settings.

```java
public class ApplicationConfiguration
{
    @Setting("{&os}.{&app}.foo")
    int foo;
    
    @Setting("{&os}.bar")
    String bar;
    
    OtherSettings otherSettings;
}

public class OtherSettings
{
    @Setting("lorem.ipsum.dolor")
    float lorem;
}
```
In this case, we need { key : value } structures to map the parameterized keys into the runtime desired value and map the settings from/to a provider using some code like the below one.

```java
<T> T read(Provider p, Class<T> type, Map<String, String> args) 
{
    return new SettingStore(p).read(type, args);
}

<T> void write(Provider p, T instance, Map<String, String> args) 
{
    new SettingStore(p).write(instance, args);
}
```

----------
Meta model
----------

This simple meta model consists of bookshelves, shelves and books. A bookshelf might contain many shelves, and each shelf might contain many books, where each book is basically anything we want to store.

There is also a provider.

```java
public interface Provider<K>
{
    // get all books identified by key from the shelf
    List<Object> get(K key, String bookShelfName, String shelfName);
    
    // put a book identified by key in the shelf
    void put(String bookShelfName, K key, String shelfName, Object value);
}
```

Again the objective is to write classes similar to the ones below.

```java
@BookShelf("People")
public class Person
{
    public enum Gender { Female, Male };
    
    @Shelf
    String name;
    
    @Shelf
    Gender gender = Gender.Female;
    
    @Shelf
    double height;
    
    @Shelf
    double weight;
    
    List<Address> addresses;
}

@BookShelf("Addresses")
public class Address
{
    @Shelf
    String street;
    
    @Shelf
    int number;
    
    @Shelf
    boolean main;
}
```

and map them using something similar to.

```java
<K, T> T pickup(Provider p, K key, Class<T> type) 
{
    return new BookShelfHandler(p).pickup(key, type);
}

<K, T> void putback(Provider p, K key, T instance) 
{
    new BookShelfHandler(p).putback(key, instance);
}
