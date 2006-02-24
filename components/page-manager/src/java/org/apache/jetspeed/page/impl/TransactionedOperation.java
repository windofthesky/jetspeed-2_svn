/*
 * Copyright 2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
    private String path;
    private int transactionType;
    
    public TransactionedOperation(String path, int type)
    {
        this.path = path;
        this.transactionType = type;
    }

    
    public String getPath()
    {
        return path;
    }

    
    public void setPath(String path)
    {
        this.path = path;
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