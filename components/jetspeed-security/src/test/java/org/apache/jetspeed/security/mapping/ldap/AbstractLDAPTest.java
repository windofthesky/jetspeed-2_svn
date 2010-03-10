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
package org.apache.jetspeed.security.mapping.ldap;

import java.io.File;

import org.apache.directory.server.core.DefaultDirectoryService;
import org.apache.directory.server.core.DirectoryService;
import org.apache.directory.server.core.entry.ServerEntry;
import org.apache.directory.server.core.partition.Partition;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmPartition;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.protocol.shared.store.LdifFileLoader;
import org.apache.directory.server.protocol.shared.transport.TcpTransport;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.jetspeed.security.mapping.ldap.dao.DefaultLDAPEntityManager;
import org.apache.jetspeed.security.mapping.ldap.dao.LDAPEntityDAOConfiguration;
import org.apache.jetspeed.security.mapping.model.impl.AttributeDefImpl;
import org.apache.jetspeed.test.JetspeedTestCase;
import org.springframework.core.io.Resource;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public abstract class AbstractLDAPTest extends JetspeedTestCase
{
    
    public static final AttributeDefImpl CN_DEF = new AttributeDefImpl("cn",false,false).cfgRequired(true).cfgIdAttribute(true);

    public static final AttributeDefImpl SN_DEF = new AttributeDefImpl("sn",false,false).cfgRequired(true).cfgIdAttribute(true);

    public static final AttributeDefImpl UID_DEF = new AttributeDefImpl("uid",false,false).cfgRequired(true).cfgIdAttribute(true);

    public static final AttributeDefImpl GIVEN_NAME_DEF = new AttributeDefImpl(
            "givenName");;

    public static final AttributeDefImpl LAST_NAME_DEF = new AttributeDefImpl(
            "lastname");;

    public static final AttributeDefImpl DESCRIPTION_ATTR_DEF = new AttributeDefImpl(
            "description");

    public static final AttributeDefImpl UNIQUEMEMBER_ATTR_DEF = new AttributeDefImpl(
    "uniqueMember",true).cfgRequired(true).cfgRequiredDefaultValue("uid=someDummyValue");

    protected LdapTemplate ldapTemplate;

    protected ContextSource contextSource;

    protected String baseDN;

    protected DefaultLDAPEntityManager entityManager;

    protected LDAPEntityDAOConfiguration userSearchConfig;

    protected boolean debugMode = false;

    protected BasicTestCases basicTestCases;
    
    /** The directory service */
    private static DirectoryService service;
    private static LdapServer server;
    private static boolean running;
        
    private static boolean deleteDir(File dir)
    {        
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i=0; i < children.length; i++)
            {
                if (!deleteDir(new File(dir, children[i])))
                {
                    return false;
                }
            }
        }
        return dir.delete();
    }
    
    public void ldapTestSetup() throws Exception
    {
        File workingDir = new File(getBaseDir()+"target/_apacheds");
        if (workingDir.exists() && !deleteDir(workingDir))
        {
            throw new Exception("Cannot delete apacheds working Directory: "+workingDir.getAbsolutePath());
        }
        
        // Initialize the LDAP service
        service = new DefaultDirectoryService();
        
        // Disable the ChangeLog system
        service.getChangeLog().setEnabled( false );
        service.setDenormalizeOpAttrsEnabled( true );
        
        // Create a new partition named 'foo'.
        Partition partition = new JdbmPartition();
        partition.setId( "sevenSeas" );
        partition.setSuffix( "o=sevenSeas" );
        service.addPartition( partition );
        
        service.setWorkingDirectory(workingDir);
        server = new LdapServer();
        server.setDirectoryService(service);
        server.setTransports(new  TcpTransport(10389));
        service.startup();
        server.start();
        
        // Inject the sevenSeas root entry if it does not already exist
        if (!service.getAdminSession().exists(partition.getSuffixDn()))
        {
            LdapDN dn = new LdapDN( "o=sevenSeas" );
            ServerEntry entry = service.newEntry( dn );
            entry.add( "objectClass", "top", "domain", "extensibleObject" );
            entry.add( "dc", "sevenSeas" );
            service.getAdminSession().add( entry );
        }
        running = true;
    }
    
    public void ldapTestTeardown() throws Exception
    {
        server.stop();
        service.shutdown();
        server = null;
        service = null;
        File workingDir = new File(getBaseDir()+"target/_apacheds");
        if (workingDir.exists())
        {
            deleteDir(workingDir);
        }
        running = false;
    }

    public void setUp() throws Exception
    {
        super.setUp();
        // TODO : move config to build environment
        baseDN = "o=sevenSeas";
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl("ldap://localhost:10389");
        contextSource.setBase(baseDN);
        contextSource.setUserDn("uid=admin,ou=system");
        contextSource.setPassword("secret");
        contextSource.afterPropertiesSet();
        ldapTemplate = new LdapTemplate();
        ldapTemplate.setContextSource(contextSource);

        if (!running) return;
        
        try
        {
            emptyLDAP();
        } catch (Exception e)
        {
            if (debugMode)
            {
                e.printStackTrace();
            }
        }
        Resource[] ldifs = initializationData();
        for (int i = 0; i < ldifs.length; i++)
        {
            LdifFileLoader loader = new  LdifFileLoader(service.getAdminSession(), ldifs[i].getFile().getAbsolutePath());
            loader.execute();
        }
        
        internalSetUp();

        basicTestCases = new BasicTestCases(entityManager, debugMode);
    }

    private void emptyLDAP() throws Exception
    {
        ldapTemplate.unbind("", true); // recursively delete root node of ldap
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        if (!running) return;
        internaltearDown();
        emptyLDAP();
    }

    public abstract void internalSetUp() throws Exception;

    protected abstract void internaltearDown() throws Exception;

    protected abstract Resource[] initializationData() throws Exception;

}
