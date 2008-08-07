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
package org.apache.jetspeed.security.spi.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.jetspeed.security.GroupPrincipal;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.impl.GroupPrincipalImpl;
import org.apache.jetspeed.security.om.InternalGroupPrincipal;
import org.apache.jetspeed.security.om.impl.InternalGroupPrincipalImpl;
import org.apache.jetspeed.security.spi.GroupSecurityHandler;
import org.apache.jetspeed.security.spi.SecurityAccess;

/**
 * @see org.apache.jetspeed.security.spi.GroupSecurityHandler
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 */
public class DefaultGroupSecurityHandler implements GroupSecurityHandler
{

    /** Common queries. */
    private SecurityAccess commonQueries = null;

    /**
     * <p>
     * Constructor providing access to the common queries.
     * </p>
     */
    public DefaultGroupSecurityHandler(SecurityAccess commonQueries)
    {
        this.commonQueries = commonQueries;
    }

    /**
     * @see org.apache.jetspeed.security.spi.GroupSecurityHandler#getGroupPrincipal(java.lang.String)
     */
    public GroupPrincipal getGroupPrincipal(String groupName)
    {
        GroupPrincipal groupPrincipal = null;
        InternalGroupPrincipal internalGroup = commonQueries.getInternalGroupPrincipal(groupName);        
        if (null != internalGroup)
        {
            groupPrincipal = new GroupPrincipalImpl(internalGroup.getPrincipalId(), internalGroup.getName(),
                                            internalGroup.isEnabled(), internalGroup.isMappingOnly());
        }
        return groupPrincipal;
    }

    /**
     * @see org.apache.jetspeed.security.spi.GroupSecurityHandler#setGroupPrincipal(org.apache.jetspeed.security.GroupPrincipal)
     */
    public void storeGroupPrincipal(GroupPrincipal groupPrincipal)
            throws SecurityException
    {
        InternalGroupPrincipal internalGroup = commonQueries.getInternalGroupPrincipal(groupPrincipal.getName());
        if (null == internalGroup)
        {
            internalGroup = new InternalGroupPrincipalImpl(groupPrincipal.getName());
            internalGroup.setEnabled(groupPrincipal.isEnabled());
            commonQueries.storeInternalGroupPrincipal(internalGroup, false);            
        }
        else if ( !internalGroup.isMappingOnly() )
        {
            if ( internalGroup.isEnabled() != groupPrincipal.isEnabled() )
            {
                internalGroup.setEnabled(groupPrincipal.isEnabled());
                commonQueries.storeInternalGroupPrincipal(internalGroup, false);
            }
        }
        else
        {
            // TODO: should we throw an exception here?
        }
    }

    /**
     * @see org.apache.jetspeed.security.spi.GroupSecurityHandler#removeGroupPrincipal(org.apache.jetspeed.security.GroupPrincipal)
     */
    public void removeGroupPrincipal(GroupPrincipal groupPrincipal)
            throws SecurityException
    {
        InternalGroupPrincipal internalGroup = commonQueries.getInternalGroupPrincipal(groupPrincipal.getName());
        if (null != internalGroup)
        {
            commonQueries.removeInternalGroupPrincipal(internalGroup);
        }
    }

    /**
     * @see org.apache.jetspeed.security.spi.GroupSecurityHandler#getGroupPrincipals(java.lang.String)
     */
    public List<GroupPrincipal> getGroupPrincipals(String filter)
    {
        List<GroupPrincipal> groupPrincipals = new LinkedList<GroupPrincipal>();
        Collection<InternalGroupPrincipal> internalGroups = commonQueries.getInternalGroupPrincipals(filter);
        for (InternalGroupPrincipal internalGroup : internalGroups)
        {
            groupPrincipals
                    .add(new GroupPrincipalImpl(internalGroup.getPrincipalId(), internalGroup.getName(),
                                internalGroup.isEnabled(), internalGroup.isMappingOnly()) 
                            );
        }
        return groupPrincipals;
    }

}
