/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.appservers.security.jboss;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.jetspeed.security.UserManager;
import org.apache.ojb.broker.PBKey;
import org.apache.ojb.broker.accesslayer.ConnectionFactoryManagedImpl;
import org.apache.ojb.broker.metadata.ConnectionPoolDescriptor;
import org.apache.ojb.broker.metadata.ConnectionRepository;
import org.apache.ojb.broker.metadata.JdbcConnectionDescriptor;
import org.apache.ojb.broker.metadata.JdbcMetadataUtils;
import org.apache.ojb.broker.metadata.MetadataManager;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;

public class JetspeedSecurityService implements JetspeedSecurityServiceMBean
{

    private final String JCD_ALIAS = "JetspeedSecurityServiceDS";

    private GenericApplicationContext ctx;

    /**
     * Create a new security service. The service's implementation is based on a <a
     * href="www.springframework.org">Spring</a> application that is assembled from the configuration files in
     * <code>META-INF/jboss-secsvc</code>.
     */
    public JetspeedSecurityService()
    {
        // Prepare JCD so that it can be resolved
        JdbcConnectionDescriptor jcd = findJcd();
        if (jcd == null)
        {
            // JCD not found, initialize
            jcd = new JdbcConnectionDescriptor();
            jcd.setJcdAlias(JCD_ALIAS);
            ConnectionPoolDescriptor cpd = new ConnectionPoolDescriptor();
            cpd.setConnectionFactory(ConnectionFactoryManagedImpl.class);
            jcd.setConnectionPoolDescriptor(cpd);
            ConnectionRepository cr = MetadataManager.getInstance().connectionRepository();
            cr.addDescriptor(jcd);
        }
        // Instatiating application
        ctx = new GenericApplicationContext();
        XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ctx);
        xmlReader.loadBeanDefinitions(new ClassPathResource("META-INF/jboss-secsvc/jboss-security-service.xml"));
        ctx.refresh();
    }

    private JdbcConnectionDescriptor findJcd()
    {
        // Try to find JCD
        ConnectionRepository cr = MetadataManager.getInstance().connectionRepository();
        return cr.getDescriptor(new PBKey(JCD_ALIAS));
    }

    /**
     * Set the JNDI name of the <code>DataSource</code> to be used to access the database.
     * 
     * @param jndiName
     */
    public void setDataSourceJndiName(String jndiName)
    {
        JdbcConnectionDescriptor jcd = findJcd();
        try
        {
            Context initialContext = new InitialContext();
            DataSource ds = (DataSource) initialContext.lookup(jndiName);
            (new JdbcMetadataUtils()).fillJCDFromDataSource(jcd, ds, null, null);
        }
        catch (NamingException e)
        {
            throw (IllegalArgumentException) (new IllegalArgumentException("Data source \"" + jndiName
                    + "\" not found in JNDI: " + e.getMessage())).initCause(e);
        }
        jcd.setDatasourceName(jndiName);
    }

    /**
     * Get the JNDI name of the <code>DataSource</code> used to access the database.
     * 
     * @return jndiName
     */
    public String getDataSourceJndiName()
    {
        JdbcConnectionDescriptor jcd = findJcd();
        if (jcd == null)
        {
            return null;
        }
        return jcd.getDatasourceName();
    }

    /**
     * @see JetspeedSecurityServiceMBean#getUserManager()
     */
    public UserManager getUserManager()
    {
        UserManager um = (UserManager) ctx.getBean("org.apache.jetspeed.security.UserManager");
        return um;
    }
}
