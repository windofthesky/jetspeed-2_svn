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
package org.apache.jetspeed.security.impl;

import org.apache.jetspeed.security.GroupPrincipal;

/**
 * <p>{@link GroupPrincipal} interface implementation.</p>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class GroupPrincipalImpl extends BasePrincipalImpl implements GroupPrincipal
{

    /** <p>The Preferences group root node</p> */
    static String PREFS_GROUP_ROOT = "/group/";

    /**
     * <p>The group principal constructor.</p>
     * @param groupName The group principal name.
     */
    public GroupPrincipalImpl(String groupName)
    {
        super(groupName, PREFS_GROUP_ROOT);
    }

    /**
     * <p>Compares this principal to the specified object.  Returns true
     * if the object passed in matches the principal represented by
     * the implementation of this interface.</p>
     * @param another Principal to compare with.
     * @return True if the principal passed in is the same as that
     * encapsulated by this principal, and false otherwise.

     */
    public boolean equals(Object another)
    {
        if (!(another instanceof GroupPrincipalImpl))
            return false;
        GroupPrincipalImpl principal = (GroupPrincipalImpl)another;
        return this.getName().equals(principal.getName());
    }

    /**
     * <p>Gets the principal implementation full path from the principal name.</p>
     * <p>Prepends PREFS_GROUP_ROOT if not prepended.</p>        
     * @param name The principal name.
     * @return The preferences full path / principal name.
     */
    public static String getFullPathFromPrincipalName(String name)
    {
        return BasePrincipalImpl.getFullPathFromPrincipalName(name, PREFS_GROUP_ROOT);
    }

    /**
     * <p>Gets the principal name from the principal implementation full path.</p>
     * <p>Remove prepended PREFS_GROUP_ROOT if present.</p>        
     * @param name The principal full path.
     * @return The principal name.
     */
    public static String getPrincipalNameFromFullPath(String fullPath)
    {
        return BasePrincipalImpl.getPrincipalNameFromFullPath(fullPath, PREFS_GROUP_ROOT);
    }
    
}
