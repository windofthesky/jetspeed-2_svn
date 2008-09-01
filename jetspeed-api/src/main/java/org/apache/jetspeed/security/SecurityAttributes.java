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
    String INFO_CATEGORY = "info";

    JetspeedPrincipal getPrincipal();

    int size();

    boolean isReadOnly();
    
    boolean isExtendable();
    
    SecurityAttributeTypes getSecurityAttributeTypes();

    Set<String> getAttributeNames();

    Set<String> getAttributeNames(String category);

    Map<String, SecurityAttribute> getAttributeMap();

    Map<String, SecurityAttribute> getAttributeMap(String category);
    
    SecurityAttribute getAttribute(String name);

    SecurityAttribute addAttribute(String name)
        throws AttributesReadOnlyException, AttributeTypeNotFoundException, AttributeAlreadyExistsException;

    SecurityAttribute addNewInfoAttribute(String name, SecurityAttributeType.DataType type)
        throws AttributesReadOnlyException, AttributeTypeAlreadyDefinedException, AttributeAlreadyExistsException, AttributesNotExtendableException;

    void removeAttribute(String name) throws AttributesReadOnlyException, AttributeReadOnlyException, AttributeRequiredException;
}
