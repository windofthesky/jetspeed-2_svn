/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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
