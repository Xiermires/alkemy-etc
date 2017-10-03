package org.alkemy.etc.model;

import java.util.List;

public interface Provider<K>
{
    List<Object> get(K key, String bookShelfName, String shelfName);
    
    void put(String bookShelfName, K key, String shelfName, Object value);
}
