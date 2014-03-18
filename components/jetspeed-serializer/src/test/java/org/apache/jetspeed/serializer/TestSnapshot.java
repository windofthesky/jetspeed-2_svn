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
package org.apache.jetspeed.serializer;

import org.apache.jetspeed.serializer.objects.JSSnapshot;
import org.apache.jetspeed.test.JetspeedTestCase;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TestSnapshot extends JetspeedTestCase
{
    protected void setUp() throws Exception
    {
        super.setUp();
    }
    
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }
    
    public void testSnapshot() throws Exception
    {
    	List<JetspeedComponentSerializer> serializers = new LinkedList<JetspeedComponentSerializer>();
    	Map<String,Object> settings = new HashMap<String,Object>();
    	JetspeedSerializerImpl serializer = new JetspeedSerializerImpl(serializers, settings);    
    	JSSnapshot snapshot = serializer.readSnapshot("j2-seed.xml", JetspeedSerializer.DEFAULT_TAG_SNAPSHOT_NAME);
    	assertNotNull(snapshot);
    }
}
