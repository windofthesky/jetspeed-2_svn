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

import net.sf.ehcache.Element;
import org.apache.jetspeed.cache.impl.EhCacheElementImpl;

import java.io.Serializable;

/**
 * JSPMQueryEhCacheElementImpl - JetspeedSecurityPersistenceManager query EhCacheElementImpl
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id: $
 */
public class JSPMQueryEhCacheElementImpl extends EhCacheElementImpl
{
    /**
     * Construct new EhCache element variant.
     *
     * @param element EhCache element
     */
    public JSPMQueryEhCacheElementImpl(Element element)
    {
        super((JSPMQueryCacheElement)element);
    }

    /**
     * Construct new EhCache element variant.
     *
     * @param key EhCache element key
     * @param key EhCache element value
     */
    public JSPMQueryEhCacheElementImpl(Serializable key, Serializable value)
    {
        super(new JSPMQueryCacheElement(key, value));
    }

    /**
     * Construct new EhCache element variant.
     *
     * @param key EhCache element key
     * @param key EhCache element value
     */
    public JSPMQueryEhCacheElementImpl(Serializable key, Object value)
    {
        super(new JSPMQueryCacheElement(key, value));
    }

    public long [] getPrincipalIds()
    {
        return ((JSPMQueryCacheElement)element).getPrincipalIds();
    }

    public void setPrincipalIds(long [] principalIds)
    {
        ((JSPMQueryCacheElement)element).setPrincipalIds(principalIds);
    }

    public long [] getPermissionIds()
    {
        return ((JSPMQueryCacheElement)element).getPermissionIds();
    }

    public void setPermissionIds(long [] permissionIds)
    {
        ((JSPMQueryCacheElement)element).setPermissionIds(permissionIds);
    }

    public long [] getDomainIds()
    {
        return ((JSPMQueryCacheElement)element).getDomainIds();
    }

    public void setDomainIds(long [] domainIds)
    {
        ((JSPMQueryCacheElement)element).setDomainIds(domainIds);
    }
}
