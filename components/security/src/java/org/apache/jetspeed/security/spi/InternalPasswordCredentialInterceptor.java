/* Copyright 2004 Apache Software Foundation
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.jetspeed.security.spi;

import java.util.Collection;

import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.om.InternalCredential;
import org.apache.jetspeed.security.om.InternalUserPrincipal;

/**
 * <p>
 * InternalPasswordCredentialInterceptor
 * </p>
 * 
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id$
 */
public interface InternalPasswordCredentialInterceptor
{
    boolean afterLoad(PasswordCredentialProvider pcProvider, String userName, InternalCredential credential) throws SecurityException;
    boolean afterAuthenticated(InternalUserPrincipal internalUser, String userName, InternalCredential credential, boolean authenticated) throws SecurityException;
    void beforeCreate(InternalUserPrincipal internalUser, Collection credentials, String userName, InternalCredential credential, String password) throws SecurityException;
    void beforeSetPassword(InternalUserPrincipal internalUser, Collection credentials, String userName, InternalCredential credential, String password) throws SecurityException;
}
