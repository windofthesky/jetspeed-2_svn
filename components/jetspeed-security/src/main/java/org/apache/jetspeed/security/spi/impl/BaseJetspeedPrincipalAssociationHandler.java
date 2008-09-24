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

import java.util.List;

import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalAssociationType;
import org.apache.jetspeed.security.JetspeedPrincipalManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.spi.JetspeedPrincipalAssociationHandler;
import org.apache.jetspeed.security.spi.JetspeedPrincipalAssociationStorageManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalManagerSPI;

/**
 * @version $Id$
 *
 */
public abstract class BaseJetspeedPrincipalAssociationHandler implements JetspeedPrincipalAssociationHandler
{
    private JetspeedPrincipalAssociationType associationType;
    private JetspeedPrincipalAssociationStorageManager jpasm;

    private JetspeedPrincipalManagerSPI from;
    private JetspeedPrincipalManagerSPI to;
    
    public BaseJetspeedPrincipalAssociationHandler(JetspeedPrincipalAssociationType associationType, JetspeedPrincipalManagerSPI from, JetspeedPrincipalManagerSPI to, JetspeedPrincipalAssociationStorageManager jpasm)
    {
        this.associationType = associationType;
        this.from = from;
        this.to = to;
        this.jpasm = jpasm;
        if (!associationType.getFromPrincipalType().getName().equals(from.getPrincipalType().getName()) ||
                        !associationType.getToPrincipalType().getName().equals(to.getPrincipalType().getName()))
        {
            throw new IllegalArgumentException("Provided ManagerFrom or ManagerTo PrincipalType do not correspond with the AssociationType");
        }
        from.addAssociationHandler(this);
        if (from != to)
        {
            to.addAssociationHandler(this);
        }
    }
    
    public JetspeedPrincipalAssociationType getAssociationType()
    {
        return associationType;
    }
    
    public JetspeedPrincipalManager getManagerFrom()
    {
        return from;
    }
    
    public JetspeedPrincipalManager getManagerTo()
    {
        return to;
    }
    
    public void add(JetspeedPrincipal from, JetspeedPrincipal to) throws SecurityException
    {
        if (from.getType().equals(associationType.getFromPrincipalType()) && to.getType().equals(associationType.getToPrincipalType()))
        {
            if (!isSynchronizing())
            {
                if (associationType.isSingular() && !getManagerFrom().getAssociatedFrom(from.getName(), from.getType(), associationType.getAssociationName()).isEmpty())
                {
                    if (associationType.isMixedTypes())
                    {
                        throw new SecurityException(SecurityException.PRINCIPAL_ASSOCIATION_SINGULAR_MIXED.createScoped(from.getType().getName(),associationType.getAssociationName(), from.getName(), to.getType().getName()));
                    }
                    else
                    {
                        throw new SecurityException(SecurityException.PRINCIPAL_ASSOCIATION_SINGULAR.createScoped(from.getType().getName(),associationType.getAssociationName(), from.getName(), from.getType().getName()));
                    }
                }
                if (associationType.isDominant() && !getManagerTo().getAssociatedTo(to.getName(), to.getType(), associationType.getAssociationName()).isEmpty())
                {
                    if (associationType.isMixedTypes())
                    {
                        throw new SecurityException(SecurityException.PRINCIPAL_ASSOCIATION_DOMINANT_MIXED.createScoped(to.getType().getName(),associationType.getAssociationName(), to.getName(), from.getType().getName()));
                    }
                    else
                    {
                        throw new SecurityException(SecurityException.PRINCIPAL_ASSOCIATION_DOMINANT.createScoped(to.getType().getName(),associationType.getAssociationName(), to.getName(), to.getType().getName()));
                    }
                }
            }
            jpasm.addAssociation(from, to, associationType.getAssociationName());
        }
    }

    public void remove(JetspeedPrincipal from, JetspeedPrincipal to) throws SecurityException
    {
        if (from.getType().equals(associationType.getFromPrincipalType()) && to.getType().equals(associationType.getToPrincipalType()))
        {
            jpasm.removeAssociation(from, to, associationType.getAssociationName());
        }
    }
    
    public void beforeRemoveFrom(JetspeedPrincipal from) throws SecurityException
    {
        // by default nothing to do
        // use isSynchronizing() to turn off constraint checks        
    }

    @SuppressWarnings("unchecked")
    public void beforeRemoveTo(JetspeedPrincipal to) throws SecurityException
    {
        if (associationType.isDependent())
        {
            List<JetspeedPrincipal> fromList = (List<JetspeedPrincipal>)getManagerTo().getAssociatedTo(to.getName(), to.getType(), associationType.getAssociationName());
            for (JetspeedPrincipal from : fromList)
            {
                getManagerFrom().removePrincipal(from);
            }
        }
        else if (associationType.isRequired() && !isSynchronizing())
        {
            if (!getManagerTo().getAssociatedTo(to.getName(), to.getType(), associationType.getAssociationName()).isEmpty())
            {
                throw new SecurityException(SecurityException.PRINCIPAL_ASSOCIATION_REQUIRED.createScoped(to.getType().getName(), to.getName(), associationType.getFromPrincipalType().getName(), associationType.getAssociationName()));
            }
        }
    }
    
    protected boolean isSynchronizing()
    {
        return SynchronizationStateAccess.isSynchronizing();
    }
}
