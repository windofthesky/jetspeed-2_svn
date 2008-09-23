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

import java.util.Map;
import java.util.Set;

/**
 * @version $Id$
 */
public interface SecurityAttributes
{
    JetspeedPrincipal getPrincipal();

    int size();

    boolean isReadOnly();
    
    boolean isExtendable();
    
    SecurityAttributeTypes getSecurityAttributeTypes();
    
    boolean isDefinedAttribute(String name);

    Set<String> getAttributeNames();

    Set<String> getAttributeNames(String category);

    Map<String, SecurityAttribute> getAttributeMap();

    Map<String, SecurityAttribute> getAttributeMap(String category);
    
    Map<String, SecurityAttribute> getInfoAttributeMap();

    /**
     * @return an unmodifiable Map<String,String> of the SecurityAttributes.INFO_CATEGORY SecurityAttributes
     */
    Map<String, String> getInfoMap();

    SecurityAttribute getAttribute(String name);

    /**
     * Returns an existing (predefined typed) attribute or create one if parameter create is true.
     * If parameter create is true and it doesn't exist yet it will be created (based
     * upon its SecurityAttributeType) first, but only if the SecurityAttributes itself
     * isn't readOnly (then a AttributesReadOnlyException will be thrown).
     * If parameter create is false and it doesn't exist yet a NULL value will be
     * returned.
     * If there is no SecurityAttributeType defined for the attribute (name), a new attribute
     * with INFO_CATEGORY will be created.
     * @param name name of a predefined SecurityAttributeType (for this JetspeedPrincipal type)
     * @param create add the attribute when it doesn't exist yet
     * @return an existing attribute or one created on the fly (if parameter create is true)
     */
    SecurityAttribute getAttribute(String name, boolean create) throws SecurityException;

    void removeAttribute(String name) throws SecurityException;
}
