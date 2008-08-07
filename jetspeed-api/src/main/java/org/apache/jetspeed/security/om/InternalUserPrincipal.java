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
package org.apache.jetspeed.security.om;

import java.util.Collection;

/**
 * <p>Interface representing Jetspeed security user principal object model.</p>
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public interface InternalUserPrincipal extends InternalPrincipal
{
    final static String USER_TYPE = "user";
    
    /**
     * <p>Getter for the security credentials.</p>
     * @return The credentials.
     */
    Collection<InternalCredential> getCredentials();

    /**
     * <p>Setter for the security credentials.</p>
     * @param credentials The credentials.
     */
    void setCredentials(Collection<InternalCredential> credentials);

    /**
     * <p>Getter for the role principals.</p>
     * @return The role principals.
     */
    Collection<InternalRolePrincipal> getRolePrincipals();

    /**
     * <p>Setter for the role principals.</p>
     * @param rolePrincipals The role principals.
     */
    void setRolePrincipals(Collection<InternalRolePrincipal> rolePrincipals);

    /**
     * <p>Getter for the group principals.</p>
     * @return The group principals.
     */
    Collection<InternalGroupPrincipal> getGroupPrincipals();

    /**
     * <p>Setter for the group principals.</p>
     * @param groupPrincipals The group principals.
     */
    void setGroupPrincipals(Collection<InternalGroupPrincipal> groupPrincipals);
}
