package org.alkemy.etc.setting;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import java.util.regex.Pattern;

import org.alkemy.annotations.AlkemyLeaf;
import org.alkemy.common.Alkemy;
import org.alkemy.common.parse.impl.VisitableAlkemyElement;
import org.alkemy.common.visitor.AlkemyElementVisitor;
import org.alkemy.etc.DynamicVariable;
import org.alkemy.util.Assertions;

public class SettingManager
{
    public static <R> R load(R r, Provider provider)
    {
        return Alkemy.mature(r, new SettingLoader(provider));
    }

    public static <R> R load(R r, Map<String, String> variables, Provider provider)
    {
        return Alkemy.mature(r, variables, new SettingLoader(provider));   
    }

    public static <R> R persist(R r, Provider provider)
    {
        return Alkemy.mature(r, new SettingPersister(provider));
    }

    public static <R> R persist(R r, Map<String, String> variables, Provider provider)
    {
        return Alkemy.mature(r, variables, new SettingPersister(provider));
    }

    static abstract class AbstractSetting implements AlkemyElementVisitor<Map<String, String>, SettingElement>
    {
        protected final Provider provider;

        protected AbstractSetting(Provider provider)
        {
            this.provider = provider;
        }

        @Override
        public SettingElement map(VisitableAlkemyElement e)
        {
            return new SettingElement(e);
        }

        @Override
        public boolean accepts(Class<?> type)
        {
            return Setting.class == type;
        }
    }

    static class SettingPersister extends AbstractSetting
    {
        SettingPersister(Provider provider)
        {
            super(provider);
        }

        @Override
        public void visit(SettingElement e, Object parent)
        {
            provider.setValue(e.key, e.get(parent));
        }

        @Override
        public void visit(SettingElement e, Object parent, Map<String, String> variables)
        {
            provider.setValue(DynamicVariable.replaceFast(e.key, variables), e.get(parent));
        }
    }

    static class SettingLoader extends AbstractSetting
    {
        SettingLoader(Provider provider)
        {
            super(provider);
        }

        @Override
        public void visit(SettingElement e, Object parent)
        {
            e.set(provider.getValue(e.key, e.type()), parent);
        }

        @Override
        public void visit(SettingElement e, Object parent, Map<String, String> variables)
        {
            e.set(provider.getValue(DynamicVariable.replaceFast(e.key, variables), e.type()), parent);
        }
    }

    public static class SettingElement extends VisitableAlkemyElement
    {
        private final static Pattern p = Pattern.compile("\\{&(.+?)\\}");

        final String key;
        final boolean dynamic;

        protected SettingElement(VisitableAlkemyElement other)
        {
            super(other);

            final Setting setting = other.desc().getAnnotation(Setting.class);
            Assertions.nonNull(setting); // shouldn't have been accepted.

            key = setting.value();
            dynamic = DynamicVariable.isDynamic(key, p);
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD })
    @AlkemyLeaf
    public static @interface Setting
    {
        String value();
    }
}
