/*
 * Created on May 26, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed;

import org.apache.jetspeed.components.util.RegistrySupportedTestCase;
import org.apache.jetspeed.container.JetspeedPortletContainerWrapper;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.container.window.impl.PortletWindowAccessorImpl;
import org.apache.pluto.PortletContainer;
import org.apache.pluto.PortletContainerImpl;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver</a>
 *
 */
public class AbstractPortalContainerTestCase extends RegistrySupportedTestCase
{

    protected PortletWindowAccessor windowAccessor;
    protected PortletContainer portletContainer;
    /**
     * 
     */
    public AbstractPortalContainerTestCase()
    {
        super();
    }

    /**
     * @param arg0
     */
    public AbstractPortalContainerTestCase( String arg0 )
    {
        super(arg0);
    }
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        windowAccessor = new PortletWindowAccessorImpl(entityAccess, portletRegistry);
        portletContainer = new JetspeedPortletContainerWrapper(new PortletContainerImpl());
    }
}
