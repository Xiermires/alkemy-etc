package org.alkemy.common.setting;

public interface Provider
{
    /**
     * Gets a value from the provider.
     */
    Object getValue(String key, Class<?> type);
    
    /**
     * Sets a key-value pair into the provider.
     */
    Object setValue(String key, Object value);
}
