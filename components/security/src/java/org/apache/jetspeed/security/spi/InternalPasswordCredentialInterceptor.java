package org.apache.jetspeed.security.spi;

import java.util.Collection;

import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.om.InternalCredential;
import org.apache.jetspeed.security.om.InternalUserPrincipal;

public interface InternalPasswordCredentialInterceptor
{
    boolean afterLoad(PasswordCredentialProvider pcProvider, String userName, InternalCredential credential) throws SecurityException;
    boolean afterAuthenticated(InternalUserPrincipal internalUser, String userName, InternalCredential credential, boolean authenticated) throws SecurityException;
    void beforeCreate(InternalUserPrincipal internalUser, Collection credentials, String userName, InternalCredential credential, String password) throws SecurityException;
    void beforeSetPassword(InternalUserPrincipal internalUser, Collection credentials, String userName, InternalCredential credential, String password) throws SecurityException;
}
