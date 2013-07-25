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

import org.apache.jetspeed.security.AlgorithmUpgradePasswordEncodingService;
import org.apache.jetspeed.security.CredentialPasswordEncoder;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.spi.AlgorithmUpgradeCredentialPasswordEncoder;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * <p>
 * MessageDigestToPBEPasswordUpgradeService allows for migrating from a MessageDigestCredentialPasswordEncoder
 * to the PBEPasswordService
 * </p>
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id:$
 */
public class AlgorithmUpgradePBEPasswordService extends PBEPasswordService implements AlgorithmUpgradeCredentialPasswordEncoder, AlgorithmUpgradePasswordEncodingService
{
    private CredentialPasswordEncoder oldEncoder;
    private Timestamp startPBEPasswordEncoding;
    
    public AlgorithmUpgradePBEPasswordService(String pbePassword, CredentialPasswordEncoder oldEncoder, String startPBEPasswordEncoding) throws InvalidKeySpecException,
            NoSuchAlgorithmException, ParseException
    {
        super(pbePassword);
        this.oldEncoder = oldEncoder;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.startPBEPasswordEncoding = new Timestamp(df.parse(startPBEPasswordEncoding).getTime());
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.AlgorithmUpgradePasswordEncodingService#usesOldEncodingAlgorithm(org.apache.jetspeed.security.PasswordCredential)
     */
    public boolean usesOldEncodingAlgorithm(PasswordCredential credential)
    {
        return usesOldEncodingAlgorithm(credential.isEnabled(), credential.getLastAuthenticationDate(), credential.getPreviousAuthenticationDate());
    }

    public String encode(PasswordCredential credential, String clearTextPassword) throws SecurityException
    {
        if ( usesOldEncodingAlgorithm(credential.isEnabled(), credential.getLastAuthenticationDate(), credential.getPreviousAuthenticationDate()))
        {
            return oldEncoder.encode(credential.getUserName(), clearTextPassword);
        }
        else
        {
            // 2013-07-25: DST: while debugging this problem, it appears credential.getNewPassword is always null,
            // while clearTextPassword had the correct password to encode with new algorithm
            // see: https://issues.apache.org/jira/browse/JS2-1286
            return encode(credential.getUserName(), clearTextPassword); // credential.getNewPassword());
        }
    }

    public void recodeIfNeeded(PasswordCredential credential, String clearTextPassword) throws SecurityException
    {
        if ( usesOldEncodingAlgorithm(credential.isEnabled(), credential.getLastAuthenticationDate(), credential.getPreviousAuthenticationDate()))
        {
            credential.setPassword(encode(credential.getUserName(), clearTextPassword), true);
        }
    }
    
    private boolean usesOldEncodingAlgorithm(boolean encoded, Timestamp lastAuthDate, Timestamp prevAuthDate )
    {
        if ( encoded )
        {
            if ( lastAuthDate != null )
            {
                return lastAuthDate.before(startPBEPasswordEncoding);
            }
            else if ( prevAuthDate != null )
            {
                // password was created, but the user is not authenticated yet
                return prevAuthDate.before(startPBEPasswordEncoding);
            }
            else
            {
                // not yet upgraded encoded password
                return true;
            }
        }
        else
        {
            return false;
        }
    }
}
