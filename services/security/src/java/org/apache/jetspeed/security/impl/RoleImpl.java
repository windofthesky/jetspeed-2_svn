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

import java.util.prefs.Preferences;

import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.RolePrincipal;

/**
 * <p>A role made of a {@link RolePrincipal} and the user {@link Preferences}.</p>
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class RoleImpl implements Role
{

    /**
     * <p>Default constructor.</p>
     */
    public RoleImpl()
    {
    }

    /**
     * <p>{@link Role} constructor given a role principal and preferences.</p>
     * @param subject The role principal.
     * @param preferences The preferences.
     */
    public RoleImpl(RolePrincipal rolePrincipal, Preferences preferences)
    {
        this.rolePrincipal = rolePrincipal;
        this.preferences = preferences;
    }

    private RolePrincipal rolePrincipal;
    /**
     * @see org.apache.jetspeed.security.Role#getPrincipal()
     */
    public RolePrincipal getPrincipal()
    {
        return this.rolePrincipal;
    }

    /**
     * @see org.apache.jetspeed.security.Role#setPrincipal(org.apache.jetspeed.security.RolePrincipal)
     */
    public void setPrincipal(RolePrincipal rolePrincipal)
    {
        this.rolePrincipal = rolePrincipal;
    }

    private Preferences preferences;

    /**
     * @see org.apache.jetspeed.security.Role#getPreferences()
     */
    public Preferences getPreferences()
    {
        return this.preferences;
    }

    /**
     * @see org.apache.jetspeed.security.Role#setPreferences(java.util.prefs.Preferences)
     */
    public void setPreferences(Preferences preferences)
    {
        this.preferences = preferences;
    }

}
