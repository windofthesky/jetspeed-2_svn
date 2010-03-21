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
package org.apache.jetspeed.security.spi;

import java.util.List;

import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalQueryContext;
import org.apache.jetspeed.security.JetspeedPrincipalResultList;
import org.apache.jetspeed.security.JetspeedPrincipalType;

/**
 * @version $Id$
 */
public interface JetspeedPrincipalAccessManager
{
    boolean principalExists(String principalName, JetspeedPrincipalType type);

    JetspeedPrincipal getPrincipal(Long id);

    JetspeedPrincipal getPrincipal(String principalName, JetspeedPrincipalType type);

    List<JetspeedPrincipal> getPrincipals(String nameFilter, JetspeedPrincipalType type);

    List<JetspeedPrincipal> getPrincipalsByAttribute(String attributeName, String attributeValue, JetspeedPrincipalType type);

    List<String> getPrincipalNames(String nameFilter, JetspeedPrincipalType type);

    List<JetspeedPrincipal> getAssociatedFrom(String principalFromName, JetspeedPrincipalType from, JetspeedPrincipalType to, String associationName);

    List<JetspeedPrincipal> getAssociatedFrom(Long principalFromId, JetspeedPrincipalType from, JetspeedPrincipalType to, String associationName);

    List<JetspeedPrincipal> getAssociatedTo(String principalToName, JetspeedPrincipalType from, JetspeedPrincipalType to, String associationName);

    List<JetspeedPrincipal> getAssociatedTo(Long principalToId, JetspeedPrincipalType from, JetspeedPrincipalType to, String associationName);

    List<String> getAssociatedNamesFrom(String principalFromName, JetspeedPrincipalType from, JetspeedPrincipalType to, String associationName);

    List<String> getAssociatedNamesFrom(Long principalFromId, JetspeedPrincipalType from, JetspeedPrincipalType to, String associationName);

    List<String> getAssociatedNamesTo(String principalToName, JetspeedPrincipalType from, JetspeedPrincipalType to, String associationName);

    List<String> getAssociatedNamesTo(Long principalToId, JetspeedPrincipalType from, JetspeedPrincipalType to, String associationName);
    
    /**
     * Retrieve all principals that match the queryContext.
     * It returns a {@link JetspeedPrincipalResultList}, containing
     * the actual result list an the total number of results from the query.
     * 
     * The returned principals are detached.
     * 
     * @param queryContext The (@see JetspeedPrincipalQueryContext) for this query.
     * @param type The principals type (@see JetspeedPrincipalType).
     * @return
     */
    public JetspeedPrincipalResultList getPrincipals(JetspeedPrincipalQueryContext queryContext, JetspeedPrincipalType type);
	
    /**
     * Retrieve all principals that match the queryContext.
     * It returns a {@link JetspeedPrincipalResultList}, containing
     * the actual result list an the total number of results from the query.
     * 
     * The returned principals are detached.
     * 
     * @param queryContext The (@see JetspeedPrincipalQueryContext) for this query.
     * @param type The principals type (@see JetspeedPrincipalType).
	 * @param securityDomain The principals security domain.
     * @return
     */
	public JetspeedPrincipalResultList getPrincipals(JetspeedPrincipalQueryContext queryContext, JetspeedPrincipalType type, Long securityDomain);    
}
