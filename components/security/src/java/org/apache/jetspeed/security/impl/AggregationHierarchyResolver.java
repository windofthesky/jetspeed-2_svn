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
 * <p>Implementation for "part of" hierarchy. For Example:
 * There're roles: 
 * <ul>
 * <li>roleA</li>
 * <li>roleA.roleB</li>
 * <li>roleA.roleB.roleC</li>
 * </ul>
 * if a user has the role [roleA] than</p>
 * <code>user.getSubject().getPrincipals()</code>
 * returns:
 * <ul>
 * <li>/role/roleA</li>
 * <li>/role/roleA/roleB</li>
 * <li>/role/roleA/roleB/roleC</li>
 * </ul> 
 * @author <a href="mailto:Artem.Grinshtein@t-systems.com">Artem Grinshtein</a>
 * @version $Id$
 */
public class AggregationHierarchyResolver implements HierarchyResolver  
{
    private static final Log log = LogFactory.getLog(AggregationHierarchyResolver.class);
    
    /**
     * @see org.apache.jetspeed.security.impl.HierarchyResolver#resolve()
     */
    public String[] resolve( Preferences prefs ) {
        ArgUtil.notNull(
                new Object[] { prefs },
                new String[] { "preferences" },
                "resolve(java.util.prefs.Preferences)");
        
        List list=new ArrayList();
        processPreferences(prefs,list);     
        return  (String [])list.toArray(new String[0]) ;
    }
    
    
    protected void processPreferences(Preferences prefs,List list) {
        list.add(prefs.absolutePath());
        try 
        {
            String [] names=prefs.childrenNames();
            for (int i = 0; i < names.length; i++)
            {
                processPreferences(prefs.node(names[i]),list);
            }
        }catch(BackingStoreException bse) {
            log.warn("can't find children of "+prefs.absolutePath(),bse);
        }
        
    }
        
}
