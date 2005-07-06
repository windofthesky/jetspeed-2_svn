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
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        windowAccessor = new PortletWindowAccessorImpl(entityAccess, true);
        portletContainer = new JetspeedPortletContainerWrapper(new PortletContainerImpl());
    }
}
