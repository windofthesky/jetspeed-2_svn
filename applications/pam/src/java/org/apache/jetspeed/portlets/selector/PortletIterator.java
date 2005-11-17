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
package org.apache.jetspeed.portlets.selector;

import java.util.List;

import org.apache.jetspeed.portlets.selector.PortletSelector.PortletInfo;
import org.apache.portals.gems.browser.DatabaseBrowserIterator;


/**
 * PortletIterator
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */

public class PortletIterator extends DatabaseBrowserIterator
{
    private static final long serialVersionUID = 1;    
    
    public PortletIterator(List result, List columnTitles,
            List columnTypes, int pageSize)
    {
        super(result, columnTitles, columnTypes, pageSize);
    }

    public int compare(Object obj1, Object obj2)
    {
        PortletInfo info1 = (PortletInfo)obj1;
        PortletInfo info2 = (PortletInfo)obj2;
        String name1 = info1.getDisplayName();
        String name2 = info2.getDisplayName();
        int order = 0;
        
        if (name1 == null)
        {
            if (name2 == null)
                order = 0;
            else
                order = -1;
        }
        else if (name2 == null)
        {
            order = 1;            
        }
        else
        {
            order = name1.compareTo(name2);
        }
        
        if (!getAscendingOrder())
        {
            order = 0 - order;
        }
        return order;
            
    }
    
}
