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

import java.security.Principal;
import java.util.prefs.Preferences;

import org.apache.jetspeed.security.Group;

/**
 * <p>A group made of a {@link GroupPrincipal} and the user {@link Preferences}.</p>
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class GroupImpl implements Group
{
    /**
     * <p>Default constructor.</p>
     */
    public GroupImpl()
    {
    }

    /**
     * <p>{@link Group} constructor given a group principal and preferences.</p>
     * @param groupPrincipal The group principal.
     * @param preferences The preferences.
     */
    public GroupImpl(Principal groupPrincipal, Preferences preferences)
    {
        this.groupPrincipal = groupPrincipal;
        this.preferences = preferences;
    }

    private Principal groupPrincipal;

    /**
     * @see org.apache.jetspeed.security.Group#getPrincipal()
     */
    public Principal getPrincipal()
    {
        return this.groupPrincipal;
    }

    /**
     * @see org.apache.jetspeed.security.Group#setPrincipal(java.security.Principal)
     */
    public void setPrincipal(Principal groupPrincipal)
    {
        this.groupPrincipal = groupPrincipal;
    }

    private Preferences preferences;

    /**
     * @see org.apache.jetspeed.security.Group#getPreferences()
     */
    public Preferences getPreferences()
    {
        return this.preferences;
    }

    /**
     * @see org.apache.jetspeed.security.Group#setPreferences(java.util.prefs.Preferences)
     */
    public void setPreferences(Preferences preferences)
    {
        this.preferences = preferences;
    }

}
