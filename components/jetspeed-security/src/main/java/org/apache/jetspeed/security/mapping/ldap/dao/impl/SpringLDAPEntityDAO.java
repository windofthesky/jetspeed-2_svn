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
package org.apache.jetspeed.security.mapping.ldap.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.mapping.EntityFactory;
import org.apache.jetspeed.security.mapping.EntitySearchResultHandler;
import org.apache.jetspeed.security.mapping.impl.CollectingEntitySearchResultHandler;
import org.apache.jetspeed.security.mapping.impl.CollectingSearchResultHandler;
import org.apache.jetspeed.security.mapping.ldap.EntityFactoryImpl;
import org.apache.jetspeed.security.mapping.ldap.dao.EntityDAO;
import org.apache.jetspeed.security.mapping.ldap.dao.LDAPEntityDAOConfiguration;
import org.apache.jetspeed.security.mapping.model.Attribute;
import org.apache.jetspeed.security.mapping.model.AttributeDef;
import org.apache.jetspeed.security.mapping.model.Entity;
import org.springframework.ldap.AttributeInUseException;
import org.springframework.ldap.NameAlreadyBoundException;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.SchemaViolationException;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.OrFilter;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @author <a href="mailto:ate@douma.nu>Ate Douma</a>
 * @version $Id$
 */
public class SpringLDAPEntityDAO implements EntityDAO
{
    private static final class DelegatingEntitySearchResultHandler implements EntitySearchResultHandler
    {
        private int delegatedIndex;
        private EntitySearchResultHandler handler;        
        public DelegatingEntitySearchResultHandler(EntitySearchResultHandler handler) { this.handler = handler; }
        public int getMaxCount() { return handler.getMaxCount(); }        
        public int getSearchPageSize() { return handler.getSearchPageSize(); }        
        public void setFeedback(Object feedback) {}
        public Object getFeedback() {return null; }
        public void setEntityFactory(EntityFactory factory) {}
        public boolean handleSearchResult(Object result, int pageSize, int pageIndex, int index)
        {
            delegatedIndex++;
            return handler.handleSearchResult(result, 0, delegatedIndex, delegatedIndex);
        }
    };    
    
    private final LDAPEntityDAOConfiguration configuration;
    private final EntityFactory              entityFactory;
    private LdapTemplate                     ldapTemplate;
    private String                           defaultSearchFilterStr;
    private int                              searchPageSize;

    public SpringLDAPEntityDAO(LDAPEntityDAOConfiguration configuration)
    {
        this.configuration = configuration;
        this.entityFactory = new EntityFactoryImpl(configuration);
        this.defaultSearchFilterStr = createSearchFilter(null);
        this.searchPageSize = configuration.getSearchPageSize();
    }

    public LDAPEntityDAOConfiguration getConfiguration()
    {
        return configuration;
    }
    
    public String getEntityType()
    {
        return entityFactory.getEntityType();
    }

    public EntityFactory getEntityFactory()
    {
        return entityFactory;
    }
    
    public void setLdapTemplate(LdapTemplate ldapTemplate)
    {
        this.ldapTemplate = ldapTemplate;
    }

    public void getAllEntities(EntitySearchResultHandler handler) throws SecurityException
    {
        getEntities(null, handler);
    }

    public void getEntities(Filter filter, EntitySearchResultHandler handler) throws SecurityException
    {
        Validate.notNull(handler, "EntitySearchResultHandler parameter must not be null");
        String sf = createSearchFilter(filter);
        SearchControls sc = getSearchControls(SearchControls.SUBTREE_SCOPE, true, configuration.getEntityAttributeNames());
        
        handler.setEntityFactory(getEntityFactory());
        PagedSearchExecutor pse = new PagedSearchExecutor(configuration.getSearchDN(), sf, sc, handler, searchPageSize);
        
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try
        {
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            ldapTemplate.search(pse,pse);            
        }
        catch (NamingException e)
        {
            throw new SecurityException(SecurityException.UNEXPECTED.create(getClass().getName(), "getEntities", e.getMessage()), e);
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
            handler.setEntityFactory(null);
        }
    }

    public void getEntities(Entity parent, Filter filter, EntitySearchResultHandler handler) throws SecurityException
    {
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        DistinguishedName parentDN = getRelativeDN(parent.getInternalId());
        if (configuration.getSearchDN().size() == 0 || parentDN.startsWith(configuration.getSearchDN()))
        {
            String sf = createSearchFilter(filter);
            SearchControls sc = getSearchControls(SearchControls.ONELEVEL_SCOPE, true, configuration.getEntityAttributeNames());
            handler.setEntityFactory(getEntityFactory());
            PagedSearchExecutor pse = new PagedSearchExecutor(parentDN, sf, sc, handler, searchPageSize);
            
            try
            {
                Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
                
                ldapTemplate.search(pse, pse);
            }
            catch (NamingException e)
            {
                throw new SecurityException(SecurityException.UNEXPECTED.create(getClass().getName(), "getEntities", e.getMessage()), e);
            }
            finally
            {
                Thread.currentThread().setContextClassLoader(currentClassLoader);
                handler.setEntityFactory(null);
            }
        }
    }

    public Entity getEntity(String entityId) throws SecurityException
    {
        CollectingEntitySearchResultHandler handler = new CollectingEntitySearchResultHandler(1);
        getEntities(new EqualsFilter(configuration.getLdapIdAttribute(), entityId), handler);
        return handler.getCount() == 1 ? handler.getSingleResult() : null;
    }

    public void getEntitiesById(Collection<String> entityIds, EntitySearchResultHandler handler) throws SecurityException
    {
        OrFilter filter = new OrFilter();
        String idAttr = configuration.getLdapIdAttribute();
        for (String id : entityIds)
        {
            filter.or(new EqualsFilter(idAttr, id));
        }
        getEntities(filter, handler);
    }

    public Entity getEntityByInternalId(String internalId) throws SecurityException
    {
        CollectingEntitySearchResultHandler handler = new CollectingEntitySearchResultHandler(1);
        getEntityByInternalId(internalId, handler);
        return handler.getSingleResult(); 
    }

    protected void getEntityByInternalId(String internalId, EntitySearchResultHandler handler) throws SecurityException
    {
        DistinguishedName principalDN = getRelativeDN(internalId);
        if (configuration.getSearchDN().size() == 0 || principalDN.startsWith(configuration.getSearchDN()))
        {
            SearchControls sc = getSearchControls(SearchControls.OBJECT_SCOPE, true, configuration.getEntityAttributeNames());
            PagedSearchExecutor pse = new PagedSearchExecutor(principalDN, defaultSearchFilterStr, sc, handler);
            handler.setEntityFactory(getEntityFactory());
            
            ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
            try
            {
                Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
                
                ldapTemplate.search(pse,pse);
            }
            catch (NamingException e)
            {
                throw new SecurityException(SecurityException.UNEXPECTED.create(getClass().getName(), "getEntityByInternalId", e.getMessage()), e);
            }
            finally
            {
                Thread.currentThread().setContextClassLoader(currentClassLoader);
                handler.setEntityFactory(null);
            }
        }
    }
    
    public void getEntitiesByInternalId(Collection<String> internalIds, final EntitySearchResultHandler handler) throws SecurityException
    {
        try
        {
            handler.setEntityFactory(getEntityFactory());
            DelegatingEntitySearchResultHandler delegatingHandler = new DelegatingEntitySearchResultHandler(handler);
            for (Iterator<String> iterator = internalIds.iterator(); iterator.hasNext();)
            {
                getEntityByInternalId(iterator.next(), delegatingHandler);
            }
        }
        finally
        {
            handler.setEntityFactory(null);
        }
    }

    public Entity getParentEntity(Entity childEntity) throws SecurityException
    {
        DistinguishedName parentDN = new DistinguishedName(childEntity.getInternalId());
        parentDN.removeLast();
        return getEntityByInternalId(parentDN.toCompactString());
    }

    protected String getInternalId(Entity entity, boolean required) throws SecurityException
    {
        if (entity.getInternalId() != null)
        {
            return entity.getInternalId();
        }
        return getInternalId(entity.getId(), required);
    }

    public String getInternalId(String entityId, boolean required) throws SecurityException
    {
        String sf = createSearchFilter(new EqualsFilter(configuration.getLdapIdAttribute(), entityId));
        SearchControls sc = getSearchControls(SearchControls.SUBTREE_SCOPE, false, new String[0]);
        CollectingSearchResultHandler<String,SearchResult> cbh = 
            new CollectingSearchResultHandler<String,SearchResult>(1)
        {
            protected String mapResult(SearchResult result, int pageSize, int pageIndex, int index)
            {
                return result.getNameInNamespace();
            }
        };
        PagedSearchExecutor pse = new PagedSearchExecutor(configuration.getSearchDN(), sf, sc, cbh);
        
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try
        {
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            
            ldapTemplate.search(pse,pse);
            
            if (cbh.getCount() != 1)
            {
                if (required)
                {
                    throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(configuration.getEntityType(), entityId));
                }
                return null;
            }
            return cbh.getSingleResult();
        }
        catch (NamingException e)
        {
            throw new SecurityException(SecurityException.UNEXPECTED.create(getClass().getName(), "getInternalId", e.getMessage()), e);
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }            
    }
    
    protected DirContextOperations getEntityContext(Entity entity, boolean withAttributes) throws SecurityException
    {
        if (entity.getInternalId() != null)
        {
            return getEntityContextByInternalId(entity.getInternalId(), withAttributes);
        }
        else
        {
            return getEntityContextById(entity.getId(), withAttributes);
        }
    }

    protected DirContextOperations getEntityContextById(String entityId, boolean withAttributes) throws SecurityException
    {
        String sf = createSearchFilter(new EqualsFilter(configuration.getLdapIdAttribute(), entityId));
        SearchControls sc = getSearchControls(SearchControls.SUBTREE_SCOPE, true, 
                                              withAttributes ? configuration.getEntityAttributeNames() : new String[0]);
        CollectingSearchResultHandler<DirContextOperations,SearchResult> cbh = 
            new CollectingSearchResultHandler<DirContextOperations,SearchResult>(1)
        {
            protected DirContextOperations mapResult(SearchResult result, int pageSize, int pageIndex, int index)
            {
                return (DirContextOperations)result.getObject();
            }
        };
        PagedSearchExecutor pse = new PagedSearchExecutor(configuration.getSearchDN(), sf, sc, cbh);
        
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try
        {
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

            ldapTemplate.search(pse, pse);
            if (cbh.getCount() == 1)
            {
                return cbh.getSingleResult();
            }
            return null;
        }
        catch (NamingException e)
        {
            throw new SecurityException(SecurityException.UNEXPECTED.create(getClass().getName(), "getEntityContext", e.getMessage()), e);
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }

    protected DirContextOperations getEntityContextByInternalId(String internalId, boolean withAttributes) throws SecurityException
    {
        DistinguishedName principalDN = getRelativeDN(internalId);
        if (configuration.getSearchDN().size() == 0 || principalDN.startsWith(configuration.getSearchDN()))
        {
            String sf = createSearchFilter(null);
            SearchControls sc = getSearchControls(SearchControls.OBJECT_SCOPE, true, 
                                                  withAttributes ? configuration.getEntityAttributeNames() : new String[0]);
            CollectingSearchResultHandler<DirContextOperations,SearchResult> cbh = 
                new CollectingSearchResultHandler<DirContextOperations,SearchResult>(1)
            {
                protected DirContextOperations mapResult(SearchResult result, int pageSize, int pageIndex, int index)
                {
                    return (DirContextOperations)result.getObject();
                }
            };
            PagedSearchExecutor pse = new PagedSearchExecutor(principalDN, sf, sc, cbh); 
            
            ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
            try
            {
                Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
                
                ldapTemplate.search(pse,pse);
                return cbh.getSingleResult();
            }
            catch (NamingException e)
            {
                throw new SecurityException(SecurityException.UNEXPECTED.create(getClass().getName(), "getEntityContext", e.getMessage()), e);
            }
            finally
            {
                Thread.currentThread().setContextClassLoader(currentClassLoader);
            }
        }
        return null;
    }

    public void add(Entity entity, Entity parentEntity) throws SecurityException
    {
        if (parentEntity == null || parentEntity.getInternalId() == null)
        {
            throw new SecurityException(SecurityException.UNEXPECTED.create(getClass().getName(), "add(Entity entity, Entity parentEntity)",
                                                                            "Provided parent entity is null or has no internal ID."));
        }
        DistinguishedName parentDn = new DistinguishedName(parentEntity.getInternalId());
        parentDn.removeFirst(new DistinguishedName(configuration.getBaseDN()));
        internalAdd(entity, parentDn);
    }

    public void add(Entity entity) throws SecurityException
    {
        // add entity to "root" searchDN
        internalAdd(entity, new DistinguishedName(configuration.getSearchDN()));
    }

    protected void internalAdd(Entity entity, DistinguishedName dn) throws SecurityException
    {
        if (dn != null)
        {
            dn.add(configuration.getLdapIdAttribute(), entity.getId());
            String internalId = getFullDN(dn).toCompactString();
            Attributes attributes = new BasicAttributes();

            BasicAttribute basicAttr = new BasicAttribute("objectClass");
            for (String objClass : configuration.getObjectClassesArray())
            {
                basicAttr.add(objClass);
            }
            attributes.put(basicAttr);
            
            for (AttributeDef attrDef : configuration.getAttributeDefinitionsMap().values())
            {
                basicAttr = null;
                if (attrDef.isIdAttribute())
                {
                    basicAttr = new BasicAttribute(attrDef.getName());
                    basicAttr.add(entity.getId());
                }
                else if (attrDef.isRelationOnly() || !attrDef.isMapped())
                {
                    if (attrDef.isMultiValue() && attrDef.isRequired())
                    {
                        basicAttr = new BasicAttribute(attrDef.getName());
                        basicAttr.add(attrDef.requiresDnDefaultValue() ? internalId : attrDef.getRequiredDefaultValue());
                    }
                }
                else if (attrDef.isMapped())
                {
                    if (attrDef.isMultiValue() && attrDef.isRequired())
                    {
                        basicAttr = new BasicAttribute(attrDef.getName());
                        basicAttr.add(attrDef.requiresDnDefaultValue() ? internalId : attrDef.getRequiredDefaultValue());
                    }
                    
                    Attribute entityAttr = entity.getAttribute(attrDef.getName());
                    if (entityAttr != null)
                    {
                        if (attrDef.isMultiValue())
                        {
                            Collection<String> entityAttrValues = entityAttr.getValues();
                            if (entityAttrValues != null && entityAttrValues.size() > 0)
                            {
                                if (basicAttr == null)
                                {
                                    basicAttr = new BasicAttribute(attrDef.getName());
                                }
                                for (String val : entityAttrValues)
                                {
                                    basicAttr.add(val);
                                }
                            }
                        }
                        else if (entityAttr.getValue() != null)
                        {
                            basicAttr = new BasicAttribute(attrDef.getName());
                            basicAttr.add(entityAttr.getValue());
                        }
                    }
                }
                if (basicAttr != null)
                {
                    attributes.put(basicAttr);
                }
            }
            ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
            try
            {
                Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
                ldapTemplate.bind(dn, null, attributes);
            }
            catch (NameAlreadyBoundException e)
            {
                // TODO: synchronize entity before throwing exception
                throw new SecurityException(SecurityException.PRINCIPAL_ALREADY_EXISTS.createScoped(entity.getType(), entity.getId()));
            }
            catch (NamingException e)
            {
                throw new SecurityException(SecurityException.UNEXPECTED.create(getClass().getName(), "add", e.getMessage()), e);
            }
            finally
            {
                Thread.currentThread().setContextClassLoader(currentClassLoader);
            }
        }
    }

    public void update(Entity entity) throws SecurityException
    {
        String internalId = getInternalId(entity, true);
        
        Collection<ModificationItem> modItems = new ArrayList<ModificationItem>();
        for (AttributeDef attrDef : configuration.getEntityAttributeDefinitionsMap().values())
        {
            if (attrDef.isMapped() && !attrDef.isIdAttribute() && !attrDef.isEntityIdAttribute())
            {
                Attribute entityAttr = entity.getAttribute(attrDef.getName());
                BasicAttribute namingAttr = new BasicAttribute(attrDef.getName());
                boolean attrAdded = false;
                if (entityAttr != null)
                {
                    if (attrDef.isMultiValue())
                    {
                        if (attrDef.isRequired())
                        {
                            // ensure defaultValue or dnDefaultValue is always present
                            namingAttr.add(attrDef.requiresDnDefaultValue() ? internalId : attrDef.getRequiredDefaultValue());
                            attrAdded = true;
                        }
                        Collection<String> values = entityAttr.getValues();
                        if (values != null && values.size() > 0)
                        {
                            for (String val : values)
                            {
                                namingAttr.add(val);
                            }
                            attrAdded = true;
                        }
                    }
                    else
                    {
                        String value = entityAttr.getValue();
                        if (value != null)
                        {
                            namingAttr.add(value);
                            attrAdded = true;
                        }
                    }
                }
                if (!attrAdded && attrDef.isMultiValue() && attrDef.isRequired())
                {
                    // ensure defaultValue or dnDefaultValue is always present
                    namingAttr.add(attrDef.requiresDnDefaultValue() ? internalId : attrDef.getRequiredDefaultValue());
                }
                // always use REPLACE_ATTRIBUTE even to remove an (empty) attribute
                // as using REMOVE_ATTRIBUTE *might* throw NoSuchAttributeException (depends on server implementation)
                modItems.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, namingAttr));
            }
        }
        
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try
        {
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            ldapTemplate.modifyAttributes(getRelativeDN(internalId), modItems.toArray(new ModificationItem[] {}));
        }
        catch (NamingException e)
        {
            throw new SecurityException(SecurityException.UNEXPECTED.create(getClass().getName(), "update", e.getMessage()), e);
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }

    public void remove(Entity entity) throws SecurityException
    {
        String internalId = getInternalId(entity, false);
        if (internalId == null)
        {
            // not found
            return;
        }
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try
        {
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            ldapTemplate.unbind(getRelativeDN(internalId));
        }
        catch (NameNotFoundException e)
        {
            // ignore
        }
        catch (NamingException e)
        {
            throw new SecurityException(SecurityException.UNEXPECTED.create(getClass().getName(), "remove", e.getMessage()), e);
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }

    public void addRelation(String entityId, String relatedEntityInternalId, String attributeName) throws SecurityException
    {
        AttributeDef attrDef = configuration.getAttributeDef(attributeName);
        if (attrDef == null)
        {
            throw new SecurityException(SecurityException.ENTITY_ATTRIBUTE_UNDEFINED.createScoped(configuration.getEntityType(), attributeName));
        }
        DirContextOperations dirCtxOps = getEntityContextById(entityId, false);
        if (dirCtxOps == null)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(configuration.getEntityType(), entityId));
        }
        ModificationItem[] modItems = new ModificationItem[1];
        modItems[0] = new ModificationItem(attrDef.isMultiValue() ? DirContext.ADD_ATTRIBUTE : DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(attributeName));
        modItems[0].getAttribute().add(relatedEntityInternalId);
        
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try
        {
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            ldapTemplate.modifyAttributes(getRelativeDN(dirCtxOps.getNameInNamespace()), modItems);
        }
        catch (AttributeInUseException e)
        {
            // relation already defined, ignore
        }
        catch (NamingException e)
        {
            throw new SecurityException(SecurityException.UNEXPECTED.create(getClass().getName(), "addRelation", e.getMessage()), e);
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }

    public void removeRelation(String entityId, String relatedEntityInternalId, String attributeName) throws SecurityException
    {
        AttributeDef attrDef = configuration.getAttributeDef(attributeName);
        if (attrDef == null)
        {
            throw new SecurityException(SecurityException.ENTITY_ATTRIBUTE_UNDEFINED.createScoped(configuration.getEntityType(), attributeName));
        }
        DirContextOperations dirCtxOps = getEntityContextById(entityId, false);
        if (dirCtxOps == null)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(configuration.getEntityType(), entityId));
        }
        ModificationItem[] modItems = new ModificationItem[1];
        modItems[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute(attributeName));
        if (attrDef.isMultiValue())
        {
            modItems[0].getAttribute().add(relatedEntityInternalId);
        }
        
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try
        {
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            try
            {
                ldapTemplate.modifyAttributes(getRelativeDN(dirCtxOps.getNameInNamespace()), modItems);
            }
            catch (SchemaViolationException e)
            {
                // required multi-value attribute removal?
                if (!(attrDef.isMultiValue() && attrDef.isRequired()))
                {
                    throw e;
                }
                // replace with required default or dn
                modItems[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new  BasicAttribute(attributeName));
                modItems[0].getAttribute().add(attrDef.requiresDnDefaultValue() ? dirCtxOps.getNameInNamespace() : attrDef.getRequiredDefaultValue());
                // try again
                ldapTemplate.modifyAttributes(getRelativeDN(dirCtxOps.getNameInNamespace()), modItems);
            }
        }
        catch (NamingException e)
        {
            throw new SecurityException(SecurityException.UNEXPECTED.create(getClass().getName(), "removeRelation", e.getMessage()), e);
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }
    
    protected SearchControls getSearchControls(int searchScope, boolean returningObjFlag, String[] attrs) 
    {
        SearchControls controls = new SearchControls();
        controls.setSearchScope(searchScope);
        controls.setReturningObjFlag(returningObjFlag);
        controls.setReturningAttributes(attrs);
        return controls;
    }

    protected DistinguishedName getRelativeDN(String fullDN)
    {
        DistinguishedName principalDN = new DistinguishedName(fullDN);
        if (configuration.getBaseDN().size() > 0)
        {
            principalDN.removeFirst(configuration.getBaseDN());
        }
        return principalDN;
    }

    protected DistinguishedName getFullDN(DistinguishedName relativeDN)
    {        
        DistinguishedName fullDN = new DistinguishedName(relativeDN);
        if (configuration.getBaseDN().size() > 0 && !fullDN.startsWith(configuration.getBaseDN()))
        {
            fullDN.prepend(configuration.getBaseDN());
        }
        return fullDN;
    }

    protected String createSearchFilter(Filter filter)
    {
        if (configuration.getSearchFilter() != null)
        {
            if (filter == null)
            {
                filter = configuration.getSearchFilter();
            }
            else
            {
                filter = new AndFilter().and(configuration.getSearchFilter()).and(filter);
            }
        }
        String filterStr = filter != null ? filter.encode() : "";
        if (StringUtils.isEmpty(filterStr))
        {
            filterStr = "(objectClass=*)"; // trivial search query
        }
        return filterStr;
    }
}