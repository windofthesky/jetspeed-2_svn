package org.apache.jetspeed.security.spi;

import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.om.InternalCredential;

public interface PasswordCredentialProvider
{
    Class getPasswordCredentialClass();
    CredentialPasswordValidator getValidator();
    CredentialPasswordEncoder getEncoder();
    PasswordCredential create(String userName, String password) throws SecurityException;
    PasswordCredential create(String userName, InternalCredential credential) throws SecurityException;
}
