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

import java.sql.SQLException;
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
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.impl.PersistentJetspeedPrincipal;
import org.apache.jetspeed.security.spi.JetspeedPermissionStorageManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalAccessManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalAssociationStorageManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalStorageManager;
import org.apache.jetspeed.security.spi.UserPasswordCredentialAccessManager;
import org.apache.jetspeed.security.spi.UserPasswordCredentialStorageManager;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.apache.ojb.broker.accesslayer.LookupException;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.apache.ojb.broker.util.collections.ManageableArrayList;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ojb.PersistenceBrokerCallback;

/**
 * @version $Id$
 */
public class JetspeedSecurityPersistenceManager
    extends InitablePersistenceBrokerDaoSupport
    implements JetspeedPrincipalAccessManager,
                JetspeedPrincipalStorageManager, UserPasswordCredentialStorageManager, UserPasswordCredentialAccessManager,
                JetspeedPrincipalAssociationStorageManager, JetspeedPermissionStorageManager
{
    private static class ManagedListByQueryCallback implements PersistenceBrokerCallback
    {
        private Query query;
        
        public ManagedListByQueryCallback(Query query)
        {
            this.query = query;
        }
        public Object doInPersistenceBroker(PersistenceBroker pb) throws PersistenceBrokerException, LookupException,
                SQLException
        {
            return pb.getCollectionByQuery(ManageableArrayList.class, query);
        }
    }
    
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
        criteria.addEqualTo("type", principal.getType().getName());
        Query query = QueryFactory.newQuery(PersistentJetspeedPrincipal.class,criteria);
        return getPersistenceBrokerTemplate().getCount(query) == 1;
    }

    //
    // JetspeedPrincipalAccessManager interface implementation
    //
    public List<JetspeedPrincipal> getAssociatedFrom(String principalFromName, JetspeedPrincipalType from, JetspeedPrincipalType to, String associationName)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("associationsTo.associationName", associationName);
        criteria.addEqualTo("associationsTo.from.name", principalFromName);
        criteria.addEqualTo("type", to.getName());
        Query query = QueryFactory.newQuery(PersistentJetspeedPrincipal.class, criteria);
        return (List<JetspeedPrincipal>) getPersistenceBrokerTemplate().execute(new ManagedListByQueryCallback(query));
    }

    public List<JetspeedPrincipal> getAssociatedTo(String principalToName, JetspeedPrincipalType from, JetspeedPrincipalType to, String associationName)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("associationsFrom.associationName", associationName);
        criteria.addEqualTo("associationsFrom.to.name", principalToName);
        criteria.addEqualTo("type", from.getName());
        Query query = QueryFactory.newQuery(PersistentJetspeedPrincipal.class, criteria);
        return (List<JetspeedPrincipal>) getPersistenceBrokerTemplate().execute(new ManagedListByQueryCallback(query));
    }

    public List<JetspeedPrincipal> getAssociatedFrom(Long principalFromId, JetspeedPrincipalType from, JetspeedPrincipalType to, String associationName)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("associationsTo.associationName", associationName);
        criteria.addEqualTo("associationsTo.from.id", principalFromId);
        criteria.addEqualTo("type", to.getName());
        Query query = QueryFactory.newQuery(PersistentJetspeedPrincipal.class, criteria);
        return (List<JetspeedPrincipal>) getPersistenceBrokerTemplate().execute(new ManagedListByQueryCallback(query));
    }

    public List<JetspeedPrincipal> getAssociatedTo(Long principalToId, JetspeedPrincipalType from, JetspeedPrincipalType to, String associationName)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("associationsFrom.associationName", associationName);
        criteria.addEqualTo("associationsFrom.to.id", principalToId);
        criteria.addEqualTo("type", from.getName());
        Query query = QueryFactory.newQuery(PersistentJetspeedPrincipal.class, criteria);
        return (List<JetspeedPrincipal>) getPersistenceBrokerTemplate().execute(new ManagedListByQueryCallback(query));
    }

    public List<String> getAssociatedNamesFrom(String principalFromName, JetspeedPrincipalType from, JetspeedPrincipalType to, String associationName)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("associationsTo.associationName", associationName);
        criteria.addEqualTo("associationsTo.from.name", principalFromName);
        criteria.addEqualTo("type", to.getName());
        ReportQueryByCriteria query = QueryFactory.newReportQuery(PersistentJetspeedPrincipal.class, criteria);
        query.setAttributes(new String[]{"name"});
        ArrayList<String> names = new ArrayList<String>();
        for (Iterator<Object[]> iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query); iter.hasNext(); )
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
        criteria.addEqualTo("type", to.getName());
        ReportQueryByCriteria query = QueryFactory.newReportQuery(PersistentJetspeedPrincipal.class, criteria);
        query.setAttributes(new String[]{"name"});
        ArrayList<String> names = new ArrayList<String>();
        for (Iterator<Object[]> iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query); iter.hasNext(); )
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
        criteria.addEqualTo("type", from.getName());
        ReportQueryByCriteria query = QueryFactory.newReportQuery(PersistentJetspeedPrincipal.class, criteria);
        query.setAttributes(new String[]{"name"});
        ArrayList<String> names = new ArrayList<String>();
        for (Iterator<Object[]> iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query); iter.hasNext(); )
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
        criteria.addEqualTo("type", from.getName());
        ReportQueryByCriteria query = QueryFactory.newReportQuery(PersistentJetspeedPrincipal.class, criteria);
        query.setAttributes(new String[]{"name"});
        ArrayList<String> names = new ArrayList<String>();
        for (Iterator<Object[]> iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query); iter.hasNext(); )
        {
            names.add((String)iter.next()[0]);
        }
        return names;
    }

    public JetspeedPrincipal getPrincipal(Long id)
    {        
        return (JetspeedPrincipal)getPersistenceBrokerTemplate().getObjectById(PersistentJetspeedPrincipal.class, id);
    }

    public JetspeedPrincipal getPrincipal(String principalName, JetspeedPrincipalType type)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("name", principalName);
        criteria.addEqualTo("type", type.getName());
        Query query = QueryFactory.newQuery(PersistentJetspeedPrincipal.class,criteria);
        return (JetspeedPrincipal)getPersistenceBrokerTemplate().getObjectByQuery(query);
    }

    public List<String> getPrincipalNames(String nameFilter, JetspeedPrincipalType type)
    {
        Criteria criteria = new Criteria();
        if (nameFilter != null && nameFilter.length() > 0)
        {
            criteria.addLike("name", nameFilter+"%");
        }
        criteria.addEqualTo("type", type.getName());
        ReportQueryByCriteria query = QueryFactory.newReportQuery(PersistentJetspeedPrincipal.class,criteria);
        query.setAttributes(new String[]{"name"});
        ArrayList<String> names = new ArrayList<String>();
        for (Iterator<Object[]> iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query); iter.hasNext(); )
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
        criteria.addEqualTo("type", type.getName());
        Query query = QueryFactory.newQuery(PersistentJetspeedPrincipal.class,criteria);
        return (List<JetspeedPrincipal>) getPersistenceBrokerTemplate().execute(new ManagedListByQueryCallback(query));
    }

    public List<JetspeedPrincipal> getPrincipalsByAttribute(String attributeName, String attributeValue, JetspeedPrincipalType type)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("attributes.name", attributeName);
        criteria.addEqualTo("attributes.value", attributeValue);
        criteria.addEqualTo("type", type.getName());
        Query query = QueryFactory.newQuery(PersistentJetspeedPrincipal.class,criteria);
        return (List<JetspeedPrincipal>) getPersistenceBrokerTemplate().execute(new ManagedListByQueryCallback(query));
    }

    public boolean principalExists(String principalName, JetspeedPrincipalType type)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("name", principalName);
        criteria.addEqualTo("type", type.getName());
        Query query = QueryFactory.newQuery(PersistentJetspeedPrincipal.class,criteria);
        return getPersistenceBrokerTemplate().getCount(query) == 1;
    }

    //
    // JetspeedPrincipalStorageManager interface implementation
    //
    public void addPrincipal(JetspeedPrincipal principal, Set<JetspeedPrincipalAssociationReference> associations)
        throws SecurityException
    {
        if (principalExists(principal))
        {
            throw new SecurityException(SecurityException.PRINCIPAL_ALREADY_EXISTS.createScoped(principal.getType().getName(), principal.getName()));
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

    public void removePrincipal(JetspeedPrincipal principal) throws SecurityException
    
    {
        if (!principalExists(principal))
        {
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(principal.getType().getName(), principal.getName()));
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
                throw new SecurityException(SecurityException.PRINCIPAL_NOT_REMOVABLE.createScoped(principal.getType().getName(), principal.getName()));
            }
            
            KeyedMessage msg = SecurityException.UNEXPECTED.create("JetspeedSecurityPersistenceManager",
                                                                   "removePrincipal",
                                                                   pbe.getMessage());
            logger.error(msg, pbe);
            throw new RuntimeException(new SecurityException(msg, pbe));            
        }
    }

    public void updatePrincipal(JetspeedPrincipal principal) throws SecurityException
                                                            
    {
        if (!principalExists(principal))
        {
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(principal.getType().getName(), principal.getName()));
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
                throw new SecurityException(SecurityException.PRINCIPAL_UPDATE_FAILURE.createScoped(principal.getType().getName(), principal.getName()));
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
        PasswordCredentialImpl pwc = (PasswordCredentialImpl)getPersistenceBrokerTemplate().getObjectByQuery(query);
        if (pwc == null)
        {
            pwc = new PasswordCredentialImpl();
        }
        // store the user by hand as its configured as auto-retrieve="false"
        pwc.setUser(user);
        return pwc;
    }

    public void storePasswordCredential(PasswordCredential credential) throws SecurityException
    {
        if (credential.isNewPasswordSet())
        {
            if (credential.getNewPassword() != null)
            {
                credential.setPassword(credential.getNewPassword(), credential.isEncoded());                
            }
        }
        getPersistenceBrokerTemplate().store(credential);
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
     * The user reference can be materialized by calling {@link #loadPasswordCredentialUser(PasswordCredential)}.
     * </p>
     */
    public PasswordCredential getPasswordCredential(String userName)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("user.name", userName);
        criteria.addEqualTo("type", PasswordCredential.TYPE_CURRENT);
        Query query = QueryFactory.newQuery(PasswordCredentialImpl.class,criteria);
        PasswordCredentialImpl pwc = (PasswordCredentialImpl)getPersistenceBrokerTemplate().getObjectByQuery(query);
        if (pwc != null)
        {
            // store the userName by hand as the user is configured as auto-retrieve="false"
            pwc.setUserName(userName);
        }
        return pwc;
    }
    
    public void loadPasswordCredentialUser(final PasswordCredential credential)
    {
        if (credential.getUser() == null)
        {
            getPersistenceBrokerTemplate().execute(
                    new PersistenceBrokerCallback()
                    { 
                        public Object doInPersistenceBroker(PersistenceBroker pb) throws PersistenceBrokerException
                        {
                            pb.retrieveReference(credential, "user");
                            return null;
                        }
                    }
            );
        }
    }

    public List<PasswordCredential> getHistoricPasswordCredentials(User user)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("principalId", user.getId());
        criteria.addEqualTo("type", PasswordCredential.TYPE_HISTORICAL);
        Query query = QueryFactory.newQuery(PasswordCredentialImpl.class,criteria);
        List<PasswordCredential> list = (List<PasswordCredential>)getPersistenceBrokerTemplate().execute(new ManagedListByQueryCallback(query));
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
        throws SecurityException
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("fromPrincipalId", from.getId());
        criteria.addEqualTo("toPrincipalId", to.getId());
        criteria.addEqualTo("associationName", associationName);
        Query query = QueryFactory.newQuery(JetspeedPrincipalAssociation.class,criteria);
        if (getPersistenceBrokerTemplate().getCount(query) == 0)
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
                    throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(from.getType().getName(), from.getName()));
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
        if (getPersistenceBrokerTemplate().getCount(query) != 0)
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
