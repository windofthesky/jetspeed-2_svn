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

import org.apache.jetspeed.security.RolePrincipal;

/**
 * <p>{@link RolePrincipal} interface implementation.</p>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class RolePrincipalImpl extends BasePrincipalImpl implements RolePrincipal
{

    /** The serial version uid. */
    private static final long serialVersionUID = -3521731040045006314L;

    /**
     * <p>The role principal constructor.</p>
     * @param roleName The role principal name.
     */
    public RolePrincipalImpl(String roleName)
    {
        super(roleName, PREFS_ROLE_ROOT);
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
        if (!(another instanceof RolePrincipalImpl))
        {
            return false;
        }
        RolePrincipalImpl principal = (RolePrincipalImpl) another;
        return this.getName().equals(principal.getName());
    }

    /**
     * <p>Gets the principal implementation full path from the principal name.</p>
     * <p>Prepends PREFS_ROLE_ROOT if not prepended.</p>        
     * @param name The principal name.
     * @return The preferences full path / principal name.
     */
    public static String getFullPathFromPrincipalName(String name)
    {
        return BasePrincipalImpl.getFullPathFromPrincipalName(name, PREFS_ROLE_ROOT);
    }

    /**
     * <p>Gets the principal name from the principal implementation full path.</p>
     * <p>Remove prepended PREFS_ROLE_ROOT if present.</p>        
     * @param fullPath The principal full path.
     * @return The principal name.
     */
    public static String getPrincipalNameFromFullPath(String fullPath)
    {
        return BasePrincipalImpl.getPrincipalNameFromFullPath(fullPath, PREFS_ROLE_ROOT);
    }
}
