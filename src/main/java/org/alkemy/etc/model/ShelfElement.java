package org.alkemy.etc.model;

import org.alkemy.parse.impl.AlkemyElement;

public class ShelfElement extends AlkemyElement
{
    final String shelfName;
    final String bookShelfName;

    protected ShelfElement(AlkemyElement other)
    {
        super(other);
        final Shelf shelf = other.desc().getAnnotation(Shelf.class);
        final String _shelfName = shelf == null ? "" : shelf.value();
        shelfName = _shelfName.isEmpty() ? other.desc().getName() : _shelfName;

        if (other.isNode())
        {
            BookShelf bookShelf = null;
            if (other.isCollection())
            {
                bookShelf = other.desc().getAnnotation(BookShelf.class);
                if (bookShelf == null)
                {
                    final Class<?> componentType = other.componentType();
                    if (componentType != null)
                        bookShelf = componentType.getAnnotation(BookShelf.class);
                }
            }
            else
            {
                bookShelf = other.desc().getAnnotation(BookShelf.class);
            }

            bookShelfName = bookShelf != null ? bookShelf.value() : null;
            if (bookShelfName == null)
                throw new DataModelSyntax("No bookshelf definition found for '%s'.", other.desc().getName());
        }
        else
        {
            bookShelfName = null;
        }
    }
}
