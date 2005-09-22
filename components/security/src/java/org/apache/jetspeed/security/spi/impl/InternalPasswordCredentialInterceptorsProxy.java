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

import java.util.Collection;
import java.util.List;

import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.om.InternalCredential;
import org.apache.jetspeed.security.om.InternalUserPrincipal;
import org.apache.jetspeed.security.spi.InternalPasswordCredentialInterceptor;
import org.apache.jetspeed.security.spi.PasswordCredentialProvider;

/**
 * <p>
 * Provides a wrapper around a list of interceptors so multiple interceptors can
 * be used with the {@link DefaultCredentialHandler}.
 * Each interceptor will be invoked sequentially.
 * </p>
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class InternalPasswordCredentialInterceptorsProxy implements InternalPasswordCredentialInterceptor
{
    private InternalPasswordCredentialInterceptor[] interceptors;

    public InternalPasswordCredentialInterceptorsProxy(List interceptors)
    {
        this.interceptors = (InternalPasswordCredentialInterceptor[]) interceptors
                .toArray(new InternalPasswordCredentialInterceptor[interceptors.size()]);
    }

    /**
     * @see org.apache.jetspeed.security.spi.InternalPasswordCredentialInterceptor#afterLoad(org.apache.jetspeed.security.spi.PasswordCredentialProvider, java.lang.String, org.apache.jetspeed.security.om.InternalCredential)
     */
    public boolean afterLoad(PasswordCredentialProvider pcProvider, String userName, InternalCredential credential)
            throws SecurityException
    {
        boolean updated = false;
        for (int i = 0; i < interceptors.length; i++)
        {
            if (interceptors[i] != null && interceptors[i].afterLoad(pcProvider, userName, credential))
            {
                updated = true;
            }
        }
        return updated;
    }

    /**
     * @see org.apache.jetspeed.security.spi.InternalPasswordCredentialInterceptor#afterAuthenticated(org.apache.jetspeed.security.om.InternalUserPrincipal, java.lang.String, org.apache.jetspeed.security.om.InternalCredential, boolean)
     */
    public boolean afterAuthenticated(InternalUserPrincipal internalUser, String userName,
            InternalCredential credential, boolean authenticated) throws SecurityException
    {
        boolean updated = false;
        for (int i = 0; i < interceptors.length; i++)
        {
            if (interceptors[i] != null
                    && interceptors[i].afterAuthenticated(internalUser, userName, credential, authenticated))
            {
                updated = true;
            }
        }
        return updated;
    }

    /**
     * @see org.apache.jetspeed.security.spi.InternalPasswordCredentialInterceptor#beforeCreate(org.apache.jetspeed.security.om.InternalUserPrincipal, java.util.Collection, java.lang.String, InternalCredential, java.lang.String)
     */
    public void beforeCreate(InternalUserPrincipal internalUser, Collection credentials, String userName,
            InternalCredential credential, String password) throws SecurityException
    {
        for (int i = 0; i < interceptors.length; i++)
        {
            if (interceptors[i] != null)
            {
                interceptors[i].beforeCreate(internalUser, credentials, userName, credential, password);
            }
        }
    }

    /**
     * @see org.apache.jetspeed.security.spi.InternalPasswordCredentialInterceptor#beforeSetPassword(org.apache.jetspeed.security.om.InternalUserPrincipal, java.util.Collection, java.lang.String, org.apache.jetspeed.security.om.InternalCredential, java.lang.String, boolean)
     */
    public void beforeSetPassword(InternalUserPrincipal internalUser, Collection credentials, String userName,
            InternalCredential credential, String password, boolean authenticated) throws SecurityException
    {
        for (int i = 0; i < interceptors.length; i++)
        {
            if (interceptors[i] != null)
            {
                interceptors[i].beforeSetPassword(internalUser, credentials, userName, credential, password,
                        authenticated);
            }
        }
    }
}
