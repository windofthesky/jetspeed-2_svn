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

package org.apache.jetspeed.security.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.jetspeed.security.AttributeAlreadyExistsException;
import org.apache.jetspeed.security.AttributesNotExtendableException;
import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.AttributeReadOnlyException;
import org.apache.jetspeed.security.AttributesReadOnlyException;
import org.apache.jetspeed.security.AttributeRequiredException;
import org.apache.jetspeed.security.SecurityAttribute;
import org.apache.jetspeed.security.SecurityAttributeType;
import org.apache.jetspeed.security.AttributeTypeAlreadyDefinedException;
import org.apache.jetspeed.security.AttributeTypeNotFoundException;
import org.apache.jetspeed.security.SecurityAttributeTypes;
import org.apache.jetspeed.security.SecurityAttributes;
import org.apache.jetspeed.security.SecurityAttributeType.DataType;

/**
 * @version $Id$
 *
 */
public class SecurityAttributesImpl implements SecurityAttributes
{
    JetspeedPrincipal jp;
    @SuppressWarnings("unchecked")
    /*
     * unchecked collection to allow using default OJB managed collections which are not Generics extendable
     */
    private Collection avColl;
    private boolean readOnly;
    private boolean extendable;
    
    private HashMap<String, SecurityAttributeImpl> saMap = new HashMap<String, SecurityAttributeImpl>();
    
    @SuppressWarnings("unchecked")
    public SecurityAttributesImpl(JetspeedPrincipal jp, Collection avColl, boolean readOnly, boolean extendable)
    {
        this.jp = jp;
        this.avColl = avColl;
        this.readOnly = jp.getType().getAttributeTypes().isReadOnly() ? true : readOnly;
        this.extendable = jp.getType().getAttributeTypes().isExtendable() ? true : extendable;
        
        Map<String, SecurityAttributeType> stMap = jp.getType().getAttributeTypes().getAttributeTypeMap();
        for (Object avObj : avColl)
        {
            SecurityAttributeValue av = (SecurityAttributeValue)avObj;
            SecurityAttributeType sat = stMap.get(av.getName());
            saMap.put(av.getName(), new SecurityAttributeImpl(sat != null ? sat : new SecurityAttributeTypeImpl(av.getName()), av));
        }
    }

    public JetspeedPrincipal getPrincipal()
    {
        return jp;
    }

    public int size()
    {
        return saMap.size();
    }

    public boolean isReadOnly()
    {
        return readOnly;
    }
    
    public boolean isExtendable()
    {
        return extendable;
    }

    public SecurityAttributeTypes getSecurityAttributeTypes()
    {
        return jp.getType().getAttributeTypes();
    }

    public Set<String> getAttributeNames()
    {
        return Collections.unmodifiableSet(new HashSet<String>(saMap.keySet()));
    }

    public Set<String> getAttributeNames(String category)
    {
        HashSet<String> set = new HashSet<String>(saMap.size());
        for (SecurityAttribute sa : saMap.values())
        {
            if (sa.getCategory().equals(category))
            {
                set.add(sa.getName());
            }
        }
        return Collections.unmodifiableSet(set);
    }

    public Map<String, SecurityAttribute> getAttributeMap()
    {
        return Collections.unmodifiableMap(new HashMap<String, SecurityAttribute>(saMap));
    }

    public Map<String, SecurityAttribute> getAttributeMap(String category)
    {
        HashMap<String, SecurityAttribute> map = new HashMap<String,SecurityAttribute>(saMap.size());
        for (SecurityAttribute sa : saMap.values())
        {
            if (sa.getCategory().equals(category))
            {
                map.put(sa.getName(), sa);
            }
        }
        return Collections.unmodifiableMap(map);
    }
    
    public Map<String, SecurityAttribute> getInfoAttributeMap()
    {
        return getAttributeMap(INFO_CATEGORY);
    }

    public SecurityAttribute getAttribute(String name)
    {
        return saMap.get(name);
    }

    public SecurityAttribute getAttribute(String name, boolean create)
        throws AttributesReadOnlyException, AttributeTypeNotFoundException
    {
        if (isReadOnly())
        {
            throw new AttributesReadOnlyException();
        }
        
        SecurityAttributeType sat = getSecurityAttributeTypes().getAttributeTypeMap().get(name);
        
        if (sat == null)
        {
            throw new AttributeTypeNotFoundException();
        }
        
        SecurityAttribute sa = saMap.get(name);
        
        if (sa != null)
        {
            return sa;
        }
        else if ( create == false )
        {
            return null;
        }
        
        SecurityAttributeValue value = new SecurityAttributeValue(name);
        avColl.add(value);
        return saMap.put(name, new SecurityAttributeImpl(sat, value));
    }

    public SecurityAttribute addNewInfoAttribute(String name, DataType type)
        throws AttributesReadOnlyException, AttributeTypeAlreadyDefinedException, AttributeAlreadyExistsException, AttributesNotExtendableException
    {
        if (isReadOnly())
        {
            throw new AttributesReadOnlyException();
        }        
        if (!isExtendable())
        {
            throw new AttributesNotExtendableException();
        }        
        SecurityAttributeType sat = getSecurityAttributeTypes().getAttributeTypeMap().get(name);
        if (sat != null)
        {
            throw new AttributeTypeAlreadyDefinedException();
        }
        if (saMap.containsKey(name))
        {
            throw new AttributeAlreadyExistsException();
        }
        // TODO: making use of the DataType parameter (now ignored)
        SecurityAttributeValue value = new SecurityAttributeValue(name);
        avColl.add(value);
        return saMap.put(name, new SecurityAttributeImpl(new SecurityAttributeTypeImpl(name), value));
    }

    public void removeAttribute(String name) throws AttributesReadOnlyException, AttributeReadOnlyException, AttributeRequiredException
    {
        if (isReadOnly())
        {
            throw new AttributesReadOnlyException();
        }
        SecurityAttributeImpl sa = saMap.get(name);
        if (sa != null)
        {
            if (sa.isReadOnly())
            {
                throw new AttributeReadOnlyException();
            }
            if (sa.isRequired())
            {
                throw new AttributeRequiredException();
            }
            saMap.remove(name);
            avColl.remove(sa.getSecurityAttributeValue());
        }
    }
}
