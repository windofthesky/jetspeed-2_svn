/*
 * Created on Jul 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.engine;

import javax.servlet.ServletConfig;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.aggregator.PageAggregator;
import org.apache.jetspeed.aggregator.PortletAggregator;
import org.apache.jetspeed.aggregator.PortletRenderer;
import org.apache.jetspeed.capabilities.Capabilities;
import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.prefs.PreferencesProvider;
import org.apache.jetspeed.prefs.PropertyManager;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.request.RequestContextComponent;
import org.apache.jetspeed.security.GroupManager;
import org.apache.jetspeed.security.PermissionManager;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.SecurityProvider;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.impl.RdbmsPolicy;
import org.apache.jetspeed.userinfo.UserInfoManager;
import org.apache.pluto.services.information.StaticInformationProvider;

/**
 * <p>
 * TestSpringEngine
 * </p>
 * <p>
 * 
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public class TestSpringEngine extends AbstractEngineTest
{
    public TestSpringEngine()
    {        
        keysToCheck = new Object[] {"IdGenerator", "DecorationLocator", "TemplateLocator", "IdGenerator", "PageFileCache", PageManager.class, 
                                     PortletRegistry.class, PortletEntityAccessComponent.class, "PortalServices",
                                     Profiler.class, Capabilities.class, PropertyManager.class, PreferencesProvider.class, UserManager.class,
                                     GroupManager.class, RoleManager.class, PermissionManager.class, RdbmsPolicy.class, SecurityProvider.class,
                                     UserInfoManager.class, RequestContextComponent.class, PortletWindowAccessor.class,
                                     PortletRenderer.class, PageAggregator.class, PortletAggregator.class, "PAM",
                                     "deploymentManager", "portletFactory", ServletConfig.class, 
                                     StaticInformationProvider.class, "NavigationalStateCodec", "PortalURL", "NavigationalStateComponent"};
    }
    
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestSpringEngine.class);
    }



}
