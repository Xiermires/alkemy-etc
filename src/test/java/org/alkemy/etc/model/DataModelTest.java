package org.alkemy.etc.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.alkemy.etc.model.Person.Gender;
import org.alkemy.util.Nodes.TypedNode;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class DataModelTest
{
    static final Table<Integer, String, Object> people = HashBasedTable.create();
    static final Table<Integer, String, Object> addresses = HashBasedTable.create();
    static final MemoryBasedBookShelves provider = new MemoryBasedBookShelves();

    @BeforeClass
    public static void pre()
    {
        people.put(1, "name", "Oscar");
        people.put(1, "gender", Gender.Male);
        people.put(1, "height", 1.73);
        people.put(1, "weight", 69.3);

        people.put(2, "name", "Stella");
        people.put(2, "gender", Gender.Female);
        people.put(2, "height", 1.65);
        people.put(2, "weight", 57.7);

        provider.bookShelves.put("People", people);

        addresses.put(1, "street", "Foo");
        addresses.put(1, "number", 1);
        addresses.put(1, "main", true);

        addresses.put(2, "street", Arrays.asList("Bar", "Baz"));
        addresses.put(2, "number", Arrays.asList(2, 3));
        addresses.put(2, "main", Arrays.asList(true, false));

        provider.bookShelves.put("Addresses", addresses);
    }

    @Test
    public void testRead()
    {
        final BookShelfHandler<Integer, Person> handler = new BookShelfHandler<>(provider);
        final Person oscar = handler.pickup(TypedNode.create(Person.class), 1);
        final Person stella = handler.pickup(TypedNode.create(Person.class), 2);
        assertThat(oscar.name, is("Oscar"));
        assertThat(oscar.gender, is(Gender.Male));
        assertThat(oscar.height, is(1.73));
        assertThat(oscar.weight, is(69.3));
        assertThat(oscar.addresses.get(0).street, is("Foo"));
        assertThat(oscar.addresses.get(0).number, is(1));
        assertThat(oscar.addresses.get(0).main, is(true));
        assertThat(stella.name, is("Stella"));
        assertThat(stella.gender, is(Gender.Female));
        assertThat(stella.height, is(1.65));
        assertThat(stella.weight, is(57.7));
        assertThat(stella.addresses.get(0).street, is("Bar"));
        assertThat(stella.addresses.get(0).number, is(2));
        assertThat(stella.addresses.get(0).main, is(true));
        assertThat(stella.addresses.get(1).street, is("Baz"));
        assertThat(stella.addresses.get(1).number, is(3));
        assertThat(stella.addresses.get(1).main, is(false));
    }
}
