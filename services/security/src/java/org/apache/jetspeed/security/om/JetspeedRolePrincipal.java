/*
 * Copyright 2000-2004 The Apache Software Foundation.
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
package org.apache.jetspeed.security.om;

import java.io.Serializable;
import java.util.Collection;
import java.sql.Timestamp;

/**
 * <p>Interface representing Jetspeed security role principal object model.</p>
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public interface JetspeedRolePrincipal extends JetspeedPrincipal
{
    /**
     * <p>Getter for the user principals.</p>
     * @return The user principals.
     */
    Collection getUserPrincipals();

    /**
     * <p>Setter for the user principals.</p>
     * @param userPrincipals The user principals.
     */
    void setUserPrincipals(Collection userPrincipals);

    /**
     * <p>Getter for the group principals.</p>
     * @return The group principals.
     */
    Collection getGroupPrincipals();

    /**
     * <p>Setter for the group principals.</p>
     * @param groupPrincipals The group principals.
     */
    void setGroupPrincipals(Collection groupPrincipals);
}
