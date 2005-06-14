/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.components.factorybeans;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;

import org.apache.pluto.factory.Factory;
import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * <p>
 * PlutoFactoryFactoryBean
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class PlutoFactoryFactoryBean extends AbstractFactoryBean
{
    
    private String className;
    private Map props;
    private ServletConfig servletConfig;
    
    /**
     * <p>
     * createInstance
     * </p>
     *
     * @see org.springframework.beans.factory.config.AbstractFactoryBean#createInstance()
     * @return
     * @throws java.lang.Exception
     */
    protected Object createInstance() throws Exception
    {        
        Factory factory = (Factory)Thread.currentThread()
            .getContextClassLoader().loadClass(className).newInstance();
        if(props == null)
        {
            props = new HashMap();
        }
        factory.init(servletConfig, props);
        return factory;
    }

    /**
     * <p>
     * getObjectType
     * </p>
     *
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     * @return
     */
    public Class getObjectType()
    {
        return Factory.class;       
    }
    

    

    /**
     * @return Returns the props.
     */
    public Map getProps()
    {
        return props;
    }
    /**
     * @param props The props to set.
     */
    public void setProps( Map props )
    {
        this.props = props;
    }
    /**
     * @return Returns the servletConfig.
     */
    public ServletConfig getServletConfig()
    {
        return servletConfig;
    }
    /**
     * @param servletConfig The servletConfig to set.
     */
    public void setServletConfig( ServletConfig servletConfig )
    {
        this.servletConfig = servletConfig;
    }
    /**
     * @return Returns the className.
     */
    public String getClassName()
    {
        return className;
    }
    /**
     * @param className The className to set.
     */
    public void setClassName( String className )
    {
        this.className = className;
    }
}
