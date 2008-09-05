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

import java.util.List;

import org.apache.jetspeed.security.spi.JetspeedPrincipalPermissionStorageManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalStorageManager;

/**
 * @version $Id$
 */
public interface JetspeedPrincipalManager extends JetspeedPrincipalStorageManager, JetspeedPrincipalPermissionStorageManager
{
    JetspeedPrincipalType getPrincipalType();

     boolean principalExists(String name);

    JetspeedPrincipal getPrincipal(String name);

    List<String> getPrincipalNames(String nameFilter);

    List<JetspeedPrincipal> getPrincipals(String nameFilter);
    
    JetspeedPrincipal newPrincipal(String name, boolean mapped);

    JetspeedPrincipal newTransientPrincipal(String name);
    
    void removePrincipal(String name) throws PrincipalNotFoundException, PrincipalNotRemovableException, DependentPrincipalException;

    List<JetspeedPrincipal> getAssociatedFrom(String principalName, JetspeedPrincipalType to, String associationName);

    List<JetspeedPrincipal> getAssociatedTo(String principalName, JetspeedPrincipalType from, String associationName);

    List<String> getAssociatedNamesFrom(String principalName, JetspeedPrincipalType to, String associationName);

    List<String> getAssociatedNamesTo(String principalName, JetspeedPrincipalType from, String associationName);
}
