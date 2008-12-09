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

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * Extension of the default/standard Spring BeanFactory (as used by Jetspeed) to allow
 * conditional filtering of BeanDefinition with the (optionally) provided JetspeedBeanDefinitionFilter.
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
@SuppressWarnings("deprecation")
public class FilteringListableBeanFactory extends DefaultListableBeanFactory
{
    private JetspeedBeanDefinitionFilter filter;
    
    public FilteringListableBeanFactory(JetspeedBeanDefinitionFilter filter, BeanFactory parentBeanFactory)
    {
        super(parentBeanFactory);
        this.filter = filter;
        if (this.filter == null)
        {
            this.filter = new JetspeedBeanDefinitionFilter();
        }
        this.filter.init();
    }

    /**
     * Override of the registerBeanDefinition method to optionally filter out a BeanDefinition and
     * if requested dynamically register an bean alias
     */
    public void registerBeanDefinition(String beanName, BeanDefinition bd)
            throws BeanDefinitionStoreException
    {
        if (filter.match(bd))
        {
            super.registerBeanDefinition(beanName, bd);
            if (filter != null)
            {
                filter.registerDynamicAlias(this, beanName, bd);
            }
        }
    }
}
