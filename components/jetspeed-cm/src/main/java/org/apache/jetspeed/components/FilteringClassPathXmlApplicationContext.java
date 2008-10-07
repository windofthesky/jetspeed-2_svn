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

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class FilteringClassPathXmlApplicationContext extends ClassPathXmlApplicationContext
{
    private JetspeedBeanDefinitionFilter filter;
    
    public FilteringClassPathXmlApplicationContext(JetspeedBeanDefinitionFilter filter, String[] configLocations, Properties initProperties)
    {
        this(filter, configLocations, initProperties, null);
    }
    
    public FilteringClassPathXmlApplicationContext(JetspeedBeanDefinitionFilter filter, String[] configLocations, Properties initProperties, ApplicationContext parent)
    {
        super(parent);
        if (initProperties != null)
        {
            PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
            ppc.setIgnoreUnresolvablePlaceholders(true);
            ppc.setSystemPropertiesMode(PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_FALLBACK);
            ppc.setProperties(initProperties);
            this.addBeanFactoryPostProcessor(ppc);
        }
        this.setConfigLocations(configLocations);
        this.filter = filter;
    }
    
    protected DefaultListableBeanFactory createBeanFactory()
    {
        return new FilteringListableBeanFactory(filter, getInternalParentBeanFactory());
    }
}
