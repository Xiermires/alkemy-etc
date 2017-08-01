package org.alkemy.etc.setting;

import org.alkemy.etc.setting.SettingManager.Setting;

public class TestClass
{
    @Setting("{&os}.{&app}.foo")
    int foo;
    
    @Setting("{&os}.bar")
    int bar;
    
    @Setting("lorem.ipsum.dolor")
    int lorem;
}
