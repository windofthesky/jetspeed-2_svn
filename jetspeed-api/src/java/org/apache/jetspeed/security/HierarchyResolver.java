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
package org.apache.jetspeed.security;

import java.util.prefs.Preferences;

/**
 * <p>
 * This class allows to implement different types of groups/roles hierarchy.
 * </p>
 * 
 * @author <a href="mailto:Artem.Grinshtein@t-systems.com">Artem Grinshtein </a>
 * @version $Id: HierarchyResolver.java 187640 2004-09-30 04:01:42Z dlestrat $
 */
public interface HierarchyResolver 
{
    
    /**
     * <p>
     * Returns absolute path names of the hierarchy roles/groups.
     * </p>
     * 
     * @param prefs Preferences for the role/group
     * @return Returns absolute path names of the dependcy roles/groups.
     */
    public String[] resolve(Preferences prefs);
    
    /**
     * <p>
     * Returns the absolute path names of the children of the given hierarchy
     * roles/groups node.
     * </p>
     * 
     * @param prefs Preferences for the role/group
     * @return Returns absolute path names of the children roles/groups.
     */
    public String[] resolveChildren(Preferences prefs);
        
}
