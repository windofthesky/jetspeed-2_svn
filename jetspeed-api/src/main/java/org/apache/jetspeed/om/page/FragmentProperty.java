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
package org.apache.jetspeed.om.page;

/**
 * This interface represents a scoped fragment property.
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public interface FragmentProperty
{
    /**
     * user standard property scope
     */
    String USER_PROPERTY_SCOPE = "user";

    /**
     * group standard property scope
     */
    String GROUP_PROPERTY_SCOPE = "group";

    /**
     * role standard property scope
     */
    String ROLE_PROPERTY_SCOPE = "role";

    /**
     * global standard property scope
     */
    String GLOBAL_PROPERTY_SCOPE = null;

    /**
     * group and role standard property scopes enabled flag
     */
    boolean GROUP_AND_ROLE_PROPERTY_SCOPES_ENABLED = false;

    /**
     * Get property name.
     *
     * @return property name
     */
    String getName();
    
    /**
     * Set property name.
     *
     * @param name property name
     */
    void setName(String name);
    
    /**
     * Get property scope. Supported scopes are: GLOBAL or null,
     * USER, GROUP, and ROLE.
     *
     * @return property scope
     */
    String getScope();
    
    /**
     * Set property scope. Supported scopes are: GLOBAL or null,
     * USER, GROUP, and ROLE.
     *
     * @param scope property scope
     */
    void setScope(String scope);
    
    /**
     * Get property scope discriminator value.
     *
     * @return property scope
     */
    String getScopeValue();
    
    /**
     * Set property scope discriminator value.
     *
     * @param value property scope discriminator 
     */
    void setScopeValue(String value);
    
    /**
     * Get property value.
     *
     * @return list of String preference values
     */
    String getValue();
    
    /**
     * Set property value.
     *
     * @param value property value
     */
    void setValue(String value);
}
