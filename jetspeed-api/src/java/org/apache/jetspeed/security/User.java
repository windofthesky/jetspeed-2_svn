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

import javax.security.auth.Subject;

/**
 * <p>A user made of a {@link Subject} and the user {@link Preferences}.</p>
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public interface User
{
    /**
     * <p>Getter for the user {@link Subject} populated with the 
     * application principals.</p>
     * @return The {@link Subject}.
     */
    Subject getSubject();

    /**
     * <p>Setter for the user {@link Subject} populated with the 
     * application principals.</p>
     * @param subject The {@link Subject}.
     */
    void setSubject(Subject subject);

    /**
     * <p>Getter for the user {@link Preferences} node, providing access to the
     * user preferences properties.</p>
     * @return The {@link Preferences}.
     */
    Preferences getPreferences();

    /**
     * <p>Setter for the user {@link Preferences} node, providing access to the
     * user preferences properties.</p>
     *  
     * @param preferences The {@link Preferences}.
     */
    void setPreferences(Preferences preferences);
    
    /**
     * Get the user attributes for a given user
     * @return a preference set of user attributes for a given user
     */
    Preferences getUserAttributes();
    
}
