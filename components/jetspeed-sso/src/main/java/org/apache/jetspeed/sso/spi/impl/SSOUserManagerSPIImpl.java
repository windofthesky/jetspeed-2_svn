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
package org.apache.jetspeed.sso.spi.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalAssociationReference;
import org.apache.jetspeed.security.JetspeedPrincipalAssociationType;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.SecurityDomain;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.impl.BaseJetspeedPrincipalManager;
import org.apache.jetspeed.security.spi.JetspeedDomainPrincipalAccessManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalAccessManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalStorageManager;
import org.apache.jetspeed.security.spi.SecurityDomainAccessManager;
import org.apache.jetspeed.security.spi.UserPasswordCredentialAccessManager;
import org.apache.jetspeed.security.spi.UserPasswordCredentialStorageManager;
import org.apache.jetspeed.sso.SSOUser;
import org.apache.jetspeed.sso.impl.IsOwnedByPrincipalAssociationHandler;
import org.apache.jetspeed.sso.impl.IsRemoteIdentityForPrincipalAssociationHandler;
import org.apache.jetspeed.sso.impl.SSOUserImpl;
import org.apache.jetspeed.sso.spi.SSOUserManagerSPI;


/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public class SSOUserManagerSPIImpl extends BaseJetspeedPrincipalManager implements SSOUserManagerSPI
{

    private static final long serialVersionUID = 1L;

    private JetspeedDomainPrincipalAccessManager domainPrincipalAccess;
    private SecurityDomainAccessManager domainAccess;
    
    private UserPasswordCredentialStorageManager credentialStorageManager;
    private UserPasswordCredentialAccessManager credentialAccessManager;

    
    public SSOUserManagerSPIImpl(JetspeedPrincipalType principalType, 
            JetspeedPrincipalAccessManager jpam, JetspeedPrincipalStorageManager jpsm, UserPasswordCredentialStorageManager credentialStorageManager,
            UserPasswordCredentialAccessManager credentialAccessManager, JetspeedDomainPrincipalAccessManager domainPrincipalAccess, SecurityDomainAccessManager domainAccess)
    {
        super(principalType, jpam, jpsm);
        this.credentialStorageManager = credentialStorageManager;
        this.credentialAccessManager = credentialAccessManager;
        this.domainPrincipalAccess=domainPrincipalAccess;
        this.domainAccess=domainAccess;
    }
    
    public JetspeedPrincipal newPrincipal(String name, boolean mapped)
    {
        return null;
    }

    public JetspeedPrincipal newTransientPrincipal(String name)
    {
        return null;
    }

    public SSOUser addUser(String name, Long domainId, JetspeedPrincipal ownerPrincipal)
            throws SecurityException
    {
        SSOUser user = newUser(name, domainId);
        Set<JetspeedPrincipalAssociationReference> reqAssociations = new HashSet<JetspeedPrincipalAssociationReference>();
        reqAssociations.add(new JetspeedPrincipalAssociationReference(JetspeedPrincipalAssociationReference.Type.TO, ownerPrincipal, IsOwnedByPrincipalAssociationHandler.ASSOCIATION_NAME));
        super.addPrincipal(user, reqAssociations);
        // by default add a relation to the owner 
        super.addAssociation(user, ownerPrincipal, IsRemoteIdentityForPrincipalAssociationHandler.ASSOCIATION_NAME);
        return user;
    }

    public PasswordCredential getPasswordCredential(SSOUser user)
            throws SecurityException
    {
        return credentialAccessManager.getPasswordCredential(user.getName(),user.getDomainId());
    }

    public SSOUser getUser(String userName, Long domainId)
            throws SecurityException
    {
        SSOUser user = (SSOUser)domainPrincipalAccess.getPrincipal(userName, getPrincipalType(), domainId);
        if (null == user)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.SSO_USER, userName));
        }
        return user;
    }

    public Collection<String> getUserNames(String nameFilter, Long domainId)
            throws SecurityException
    {
        return (List<String>) domainPrincipalAccess.getPrincipalNames(nameFilter, getPrincipalType(), domainId);
    }

    protected List<? extends JetspeedPrincipal> getPrincipals(String nameFilter, Long domainId)
    {
        return domainPrincipalAccess.getPrincipals(nameFilter, getPrincipalType(), domainId);
    }
    
    @SuppressWarnings("unchecked")
    public Collection<SSOUser> getUsers(String nameFilter, Long domainId)
            throws SecurityException
    {
        return (List<SSOUser>) getPrincipals(nameFilter, domainId);
    }

    public SSOUser newUser(String name, Long domainId)
    {
        SSOUserImpl user = new SSOUserImpl();
        user.setDomainId(domainId);
        user.setName(name);
        return user;
    }

    public void removeUser(String userName, Long domainId)
            throws SecurityException
    {
        JetspeedPrincipal user;        
        user = getUser(userName, domainId);
        super.removePrincipal(user);
    }

    public void storePasswordCredential(PasswordCredential credential) throws SecurityException
    {
        credentialStorageManager.storePasswordCredential(credential);
    }

    public void updateUser(SSOUser user) throws SecurityException
    {
        super.updatePrincipal(user);
    }

    public boolean userExists(String userName, Long domainId)
    {
        return domainPrincipalAccess.principalExists(userName, getPrincipalType(), domainId);
    }

    public Collection<SSOUser> getUsers(JetspeedPrincipal principal) throws SecurityException
    {
        Collection<SecurityDomain> allDomains = domainAccess.getDomainsOwnedBy(principal.getDomainId());
        Collection<SSOUser> users = new ArrayList<SSOUser>();
        for (SecurityDomain domain : allDomains){
            users.addAll(getUsers(principal,domain.getDomainId()));
        }
        return users;
    }    
    
    @SuppressWarnings("unchecked")
    public Collection<SSOUser> getUsers(JetspeedPrincipal principal, Long remoteSecurityDomain) throws SecurityException
    {
        return (Collection<SSOUser>)_getSSOUsersForPrincipal(principal,remoteSecurityDomain);
    }    

    private Collection<? extends JetspeedPrincipal> _getSSOUsersForPrincipal(JetspeedPrincipal principal, Long remoteSecurityDomain) throws SecurityException
    {
        return domainPrincipalAccess.getAssociatedTo(principal.getName(), getPrincipalType(), principal.getType(), IsRemoteIdentityForPrincipalAssociationHandler.ASSOCIATION_NAME, remoteSecurityDomain, principal.getDomainId());
    }  
    
    public void addSSOUserToPrincipal(SSOUser user, JetspeedPrincipal principal)
            throws SecurityException
    {
        super.addAssociation(user,principal,IsRemoteIdentityForPrincipalAssociationHandler.ASSOCIATION_NAME);
        
    }

    public Collection<JetspeedPrincipal> getPortalPrincipals(SSOUser remoteUser, Long portalPrincipalDomain)
    {
        Collection<JetspeedPrincipal> principalsFound = new ArrayList<JetspeedPrincipal>();
        List<JetspeedPrincipalAssociationType> assTypes = super.getAssociationTypes();
        for (JetspeedPrincipalAssociationType type : assTypes)
        {
            if (type.getAssociationName().equals(IsRemoteIdentityForPrincipalAssociationHandler.ASSOCIATION_NAME)){
                Collection<JetspeedPrincipal> principalsForThisType = domainPrincipalAccess.getAssociatedFrom(remoteUser.getName(), getPrincipalType(), type.getToPrincipalType(), IsRemoteIdentityForPrincipalAssociationHandler.ASSOCIATION_NAME, remoteUser.getDomainId(), portalPrincipalDomain);
                principalsFound.addAll(principalsForThisType);
            }
        }
        return principalsFound;
    }    
    
}
