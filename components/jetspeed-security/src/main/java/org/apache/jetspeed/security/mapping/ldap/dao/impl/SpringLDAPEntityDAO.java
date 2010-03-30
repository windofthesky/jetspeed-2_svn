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
import java.util.List;

import javax.naming.Binding;
import javax.naming.Name;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;

import org.apache.commons.lang.StringUtils;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.mapping.EntityFactory;
import org.apache.jetspeed.security.mapping.ldap.EntityFactoryImpl;
import org.apache.jetspeed.security.mapping.ldap.dao.CollectingBindingsCallbackHandler;
import org.apache.jetspeed.security.mapping.ldap.dao.EntityContextMapper;
import org.apache.jetspeed.security.mapping.ldap.dao.DefaultEntityContextMapper;
import org.apache.jetspeed.security.mapping.ldap.dao.EntityDAO;
import org.apache.jetspeed.security.mapping.ldap.dao.LDAPEntityDAOConfiguration;
import org.apache.jetspeed.security.mapping.model.Attribute;
import org.apache.jetspeed.security.mapping.model.AttributeDef;
import org.apache.jetspeed.security.mapping.model.Entity;
import org.springframework.ldap.NameAlreadyBoundException;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DirContextProcessor;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.simple.SimpleLdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.OrFilter;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public class SpringLDAPEntityDAO implements EntityDAO
{
    public static final String DN_REFERENCE_MARKER = "#dn";
    
    protected enum UpdateMode
    {
        MAPPED, INTERNAL, ALL
    };
    
    private static final DirContextProcessor nullDirContextProcessor = new DirContextProcessor()
    {
        public void postProcess(DirContext ctx) throws javax.naming.NamingException{}
        public void preProcess(DirContext ctx) throws javax.naming.NamingException{}
    };

    private final LDAPEntityDAOConfiguration configuration;
    private final EntityFactory              entityFactory;
    private EntityContextMapper              contextMapper;
    private LdapTemplate                     ldapTemplate;
    private SimpleLdapTemplate               simpleLdapTemplate;
    private String                           defaultSearchFilterStr;

    public SpringLDAPEntityDAO(LDAPEntityDAOConfiguration configuration)
    {
        this.configuration = configuration;
        this.entityFactory = new EntityFactoryImpl(configuration);
        this.contextMapper = new DefaultEntityContextMapper(entityFactory);
        this.defaultSearchFilterStr = createSearchFilter(null);
    }

    public LDAPEntityDAOConfiguration getConfiguration()
    {
        return configuration;
    }

    protected EntityContextMapper getContextMapper()
    {
        return contextMapper;
    }
    
    public String getEntityType()
    {
        return entityFactory.getEntityType();
    }

    public EntityFactory getEntityFactory()
    {
        return entityFactory;
    }
    
    public void setEntityContextMapper(EntityContextMapper contextMapper)
    {
        this.contextMapper = contextMapper;
    }

    public void setLdapTemplate(SimpleLdapTemplate simpleLdapTemplate)
    {
        this.simpleLdapTemplate = simpleLdapTemplate;
        this.ldapTemplate = (LdapTemplate)simpleLdapTemplate.getLdapOperations();
    }

    public Collection<Entity> getEntities(Filter filter)
    {
        String filterStr = createSearchFilter(filter);
        Collection<Entity> results = null;
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try
        {
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            results = simpleLdapTemplate.search(configuration.getSearchDN(), filterStr, 
                                                getSearchControls(SearchControls.SUBTREE_SCOPE, true,configuration.getEntityAttributeNames()), 
                                                getContextMapper(), nullDirContextProcessor);
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
        return results;
    }

    public Collection<Entity> getEntities(Entity parent, Filter filter)
    {
        String filterStr = createSearchFilter(filter);
        Collection<Entity> results = null;
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        DistinguishedName parentDN = getRelativeDN(parent.getInternalId());
        if (configuration.getSearchDN().size() == 0 || parentDN.endsWith(configuration.getSearchDN()))
        {
            try
            {
                Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
                results = simpleLdapTemplate.search(parentDN, filterStr, 
                                                    getSearchControls(SearchControls.ONELEVEL_SCOPE, true,configuration.getEntityAttributeNames()), 
                                                    getContextMapper(), nullDirContextProcessor);
                            }
            finally
            {
                Thread.currentThread().setContextClassLoader(currentClassLoader);
            }
        }
        return results;
    }

    public Collection<Entity> getAllEntities()
    {
        return getEntities(null);
    }

    public Entity getEntity(String entityId)
    {
        Collection<Entity> entities = getEntities(new EqualsFilter(configuration.getLdapIdAttribute(), entityId));
        if (entities != null && entities.size() == 1)
        {
            return entities.iterator().next();
        }
        return null;
    }

    public Collection<Entity> getEntitiesById(Collection<String> entityIds)
    {
        OrFilter filter = new OrFilter();
        String idAttr = configuration.getLdapIdAttribute();
        for (String id : entityIds)
        {
            filter.or(new EqualsFilter(idAttr, id));
        }
        return getEntities(filter);
    }

    public Entity getEntityByInternalId(String internalId)
    {
        Entity resultEntity = null;
        DistinguishedName principalDN = getRelativeDN(internalId);
        if (configuration.getSearchDN().size() == 0 || principalDN.endsWith(configuration.getSearchDN()))
        {
            ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
            try
            {
                Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
                List<Entity> result = simpleLdapTemplate.search(principalDN, defaultSearchFilterStr, 
                                                                getSearchControls(SearchControls.OBJECT_SCOPE, true, configuration.getEntityAttributeNames()), 
                                                                getContextMapper(), nullDirContextProcessor);
                if (!result.isEmpty())
                {
                    resultEntity = result.get(0);
                }
            }
            finally
            {
                Thread.currentThread().setContextClassLoader(currentClassLoader);
            }
        }
        return resultEntity;
    }
    
    public Collection<Entity> getEntitiesByInternalId(Collection<String> internalIds)
    {
        final Collection<Entity> resultSet = new ArrayList<Entity>();
        for (Iterator<String> iterator = internalIds.iterator(); iterator.hasNext();)
        {
            Entity resultEntity = getEntityByInternalId(iterator.next());
            if (resultEntity != null)
            {
                resultSet.add(resultEntity);
            }
        }
        return resultSet;
    }

    public Entity getParentEntity(Entity childEntity)
    {
        DistinguishedName parentDN = new DistinguishedName(childEntity.getInternalId());
        parentDN.removeLast();
        return getEntityByInternalId(parentDN.encode());
    }

    protected String getInternalId(Entity entity)
    {
        if (entity.getInternalId() != null)
        {
            return entity.getInternalId();
        }
        String filterStr = createSearchFilter(new EqualsFilter(configuration.getLdapIdAttribute(), entity.getId()));
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try
        {
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            CollectingBindingsCallbackHandler handler = new CollectingBindingsCallbackHandler();
            ldapTemplate.search(configuration.getSearchDN(), filterStr, getSearchControls(SearchControls.SUBTREE_SCOPE, false, new String[0]), handler);
            if (handler.getList().isEmpty() || handler.getList().size() != 1)
            {
                return null;
            }
            return ((Binding)handler.getList().get(0)).getNameInNamespace();
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }            
    }
    
    public DirContextOperations getEntityContext(Entity entity, boolean withAttributes)
    {
        if (entity.getInternalId() != null)
        {
            return getEntityContext(entity.getInternalId(), withAttributes);
        }
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try
        {
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            CollectingBindingsCallbackHandler handler = new CollectingBindingsCallbackHandler();
            ldapTemplate.search(configuration.getSearchDN(), createSearchFilter(new EqualsFilter(configuration.getLdapIdAttribute(), entity.getId())),
                                getSearchControls(SearchControls.SUBTREE_SCOPE, true, withAttributes ? configuration.getEntityAttributeNames() : new String[0]), 
                                handler);
            if (!handler.getList().isEmpty() && handler.getList().size() == 1)
            {
                return (DirContextOperations)((Binding)handler.getList().get(0)).getObject();
            }
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
        return null;
    }

    public DirContextOperations getEntityContext(String internalId, boolean withAttributes)
    {
        DistinguishedName principalDN = getRelativeDN(internalId);
        if (configuration.getSearchDN().size() == 0 || principalDN.endsWith(configuration.getSearchDN()))
        {
            ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
            try
            {
                Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
                CollectingBindingsCallbackHandler handler = new CollectingBindingsCallbackHandler();
                ldapTemplate.search(principalDN, createSearchFilter(null),
                                    getSearchControls(SearchControls.OBJECT_SCOPE, true, withAttributes ? configuration.getEntityAttributeNames() : new String[0]), 
                                    handler);
                if (!handler.getList().isEmpty())
                {
                    return (DirContextOperations)((Binding)handler.getList().get(0)).getObject();
                }
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

    public void internalAdd(Entity entity, DistinguishedName dn) throws SecurityException
    {
        Attributes attributes = new BasicAttributes();
        if (dn != null)
        {
            dn.add(configuration.getLdapIdAttribute(), entity.getId());
            String fullDN = null;
            for (AttributeDef attrDef : configuration.getAttributeDefinitionsMap().values())
            {
                Attribute entityAttr = attrDef.isRelationOnly() ? null : entity.getAttribute(attrDef.getName());
                BasicAttribute basicAttr = null;
                if (entityAttr != null)
                {
                    if (attrDef.isMultiValue())
                    {
                        Collection<String> entityAttrValues = entityAttr.getValues();
                        if (entityAttrValues != null && entityAttrValues.size() > 0)
                        {
                            basicAttr = new BasicAttribute(attrDef.getName());
                            for (String val : entityAttrValues)
                            {
                                basicAttr.add(val);
                            }
                        }
                    }
                    else
                    {
                        basicAttr = new BasicAttribute(attrDef.getName());
                        basicAttr.add(entityAttr.getValue());
                    }
                }
                else
                {
                    if (attrDef.isIdAttribute())
                    {
                        basicAttr = new BasicAttribute(attrDef.getName());
                        basicAttr.add(entity.getId());
                    }
                    else if (attrDef.isRequired())
                    {
                        String requiredValue = attrDef.getRequiredDefaultValue();
                        if (requiredValue != null && requiredValue.length() > 0)
                        {
                            basicAttr = new BasicAttribute(attrDef.getName());
                            if (SpringLDAPEntityDAO.DN_REFERENCE_MARKER.equals(requiredValue))
                            {
                                if (fullDN == null)
                                {
                                    fullDN = getFullDN(dn).encode();
                                }
                                basicAttr.add(fullDN);
                            }
                            else
                            {
                                basicAttr.add(requiredValue);
                            }
                        }
                        else
                        {
                            // missing required attribute value, LDAP will/should throw exception
                        }
                    }
                }
                if (basicAttr != null)
                {
                    attributes.put(basicAttr);
                }
            }
            BasicAttribute attr = new BasicAttribute("objectClass");
            for (String objClass : configuration.getObjectClassesArray())
            {
                attr.add(objClass);
            }
            attributes.put(attr);
            ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
            try
            {
                Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
                ldapTemplate.bind(dn, null, attributes);
            }
            catch (NameAlreadyBoundException e)
            {
                throw new SecurityException(SecurityException.PRINCIPAL_ALREADY_EXISTS.createScoped(entity.getType(), entity.getId()));
            }
            finally
            {
                Thread.currentThread().setContextClassLoader(currentClassLoader);
            }
        }
    }

    public void update(Entity entity) throws SecurityException
    {
        internalUpdate(entity, UpdateMode.MAPPED);
    }

    public void updateInternalAttributes(Entity entity) throws SecurityException
    {
        internalUpdate(entity, UpdateMode.INTERNAL);
    }
    
    
    protected void internalUpdate(Entity entity, UpdateMode umode) throws SecurityException
    {
        DirContextOperations dirCtxOps = getEntityContext(entity, true);
        if (dirCtxOps == null)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(entity.getType(), entity.getId()));
        }
        String internalId = dirCtxOps.getNameInNamespace();
        Name dn = getRelativeDN(internalId);
        
        Collection<ModificationItem> modItems = getModItems(entity, dirCtxOps, umode);
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try
        {
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            ldapTemplate.modifyAttributes(dn, modItems.toArray(new ModificationItem[] {}));
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }

    public void remove(Entity entity) throws SecurityException
    {
        String internalId = getInternalId(entity);
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
        finally
        {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }

    public void addRelation(String entityId, String relatedEntityId, String attributeName)
    {
        // TODO
    }

    public void removeRelation(String EntityId, String relatedEntityId, String attributeName)
    {
        // TODO
    }
    
    protected SearchControls getSearchControls(int searchScope, boolean returningObjFlag, String[] attrs) 
    {
        SearchControls controls = new SearchControls();
        controls.setSearchScope(searchScope);
        controls.setReturningObjFlag(returningObjFlag);
        controls.setReturningAttributes(attrs);
        return controls;
    }

    protected Collection<ModificationItem> getModItems(Entity entity, DirContextOperations dirCtxOps, UpdateMode umode)
    {
        Collection<ModificationItem> modItems = new ArrayList<ModificationItem>();
        for (AttributeDef attrDef : configuration.getEntityAttributeDefinitionsMap().values())
        {
            if (!attrDef.getName().equals(configuration.getLdapIdAttribute()))
            {
                if (umode == UpdateMode.ALL || (umode == UpdateMode.MAPPED && attrDef.isMapped()) || (umode == UpdateMode.INTERNAL && !attrDef.isMapped()))
                {
                    Attribute entityAttr = entity.getAttribute(attrDef.getName());
                    boolean attrAdded = false;
                    if (entityAttr != null)
                    {
                        if (attrDef.isMultiValue())
                        {
                            Collection<String> values = entityAttr.getValues();
                            if (values != null)
                            {
                                javax.naming.directory.Attribute namingAttr = new BasicAttribute(entityAttr.getName());
                                if (values.size() > 0)
                                {
                                    for (String val : values)
                                    {
                                        namingAttr.add(val);
                                    }
                                    modItems.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, namingAttr));
                                    attrAdded = true;
                                }
                            }
                        }
                        else
                        {
                            String value = entityAttr.getValue();
                            if (value != null)
                            {
                                javax.naming.directory.Attribute namingAttr = new BasicAttribute(entityAttr.getName(), entityAttr.getValue());
                                modItems.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, namingAttr));
                                attrAdded = true;
                            }
                        }
                    }
                    if (!attrAdded)
                    {
                        // entity attribute not added, so remove it if present
                        // in ldap.
                        Object namingAttrValue = dirCtxOps.getObjectAttribute(attrDef.getName());
                        if (namingAttrValue != null)
                        {
                            BasicAttribute basicAttr = new BasicAttribute(attrDef.getName());
                            if (attrDef.isRequired())
                            {
                                if (attrDef.getRequiredDefaultValue() != null)
                                {
                                    String defaultValue = attrDef.getRequiredDefaultValue();
                                    if (SpringLDAPEntityDAO.DN_REFERENCE_MARKER.equals(defaultValue))
                                    {
                                        defaultValue = entity.getInternalId();
                                    }
                                    basicAttr.add(defaultValue);
                                    modItems.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, basicAttr));
                                }
                                else
                                {
                                    // TODO throw exception
                                    break;
                                }
                            }
                            else
                            {
                                modItems.add(new ModificationItem(DirContext.REMOVE_ATTRIBUTE, basicAttr));
                            }
                        }
                    }
                }
            }
        }
        return modItems;
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
        if (configuration.getBaseDN().size() > 0 && !fullDN.endsWith(configuration.getBaseDN()))
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