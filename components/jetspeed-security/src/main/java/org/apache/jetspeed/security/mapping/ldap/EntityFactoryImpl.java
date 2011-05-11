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
package org.apache.jetspeed.security.mapping.ldap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang.StringUtils;
import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.SecurityAttribute;
import org.apache.jetspeed.security.SecurityAttributes;
import org.apache.jetspeed.security.mapping.EntityFactory;
import org.apache.jetspeed.security.mapping.ldap.dao.LDAPEntityDAOConfiguration;
import org.apache.jetspeed.security.mapping.model.Attribute;
import org.apache.jetspeed.security.mapping.model.AttributeDef;
import org.apache.jetspeed.security.mapping.model.Entity;
import org.apache.jetspeed.security.mapping.model.impl.AttributeImpl;
import org.apache.jetspeed.security.mapping.model.impl.EntityImpl;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapRdn;
import org.springframework.ldap.support.LdapUtils;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public class EntityFactoryImpl implements EntityFactory
{
    private LDAPEntityDAOConfiguration searchConfiguration;
    private boolean createAllowed = true;
    private boolean updateAllowed = true;
    private boolean removeAllowed = true;

    public EntityFactoryImpl(LDAPEntityDAOConfiguration searchConfiguration)
    {
        this.searchConfiguration = searchConfiguration;
    }
    
    public String getEntityType()
    {
        return searchConfiguration.getEntityType();
    }

    public boolean isCreateAllowed()
    {
        return createAllowed;
    }

    public void setCreateAllowed(boolean createAllowed)
    {
        this.createAllowed = createAllowed;
    }

    public boolean isUpdateAllowed()
    {
        return updateAllowed;
    }

    public void setUpdateAllowed(boolean updateAllowed)
    {
        this.updateAllowed = updateAllowed;
    }

    public boolean isRemoveAllowed()
    {
        return removeAllowed;
    }

    public void setRemoveAllowed(boolean removeAllowed)
    {
        this.removeAllowed = removeAllowed;
    }

    protected EntityImpl internalCreateEntity(String entityId, String internalId, Set<Attribute> attributes)
    {
        EntityImpl entity = new EntityImpl(searchConfiguration.getEntityType(), entityId, searchConfiguration.getAttributeDefinitionsMap());
        entity.setAttributes(attributes);
        if (internalId != null)
        {
            entity.setInternalId(internalId);
        }
        return entity;
    }

    public Entity createEntity(JetspeedPrincipal principal)
    {
        Set<Attribute> ldapAttrValues = new HashSet<Attribute>();
        SecurityAttributes sas = principal.getSecurityAttributes();
        for (AttributeDef attrDef : searchConfiguration.getEntityAttributeDefinitionsMap().values())
        {
            if (attrDef.isMapped())
            {
                SecurityAttribute sa = sas.getAttribute(attrDef.getMappedName());
                if (sa != null)
                {
                    // currently only single-valued attributes are supported
                    AttributeImpl attr = new AttributeImpl(attrDef);
                    attr.setValue(sa.getStringValue());
                    ldapAttrValues.add(attr);
                }
            }
        }
        return internalCreateEntity(principal.getName(), null, ldapAttrValues);
    }

    protected List<String> getStringAttributes(Attributes originalAttrs, String name, boolean containsDN)
    {
        ArrayList<String> attributes = null;
        javax.naming.directory.Attribute attribute = originalAttrs.get(name);
        if (attribute != null)
        {
            int size = attribute.size();
            if (size > 0)
            {
                attributes = new ArrayList<String>(size);
                for (int i = 0; i < size; i++)
                {
                    try
                    {
                        String value = (String) attribute.get(i);
                        if (containsDN && !StringUtils.isEmpty(value))
                        {
                            // ensure dn values are all always equally encoded so we can use values.contains(internalId)
                            value = new DistinguishedName(value).toCompactString();
                        }
                        attributes.add(value);
                    }
                    catch (NamingException e)
                    {
                        throw LdapUtils.convertLdapException(e);
                    }
                }
            }
        }
        return attributes;
    }
    
    public Entity loadEntity(Object providerContext)
    {
        Entity entity = null;
        DirContextOperations ctx = null;
        
        if (providerContext instanceof SearchResult)
        {
            ctx = (DirContextOperations) ((SearchResult) (providerContext)).getObject();
        }
        else if (providerContext instanceof DirContextAdapter)
        {
            ctx = (DirContextOperations) providerContext;
        }
        if (ctx != null)
        {
            String entityId = null;
            String dn = ctx.getNameInNamespace();
            Set<Attribute> attributes = new HashSet<Attribute>();
            Attributes attrs = ctx.getAttributes();
            for (AttributeDef attrDef : searchConfiguration.getEntityAttributeDefinitionsMap().values())
            {
                List<String> values = null;
                values = getStringAttributes(attrs, attrDef.getName(), attrDef.requiresDnDefaultValue());
                if (values != null)
                {
                    Attribute a = new AttributeImpl(attrDef);
                    if (attrDef.isMultiValue())
                    {
                        // remove the dummy value for required fields when present.
                        if (attrDef.isRequired())
                        {
                            String defaultValue = attrDef.requiresDnDefaultValue() ? dn : attrDef.getRequiredDefaultValue();
                            values.remove(defaultValue);
                        }
                        if (values.size() != 0)
                        {
                            a.setValues(values);
                        }
                        else
                        {
                            attributes.add(a);
                        }
                    }
                    else
                    {
                        String value = values.get(0);
                        if (attrDef.isEntityIdAttribute())
                        {
                            entityId = value;
                        }
                        a.setValue(value);
                    }
                    attributes.add(a);
                }
            }
            if (entityId == null)
            {
                DistinguishedName name = new DistinguishedName(dn);
                LdapRdn rdn = name.getLdapRdn(name.size() - 1);
                if (rdn.getKey().equals(searchConfiguration.getLdapIdAttribute()))
                {
                    entityId = rdn.getValue();
                }
                else
                {
                    // TODO: throw exception???
                    return null;
                }
            }
            entity = internalCreateEntity(entityId, dn, attributes);
            entity.setLive(true);
        }
        return entity;
    }
}
