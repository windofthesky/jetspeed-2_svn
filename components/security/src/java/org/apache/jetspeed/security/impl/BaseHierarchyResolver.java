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
package org.apache.jetspeed.security.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.util.ArgUtil;

/**
 * <p>
 * Base implementation for the hierarchy resolver.
 * <p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class BaseHierarchyResolver
{
    /** The logger. */
    private static final Log log = LogFactory.getLog(BaseHierarchyResolver.class);
    
    /**
     * @see org.apache.jetspeed.security.HierarchyResolver#resolveChildren(java.util.prefs.Preferences)
     */
    public String[] resolveChildren(Preferences prefs)
    {
        ArgUtil.notNull(new Object[] { prefs }, new String[] { "preferences" }, "resolveChildren(java.util.prefs.Preferences)");

        List children = new ArrayList();
        processPreferences(prefs, children);
        return (String[]) children.toArray(new String[0]);
    }
    
    /**
     * <p>
     * Recursively processes the preferences.
     * </p>
     * 
     * @param prefs The preferences.
     * @param list The list to add the preferences to.
     */
    protected void processPreferences(Preferences prefs, List list)
    {
        if (!list.contains(prefs.absolutePath()))
        {
            list.add(prefs.absolutePath());
        }
        try
        {
            String[] names = prefs.childrenNames();
            for (int i = 0; i < names.length; i++)
            {
                processPreferences(prefs.node(names[i]), list);
            }
        }
        catch (BackingStoreException bse)
        {
            log.warn("can't find children of " + prefs.absolutePath(), bse);
        }
    }
}
