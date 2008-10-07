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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jetspeed.security.CredentialPasswordValidator;
import org.apache.jetspeed.security.SecurityException;

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
    private String passwordPattern;
    private boolean strictPassword = false;
    /* Example:
        * Must be at least 6 characters
        * Must contain at least one one lower case letter, one upper case letter, one digit and one special character
        * Valid special characters are @#$%^&+=
     */
    private final static String defaultPasswordPattern = "[^.*(?=.{6,})(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$]";
    
    public DefaultCredentialPasswordValidator(String passwordPattern)
    {
        this.passwordPattern = passwordPattern;
        this.strictPassword = true;
    }
    public DefaultCredentialPasswordValidator()
    {
        strictPassword = false;
    }
    
    /**
     * @see org.apache.jetspeed.security.CredentialPasswordValidator#validate(java.lang.String)
     */
    public void validate(String clearTextPassword) throws SecurityException
    {
       if (strictPassword)
       {
           Pattern p = Pattern.compile(passwordPattern);
           //Match the given string with the pattern
           Matcher m = p.matcher(clearTextPassword);
           if(!m.matches())
               throw new SecurityException(SecurityException.INVALID_PASSWORD);
       }
       else
       {
        if ( clearTextPassword == null || clearTextPassword.length() == 0)
            throw new SecurityException(SecurityException.INVALID_PASSWORD);
       }
 
    }
}
