/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
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
        confList.add("jetspeed-base.xml");
        confList.add("page-manager.xml");
        confList.add("registry.xml");
        return (String[]) confList.toArray(new String[1]);
    }
}
