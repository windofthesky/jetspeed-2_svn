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
package org.apache.jetspeed.security.spi.impl;

import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.spi.CredentialPasswordValidator;

/**
 * <p>
 * SimpleCredentialPasswordValidator
 * </p>
 * 
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id$
 */
public class SimpleCredentialPasswordValidator implements CredentialPasswordValidator
{
    private int minPasswordLength;
    private int minNumberOfDigits;
    
    /**
     * @param minPasswordLength
     * @param minNumberOfDigits
     */
    public SimpleCredentialPasswordValidator(int minPasswordLength, int minNumberOfDigits)
    {
        this.minPasswordLength = minPasswordLength;
        this.minNumberOfDigits = minNumberOfDigits;
    }

    /**
     * @see org.apache.jetspeed.security.spi.CredentialPasswordValidator#validate(char[])
     */
    public void validate(String clearTextPassword) throws SecurityException
    {
        int digits = 0;
        char[] pwd = clearTextPassword.toCharArray();

        if ( minPasswordLength > 0 && pwd.length < minPasswordLength )
        {
            throw new SecurityException(SecurityException.INVALID_PASSWORD);
        }

        if ( minNumberOfDigits > 0)
        {
            for ( int i = 0; i < pwd.length; i++ )
            {
                if (Character.isDigit(pwd[i]))
                {
                    digits++;
                }
            }
            if (digits < minNumberOfDigits)
            {
                throw new SecurityException(SecurityException.INVALID_PASSWORD);
            }
        }
    }
}
