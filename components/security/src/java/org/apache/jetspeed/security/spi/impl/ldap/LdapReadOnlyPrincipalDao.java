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

import org.apache.jetspeed.security.SecurityException;

/**
 * <p>
 * Interface for read only principals.
 * </p>
 * 
 * @author <a href="mailto:mike.long@dataline.com">Mike Long </a>
 */
public interface LdapReadOnlyPrincipalDao
{
    /**
     * <p>
     * Searches the LDAP server for the user with the specified principal id
     * (uid attribute).
     * </p>
     * 
     * @return The principal's uid value
     * @throws SecurityException A {@link SecurityException}.
     */
    String lookupByUid(final String principalUid) throws SecurityException;

}