package org.apache.jetspeed.security.spi.impl;

import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.spi.CredentialPasswordValidator;

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
