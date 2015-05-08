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
package org.apache.jetspeed.security.spi.impl.cache;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.apache.jetspeed.cache.CacheElement;
import org.apache.jetspeed.cache.impl.EhCacheImpl;

/**
 * JSPMQueryEhCacheImpl - JetspeedSecurityPersistenceManager query EhCacheImpl
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id: $
 */
public class JSPMQueryEhCacheImpl extends EhCacheImpl
{
    /**
     * Construct new query cache supporting principal, permission, and
     * domain dependency driven eviction.
     *
     * @param ehcache backing EhCache
     */
    public JSPMQueryEhCacheImpl(Ehcache ehcache)
    {
        super(ehcache);
    }

    @Override
    public CacheElement get(Object key)
    {
        Element element = ehcache.get(key);
        if (element == null)
            return null;
        return new JSPMQueryEhCacheElementImpl(element);
    }

    @Override
    public void put(CacheElement element)
    {
        JSPMQueryEhCacheElementImpl elementImpl = (JSPMQueryEhCacheElementImpl)element;
        ehcache.put(elementImpl.getImplElement());
    }

    /**
     * Evict cached queries for principal. Operation should be rare, so
     * implemented using potentially expensive iteration over keys. Should
     * this become an issue, cache could maintain lookup maps of keys for
     * principal.
     *
     * @param principalId principal id
     */
    public void evictPrincipal(long principalId)
    {
        // validate id
        if (principalId <= 0)
        {
            return;
        }
        // iterate over cache elements
        for (Object key : ehcache.getKeys())
        {
            JSPMQueryCacheElement element = (JSPMQueryCacheElement)ehcache.get(key);
            if (element != null)
            {
                // evict elements with matching principal
                if (hasMatchingId(element.getPrincipalIds(), principalId))
                {
                    ehcache.removeQuiet(key);
                }
            }
        }
    }

    /**
     * Evict cached queries for permission. Operation should be rare, so
     * implemented using potentially expensive iteration over keys. Should
     * this become an issue, cache could maintain lookup maps of keys for
     * permission.
     *
     * @param permissionId permission id
     */
    public void evictPermission(long permissionId)
    {
        // validate id
        if (permissionId <= 0)
        {
            return;
        }
        // iterate over cache elements
        for (Object key : ehcache.getKeys())
        {
            JSPMQueryCacheElement element = (JSPMQueryCacheElement)ehcache.get(key);
            if (element != null)
            {
                // evict elements with matching permission
                if (hasMatchingId(element.getPermissionIds(), permissionId))
                {
                    ehcache.removeQuiet(key);
                }
            }
        }
    }

    /**
     * Evict cached queries for domain. Operation should be rare, so
     * implemented using potentially expensive iteration over keys. Should
     * this become an issue, cache could maintain lookup maps of keys for
     * domain.
     *
     * @param domainId domain id
     */
    public void evictDomain(long domainId)
    {
        // validate id
        if (domainId <= 0)
        {
            return;
        }
        // iterate over cache elements
        for (Object key : ehcache.getKeys())
        {
            JSPMQueryCacheElement element = (JSPMQueryCacheElement)ehcache.get(key);
            if (element != null)
            {
                // evict elements with matching domain
                if (hasMatchingId(element.getDomainIds(), domainId))
                {
                    ehcache.removeQuiet(key);
                }
            }
        }
    }

    /**
     * Check ids for matching id.
     *
     * @param ids ids array to check
     * @param matchingId id to check
     * @return matching
     */
    private static boolean hasMatchingId(long [] ids, long matchingId)
    {
        if (ids == null) {
            return false;
        }
        for (long id : ids)
        {
            if ((id == matchingId) || (id == JSPMQueryCacheElement.ANY_ID))
            {
                return true;
            }
        }
        return false;
    }
}
