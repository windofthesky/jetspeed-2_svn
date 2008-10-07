/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.components;

import java.util.Properties;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class FilteringXmlWebApplicationContext extends XmlWebApplicationContext
{
    private JetspeedBeanDefinitionFilter filter;
    
    public FilteringXmlWebApplicationContext(JetspeedBeanDefinitionFilter filter, String[] configLocations, Properties initProperties, ServletContext servletContext)
    {
        this(filter, configLocations, initProperties, servletContext, null);
    }
    
    public FilteringXmlWebApplicationContext(JetspeedBeanDefinitionFilter filter, String[] configLocations, Properties initProperties, ServletContext servletContext, ApplicationContext parent)
    {
        super();
        if (parent != null)
        {
            this.setParent(parent);
        }
        if (initProperties != null)
        {
            PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
            ppc.setIgnoreUnresolvablePlaceholders(true);
            ppc.setSystemPropertiesMode(PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_FALLBACK);
            ppc.setProperties(initProperties);
            addBeanFactoryPostProcessor(ppc);
        }
        setConfigLocations(configLocations);
        setServletContext(servletContext);
        this.filter = filter;
    }
    
    protected DefaultListableBeanFactory createBeanFactory()
    {
        return new FilteringListableBeanFactory(filter, getInternalParentBeanFactory());
    }
}
