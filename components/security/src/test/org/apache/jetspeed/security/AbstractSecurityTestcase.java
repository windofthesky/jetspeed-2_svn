/*
 * Created on May 27, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.security;

import java.security.Policy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.security.auth.Subject;

import org.apache.jetspeed.components.persistence.store.util.PersistenceSupportedTestCase;
import org.apache.jetspeed.prefs.impl.PreferencesProviderImpl;
import org.apache.jetspeed.security.impl.AuthenticationProviderImpl;
import org.apache.jetspeed.security.impl.GroupManagerImpl;
import org.apache.jetspeed.security.impl.PermissionManagerImpl;
import org.apache.jetspeed.security.impl.RdbmsPolicy;
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

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public class AbstractSecurityTestcase extends PersistenceSupportedTestCase
{
    /** The preferences provider. */
    protected PreferencesProviderImpl prefsProvider;
    
    /** SPI Common Queries. */
    protected CommonQueries cq;
    
    /** SPI Default Credential Handler. */
    protected CredentialHandler ch;
    
    /** SPI Default User Security Handler. */
    protected UserSecurityHandler ush;
    
    /** SPI Default Role Security Handler. */
    protected RoleSecurityHandler rsh;
    
    /** SPI Default Group Security Handler. */
    protected GroupSecurityHandler gsh;
    
    /** SPI Default Security Mapping Handler. */
    protected SecurityMappingHandler smh;
    
    /** The security provider. */
    protected SecurityProvider securityProvider;
    
    /** The user manager. */
    protected UserManager ums;

    /** The group manager. */
    protected GroupManager gms;

    /** The role manager. */
    protected RoleManager rms;

    /** The permission manager. */
    protected PermissionManager pms;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {

        super.setUp();
        
        prefsProvider = new PreferencesProviderImpl(persistenceStore, "org.apache.jetspeed.prefs.impl.PreferencesFactoryImpl", false);
        prefsProvider.start();
        
        cq = new CommonQueries(persistenceStore);
        ch = new DefaultCredentialHandler(cq);
        ush = new DefaultUserSecurityHandler(cq);
        rsh = new DefaultRoleSecurityHandler(cq);
        gsh = new DefaultGroupSecurityHandler(cq);
        smh = new DefaultSecurityMappingHandler(cq);
        pms = new PermissionManagerImpl(persistenceStore);
        Policy policy = new RdbmsPolicy(pms);
        securityProvider = new SecurityProviderImpl(policy, ch, ush, rsh, gsh, smh);
        ums = new UserManagerImpl(securityProvider);
        gms = new GroupManagerImpl(persistenceStore, securityProvider);
        rms = new RoleManagerImpl(persistenceStore, securityProvider);
        
        new AuthenticationProviderImpl("login.conf", ums);
    }

    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public AbstractSecurityTestcase()
    {
        super();
    }

    /**
     * @param arg0
     */
    public AbstractSecurityTestcase(String arg0)
    {
        super(arg0);
    }

    /**
     * Returns subject's principals of type claz
     * 
     * @param subject
     * @param claz
     * @return Returns subject's principals of type claz
     */
    protected Collection getPrincipals(Subject subject, Class claz)
    {
        List principals = new ArrayList();
        for (Iterator iter = subject.getPrincipals().iterator(); iter.hasNext();)
        {
            Object element = iter.next();
            if (claz.isInstance(element))
                principals.add(element);

        }
        return principals;
    }

}