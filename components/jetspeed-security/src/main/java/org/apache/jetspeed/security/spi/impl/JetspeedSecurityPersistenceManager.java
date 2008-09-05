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
import java.util.Set;

import org.apache.jetspeed.security.DependentPrincipalException;
import org.apache.jetspeed.security.JetspeedPermission;
import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalAssociationReference;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.PortalResourcePermission;
import org.apache.jetspeed.security.PrincipalAlreadyExistsException;
import org.apache.jetspeed.security.PrincipalAssociationNotAllowedException;
import org.apache.jetspeed.security.PrincipalAssociationRequiredException;
import org.apache.jetspeed.security.PrincipalAssociationUnsupportedException;
import org.apache.jetspeed.security.PrincipalNotFoundException;
import org.apache.jetspeed.security.PrincipalNotRemovableException;
import org.apache.jetspeed.security.PrincipalUpdateException;
import org.apache.jetspeed.security.spi.JetspeedPermissionStorageManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalAccessManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalAssociationStorageManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalPermissionStorageManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalStorageManager;
import org.apache.jetspeed.security.spi.UserPasswordCredentialStorageManager;

/**
 * @version $Id$
 */
public class JetspeedSecurityPersistenceManager<T extends JetspeedPrincipal> implements JetspeedPrincipalAccessManager<T>,
                JetspeedPrincipalStorageManager, UserPasswordCredentialStorageManager,
                JetspeedPrincipalAssociationStorageManager, JetspeedPrincipalPermissionStorageManager,
                JetspeedPermissionStorageManager
{

    public List<T> getAssociatedFrom(String principalFromName,
            JetspeedPrincipalType from, JetspeedPrincipalType to,
            String associationName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<T> getAssociatedFrom(Long principalFromId,
            JetspeedPrincipalType from, JetspeedPrincipalType to,
            String associationName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<String> getAssociatedNamesFrom(String principalFromName,
            JetspeedPrincipalType from, JetspeedPrincipalType to,
            String associationName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<String> getAssociatedNamesFrom(Long principalFromId,
            JetspeedPrincipalType from, JetspeedPrincipalType to,
            String associationName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<String> getAssociatedNamesTo(String principalToName,
            JetspeedPrincipalType from, JetspeedPrincipalType to,
            String associationName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<String> getAssociatedNamesTo(Long principalToId,
            JetspeedPrincipalType from, JetspeedPrincipalType to,
            String associationName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<T> getAssociatedTo(String principalToName,
            JetspeedPrincipalType from, JetspeedPrincipalType to,
            String associationName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<T> getAssociatedTo(Long principalToId,
            JetspeedPrincipalType from, JetspeedPrincipalType to,
            String associationName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public T getPrincipal(Long id)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public T getPrincipal(String principalName, JetspeedPrincipalType type)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<String> getPrincipalNames(String nameFilter,
            JetspeedPrincipalType type)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<T> getPrincipals(String nameFilter, JetspeedPrincipalType type)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<String> getPrincipals(JetspeedPermission permission)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<String> getPrincipals(JetspeedPermission permission,
            JetspeedPrincipalType type)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<T> getPrincipalsByAttribute(String attributeName,
            String attributeValue, JetspeedPrincipalType type)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean principalExists(String principalName,
            JetspeedPrincipalType type)
    {
        // TODO Auto-generated method stub
        return false;
    }

    public void addPrincipal(JetspeedPrincipal principal,
            Set<JetspeedPrincipalAssociationReference> associations)
            throws PrincipalAssociationNotAllowedException,
            PrincipalAlreadyExistsException,
            PrincipalAssociationRequiredException
    {
        // TODO Auto-generated method stub
        
    }

    public boolean isMapped()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public void removePrincipal(JetspeedPrincipal principal)
            throws PrincipalNotFoundException, PrincipalNotRemovableException,
            DependentPrincipalException
    {
        // TODO Auto-generated method stub
        
    }

    public void updatePrincipal(JetspeedPrincipal principal)
            throws PrincipalUpdateException, PrincipalNotFoundException
    {
        // TODO Auto-generated method stub
        
    }

    public void addCredential(PasswordCredential credential)
    {
        // TODO Auto-generated method stub
        
    }

    public void removeCredential(PasswordCredential credential)
    {
        // TODO Auto-generated method stub
        
    }

    public void updateCredential(PasswordCredential credential)
    {
        // TODO Auto-generated method stub
        
    }

    public void addAssociation(JetspeedPrincipal from, JetspeedPrincipal to,
            String associationName) throws PrincipalNotFoundException,
            PrincipalAssociationUnsupportedException,
            PrincipalAssociationNotAllowedException
    {
        // TODO Auto-generated method stub
        
    }

    public void removeAssociation(JetspeedPrincipal from, JetspeedPrincipal to,
            String associationName) throws PrincipalNotFoundException,
            PrincipalAssociationUnsupportedException,
            PrincipalAssociationRequiredException
    {
        // TODO Auto-generated method stub
        
    }

    public void grantPermission(JetspeedPrincipal principal,
            JetspeedPermission permission)
    {
        // TODO Auto-generated method stub
        
    }

    public void revokeAll(JetspeedPrincipal principal)
    {
        // TODO Auto-generated method stub
        
    }

    public void revokePermission(JetspeedPrincipal principal,
            JetspeedPermission permission)
    {
        // TODO Auto-generated method stub
        
    }

    public void addPermission(PortalResourcePermission p)
    {
        // TODO Auto-generated method stub
        
    }

    public void removePermission(JetspeedPermission p)
    {
        // TODO Auto-generated method stub
        
    }

    public void updatePermission(JetspeedPermission p)
    {
        // TODO Auto-generated method stub
        
    }
}
