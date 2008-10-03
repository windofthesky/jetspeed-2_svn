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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.ldap.LdapContext;

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
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.support.LdapUtils;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public class EntityFactoryImpl implements EntityFactory
{
    LDAPEntityDAOConfiguration searchConfiguration;

    public EntityFactoryImpl(LDAPEntityDAOConfiguration searchConfiguration)
    {
        this.searchConfiguration = searchConfiguration;
    }

    private EntityImpl internalCreateEntity(String entityId, String internalId, Set<Attribute> attributes)
    {
        EntityImpl entity = new EntityImpl(searchConfiguration.getEntityType(), entityId, searchConfiguration.getAttributeDefinitions());
        entity.setAttributes(attributes);
        if (internalId != null)
        {
            entity.setInternalId(internalId);
        }
        entity.setType(searchConfiguration.getEntityType());
        return entity;
    }

    public Entity createEntity(JetspeedPrincipal principal)
    {
        Set<Attribute> ldapAttrValues = new HashSet<Attribute>();
        SecurityAttributes sas = principal.getSecurityAttributes();
        for (AttributeDef attrDef : searchConfiguration.getAttributeDefinitions())
        {
            SecurityAttribute sa = sas.getAttribute(attrDef.getMappedName());
            if (sa != null)
            {
                // currently only single-valued attributes are supported
                ldapAttrValues.add(new AttributeImpl(attrDef));
            }
        }
        return internalCreateEntity(principal.getName(), null, ldapAttrValues);
    }

    public String[] getStringAttributes(Attributes originalAttrs, String name)
    {
        String[] attributes;
        javax.naming.directory.Attribute attribute = originalAttrs.get(name);
        if (attribute != null && attribute.size() > 0)
        {
            attributes = new String[attribute.size()];
            for (int i = 0; i < attribute.size(); i++)
            {
                try
                {
                    attributes[i] = (String) attribute.get(i);
                }
                catch (NamingException e)
                {
                    throw LdapUtils.convertLdapException(e);
                }
            }
        }
        else
        {
            return null;
        }
        return attributes;
    }

    public Entity createEntity(DirContext ctx)
    {
        String entityId = null;
        Entity entity = null;
        Set<Attribute> attributes = new HashSet<Attribute>();
        for (AttributeDef attrDef : searchConfiguration.getAttributeDefinitions())
        {
            String[] values = null;
            try
            {
                
                values = getStringAttributes(ctx.getAttributes(""), attrDef.getName());
            }
            catch (NamingException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (values != null && values.length > 0)
            {
                Attribute a = new AttributeImpl(attrDef);
                if (attrDef.isMultiValue())
                {
                    Collection<String> attrValues = new ArrayList<String>();
                    attrValues.addAll(Arrays.asList(values));
                    // remove the dummy value for required fields when present.
                    if (attrDef.isRequired() && attrDef.getRequiredDefaultValue() != null && attrValues.contains(attrDef.getRequiredDefaultValue()))
                    {
                        attrValues.remove(attrDef.getRequiredDefaultValue());
                    }
                    if (attrValues.size() != 0)
                    {
                        a.setValues(attrValues);
                        attributes.add(a);
                    }
                }
                else
                {
                    if (attrDef.getName().equals(searchConfiguration.getLdapIdAttribute()))
                    {
                        entityId = values[0];
                    }
                    if (values[0] != null)
                    {
                        // check if the value is not the required default value (a dummy value) If it is, ignore the attribute.
                        if (!(attrDef.isRequired() && attrDef.getRequiredDefaultValue() != null && values[0].equals(attrDef.getRequiredDefaultValue())))
                        {
                            a.setValue(values[0]);
                            attributes.add(a);
                        }
                    }
                }
            }
        }
        try
        {
            entity = internalCreateEntity(entityId, ctx.getNameInNamespace(), attributes);
        }
        catch (NamingException e)
        {
            entity = null;
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return entity;
    }
}
