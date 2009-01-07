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
package org.apache.jetspeed.security;


/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public interface SecurityDomain
{

    public static final String SYSTEM_NAME = "[system]";
    public static final String DEFAULT_NAME = "[default]";
    
    /**
     * Unique domain id. The ids 0 (system) and 1 (default domain) are reserved.
     * 
     * @return the domain id
     */
    Long getDomainId();
    
    /**
     * Unique string identifier for this domain. E.g. can be used from declarative references
     * to this domain. 
     * @return name
     */
    String getName();
    
    /**
     * Returns the id of the domain which is the owner of this domain. This feature is used
     * by remote domains, which can only be accessed in the context of a local domain: a local
     * domain is the owner of a remote domain.
     * @return
     */
    Long getOwnerDomainId();
    
    /**
     * Returns whether this domain constitutes a local (false) or remote (true) domain. Remote 
     * domains are accessed via the SSO component.
     * @return remote
     */
    boolean isRemote();
    
    /**
     * Returns whether this domain is enabled or not.
     * @return enabled
     */
    boolean isEnabled();
}
