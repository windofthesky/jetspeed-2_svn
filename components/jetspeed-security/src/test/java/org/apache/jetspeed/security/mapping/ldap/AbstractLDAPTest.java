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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import javax.naming.directory.DirContext;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.apache.directory.server.protocol.shared.store.LdifFileLoader;
import org.springframework.core.io.Resource;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

import org.apache.jetspeed.security.mapping.ldap.dao.DefaultLDAPEntityManager;
import org.apache.jetspeed.security.mapping.ldap.dao.LDAPEntityDAOConfiguration;
import org.apache.jetspeed.security.mapping.model.Attribute;
import org.apache.jetspeed.security.mapping.model.AttributeDef;
import org.apache.jetspeed.security.mapping.model.Entity;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public abstract class AbstractLDAPTest extends TestCase
{

    protected LdapTemplate ldapTemplate;

    protected ContextSource contextSource;

    protected String baseDN;

    protected DefaultLDAPEntityManager entityManager;

    protected LDAPEntityDAOConfiguration userSearchConfig;

    protected boolean debugMode = false;

    protected BasicTestCases basicTestCases;

    public void setUp() throws Exception
    {
        baseDN = "o=sevenSeas";
        // TODO : move config to build environment
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl("ldap://localhost:389");
        contextSource.setBase(baseDN);
        contextSource.setUserDn("cn=admin,o=sevenSeas");
        contextSource.setPassword("secret");
        contextSource.afterPropertiesSet();
        ldapTemplate = new LdapTemplate();
        ldapTemplate.setContextSource(contextSource);

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

        DirContext dirContext = ldapTemplate.getContextSource()
                .getReadWriteContext();
        loadLdifs(ldapTemplate.getContextSource().getReadWriteContext(),
                initializationData());
        internalSetUp();

        basicTestCases = new BasicTestCases(entityManager, debugMode);
    }

    public static void loadLdifs(DirContext context, Resource[] ldifFiles)
            throws IOException
    {

        for (int i = 0; i < ldifFiles.length; i++)
        {
            File tempFile = File.createTempFile("spring_ldap_test", ".ldif");
            try
            {
                InputStream inputStream = ldifFiles[i].getInputStream();
                IOUtils.copy(inputStream, new FileOutputStream(tempFile));
                LdifFileLoader fileLoader = new LdifFileLoader(context,
                        tempFile.getAbsolutePath());
                fileLoader.execute();
            } finally
            {
                try
                {
                    tempFile.delete();
                } catch (Exception e)
                {
                    // Ignore this
                }
            }
        }
    }

    private void emptyLDAP() throws Exception
    {
        ldapTemplate.unbind("", true); // recursively delete root node of ldap
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        internaltearDown();
        emptyLDAP();
    }

    public abstract void internalSetUp() throws Exception;

    protected abstract void internaltearDown() throws Exception;

    protected abstract Resource[] initializationData() throws Exception;

}
