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
package org.apache.jetspeed.security.activeauthentication;

import org.apache.jetspeed.cache.CacheElement;
import org.apache.jetspeed.cache.JetspeedCache;

import java.util.List;

/**
 * <p>
 * AuthenticationCacheBeanImpl
 * </p>
 * <p>
 * Short-lived cache implementation to bridge deficiencies in Java Login Modules and general Active Authentication patterns
 * based on Java login modules. Caches Authentication information across redirects, requests, and threads. The life-time
 * of this cached authentication information is meant to be very short lived. 
 * </p>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 *
 */
public class ActiveAuthenticationIdentityProviderImpl implements ActiveAuthenticationIdentityProvider
{
    JetspeedCache cache;
    List<String> sessionAttributes;
    
    public ActiveAuthenticationIdentityProviderImpl(JetspeedCache cache, List<String> sessionAttributes)
    {
        this.cache = cache;
        this.sessionAttributes = sessionAttributes;
    }
    
    public IdentityToken createIdentityToken(String seed)
    {
        String token = seed + "-" + String.valueOf(System.currentTimeMillis());
        return createToken(token);        
    }

    public IdentityToken createIdentityToken()
    {
        String token = String.valueOf(System.currentTimeMillis());
        return createToken(token);
    }

    private IdentityToken createToken(String token)
    {
        IdentityToken identityToken = new IdentityTokenImpl(token);
        CacheElement element = cache.createElement(token, identityToken);        
        cache.put(element);
        return identityToken;        
    }
    
    public void completeAuthenticationEvent(String token)
    {
        cache.remove(token);
    }
    
    public List<String> getSessionAttributeNames()
    {
        return this.sessionAttributes;
    }
    
}
