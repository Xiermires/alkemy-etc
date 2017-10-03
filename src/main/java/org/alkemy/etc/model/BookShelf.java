package org.alkemy.etc.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.alkemy.annotations.AlkemyLeaf;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE })
@AlkemyLeaf(Shelf.class)
public @interface BookShelf
{
    String value() default "";
}
