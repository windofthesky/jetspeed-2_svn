package org.apache.jetspeed.security.spi;

import org.apache.jetspeed.security.SecurityException;

public interface CredentialPasswordEncoder
{
    String encode(String userName, String clearTextPassword) throws SecurityException;
}
