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

import java.util.Set;


public interface SecurityAttributes {
    String USER_INFO_CATEGORY = "user_info";
    
    JetspeedPrincipal getPrincipal();
    int size();
    boolean isReadonly();
    Set<SecurityAttributeType> getAttributeTypes();
    Set<SecurityAttributeType> getAttributeTypes(String category);
    Set<String> getAttributeNames();
    Set<String> getAttributeNames(String category);
    Set<SecurityAttribute> getAttributes();
    Set<SecurityAttribute> getAttributes(String category);
    SecurityAttribute getAttribute(String name);
    SecurityAttribute newAttribute(String name) throws ReadonlyAttributesException, SecurityAttributeTypeNotFoundException, AttributeAlreadyExistsException;
    SecurityAttribute newAttribute(String name, String category, SecurityAttributeType.TYPE type) throws ReadonlyAttributesException, AttributeAlreadyExistsException;
    void removeAttribute(String name) throws ReadonlyAttributesException, RequiredAttributeException;
}
