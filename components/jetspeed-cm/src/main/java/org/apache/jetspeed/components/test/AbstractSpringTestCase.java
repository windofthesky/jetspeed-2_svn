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
package org.apache.jetspeed.components.test;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.components.JetspeedBeanDefinitionFilter;
import org.apache.jetspeed.components.SpringComponentManager;
import org.apache.jetspeed.test.JetspeedTestCase;

import java.io.IOException;
import java.util.Properties;

/**
 * <p>
 * AbstractSpringTestCase
 * </p>
 * <p>
 * 
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public abstract class AbstractSpringTestCase extends JetspeedTestCase
{
    protected SpringComponentManager scm;

    protected final static String[] SUPPORTED_PORTLET_MODES = { "normal", "maximized", "minimized", "solo", "detach", "close" };
    protected final static String[] SUPPORTED_WINDOW_STATES = { "view", "edit", "help", "about", "config", "edit_defaults", "preview", "print", "secure" };

    // override to provide own
    protected String[] getSupportedPortletModes()
    {
    	return SUPPORTED_PORTLET_MODES;
    }
    
    protected String[] getSupportedWindowStates()
    {
    	return SUPPORTED_WINDOW_STATES;
    }
    
    /**
     * setup Spring context as part of test setup
     */
    protected void setUp() throws Exception
    {        
        super.setUp();
        String [] configurations = getConfigurations();
        if ((configurations != null) && (configurations.length > 0))
        {
            scm = new SpringComponentManager(getBeanDefinitionFilter(), getBootConfigurations(), getConfigurations(), getBaseDir() + "target/test-classes/webapp", getInitProperties(), false);
            scm.start();
            new JetspeedActions(getSupportedPortletModes(), getSupportedWindowStates());
        }
    }

    /**
     * close Spring context as part of test teardown
     */
    protected void tearDown() throws Exception
    {
        if (scm != null) {
            scm.stop();
        }
        super.tearDown();
    }

    /**
     * required specification of spring configurations
     */
    protected abstract String[] getConfigurations();
    
    /**
     * optional specification of boot spring configurations
     */
    protected String[] getBootConfigurations()
    {
        return null;
    }

    protected Properties getInitProperties()
    {
        return new Properties();
    }
    
    protected JetspeedBeanDefinitionFilter getBeanDefinitionFilter() throws IOException
    {
        String categories = getBeanDefinitionFilterCategories();
        return ((categories != null) ? new JetspeedBeanDefinitionFilter(categories) : new JetspeedBeanDefinitionFilter());
    }
    
    /**
     * Have your test provide its spring-filter categories with a comma separated list of categories
     * @return a list of one or more filter categories
     */
    protected abstract String getBeanDefinitionFilterCategories();

    /**
     * Have your test provide its spring-filter categories with a single category key. Keys are usually configured 
     * in the spring-filter.properties file as a list of one or more categories.
     * 
     * @return a single category key string
     */    
    protected String getBeanDefinitionFilterCategoryKey()
    {
        return null;
    }
    
}
