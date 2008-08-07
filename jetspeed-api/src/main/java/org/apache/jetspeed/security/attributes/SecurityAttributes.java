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
package org.apache.jetspeed.security.attributes;

import java.security.Principal;
import java.util.Map;

public interface SecurityAttributes
{
    /** constant used by createUserInformation to store user information attributes */
    final static String USER_INFORMATION = "user_info";
    /** constant representing all other security attributes besides user_info, but not limited to */
    final static String SECURITY_ATTRIBUTE = "attribute";

    /**
     * Retrieve the security attributes map 
     * @return the map for a given principal
     */
    Map<String, SecurityAttribute> getAttributes();
 
    /**
     * Retrieve the security attributes map for a given kind of attribute (see constants above)
     * 
     * @param type the type of attirbute such as USER_INFORMATION or SECURITY_ATTRIBUTE
     * @return the map for a given principal
     */
    public Map<String, SecurityAttribute> getAttributes(String type);
    
    /**
     * Get the security principal for this set of attributes 
     * @return
     */
    Principal getPrincipal();
    
    /**
     * Create a general security attribute
     * @param name the name of the security attribute
     * @param value the string value of the security attribute
     * @return a newly created security attribute object
     */
    SecurityAttribute createAttribute(String name, String value);

    /**
     * Create a Portlet API User Information type attribute
     * 
     * @param name the name of the security attribute, usually a valid Portlet API User information name, see spec
     * @param value the string value of the security attribute
     * @return a newly created security attribute object
     */    
    SecurityAttribute createUserInformation(String name, String value);
}
