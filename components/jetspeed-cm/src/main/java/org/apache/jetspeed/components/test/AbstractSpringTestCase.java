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

import java.util.Properties;

import org.apache.jetspeed.components.JetspeedBeanDefinitionFilter;
import org.apache.jetspeed.components.SpringComponentManager;
import org.apache.jetspeed.test.JetspeedTestCase;

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

    /**
     * setup Spring context as part of test setup
     */
    protected void setUp() throws Exception
    {        
        super.setUp();
        scm = new SpringComponentManager(getBeanDefinitionFilter(), getBootConfigurations(), getConfigurations(), getBaseDir()+"target/test-classes/webapp", getPostProcessProperties(), false);
        scm.start();
    }

    /**
     * close Spring context as part of test teardown
     */
    protected void tearDown() throws Exception
    {        
        scm.stop();
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

    protected Properties getPostProcessProperties()
    {
        return new Properties();
    }
    
    protected JetspeedBeanDefinitionFilter getBeanDefinitionFilter()
    {
        return null;
    }
}
