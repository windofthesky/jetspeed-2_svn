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

import java.sql.Connection;

import javax.naming.NamingException;
import javax.sql.DataSource;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.components.datasource.DBCPDatasourceComponent;
import org.apache.jetspeed.components.datasource.DatasourceComponent;
import org.apache.jetspeed.components.util.DatasourceTestCase;
/**
 * <p>
 * TestJNDIComponent
 * </p>@
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $ $
 *  
 */
public class TestRDBMS extends DatasourceTestCase
{
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestRDBMS.class);
    }
    /**
     * Defines the testcase name for JUnit.
     * 
     * @param name
     *            the testcase's name.
     */
    public TestRDBMS(String name)
    {
        super(name);
    }
    
    public void testDBCP_1() throws Exception
    {
        assertTrue(DatasourceComponent.class.isAssignableFrom(DBCPDatasourceComponent.class));

        assertNotNull(datasourceComponent);
        DataSource ds = datasourceComponent.getDatasource();
        assertNotNull(ds);
        Connection conn = ds.getConnection();
        assertNotNull(conn);
        assertFalse(conn.isClosed());
        conn.close();
        jndiDS.tearDown();
        
        try
        {
        	jndiDS.getJNDI().getRootContext().lookup("java:/jdbc/jetspeed");
            assertNotNull("java:/jdbc/jetspeed was not unbound", null);
        }
        catch (NamingException e)
        {
       
        }
        
    }
    
   

}
