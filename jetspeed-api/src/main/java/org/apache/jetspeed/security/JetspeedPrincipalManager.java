/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.security;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @version $Id$
 */
public interface JetspeedPrincipalManager extends Serializable
{
    JetspeedPrincipalType getPrincipalType();
    
    List<JetspeedPrincipalAssociationType> getAssociationTypes();

     boolean principalExists(String name);

    JetspeedPrincipal getPrincipal(String name);

    /**
     * <p>
     * Retrieves a detached and modifiable List of principal names, finding principals matching the corresponding
     * principal name filter.
     * </p>
     * 
     * @param nameFilter The filter used to retrieve matching principals.
     * @return A list of principal names
     */
    List<String> getPrincipalNames(String nameFilter);

    /**
     * <p>
     * Retrieves a detached and modifiable {@link JetspeedPrincipal} list matching the corresponding
     * principal name filter.
     * </p>
     * 
     * @param nameFilter The filter used to retrieve matching principal.
     * @return a list of {@link JetspeedPrincipal}
     */
    List<? extends JetspeedPrincipal> getPrincipals(String nameFilter);
    
    /**
     * Retrieve all principals that match the queryContext.
     * It returns a {@link JetspeedPrincipalResultList}, containing
     * the actual result list an the total number of results from the query.
     * 
     * The returned principals are detached.
     * 
     * @param queryContext The (@see JetspeedPrincipalQueryContext) for this query.
     * @return
     */
    public JetspeedPrincipalResultList getPrincipals(JetspeedPrincipalQueryContext queryContext);    
    
    List<? extends JetspeedPrincipal> getPrincipalsByAttribute(String attributeName, String attributeValue);
    
    JetspeedPrincipal newPrincipal(String name, boolean mapped);

    JetspeedPrincipal newTransientPrincipal(String name);
    
    void removePrincipal(String name) throws SecurityException;

    /**
     * <p>
     * Retrieves a detached and modifiable {@link JetspeedPrincipal} list of all the principals managed by this manager which are
     * associated <em>from</em> the specified principal by the specified association.
     * </p>
     * <p>
     * If the association is {@link JetspeedPrincipalAssociationType#isSingular()} at most one principal will be returned.
     * </p>
     * 
     * @param principalFromName The principal name to find the other principals associated <em>to</em>.
     * @param from The principal type of the provided principal name
     * @param associationName The name of the association <em>from</em> the provided principal type <em>to</em> this Manager principal type.
     * @return The list of {@link JetspeedPrincipal} in the <em>to</em> side of the provided association for the provided principal name and its type
     */
    List<? extends JetspeedPrincipal> getAssociatedFrom(String principalFromName, JetspeedPrincipalType from, String associationName);

    /**
     * <p>
     * Retrieves a detached and modifiable {@link JetspeedPrincipal} list of all the principals managed by this manager which are
     * associated <em>to</em> the specified principal by the specified association.
     * </p>
     * <p>
     * If the association is {@link JetspeedPrincipalAssociationType#isDominant()} at most one principal will be returned.
     * </p>
     * 
     * @param principalToName The principal name to find the other principals associated <em>from</em>.
     * @param to The principal type of the provided principal name
     * @param associationName The name of the association <em>from</em> this Manager principal type <em>to</em> the provided principal type
     * @return The list of {@link JetspeedPrincipal} in the <em>from</em> side of the provided association for the provided principal name and its type
     */
    List<? extends JetspeedPrincipal> getAssociatedTo(String principalToName, JetspeedPrincipalType to, String associationName);

    /**
     * <p>
     * Retrieves a detached and modifiable list of the names of all the principals managed by this manager which are
     * associated <em>from</em> the specified principal by the specified association.
     * </p>
     * <p>
     * If the association is {@link JetspeedPrincipalAssociationType#isSingular()} at most one principal name will be returned.
     * </p>
     * 
     * @param principalFromName The principal name to find the other principals associated <em>to</em>.
     * @param from The principal type of the provided principal name
     * @param associationName The name of the association <em>from</em> the provided principal type <em>to</em> this Manager principal type
     * @return The list of the names of the principals in the <em>from</em> side of the provided association for the provided principal name and its type
     */
    List<String> getAssociatedNamesFrom(String principalFromName, JetspeedPrincipalType from, String associationName);

    /**
     * <p>
     * Retrieves a detached and modifiable list of the names of all the principals managed by this manager which are
     * associated <em>to</em> the specified principal by the specified association.
     * </p>
     * <p>
     * If the association is {@link JetspeedPrincipalAssociationType#isDominant()} at most one principal name will be returned.
     * </p>
     * 
     * @param principalToName The principal name to find the other principals associated <em>from</em>.
     * @param to The principal type of the provided principal name
     * @param associationName The name of the association <em>from</em> this Manager principal type the <em>to</em> principal type
     * @return The list of the names of the principals in the <em>from</em> side of the provided association for the provided principal name and its type
     */
    List<String> getAssociatedNamesTo(String principalToName, JetspeedPrincipalType to, String associationName);

    void addPrincipal(JetspeedPrincipal principal, Set<JetspeedPrincipalAssociationReference> associations)
        throws SecurityException;

    void updatePrincipal(JetspeedPrincipal principal) throws SecurityException;

    void removePrincipal(JetspeedPrincipal principal)
        throws SecurityException;

    void addAssociation(JetspeedPrincipal from, JetspeedPrincipal to, String associationName)
        throws SecurityException;

    void transferAssociationTo(JetspeedPrincipal from, JetspeedPrincipal to, JetspeedPrincipal target, String associationName)
        throws SecurityException;

    void transferAssociationFrom(JetspeedPrincipal from, JetspeedPrincipal to, JetspeedPrincipal target, String associationName)
        throws SecurityException;
    
    void removeAssociation(JetspeedPrincipal from, JetspeedPrincipal to, String associationName)
        throws SecurityException;
           
    void addListener(PrincipalManagerEventListener listener);
    
    void removeListener(PrincipalManagerEventListener listener);
}
