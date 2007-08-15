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
package org.apache.jetspeed.security.spi;

import java.security.Principal;
import java.util.List;

import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.UserPrincipal;

/**
 * <p>
 * This interface encapsulates the persistence of a user security.
 * </p>
 * <p>
 * This provides a central placeholder for changing the persistence of user
 * security information.
 * </p>
 * <p>
 * A security implementation wanting to store user security implementation in
 * LDAP for instance would need to provide an LDAP implementation of this
 * interface.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public interface UserSecurityHandler
{
    /**
     * <p>
     * Checks if a UserPrincipal exists 
     * @param userName
     * @return true if a UserPrincipal exists
     */
    boolean isUserPrincipal(String userName);
    
    /**
     * <p>
     * Gets the user principal for the given user name.
     * </p>
     * 
     * @param username The user name.
     * @return The <code>Principal</p>
     */
    Principal getUserPrincipal(String username);
    
    /**
     * <p>
     * Gets the an iterator of user principals for a given filter.
     * </p>
     * 
     * @param filter The filter.
     * @return The list of <code>Principal</code>
     */
    List getUserPrincipals(String filter);
    
    /**
     * <p>
     * Adds a new user principal in the backing store.
     * </p>
     * 
     * @param userPrincipal The new <code>UserPrincipal</code>.
     * @throws SecurityException Throws a {@link SecurityException}.
     */
    void addUserPrincipal(UserPrincipal userPrincipal) throws SecurityException;
    
    /**
     * <p>
     * Updates the user principal in the backing store.
     * </p>
     * 
     * @param userPrincipal The <code>UserPrincipal</code>.
     * @throws SecurityException Throws a {@link SecurityException}.
     */
    void updateUserPrincipal(UserPrincipal userPrincipal) throws SecurityException;
    
    /**
     * <p>
     * Removes the user principal.
     * </p>
     * 
     * @param userPrincipal The <code>UserPrincipal</code>.
     * @throws SecurityException Throws a {@link SecurityException}.
     */
    void removeUserPrincipal(UserPrincipal userPrincipal) throws SecurityException;
}
