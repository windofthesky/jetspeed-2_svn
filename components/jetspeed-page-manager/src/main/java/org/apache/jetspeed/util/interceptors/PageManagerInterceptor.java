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

import java.util.ArrayList;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.jetspeed.page.impl.DatabasePageManager;
import org.apache.jetspeed.page.impl.DatabasePageManagerCache;

/**
 * Aspect that will attempt to rollback cache entries upon Page Manager failures
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 */
public class PageManagerInterceptor implements MethodInterceptor
{
    /** Serialization version identifier */
    private static final long serialVersionUID = -1316279974504594833L;
    
    private final String[] cacheTransactionMethodsPrefix;
    private final String[] cacheTransactionMethods;
    
    public PageManagerInterceptor(List<String> cacheTransactionMethods)
    {
        List<String> cacheTransactionMethodsPrefixList = new ArrayList<String>();
        List<String> cacheTransactionMethodsList = new ArrayList<String>();
        for (String cacheTransactionMethod : cacheTransactionMethods)
        {
            if (cacheTransactionMethod.endsWith("*"))
            {
                cacheTransactionMethodsPrefixList.add(cacheTransactionMethod.substring(0,cacheTransactionMethod.length()-1));
            }
            else
            {
                cacheTransactionMethodsPrefixList.add(cacheTransactionMethod);
            }
        }
        this.cacheTransactionMethodsPrefix = cacheTransactionMethodsPrefixList.toArray(new String[cacheTransactionMethodsPrefixList.size()]);
        this.cacheTransactionMethods = cacheTransactionMethodsList.toArray(new String[cacheTransactionMethodsList.size()]);
    }
    
    /**
     * Encloses <code>super.invoke()</code> in a try/catch block, where the
     * catch block contains additional retry logic.
     */
    public Object invoke(MethodInvocation invocation) throws Throwable
    {
        // filter DBPM cache transactional processing by method name
        String methodName = invocation.getMethod().getName();
        boolean performCacheTransactionProcessing = false;
        for (String cacheTransactionMethodPrefix : cacheTransactionMethodsPrefix)
        {
            if (methodName.startsWith(cacheTransactionMethodPrefix))
            {
                performCacheTransactionProcessing = true;
                break;
            }
        }
        if (!performCacheTransactionProcessing)
        {
            for (String cacheTransactionMethod : cacheTransactionMethods)
            {
                if (methodName.equals(cacheTransactionMethod))
                {
                    performCacheTransactionProcessing = true;
                    break;
                }
            }
        }
        // invoke DBPM entry point
        try
        {            
            return invocation.proceed();
        } 
        catch (Exception exp)
        {
            // rollback cache transactions
            if (performCacheTransactionProcessing)
            {
                DatabasePageManagerCache.rollbackTransactions();
                DatabasePageManager.rollbackTransactions();
            }
            throw exp;
        }
        finally
        {
            // clear cache transaction tracking
            if (performCacheTransactionProcessing)
            {
                DatabasePageManagerCache.clearTransactions();
                DatabasePageManager.clearTransactions();
            }
        }
    }

}
