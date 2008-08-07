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
import java.util.Collection;

import org.apache.jetspeed.security.SecurityException;

public interface SecurityAttributesProvider
{
    /**
     * Retrieve all security attributes for a given security principal and return them in a map of SecurityAttribute
     * 
     * @param principal A valid Jetspeed principal such as a RolePrincipal, UserPrincipal, or GroupPrincipal
     * @return SecurityAttributes containing the map of attributes
     * @throws SecurityException
     */
    SecurityAttributes retrieveAttributes(Principal principal) throws SecurityException;
      
    /**
     * Persist the given security attributes to the database. This method will determine which attributes have been modified, deleted or added and update appropriately.
     *   
     * @param attributes Contains a map of security attributes to be stored
     * @throws SecurityException
     */
    void saveAttributes(SecurityAttributes attributes) throws SecurityException;
    
    /**
     * Create an empty collection (map) of security attributes for a given Jetspeed principal
     * 
     * @param principal A valid Jetspeed principal such as a RolePrincipal, UserPrincipal, or GroupPrincipal
     * @return a set of security attributes
     * @throws SecurityException
     */
    SecurityAttributes createSecurityAttributes(Principal principal) throws SecurityException;
    
    /**
     * Deletes attributes for a given principal. To delete individual security attributes, delete the specific attribute from the map and then call <code>saveAttributes</code> to commit the deletion.
     *  
     * @param principal A valid Jetspeed principal such as a RolePrincipal, UserPrincipal, or GroupPrincipal
     * @throws SecurityException
     */
    void deleteAttributes(Principal principal) throws SecurityException;
    
    /**
     * Given a name value pair of attributes, return a collection of found attributes that match the lookup
     * @param name
     * @param value
     * @return collection of SecurityAttributes
     * @throws SecurityException
     */
    Collection<SecurityAttributes> lookupAttributes(String name, String value) throws SecurityException;
}
