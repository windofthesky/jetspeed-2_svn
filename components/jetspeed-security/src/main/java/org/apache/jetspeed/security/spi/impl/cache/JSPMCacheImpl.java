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

import org.apache.jetspeed.cache.DistributedCacheObject;
import org.apache.jetspeed.cache.JetspeedCache;
import org.apache.jetspeed.cache.JetspeedCacheEventAdapter;
import org.apache.jetspeed.cache.JetspeedCacheEventListener;
import org.apache.jetspeed.cache.impl.EhCacheDistributedElementImpl;
import org.apache.jetspeed.cache.impl.EhCacheDistributedImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JSPMCacheImpl - JetspeedSecurityPersistenceManager cache
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id: $
 */
public class JSPMCacheImpl implements JSPMCache
{
    private static final Logger log = LoggerFactory.getLogger(JSPMCacheImpl.class);

    // distributed instance caches
    private EhCacheDistributedImpl principalCache;
    private EhCacheDistributedImpl permissionCache;
    private EhCacheDistributedImpl domainCache;

    // distributed instance cache listeners
    private JetspeedCacheEventListener principalCacheListener;
    private JetspeedCacheEventListener permissionCacheListener;
    private JetspeedCacheEventListener domainCacheListener;

    // local query caches
    private JSPMQueryEhCacheImpl principalQueryCache;
    private JSPMQueryEhCacheImpl associationQueryCache;
    private JSPMQueryEhCacheImpl passwordCredentialQueryCache;
    private JSPMQueryEhCacheImpl permissionQueryCache;
    private JSPMQueryEhCacheImpl domainQueryCache;

    /**
     * Create JSPMCache component instance.
     *
     * @param principalCache distributed principal instance cache
     * @param permissionCache distributed permission instance cache
     * @param domainCache distributed domain instance cache
     * @param principalQueryCache local principal query cache
     * @param associationQueryCache local principal association query cache
     * @param passwordCredentialQueryCache local principal password credential query cache
     * @param permissionQueryCache local permission query cache
     * @param domainQueryCache local domain query cache
     */
    public JSPMCacheImpl(EhCacheDistributedImpl principalCache,
                         EhCacheDistributedImpl permissionCache,
                         EhCacheDistributedImpl domainCache,
                         JSPMQueryEhCacheImpl principalQueryCache,
                         JSPMQueryEhCacheImpl associationQueryCache,
                         JSPMQueryEhCacheImpl passwordCredentialQueryCache,
                         JSPMQueryEhCacheImpl permissionQueryCache,
                         JSPMQueryEhCacheImpl domainQueryCache)
    {
        this.principalCache = principalCache;
        this.permissionCache = permissionCache;
        this.domainCache = domainCache;

        this.principalQueryCache = principalQueryCache;
        this.associationQueryCache = associationQueryCache;
        this.passwordCredentialQueryCache = passwordCredentialQueryCache;
        this.permissionQueryCache = permissionQueryCache;
        this.domainQueryCache = domainQueryCache;
    }

    /**
     * Initialize caches and listeners.
     */
    public void initialize()
    {
        // principal cache listener
        principalCacheListener = new JetspeedCacheEventAdapter()
        {
            @Override
            public void notifyElementRemoved(JetspeedCache cache, boolean local, Object key, Object element)
            {
                // removed notification sent on remove and update:
                // clear query cache elements base on principal id
                if (key instanceof Long)
                {
                    long principalId = (Long)key;
                    principalQueryCache.evictPrincipal(principalId);
                    associationQueryCache.evictPrincipal(principalId);
                    passwordCredentialQueryCache.evictPrincipal(principalId);
                    permissionQueryCache.evictPrincipal(principalId);
                }
            }
        };
        principalCache.addEventListener(principalCacheListener, false);

        // permission cache listener
        permissionCacheListener = new JetspeedCacheEventAdapter()
        {
            @Override
            public void notifyElementRemoved(JetspeedCache cache, boolean local, Object key, Object element)
            {
                // removed notification sent on remove and update:
                // clear query cache elements base on permission id
                if (key instanceof Long)
                {
                    long permissionId = (Long)key;
                    principalQueryCache.evictPermission(permissionId);
                    permissionQueryCache.evictPermission(permissionId);
                }
            }
        };
        permissionCache.addEventListener(permissionCacheListener, false);

        // domain cache listener
        domainCacheListener = new JetspeedCacheEventAdapter()
        {
            @Override
            public void notifyElementRemoved(JetspeedCache cache, boolean local, Object key, Object element)
            {
                // removed notification sent on remove and update:
                // clear query cache elements base on domain id
                if (key instanceof Long)
                {
                    long domainId = (Long)key;
                    principalQueryCache.evictDomain(domainId);
                    associationQueryCache.evictDomain(domainId);
                    passwordCredentialQueryCache.evictDomain(domainId);
                    domainQueryCache.evictDomain(domainId);
                }
            }
        };
        domainCache.addEventListener(domainCacheListener, false);
    }

    /**
     * Terminate caches and listeners.
     */
    public void terminate()
    {
        // distributed cache listeners
        principalCache.removeEventListener(principalCacheListener, false);
        principalCache.removeEventListener(principalCacheListener, true);
        permissionCache.removeEventListener(permissionCacheListener, false);
        permissionCache.removeEventListener(permissionCacheListener, true);
        domainCache.removeEventListener(domainCacheListener, false);
        domainCache.removeEventListener(domainCacheListener, true);

        // clear caches
        clear();
    }

    @Override
    public Object getPrincipal(Long id)
    {
        EhCacheDistributedElementImpl cacheElementImpl = (EhCacheDistributedElementImpl)principalCache.get(id);
        return ((cacheElementImpl != null) ? cacheElementImpl.getContent() : null);
    }

    @Override
    public void putPrincipal(Long id, Object principal)
    {
        principalCache.put(new EhCacheDistributedElementImpl(id, (DistributedCacheObject) principal));
    }

    @Override
    public void evictPrincipal(Long id)
    {
        evictFromDistributedCache(principalCache, id);
    }

    @Override
    public Object getPermission(Long id)
    {
        EhCacheDistributedElementImpl cacheElementImpl = (EhCacheDistributedElementImpl)permissionCache.get(id);
        return ((cacheElementImpl != null) ? cacheElementImpl.getContent() : null);
    }

    @Override
    public void putPermission(Long id, Object permission)
    {
        permissionCache.put(new EhCacheDistributedElementImpl(id, (DistributedCacheObject) permission));
    }

    @Override
    public void evictPermission(Long id)
    {
        evictFromDistributedCache(permissionCache, id);
    }

    @Override
    public Object getDomain(Long id)
    {
        EhCacheDistributedElementImpl cacheElementImpl = (EhCacheDistributedElementImpl)domainCache.get(id);
        return ((cacheElementImpl != null) ? cacheElementImpl.getContent() : null);
    }

    @Override
    public void putDomain(Long id, Object domain)
    {
       domainCache.put(new EhCacheDistributedElementImpl(id, (DistributedCacheObject) domain));
    }

    @Override
    public void evictDomain(Long id)
    {
        evictFromDistributedCache(domainCache, id);
    }

    @Override
    public Object getPrincipalQuery(String key)
    {
        JSPMQueryEhCacheElementImpl cacheElementImpl = (JSPMQueryEhCacheElementImpl)principalQueryCache.get(key);
        return ((cacheElementImpl != null) ? cacheElementImpl.getImplElement().getObjectValue() : null);
    }

    @Override
    public void putPrincipalQuery(String key, Long principalId, Long permissionId, Long domainId, Object query)
    {
        principalQueryCache.put(newJSPMQueryEhCacheElementImpl(key, principalId, null, permissionId, domainId, null,
                query));
    }

    @Override
    public Object getAssociationQuery(String key)
    {
        JSPMQueryEhCacheElementImpl cacheElementImpl = (JSPMQueryEhCacheElementImpl)associationQueryCache.get(key);
        return ((cacheElementImpl != null) ? cacheElementImpl.getImplElement().getObjectValue() : null);
    }

    @Override
    public void putAssociationQuery(String key, Long principalId, Long [] otherPrincipalIds, Long domainId,
                                    Long otherDomainId, Object query)
    {
        associationQueryCache.put(newJSPMQueryEhCacheElementImpl(key, principalId, otherPrincipalIds, null, domainId,
                otherDomainId, query));
    }

    @Override
    public Object getPasswordCredentialQuery(String key)
    {
        JSPMQueryEhCacheElementImpl cacheElementImpl = (JSPMQueryEhCacheElementImpl)passwordCredentialQueryCache.get(key);
        return ((cacheElementImpl != null) ? cacheElementImpl.getImplElement().getObjectValue() : null);
    }

    @Override
    public void putPasswordCredentialQuery(String key, Long principalId, Long domainId, Object query)
    {
        passwordCredentialQueryCache.put(newJSPMQueryEhCacheElementImpl(key, principalId, null, null, domainId, null,
                query));
    }

    @Override
    public Object getPermissionQuery(String key)
    {
        JSPMQueryEhCacheElementImpl cacheElementImpl = (JSPMQueryEhCacheElementImpl)permissionQueryCache.get(key);
        return ((cacheElementImpl != null) ? cacheElementImpl.getImplElement().getObjectValue() : null);
    }

    @Override
    public void putPermissionQuery(String key, Long principalId, Long [] otherPrincipalIds, Long permissionId,
                                   Long domainId, Object query)
    {
        permissionQueryCache.put(newJSPMQueryEhCacheElementImpl(key, principalId, otherPrincipalIds, permissionId,
                domainId, null, query));
    }

    @Override
    public Object getDomainQuery(String key)
    {
        JSPMQueryEhCacheElementImpl cacheElementImpl = (JSPMQueryEhCacheElementImpl)domainQueryCache.get(key);
        return ((cacheElementImpl != null) ? cacheElementImpl.getImplElement().getObjectValue() : null);
    }

    @Override
    public void putDomainQuery(String key, Long domainId, Object query)
    {
        domainQueryCache.put(newJSPMQueryEhCacheElementImpl(key, null, null, null, domainId, null, query));
    }

    @Override
    public int size()
    {
        // compute size from sum of all cache sizes
        int size = 0;
        size += principalCache.getSize();
        size += permissionCache.getSize();
        size += domainCache.getSize();
        size += principalQueryCache.getSize();
        size += associationQueryCache.getSize();
        size += passwordCredentialQueryCache.getSize();
        size += permissionQueryCache.getSize();
        size += domainQueryCache.getSize();
        return size;
    }

    @Override
    public void clear()
    {
        // clear all caches
        principalCache.clear();
        permissionCache.clear();
        domainCache.clear();
        principalQueryCache.clear();
        associationQueryCache.clear();
        passwordCredentialQueryCache.clear();
        permissionQueryCache.clear();
        domainQueryCache.clear();
    }

    @Override
    public boolean isDistributed() {
        // return distributed status for instance caches
        return (principalCache.isDistributed() && permissionCache.isDistributed() && domainCache.isDistributed());
    }

    /**
     * Construct new query cache element with dependent instance
     * cache ids.
     *
     * @param key cache key
     * @param principalId dependent principal id, ANY_ID, or null
     * @param otherPrincipalIds dependent principal ids or null
     * @param permissionId dependent permission id, ANY_ID, or null
     * @param domainId dependent domain id, ANY_ID, or null
     * @param otherDomainId dependent domain id, ANY_ID, or null
     * @param element cache element value
     * @return new cache element
     */
    private static JSPMQueryEhCacheElementImpl newJSPMQueryEhCacheElementImpl(String key, Long principalId,
                                                                              Long [] otherPrincipalIds,
                                                                              Long permissionId, Long domainId,
                                                                              Long otherDomainId, Object element)
    {
        JSPMQueryEhCacheElementImpl cacheElementImpl = new JSPMQueryEhCacheElementImpl(key, element);
        if (principalId != null)
        {
            if ((otherPrincipalIds != null) && (otherPrincipalIds.length > 0))
            {
                long [] principalIds = new long[otherPrincipalIds.length+1];
                principalIds[0] = principalId;
                for (int i = 0, limit = otherPrincipalIds.length; (i < limit); i++)
                {
                    principalIds[i+1] = otherPrincipalIds[i];
                }
                cacheElementImpl.setPrincipalIds(principalIds);
            }
            else
            {
                cacheElementImpl.setPrincipalIds(new long[]{principalId});
            }
        }
        else if ((otherPrincipalIds != null) && (otherPrincipalIds.length > 0))
        {
            long [] principalIds = new long[otherPrincipalIds.length];
            for (int i = 0, limit = otherPrincipalIds.length; (i < limit); i++)
            {
                principalIds[i] = otherPrincipalIds[i];
            }
            cacheElementImpl.setPrincipalIds(principalIds);

        }
        if (permissionId != null)
        {
            cacheElementImpl.setPermissionIds(new long[]{permissionId});
        }
        if (domainId != null)
        {
            if (otherDomainId != null)
            {
                cacheElementImpl.setDomainIds(new long[]{domainId, otherDomainId});
            }
            else
            {
                cacheElementImpl.setDomainIds(new long[]{domainId});
            }
        }
        else if (otherDomainId != null)
        {
            cacheElementImpl.setDomainIds(new long[]{otherDomainId});
        }
        return cacheElementImpl;
    }

    /**
     * Evict with notification from distributed instance cache.
     * Notification is sent even if instance is not in the cache
     * so that eviction is done in distributed peers.
     *
     * @param cache distributed instance cache
     * @param id instance id to evict and notify
     */
    private static void evictFromDistributedCache(EhCacheDistributedImpl cache, Long id)
    {
        // force remove to notify local and remote listeners
        if (!cache.remove(id))
        {
            cache.put(new EhCacheDistributedElementImpl(id, (DistributedCacheObject) CACHE_NULL));
            cache.remove(id);
        }
    }
}
