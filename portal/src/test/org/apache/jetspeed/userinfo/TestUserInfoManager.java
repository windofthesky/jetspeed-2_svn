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
package org.apache.jetspeed.userinfo;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.portlet.PortletRequest;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.AbstractPrefsSupportedTestCase;
import org.apache.jetspeed.cache.PortletCache;
import org.apache.jetspeed.factory.JetspeedPortletFactory;
import org.apache.jetspeed.factory.JetspeedPortletFactoryProxy;
import org.apache.jetspeed.mockobjects.request.MockRequestContext;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.prefs.PropertyException;
import org.apache.jetspeed.prefs.om.Property;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.SecurityHelper;
import org.apache.jetspeed.security.SecurityProvider;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.impl.GroupManagerImpl;
import org.apache.jetspeed.security.impl.PermissionManagerImpl;
import org.apache.jetspeed.security.impl.RoleManagerImpl;
import org.apache.jetspeed.security.impl.SecurityProviderImpl;
import org.apache.jetspeed.security.impl.UserManagerImpl;
import org.apache.jetspeed.security.spi.CredentialHandler;
import org.apache.jetspeed.security.spi.GroupSecurityHandler;
import org.apache.jetspeed.security.spi.RoleSecurityHandler;
import org.apache.jetspeed.security.spi.SecurityMappingHandler;
import org.apache.jetspeed.security.spi.UserSecurityHandler;
import org.apache.jetspeed.security.spi.impl.CommonQueries;
import org.apache.jetspeed.security.spi.impl.DefaultCredentialHandler;
import org.apache.jetspeed.security.spi.impl.DefaultGroupSecurityHandler;
import org.apache.jetspeed.security.spi.impl.DefaultRoleSecurityHandler;
import org.apache.jetspeed.security.spi.impl.DefaultSecurityMappingHandler;
import org.apache.jetspeed.security.spi.impl.DefaultUserSecurityHandler;
import org.apache.jetspeed.userinfo.impl.UserInfoManagerImpl;
import org.apache.jetspeed.util.descriptor.ExtendedPortletMetadata;
import org.apache.jetspeed.util.descriptor.PortletApplicationDescriptor;

/**
 * <p>Unit test for {@link UserInfoManager}</p>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class TestUserInfoManager extends AbstractPrefsSupportedTestCase
{

    /** The user info manager. */
    private UserInfoManager uim;

    /** The user manager. */
    private UserManager ums;
    
    private Object gms;

    private Object rms;

    private PermissionManagerImpl pms;



    /**
     * <p>Defines the testcase name for JUnit.</p>
     *
     * @param name the testcase's name.
     */
    public TestUserInfoManager(String name)
    {
        super(name);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() throws Exception
    {
        super.setUp();
        
        PortletCache portletCache = new PortletCache();
        new JetspeedPortletFactoryProxy(new JetspeedPortletFactory(portletCache));
        
        CommonQueries cq = new CommonQueries(persistenceStore);
        CredentialHandler ch = new DefaultCredentialHandler(cq);
        UserSecurityHandler ush = new DefaultUserSecurityHandler(cq);
        RoleSecurityHandler rsh = new DefaultRoleSecurityHandler(cq);
        GroupSecurityHandler gsh = new DefaultGroupSecurityHandler(cq);
        SecurityMappingHandler smh = new DefaultSecurityMappingHandler(cq);
        SecurityProvider securityProvider = new SecurityProviderImpl(ch, ush, rsh, gsh, smh);
        
        ums = new UserManagerImpl(securityProvider);
        gms = new GroupManagerImpl(securityProvider);
        rms = new RoleManagerImpl(securityProvider);
        
        ums = new UserManagerImpl(securityProvider);
        uim = new UserInfoManagerImpl(ums, portletRegistry); 
    
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown() throws Exception
    {
        super.tearDown();
    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestUserInfoManager.class);
    }

    /** Test set user info map. */
    public void testSetUserInfoMap() throws Exception
    {
        PortletApplicationDescriptor pad = new PortletApplicationDescriptor(new FileReader("./test/testdata/deploy/portlet.xml"), "unit-test");
        MutablePortletApplication app = pad.createPortletApplication();            
        assertNotNull("App is null", app);

        // persist the app
        try
        {
            persistenceStore.getTransaction().begin();
            portletRegistry.registerPortletApplication(app);
            persistenceStore.getTransaction().commit();
        }
        catch (Exception e)
        {
            String msg =
                "Unable to register portlet application, " + app.getName() + ", through the portlet portletRegistry: " + e.toString();
            persistenceStore.getTransaction().rollback();
            throw new Exception(msg, e);
        }

        RequestContext request = initRequestContext("anon");

        // Without linked attributes
        // There are no preferences associated to the user profile.
        Map userInfo = uim.getUserInfoMap(app.getId(), request);
        assertNull(PortletRequest.USER_INFO + " is null", userInfo);

        // The user has preferences associated to the user profile.
        initUser();
        request = initRequestContext("test");
        userInfo = uim.getUserInfoMap(app.getId(), request);
        assertNotNull(PortletRequest.USER_INFO + " should not be null", userInfo);
        assertEquals("should contain user.name.given", "Test Dude", (String) userInfo.get("user.name.given"));
        assertEquals("should contain user.name.family", "Dudley", (String) userInfo.get("user.name.family"));
        assertNull("should not contain user.home-info.online.email", userInfo.get("user.home-info.online.email"));
        
        // With linked attributes
        ExtendedPortletMetadata extMetaData = new ExtendedPortletMetadata(new FileReader("./test/testdata/deploy/jetspeed-portlet.xml"), app);
        extMetaData.load();
        
        userInfo = uim.getUserInfoMap(app.getId(), request);
        assertNotNull(PortletRequest.USER_INFO + " should not be null", userInfo);
        assertEquals("should contain user-name-given", "Test Dude", (String) userInfo.get("user-name-given"));
        assertEquals("should contain user-name-family", "Dudley", (String) userInfo.get("user-name-family"));
         
        // remove the app
        try
        {
            persistenceStore.getTransaction().begin();
            portletRegistry.removeApplication(app);
            persistenceStore.getTransaction().commit();
        }
        catch (Exception e)
        {
            String msg =
                "Unable to remove portlet application, " + app.getName() + ", through the portlet portletRegistry: " + e.toString();
            throw new Exception(msg, e);
        }
                
        destroyUser();
    }

    /**
     * <p>Initialize the mock request context.</p>
     * @param username The username.
     * @return The request context.
     */
    private RequestContext initRequestContext(String username)
    {
        RequestContext request = new MockRequestContext("default-other");

        request.setSubject(SecurityHelper.createSubject(username));
        return request;
    }

    /**
     * <p>Init test user.</p>
     */
    private void initUser()
    {
        User user = null;
        try
        {
            ums.addUser("test", "password");
            user = ums.getUser("test");
        }
        catch (SecurityException sex)
        {
            assertTrue("user exists. should not have thrown an exception.", false);
        }
        Preferences userInfoPrefs = user.getPreferences().node("userinfo");
        Map propertyKeys = initPropertyKeysMap();
        try
        {
            propertyManager.addPropertyKeys(userInfoPrefs, propertyKeys);
        }
        catch (PropertyException pex)
        {
            assertTrue("should have add propertyKeys. should not have thrown an exception.", false);
        }
        userInfoPrefs.put("user.name.given", "Test Dude");
        userInfoPrefs.put("user.name.family", "Dudley");
    }

    /**
     * <p>Init property property keys map.</p>
     */
    protected Map initPropertyKeysMap()
    {
        // Build a few property keys.
        Map propertyKeys = new HashMap();
        propertyKeys.put("user.name.given", new Integer(Property.STRING_TYPE));
        propertyKeys.put("user.name.family", new Integer(Property.STRING_TYPE));

        return propertyKeys;
    }

    /**
     * <p>Destroy user test object.</p>
     */
    protected void destroyUser()
    {
        try
        {
            if (ums.userExists("test"))
            {
                ums.removeUser("test");
            }
        }
        catch (SecurityException sex)
        {
            System.out.println("could not remove test users. exception caught: " + sex);
        }
    }

}