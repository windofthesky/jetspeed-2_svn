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

import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.om.InternalCredential;

/**
 * <p>
 * AlgorithmUpgradeCredentialPasswordEncoder which is provided with the InternalCredential as well
 * to allow for migrating between two different encoding schemes.
 * </p>
 * <p>
 * The extended encode method is *only* called in the context of validating an existing (old) password,
 * and not used for creating or updating to a new password directl!
 * </p>
 * <p>
 * After successfull authentication, the recodeIfNeeded method will be called allowing to migrate to the new encryption scheme.
 * </p>
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public interface AlgorithmUpgradeCredentialPasswordEncoder extends CredentialPasswordEncoder
{
    String encode(String userName, String clearTextPassword, InternalCredential credential) throws SecurityException;
    void recodeIfNeeded(String userName, String clearTextPassword, InternalCredential credential) throws SecurityException;
    boolean usesOldEncodingAlgorithm(PasswordCredential credential);
}
