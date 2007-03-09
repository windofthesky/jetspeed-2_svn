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

package org.apache.jetspeed.cache.impl;

import java.util.StringTokenizer;

import net.sf.ehcache.Element;

import org.apache.jetspeed.cache.CacheElement;
import org.apache.jetspeed.cache.JetspeedCache;
import org.apache.jetspeed.cache.JetspeedCacheEventListener;

/**
 * Wrapper around actual cache element implementation
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class EhPortletContentCacheElementImpl implements CacheElement
{
    Element element;
    public static final String KEY_SEPARATOR = "/";
    
    
 

	public EhPortletContentCacheElementImpl(Element element)
    {
        this.element = element;
    }



	
    public Object getKey()
    {
        return element.getObjectKey();
    }
    
    public String getUserKey()
    {
        String key = (String)element.getObjectKey();
        return key.substring(0, key.indexOf(KEY_SEPARATOR));
    }

    public String getEntityKey()
    {
        String key = (String)element.getObjectKey();
        return key.substring(key.indexOf(KEY_SEPARATOR) + 1);
    }
    
    public Object getContent()
    {
        return element.getObjectValue();
    }

    public int getTimeToIdleSeconds()
    {
        return element.getTimeToIdle();
    }

    public int getTimeToLiveSeconds()
    {
        return element.getTimeToLive();
    }

    public boolean isEternal()
    {
        return element.isEternal();
    }

    public Element getImplElement()
    {
        return element;
    }

    public void setEternal(boolean eternal)
    {
        element.setEternal(eternal);
    }

    public void setTimeToIdleSeconds(int timeToIdle)
    {
        element.setTimeToIdle(timeToIdle);
    }

    public void setTimeToLiveSeconds(int timeToLive)
    {
        element.setTimeToLive(timeToLive);
    }
}