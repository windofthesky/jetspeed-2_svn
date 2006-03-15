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
package org.apache.jetspeed.components.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import junit.framework.TestCase;

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
public abstract class AbstractSpringTestCase extends TestCase
{
    /**
     * Provides access to the Spring ApplicationContext.
     */
    protected ClassPathXmlApplicationContext ctx;

    /**
     * setup Spring context as part of test setup
     */
    protected void setUp() throws Exception
    {        
        super.setUp();
        if (ctx == null)
        {
            String [] bootConfigurations = getBootConfigurations();
            if (bootConfigurations != null)
            {
                ApplicationContext bootContext = new ClassPathXmlApplicationContext(bootConfigurations, true);
                ctx = new ClassPathXmlApplicationContext(getConfigurations(), true, bootContext);
            }
            else
            {
                ctx = new ClassPathXmlApplicationContext(getConfigurations(), true);
            }
        }
    }

    /**
     * close Spring context as part of test teardown
     */
    protected void tearDown() throws Exception
    {        
        super.tearDown();
        if (ctx != null)
        {
            ctx.close();
        }
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
}
