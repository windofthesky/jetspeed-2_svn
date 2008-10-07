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

package org.apache.jetspeed.security.spi.impl;

import java.io.Serializable;

import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.spi.PersistentJetspeedPermission;

/**
 * @version $Id$
 *
 */
public class JetspeedPrincipalPermission implements Serializable
{
    private static final long serialVersionUID = 1842368505096279355L;
    
    @SuppressWarnings("unused")
    private Long principalId;
    @SuppressWarnings("unused")
    private Long permissionId;
    
    public JetspeedPrincipalPermission()
    {
        // needed for OJB/JPA although in practice it should *never* be needed to be loaded
        // as the only operations to be used are insert/delete, never update
    }

    public JetspeedPrincipalPermission(JetspeedPrincipal principal, PersistentJetspeedPermission permission)
    {
        this.principalId = principal.getId();
        this.permissionId = permission.getId();
    }
}
