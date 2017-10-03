package org.alkemy.etc.model;

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
