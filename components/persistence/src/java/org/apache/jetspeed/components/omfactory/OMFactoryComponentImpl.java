/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.components.omfactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.picocontainer.defaults.ConstructorComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;


/**
 * <p>
 * OMFactoryComponentImpl
 * </p>
 * 
 * 
 * @
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $ $
 *
 */
public class OMFactoryComponentImpl extends DefaultPicoContainer implements OMFactory
{
	
	private static final Log log = LogFactory.getLog(OMFactoryComponentImpl.class);

    public OMFactoryComponentImpl(Properties props)
    {
        // We allways want new instance of the object model
        super(new ConstructorComponentAdapterFactory());
        this.props = props;
        this.classMap = new HashMap();
    }

    private Properties props;

    private Map classMap;

    /** 
     * <p>
     * newInstance
     * </p>
     * 
     * @see org.apache.jetspeed.registry.OMFactory#newInstance(java.lang.Class)
     * @param interfase
     * @return
     */
    public Object newInstance(Class interfase) throws InstantiationException, IllegalAccessException, ClassNotFoundException
    {
        return newInstance(interfase.getName());
    }

    /** 
     * <p>
     * newInstance
     * </p>
     * 
     * @see org.apache.jetspeed.registry.OMFactory#newInstance(java.lang.String)
     * @param key
     * @return
     */
    public Object newInstance(String key) throws InstantiationException, IllegalAccessException, ClassNotFoundException
    {
        return getComponentInstance(key);
    }

    /** 
     * <p>
     * getImplementation
     * </p>
     * 
     * @see org.apache.jetspeed.registry.OMFactory#getImplementation(java.lang.Class)
     * @param interfase
     * @return
     */
    public Class getImplementation(Class interfase) throws ClassNotFoundException
    {
        return getImplementation(interfase.getName());
    }

    /** 
     * <p>
     * getImplementation
     * </p>
     * 
     * @see org.apache.jetspeed.registry.OMFactory#getImplementation(java.lang.String)
     * @param key
     * @return
     */
    public Class getImplementation(String key) throws ClassNotFoundException
    {
        if (classMap.containsKey(key))
        {
            return (Class) classMap.get(key);
        }

        String className = props.getProperty(key);

        if (className != null)
        {
            Class clazz = Class.forName(className);
            classMap.put(key, clazz);
            return clazz;
        }
        else
        {
            throw new ClassNotFoundException("There is no class defined for the key/interface: " + key);
        }

    }

    /**
     * @see org.picocontainer.Startable#start()
     */
    public void start()
    {
    	Iterator keys = props.keySet().iterator();
    	while(keys.hasNext())
    	{
    		String key = (String) keys.next();
    		String className = props.getProperty(key);
    		try
            {
                registerComponentImplementation(key, Class.forName(className));
            }            
            catch (ClassNotFoundException e)
            {
                log.error("Unable to load and register class "+className+": "+e.toString(), e);
            }
    	}
        
        super.start();
    }

}
