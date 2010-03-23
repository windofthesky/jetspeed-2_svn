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
package org.apache.jetspeed.components.jndi;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.jetspeed.components.datasource.BoundDBCPDatasourceComponent;

/**
 * JetspeedDSTestJNDIComponent
 * <p>
 * Uses TyrexJNDIComponent to define a jetspeed Datasource for testing purposes only.
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class JetspeedTestJNDIComponent
{
    public static final String JNDI_DS_NAME = "jetspeed";
    
    protected BoundDBCPDatasourceComponent datasourceComponent;
    protected JNDIComponent jndi;
    
    public void setup() throws Exception
    {
        jndi = new TyrexJNDIComponent();
        String url = System.getProperty("org.apache.jetspeed.database.url");
        String driver = System.getProperty("org.apache.jetspeed.database.driver");
        String user = System.getProperty("org.apache.jetspeed.database.user");
        String password = System.getProperty("org.apache.jetspeed.database.password");
        datasourceComponent = new BoundDBCPDatasourceComponent(user, password, driver, url, 20, 5000,
                GenericObjectPool.WHEN_EXHAUSTED_GROW, true, JNDI_DS_NAME, jndi);
        datasourceComponent.start();
    }
    
    public void tearDown() throws Exception
    {
        datasourceComponent.stop();
        jndi.unbindFromCurrentThread();
    }
    
    public BoundDBCPDatasourceComponent getDatasourceComponent() {
    	return datasourceComponent;
    }

    public JNDIComponent getJNDI() {
    	return jndi;
    }

}
