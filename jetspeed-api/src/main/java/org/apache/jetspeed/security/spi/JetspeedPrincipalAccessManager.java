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
package org.apache.jetspeed.security.spi;

import java.util.List;

import org.apache.jetspeed.security.JetspeedPermission;
import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.SecurityAttributes;

/**
 * @version $Id$
 */
public interface JetspeedPrincipalAccessManager
{
    boolean principalExists(String principalName, JetspeedPrincipalType type);

    JetspeedPrincipal getPrincipal(Long id);

    JetspeedPrincipal getPrincipal(String principalName, JetspeedPrincipalType type);

    List<JetspeedPrincipal> getPrincipals(String nameFilter, List<SecurityAttributes> attributes, JetspeedPrincipalType type);

    List<String> getPrincipalNames(String nameFilter, List<SecurityAttributes> attributes, JetspeedPrincipalType type);

    List<JetspeedPrincipal> getAssociatedFrom(String principalName, JetspeedPrincipalType type, String associationName);

    List<JetspeedPrincipal> getAssociatedFrom(Long principalId, JetspeedPrincipalType type, String associationName);

    List<JetspeedPrincipal> getAssociatedTo(String principalName, JetspeedPrincipalType type, String associationName);

    List<JetspeedPrincipal> getAssociatedTo(Long principalId, JetspeedPrincipalType type, String associationName);

    List<String> getAssociatedNamesFrom(String principalName, JetspeedPrincipalType type, String associationName);

    List<String> getAssociatedNamesFrom(Long principalId, JetspeedPrincipalType type, String associationName);

    List<String> getAssociatedNamesTo(String principalName, JetspeedPrincipalType type, String associationName);

    List<String> getAssociatedNamesTo(Long principalId, JetspeedPrincipalType type, String associationName);

    List<String> getPrincipals(JetspeedPermission permission);

    List<String> getPrincipals(JetspeedPermission permission, JetspeedPrincipalType type);
}
