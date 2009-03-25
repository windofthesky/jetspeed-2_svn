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

package org.apache.jetspeed.container.state.impl;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @version $Id$
 *
 */
public class ValuesAndWindowUsage implements Serializable
{
    private static final long serialVersionUID = -888938963573388334L;
    
    private String[] values;
    private Set<String> windowIds;
    private Set<String> pageIds;
    
    public ValuesAndWindowUsage(String[] values)
    {
        this.values = values;
    }
    
    public void setValues(String[] values)
    {
        this.values = values;
        this.windowIds = null;
        this.pageIds = null;
    }
    
    public String[] getValues()
    {
        return values;
    }        
    
    public void registerWindowUsage(String pageId, String windowId)
    {
        if (windowIds == null)
        {
            windowIds = new HashSet<String>();
            pageIds = new HashSet<String>();
        }
        windowIds.add(windowId);
        pageIds.add(pageId);
    }

    public Set<String> getWindowIds()
    {
        return windowIds;
    }

    public Set<String> getPageIds()
    {
        return pageIds;
    }
}
