/* Copyright 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.security.spi.ldap;

import junit.framework.TestCase;

import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;
import org.apache.jetspeed.security.spi.CredentialHandler;
import org.apache.jetspeed.security.spi.UserSecurityHandler;
import org.apache.jetspeed.security.spi.impl.LdapCredentialHandler;
import org.apache.jetspeed.security.spi.impl.LdapUserSecurityHandler;
import org.apache.jetspeed.security.spi.impl.ldap.LdapUserCredentialDao;
import org.apache.jetspeed.security.spi.impl.ldap.LdapUserCredentialDaoImpl;
import org.apache.jetspeed.security.spi.impl.ldap.LdapUserSecurityDao;
import org.apache.jetspeed.security.spi.impl.ldap.LdapUserSecurityDaoImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

/**
 * <p>
 * Abstract test case for LDAP providers.
 * </p>
 * 
 * @author <a href="mailto:mike.long@dataline.com">Mike Long </a>
 */
public abstract class AbstractLdapTest extends TestCase
{
    /** The ldap properties. */
    private static Properties props = null;

    /** The {@link UserSecurityHandler}. */
    UserSecurityHandler userHandler;

    /** The {@link CredentialHandler}. */
    CredentialHandler crHandler;

    /** The {@link UserPrincipal}. */
    UserPrincipal prin;
    
    /** Random seed. */
    Random rand = new Random(System.currentTimeMillis());

    /** The test uid. */
    protected String uid;

    /** The test password. */
    protected String password = "fred";

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        initializeConfiguration();
        LdapUserCredentialDao credDao = new LdapUserCredentialDaoImpl(props.getProperty("org.apache.jetspeed.ldap.ldapServerName"),
                props.getProperty("org.apache.jetspeed.ldap.rootDn"), props.getProperty("org.apache.jetspeed.ldap.rootPassword"),
                props.getProperty("org.apache.jetspeed.ldap.rootContext"), props.getProperty("org.apache.jetspeed.ldap.defaultDnSuffix"));
        
        LdapUserSecurityDao userSecDao = new LdapUserSecurityDaoImpl(props.getProperty("org.apache.jetspeed.ldap.ldapServerName"),
                props.getProperty("org.apache.jetspeed.ldap.rootDn"), props.getProperty("org.apache.jetspeed.ldap.rootPassword"),
                props.getProperty("org.apache.jetspeed.ldap.rootContext"), props.getProperty("org.apache.jetspeed.ldap.defaultDnSuffix"));    
        
        userHandler = new LdapUserSecurityHandler(userSecDao);
        crHandler = new LdapCredentialHandler(credDao);
        uid = Integer.toString(rand.nextInt());
        prin = new UserPrincipalImpl(uid);
        userHandler.addUserPrincipal(prin);
        crHandler.setPassword(uid, "", password);
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();

        if (prin != null)
        {
            userHandler.removeUserPrincipal(prin);
        }
    }
    
    /**
     * <p>
     * Init ldap config.
     * </p>
     */
    protected static void initializeConfiguration()
    {
        String testPropsPath = "./etc/ldap.properties";
        try
        {
            File testFile = new File(testPropsPath);
            if (testFile.exists())
            {
                FileInputStream is = new FileInputStream(testPropsPath);
                props = new Properties();
                props.load(is);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}