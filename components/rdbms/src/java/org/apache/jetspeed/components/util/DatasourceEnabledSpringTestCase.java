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
package org.apache.jetspeed.components.util;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.jetspeed.components.datasource.BoundDBCPDatasourceComponent;
import org.apache.jetspeed.components.jndi.JNDIComponent;
import org.apache.jetspeed.components.jndi.TyrexJNDIComponent;
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

   

    protected BoundDBCPDatasourceComponent datasourceComponent;
    protected JNDIComponent jndi;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        
        jndi = new TyrexJNDIComponent();
        String url = System.getProperty("org.apache.jetspeed.database.url");
        String driver = System.getProperty("org.apache.jetspeed.database.driver");
        String user = System.getProperty("org.apache.jetspeed.database.user");
        String password = System.getProperty("org.apache.jetspeed.database.password");
        datasourceComponent = new BoundDBCPDatasourceComponent(user, password, driver, url, 20, 5000,
                GenericObjectPool.WHEN_EXHAUSTED_GROW, true, "jetspeed", jndi);
        datasourceComponent.start();
        
        super.setUp();    
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        jndi.unbindFromCurrentThread();
        super.tearDown();
    }

    protected String[] getBootConfigurations()
    {
        return new String[]
        { "boot/datasource.xml"};
    }

}
