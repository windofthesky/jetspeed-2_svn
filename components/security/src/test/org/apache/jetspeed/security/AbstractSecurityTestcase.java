/*
 * Created on May 27, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.security;

import org.apache.jetspeed.components.persistence.store.util.PersistenceSupportedTestCase;
import org.apache.jetspeed.security.impl.GroupManagerImpl;
import org.apache.jetspeed.security.impl.PermissionManagerImpl;
import org.apache.jetspeed.security.impl.RdbmsPolicy;
import org.apache.jetspeed.security.impl.RoleManagerImpl;
import org.apache.jetspeed.security.impl.SecurityProviderImpl;
import org.apache.jetspeed.security.impl.UserManagerImpl;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver</a>
 *
 */
public class AbstractSecurityTestcase extends PersistenceSupportedTestCase
{

    protected UserManagerImpl ums;
    protected GroupManagerImpl gms;
    protected RoleManagerImpl rms;
    protected PermissionManagerImpl pms;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
      
        super.setUp();
        ums = new UserManagerImpl(persistenceStore);
        gms = new GroupManagerImpl(persistenceStore);
        rms =new RoleManagerImpl(persistenceStore);
        pms = new PermissionManagerImpl(persistenceStore);
        new SecurityProviderImpl("login.conf", new RdbmsPolicy(pms), ums);   
    }
    /**
     * 
     */
    public AbstractSecurityTestcase()
    {
        super();
    }

    /**
     * @param arg0
     */
    public AbstractSecurityTestcase( String arg0 )
    {
        super(arg0);
    }

}
