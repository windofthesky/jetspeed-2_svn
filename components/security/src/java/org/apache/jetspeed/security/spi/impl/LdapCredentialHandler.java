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
     * @see org.apache.jetspeed.security.spi.CredentialHandler#getPrivateCredentials(java.lang.String)
     */
    public Set getPrivateCredentials(String username)
    {
        Set privateCredentials = new HashSet();
        if (username.equals("ldap1"))
        {
            privateCredentials.add(new DefaultPasswordCredentialImpl(username, "password".toCharArray()));
        }
        else if (username.equals("ldap2"))
        {
            privateCredentials.add(new DefaultPasswordCredentialImpl(username, "password".toCharArray()));
        }
        else if (username.equals("ldap2"))
        {
            privateCredentials.add(new DefaultPasswordCredentialImpl(username, "password".toCharArray()));
        }
        return privateCredentials;
    }

    /**
     * @see org.apache.jetspeed.security.spi.CredentialHandler#setPassword(java.lang.String,java.lang.String,java.lang.String)
     */
    public void setPassword(String userName, String oldPassword, String newPassword ) throws SecurityException
    {
        // TODO Auto-generated method stub
    }

    /**
     * @see org.apache.jetspeed.security.spi.CredentialHandler#setPasswordEnabled(java.lang.String, boolean)
     */
    public void setPasswordEnabled(String userName, boolean enabled) throws SecurityException
    {
        // TODO Auto-generated method stub

    }
    
    /**
     * @see org.apache.jetspeed.security.spi.CredentialHandler#setPasswordUpdateRequired(java.lang.String, boolean)
     */
    public void setPasswordUpdateRequired(String userName, boolean updateRequired) throws SecurityException
    {
        // TODO Auto-generated method stub

    }
    
    public boolean authenticate(String userName, String password)
    {
        if (userName.equals("ldap1") || userName.equals("ldap2") || userName.equals("ldap3"))
        {
            return "password".equals(password);
        }
        return false;
    }
}
