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

import java.util.HashSet;
import java.util.Set;

import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.spi.CredentialHandler;

/**
 * @see org.apache.jetspeed.security.spi.CredentialHandler
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public class LdapCredentialHandler implements CredentialHandler
{

    /**
     * <p>
     * Default Constructor.
     * </p>
     */
    public LdapCredentialHandler()
    {
    }
    
    /**
     * @see org.apache.jetspeed.security.spi.CredentialHandler#getPublicCredentials(java.lang.String)
     */
    public Set getPublicCredentials(String username)
    {
        Set publicCredentials = new HashSet();
        return publicCredentials;
    }

    /**
     * @see org.apache.jetspeed.security.spi.CredentialHandler#setPublicPasswordCredential(org.apache.jetspeed.security.PasswordCredential, org.apache.jetspeed.security.PasswordCredential)
     */
    public void setPublicPasswordCredential(PasswordCredential oldPwdCredential, PasswordCredential newPwdCredential)
            throws SecurityException
    {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.apache.jetspeed.security.spi.CredentialHandler#getPrivateCredentials(java.lang.String)
     */
    public Set getPrivateCredentials(String username)
    {
        Set privateCredentials = new HashSet();
        if (username.equals("ldap1"))
        {
            privateCredentials.add(new PasswordCredential(username, "password".toCharArray()));
        }
        else if (username.equals("ldap2"))
        {
            privateCredentials.add(new PasswordCredential(username, "password".toCharArray()));
        }
        else if (username.equals("ldap2"))
        {
            privateCredentials.add(new PasswordCredential(username, "password".toCharArray()));
        }
        return privateCredentials;
    }

    /**
     * @see org.apache.jetspeed.security.spi.CredentialHandler#setPrivatePasswordCredential(org.apache.jetspeed.security.PasswordCredential, org.apache.jetspeed.security.PasswordCredential)
     */
    public void setPrivatePasswordCredential(PasswordCredential oldPwdCredential, PasswordCredential newPwdCredential)
            throws SecurityException
    {
        // TODO Auto-generated method stub

    }

}
