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
package org.apache.jetspeed.administration;

import org.apache.jetspeed.om.folder.Folder;

/**
 * Helper for admininstration
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:chris@bluesunrise.com">Chris Schaefer</a>
 * @version $Id: $
 */
public class AdminUtil
{
    static public String concatenatePaths(String base, String path)
    {
        String result = "";
        if (base == null)
        {
            if (path == null)
            {
                return result;
            }
            return path;
        }
        else
        {
            if (path == null)
            {
                return base;
            }
        }
        if (base.endsWith(Folder.PATH_SEPARATOR)) 
        {
            if (path.startsWith(Folder.PATH_SEPARATOR))
            {
                result = base.concat(path.substring(1));
                return result;
            }
        
        }
        else
        {
            if (!path.startsWith(Folder.PATH_SEPARATOR)) 
            {
                result = base.concat(Folder.PATH_SEPARATOR).concat(path);
                return result;
            }
        }
        return base.concat(path);
    }
    
}
