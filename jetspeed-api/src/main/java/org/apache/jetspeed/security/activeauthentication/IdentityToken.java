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
package org.apache.jetspeed.security.activeauthentication;

import java.util.Iterator;


/**
 * <p>
 * Identity Token
 * </p>
 * <p>
 * Holds a unique token identifying the current authentication process.  
 * This token can hold one or more unique name / value (object) attributes
 * </p>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 *
 */
public interface IdentityToken
{
    /**
     * Get the value of the identity token 
     * @return the identity token string
     */
    String getToken();
    
    /**
     * set a name/value attribute on this token
     * @param name
     * @param value
     */
    void setAttribute(String name, Object value);
    
    /** 
     * Get an attribute value given the attribute name
     * @param name
     * @return
     */
    Object getAttribute(String name);
    
    /**
     * Get an iterator over all attribute names
     * @return
     */
    Iterator getAttributeNames();
}
