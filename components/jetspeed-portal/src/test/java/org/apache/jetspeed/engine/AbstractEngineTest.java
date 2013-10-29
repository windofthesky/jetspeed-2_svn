/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import org.apache.jetspeed.components.ComponentManagement;
import org.apache.jetspeed.test.JetspeedTestCase;
import org.apache.jetspeed.testhelpers.SpringEngineHelper;

import java.util.HashMap;

/**
 * <p>
 * AbstractEngineTest
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 */
public abstract class AbstractEngineTest extends JetspeedTestCase
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

    private SpringEngineHelper engineHelper;

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

    protected void verifyComponents(Object[] keys)
    {
        ComponentManagement cm = engine.getComponentManager();
        for (int i = 0; i < keys.length; i++)
        {
            assertNotNull("Could not get component instance " + keys[i], cm.getComponent(keys[i]));
            System.out.println("Load component " + cm.getComponent(keys[i]).getClass() + " for key " + keys[i]);
        }
    }

}
