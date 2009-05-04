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
 * Created on Jul 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.engine;

import java.util.HashMap;

import javax.servlet.ServletConfig;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.aggregator.PageAggregator;
import org.apache.jetspeed.aggregator.PortletAggregator;
import org.apache.jetspeed.aggregator.PortletRenderer;
import org.apache.jetspeed.capabilities.Capabilities;
import org.apache.jetspeed.components.ComponentManagement;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.request.RequestContextComponent;
import org.apache.jetspeed.security.GroupManager;
import org.apache.jetspeed.security.PermissionManager;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.impl.RdbmsPolicy;
import org.apache.jetspeed.test.JetspeedTestCase;
import org.apache.jetspeed.testhelpers.SpringEngineHelper;
import org.apache.jetspeed.userinfo.UserInfoManager;

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
public class TestSpringEngine extends JetspeedTestCase
{
    private Engine engine;
    private SpringEngineHelper engineHelper;
    
    
    protected void setUp() throws Exception
    {
        super.setUp();
       HashMap context = new HashMap();
       engineHelper = new SpringEngineHelper(context);
       engineHelper.setUp(getBaseDir());
       engine = (Engine) context.get(SpringEngineHelper.ENGINE_ATTR);
    }

    protected void tearDown() throws Exception
    {
        engineHelper.tearDown();
        super.tearDown();        
    }

    public void testSomeEngine() throws Exception
    {
    }

}
