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

import java.util.Comparator;

import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.SecurityException;

/**
 * <p>
 * Maintains a configurable FIFO stack of used password credentials for a principal.
 * It also requires a unique password (with regards to the values currently in the stack) when 
 * a password is changed directly by the user itself.</p>
 * <p>
 * The historical passwords are maintained as {@link PasswordCredential} instances with a {@link PasswordCredential#getType() type}
 *  value {@link PasswordCredential#TYPE_HISTORICAL} to distinguish them from the current password credential.</p>
 * <p>
 * <em>Implementation Note:</em><br>
 * When a new password is about to be saved, a new <em>copy</em> of the current credential is saved as
 * a historic password credential. This means that the current password credential <em>instance</em> remains the same.</p>
 * <p>
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class PasswordHistoryInterceptor extends AbstractPasswordCredentialInterceptorImpl
{
    private int historySize;
    
    private static final Comparator<PasswordCredential> internalCredentialCreationDateComparator =
        new Comparator<PasswordCredential>()
        {
            public int compare(PasswordCredential o1, PasswordCredential o2)
            {
                return o1.getCreationDate().compareTo(o2.getCreationDate());
            }
        };
    
    /**
     * @param historySize stack size maintained for historical passwords
     */
    public PasswordHistoryInterceptor(int historySize)
    {
        this.historySize = historySize;
    }
    
    public void beforeSetPassword(PasswordCredential credential, String password, boolean authenticated) throws SecurityException
    {
/* TODO     
        Collection internalCredentials = internalUser.getCredentials();
        ArrayList historicalPasswordCredentials = new ArrayList();
        if ( internalCredentials != null )
        {
            InternalCredential currCredential;
            Iterator iter = internalCredentials.iterator();
            
            while (iter.hasNext())
            {
                currCredential = (InternalCredential) iter.next();
                if (currCredential.getType() == InternalCredential.PRIVATE )
                {
                    if ((null != currCredential.getClassname())
                            && (currCredential.getClassname().equals(HISTORICAL_PASSWORD_CREDENTIAL)))
                    {
                        historicalPasswordCredentials.add(currCredential);
                    }
                }
            }
        }
        if (historicalPasswordCredentials.size() > 1)
        {
            Collections.sort(historicalPasswordCredentials,internalCredentialCreationDateComparator);
        }
        
        int historyCount = historicalPasswordCredentials.size();
        InternalCredential historicalPasswordCredential;
        if ( authenticated )
        {
            // check password already used
            for ( int i = 0; i < historyCount && i < historySize; i++ )
            {
                historicalPasswordCredential = (InternalCredential)historicalPasswordCredentials.get(i);
                if ( historicalPasswordCredential.getValue() != null &&
                        historicalPasswordCredential.getValue().equals(password) )
                {
                    throw new PasswordAlreadyUsedException();
                }
            }
        }

        for ( int i = historySize-1; i < historyCount; i++ )
        {
            credentials.remove(historicalPasswordCredentials.get(i));
        }
        historicalPasswordCredential = new InternalCredentialImpl(credential,HISTORICAL_PASSWORD_CREDENTIAL);
        credentials.add(historicalPasswordCredential);
        
        // fake update to current InternalCredential as being an insert of a new one
        credential.setCreationDate(new Timestamp(new Date().getTime()));
*/        
    }
}
