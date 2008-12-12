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
package org.apache.jetspeed.security.spi;

import org.apache.jetspeed.security.CredentialPasswordEncoder;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.SecurityException;

/**
 * <p>
 * AlgorithmUpgradeCredentialPasswordEncoder which is provided with the PasswordCredential as well
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
    String encode(PasswordCredential credential, String clearTextPassword) throws SecurityException;
    void recodeIfNeeded(PasswordCredential credential, String clearTextPassword) throws SecurityException;
    boolean usesOldEncodingAlgorithm(PasswordCredential credential);
}
