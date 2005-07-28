/*
 * Created on May 26, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.components.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.prefs.util.test.AbstractPrefsSupportedTestCase;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver</a>
 *
 */
public abstract class RegistrySupportedTestCase extends AbstractPrefsSupportedTestCase
{

    protected PortletRegistry portletRegistry;
    protected PortletEntityAccessComponent entityAccess;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {       
        super.setUp();
        portletRegistry = (PortletRegistry) ctx.getBean("portletRegistry");
        entityAccess = (PortletEntityAccessComponent) ctx.getBean("portletEntityAccess");
    }   
    
    protected String[] getConfigurations()
    {
        String[] confs = super.getConfigurations();
        List confList = new ArrayList(Arrays.asList(confs));
        confList.add("registry.xml");
        return (String[]) confList.toArray(new String[1]);
    }
}
