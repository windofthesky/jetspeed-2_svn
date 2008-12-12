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
package org.apache.jetspeed.util.interceptors;

import java.sql.SQLException;
import java.util.StringTokenizer;

import org.aopalliance.intercept.MethodInvocation;

/**
 * MethodReplayDecisionMaker intended for use with methods marked as
 * transactional, where the decision to replay the method is based on the
 * content of the underlying exception from the resource.
 * 
 * @author a336317
 * @author a202225
 */
public class TransactionalMethodReplayDecisionMaker implements
        MethodReplayDecisionMaker
{

    private int[] sqlErrorCodes;

    public boolean shouldReplay(MethodInvocation invocation, Exception exception)
    {
        SQLException sqlException = findSQLException(exception);
        if (sqlException != null)
        {
            int errorCode = sqlException.getErrorCode();

            if (errorCode != 0) { return isErrorCodeListed(errorCode); }
        }

        return false;
    }

    // Recursively search the exception tree looking for the first SQLException
    protected SQLException findSQLException(Exception exception)
    {
        SQLException foundException = null;
        if (exception != null)
        {
            if (exception instanceof SQLException)
            {
                foundException = (SQLException) exception;
            } 
            else
            {
                // Look at the cause
                Throwable throwable = exception.getCause();
                if (throwable != null && throwable instanceof Exception)
                {
                    foundException = findSQLException((Exception) throwable);
                }
            }
        }

        return foundException;
    }

    public void setSqlErrorCodes(String sqlErrorCodesString)
    {
        StringTokenizer tokenizer = new StringTokenizer(sqlErrorCodesString,
                ",");

        this.sqlErrorCodes = new int[tokenizer.countTokens()];

        for (int i = 0; tokenizer.hasMoreTokens(); i++)
        {
            this.sqlErrorCodes[i] = new Integer(tokenizer.nextToken())
                    .intValue();
        }
    }

    private boolean isErrorCodeListed(int errorCode)
    {
        for (int i = 0; i < this.sqlErrorCodes.length; i++)
        {
            if (this.sqlErrorCodes[i] == errorCode) return true;
        }

        return false;
    }
}