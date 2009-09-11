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
package org.apache.jetspeed.proxy.impl;

import java.util.Map;

import org.apache.jetspeed.proxy.HttpReverseProxyPathMapper;

/**
 * Default implementation of <CODE>HttpProxyPathMapper</CODE> interface.
 * 
 * @version $Id$
 */
public class DefaultHttpProxyPathMapperImpl implements HttpReverseProxyPathMapper
{
    private String localBasePath;
    private String remoteBaseURL;
    private Map<String, Object> attributes;
    
    public DefaultHttpProxyPathMapperImpl(String localBasePath, String remoteBaseURL, Map<String, Object> attributes)
    {
        this.localBasePath = localBasePath;
        this.remoteBaseURL = remoteBaseURL;
        this.attributes = attributes;
    }
    
    public String getLocalBasePath()
    {
        return localBasePath;
    }

    public String getRemoteBaseURL()
    {
        return remoteBaseURL;
    }

    public String getRemoteURL(String localPath)
    {
        if (localPath.startsWith(localBasePath))
        {
            return remoteBaseURL + localPath.substring(localBasePath.length());
        }
        
        throw new IllegalArgumentException("The localPath should start with '" + localBasePath + "'.");
    }
    
    public String getLocalPath(String remoteURL)
    {
        if (remoteURL.startsWith(remoteBaseURL))
        {
            return localBasePath + remoteURL.substring(remoteBaseURL.length());
        }
        
        throw new IllegalArgumentException("The remoteURL should start with '" + remoteBaseURL + "'.");
    }
    
    public Object getAttribute(String attrName)
    {
        if (attributes != null)
        {
            return attributes.get(attrName);
        }
        else
        {
            return null;
        }
    }
    
}
