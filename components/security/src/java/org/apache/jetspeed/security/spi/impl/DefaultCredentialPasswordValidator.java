package org.apache.jetspeed.security.spi.impl;

import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.spi.CredentialPasswordValidator;

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
            throw new SecurityException(SecurityException.INVALID_PASSWORD);
    }
}
