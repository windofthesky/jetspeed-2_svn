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

import org.apache.jetspeed.security.SecurityException;

/**
 * <p>
 * CredentialPasswordEncoder
 * </p>
 * 
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id: CredentialPasswordEncoder.java 187914 2004-11-08 22:36:04Z ate $
 */
public interface CredentialPasswordEncoder
{
    String encode(String userName, String clearTextPassword) throws SecurityException;
}
