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

import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.mapping.SecurityEntityManager;
import org.apache.jetspeed.security.mapping.model.SecurityEntityRelationType;
import org.apache.jetspeed.security.spi.JetspeedPrincipalAssociationStorageManager;

/**
 * @author <a href="mailto:vkumar@apache.org">Vivek Kumar</a>
 * @version $Id$
 */
public class JetspeedPrincipalLdapAssociationStorageManager implements JetspeedPrincipalAssociationStorageManager
{
    private SecurityEntityManager ldapEntityManager;
    private JetspeedPrincipalAssociationStorageManager databaseStorageManager;

    /**
     * @param ldapEntityManager
     */
    public JetspeedPrincipalLdapAssociationStorageManager(JetspeedPrincipalAssociationStorageManager databaseStorageMngr,
                                                          SecurityEntityManager ldapEntityManager)
    {
        this.ldapEntityManager = ldapEntityManager;
        this.databaseStorageManager = databaseStorageMngr;
    }

    public void addAssociation(JetspeedPrincipal from, JetspeedPrincipal to, String associationName) throws SecurityException
    {
        if (!SynchronizationStateAccess.isSynchronizing() && !ldapEntityManager.isReadOnly())
        {
            SecurityEntityRelationType relationType = ldapEntityManager.getSupportedEntityRelationType(associationName, from.getType().getName(), to.getType().getName());
            if (relationType != null && relationType.isCreateAllowed())
            {
                ldapEntityManager.addRelation(from.getName(), to.getName(), relationType);
            }
        }
        databaseStorageManager.addAssociation(from, to, associationName);
    }

    public void removeAssociation(JetspeedPrincipal from, JetspeedPrincipal to, String associationName) throws SecurityException
    {
        if (!SynchronizationStateAccess.isSynchronizing() && !ldapEntityManager.isReadOnly())
        {
            SecurityEntityRelationType relationType = ldapEntityManager.getSupportedEntityRelationType(associationName, from.getType().getName(), to.getType().getName());
            if (relationType != null && relationType.isRemoveAllowed())
            {
                ldapEntityManager.removeRelation(from.getName(), to.getName(), relationType);
            }
        }
        databaseStorageManager.removeAssociation(from, to, associationName);
    }
}
