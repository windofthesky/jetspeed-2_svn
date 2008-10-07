/*
 * Created on Apr 26, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.components.util;

import org.apache.commons.configuration.Configuration;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver</a>
 *
 */
public class ConfiguredComponentInfo implements ComponentInfo
{
    
    protected static final String IS_SINGLETON_PROP = "is.singleton";
    protected static final String CLASSNAME_PROP = "classname";
    private static final String KEY_AS_CLASS_PROP = "key.as.class";
    protected static final String KEY_PROP = "key";
    private Configuration config;
    private String componentId;

    public ConfiguredComponentInfo(String componentId, Configuration config)
    {
        if(config == null)
        {
            throw new IllegalArgumentException("Configuration for ConfiguredComponentInfo cannot be null.");
        }
        this.config = config;
        this.componentId = componentId;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.util.ComponentInfo#getComponentKey()
     */
    public Object getComponentKey(ClassLoader cl)
    {
        String key = config.getString(KEY_PROP);
        if(key == null)
        {
            key = componentId;
        }
        
        try
        {
            if(config.getBoolean(KEY_AS_CLASS_PROP, false))
            {
                return cl.loadClass(key);
            }
            else
            {
                return key;
            }
        }
        catch (ClassNotFoundException e)
        {            
            throw new IllegalStateException("Could not use key, "+key+", as a Class because that class was not found: "+e.toString());
        }
        
        
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.util.ComponentInfo#getComponentClass()
     */
    public Class getComponentClass(ClassLoader cl) throws ClassNotFoundException
    {        
        String className = config.getString(CLASSNAME_PROP);
        if(className == null)
        {
            throw new IllegalStateException("No classname property was defined for component "+componentId);
        }
        return cl.loadClass(className);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.util.ComponentInfo#isSingleton()
     */
    public boolean isSingleton()
    {
        return config.getBoolean(IS_SINGLETON_PROP, true);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.util.ComponentInfo#getConfiguration()
     */
    public Configuration getConfiguration()
    {
        return config;
    }
}
