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

import java.util.Set;

import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalAssociationReference;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.mapping.EntityFactory;
import org.apache.jetspeed.security.mapping.SecurityEntityManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalStorageManager;

/**
 * @author <a href="mailto:vkumar@apache.org">Vivek Kumar</a>
 * @version $Id$
 */
public class JetspeedPrincipalLdapStorageManager implements JetspeedPrincipalStorageManager
{
    private SecurityEntityManager ldapEntityManager;
    private JetspeedPrincipalStorageManager delegateJpsm;

    public JetspeedPrincipalLdapStorageManager(JetspeedPrincipalStorageManager databaseStorage, SecurityEntityManager ldapEntityManager)
    {
        this.delegateJpsm = databaseStorage;
        this.ldapEntityManager = ldapEntityManager;
    }

    public void addPrincipal(JetspeedPrincipal principal, Set<JetspeedPrincipalAssociationReference> associations) throws SecurityException
    {
        if (!SynchronizationStateAccess.isSynchronizing() && !ldapEntityManager.isReadOnly())
        {
            EntityFactory entityFactory = ldapEntityManager.getEntityFactory(principal.getType().getName());
            if (entityFactory.isCreateAllowed())
            {
                ldapEntityManager.addEntity(entityFactory.createEntity(principal));
            }
        }
        delegateJpsm.addPrincipal(principal, associations);
    }

    public boolean isMapped()
    {
        return false;
    }

    public void removePrincipal(JetspeedPrincipal principal) throws SecurityException
    {
        if (!SynchronizationStateAccess.isSynchronizing() && !ldapEntityManager.isReadOnly())
        {
            EntityFactory entityFactory = ldapEntityManager.getEntityFactory(principal.getType().getName());
            if (entityFactory.isRemoveAllowed())
            {
                ldapEntityManager.removeEntity(entityFactory.createEntity(principal));
            }
        }
        delegateJpsm.removePrincipal(principal);
    }

    public void updatePrincipal(JetspeedPrincipal principal) throws SecurityException
    {
        if (!SynchronizationStateAccess.isSynchronizing() && !ldapEntityManager.isReadOnly())
        {
            EntityFactory entityFactory = ldapEntityManager.getEntityFactory(principal.getType().getName());
            if (entityFactory.isUpdateAllowed())
            {
                ldapEntityManager.updateEntity(entityFactory.createEntity(principal));
            }
        }
        delegateJpsm.updatePrincipal(principal);
    }
}
