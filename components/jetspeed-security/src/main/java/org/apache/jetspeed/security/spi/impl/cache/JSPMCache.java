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

/**
 * JSPMCache - JetspeedSecurityPersistenceManager cache
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id: $
 */
public interface JSPMCache {

    /**
     * Negative caching null sentinel cache element value.
     */
    public static final Object CACHE_NULL = new DistributedCacheObject()
    {
        @Override
        public void notifyChange(int action)
        {
        }

        @Override
        public String toString()
        {
            return "JSPMCache.CACHE_NULL";
        }
    };

    /**
     * Cache element wildcard dependent id.
     */
    public static final Long ANY_ID = JSPMQueryCacheElement.ANY_ID;

    /**
     * Get principal from instance cache.
     *
     * @param id principal id
     * @return principal instance or CACHE_NULL
     */
    Object getPrincipal(Long id);

    /**
     * Put principal in instance cache.
     *
     * @param id principal id
     * @param principal principal instance or CACHE_NULL
     */
    void putPrincipal(Long id, Object principal);

    /**
     * Evict principal from instance cache and notify local
     * and distributed listeners. Also evicts related queries
     * from query caches.
     *
     * @param id principal id
     */
    void evictPrincipal(Long id);

    /**
     * Get permission from instance cache.
     *
     * @param id permission id
     * @return permission instance or CACHE_NULL
     */
    Object getPermission(Long id);

    /**
     * Put permission in instance cache.
     *
     * @param id permission id
     * @param permission permission instance or CACHE_NULL
     */
    void putPermission(Long id, Object permission);

    /**
     * Evict permission from instance cache and notify local
     * and distributed listeners. Also evicts related queries
     * from query caches.
     *
     * @param id permission id
     */
    void evictPermission(Long id);

    /**
     * Get domain from instance cache.
     *
     * @param id domain id
     * @return domain instance or CACHE_NULL
     */
    Object getDomain(Long id);

    /**
     * Put domain in instance cache.
     *
     * @param id domain id
     * @param domain domain instance or CACHE_NULL
     */
    void putDomain(Long id, Object domain);

    /**
     * Evict domain from instance cache and notify local
     * and distributed listeners. Also evicts related queries
     * from query caches.
     *
     * @param id domain id
     */
    void evictDomain(Long id);

    /**
     * Get principal query result from query cache.
     *
     * @param key principal query key
     * @return principle query result or CACHE_NULL
     */
    Object getPrincipalQuery(String key);

    /**
     * Put principal query result and dependent instance ids
     * in query cache. Dependent instance ids are used to
     * evict based on instance cache notifications.
     *
     * @param key principal query key
     * @param principalId dependent principal id, ANY_ID, or null
     * @param permissionId dependent permission id, ANY_ID, or null
     * @param domainId dependent domain id, ANY_ID, or null
     * @param query query result
     */
    void putPrincipalQuery(String key, Long principalId, Long permissionId, Long domainId, Object query);

    /**
     * Get principal association query result from query cache.
     *
     * @param key principal association query key
     * @return principle association query result or CACHE_NULL
     */
    Object getAssociationQuery(String key);

    /**
     * Put principal association query result and dependent
     * instance ids in query cache. Dependent instance ids are
     * used to evict based on instance cache notifications.
     *
     * @param key principal association query key
     * @param principalId dependent principal id, ANY_ID, or null
     * @param otherPrincipalIds dependent principal ids or null
     * @param domainId dependent domain id, ANY_ID, or null
     * @param otherDomainId dependent domain id, ANY_ID, or null
     * @param query query result
     */
    void putAssociationQuery(String key, Long principalId, Long [] otherPrincipalIds, Long domainId, Long otherDomainId,
                             Object query);

    /**
     * Get principal password credential query result from query cache.
     *
     * @param key principal password credential query key
     * @return principle password credential query result or CACHE_NULL
     */
    Object getPasswordCredentialQuery(String key);

    /**
     * Put principal password credential query result and
     * dependent instance ids in query cache. Dependent
     * instance ids are used to evict based on instance cache
     * notifications.
     *
     * @param key principal password credential query key
     * @param principalId dependent principal id, ANY_ID, or null
     * @param domainId dependent domain id, ANY_ID, or null
     * @param query query result
     */
    void putPasswordCredentialQuery(String key, Long principalId, Long domainId, Object query);

    /**
     * Get permission query result from query cache.
     *
     * @param key permission query key
     * @return permission query result or CACHE_NULL
     */
    Object getPermissionQuery(String key);

    /**
     * Put permission query result and dependent instance ids
     * in query cache. Dependent instance ids are used to
     * evict based on instance cache notifications.
     *
     * @param key permission query key
     * @param principalId dependent principal id, ANY_ID, or null
     * @param otherPrincipalIds dependent principal ids or null
     * @param permissionId dependent permission id, ANY_ID, or null
     * @param domainId dependent domain id, ANY_ID, or null
     * @param query query result
     */
    void putPermissionQuery(String key, Long principalId, Long [] otherPrincipalIds, Long permissionId, Long domainId,
                            Object query);

    /**
     * Get domain query result from query cache.
     *
     * @param key domain query key
     * @return domain query result or CACHE_NULL
     */
    Object getDomainQuery(String key);

    /**
     * Put domain query result and dependent instance ids in
     * query cache. Dependent instance ids are used to evict
     * based on instance cache notifications.
     *
     * @param key domain query key
     * @param domainId dependent domain id, ANY_ID, or null
     * @param query query result
     */
    void putDomainQuery(String key, Long domainId, Object query);

    /**
     * Return sum of the number of elements in all instance
     * and query caches.
     *
     * @return cache size
     */
    int size();

    /**
     * Clear all instance and query caches.
     */
    void clear();

    /**
     * Return distributed cache configuration.
     *
     * @return distributed
     */
    boolean isDistributed();
}
