/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import java.util.Map;
import java.util.prefs.Preferences;

import javax.security.auth.Subject;

import org.apache.jetspeed.security.attributes.SecurityAttributes;

/**
 * <p>A user made of a {@link Subject} and the user {@link Preferences}.</p>
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public interface User
{
    /**
     * <p>
     * The default user attributes property set.
     * </p>
     */
    final static String USER_INFO_PROPERTY_SET = "userinfo";
    
    /**
     * the subsite path for a given user stored as a user attribute
     */
    final static String USER_INFO_SUBSITE = "subsite";
    
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
     * <p>Getter providing access to the
     * user security attributes.</p>
     * @return The security attributes for a user
     */
    SecurityAttributes getAttributes();

    /**
     * <p>Setter providing access to the
     * user security attributes.</p>
     * @param attributes The security attributes for a user
     */
    void setAttributes(SecurityAttributes attributes);
    
    /**
     * Get the Portlet API User Attributes for a given user
     * @return a READ ONLY set of user attributes for a given user
     */
    Map<String, String> getUserAttributes();
    
    /**
     * Get the best user principal for this user
     * @return a user principal
     */
    UserPrincipal getUserPrincipal();
}
