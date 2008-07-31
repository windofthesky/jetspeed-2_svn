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
package org.apache.jetspeed.components.util;

import org.apache.jetspeed.components.jndi.JetspeedTestJNDIComponent;
import org.apache.jetspeed.components.test.AbstractSpringTestCase;

/**
 * <p>
 * DatasourceEnabledSpringTestCase
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public abstract class DatasourceEnabledSpringTestCase extends AbstractSpringTestCase
{
    protected JetspeedTestJNDIComponent jndiDS;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        jndiDS = new JetspeedTestJNDIComponent();
        jndiDS.setup();
        super.setUp();    
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
        jndiDS.tearDown();
    }

    protected String[] getBootConfigurations()
    {
        return new String[]
        { "boot/datasource.xml"};
    }

    protected String getBeanDefinitionFilterCategoryKey()
    {
        return null; // not implemented
    }
    
    protected String getBeanDefinitionFilterCategories()
    {
        return "default,jdbcDS";
    }
}
