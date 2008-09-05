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
package org.apache.jetspeed.security.mapping.ldap.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.AbstractContextMapper;

import org.apache.jetspeed.security.mapping.model.Attribute;
import org.apache.jetspeed.security.mapping.model.AttributeDef;
import org.apache.jetspeed.security.mapping.model.impl.AttributeImpl;
import org.apache.jetspeed.security.mapping.model.impl.EntityImpl;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public class DefaultEntityContextMapper extends AbstractContextMapper
{

    LDAPEntityDAOConfiguration searchConfiguration;

    public DefaultEntityContextMapper(
            LDAPEntityDAOConfiguration searchConfiguration)
    {
        this.searchConfiguration = searchConfiguration;
    }

    public Object doMapFromContext(DirContextOperations ctx)
    {
        String entityId = null;
        Set<Attribute> attributes = new HashSet<Attribute>();
        for (AttributeDef attrDef : searchConfiguration
                .getAttributeDefinitions())
        {

            String[] values = ctx.getStringAttributes(attrDef.getName());
            if (values != null && values.length > 0)
            {
                Attribute a = new AttributeImpl(attrDef);
                if (attrDef.isMultiValue())
                {
                    Collection attrValues = new ArrayList();
                    attrValues.addAll(Arrays.asList(values));
                    a.setValues(attrValues);
                } else
                {
                    if (attrDef.getName().equals(
                            searchConfiguration.getLdapIdAttribute()))
                    {
                        entityId = values[0];
                    }
                    a.setValue(values[0]);
                }
                attributes.add(a);
            }
        }
        if (entityId != null)
        {
            EntityImpl entity = new EntityImpl(searchConfiguration
                    .getEntityType(), entityId, searchConfiguration
                    .getAttributeDefinitions());
            entity.setAttributes(attributes);
            entity.setInternalId(ctx.getNameInNamespace().toString()); // set
                                                                       // full
                                                                       // DN
                                                                       // (incl.
                                                                       // base
                                                                       // DN) as
                                                                       // internal
                                                                       // ID
            entity.setType(searchConfiguration.getEntityType());
            return entity;
        } else
        {
            return null;
        }
    }

}
