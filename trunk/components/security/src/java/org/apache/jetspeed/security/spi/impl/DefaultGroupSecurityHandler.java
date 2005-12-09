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
package org.apache.jetspeed.security.spi.impl;

import java.security.Principal;
import java.util.Iterator;
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
    public Principal getGroupPrincipal(String groupFullPathName)
    {
        GroupPrincipal groupPrincipal = null;
        InternalGroupPrincipal internalGroup = commonQueries
                .getInternalGroupPrincipal(GroupPrincipalImpl
                        .getFullPathFromPrincipalName(groupFullPathName));
        if (null != internalGroup)
        {
            groupPrincipal = new GroupPrincipalImpl(GroupPrincipalImpl
                    .getPrincipalNameFromFullPath(internalGroup.getFullPath()));
        }
        return groupPrincipal;
    }

    /**
     * @see org.apache.jetspeed.security.spi.GroupSecurityHandler#setGroupPrincipal(org.apache.jetspeed.security.GroupPrincipal)
     */
    public void setGroupPrincipal(GroupPrincipal groupPrincipal)
            throws SecurityException
    {
        String fullPath = groupPrincipal.getFullPath();
        InternalGroupPrincipal internalGroup = commonQueries.getInternalGroupPrincipal(fullPath);
        if ( null == internalGroup )
        {
            internalGroup = new InternalGroupPrincipalImpl(fullPath);
            internalGroup.setEnabled(groupPrincipal.isEnabled());
            commonQueries.setInternalGroupPrincipal(internalGroup, false);
        }
        else if ( !internalGroup.isMappingOnly() )
        {
            if ( internalGroup.isEnabled() != groupPrincipal.isEnabled() )
            {
                internalGroup.setEnabled(groupPrincipal.isEnabled());
                commonQueries.setInternalGroupPrincipal(internalGroup, false);
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
        InternalGroupPrincipal internalGroup = commonQueries
                .getInternalGroupPrincipal(groupPrincipal.getFullPath());
        if (null != internalGroup)
        {
            commonQueries.removeInternalGroupPrincipal(internalGroup);
        }
    }

    /**
     * @see org.apache.jetspeed.security.spi.GroupSecurityHandler#getGroupPrincipals(java.lang.String)
     */
    public List getGroupPrincipals(String filter)
    {
        List groupPrincipals = new LinkedList();
        Iterator result = commonQueries.getInternalGroupPrincipals(filter);
        while (result.hasNext())
        {
            InternalGroupPrincipal internalGroup = (InternalGroupPrincipal) result
                    .next();
            String path = internalGroup.getFullPath();
            if (path == null)
            {
                continue;
            }
            groupPrincipals
                    .add(new GroupPrincipalImpl(GroupPrincipalImpl
                            .getPrincipalNameFromFullPath(internalGroup
                                    .getFullPath())));
        }
        return groupPrincipals;
    }

}