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

import java.sql.Timestamp;

/**
 * @version $Id$
 */
public interface JetspeedPrincipal
{
    Long getId();

    String getName();

    JetspeedPrincipalType getType();

    void setName(String name);

    Timestamp getCreationDate();

    void setCreationDate(Timestamp creationDate);

    Timestamp getModifiedDate();

    void setModifiedDate(Timestamp modifiedDate);

    boolean isEnabled();

    void setEnable(boolean enabled);

    boolean isMapped(); // true if managed (mapped) through an external authorization provider (e.g. LDAP)

    void setMapped(boolean mapped);

    boolean isReadonly(); // true if enabled may not be modified, nor mapped associations, permissions and attributes

    void setReadonly(boolean readonly);

    boolean isRemovable();

    void setRemovable(boolean removable);

    boolean isExtendable(); // true if adding associations, permissions and attributes is allowed

    void setExtendable(boolean extendable);

    SecurityAttributes getSecurityAttributes();
}
