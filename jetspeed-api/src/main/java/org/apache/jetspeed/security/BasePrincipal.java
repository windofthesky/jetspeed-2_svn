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

import java.io.Serializable;

import java.security.Principal;

/**
* <p>The base principal.</p>
* @author <a href="mailto:taylor@apache.org">David Taylor</a>, <a href="mailto:dlestrat@apache.org">David Le Strat</a>
*/
public interface BasePrincipal extends Principal, Serializable
{
    /** <p>The Preferences user root node</p> */
    final static String  PREFS_USER_ROOT = "/user/";
    
    /** <p>The Preferences group root node</p> */
    final static String PREFS_GROUP_ROOT = "/group/";
    
    /** <p>The Preferences role root node</p> */
    final static String  PREFS_ROLE_ROOT = "/role/";
    
    /**
     * <p>Provides the principal full path prepending PREFS_{PRINCPAL}_ROOT if not prepended.</p>
     * @return The principal full path.
     */
    String getFullPath();

    /**
     * <p>Getter for the enabled state</p>
     * @return true if enabled
     */
    boolean isEnabled();
    
    /**
     * Setter for the enabled state</p>
     * @param enabled The enabled state
     */
    void setEnabled(boolean enabled);
    
    /**
     * <p>is this principal a security principal mapping or a real principal</p>
     * @return true if is a mapping
     */
    boolean isMapping();
    
}
