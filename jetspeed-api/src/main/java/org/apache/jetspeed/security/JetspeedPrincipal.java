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
package org.apache.jetspeed.security;

import java.security.Principal; 
import java.sql.Timestamp;
import java.util.Map;

/**
 * @version $Id$
 */
public interface JetspeedPrincipal extends Principal
{
    Long getId();

    String getName();

    JetspeedPrincipalType getType();

    Timestamp getCreationDate();

    Timestamp getModifiedDate();

    boolean isEnabled();

    void setEnabled(boolean enabled) throws SecurityException;

    boolean isTransient();

    boolean isMapped(); // true if managed (mapped) through an external authorization provider (e.g. LDAP)

    boolean isReadOnly(); // true if enabled may not be modified, nor mapped associations, permissions and attributes

    boolean isRemovable();

    boolean isExtendable(); // true if adding associations, permissions and attributes is allowed

    SecurityAttributes getSecurityAttributes();
    
    /**
     * @return an unmodifiable Map<String,String> of the SecurityAttributes.INFO_CATEGORY SecurityAttributes
     */
    Map<String, String> getInfoMap();
    
    Long getDomainId();
}
