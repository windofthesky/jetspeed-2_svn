/* Copyright 2004 Apache Software Foundation
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.om.InternalCredential;
import org.apache.jetspeed.security.om.InternalUserPrincipal;
import org.apache.jetspeed.security.om.impl.InternalCredentialImpl;

/**
 * <p>
 * InternalPasswordCredentialHistoryHandlingInterceptor
 * </p>
 * 
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id$
 */
public class InternalPasswordCredentialHistoryHandlingInterceptor extends
        InternalPasswordCredentialStateHandlingInterceptor
{
    private int historySize;
    
    private static String HISTORICAL_PASSWORD_CREDENTIAL = "org.apache.jetspeed.security.spi.impl.HistoricalPasswordCredentialImpl";
    
    private static final Comparator internalCredentialCreationDateComparator =
        new Comparator()
        {
            public int compare(Object obj1, Object obj2)
            {
                return ((InternalCredential)obj2).getCreationDate().compareTo(((InternalCredential)obj1).getCreationDate());
            }
        };
    
    public InternalPasswordCredentialHistoryHandlingInterceptor(int maxNumberOfAuthenticationFailures,
            int maxLifeSpanInDays, int historySize)
    {
        super(maxNumberOfAuthenticationFailures, maxLifeSpanInDays);
        this.historySize = historySize;
    }
    
    /**
     * @see org.apache.jetspeed.security.spi.InternalPasswordCredentialStateHandlingInterceptor#beforeSetPassword(org.apache.jetspeed.security.om.InternalUserPrincipal, java.util.Collection, java.lang.String, org.apache.jetspeed.security.om.InternalCredential, java.lang.String, boolean)
     */
    public void beforeSetPassword(InternalUserPrincipal internalUser, Collection credentials, String userName,
            InternalCredential credential, String password, boolean authenticated) throws SecurityException
    {
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
        
        int historyCount = historyCount = historicalPasswordCredentials.size();
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
                    throw new SecurityException(SecurityException.PASSWORD_ALREADY_USED);
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
        
        super.beforeSetPassword(internalUser, credentials, userName, credential, password, authenticated);
    }
}
