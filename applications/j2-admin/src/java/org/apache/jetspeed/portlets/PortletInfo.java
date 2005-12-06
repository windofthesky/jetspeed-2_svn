/* Copyright 2004 Apache Software Foundation
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.apache.jetspeed.portlets;

/**
 * Portlet Info
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class PortletInfo 
{
    /**
     * 
     */
    String name;
    String displayName;
    String description;
    
    public PortletInfo(String name, String displayName, String description)
    {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
    }
    /**
     * @return Returns the description.
     */
    public String getDescription()
    {
        return description;
    }
    /**
     * @return Returns the displayName.
     */
    public String getDisplayName()
    {
        return displayName;
    }
    /**
     * @return Returns the name.
     */
    public String getName()
    {
        return name;
    }
}