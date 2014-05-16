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

import java.util.List;


/**
 * <p>
 * ActiveAuthenticationIdentityProvider
 * </p>
 * <p>
 * Provides identity tokens used during active authentication to bridge the deficiencies  
 * in Java Login Modules and general Active Authentication patterns
 * based on Java login modules. Creates a unique, short lived identity token, caching basic Authentication information across redirects, 
 * requests, and threads during the active authentication process. The life-time
 * of this cached authentication information is meant to be very short lived. 
 * </p>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 *
 */
public interface ActiveAuthenticationIdentityProvider
{
    /**
     * Start an authentication event with the server, creating a new and unique identity token 
     * 
     * @return the newly created identity token 
     */
    IdentityToken createIdentityToken();

    /**
     * Start an authentication event with the server, creating a new and unique identity token 
     *
     * @param seed seed information to add to token
     * @return the newly created identity token 
     */
    IdentityToken createIdentityToken(String seed);

    /**
     * Completes an authentication event for a given authentication token
     * 
     * @param token The token identifying the authentication event to be completed
     */
    void completeAuthenticationEvent(String token);
    
    /**
     * Get a list of session attribute names that should be saved and restored upon authentication events
     * @return list of session attribute names
     */
    List<String> getSessionAttributeNames();

}
