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

import java.io.Serializable;

/**
 * @version $Id$
 */
public interface JetspeedPermission extends Serializable
{
    /**
     * The type of permission. Supported values see:
     *      "folder"   - {@link org.apache.jetspeed.security.PermissionFactory#FOLDER_PERMISSION}
     *      "fragment" - {@link org.apache.jetspeed.security.PermissionFactory#FRAGMENT_PERMISSION}
     *      "portlet"  - {@link org.apache.jetspeed.security.PermissionFactory#PORTLET_PERMISSION}
     *      "page"     - {@link org.apache.jetspeed.security.PermissionFactory#PAGE_PERMISSION}
     *
     * @return a valid permission type string
     */
    String getType();

    /**
     * @return the name of the permission such as a portletName for a PORTLET_PERMISSION, or a page name for a PAGE_PERMISSION
     */
    String getName();

    /**
     * Typical actions supported: view, minimized, maximized, secure, edit
     *
     * @return a comma-separated list of valid actions provided by this permission
     */
    String getActions();
}
