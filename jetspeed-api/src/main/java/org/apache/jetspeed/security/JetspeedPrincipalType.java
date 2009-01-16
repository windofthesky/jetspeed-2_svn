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

import java.io.Serializable;

/**
 * <p>The base JetspeedPrincipal type.</p>
 * 
 * @author <a href="mailto:vkumar@apache.org">Vivek Kumar</a>
 * @version $Id$
 */
public interface JetspeedPrincipalType extends Serializable
{
    /** build-in supported JetspeedPrincipalType "group" */
    String GROUP = "group";
    /** build-in supported JetspeedPrincipalType "role" */
    String ROLE = "role";
    /** build-in supported JetspeedPrincipalType "user" */
    String USER = "user";
    /** build-in supported JetspeedPrincipalType "ssoUser" */
    String SSO_USER = "ssoUser";
    
    /**
     * The name to identify the type of a JetspeedPrincipal.
     * <p>
     * While the implementation class of a specific JetspeedPrincipalType might be replaced,
     * the type name should remain constant (e.g. for a {@link #USER} a different implementation
     * could be provided but it will remain a {@link #USER} afterall).
     * </p>
     * <p>
     * Note: the name <em>value</em> must conform to the Java Identifier requirements (e.g. no spaces, dots, etc.)
     * to support localization through resource bundles.
     * </p>
     */
    String getName();

    String getClassName();
    
    Class<JetspeedPrincipal> getPrincipalClass();

    SecurityAttributeTypes getAttributeTypes();
}
