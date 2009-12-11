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

import java.util.Map;

import junit.framework.TestCase;

/**
 * TestHttpUtils
 * 
 * @version $Id$
 */
public class TestHttpUtils extends TestCase
{

    public void testBasicParsing() throws Exception
    {
        String queryString = "a=1&b=2&c=3";
        Map<String, String []> queryParamMap = HttpUtils.parseQueryString(queryString);
        
        assertNotNull(queryParamMap);
        
        assertNotNull(queryParamMap.get("a"));
        assertEquals(1, queryParamMap.get("a").length);
        assertEquals("1", queryParamMap.get("a")[0]);
        
        assertNotNull(queryParamMap.get("b"));
        assertEquals(1, queryParamMap.get("b").length);
        assertEquals("2", queryParamMap.get("b")[0]);
        
        assertNotNull(queryParamMap.get("c"));
        assertEquals(1, queryParamMap.get("c").length);
        assertEquals("3", queryParamMap.get("c")[0]);
    }
    
    public void testMultiValueParsing() throws Exception
    {
        String queryString = "a=1&b=2&c=3&a=11&b=22&a=111";
        Map<String, String []> queryParamMap = HttpUtils.parseQueryString(queryString);
        
        assertNotNull(queryParamMap);
        
        assertNotNull(queryParamMap.get("a"));
        assertEquals(3, queryParamMap.get("a").length);
        assertEquals("1", queryParamMap.get("a")[0]);
        assertEquals("11", queryParamMap.get("a")[1]);
        assertEquals("111", queryParamMap.get("a")[2]);
        
        assertNotNull(queryParamMap.get("b"));
        assertEquals(2, queryParamMap.get("b").length);
        assertEquals("2", queryParamMap.get("b")[0]);
        assertEquals("22", queryParamMap.get("b")[1]);
        
        assertNotNull(queryParamMap.get("c"));
        assertEquals(1, queryParamMap.get("c").length);
        assertEquals("3", queryParamMap.get("c")[0]);
    }
    
    public void testEncodedMultiValueParsing() throws Exception
    {
        String queryString = "param+1=value+1&param%202=value%202";
        Map<String, String []> queryParamMap = HttpUtils.parseQueryString(queryString);
        
        assertNotNull(queryParamMap);
        
        assertNotNull(queryParamMap.get("param 1"));
        assertEquals(1, queryParamMap.get("param 1").length);
        assertEquals("value 1", queryParamMap.get("param 1")[0]);
        
        assertNotNull(queryParamMap.get("param 2"));
        assertEquals(1, queryParamMap.get("param 2").length);
        assertEquals("value 2", queryParamMap.get("param 2")[0]);
    }
}
