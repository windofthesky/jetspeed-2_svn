/*
 * Created on May 26, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.components.util;

import org.apache.jetspeed.components.persistence.store.util.PersistenceSupportedTestCase;
import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent;
import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponentImpl;
import org.apache.jetspeed.components.portletregistry.PortletRegistryComponent;
import org.apache.jetspeed.components.portletregistry.PortletRegistryComponentImpl;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver</a>
 *
 */
public class RegistrySupportedTestCase extends PersistenceSupportedTestCase
{

    protected PortletRegistryComponent portletRegistry;
    protected PortletEntityAccessComponent entityAccess;

    /**
     * 
     */
    public RegistrySupportedTestCase()
    {
        super();
    }

    /**
     * @param arg0
     */
    public RegistrySupportedTestCase( String arg0 )
    {
        super(arg0);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {       
        super.setUp();
        portletRegistry = new PortletRegistryComponentImpl(persistenceStore);
        entityAccess = new PortletEntityAccessComponentImpl(persistenceStore);
    }
}
