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
package org.apache.jetspeed.proxy;

/**
 * proxy path mapper interface for http reverse proxy service.
 * 
 * @version $Id$
 */
public interface HttpReverseProxyPathMapper
{
    
    /**
     * Returns the local base path to be mapped to remote base url.
     * @return
     */
    public String getLocalBasePath();
    
    /**
     * Returns the remote base url mapped by the local path.
     * @return
     */
    public String getRemoteBaseURL();
    
    /**
     * Generates a remote url mapped by the path.
     */
    public String getRemoteURL(String localPath);
    
    /**
     * Generates a local url mapped by the remote url.
     */
    public String getLocalPath(String remoteURL);
    
    /**
     * Returns the attribute object specified by the attrName.
     * @param attrName
     * @return
     */
    public Object getAttribute(String attrName);
    
}
