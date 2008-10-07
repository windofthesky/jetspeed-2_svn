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
package org.apache.jetspeed.security.spi.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.apache.jetspeed.security.CredentialPasswordEncoder;
import org.apache.jetspeed.security.SecurityException;

/**
 * <p>
 * MessageDigestCredentialPasswordEncoder
 * </p>
 * 
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id$
 */
public class MessageDigestCredentialPasswordEncoder implements CredentialPasswordEncoder
{
    // Allow copying of encoded passwords or salt the digest with the userName preventing that
    boolean simpleEncryption = false;
    MessageDigest digester;
    
    public MessageDigestCredentialPasswordEncoder() throws NoSuchAlgorithmException
    {
        this("SHA-1", false);
    }
    
    public MessageDigestCredentialPasswordEncoder(boolean simpleEncryption) throws NoSuchAlgorithmException
    {
        this("SHA-1", simpleEncryption);
    }
    
    public MessageDigestCredentialPasswordEncoder(String algorithm) throws NoSuchAlgorithmException
    {
        this(algorithm, false);
    }
    
    public MessageDigestCredentialPasswordEncoder(String algorithm, boolean simpleEncryption) throws NoSuchAlgorithmException
    {
        this.digester = MessageDigest.getInstance(algorithm);
        this.simpleEncryption = simpleEncryption;
    }
    
    public String getAlgorithm()
    {
        return digester.getAlgorithm();
    }

    /**
     * @see org.apache.jetspeed.security.CredentialPasswordEncoder#encode(java.lang.String, java.lang.String)
     */
    public String encode(String userName, String clearTextPassword)
            throws SecurityException
    {
        byte[] value;
        synchronized(digester)
        {
            digester.reset();
            value = digester.digest(clearTextPassword.getBytes());
            if (!simpleEncryption)
            {
                // don't allow copying of encoded passwords
                digester.update(userName.getBytes());
            }
            value = digester.digest(value);
        }
        return new String(Base64.encodeBase64(value));
    }
}
