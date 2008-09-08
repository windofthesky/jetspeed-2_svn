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
import org.apache.jetspeed.security.JetspeedPermission;
import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalAssociationReference;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.PortalResourcePermission;
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
import org.apache.jetspeed.security.spi.JetspeedPrincipalPermissionStorageManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalStorageManager;
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
                JetspeedPrincipalStorageManager, UserPasswordCredentialStorageManager,
                JetspeedPrincipalAssociationStorageManager, JetspeedPrincipalPermissionStorageManager,
                JetspeedPermissionStorageManager
{
    public JetspeedSecurityPersistenceManager(String repositoryPath)
    {
        super(repositoryPath);
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
        return (JetspeedPrincipal)getPersistenceBrokerTemplate().getObjectByQuery(query);
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

    public List<String> getPrincipals(JetspeedPermission permission)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<String> getPrincipals(JetspeedPermission permission, JetspeedPrincipalType type)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean principalExists(String principalName, JetspeedPrincipalType type)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("name", principalName);
        Query query = QueryFactory.newQuery(type.getPrincipalClass(),criteria);
        return getPersistenceBroker(true).getCount(query) == 1;
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
    // JetspeedPrincipalStorageManager interface implementation
    //
    public PasswordCredential getPasswordCredential(User user)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void storePasswordCredential(PasswordCredential credential) throws SecurityException
    {
        // TODO Auto-generated method stub
        
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
    // JetspeedPrincipalPermissionStorageManager interface implementation
    //
    public void grantPermission(JetspeedPrincipal principal, JetspeedPermission permission)
    {
        // TODO Auto-generated method stub
    }

    public void revokeAll(JetspeedPrincipal principal)
    {
        // TODO Auto-generated method stub
    }

    public void revokePermission(JetspeedPrincipal principal, JetspeedPermission permission)
    {
        // TODO Auto-generated method stub
    }

    //
    // JetspeedPermissionStorageManager interface implementation
    //
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
