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
package org.apache.jetspeed.security.spi.impl.ldap;

import java.security.Principal;

import org.apache.jetspeed.security.SecurityException;

/**
 * <p>
 * Generic DAO interface for LDAP principals.
 * </p>
 * 
 * @author <a href="mailto:mike.long@dataline.com">Mike Long </a>, <a
 *         href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public interface LdapPrincipalDao extends LdapReadOnlyPrincipalDao
{
    /**
     * <p>
     * Makes a new ldap entry for the specified principal.
     * </p>
     * 
     * @param principalUid The principal uid.
     * @throws SecurityException Throws a {@link SecurityException}.
     */
    abstract void create(final String principalUid) throws SecurityException;

    /**
     * <p>
     * Deletes a ldap entry for the specified principal.
     * </p>
     * 
     * @param principalUid The principal uid.
     * @throws SecurityException Throws a {@link SecurityException}.
     */
    abstract void delete(final String principalUid) throws SecurityException;

    /**
     * <p>
     * Search the ldap directory for the principal.
     * </p>
     * 
     * @param principalUid The uid value of the principal.
     * @param principalType The type of principal.
     * @return All the objects of this LDAP class type.
     */
    Principal[] find(final String principalUid, String principalType) throws SecurityException;

    /**
     * <p>
     * Converts the uid to an ldap acceptable name.
     * </p>
     * 
     * @param uid The uid.
     * @return The converted name.
     */
    String convertUidToLdapAcceptableName(String uid);
}