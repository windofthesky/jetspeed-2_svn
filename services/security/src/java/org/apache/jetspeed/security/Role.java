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
package org.apache.jetspeed.security;

import java.util.prefs.Preferences;

/**
 * <p>A role made of a {@link RolePrincipal} and the role {@link Preferences}.</p>
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public interface Role
{
    /**
     * <p>Getter for the role {@link RolePrincipal}.</p>
     * @return The {@link RolePrincipal}.
     */
    RolePrincipal getPrincipal();

    /**
     * <p>Setter for the role {@link RolePrincipal}.</p>
     * @param rolePrincipal The {@link RolePrincipal}.
     */
    void setPrincipal(RolePrincipal rolePrincipal);

    /**
     * <p>Getter for the role {@link Preferences} node, providing access to the
     * role preferences properties.</p>
     * @return The {@link Preferences}.
     */
    Preferences getPreferences();

    /**
     * <p>Setter for the role {@link Preferences} node, providing access to the
     * role preferences properties.</p>
     * @param preferences The {@link Preferences}.
     */
    void setPreferences(Preferences preferences);
}
