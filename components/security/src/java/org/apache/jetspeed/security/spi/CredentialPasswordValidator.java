package org.apache.jetspeed.security.spi;

import org.apache.jetspeed.security.SecurityException;

public interface CredentialPasswordValidator
{
    void validate(String clearTextPassword) throws SecurityException;
}
