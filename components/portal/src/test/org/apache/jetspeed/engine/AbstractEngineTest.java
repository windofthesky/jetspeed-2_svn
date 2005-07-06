/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.engine;

import java.io.FileInputStream;

import javax.servlet.ServletConfig;

import junit.framework.TestCase;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalTestConstants;
import org.apache.jetspeed.components.ComponentManagement;
import org.jmock.Mock;

import com.mockrunner.mock.web.MockServletConfig;
import com.mockrunner.mock.web.MockServletContext;

/**
 * <p>
 * AbstractEngineTest
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 */
public abstract class AbstractEngineTest extends TestCase
{

    /**
     * 
     */
    public AbstractEngineTest()
    {
        super();
    }

    /**
     * @param arg0
     */
    public AbstractEngineTest(String arg0)
    {
        super(arg0);
    }

    protected Engine engine;

    protected Object[] keysToCheck;

    public void testEngine() throws Exception
    {
        assertNotNull(engine.getComponentManager());
        assertNotNull(engine.getComponentManager().getRootContainer());
        if (keysToCheck != null)
        {
            verifyComponents(keysToCheck);
        }
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        // need to flag internal JNDI on...
        System.setProperty(AbstractEngine.JNDI_SUPPORT_FLAG_KEY, "true");
        PropertiesConfiguration config = new PropertiesConfiguration();
        config.load(new FileInputStream(PortalTestConstants.JETSPEED_PROPERTIES_PATH));
        Mock servletConfigMock = new Mock(ServletConfig.class);
        MockServletConfig msc = new MockServletConfig();
        msc.setServletContext(new MockServletContext());
        engine = Jetspeed.createEngine(config, PortalTestConstants.PORTAL_WEBAPP_PATH, msc, getEngineClass());

    }

    protected void tearDown() throws Exception
    {

        super.tearDown();
    }

    protected void verifyComponents(Object[] keys)
    {
        ComponentManagement cm = engine.getComponentManager();
        for (int i = 0; i < keys.length; i++)
        {
            assertNotNull("Could not get component insatance " + keys[i], cm.getComponent(keys[i]));
            System.out.println("Load componenet " + cm.getComponent(keys[i]).getClass() + " for key " + keys[i]);
        }
    }

    protected abstract Class getEngineClass();
}
