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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.jetspeed.components.dao.InitablePersistenceBrokerDaoSupport;
import org.apache.jetspeed.i18n.KeyedMessage;
import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalAssociationReference;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.PrincipalAlreadyExistsException;
import org.apache.jetspeed.security.PrincipalNotFoundException;
import org.apache.jetspeed.security.PrincipalNotRemovableException;
import org.apache.jetspeed.security.PrincipalUpdateException;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.impl.PersistentJetspeedPrincipal;
import org.apache.jetspeed.security.spi.JetspeedPermissionStorageManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalAccessManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalAssociationStorageManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalStorageManager;
import org.apache.jetspeed.security.spi.UserPasswordCredentialAccessManager;
import org.apache.jetspeed.security.spi.UserPasswordCredentialStorageManager;
import org.apache.ojb.broker.Identity;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.apache.ojb.broker.util.collections.ManageableArrayList;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * @version $Id$
 */
public class JetspeedSecurityPersistenceManager
    extends InitablePersistenceBrokerDaoSupport
    implements JetspeedPrincipalAccessManager,
                JetspeedPrincipalStorageManager, UserPasswordCredentialStorageManager, UserPasswordCredentialAccessManager,
                JetspeedPrincipalAssociationStorageManager, JetspeedPermissionStorageManager
{
    public JetspeedSecurityPersistenceManager(String repositoryPath)
    {
        super(repositoryPath);
    }
    
    public boolean principalExists(JetspeedPrincipal principal)
    {
        if (principal.getId() == null)
        {
            return principalExists(principal.getName(), principal.getType());
        }
        Criteria criteria = new Criteria();
        criteria.addEqualTo("id", principal.getId());
        Query query = QueryFactory.newQuery(principal.getType().getPrincipalClass(),criteria);
        return getPersistenceBroker(true).getCount(query) == 1;
    }

    //
    // JetspeedPrincipalAccessManager interface implementation
    //
    public List<JetspeedPrincipal> getAssociatedFrom(String principalFromName, JetspeedPrincipalType from, JetspeedPrincipalType to, String associationName)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("associationsTo.associationName", associationName);
        criteria.addEqualTo("associationsTo.from.name", principalFromName);
        Query query = QueryFactory.newQuery(to.getPrincipalClass(), criteria);
        return (List<JetspeedPrincipal>) getPersistenceBroker(true).getCollectionByQuery(ManageableArrayList.class, query);
    }

    public List<JetspeedPrincipal> getAssociatedTo(String principalToName, JetspeedPrincipalType from, JetspeedPrincipalType to, String associationName)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("associationsFrom.associationName", associationName);
        criteria.addEqualTo("associationsFrom.to.name", principalToName);
        Query query = QueryFactory.newQuery(from.getPrincipalClass(), criteria);
        return (List<JetspeedPrincipal>) getPersistenceBroker(true).getCollectionByQuery(ManageableArrayList.class, query);
    }

    public List<JetspeedPrincipal> getAssociatedFrom(Long principalFromId, JetspeedPrincipalType from, JetspeedPrincipalType to, String associationName)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("associationsTo.associationName", associationName);
        criteria.addEqualTo("associationsTo.from.id", principalFromId);
        Query query = QueryFactory.newQuery(to.getPrincipalClass(), criteria);
        return (List<JetspeedPrincipal>) getPersistenceBroker(true).getCollectionByQuery(ManageableArrayList.class, query);
    }

    public List<JetspeedPrincipal> getAssociatedTo(Long principalToId, JetspeedPrincipalType from, JetspeedPrincipalType to, String associationName)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("associationsFrom.associationName", associationName);
        criteria.addEqualTo("associationsFrom.to.id", principalToId);
        Query query = QueryFactory.newQuery(from.getPrincipalClass(), criteria);
        return (List<JetspeedPrincipal>) getPersistenceBroker(true).getCollectionByQuery(ManageableArrayList.class, query);
    }

    public List<String> getAssociatedNamesFrom(String principalFromName, JetspeedPrincipalType from, JetspeedPrincipalType to, String associationName)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("associationsTo.associationName", associationName);
        criteria.addEqualTo("associationsTo.from.name", principalFromName);
        ReportQueryByCriteria query = QueryFactory.newReportQuery(to.getPrincipalClass(), criteria);
        query.setAttributes(new String[]{"name"});
        ArrayList<String> names = new ArrayList<String>();
        for (Iterator<Object[]> iter = getPersistenceBroker(true).getReportQueryIteratorByQuery(query); iter.hasNext(); )
        {
            names.add((String)iter.next()[0]);
        }
        return names;
    }

    public List<String> getAssociatedNamesFrom(Long principalFromId, JetspeedPrincipalType from, JetspeedPrincipalType to, String associationName)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("associationsTo.associationName", associationName);
        criteria.addEqualTo("associationsTo.from.id", principalFromId);
        ReportQueryByCriteria query = QueryFactory.newReportQuery(to.getPrincipalClass(), criteria);
        query.setAttributes(new String[]{"name"});
        ArrayList<String> names = new ArrayList<String>();
        for (Iterator<Object[]> iter = getPersistenceBroker(true).getReportQueryIteratorByQuery(query); iter.hasNext(); )
        {
            names.add((String)iter.next()[0]);
        }
        return names;
    }

    public List<String> getAssociatedNamesTo(String principalToName, JetspeedPrincipalType from, JetspeedPrincipalType to, String associationName)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("associationsFrom.associationName", associationName);
        criteria.addEqualTo("associationsFrom.to.name", principalToName);
        ReportQueryByCriteria query = QueryFactory.newReportQuery(from.getPrincipalClass(), criteria);
        query.setAttributes(new String[]{"name"});
        ArrayList<String> names = new ArrayList<String>();
        for (Iterator<Object[]> iter = getPersistenceBroker(true).getReportQueryIteratorByQuery(query); iter.hasNext(); )
        {
            names.add((String)iter.next()[0]);
        }
        return names;
    }

    public List<String> getAssociatedNamesTo(Long principalToId, JetspeedPrincipalType from, JetspeedPrincipalType to, String associationName)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("associationsFrom.associationName", associationName);
        criteria.addEqualTo("associationsFrom.to.id", principalToId);
        ReportQueryByCriteria query = QueryFactory.newReportQuery(from.getPrincipalClass(), criteria);
        query.setAttributes(new String[]{"name"});
        ArrayList<String> names = new ArrayList<String>();
        for (Iterator<Object[]> iter = getPersistenceBroker(true).getReportQueryIteratorByQuery(query); iter.hasNext(); )
        {
            names.add((String)iter.next()[0]);
        }
        return names;
    }

    public JetspeedPrincipal getPrincipal(Long id)
    {        
        Identity oid = getPersistenceBroker(true).serviceIdentity().buildIdentity(PersistentJetspeedPrincipal.class, id);
        return (JetspeedPrincipal)getPersistenceBroker(true).getObjectByIdentity(oid);
    }

    public JetspeedPrincipal getPrincipal(String principalName, JetspeedPrincipalType type)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("name", principalName);
        Query query = QueryFactory.newQuery(type.getPrincipalClass(),criteria);
        return (JetspeedPrincipal)getPersistenceBroker(true).getObjectByQuery(query);
    }

    public List<String> getPrincipalNames(String nameFilter, JetspeedPrincipalType type)
    {
        Criteria criteria = new Criteria();
        if (nameFilter != null && nameFilter.length() > 0)
        {
            criteria.addLike("name", nameFilter+"%");
        }
        ReportQueryByCriteria query = QueryFactory.newReportQuery(type.getPrincipalClass(),criteria);
        query.setAttributes(new String[]{"name"});
        ArrayList<String> names = new ArrayList<String>();
        for (Iterator<Object[]> iter = getPersistenceBroker(true).getReportQueryIteratorByQuery(query); iter.hasNext(); )
        {
            names.add((String)iter.next()[0]);
        }
        return names;
    }

    public List<JetspeedPrincipal> getPrincipals(String nameFilter, JetspeedPrincipalType type)
    {
        Criteria criteria = new Criteria();
        if (nameFilter != null && nameFilter.length() > 0)
        {
            criteria.addLike("name", nameFilter+"%");
        }
        Query query = QueryFactory.newQuery(type.getPrincipalClass(),criteria);
        return (List<JetspeedPrincipal>) getPersistenceBroker(true).getCollectionByQuery(ManageableArrayList.class, query);
    }

    public List<JetspeedPrincipal> getPrincipalsByAttribute(String attributeName, String attributeValue, JetspeedPrincipalType type)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("attributes.name", attributeName);
        criteria.addEqualTo("attributes.value", attributeValue);
        Query query = QueryFactory.newQuery(type.getPrincipalClass(),criteria);
        return (List<JetspeedPrincipal>) getPersistenceBroker(true).getCollectionByQuery(ManageableArrayList.class, query);
    }

    public boolean principalExists(String principalName, JetspeedPrincipalType type)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("name", principalName);
        Query query = QueryFactory.newQuery(type.getPrincipalClass(),criteria);
        return getPersistenceBroker(true).getCount(query) == 1;
    }

    //
    // JetspeedPrincipalStorageManager interface implementation
    //
    public void addPrincipal(JetspeedPrincipal principal, Set<JetspeedPrincipalAssociationReference> associations)
        throws PrincipalAlreadyExistsException
    {
        if (principalExists(principal))
        {
            throw new PrincipalAlreadyExistsException();
        }
        try
        {
            getPersistenceBrokerTemplate().store(principal);
        }
        catch (Exception pbe)
        {
            KeyedMessage msg = SecurityException.UNEXPECTED.create("JetspeedSecurityPersistenceManager",
                                                                   "addPrincipal",
                                                                   pbe.getMessage());
            logger.error(msg, pbe);
            throw new RuntimeException(new SecurityException(msg, pbe));            
        }
        // Note: the (optional) required associations are expected to be stored by the calling JetspeedPrincipalManager
    }

    public boolean isMapped()
    {
        return false;
    }

    public void removePrincipal(JetspeedPrincipal principal) throws PrincipalNotFoundException,
                                                            PrincipalNotRemovableException
    {
        if (!principalExists(principal))
        {
            throw new PrincipalNotFoundException();
        }
        try
        {
            getPersistenceBrokerTemplate().delete(principal);
        }
        catch (Exception pbe)
        {
            if (pbe instanceof DataIntegrityViolationException)
            {
                logger.error(pbe.getMessage(), pbe);
                throw new PrincipalNotRemovableException();
            }
            
            KeyedMessage msg = SecurityException.UNEXPECTED.create("JetspeedSecurityPersistenceManager",
                                                                   "removePrincipal",
                                                                   pbe.getMessage());
            logger.error(msg, pbe);
            throw new RuntimeException(new SecurityException(msg, pbe));            
        }
    }

    public void updatePrincipal(JetspeedPrincipal principal) throws PrincipalUpdateException,
                                                            PrincipalNotFoundException
    {
        if (!principalExists(principal))
        {
            throw new PrincipalNotFoundException();
        }
        try
        {
            getPersistenceBrokerTemplate().store(principal);
        }
        catch (Exception pbe)
        {
            if (pbe instanceof DataIntegrityViolationException)
            {
                logger.error(pbe.getMessage(), pbe);
                throw new PrincipalUpdateException();
            }
            
            KeyedMessage msg = SecurityException.UNEXPECTED.create("JetspeedSecurityPersistenceManager",
                                                                   "removePrincipal",
                                                                   pbe.getMessage());
            logger.error(msg, pbe);
            throw new RuntimeException(new SecurityException(msg, pbe));            
        }
    }

    //
    // UserPasswordCredentialStorageManager interface implementation
    //
    public PasswordCredential getPasswordCredential(User user)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("principalId", user.getId());
        criteria.addEqualTo("type", PasswordCredential.TYPE_CURRENT);
        Query query = QueryFactory.newQuery(PasswordCredentialImpl.class,criteria);
        PasswordCredentialImpl pwc = (PasswordCredentialImpl)getPersistenceBroker(true).getObjectByQuery(query);
        if (pwc != null)
        {
            // store the user by hand as its configured as auto-retrieve="false"
            pwc.setUser(user);
        }
        return pwc;
    }

    public void storePasswordCredential(PasswordCredential credential) throws SecurityException
    {
        if (credential.isNewPasswordSet())
        {
            if (credential.getNewPassword() != null)
            {
                credential.setPassword(credential.getNewPassword().toCharArray(), credential.isPasswordEncoded());                
            }
        }
        getPersistenceBroker(true).store(credential);
    }

    //
    // UserPasswordCredentialAccessManager interface implementation
    //
    /**
     * <p>
     * Retrieves the current PasswordCredential by userName
     * </p>
     * <p>
     * Warning: the User reference is configured with auto-retrieve="false".
     * This is intentionally done to allow retrieving the credential for authentication purposes only
     * so no User is loaded when authentication fails.
     * The user reference can be materialized by OJB using persistenceBroker.retrieveReference(credential, "user").
     * </p>
     */
    public PasswordCredential getPasswordCredential(String userName)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("user.name", userName);
        criteria.addEqualTo("type", PasswordCredential.TYPE_CURRENT);
        Query query = QueryFactory.newQuery(PasswordCredentialImpl.class,criteria);
        PasswordCredentialImpl pwc = (PasswordCredentialImpl)getPersistenceBroker(true).getObjectByQuery(query);
        if (pwc != null)
        {
            // store the userName by hand as the user is configured as auto-retrieve="false"
            pwc.setUserName(userName);
        }
        return pwc;
    }

    public List<PasswordCredential> getHistoricPasswordCredentials(User user)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("principalId", user.getId());
        criteria.addEqualTo("type", PasswordCredential.TYPE_HISTORICAL);
        Query query = QueryFactory.newQuery(PasswordCredentialImpl.class,criteria);
        List<PasswordCredential> list = (List<PasswordCredential>)getPersistenceBroker(true).getCollectionByQuery(ManageableArrayList.class, query);
        for (PasswordCredential pwc : list)
        {
            // store the user by hand as its configured as auto-retrieve="false"
            ((PasswordCredentialImpl)pwc).setUser(user);
        }
        return list;
    }

    //
    // JetspeedPrincipalAssociationStorageManager interface implementation
    //
    public void addAssociation(JetspeedPrincipal from, JetspeedPrincipal to, String associationName)
        throws PrincipalNotFoundException
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("fromPrincipalId", from.getId());
        criteria.addEqualTo("toPrincipalId", to.getId());
        criteria.addEqualTo("associationName", associationName);
        Query query = QueryFactory.newQuery(JetspeedPrincipalAssociation.class,criteria);
        if (getPersistenceBroker(true).getCount(query) == 0)
        {
            try
            {
                getPersistenceBrokerTemplate().store(new JetspeedPrincipalAssociation(from, to, associationName));
            }
            catch (Exception pbe)
            {
                if (pbe instanceof DataIntegrityViolationException)
                {
                    logger.error(pbe.getMessage(), pbe);
                    throw new PrincipalNotFoundException();
                }
                
                KeyedMessage msg = SecurityException.UNEXPECTED.create("JetspeedSecurityPersistenceManager",
                                                                       "addAssociation",
                                                                       pbe.getMessage());
                logger.error(msg, pbe);
                throw new RuntimeException(new SecurityException(msg, pbe));
            }
        }
    }

    public void removeAssociation(JetspeedPrincipal from, JetspeedPrincipal to, String associationName)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("fromPrincipalId", from.getId());
        criteria.addEqualTo("toPrincipalId", to.getId());
        criteria.addEqualTo("associationName", associationName);
        Query query = QueryFactory.newQuery(JetspeedPrincipalAssociation.class,criteria);
        if (getPersistenceBroker(true).getCount(query) != 0)
        {
            try
            {
                getPersistenceBrokerTemplate().delete(new JetspeedPrincipalAssociation(from, to, associationName));
            }
            catch (Exception pbe)
            {
                KeyedMessage msg = SecurityException.UNEXPECTED.create("JetspeedSecurityPersistenceManager",
                                                                       "removeAssociation",
                                                                       pbe.getMessage());
                logger.error(msg, pbe);
                throw new RuntimeException(new SecurityException(msg, pbe));
            }
        }
    }

    //
    // JetspeedPermissionStorageManager interface implementation
    //
    public void addPermission(PersistentJetspeedPermission permission)
    {
        // TODO Auto-generated method stub
    }

    public void grantPermission(PersistentJetspeedPermission permission, JetspeedPrincipal principal)
    {
        // TODO Auto-generated method stub
    }

    public void grantPermissionOnlyTo(PersistentJetspeedPermission permission, List<JetspeedPrincipal> principal)
    {
        // TODO Auto-generated method stub
    }

    public void removePermission(PersistentJetspeedPermission permission)
    {
        // TODO Auto-generated method stub
    }

    public void revokeAllPermissions(JetspeedPrincipal principal)
    {
        // TODO Auto-generated method stub
    }

    public void revokePermission(PersistentJetspeedPermission permission, JetspeedPrincipal principal)
    {
        // TODO Auto-generated method stub
    }

    public void updatePermission(PersistentJetspeedPermission permission, String actions)
    {
        // TODO Auto-generated method stub
    }    
}
