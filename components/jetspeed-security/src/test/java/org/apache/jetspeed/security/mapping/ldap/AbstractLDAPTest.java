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

import org.apache.jetspeed.security.EmbeddedApacheDSTestService;
import org.apache.jetspeed.security.mapping.ldap.dao.DefaultLDAPEntityManager;
import org.apache.jetspeed.security.mapping.ldap.dao.LDAPEntityDAOConfiguration;
import org.apache.jetspeed.security.mapping.model.impl.AttributeDefImpl;
import org.apache.jetspeed.test.JetspeedTestCase;
import org.springframework.core.io.Resource;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.pool.factory.MutablePoolingContextSource;
import org.springframework.ldap.pool.factory.PoolingContextSource;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public abstract class AbstractLDAPTest extends JetspeedTestCase
{
    
    public static final AttributeDefImpl CN_DEF = new AttributeDefImpl("cn",false,false).cfgRequired(true).cfgIdAttribute(true);

    public static final AttributeDefImpl SN_DEF = new AttributeDefImpl("sn",false,false).cfgRequired(true).cfgIdAttribute(true);

    public static final AttributeDefImpl UID_DEF = new AttributeDefImpl("uid",false,false).cfgRequired(true).cfgIdAttribute(true);

    public static final AttributeDefImpl GIVEN_NAME_DEF = new AttributeDefImpl("givenName");

    public static final AttributeDefImpl LAST_NAME_DEF = new AttributeDefImpl("lastname");

    public static final AttributeDefImpl DESCRIPTION_ATTR_DEF = new AttributeDefImpl("description");

    public static final AttributeDefImpl UNIQUEMEMBER_ATTR_DEF = new AttributeDefImpl("uniqueMember",true).cfgRequired(true).cfgRequiredDefaultValue("uid=someDummyValue");

    protected LdapTemplate ldapTemplate;

    protected ContextSource contextSource;

    protected String baseDN;

    protected DefaultLDAPEntityManager entityManager;

    protected LDAPEntityDAOConfiguration userSearchConfig;

    protected boolean debugMode = false;

    protected BasicTestCases basicTestCases;
    
    private static EmbeddedApacheDSTestService ldapService;
    
    public AbstractLDAPTest()
    {
        ldapService = new EmbeddedApacheDSTestService(getBaseDN(), getLdapPort(), getWorkingDir());
    }
        
    public void ldapTestSetup() throws Exception
    {
        ldapService.start();
    }
    
    public void ldapTestTeardown() throws Exception
    {
        ldapService.stop();
    }

    public void setUp() throws Exception
    {
        super.setUp();
        // TODO : move config to build environment
        baseDN = getBaseDN();
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl("ldap://localhost:"+Integer.toString(getLdapPort()));
        contextSource.setBase(baseDN);
        contextSource.setUserDn("uid=admin,ou=system");
        contextSource.setPassword("secret");
        contextSource.setPooled(false);
        contextSource.afterPropertiesSet();
        PoolingContextSource pcs = new MutablePoolingContextSource();
        pcs.setContextSource(contextSource);
        
        ldapTemplate = new LdapTemplate(pcs);

        if (!ldapService.isRunning()) return;
        
        Resource[] ldifs = initializationData();
        for (int i = 0; i < ldifs.length; i++)
        {
            ldapService.loadLdif(ldifs[i].getFile());
        }
        
        internalSetUp();

        basicTestCases = new BasicTestCases(entityManager, debugMode);
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        if (!ldapService.isRunning()) return;
        internaltearDown();
        ldapService.revert();
    }
    
    protected String getBaseDN()
    {
        return "o=sevenSeas";
    }
    
    protected int getLdapPort()
    {
        return 10389;
    }
    
    protected File getWorkingDir()
    {
        return new File(getBaseDir()+"target/_apacheds");
    }

    public abstract void internalSetUp() throws Exception;

    protected abstract void internaltearDown() throws Exception;

    protected abstract Resource[] initializationData() throws Exception;

}
