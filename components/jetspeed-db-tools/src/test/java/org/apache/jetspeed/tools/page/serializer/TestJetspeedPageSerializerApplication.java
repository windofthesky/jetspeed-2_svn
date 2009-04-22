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

package org.apache.jetspeed.tools.page.serializer;

import java.io.File;
import java.util.Properties;

import org.slf4j.LoggerFactory;
import org.apache.jetspeed.components.util.Slf4JToolsLogger;
import org.apache.jetspeed.page.JetspeedPageSerializerApplication;
import org.apache.jetspeed.test.JetspeedTestCase;
import org.apache.jetspeed.tools.ToolsLogger;

/**
 * @version $Id$
 *
 */
public class TestJetspeedPageSerializerApplication extends JetspeedTestCase
{
    private static ToolsLogger logger = new Slf4JToolsLogger(LoggerFactory.getLogger(TestJetspeedPageSerializerApplication.class));
    
    public void testSerializer() throws Exception
    {
        JetspeedPageSerializerApplication app = new JetspeedPageSerializerApplicationImpl();
        String applicationRootPath = getBaseDir()+"target/test-classes/webapp";
        Properties initProperties = new Properties();
        initProperties.put("psml.pages.path",getBaseDir()+"../../applications/jetspeed/src/main/webapp/WEB-INF/pages");
        app.importPages(logger, applicationRootPath, "pageSerializer", applicationRootPath+"/WEB-INF/conf/spring-filter.properties", initProperties, "/");
        File exportPath = new File(applicationRootPath+"/WEB-INF/exportedPages");
        exportPath.mkdirs();
        initProperties.put("psml.pages.path",exportPath.getAbsolutePath());
        app.exportPages(logger, applicationRootPath, "pageSerializer", applicationRootPath+"/WEB-INF/conf/spring-filter.properties", initProperties, "/");
    }
}
