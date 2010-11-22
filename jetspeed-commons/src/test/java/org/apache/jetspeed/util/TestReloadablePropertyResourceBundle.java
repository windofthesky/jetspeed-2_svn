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
package org.apache.jetspeed.util;

import java.io.IOException;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import junit.framework.TestCase;

/**
 * TestReloadablePropertyResourceBundle
 * 
 * @version $Id$
 */
public class TestReloadablePropertyResourceBundle extends TestCase
{
    private ReloadablePropertyResourceBundle bundle;
    
    @Override
    public void setUp()
    {
        String baseName = TestReloadablePropertyResourceBundle.class.getName();
        ResourceBundle internalBundle = ResourceBundle.getBundle(baseName, new Locale("en","US"));
        assertTrue(internalBundle instanceof PropertyResourceBundle);
        bundle = new ReloadablePropertyResourceBundle((PropertyResourceBundle) internalBundle, baseName);
    }
    
    public void testNormalResourceBundle()
    {
        assertEquals("Hello, World!", bundle.getString("greeting.message"));
    }

    public void testResourceBundle() throws IOException
    {
        bundle.baseName = TestReloadablePropertyResourceBundle.class.getName() + "_modified";
        bundle.reload(getClass().getClassLoader());
        assertEquals("Hello, World! (2)", bundle.getString("greeting.message"));
        bundle.reset();
        assertEquals("Hello, World!", bundle.getString("greeting.message"));
        bundle.reload(getClass().getClassLoader());
        assertEquals("Hello, World! (2)", bundle.getString("greeting.message"));
    }
}
