package org.alkemy.etc.model;

import java.util.ArrayList;
import java.util.List;

import org.alkemy.parse.impl.AlkemyElement;
import org.alkemy.util.AlkemyUtils;
import org.alkemy.util.Node;
import org.alkemy.util.Nodes.TypedNode;

import com.google.common.collect.Iterables;

public class BookShelfHandler<K, V>
{
    private final Provider<K> provider;

    public BookShelfHandler(Provider<K> provider)
    {
        this.provider = provider;
    }

    protected V pick(K key, Class<V> type)
    {
        final TypedNode<V, AlkemyElement> root = TypedNode.create(type);
        final V instance = root.data().newInstance(root.type());
        if (instance != null)
        {
            root.data().set(instance, null);
            final ShelfElement node = new ShelfElement(root.data());
            final ArrayList<ShelfElement> children = new ArrayList<>();
            root.children().forEach(c -> processNode(c, key, instance, children));

            children.forEach(c -> c.set(Iterables.getOnlyElement(provider.get(key, node.bookShelfName, c.shelfName), null),
                    instance));
        }
        return instance;
    }

    private void processNode(Node<? extends AlkemyElement> e, K key, Object parent, List<ShelfElement> children)
    {
        if (e.hasChildren())
        {
            final Object instance = AlkemyUtils.getOrCreateNode(e, parent, true);
            final ShelfElement node = new ShelfElement(e.data());
            final ArrayList<ShelfElement> nodeChildren = new ArrayList<>();
            e.children().forEach(c -> processNode(c, key, instance, nodeChildren));

            if (node.isCollection())
            {
                final List<Object> items = new ArrayList<Object>();
                for (ShelfElement leaf : nodeChildren)
                {
                    final List<Object> values = provider.get(key, node.bookShelfName, leaf.shelfName);
                    for (int i = 0; i < values.size(); i++)
                    {
                        final Object container;
                        if (items.size() > i)
                        {
                            container = items.get(i);
                        }
                        else
                        {
                            container = node.newComponentInstance();
                            items.add(container);
                        }
                        leaf.set(values.get(i), container);
                    }
                }
                node.set(items, parent);
            }
            else
            {
                nodeChildren.forEach(c -> c.set(Iterables
                        .getOnlyElement(provider.get(key, node.bookShelfName, c.shelfName), null), instance));
            }
        }
        else
        {
            children.add(new ShelfElement(e.data()));
        }
    }
}
