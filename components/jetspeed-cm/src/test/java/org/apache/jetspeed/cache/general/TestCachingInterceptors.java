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
package org.apache.jetspeed.cache.general;

import org.apache.jetspeed.components.MockComponent;
import org.apache.jetspeed.components.test.AbstractSpringTestCase;

/**
 * <p>
 * TestCachingInterceptors
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class TestCachingInterceptors extends AbstractSpringTestCase
{
       
    
    
    public void testInterceptors() throws Exception
    {
        MockComponent mc = scm.lookupComponent("mockComponent");
        InvocationCountingCache cache = scm.lookupComponent("systemCache");
        assertNotNull(mc);
        assertNotNull(cache);
        
        assertNotNull(mc.getValue("2"));
        assertEquals(1, cache.containsCount);
        assertEquals(0, cache.getCount);
        assertEquals(0, cache.successGetCount);
        assertEquals(1, cache.putCount);
        assertEquals(0, cache.removeCount);
        
        assertNotNull(mc.getValue("2"));
        assertEquals(2, cache.containsCount);
        assertEquals(1, cache.getCount);
        assertEquals(1, cache.successGetCount);
        assertEquals(1, cache.putCount);
        assertEquals(0, cache.removeCount);
        
        mc.setValue("2", "some other value");
        assertEquals(2, cache.containsCount);
        assertEquals(1, cache.getCount);
        assertEquals(1, cache.successGetCount);
        assertEquals(1, cache.putCount);
        assertEquals(1, cache.removeCount);
        
        assertEquals("some other value", mc.getValue("2"));
        assertEquals(3, cache.containsCount);
        assertEquals(1, cache.getCount);
        assertEquals(1, cache.successGetCount);
        assertEquals(2, cache.putCount);
        assertEquals(1, cache.removeCount);
    }
    
    
    protected String[] getConfigurations()
    {
        return new String[] {"org/apache/jetspeed/cache/general/cache-test.xml"};
    }

    protected String getBeanDefinitionFilterCategories()
    {
        return "default";
    }
}
