package org.apache.jetspeed.security.spi.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.spi.CredentialPasswordEncoder;

public class MessageDigestCredentialPasswordEncoder implements CredentialPasswordEncoder
{
    MessageDigest digester;
    
    public MessageDigestCredentialPasswordEncoder() throws NoSuchAlgorithmException
    {
        this("SHA-1");
    }
    
    public MessageDigestCredentialPasswordEncoder(String algorithm) throws NoSuchAlgorithmException
    {
        this.digester = MessageDigest.getInstance(algorithm);
    }
    
    public String getAlgorithm()
    {
        return digester.getAlgorithm();
    }

    /**
     * @see org.apache.jetspeed.security.spi.CredentialPasswordEncoder#encode(java.lang.String, java.lang.String)
     */
    public String encode(String userName, String clearTextPassword)
            throws SecurityException
    {
        byte[] value;
        synchronized(digester)
        {
            digester.reset();
            value = digester.digest(clearTextPassword.getBytes());            
            // don't allow copying of encoded passwords
            digester.update(userName.getBytes());
            value = digester.digest(value);
        }
        return new String(Base64.encodeBase64(value));
    }
}
