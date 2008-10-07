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

package org.apache.jetspeed.page;

import java.util.Properties;

import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.page.PageSerializer.Result;
import org.apache.jetspeed.tools.ToolsLogger;

/**
 * Standalone execution of JetspeedPageSerializer
 * 
 * @version $Id$
 *
 */
public interface JetspeedPageSerializerApplication
{

    Result importPages(ToolsLogger logger, String applicationRootPath, String categories,
            String filterPropertiesFileName, Properties initProperties, String rootFolder) throws JetspeedException;

    Result exportPages(ToolsLogger logger, String applicationRootPath, String categories,
            String filterPropertiesFileName, Properties initProperties, String rootFolder) throws JetspeedException;

}