package org.alkemy.etc.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

// Simple storage for demo purposes.
public class MemoryBasedBookShelves implements Provider<Integer>
{
    final Map<String, Table<Integer, String, Object>> bookShelves = new HashMap<>();

    @Override
    public List<Object> put(String bookShelfName, Integer key, String shelfName, Object value)
    {
        final Table<Integer, String, Object> bookShelf = bookShelves.get(bookShelfName);
        if (bookShelf == null) {
            final HashBasedTable<Integer, String, Object> table = HashBasedTable.create();
            bookShelves.put(bookShelfName, table);
            table.put(key, shelfName, value);
            return null;
        } else {
            return Arrays.asList(bookShelf.put(key, shelfName, value));
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<Object> get(Integer key, String bookShelfName, String shelfName)
    {
        final Object os = bookShelves.get(bookShelfName).get(key, shelfName);
        return os instanceof List ? (List<Object>) os : Arrays.asList(os);
    }
}
