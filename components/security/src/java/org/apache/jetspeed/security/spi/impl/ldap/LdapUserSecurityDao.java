/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.security.spi.impl.ldap;

import java.security.Principal;

import org.apache.jetspeed.security.SecurityException;

/**
 * <p>
 * Ldap dao for user security implementation.
 * </p>
 * 
 * @author <a href="mailto:mike.long@dataline.com">Mike Long </a>
 */
public interface LdapUserSecurityDao extends LdapUserDao
{
    /**
     * <p>
     * Makes a new ldap entry for the specified user.
     * </p>
     * 
     * @param uid The uid.
     * @throws SecurityException Throws a {@link SecurityException}.
     */
    public abstract void create(final String uid) throws SecurityException;

    /**
     * <p>
     * Delete a user by uid.
     * </p>
     * 
     * @param uid The uid.
     * @throws SecurityException Throws a {@link SecurityException}.
     */
    public abstract void delete(final String uid) throws SecurityException;

    /**
     * <p>
     * Find the uid principals.
     * </p>
     * 
     * @param uid The uid.
     * @throws SecurityException Throws a {@link SecurityException}.
     */
    public Principal[] find(final String uid) throws SecurityException;
}