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
package org.apache.jetspeed.security.spi.impl;

import org.apache.jetspeed.security.InvalidPasswordException;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.spi.CredentialPasswordValidator;

/**
 * <p>
 * DefaultCredentialPasswordValidator
 * </p>
 * 
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id$
 */
public class DefaultCredentialPasswordValidator implements CredentialPasswordValidator
{
    public DefaultCredentialPasswordValidator()
    {
    }

    /**
     * @see org.apache.jetspeed.security.spi.CredentialPasswordValidator#validate(java.lang.String)
     */
    public void validate(String clearTextPassword) throws SecurityException
    {
        if ( clearTextPassword == null || clearTextPassword.length() == 0)
            throw new InvalidPasswordException();
    }
}
