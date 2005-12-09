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

import java.security.Principal;
import java.util.prefs.Preferences;

/**
 * <p>A group made of a {@link GroupPrincipal} and the group {@link Preferences}.</p>
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public interface Group
{
    /**
     * <p>Getter for the group {@link Principal}.</p>
     * @return The {@link GroupPrincipal}.
     */
    Principal getPrincipal();

    /**
     * <p>Setter for the group {@link Principal}.</p>
     * @param groupPrincipal The {@link Principal}.
     */
    void setPrincipal(Principal groupPrincipal);

    /**
     * <p>Getter for the group {@link Preferences} node, providing access to the
     * group preferences properties.</p>
     * @return The {@link Preferences}.
     */
    Preferences getPreferences();

    /**
     * <p>Setter for the group {@link Preferences} node, providing access to the
     * group preferences properties.</p>
     * @param preferences The {@link Preferences}.
     */
    void setPreferences(Preferences preferences);
}
