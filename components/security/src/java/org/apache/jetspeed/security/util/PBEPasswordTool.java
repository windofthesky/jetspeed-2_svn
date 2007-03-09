/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.security.util;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.jetspeed.security.SecurityException;

/**
 * <p>
 * PBEPasswordTool encodes and decodes user passwords using Password Based encryptionl
 * </p>
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class PBEPasswordTool
{
    // PKCS #5 (PBE) algoritm
    private static final String CIPHER_ALGORITM = "PBEwithMD5andDES";
    // PKCS #5 iteration count is advised to be at least  1000
    private static final int PKCS_5_ITERATIONCOUNT = 1111;
    // pseudo random base salt which will be overlayed with userName.getBytes()
    private static final byte[] PKCS_5_BASE_SALT = {(byte)0xA9, (byte)0x9B, (byte)0xC8, (byte)0x32, (byte)0x56, (byte)0x35, (byte)0xE3, (byte)0x03};
    
    // PBE cipher
    private SecretKey pbeKey;
    
    public PBEPasswordTool(String pbePassword) throws InvalidKeySpecException, NoSuchAlgorithmException
    {
        pbeKey = SecretKeyFactory.getInstance(CIPHER_ALGORITM).generateSecret(new PBEKeySpec(pbePassword.toCharArray()));
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.spi.CredentialPasswordEncoder#encode(java.lang.String, java.lang.String)
     * @see org.apache.jetspeed.security.PasswordEncodingService#encode(java.lang.String, java.lang.String)
     */
    public String encode(String userName, String clearTextPassword) throws SecurityException
    {
        try
        {
            // prevent dictionary attacks as well as copying of encoded passwords by using the userName as salt
            PBEParameterSpec cipherSpec = new PBEParameterSpec(createSalt(userName.getBytes("UTF-8")), PKCS_5_ITERATIONCOUNT);
            
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITM);
            cipher.init(Cipher.ENCRYPT_MODE,pbeKey,cipherSpec);
            
            return new String(Base64.encodeBase64(cipher.doFinal(clearTextPassword.getBytes("UTF-8"))), "UTF-8");
        }
        catch (Exception e)
        {
            throw new SecurityException(SecurityException.UNEXPECTED.create("PBEPasswordTool","encode",e.getMessage()), e);
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.PasswordEncodingService#decode(java.lang.String, java.lang.String)
     */
    public String decode(String userName, String encodedPassword) throws SecurityException
    {
        try
        {
            // prevent dictionary attacks as well as copying of encoded passwords by using the userName as salt
            PBEParameterSpec cipherSpec = new PBEParameterSpec(createSalt(userName.getBytes("UTF-8")), PKCS_5_ITERATIONCOUNT);
            
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITM);
            cipher.init(Cipher.DECRYPT_MODE,pbeKey,cipherSpec);
            
            return new String(cipher.doFinal(Base64.decodeBase64(encodedPassword.getBytes("UTF-8"))), "UTF-8");
        }
        catch (Exception e)
        {
            throw new SecurityException(SecurityException.UNEXPECTED.create("PBEPasswordTool","decode",e.getMessage()), e);
        }
    }
    
    /*
     * Create a PCKS #5 salt using the BASE_PCKS_5_SALT overlayed with the provided secret parameter
     */
    private byte[] createSalt(byte[] secret)
    {
        byte[] salt = new byte[PKCS_5_BASE_SALT.length];
        int i = 0;
        for (;i < salt.length && i < secret.length; i++)
        {
            salt[i] = secret[i];
        }
        for (; i < salt.length; i++)
        {
            salt[i] = PKCS_5_BASE_SALT[i];
        }
        return salt;
    }

    public static void main(String args[]) throws Exception
    {
        if (args.length != 4 || (!args[0].equals("encode") && !args[0].equals("decode")))
        {
            System.err.println("Encode/Decode a user password using Password Based Encryption");
            System.err.println("Usage: PBEPasswordTool <encode|decode> <encoding-password> <username> <password>");
            System.err.println("  encode|decode    : specify if to encode or decode the provided password");
            System.err.println("  encoding-password: the password to be used for encoding and decoding");
            System.err.println("  username         : the name of the user to which the provided password belongs");
            System.err.println("  password         : the cleartext password to encode, or the encoded password to decode\n");
        }
        else if (args[0].toLowerCase().equals("encode"))
        {
            System.out.println("Encoded password: "+new PBEPasswordTool(args[1]).encode(args[2],args[3]));
        }
        else
        {
            System.out.println("Decoded password: "+new PBEPasswordTool(args[1]).decode(args[2],args[3]));
        }
    }
}
