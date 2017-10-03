package org.alkemy.etc.model;

import java.util.List;

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
   

 
 
 
 

