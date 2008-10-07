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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.SecurityAttribute;
import org.apache.jetspeed.security.SecurityAttributeType;
import org.apache.jetspeed.security.SecurityAttributeTypes;
import org.apache.jetspeed.security.SecurityAttributes;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.spi.impl.SynchronizationStateAccess;

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
    private boolean persistent;
    
    private HashMap<String, SecurityAttributeImpl> saMap = new HashMap<String, SecurityAttributeImpl>();
    
    @SuppressWarnings("unchecked")
    public SecurityAttributesImpl(JetspeedPrincipal jp)
    {
        this.jp = jp;
        if (!jp.isTransient())
        {
            throw new IllegalArgumentException("Provided JetspeedPrincipal is not transient");
        }
        this.avColl =  new ArrayList<SecurityAttributeValue>();
        this.persistent = false;
        this.readOnly = false;
        this.extendable = false;
    }

    @SuppressWarnings("unchecked")
    public SecurityAttributesImpl(JetspeedPrincipal jp, Collection avColl, boolean readOnly, boolean extendable)
    {
        this.jp = jp;
        this.avColl = avColl;
        this.persistent = true;
        this.readOnly = jp.getType().getAttributeTypes().isReadOnly() ? true : readOnly;
        this.extendable = jp.getType().getAttributeTypes().isExtendable() ? true : extendable;
        
        Map<String, SecurityAttributeType> stMap = jp.getType().getAttributeTypes().getAttributeTypeMap();
        for (Object avObj : avColl)
        {
            SecurityAttributeValue av = (SecurityAttributeValue)avObj;
            SecurityAttributeType sat = stMap.get(av.getName());
            saMap.put(av.getName(), new SecurityAttributeImpl(sat != null ? sat : new SecurityAttributeTypeImpl(av.getName()), av, true));
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

    public boolean isDefinedAttribute(String name)
    {
        return jp.getType().getAttributeTypes().getAttributeTypeMap().containsKey(name);
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
        return getAttributeMap(SecurityAttributeType.INFO_CATEGORY);
    }
    
    public Map<String, String> getInfoMap()
    {
        HashMap<String, String> map = new HashMap<String,String>(saMap.size());
        for (SecurityAttribute sa : saMap.values())
        {
            if (sa.getCategory().equals(SecurityAttributeType.INFO_CATEGORY))
            {
                map.put(sa.getName(), sa.getStringValue());
            }
        }
        return Collections.unmodifiableMap(map);
    }

    public SecurityAttribute getAttribute(String name)
    {
        return saMap.get(name);
    }

    public SecurityAttribute getAttribute(String name, boolean create)
        throws SecurityException
    {
        SecurityAttributeImpl sa = saMap.get(name);
        
        if (sa != null)
        {
            return sa;
        }
        else if (!create)
        {
            return null;
        }
        
        if (isReadOnly() && !isSynchronizing())
        {
            throw new SecurityException(SecurityException.ATTRIBUTES_ARE_READ_ONLY.createScoped(getPrincipal().getType().getName()));
        }
        
        SecurityAttributeType sat = getSecurityAttributeTypes().getAttributeTypeMap().get(name);
                
        if (sat == null)
        {
            if (!isExtendable() && !isSynchronizing())
            {
                throw new SecurityException(SecurityException.ATTRIBUTES_NOT_EXTENDABLE.createScoped(getPrincipal().getType().getName()));
            }
            // New INFO_CATEGORY attribute, always of type STRING
            SecurityAttributeValue value = new SecurityAttributeValue(name);
            avColl.add(value);
            sa = new SecurityAttributeImpl(new SecurityAttributeTypeImpl(name), value, persistent);
            
        }
        else
        {
            SecurityAttributeValue value = new SecurityAttributeValue(name);
            avColl.add(value);
            sa = new SecurityAttributeImpl(sat, value, persistent);
        }
        
        saMap.put(name, sa);
        return sa;
    }

    public void removeAttribute(String name) throws SecurityException
    {
        if (isReadOnly() && !isSynchronizing())
        {
            throw new SecurityException(SecurityException.ATTRIBUTES_ARE_READ_ONLY.createScoped(getPrincipal().getType().getName()));
        }
        SecurityAttributeImpl sa = saMap.get(name);
        if (sa != null)
        {
            if (sa.isReadOnly() && !isSynchronizing())
            {
                throw new SecurityException(SecurityException.ATTRIBUTE_IS_READ_ONLY.createScoped(getPrincipal().getType().getName(), name));
            }
            if (sa.isRequired() && !isSynchronizing())
            {
                throw new SecurityException(SecurityException.ATTRIBUTE_IS_REQUIRED.createScoped(getPrincipal().getType().getName(), name));
            }
            saMap.remove(name);
            avColl.remove(sa.getSecurityAttributeValue());
        }
    }
    
    protected boolean isSynchronizing(){
        return SynchronizationStateAccess.isSynchronizing();
    }
}
