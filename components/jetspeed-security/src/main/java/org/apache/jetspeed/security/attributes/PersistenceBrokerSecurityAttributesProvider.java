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
package org.apache.jetspeed.security.attributes;

import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.jetspeed.security.BasePrincipal;
import org.apache.jetspeed.security.GroupPrincipal;
import org.apache.jetspeed.security.RemotePrincipal;
import org.apache.jetspeed.security.RolePrincipal;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.impl.GroupPrincipalImpl;
import org.apache.jetspeed.security.impl.RemotePrincipalImpl;
import org.apache.jetspeed.security.impl.RolePrincipalImpl;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;
import org.apache.jetspeed.security.SecurityException;

/**
 * <p>
 * Persistence for Security Attributes including Portlet API User Attributes
 * </p>
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class PersistenceBrokerSecurityAttributesProvider 
        extends PersistenceBrokerDaoSupport 
        implements SecurityAttributesProvider
{    
    public PersistenceBrokerSecurityAttributesProvider()
    {
    }
        
    public SecurityAttributes retrieveAttributes(Principal principal)  throws SecurityException
    {
        if (!(principal instanceof UserPrincipal))
            throw new SecurityException(
                    SecurityException.UNKNOWN_PRINCIPAL_TYPE.create(principal.getClass().toString())); 
        BasePrincipal bp = (BasePrincipal)principal;
        Criteria c = new Criteria();        
        c.addEqualTo("principalId", bp.getId());
        QueryByCriteria query = QueryFactory.newQuery(SecurityAttributeImpl.class, c);
        Map<String, SecurityAttribute> result = new HashMap<String, SecurityAttribute>();
        Collection<SecurityAttribute> queryResult = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        for (SecurityAttribute sa : queryResult)
        {
            result.put(sa.getName(), sa);
        }
        return new SecurityAttributesImpl(principal, result);        
    }
    
    public SecurityAttributes createSecurityAttributes(Principal principal) throws SecurityException
    {
        if (!(principal instanceof UserPrincipal))
            throw new SecurityException(
                    SecurityException.UNKNOWN_PRINCIPAL_TYPE.create(principal.getClass().toString())); 
        return new SecurityAttributesImpl(principal, new HashMap<String, SecurityAttribute>());
    }
    
    public void deleteAttributes(Principal principal) throws SecurityException
    {
        if (!(principal instanceof BasePrincipal))
            throw new SecurityException(
                    SecurityException.UNKNOWN_PRINCIPAL_TYPE.create(principal.getClass().toString())); 
        BasePrincipal bp = (BasePrincipal)principal;
        Criteria c = new Criteria();        
        
        c.addEqualTo("principalId", bp.getId());
        QueryByCriteria query = QueryFactory.newQuery(SecurityAttributeImpl.class, c);        
        getPersistenceBrokerTemplate().deleteByQuery(query);
    }
    
    
    public void saveAttributes(SecurityAttributes after) throws SecurityException
    {
        SecurityAttributes before = this.retrieveAttributes(after.getPrincipal());
        // first pass, get any deletes
        for (String key : before.getAttributes().keySet())
        {
            SecurityAttribute aa = after.getAttributes().get(key);
            if (aa == null)
            {
                // it was there before, now its gone, so delete it
                SecurityAttribute ba = before.getAttributes().get(key);                
                getPersistenceBrokerTemplate().delete(ba);
            }
            else
            {
                SecurityAttribute ba = before.getAttributes().get(key);
                if (diff(aa, ba))
                {
                    copy(aa, ba);
                    getPersistenceBrokerTemplate().store(ba);
                }
            }            
        }
        // second pass, get any adds (updates already processed)
        for (String key : after.getAttributes().keySet())
        {
            SecurityAttribute ba = before.getAttributes().get(key);
            // if found do nothing, otherwise add it
            if (ba == null)
            {
                SecurityAttribute aa = after.getAttributes().get(key);                
                getPersistenceBrokerTemplate().store(aa);
            }
        }        
    }

    private void copy(SecurityAttribute source, SecurityAttribute dest)
    {
        dest.setValue(source.getValue());
    }
    
    private boolean diff(SecurityAttribute a1, SecurityAttribute a2)
    {
        if (a1.getName().equals(a2.getName()) && a1.getType().equals(a2.getType())
                && a1.getValue().equals(a2.getValue()))
            return false;
        return true;
    }

    public Collection<SecurityAttributes> lookupAttributes(String name, String value) throws SecurityException
    {
        Criteria c = new Criteria();        
        c.addEqualTo("name", name);
        c.addEqualTo("value", value);
        QueryByCriteria query = QueryFactory.newQuery(SecurityAttributeImpl.class, c);
        Map<Long, SecurityAttributes> uniques = new HashMap<Long, SecurityAttributes>();
        Collection<SecurityAttribute> queryResult = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        for (SecurityAttribute attrib : queryResult)
        {
            SecurityAttributes sa = uniques.get(attrib.getPrincipalId());
            if (sa == null)
            {
                BasePrincipal principal = createPrincipalFromAttribute(attrib);
                Map<String, SecurityAttribute> result = new HashMap<String, SecurityAttribute>();
                result.put(attrib.getName(), attrib);
                sa = new SecurityAttributesImpl(principal, result);
                uniques.put(attrib.getPrincipalId(), sa);
            }
            else
            {
                sa.getAttributes().put(attrib.getName(), attrib);
            }
        }
        Collection<SecurityAttributes> resultSet = new LinkedList<SecurityAttributes>();
        for (Map.Entry<Long, SecurityAttributes> e : uniques.entrySet())
        {
            resultSet.add(e.getValue());
        }
        return resultSet;
   }

    public BasePrincipal createPrincipalFromAttribute(SecurityAttribute attr) throws SecurityException
    {
        if (attr.getType().equals(UserPrincipal.PRINCIPAL_TYPE))
            return new UserPrincipalImpl(attr.getPrincipalId(), attr.getName());
        else if (attr.getType().equals(GroupPrincipal.PRINCIPAL_TYPE))
            return new GroupPrincipalImpl(attr.getPrincipalId(), attr.getName());
        else if (attr.getType().equals(RolePrincipal.PRINCIPAL_TYPE))
            return new RolePrincipalImpl(attr.getPrincipalId(), attr.getName());
        else if (attr.getType().equals(RemotePrincipal.PRINCIPAL_TYPE))
            return new RemotePrincipalImpl(attr.getPrincipalId(), attr.getName());        
        else
            throw new SecurityException(
                    SecurityException.UNKNOWN_PRINCIPAL_TYPE.create(attr.getType())); 
   }
}