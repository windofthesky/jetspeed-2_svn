package org.apache.jetspeed.security.spi.impl;

import java.util.Collection;

import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.om.InternalCredential;
import org.apache.jetspeed.security.om.InternalUserPrincipal;
import org.apache.jetspeed.security.spi.InternalPasswordCredentialInterceptor;
import org.apache.jetspeed.security.spi.PasswordCredentialProvider;

public class DefaultInternalPasswordCredentialInterceptor implements InternalPasswordCredentialInterceptor
{
    public DefaultInternalPasswordCredentialInterceptor()
    {
    }

    /**
     * @see org.apache.jetspeed.security.spi.InternalPasswordCredentialInterceptor#afterLoad(org.apache.jetspeed.security.spi.PasswordCredentialProvider, java.lang.String, org.apache.jetspeed.security.om.InternalCredential)
     */
    public boolean afterLoad(PasswordCredentialProvider pcProvider, String userName, InternalCredential credential)
            throws SecurityException
    {
        boolean updated = false;
        if (!credential.isEncoded() && pcProvider.getEncoder() != null)
        {
            if ( pcProvider.getValidator() != null)
            {
                pcProvider.getValidator().validate(credential.getValue());
            }            
            credential.setValue(pcProvider.getEncoder().encode(userName,credential.getValue()));
            credential.setEncoded(true);
            updated = true;
        }
        return updated;
    }

    /**
     * @see org.apache.jetspeed.security.spi.InternalPasswordCredentialInterceptor#afterAuthenticated(org.apache.jetspeed.security.om.InternalUserPrincipal, java.lang.String, org.apache.jetspeed.security.om.InternalCredential, boolean)
     */
    public boolean afterAuthenticated(InternalUserPrincipal internalUser, String userName,
            InternalCredential credential, boolean authenticated) throws SecurityException
    {
        return false;
    }

    /**
     * @see org.apache.jetspeed.security.spi.InternalPasswordCredentialInterceptor#beforeCreate(org.apache.jetspeed.security.om.InternalUserPrincipal, java.util.Collection, java.lang.String, InternalCredential, java.lang.String)
     */
    public void beforeCreate(InternalUserPrincipal internalUser, Collection credentials, String userName,
            InternalCredential credential, String password) throws SecurityException
    {
    }

    /**
     * @see org.apache.jetspeed.security.spi.InternalPasswordCredentialInterceptor#beforeSetPassword(org.apache.jetspeed.security.om.InternalUserPrincipal, java.util.Collection, java.lang.String, org.apache.jetspeed.security.om.InternalCredential, java.lang.String)
     */
    public void beforeSetPassword(InternalUserPrincipal internalUser, Collection credentials, String userName,
            InternalCredential credential, String password) throws SecurityException
    {
    }
}
