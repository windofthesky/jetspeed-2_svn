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

/*
 * Adapted and simplyfied from Apache Directory Studio:
 *   /directory/studio/trunk/ldapbrowser-core/src/main/java/org/apache/directory/studio/ldapbrowser/core/model/Password.java, svn r827980
 *   /directory/studio/trunk/ldif-parser/src/main/java/org/apache/directory/studio/ldifparser/LdifUtils.java, svn r827963
 *   /directory/studio/trunk/ldapbrowser-core/src/main/java/org/apache/directory/studio/ldapbrowser/core/utils/UnixCrypt.java, svn r827980
 * 
 * UnixCrypt.java has been copied "as is", see /org/apache/jetspeed/security/util/UnixCrypt.java
 */

package org.apache.jetspeed.security.spi.impl;

import java.io.UnsupportedEncodingException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.apache.commons.codec.binary.Base64;

import org.apache.jetspeed.security.CredentialPasswordEncoder;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.util.UnixCrypt;

/**
 * <p> LdapCredentialPasswordEncoder </p>
 * 
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id$
 */
public class LdapCredentialPasswordEncoder implements CredentialPasswordEncoder
{
    private static final long serialVersionUID = -575380709827140201L;
    
    private static enum HashMethod { SHA, SSHA, MD5, SMD5, CRYPT };
    private final HashMethod hashMethod;
    private final MessageDigest digester;
    private final boolean saltedDigest;

    public LdapCredentialPasswordEncoder(String hashMethod) throws NoSuchAlgorithmException
    {
        if (HashMethod.CRYPT.toString().equals(hashMethod))
        {
            this.hashMethod = HashMethod.CRYPT;
            this.digester = null;
            this.saltedDigest = false;
        }
        else if (HashMethod.SHA.toString().equals(hashMethod))
        {
            this.hashMethod = HashMethod.SHA;
            this.digester = MessageDigest.getInstance("SHA");
            this.saltedDigest = false;
        }
        else if (HashMethod.SSHA.toString().equals(hashMethod))
        {
            this.hashMethod = HashMethod.SSHA;
            this.digester = MessageDigest.getInstance("SHA");
            this.saltedDigest = true;
        }
        else if (HashMethod.MD5.toString().equals(hashMethod))
        {
            this.hashMethod = HashMethod.MD5;
            this.digester = MessageDigest.getInstance("MD5");
            this.saltedDigest = false;
        }
        else if (HashMethod.SMD5.toString().equals(hashMethod))
        {
            this.hashMethod = HashMethod.SMD5;
            this.digester = MessageDigest.getInstance("MD5");
            this.saltedDigest = true;
        }
        else
        {
            throw new IllegalArgumentException("Unsupported hashMethod " + hashMethod);
        }
    }

    /**
     * @see org.apache.jetspeed.security.CredentialPasswordEncoder#encode(java.lang.String, java.lang.String)
     */
    public String encode(String userName, String clearTextPassword) throws SecurityException
    {
        StringBuffer sb = new StringBuffer().append('{').append(hashMethod).append('}');

        if ( hashMethod == HashMethod.CRYPT )
        {
            byte[] salt = new byte[2];
            SecureRandom sr = new SecureRandom();
            int i1 = sr.nextInt( 64 );
            int i2 = sr.nextInt( 64 );
            salt[0] = ( byte ) ( i1 < 12 ? ( i1 + '.' ) : i1 < 38 ? ( i1 + 'A' - 12 ) : ( i1 + 'a' - 38 ) );
            salt[1] = ( byte ) ( i2 < 12 ? ( i2 + '.' ) : i2 < 38 ? ( i2 + 'A' - 12 ) : ( i2 + 'a' - 38 ) );
            String saltString = utf8decode(salt);
            sb.append(saltString).append(UnixCrypt.crypt(clearTextPassword, saltString).substring(2));
        }
        else
        {
            sb.append(digest(clearTextPassword, saltedDigest));
        }

        return sb.toString();
    }

    private static byte[] utf8encode( String s )
    {
        try
        {
            return s.getBytes( "UTF-8" );
        }
        catch ( UnsupportedEncodingException e )
        {
            return s.getBytes();
        }
    }

    private static String utf8decode( byte[] b )
    {
        try
        {
            return new String( b, "UTF-8" );
        }
        catch ( UnsupportedEncodingException e )
        {
            return new String( b );
        }
    }
    
    private String digest( String password, boolean salted )
    {
        byte[] result;
        
        byte[] passwordBytes = utf8encode( password );
        
        synchronized (digester)
        {
            digester.reset();
            if ( salted )
            {
                // set salt
                byte[] salt = new byte[8];
                new SecureRandom().nextBytes( salt );
                digester.update( passwordBytes );
                digester.update( salt );
                byte[] hashedPassword = digester.digest();
                result = new byte[hashedPassword.length + salt.length];
                
                System.arraycopy( hashedPassword, 0, result, 0, hashedPassword.length );
                System.arraycopy( salt, 0, result, hashedPassword.length, salt.length );
            }
            else
            {
                result = digester.digest(passwordBytes);
            }
        }
        return utf8decode(Base64.encodeBase64(result));
    }
    
    public static void main( String[] arg ) throws Exception
    {
        if ( arg.length != 2 )
        {
            System.err.println( "Usage - java org.apache.jetspeed.security.spi.impl.LdapCredentialPasswordEncoder <CRYPT|SHA|SSHA|MD5|SMD5> <password>" );
            System.exit( 1 );
        }
        else
        {
            System.err.println( "Encoded password=" + new LdapCredentialPasswordEncoder(arg[0]).encode(null, arg[1]));
        }
    }
}
