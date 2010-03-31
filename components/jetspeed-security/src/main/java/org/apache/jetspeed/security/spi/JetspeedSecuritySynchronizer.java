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
package org.apache.jetspeed.security.spi;

import org.apache.jetspeed.security.SecurityException;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public interface JetspeedSecuritySynchronizer
{
	
	/**
	 * Synchronizes the user principal with the specified name. 
	 * @param name
	 */
    void synchronizeUserPrincipal(String name, boolean recursive) throws SecurityException;
    
    /**
     * Synchronize all principals of a certain type.
     * @param principalTypeName
     * @param recursive if true, all nested principals associated to this principal will be synchronized. If false, only the direct (first level) associated
     *          principals will be synchronized.
     */
    void synchronizePrincipalsByType(String principalTypeName, boolean recursive) throws SecurityException;
    
    /**
     *  Synchronizes all principals.
     */
    void synchronizeAll() throws SecurityException;
    
}
