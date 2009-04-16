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

package org.apache.jetspeed.components;

import java.util.HashSet;
import java.util.Set;

import org.apache.jetspeed.test.JetspeedTestCase;

public class TestBeanDefinitionMatcher extends JetspeedTestCase
{
    /**
     * Test JetspeedBeanDefinitionFilterMatcher expression matching
     */
    public void testMatcher()
    {
        // construct matcher with categories
        Set<String> testCategories = new HashSet<String>();
        testCategories.add("x");
        testCategories.add("yy");
        testCategories.add("zzz0");
        JetspeedBeanDefinitionFilterMatcher matcher = new JetspeedBeanDefinitionFilterMatcher(testCategories);
        
        // validate matching expressions
        assertTrue(matcher.match("x"));
        assertTrue(matcher.match("zzz0"));
        assertTrue(matcher.match("x and yy"));
        assertTrue(matcher.match("x and yy and not a"));
        assertFalse(matcher.match("a or x and b"));
        assertTrue(matcher.match("(yy)"));
        assertTrue(matcher.match("(((yy)))"));
        assertFalse(matcher.match("(a)"));
        assertTrue(matcher.match("(x or a)"));
        assertTrue(matcher.match("(not a and b or zzz0)"));
        assertTrue(matcher.match("(not a and (b or zzz0))"));
        assertFalse(matcher.match("(not a and (b or c))"));
        assertTrue(matcher.match("(not a and not (b or c))"));
        assertTrue(matcher.match("not (a or (b and x) and zzz0)"));
        assertTrue(matcher.match("x and yy or a and b"));
        assertFalse(matcher.match("x and (a or b or c)"));
        assertTrue(matcher.match("x AND ((a OR b OR c) OR yy)"));
    }
}
