/*
 * Copyright 2000-2004 The Apache Software Foundation.
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
package org.apache.jetspeed.components;
import java.sql.Connection;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import junit.framework.Test;

import org.apache.jetspeed.components.datasource.BoundDBCPDatasourceComponent;
import org.apache.jetspeed.components.datasource.DBCPDatasourceComponent;
import org.apache.jetspeed.components.datasource.DatasourceComponent;
import org.apache.jetspeed.components.jndi.JNDIComponent;
import org.picocontainer.MutablePicoContainer;
/**
 * <p>
 * TestJNDIComponent
 * </p>@
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $ $
 *  
 */
public class TestRDBMS extends AbstractComponentAwareTestCase
{
    public static Test suite()
    {
        ComponentAwareTestSuite suite = new ComponentAwareTestSuite(TestRDBMS.class);
        suite.setScript("org/apache/jetspeed/containers/rdbms.container.groovy");
        return suite;
    }
    /**
     * Defines the testcase name for JUnit.
     * 
     * @param name
     *            the testcase's name.
     */
    public TestRDBMS(String name)
    {
        super(name, "./src/test/Log4j.properties");
    }
    
    public void testDBCP_1() throws Exception
    {
        assertTrue(DatasourceComponent.class.isAssignableFrom(DBCPDatasourceComponent.class));
        MutablePicoContainer defaultContainer = getComponentManager().getRootContainer();
        DatasourceComponent dsc = (DatasourceComponent) defaultContainer
        .getComponentInstance(DatasourceComponent.class);
        InitialContext context = new InitialContext();
        //look up from jndi
        assertNotNull(context.lookup("java:comp/env/jdbc/jetspeed"));
        assertNotNull(dsc);
        DataSource ds = dsc.getDatasource();
        assertNotNull(ds);
        Connection conn = ds.getConnection();
        assertNotNull(conn);
        assertFalse(conn.isClosed());
        conn.close();
        ((BoundDBCPDatasourceComponent)dsc).stop();
        JNDIComponent jndi = (JNDIComponent)defaultContainer.getComponentInstance(JNDIComponent.class);
        
        try
        {
            context.lookup("java:comp/env/jdbc/jetspeed");
            assertNotNull("java:comp/env/jdbc/jetspeed was not unbound", null);
        }
        catch (NamingException e)
        {
       
        }
        
 
     
    }
    
   

}
