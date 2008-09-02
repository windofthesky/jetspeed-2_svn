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

import javax.security.auth.Subject;

/**
 * <p>User simply extending JetspeedPrincipal interface.</p>
 * @version $Id
 */
public interface User extends JetspeedPrincipal
{
    
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
     * Get the Portlet API User Attributes for a given user
     * @return a READ ONLY set of user attributes for a given user
     */
    Map<String, String> getUserInfo();
    
}
