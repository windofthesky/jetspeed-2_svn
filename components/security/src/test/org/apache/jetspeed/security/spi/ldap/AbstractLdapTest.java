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

import org.apache.jetspeed.security.GroupPrincipal;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.impl.GroupPrincipalImpl;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;
import org.apache.jetspeed.security.spi.CredentialHandler;
import org.apache.jetspeed.security.spi.GroupSecurityHandler;
import org.apache.jetspeed.security.spi.UserSecurityHandler;
import org.apache.jetspeed.security.spi.impl.LdapCredentialHandler;
import org.apache.jetspeed.security.spi.impl.LdapGroupSecurityHandler;
import org.apache.jetspeed.security.spi.impl.LdapUserSecurityHandler;
import org.apache.jetspeed.security.spi.impl.ldap.LdapPrincipalDao;
import org.apache.jetspeed.security.spi.impl.ldap.LdapUserCredentialDao;
import org.apache.jetspeed.security.spi.impl.ldap.LdapUserCredentialDaoImpl;
import org.apache.jetspeed.security.spi.impl.ldap.LdapUserPrincipalDaoImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

import javax.naming.NamingException;

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

    /** The {@link GroupSecurityHandler}. */
    GroupSecurityHandler grHandler;

    /** Random seed. */
    Random rand = new Random(System.currentTimeMillis());

    /** Group principal.*/
    GroupPrincipal gp1;

    /** Group principal.*/
    GroupPrincipal gp2;

    /** User principal.*/
    UserPrincipal up1;

    /** User principal.*/
    UserPrincipal up2;

    /** Group uid.*/
    protected String gpUid1;

    /** Group uid.*/
    protected String gpUid2;

    /** User uid.*/
    protected String uid1;

    /** User uid.*/
    protected String uid2;

    /** The test password. */
    protected String password = "fred";

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        initializeConfiguration();
        LdapUserCredentialDao credDao = new LdapUserCredentialDaoImpl(props
                .getProperty("org.apache.jetspeed.ldap.ldapServerName"), props
                .getProperty("org.apache.jetspeed.ldap.rootDn"), props
                .getProperty("org.apache.jetspeed.ldap.rootPassword"), props
                .getProperty("org.apache.jetspeed.ldap.rootContext"), props
                .getProperty("org.apache.jetspeed.ldap.defaultDnSuffix"));

        LdapPrincipalDao userPrincDao = new LdapUserPrincipalDaoImpl(props
                .getProperty("org.apache.jetspeed.ldap.ldapServerName"), props
                .getProperty("org.apache.jetspeed.ldap.rootDn"), props
                .getProperty("org.apache.jetspeed.ldap.rootPassword"), props
                .getProperty("org.apache.jetspeed.ldap.rootContext"), props
                .getProperty("org.apache.jetspeed.ldap.defaultDnSuffix"));

        userHandler = new LdapUserSecurityHandler(userPrincDao);
        crHandler = new LdapCredentialHandler(credDao);
        uid1 = Integer.toString(rand.nextInt());
        uid2 = Integer.toString(rand.nextInt());
        up1 = new UserPrincipalImpl(uid1);
        userHandler.addUserPrincipal(up1);
        crHandler.setPassword(uid1, "", password);
        up2 = new UserPrincipalImpl(uid2);
        userHandler.addUserPrincipal(up2);
        crHandler.setPassword(uid2, "", password);
        createGroupPrincipals();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();

        if (up1 != null)
        {
            userHandler.removeUserPrincipal(up1);
        }
        if (up2 != null)
        {
            userHandler.removeUserPrincipal(up2);
        }
        if (gp1 != null)
        {
            grHandler.removeGroupPrincipal(gp1);
        }
        if (gp2 != null)
        {
            grHandler.removeGroupPrincipal(gp2);
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

    /**
     * @throws NamingException A {@link NamingException}.
     * @throws SecurityException A {@link SecurityException}.
     */
    private void createGroupPrincipals() throws SecurityException, NamingException
    {
        grHandler = new LdapGroupSecurityHandler();
        gpUid1 = Integer.toString(rand.nextInt());
        gp1 = new GroupPrincipalImpl(gpUid1);
        grHandler.setGroupPrincipal(gp1);

        gpUid2 = Integer.toString(rand.nextInt());
        gp2 = new GroupPrincipalImpl(gpUid2);
        grHandler.setGroupPrincipal(gp2);
    }
}