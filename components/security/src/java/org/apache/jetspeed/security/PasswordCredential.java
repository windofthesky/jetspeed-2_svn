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
package org.apache.jetspeed.security;

import java.io.Serializable;
import java.util.Arrays;

/**
 * <p>
 * Password credential. Provides the same mechanism as J2EE
 * <code>javax.resource.spi.security.PasswordCredential</code>.
 * </p>
 * 
 * <p>
 * Code borrowed from the Geronimo project.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public final class PasswordCredential implements Serializable
{

    /** The user name. */
    private String userName;

    /** The password. */
    private char[] password;

    /**
     * @param userName
     * @param password
     */
    public PasswordCredential(String userName, char[] password)
    {
        this.userName = userName;
        this.password = (char[]) password.clone();
    }

    /**
     * @return The username.
     */
    public String getUserName()
    {
        return userName;
    }

    /**
     * @return The password.
     */
    public char[] getPassword()
    {
        return (char[]) password.clone();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (!(o instanceof PasswordCredential))
            return false;

        final PasswordCredential credential = (PasswordCredential) o;

        if (!Arrays.equals(password, credential.password))
            return false;
        if (!userName.equals(credential.userName))
            return false;

        return true;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        int result = userName.hashCode();
        for (int i = 0; i < password.length; i++)
        {
            result *= password[i];
        }
        return result;
    }
}