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

package org.apache.jetspeed.tools.db.serializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.test.JetspeedTestCase;

/**
 * @version $Id$
 *
 */
public class TestJetspeedSerializerApplication extends JetspeedTestCase
{
    private static Logger logger = LoggerFactory.getLogger(TestJetspeedSerializerApplication.class);
    
    public void testSerializer() throws Exception
    {
        JetspeedSerializerApplicationImpl app = new JetspeedSerializerApplicationImpl();
        app.importFiles(logger, getBaseDir()+"target/test-classes/webapp", "serializer", getBaseDir()+"target/test-classes/webapp/WEB-INF/conf/spring-filter.properties", null, new String[]{getBaseDir()+"target/test-classes/seed/j2-seed.xml"});
        app.export(logger, getBaseDir()+"target/test-classes/webapp", "serializer", getBaseDir()+"target/test-classes/webapp/WEB-INF/conf/spring-filter.properties", null, getBaseDir()+"target/test-classes/seed/j2-seed-export.xml",null);
    }
}
