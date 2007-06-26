package org.apache.jetspeed.security.impl;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.security.MessageDigest;

import javax.mail.internet.MimeUtility;

import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.spi.CredentialPasswordEncoder;

public class Jetspeed1CredentialPasswordEncoder implements
		CredentialPasswordEncoder {

    protected String passwordsAlgorithm = "SHA";
    protected String encodingMethod = "base64";

    // We don't need the constructors to do anything, but it crashes if we
    // don't provide them.
    /*
    public Jetspeed1CredentialPasswordEncoder() {}
    public Jetspeed1CredentialPasswordEncoder(boolean dummy) {}
    public Jetspeed1CredentialPasswordEncoder(String algorithm) 
    {
    	this.passwordsAlgorithm = algorithm;
    }
    
    public Jetspeed1CredentialPasswordEncoder(boolean dummy1, String dummy2) {}
    */
    
    public Jetspeed1CredentialPasswordEncoder()
    {
    	this("SHA", "base64");
    }
    
    public Jetspeed1CredentialPasswordEncoder( String algorithm )
    {
    	this(algorithm, "base64");
    }
    
    public Jetspeed1CredentialPasswordEncoder( String algorithm, String encoding )
    {
    	this.passwordsAlgorithm = algorithm;
    	this.encodingMethod = encoding;
    }
    
    public String encode(String userName, String clearTextPassword)
			throws SecurityException {
    	try
    	{
        MessageDigest md = MessageDigest.getInstance(passwordsAlgorithm);
        // We need to use unicode here, to be independent of platform's
        // default encoding. Thanks to SGawin for spotting this.
        byte[] digest = md.digest(clearTextPassword.getBytes("UTF-8"));
        ByteArrayOutputStream bas = new ByteArrayOutputStream(digest.length + digest.length / 3 + 1);
        OutputStream encodedStream = MimeUtility.encode(bas, "base64");
        encodedStream.write(digest);
        encodedStream.flush();
        encodedStream.close();
        return bas.toString();
    	}
    	catch( Exception e )
    	{
            //logger.error("Unable to encrypt password."+e.getMessage(), e);
            return null;
    	}
	}

}
