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
package org.apache.jetspeed.components.portletregistry;


/**
 * <p>Helper class for the portlet registry.</p>
 * @author <a href="dlestrat@apache.org">David Le Strat</a>
 */
public class PortletRegistryHelper
{

    /**
     * <p>Parses the portlet application name from the portlet
     * unique name.</p>
     * @param uniqueName The portlet unique name.
     */
    public static String parseAppName(String uniqueName)
    {
        int split = splitUniqueName(uniqueName);
        return uniqueName.substring(0, split);
    }

    /**
     * <p>Parses the portlet name from the portlet
     * unique name.</p>
     * @param uniqueName The portlet unique name.
     */
    public static String parsePortletName(String uniqueName)
    {
        int split = splitUniqueName(uniqueName);
        return uniqueName.substring((split + 2), uniqueName.length());
    }

    /**
     * <p>Utility method to split the unique name given the
     * PORTLET_UNIQUE_NAME_SEPARATOR.</p>
     * @param uniqueName
     * @return
     */
    private static int splitUniqueName(String uniqueName)
    {
        int split = 0;
        if (null != uniqueName)
        {
            split = uniqueName.indexOf(PersistenceBrokerPortletRegistry.PORTLET_UNIQUE_NAME_SEPARATOR);
        }
        if (split < 1)
        {
            throw new IllegalArgumentException(
                "The unique portlet name, \""
                    + uniqueName
                    + "\";  is not well formed.  No "
                    + PersistenceBrokerPortletRegistry.PORTLET_UNIQUE_NAME_SEPARATOR
                    + " delimiter was found.");
        }
        return split;
    }

    public static String makeUniqueName(String appName, String portletName)
    {
        return appName + PersistenceBrokerPortletRegistry.PORTLET_UNIQUE_NAME_SEPARATOR + portletName;
    }


}
