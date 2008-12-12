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
package org.apache.jetspeed.container.url;

/**
 * <p>
 * BasePortalURL defines the interface for manipulating Base URLs in a portal.
 * Base URLs contain the isSecure flag, server name, server port, and server scheme.
 * This abstraction was necessary for wiring the entire portal's base URL via another
 * mechanism than retrieving from the servlet request.
 * </p>
 * 
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id: $
 *
 */
public interface BasePortalURL
{
    boolean isSecure();    
    void setSecure(boolean secure);    
    String getServerName();    
    void setServerName(String serverName);
    int getServerPort();    
    void setServerPort(int serverPort);    
    String getServerScheme();    
    void setServerScheme(String serverScheme);    
}