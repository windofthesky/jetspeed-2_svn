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
package org.apache.jetspeed.security.spi.impl.ldap;

import org.apache.jetspeed.security.SecurityException;

/**
 * <p>
 * User credential dao.
 * </p>
 * 
 * @author <a href="mailto:mike.long@dataline.com">Mike Long </a>
 */
public interface LdapUserCredentialDao extends LdapUserDao
{
    /**
     * <p>
     * Updates the password for the specified user.
     * </p>
     * 
     * @param uid The uid.
     * @param password The password.
     * @throws SecurityException A {@link SecurityException}.
     */
    public abstract void changePassword(final String uid, final String password) throws SecurityException;

    /**
     * <p>
     * Looks up the user by the UID attribute. If this lookup succeeds, this
     * method then attempts to authenticate the user using the password,
     * throwing an AuthenticationException if the password is incorrect or an
     * OperationNotSupportedException if the password is empty.
     * </p>
     * 
     * @param uid The uid.
     * @param password The password.
     * @throws SecurityException A {@link SecurityException}.
     */
    public abstract boolean authenticate(final String uid, final String password) throws SecurityException;

    /**
     * @param uid The uid.
     * @return The password.
     *@throws SecurityException A {@link SecurityException}.@throws SecurityException
     */
    public abstract char[] getPassword(final String uid) throws SecurityException;
}