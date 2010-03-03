/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.page.impl;


/**
 * A transactioned operation is a single Page Manager DML operation that was applied
 * to the OJB cache. Im finding that OJB is not properly synchronizing its cache
 * upon rollback of database transactions. This code may not be needed in future
 * versions of OJB which have fixed this bug.
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class TransactionedOperation 
{
    public static final int ADD_OPERATION = 0;
    public static final int UPDATE_OPERATION = 1;
    public static final int ADD_FRAGMENT_PROPERTIES_OPERATION = 2;
    public static final int UPDATE_FRAGMENT_PROPERTIES_OPERATION = 3;
    public static final int ADD_PRINCIPAL_FRAGMENT_PROPERTIES_OPERATION = 4;
    public static final int UPDATE_PRINCIPAL_FRAGMENT_PROPERTIES_OPERATION = 5;
    private String path;
    private String fragmentKey;
    private String principalKey;
    private int transactionType;
    
    public TransactionedOperation(String key, int type)
    {
        this.transactionType = type;
        switch (transactionType)
        {
            case ADD_OPERATION:
            case UPDATE_OPERATION:
                this.path = key;
                break;
            case ADD_FRAGMENT_PROPERTIES_OPERATION:
            case UPDATE_FRAGMENT_PROPERTIES_OPERATION:
                this.fragmentKey = key;
                break;
            case ADD_PRINCIPAL_FRAGMENT_PROPERTIES_OPERATION:
            case UPDATE_PRINCIPAL_FRAGMENT_PROPERTIES_OPERATION:
                this.principalKey = key;
                break;
        }
    }
    
    public String getPath()
    {
        return path;
    }
    
    public void setPath(String path)
    {
        this.path = path;
    }

    public String getFragmentKey()
    {
        return fragmentKey;
    }
    
    public void setFragmentKey(String fragmentKey)
    {
        this.fragmentKey = fragmentKey;
    }

    public String getPrincipalKey()
    {
        return principalKey;
    }
    
    public void setPrincipalKey(String principalKey)
    {
        this.principalKey = principalKey;
    }
    
    public int getTransactionType()
    {
        return transactionType;
    }
   
    public void setTransactionType(int transactionType)
    {
        this.transactionType = transactionType;
    }
}