/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.cornerstone.framework.util;

import java.util.Properties;

import org.apache.cornerstone.framework.constant.Constant;

/**
Models a tree using plain old Properties files.  Up to 4 segments of
path can be passed to compose a full path to a leaf node.
*/

public class PropertiesTree extends Properties
{
    public static final String REVISION = "$Revision$";

    public String getProperty(String p1, String p2)
    {
        String fullPath = p1 + Constant.CONF_DELIM + p2;
        return getProperty(fullPath);
    }

    public String getProperty(String p1, String p2, String p3)
    {
        String fullPath = p1 + Constant.CONF_DELIM + p2 + Constant.CONF_DELIM + p3;
        return getProperty(fullPath);
    }

    public String getProperty(String p1, String p2, String p3, String p4)
    {
        String fullPath = p1 + Constant.CONF_DELIM + p2 + Constant.CONF_DELIM + p3 + Constant.CONF_DELIM + p4;
        return getProperty(fullPath);
    }
}