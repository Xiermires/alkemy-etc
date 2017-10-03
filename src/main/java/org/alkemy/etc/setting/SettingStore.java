/*******************************************************************************
 * Copyright (c) 2017, Xavier Miret Andres <xavier.mires@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package org.alkemy.etc.setting;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import org.alkemy.Alkemy;
import org.alkemy.annotations.AlkemyLeaf;
import org.alkemy.etc.DynamicVariable;
import org.alkemy.parse.impl.AlkemyElement;
import org.alkemy.util.Assertions;
import org.alkemy.util.Node;
import org.alkemy.util.Nodes.TypedNode;
import org.alkemy.util.Pair;
import org.alkemy.util.Traversers.Callback;

public class SettingStore<T>
{
    private final TypedNode<T, SettingElement> root;

    public SettingStore(Class<T> type)
    {
        root = TypedNode.create(Alkemy.parse(type//
                , p -> Setting.class == p.alkemyType() || p.isNode() //
                , e -> new SettingElement(e))//
                , type);

        nodesFirst(root);
    }

    public static <T> T read(Class<T> type, Provider p, Map<String, String> variables)
    {
        return new SettingStore<T>(type).read(p, variables);
    }

    public static <T> T write(Class<T> type, T t, Provider p, Map<String, String> variables)
    {
        return new SettingStore<T>(type).write(t, p, variables);
    }

    // sort to avoid recursion while writing
    private void nodesFirst(Node<SettingElement> r)
    {
        final Comparator<Node<SettingElement>> cmp = (lhs, rhs) ->
        {
            return lhs.hasChildren() ? rhs.hasChildren() ? 0 : -1 : rhs.hasChildren() ? 1 : 0;
        };

        if (r.hasChildren())
        {
            Collections.sort(r.children(), cmp);
        }
        r.children().forEach(e -> nodesFirst(e));
    }

    public T read(Provider p, Map<String, String> variables)
    {
        final SettingReader<T> reader = new SettingReader<T>(p, root, variables);
        root.postorder().forEach(reader);
        return reader.parent;
    }

    public T write(T t, Provider p, Map<String, String> variables)
    {
        final CurrentInstance parent = new CurrentInstance(t);
        final SettingWriter<T> writer = new SettingWriter<T>(parent, p, variables);
        root.preorder(parent).forEach(writer);
        return t;
    }

    public static class SettingReader<T> implements Consumer<SettingElement>
    {
        private final Provider p;
        private final TypedNode<T, SettingElement> r;
        private final Map<String, String> m;
        private List<Pair<SettingElement, Object>> leafs = new ArrayList<>();
        private T parent = null;

        SettingReader(Provider p, TypedNode<T, SettingElement> r, Map<String, String> m)
        {
            this.p = p;
            this.r = r;
            this.m = m;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void accept(SettingElement e)
        {
            if (e.isNode())
            {
                Object node = null;
                for (Pair<SettingElement, Object> pair : leafs)
                {
                    if (pair.second != null)
                    {
                        if (node == null)
                            node = e.newInstance();

                        pair.first.set(pair.second, node);
                    }
                }
                if (r.data() == e && node != null)
                    parent = (T) node;
                else if (r.data() != e)
                {
                    leafs.clear();
                    leafs.add(Pair.create(e, node));
                }
            }
            else
            {
                leafs.add(Pair.create(e, p.getValue(e.getKey(m), e.type())));
            }
        }
    }

    static class CurrentInstance implements Callback<SettingElement>, Supplier<Object>
    {
        Object ref = null;
        final Deque<Object> instances = new ArrayDeque<>();

        CurrentInstance(Object o)
        {
            ref = o;
        }

        @Override
        public boolean onEnterNode(Node<SettingElement> node)
        {
            final Object instance = node.data().get(ref);
            if (instance != null)
            {
                instances.push(ref);
                ref = instance;
            }
            return instance != null;
        }

        @Override
        public boolean onExitNode(Node<SettingElement> node)
        {
            if (!instances.isEmpty())
                ref = instances.pop();
            
            return true;
        }

        @Override
        public Object get()
        {
            return ref;
        }
    }

    public static class SettingWriter<T> implements Consumer<SettingElement>
    {
        private final CurrentInstance ref;
        private final Provider p;
        private final Map<String, String> m;

        SettingWriter(CurrentInstance ref, Provider p, Map<String, String> m)
        {
            this.ref = ref;
            this.p = p;
            this.m = m;
        }

        @Override
        public void accept(SettingElement e)
        {
            if (!e.isNode()) {
                final Object v = e.get(ref.get());
                if (v != null) {
                    p.setValue(e.getKey(m), v);
                }
            }
        }
    }

    public static class SettingElement extends AlkemyElement
    {
        private final static Pattern p = Pattern.compile("\\{&(.+?)\\}");

        final String key;
        final boolean dynamic;

        protected SettingElement(AlkemyElement other)
        {
            super(other);

            if (other.isNode())
            {
                key = null;
                dynamic = false;
            }
            else
            {
                final Setting setting = other.desc().getAnnotation(Setting.class);
                Assertions.nonNull(setting); // shouldn't have been accepted.

                final String value = setting.value();
                key = value.isEmpty() ? other.valueName() : value;
                dynamic = DynamicVariable.isDynamic(key, p);
            }
        }
        
        String getKey(Map<String, String> m) {
            return dynamic ? DynamicVariable.replaceFast(key, m) : key;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD })
    @AlkemyLeaf
    public @interface Setting
    {
        String value() default "";
    }
}
