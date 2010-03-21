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

import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.jetspeed.components.dao.InitablePersistenceBrokerDaoSupport;
import org.apache.jetspeed.i18n.KeyedMessage;
import org.apache.jetspeed.security.JetspeedPermission;
import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalAssociationReference;
import org.apache.jetspeed.security.JetspeedPrincipalQueryContext;
import org.apache.jetspeed.security.JetspeedPrincipalResultList;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.SecurityDomain;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.impl.PersistentJetspeedPrincipal;
import org.apache.jetspeed.security.impl.SecurityDomainImpl;
import org.apache.jetspeed.security.impl.TransientJetspeedPrincipal;
import org.apache.jetspeed.security.spi.JetspeedDomainPrincipalAccessManager;
import org.apache.jetspeed.security.spi.JetspeedPermissionAccessManager;
import org.apache.jetspeed.security.spi.JetspeedPermissionStorageManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalAccessManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalAssociationStorageManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalLookupManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalStorageManager;
import org.apache.jetspeed.security.spi.PersistentJetspeedPermission;
import org.apache.jetspeed.security.spi.SecurityDomainAccessManager;
import org.apache.jetspeed.security.spi.SecurityDomainStorageManager;
import org.apache.jetspeed.security.spi.UserPasswordCredentialAccessManager;
import org.apache.jetspeed.security.spi.UserPasswordCredentialStorageManager;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.apache.ojb.broker.accesslayer.LookupException;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.apache.ojb.broker.util.collections.ManageableArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.orm.ojb.PersistenceBrokerCallback;

/**
 * @version $Id$
 */
public class JetspeedSecurityPersistenceManager
    extends InitablePersistenceBrokerDaoSupport
    implements Serializable,JetspeedPrincipalAccessManager,
                JetspeedPrincipalStorageManager, JetspeedDomainPrincipalAccessManager, UserPasswordCredentialStorageManager, UserPasswordCredentialAccessManager,
                JetspeedPrincipalAssociationStorageManager, JetspeedPermissionAccessManager, JetspeedPermissionStorageManager, 
                SecurityDomainStorageManager, SecurityDomainAccessManager
{
    private static final long serialVersionUID = -2689340557699526023L;
	
    static final Logger log = LoggerFactory.getLogger(JetspeedSecurityPersistenceManager.class);
    
    private Long defaultSecurityDomainId;
    
    private JetspeedPrincipalLookupManagerFactory jpplf = null;
    
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
    
	public JetspeedSecurityPersistenceManager(String repositoryPath, JetspeedPrincipalLookupManagerFactory lookupManagerFactory)
    {
        super(repositoryPath);
        this.jpplf = lookupManagerFactory;
        
    }
    
    @SuppressWarnings("unchecked")
    protected Long getPrincipalId(String name, String type, Long domainId) throws SecurityException
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("name", name);
        criteria.addEqualTo("type", type);
        criteria.addEqualTo("domainId", domainId);
        ReportQueryByCriteria query = QueryFactory.newReportQuery(PersistentJetspeedPrincipal.class, criteria);
        query.setAttributes(new String[]{"id"});
        // need to force OJB to return a Long, otherwise it'll return a Integer causing a CCE
        query.setJdbcTypes(new int[]{Types.BIGINT});
        for (Iterator<Object[]> iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query); iter.hasNext(); )
        {
            return (Long)iter.next()[0];
        }
        throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(type, name));
    }
    
	public boolean principalExists(JetspeedPrincipal principal)
    {
        if (principal.getId() == null)
        {
            if (principal.getDomainId() != null){
                return principalExists(principal.getName(), principal.getType(), principal.getDomainId());    
            } else {
                return principalExists(principal.getName(), principal.getType());
            }
            
        }
        Criteria criteria = new Criteria();
        criteria.addEqualTo("id", principal.getId());
        criteria.addEqualTo("type", principal.getType().getName());
        criteria.addEqualTo("domainId", principal.getDomainId());
        Query query = QueryFactory.newQuery(PersistentJetspeedPrincipal.class,criteria);
        return getPersistenceBrokerTemplate().getCount(query) == 1;
    }

	public List<JetspeedPrincipal> getAssociatedFrom(String principalFromName, JetspeedPrincipalType from, JetspeedPrincipalType to, String associationName){
	    Long defaultDomainId = getDefaultSecurityDomainId();
	    return getAssociatedFrom(principalFromName, from, to, associationName, defaultDomainId, defaultDomainId);
	}
    //
    // JetspeedPrincipalAccessManager interface implementation
    //
    @SuppressWarnings("unchecked") 
    public List<JetspeedPrincipal> getAssociatedFrom(String principalFromName, JetspeedPrincipalType from, JetspeedPrincipalType to, String associationName, Long fromSecurityDomain, Long toSecurityDomain)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("associationsTo.associationName", associationName);
        criteria.addEqualTo("associationsTo.from.name", principalFromName);
        criteria.addEqualTo("associationsTo.from.type", from.getName());
        criteria.addEqualTo("type", to.getName());
        criteria.addEqualTo("associationsTo.from.domainId", fromSecurityDomain);
        criteria.addEqualTo("domainId", toSecurityDomain);
        Query query = QueryFactory.newQuery(PersistentJetspeedPrincipal.class, criteria);
        return (List<JetspeedPrincipal>) getPersistenceBrokerTemplate().execute(new ManagedListByQueryCallback(query));
    }

    public List<JetspeedPrincipal> getAssociatedTo(String principalToName, JetspeedPrincipalType from, JetspeedPrincipalType to, String associationName){
        Long defaultDomainId = getDefaultSecurityDomainId();
        return getAssociatedTo(principalToName, from, to, associationName, defaultDomainId, defaultDomainId);
    }

    @SuppressWarnings("unchecked") 
    public List<JetspeedPrincipal> getAssociatedTo(String principalToName, JetspeedPrincipalType from, JetspeedPrincipalType to, String associationName, Long fromSecurityDomain, Long toSecurityDomain)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("associationsFrom.associationName", associationName);
        criteria.addEqualTo("associationsFrom.to.name", principalToName);
        criteria.addEqualTo("type", from.getName());
        criteria.addEqualTo("associationsFrom.to.type", to.getName());
        criteria.addEqualTo("associationsFrom.to.domainId", toSecurityDomain);
        criteria.addEqualTo("domainId", fromSecurityDomain);
        Query query = QueryFactory.newQuery(PersistentJetspeedPrincipal.class, criteria);
        return (List<JetspeedPrincipal>) getPersistenceBrokerTemplate().execute(new ManagedListByQueryCallback(query));
    }

    public List<JetspeedPrincipal> getAssociatedFrom(Long principalFromId, JetspeedPrincipalType from, JetspeedPrincipalType to, String associationName){
        Long defaultDomainId = getDefaultSecurityDomainId();
        return getAssociatedFrom(principalFromId, from, to, associationName, defaultDomainId, defaultDomainId);
    }

    @SuppressWarnings("unchecked") 
    public List<JetspeedPrincipal> getAssociatedFrom(Long principalFromId, JetspeedPrincipalType from, JetspeedPrincipalType to, String associationName, Long fromSecurityDomain, Long toSecurityDomain)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("associationsTo.associationName", associationName);
        criteria.addEqualTo("associationsTo.from.id", principalFromId);
        criteria.addEqualTo("associationsTo.from.type", from.getName());
        criteria.addEqualTo("type", to.getName());
        criteria.addEqualTo("associationsTo.from.domainId", fromSecurityDomain);
        criteria.addEqualTo("domainId", toSecurityDomain);
        Query query = QueryFactory.newQuery(PersistentJetspeedPrincipal.class, criteria);
        return (List<JetspeedPrincipal>) getPersistenceBrokerTemplate().execute(new ManagedListByQueryCallback(query));
    }

    public List<JetspeedPrincipal> getAssociatedTo(Long principalToId, JetspeedPrincipalType from, JetspeedPrincipalType to, String associationName){
        Long defaultDomainId = getDefaultSecurityDomainId();
        return getAssociatedTo(principalToId, from, to, associationName, defaultDomainId, defaultDomainId);
    }

    @SuppressWarnings("unchecked") 
    public List<JetspeedPrincipal> getAssociatedTo(Long principalToId, JetspeedPrincipalType from, JetspeedPrincipalType to, String associationName, Long fromSecurityDomain, Long toSecurityDomain)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("associationsFrom.associationName", associationName);
        criteria.addEqualTo("associationsFrom.to.id", principalToId);
        criteria.addEqualTo("type", from.getName());
        criteria.addEqualTo("associationsFrom.to.type", to.getName());
        criteria.addEqualTo("associationsFrom.to.domainId", toSecurityDomain);
        criteria.addEqualTo("domainId", fromSecurityDomain);
        Query query = QueryFactory.newQuery(PersistentJetspeedPrincipal.class, criteria);
        return (List<JetspeedPrincipal>) getPersistenceBrokerTemplate().execute(new ManagedListByQueryCallback(query));
    }

    public List<String> getAssociatedNamesFrom(String principalFromName, JetspeedPrincipalType from, JetspeedPrincipalType to, String associationName){
        Long defaultDomainId = getDefaultSecurityDomainId();
        return getAssociatedNamesFrom(principalFromName, from, to, associationName, defaultDomainId, defaultDomainId);
    }

    @SuppressWarnings("unchecked") 
    public List<String> getAssociatedNamesFrom(String principalFromName, JetspeedPrincipalType from, JetspeedPrincipalType to, String associationName, Long fromSecurityDomain, Long toSecurityDomain)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("associationsTo.associationName", associationName);
        criteria.addEqualTo("associationsTo.from.name", principalFromName);
        criteria.addEqualTo("associationsTo.from.type", from.getName());
        criteria.addEqualTo("type", to.getName());
        criteria.addEqualTo("associationsTo.from.domainId", fromSecurityDomain);
        criteria.addEqualTo("domainId", toSecurityDomain);
        ReportQueryByCriteria query = QueryFactory.newReportQuery(PersistentJetspeedPrincipal.class, criteria);
        query.setAttributes(new String[]{"name"});
        ArrayList<String> names = new ArrayList<String>();
        for (Iterator<Object[]> iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query); iter.hasNext(); )
        {
            names.add((String)iter.next()[0]);
        }
        return names;
    }

    public List<String> getAssociatedNamesFrom(Long principalFromId, JetspeedPrincipalType from, JetspeedPrincipalType to, String associationName){
        Long defaultDomainId = getDefaultSecurityDomainId();
        return getAssociatedNamesFrom(principalFromId, from, to, associationName, defaultDomainId, defaultDomainId);
    }

    @SuppressWarnings("unchecked") 
    public List<String> getAssociatedNamesFrom(Long principalFromId, JetspeedPrincipalType from, JetspeedPrincipalType to, String associationName, Long fromSecurityDomain, Long toSecurityDomain)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("associationsTo.associationName", associationName);
        criteria.addEqualTo("associationsTo.from.id", principalFromId);
        criteria.addEqualTo("associationsTo.from.type", from.getName());
        criteria.addEqualTo("type", to.getName());
        criteria.addEqualTo("associationsTo.from.domainId", fromSecurityDomain);
        criteria.addEqualTo("domainId", toSecurityDomain);
        ReportQueryByCriteria query = QueryFactory.newReportQuery(PersistentJetspeedPrincipal.class, criteria);
        query.setAttributes(new String[]{"name"});
        ArrayList<String> names = new ArrayList<String>();
        for (Iterator<Object[]> iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query); iter.hasNext(); )
        {
            names.add((String)iter.next()[0]);
        }
        return names;
    }

    public List<String> getAssociatedNamesTo(String principalToName, JetspeedPrincipalType from, JetspeedPrincipalType to, String associationName){
        Long defaultDomainId = getDefaultSecurityDomainId();
        return getAssociatedNamesTo(principalToName, from, to, associationName, defaultDomainId, defaultDomainId);
    }

    @SuppressWarnings("unchecked") 
    public List<String> getAssociatedNamesTo(String principalToName, JetspeedPrincipalType from, JetspeedPrincipalType to, String associationName, Long fromSecurityDomain, Long toSecurityDomain)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("associationsFrom.associationName", associationName);
        criteria.addEqualTo("associationsFrom.to.name", principalToName);
        criteria.addEqualTo("type", from.getName());
        criteria.addEqualTo("associationsFrom.to.type", to.getName());
        criteria.addEqualTo("associationsFrom.to.domainId", toSecurityDomain);
        criteria.addEqualTo("domainId", fromSecurityDomain);
        ReportQueryByCriteria query = QueryFactory.newReportQuery(PersistentJetspeedPrincipal.class, criteria);
        query.setAttributes(new String[]{"name"});
        ArrayList<String> names = new ArrayList<String>();
        for (Iterator<Object[]> iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query); iter.hasNext(); )
        {
            names.add((String)iter.next()[0]);
        }
        return names;
    }

    public List<String> getAssociatedNamesTo(Long principalToId, JetspeedPrincipalType from, JetspeedPrincipalType to, String associationName){
        Long defaultDomainId = getDefaultSecurityDomainId();
        return getAssociatedNamesTo(principalToId, from, to, associationName, defaultDomainId, defaultDomainId);
    }

    @SuppressWarnings("unchecked") 
    public List<String> getAssociatedNamesTo(Long principalToId, JetspeedPrincipalType from, JetspeedPrincipalType to, String associationName, Long fromSecurityDomain, Long toSecurityDomain)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("associationsFrom.associationName", associationName);
        criteria.addEqualTo("associationsFrom.to.id", principalToId);
        criteria.addEqualTo("type", from.getName());
        criteria.addEqualTo("associationsFrom.to.type", to.getName());
        criteria.addEqualTo("associationsFrom.to.domainId", toSecurityDomain);
        criteria.addEqualTo("domainId", fromSecurityDomain);
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
        return getPrincipal(principalName, type, getDefaultSecurityDomainId());
    }
    
    public JetspeedPrincipal getPrincipal(String principalName, JetspeedPrincipalType type, Long securityDomain)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("name", principalName);
        criteria.addEqualTo("type", type.getName());
        criteria.addEqualTo("domainId", securityDomain);
        Query query = QueryFactory.newQuery(PersistentJetspeedPrincipal.class,criteria);
        return (JetspeedPrincipal)getPersistenceBrokerTemplate().getObjectByQuery(query);
    }

    public List<String> getPrincipalNames(String nameFilter, JetspeedPrincipalType type)
    {
        return getPrincipalNames(nameFilter, type, getDefaultSecurityDomainId());
    }

    @SuppressWarnings("unchecked") 
    public List<String> getPrincipalNames(String nameFilter, JetspeedPrincipalType type, Long securityDomain)
    {
        Criteria criteria = new Criteria();
        if (nameFilter != null && nameFilter.length() > 0)
        {
            criteria.addLike("name", nameFilter+"%");
        }
        criteria.addEqualTo("type", type.getName());
        criteria.addEqualTo("domainId", securityDomain);
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
        return getPrincipals(nameFilter, type, getDefaultSecurityDomainId());
    }
    
    @SuppressWarnings("unchecked") 
    public List<JetspeedPrincipal> getPrincipals(String nameFilter, JetspeedPrincipalType type, Long securityDomain)
    {
        Criteria criteria = new Criteria();
        if (nameFilter != null && nameFilter.length() > 0)
        {
            criteria.addLike("name", nameFilter+"%");
        }
        criteria.addEqualTo("type", type.getName());
        criteria.addEqualTo("domainId", securityDomain);
        Query query = QueryFactory.newQuery(PersistentJetspeedPrincipal.class,criteria);
        return (List<JetspeedPrincipal>) getPersistenceBrokerTemplate().execute(new ManagedListByQueryCallback(query));
    }

    public List<JetspeedPrincipal> getPrincipalsByAttribute(String attributeName, String attributeValue, JetspeedPrincipalType type)
    {
        return getPrincipalsByAttribute(attributeName, attributeValue, type, getDefaultSecurityDomainId());
    }
    
    @SuppressWarnings("unchecked") 
    public List<JetspeedPrincipal> getPrincipalsByAttribute(String attributeName, String attributeValue, JetspeedPrincipalType type, Long securityDomain)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("attributes.name", attributeName);
        criteria.addEqualTo("attributes.value", attributeValue);
        criteria.addEqualTo("type", type.getName());
        criteria.addEqualTo("domainId", securityDomain);
        Query query = QueryFactory.newQuery(PersistentJetspeedPrincipal.class,criteria);
        return (List<JetspeedPrincipal>) getPersistenceBrokerTemplate().execute(new ManagedListByQueryCallback(query));
    }

    public boolean principalExists(String principalName, JetspeedPrincipalType type)
    {
        return principalExists(principalName, type, getDefaultSecurityDomainId());
    }
    
    public boolean principalExists(String principalName, JetspeedPrincipalType type, Long securityDomain)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("name", principalName);
        criteria.addEqualTo("type", type.getName());
        criteria.addEqualTo("domainId", securityDomain);
        Query query = QueryFactory.newQuery(PersistentJetspeedPrincipal.class,criteria);
        return getPersistenceBrokerTemplate().getCount(query) == 1;
    }

    //
    // JetspeedPrincipalStorageManager interface implementation
    //
    public void addPrincipal(JetspeedPrincipal principal, Set<JetspeedPrincipalAssociationReference> associations)
        throws SecurityException
    {
        if (principal.getDomainId() == null && principal instanceof TransientJetspeedPrincipal){
            ((TransientJetspeedPrincipal)principal).setDomainId(getDefaultSecurityDomainId());
        }
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
            throw new SecurityException(msg, pbe);            
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
            throw new SecurityException(msg, pbe);            
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
            throw new SecurityException(msg, pbe);            
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

    public PasswordCredential getPasswordCredential(String userName){
        return getPasswordCredential(userName,getDefaultSecurityDomainId());
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
    public PasswordCredential getPasswordCredential(String userName, Long securityDomain)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("user.name", userName);
        criteria.addEqualTo("user.enabled",true);
        criteria.addEqualTo("type", PasswordCredential.TYPE_CURRENT);
        criteria.addEqualTo("domainId", securityDomain);
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

    public List<PasswordCredential> getHistoricPasswordCredentials(User user){
        return getHistoricPasswordCredentials(user,getDefaultSecurityDomainId());
    }
    
    @SuppressWarnings("unchecked") 
    public List<PasswordCredential> getHistoricPasswordCredentials(User user, Long securityDomain)
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
                throw new SecurityException(msg, pbe);
            }
        }
    }

    public void removeAssociation(JetspeedPrincipal from, JetspeedPrincipal to, String associationName) throws SecurityException
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
                throw new SecurityException(msg, pbe);
            }
        }
    }

    //
    // JetspeedPermissionAccessManager interface implementation
    //
    @SuppressWarnings("unchecked") 
    public List<PersistentJetspeedPermission> getPermissions()
    {
        QueryByCriteria query = QueryFactory.newQuery(PersistentJetspeedPermissionImpl.class, new Criteria());
        query.addOrderByAscending("type");
        query.addOrderByAscending("name");
        return (List<PersistentJetspeedPermission>)getPersistenceBrokerTemplate().execute(new ManagedListByQueryCallback(query));
    }

    public List<PersistentJetspeedPermission> getPermissions(String type)
    {
        return getPermissions(type, null);
    }

    @SuppressWarnings("unchecked") 
    public List<PersistentJetspeedPermission> getPermissions(String type, String nameFilter)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("type", type);
        if (nameFilter != null && nameFilter.length() > 0)
        {
            criteria.addLike("name", nameFilter+"%");
        }
        QueryByCriteria query = QueryFactory.newQuery(PersistentJetspeedPermissionImpl.class, criteria);
        query.addOrderByAscending("name");
        return (List<PersistentJetspeedPermission>)getPersistenceBrokerTemplate().execute(new ManagedListByQueryCallback(query));
    }

    public boolean permissionExists(JetspeedPermission permission)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("type", permission.getType());
        criteria.addEqualTo("name", permission.getName());
        criteria.addEqualTo("actions", permission.getActions());
        Query query = QueryFactory.newQuery(PersistentJetspeedPermissionImpl.class, criteria);
        return getPersistenceBrokerTemplate().getCount(query) == 1;
    }
    
    @SuppressWarnings("unchecked") 
    public List<PersistentJetspeedPermission> getPermissions(PersistentJetspeedPrincipal principal)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("principals.principalId", principal.getId());
        QueryByCriteria query = QueryFactory.newQuery(PersistentJetspeedPermissionImpl.class, criteria);
        query.addOrderByAscending("type");
        query.addOrderByAscending("name");
        return (List<PersistentJetspeedPermission>) getPersistenceBrokerTemplate().execute(new ManagedListByQueryCallback(query));
    }

    @SuppressWarnings("unchecked") 
    public List<JetspeedPrincipal> getPrincipals(PersistentJetspeedPermission permission, String principalType)
    {
        Criteria criteria = new Criteria();
        if (permission.getId() != null)
        {
            criteria.addEqualTo("permissions.permissionId", permission.getId());
        }
        else
        {
            criteria.addEqualTo("permissions.permission.type", permission.getType());
            criteria.addEqualTo("permissions.permission.name", permission.getName());
        }
        if (principalType != null)
        {
            criteria.addEqualTo("type", principalType);
        }
        criteria.addEqualTo("domainId", getDefaultSecurityDomainId());
        QueryByCriteria query = QueryFactory.newQuery(PersistentJetspeedPrincipal.class, criteria);
        query.addOrderByAscending("type");
        query.addOrderByAscending("name");
        return (List<JetspeedPrincipal>) getPersistenceBrokerTemplate().execute(new ManagedListByQueryCallback(query));
    }

    //
    // JetspeedPermissionStorageManager interface implementation
    //
    public void addPermission(PersistentJetspeedPermission permission) throws SecurityException
    {
        if (permission.getId() != null || permissionExists(permission))
        {
            throw new SecurityException(SecurityException.PERMISSION_ALREADY_EXISTS.create(permission.getName()));
        }
        try
        {
            getPersistenceBrokerTemplate().store(permission);
        }
        catch (Exception pbe)
        {
            KeyedMessage msg = SecurityException.UNEXPECTED.create("JetspeedSecurityPersistenceManager",
                                                                   "addPermission",
                                                                   pbe.getMessage());
            logger.error(msg, pbe);
            throw new SecurityException(msg, pbe);            
        }
    }

    public void updatePermission(PersistentJetspeedPermission permission) throws SecurityException
    {
        Criteria criteria = new Criteria();
        if (permission.getId() == null)
        {
            criteria.addEqualTo("type", permission.getType());
            criteria.addEqualTo("name", permission.getName());
        }
        else
        {
            criteria.addEqualTo("id", permission.getId());
        }
        Query query = QueryFactory.newQuery(PersistentJetspeedPermissionImpl.class, criteria);
        PersistentJetspeedPermission current = (PersistentJetspeedPermission)getPersistenceBrokerTemplate().getObjectByQuery(query);
        if (current == null)
        {
            throw new SecurityException(SecurityException.PERMISSION_DOES_NOT_EXIST.create(permission.getName()));
        }
        if (!current.getActions().equals(permission.getActions()))
        {
            current.setActions(permission.getActions());
            try
            {
                getPersistenceBrokerTemplate().store(current);
            }
            catch (Exception pbe)
            {
                KeyedMessage msg = SecurityException.UNEXPECTED.create("JetspeedSecurityPersistenceManager",
                                                                       "updatePermission",
                                                                       pbe.getMessage());
                logger.error(msg, pbe);
                throw new SecurityException(msg, pbe);
            }
        }
    }    
    
    public void removePermission(PersistentJetspeedPermission permission) throws SecurityException
    {
        Criteria criteria = new Criteria();
        if (permission.getId() == null)
        {
            criteria.addEqualTo("type", permission.getType());
            criteria.addEqualTo("name", permission.getName());
        }
        else
        {
            criteria.addEqualTo("id", permission.getId());
        }
        Query query = QueryFactory.newQuery(PersistentJetspeedPermissionImpl.class, criteria);
        PersistentJetspeedPermission current = (PersistentJetspeedPermission)getPersistenceBrokerTemplate().getObjectByQuery(query);
        if (current == null)
        {
            throw new SecurityException(SecurityException.PERMISSION_DOES_NOT_EXIST.create(permission.getName()));
        }
        try
        {
            getPersistenceBrokerTemplate().delete(current);
        }
        catch (Exception pbe)
        {
            KeyedMessage msg = SecurityException.UNEXPECTED.create("JetspeedSecurityPersistenceManager",
                                                                   "removePermission",
                                                                   pbe.getMessage());
            logger.error(msg, pbe);
            throw new SecurityException(msg, pbe);            
        }
    }    

    public void grantPermission(PersistentJetspeedPermission permission, JetspeedPrincipal principal) throws SecurityException
    {
        if (permission.getId() == null)
        {
            Criteria criteria = new Criteria();
            criteria.addEqualTo("type", permission.getType());
            criteria.addEqualTo("name", permission.getName());
            Query query = QueryFactory.newQuery(PersistentJetspeedPermissionImpl.class, criteria);
            PersistentJetspeedPermission p = (PersistentJetspeedPermission)getPersistenceBrokerTemplate().getObjectByQuery(query);
            if (p == null)
            {
                throw new SecurityException(SecurityException.PERMISSION_DOES_NOT_EXIST.create(permission.getName()));
            }
            permission = p;
        }
        grantPermission(permission, principal, true);
    }

    protected void grantPermission(PersistentJetspeedPermission permission, JetspeedPrincipal principal, boolean checkExists) throws SecurityException
    {
        if (principal.isTransient() || principal.getId() == null)
        {
            JetspeedPrincipal p = getPrincipal(principal.getName(), principal.getType());
            if (p ==  null)
            {
                throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(principal.getType().getName(), principal.getName()));
            }
            principal = p;
        }
        Criteria criteria = new Criteria();
        criteria.addEqualTo("principalId", principal.getId());
        criteria.addEqualTo("permissionId", permission.getId());
        Query query = QueryFactory.newQuery(JetspeedPrincipalPermission.class,criteria);
        if (!checkExists || getPersistenceBrokerTemplate().getCount(query) == 0)
        {
            try
            {
                getPersistenceBrokerTemplate().store(new JetspeedPrincipalPermission(principal, permission));
            }
            catch (Exception pbe)
            {
                KeyedMessage msg = SecurityException.UNEXPECTED.create("JetspeedSecurityPersistenceManager",
                                                                       "grantPermission",
                                                                       pbe.getMessage());
                logger.error(msg, pbe);
                throw new SecurityException(msg, pbe);
            }
        }
    }

    @SuppressWarnings("unchecked") 
    public void grantPermissionOnlyTo(PersistentJetspeedPermission permission, String principalType, List<JetspeedPrincipal> principals) throws SecurityException
    {
        if (permission.getId() == null)
        {
            Criteria criteria = new Criteria();
            criteria.addEqualTo("type", permission.getType());
            criteria.addEqualTo("name", permission.getName());
            Query query = QueryFactory.newQuery(PersistentJetspeedPermissionImpl.class, criteria);
            PersistentJetspeedPermission p = (PersistentJetspeedPermission)getPersistenceBrokerTemplate().getObjectByQuery(query);
            if (p == null)
            {
                throw new SecurityException(SecurityException.PERMISSION_DOES_NOT_EXIST.create(permission.getName()));
            }
            permission = p;
        }
        Criteria criteria = new Criteria();
        criteria.addEqualTo("permissions.permissionId", permission.getId());
        if (principalType != null)
        {
            criteria.addEqualTo("type", principalType);
        }
        criteria.addEqualTo("domainId", getDefaultSecurityDomainId());
        QueryByCriteria query = QueryFactory.newQuery(PersistentJetspeedPrincipal.class, criteria);
        List<JetspeedPrincipal> currentList = (List<JetspeedPrincipal>) getPersistenceBrokerTemplate().execute(new ManagedListByQueryCallback(query));
        List<JetspeedPrincipal> targetList = new ArrayList<JetspeedPrincipal>(principals);
        for (Iterator<JetspeedPrincipal> i = currentList.iterator(); i.hasNext(); )
        {
            JetspeedPrincipal current = i.next();
            for (Iterator<JetspeedPrincipal> j = targetList.iterator(); j.hasNext(); )
            {
                JetspeedPrincipal target = j.next();
                
                if (principalType != null && !target.getType().getName().equals(principalType))
                {
                    throw new SecurityException(SecurityException.UNEXPECTED.create("JetspeedSecurityPersistenceManager", 
                                                                                    "grantPermissionOnlyTo",
                                                                                    "Specified "+target.getType().getName()+" principal: "+target.getName()+" is not of type: "+principalType));
                }
                if (current.getType().getName().equals(target.getType().getName()) && current.getName().equals(target.getName()))
                {
                    j.remove();
                    current = null;
                    break;
                }
            }
            if (current == null)
            {
                i.remove();
            }
        }
        for (Iterator<JetspeedPrincipal> i = currentList.iterator(); i.hasNext(); )
        {
            revokePermission(permission, i.next());
        }
        for (Iterator<JetspeedPrincipal> i = targetList.iterator(); i.hasNext(); )
        {
            grantPermission(permission, i.next(), false);
        }
    }

    public void revokePermission(PersistentJetspeedPermission permission, JetspeedPrincipal principal) throws SecurityException
    {
        Long principalId = null;
        Criteria criteria = new Criteria();
        if (principal.isTransient() || principal.getId() == null)
        {
            principalId = getPrincipalId(principal.getName(), principal.getType().getName(), getDefaultSecurityDomainId());
        }
        else
        {
            principalId = principal.getId();
        }
        criteria.addEqualTo("principalId", principalId);
        if (permission.getId() == null)
        {
            criteria.addEqualTo("permission.type", permission.getType());
            criteria.addEqualTo("permission.name", permission.getName());
        }
        else
        {
            criteria.addEqualTo("permissionId", permission.getId());
        }
        Query query = QueryFactory.newQuery(JetspeedPrincipalPermission.class,criteria);
        try
        {
            getPersistenceBrokerTemplate().deleteByQuery(query);
        }
        catch (Exception pbe)
        {
            KeyedMessage msg = SecurityException.UNEXPECTED.create("JetspeedSecurityPersistenceManager",
                                                                   "revokePermission",
                                                                   pbe.getMessage());
            logger.error(msg, pbe);
            throw new SecurityException(msg, pbe);
        }
    }
    
    public void revokeAllPermissions(JetspeedPrincipal principal) throws SecurityException
    {
        Long principalId = null;
        Criteria criteria = new Criteria();
        if (principal.isTransient() || principal.getId() == null)
        {
            principalId = getPrincipalId(principal.getName(), principal.getType().getName(), getDefaultSecurityDomainId());
        }
        else
        {
            principalId = principal.getId();
        }
        criteria.addEqualTo("principalId", principalId);
        Query query = QueryFactory.newQuery(JetspeedPrincipalPermission.class,criteria);
        try
        {
            getPersistenceBrokerTemplate().deleteByQuery(query);
        }
        catch (Exception pbe)
        {
            KeyedMessage msg = SecurityException.UNEXPECTED.create("JetspeedSecurityPersistenceManager",
                                                                   "revokeAllPermissions",
                                                                   pbe.getMessage());
            logger.error(msg, pbe);
            throw new SecurityException(msg, pbe);
        }
    }

    protected boolean domainExists(SecurityDomain domain){        
        if (domain.getDomainId() != null){
            return getDomain(domain.getDomainId()) != null;
        } else {
            return getDomainByName(domain.getName()) != null; 
        }
    }
    
    public void addDomain(SecurityDomain domain) throws SecurityException 
    {
        if (domainExists(domain))
        {
            throw new SecurityException(SecurityException.SECURITY_DOMAIN_EXISTS.create(domain.getName()));
        }
        try
        {
            getPersistenceBrokerTemplate().store(domain);
        }
        catch (Exception pbe)
        {
            KeyedMessage msg = SecurityException.UNEXPECTED.create("JetspeedSecurityPersistenceManager",
                                                                   "addDomain",
                                                                   pbe.getMessage());
            logger.error(msg, pbe);
            throw new SecurityException(msg, pbe);            
        }
    }

    public SecurityDomain getDomain(Long domainId)
    {
        try{
            return (SecurityDomain) getPersistenceBrokerTemplate().getObjectById(SecurityDomainImpl.class, domainId);    
        } catch (ObjectRetrievalFailureException ore){
            return null;
        }
        
    }

    protected Long getDefaultSecurityDomainId()
    {
        if (defaultSecurityDomainId == null)
        {
            SecurityDomain d = getDomainByName(SecurityDomain.DEFAULT_NAME);
            if (d != null)
            {
                // cache real default security domain id
                defaultSecurityDomainId = d.getDomainId();
            }
            else
            {
                throw new IllegalStateException("The default security domain could not be found.");
            }
        }   
        return defaultSecurityDomainId;
    }
    
    public SecurityDomain getDomainByName(String domainName)
    {
    	Criteria criteria = new Criteria();
        criteria.addEqualTo("name", domainName);
        Query query = QueryFactory.newQuery(SecurityDomainImpl.class,criteria);
        return (SecurityDomain) getPersistenceBrokerTemplate().getObjectByQuery(query);    
    }

    @SuppressWarnings("unchecked") 
    public Collection<SecurityDomain> getAllDomains()
    {
        QueryByCriteria query = QueryFactory.newQuery(SecurityDomainImpl.class, new Criteria());
        query.addOrderByAscending("name");
        return (List<SecurityDomain>)getPersistenceBrokerTemplate().execute(new ManagedListByQueryCallback(query));
    }
    
    public void removeDomain(SecurityDomain domain) throws SecurityException
    {
        if (!domainExists(domain))
        {
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.create(domain.getName()));
        }
        try
        {
            getPersistenceBrokerTemplate().delete(domain);
        }
        catch (Exception pbe)
        {
            if (pbe instanceof DataIntegrityViolationException)
            {
                logger.error(pbe.getMessage(), pbe);
                throw new SecurityException(SecurityException.SECURITY_DOMAIN_NOT_REMOVABLE.create(domain.getName()));
            }
            
            KeyedMessage msg = SecurityException.UNEXPECTED.create("JetspeedSecurityPersistenceManager",
                                                                   "removeDomain",
                                                                   pbe.getMessage());
            logger.error(msg, pbe);
            throw new SecurityException(msg, pbe);            
        }
        
    }
    
    public void updateDomain(SecurityDomain domain) throws SecurityException
    {
         if (!domainExists(domain)){
             throw new SecurityException(SecurityException.SECURITY_DOMAIN_DOES_NOT_EXIST.create(domain.getName()));
         }
         try
         {
             getPersistenceBrokerTemplate().store(domain);
         }
         catch (Exception pbe)
         {
             if (pbe instanceof DataIntegrityViolationException)
             {
                 logger.error(pbe.getMessage(), pbe);
                 throw new SecurityException(SecurityException.SECURITY_DOMAIN_UPDATE_FAILURE.create(domain.getDomainId()));
             }
             
             KeyedMessage msg = SecurityException.UNEXPECTED.create("JetspeedSecurityPersistenceManager",
                                                                    "updateDomain",
                                                                    pbe.getMessage());
             logger.error(msg, pbe);
             throw new SecurityException(msg, pbe);            
         }
    }

    @SuppressWarnings("unchecked") 
    public Collection<SecurityDomain> getDomainsOwnedBy(Long ownerDomainId)
    {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("ownerDomainId", ownerDomainId);
        QueryByCriteria query = QueryFactory.newQuery(SecurityDomainImpl.class, criteria);
        query.addOrderByAscending("name");
        return (List<SecurityDomain>)getPersistenceBrokerTemplate().execute(new ManagedListByQueryCallback(query));
    }
    
	public JetspeedPrincipalResultList getPrincipals(JetspeedPrincipalQueryContext queryContext, JetspeedPrincipalType type) {
		return getPrincipals(queryContext, type, getDefaultSecurityDomainId());
	}
	
	/* (non-Javadoc)
	 * @see org.apache.jetspeed.security.spi.JetspeedPrincipalAccessManager#getPrincipals(org.apache.jetspeed.security.JetspeedPrincipalQueryContext, org.apache.jetspeed.security.JetspeedPrincipalType, java.lang.Long)
	 */
	public JetspeedPrincipalResultList getPrincipals(JetspeedPrincipalQueryContext queryContext, JetspeedPrincipalType type, Long securityDomain) {
		JetspeedPrincipalLookupManager jppm = jpplf.getJetspeedPrincipalLookupManager();
		queryContext.put(JetspeedPrincipalQueryContext.JETSPEED_PRINCIPAL_TYPE, type.getName());
		queryContext.put(JetspeedPrincipalQueryContext.SECURITY_DOMAIN, securityDomain);
		return jppm.getPrincipals(queryContext);
	}
    
}
