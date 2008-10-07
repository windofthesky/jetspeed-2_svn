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
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Configures and adds a Spring PropertyPlaceholderConfigurer to an ConfigurableApplicationContext
 * for replacing specific property placeholders at initialization time.
 * <p>
 * The PropertyPlaceholderConfigurer will be setup to ignore unresolved property placeholders
 * and to <emp>never</emp> fallback to System properties if a property is unresolved.
 * </p>
 * <p>
 * The PropertyPlaceholderConfigurer is then added to the provided ConfigurableApplicationContext
 * using {@link ConfigurableApplicationContext#addBeanFactoryPostProcessor}.
 * <p/>
 * <p>
 * This should be done before the context is refreshed for the first time.
 * </p>
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class ApplicationContextBootPropertiesConfigurer
{
    public static void init(ConfigurableApplicationContext context, Properties bootProperties)
    {
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        ppc.setIgnoreUnresolvablePlaceholders(true);
        ppc.setSystemPropertiesMode(PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_NEVER);
        ppc.setProperties(bootProperties);
        context.addBeanFactoryPostProcessor(ppc);
    }
}
