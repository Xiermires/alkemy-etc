package org.alkemy.common.setting;

import org.alkemy.common.setting.SettingHandler.Setting;

public class TestClass
{
    @Setting("{&os}.{&app}.foo")
    int foo;
    
    @Setting("{&os}.bar")
    int bar;
    
    @Setting("lorem.ipsum.dolor")
    int lorem;
}
