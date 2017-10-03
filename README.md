# alkemy-etc

The [alkemy-core](https://github.com/Xiermires/alkemy-core) provides an object mapping framework to allow creating your own mapping and injection strategies, but it doesn't provide any real-use examples. 

This small project aims to exemplify the usage of the framework by creating some injection scenarios. Code supporting these use cases can be found within the repository.

--------
Settings
--------

Let's build a small application to map settings. 

The setting storage is availble through the following interface.

```java
public interface Provider
{
    /**
     * Gets a value from the provider.
     */
    Object getValue(String key, Class<?> type);
    
    /**
     * Sets a value into the provider.
     */
    Object setValue(String key, Object value);
}
```

Our objective is to write classes such as this one...

```
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

and map the settings from/to a provider using some code like the below one.

```java
<T> T read(Provider p, Class<T> type, Map<String, String> args) 
{
    return new SettingStore(p).read(type, args);
}

<T> T write(Provider p, T instance, Map<String, String> args) 
{
    return new SettingStore(p).write(instance, args);
}
```

----------
Meta model
----------

This simple meta model consists of bookshelves, shelves and books. A bookshelf might contain many shelves, and each shelf might contain many books, where each book is a property we want to map.

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
