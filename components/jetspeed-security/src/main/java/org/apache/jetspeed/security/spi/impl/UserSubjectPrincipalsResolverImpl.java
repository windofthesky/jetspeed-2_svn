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

package org.apache.jetspeed.security.spi.impl;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalAssociationType;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.spi.JetspeedPrincipalAccessManager;
import org.apache.jetspeed.security.spi.UserSubjectPrincipalsProvider;
import org.apache.jetspeed.security.spi.UserSubjectPrincipalsResolver;

/**
 * @version $Id$
 *
 */
public class UserSubjectPrincipalsResolverImpl implements UserSubjectPrincipalsResolver
{
    private UserSubjectPrincipalsProvider spp;
    private JetspeedPrincipalType principalType;
    private JetspeedPrincipalAssociationType uat; 
    private List<JetspeedPrincipalAssociationType> iatList; 
    private boolean fromUser;
    private boolean initialized;
    private JetspeedPrincipalAccessManager accessManager;
    
    private static List<Object> wrapInList(JetspeedPrincipalAssociationType type)
    {
        if (type != null)
        {
            List<Object> list = new ArrayList<Object>(1);
            list.add(type);
            return list;
        }
        return Collections.emptyList();
    }

    public UserSubjectPrincipalsResolverImpl(UserSubjectPrincipalsProvider spp, JetspeedPrincipalType principalType,
                                         JetspeedPrincipalAssociationType userAssociationType)
    {
        this(spp, principalType, userAssociationType, (JetspeedPrincipalAssociationType)null);
    }
    
    public UserSubjectPrincipalsResolverImpl(UserSubjectPrincipalsProvider spp, JetspeedPrincipalType principalType,
            JetspeedPrincipalAssociationType userAssociationType,
            JetspeedPrincipalAssociationType indirectAssociationType)
    {
        this(spp, principalType, userAssociationType, wrapInList(indirectAssociationType));
    }
    
    public UserSubjectPrincipalsResolverImpl(UserSubjectPrincipalsProvider spp, JetspeedPrincipalType principalType,
                                         JetspeedPrincipalAssociationType userAssociationType,
                                         List<Object> indirectAssociationTypes)
    {
        if (!spp.getPrincipalType().getName().equals(JetspeedPrincipalType.USER))
        {
            throw new IllegalArgumentException("The provided SubjectPrincipalsProvider should be for the user principalType");
        }
        this.spp = spp;

        if (principalType.getName().equals(JetspeedPrincipalType.USER))
        {
            throw new IllegalArgumentException("This resolver principalType cannot be that of the user principalType");
        }
        this.principalType = principalType;        
        
        if (userAssociationType.getFromPrincipalType().getName().equals(JetspeedPrincipalType.USER))
        {
            if (userAssociationType.getToPrincipalType() != principalType)
            {
                throw new IllegalArgumentException("Provided userAssociationType doesn't target this resolver principalType");
            }
            fromUser = true;
        }
        else if (userAssociationType.getToPrincipalType().getName().equals(JetspeedPrincipalType.USER))
        {
            if (userAssociationType.getFromPrincipalType() != principalType)
            {
                throw new IllegalArgumentException("Provided userAssociationType doesn't target this resolver principalType");
            }
            fromUser = false;
        }
        else
        {
            throw new IllegalArgumentException("Provided userAssociationType is not for a user association");
        }
        this.uat = userAssociationType;
        
        if (indirectAssociationTypes != null && !indirectAssociationTypes.isEmpty())
        {
            iatList = new ArrayList<JetspeedPrincipalAssociationType>(indirectAssociationTypes.size());
            
            for (Object o: indirectAssociationTypes)
            {
                JetspeedPrincipalAssociationType iat = (JetspeedPrincipalAssociationType)o;
                if (iat.getFromPrincipalType() != principalType && iat.getToPrincipalType() != principalType)
                {
                    throw new IllegalArgumentException("Provided indirectAssociationType "+iat.getAssociationName()+" should match this resolvers principalType");
                }
                else
                {
                   iatList.add(iat); 
                }
            }
        }
        
        spp.addSubjectPrincipalsResolver(this);
    }
    
    protected void checkInitialized()
    {
        if (!initialized)
        {
            accessManager = spp.getPrincipalAccessManager();
            initialized = true;
        }
    }

    public JetspeedPrincipalType getPrincipalType()
    {
        return principalType;
    }
    
    protected void processFound(List<JetspeedPrincipal> found, User user, Set<Long> resolvedIds, Set<Principal> principals, Map<String, UserSubjectPrincipalsResolver> resolvers)
    {
        for (int i = found.size() -1; i > -1; i--)
        {
            JetspeedPrincipal p = found.get(i);
            if (!p.isEnabled() || !resolvers.containsKey(p.getType().getName()) || !resolvedIds.add(p.getId()))
            {
                found.remove(i);
            }
        }
        for (JetspeedPrincipal p : found)
        {
            resolvers.get(p.getType().getName()).processPrincipal(p, user, resolvedIds, principals, resolvers);
        }
    }

    public void resolve(User user, Set<Long> resolvedIds, Set<Principal> principals, Map<String, UserSubjectPrincipalsResolver> resolvers)
    {
        checkInitialized();
        if (user.getId() == null || !user.isEnabled())
        {
            // sanity check
            return;
        }
        List<JetspeedPrincipal> found = null;
        if (fromUser)
        {
            found = accessManager.getAssociatedFrom(user.getId(), uat.getFromPrincipalType(), uat.getToPrincipalType(), uat.getAssociationName());
        }
        else
        {
            found = accessManager.getAssociatedTo(user.getId(), uat.getFromPrincipalType(), uat.getToPrincipalType(), uat.getAssociationName());
        }
        processFound(found, user, resolvedIds, principals, resolvers);
    }
    
    public void processPrincipal(JetspeedPrincipal principal, User user, Set<Long> resolvedIds, Set<Principal> principals, Map<String, UserSubjectPrincipalsResolver> resolvers)
    {
        checkInitialized();
        if (user.getId() == null || !user.isEnabled() || principal.getId() == null || !principal.isEnabled() || principal.getType() != principalType)
        {
            // sanity check
            return;
        }
        principals.add(principal);
        if (iatList != null)
        {
            for (JetspeedPrincipalAssociationType iat : iatList)
            {
                List <JetspeedPrincipal> found = null;
                
                if ((!iat.isMixedTypes() && !iat.isSingular()) || !iat.getFromPrincipalType().getName().equals(getPrincipalType().getName()))
                {
                    found = accessManager.getAssociatedTo(principal.getId(), iat.getFromPrincipalType(), iat.getToPrincipalType(), iat.getAssociationName());
                }
                else
                {
                    found = accessManager.getAssociatedFrom(principal.getId(), iat.getFromPrincipalType(), iat.getToPrincipalType(), iat.getAssociationName());
                }
                processFound(found, user, resolvedIds, principals, resolvers);
            }
        }
    }
}