/*
 * Copyright 2000-2004 The Apache Software Foundation.
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
package org.apache.jetspeed.util.interceptors;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.page.impl.DatabasePageManagerCache;

/**
 * Aspect that will attempt to rollback cache entries upon Page Manager failures
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 */
public class PageManagerInterceptor implements MethodInterceptor
{

    /** Log reference */
    private Log log = LogFactory.getLog(PageManagerInterceptor.class);

    /** Serialization version identifier */
    private static final long serialVersionUID = -1316279974504594833L;

    /**
     * Encloses <code>super.invoke()</code> in a try/catch block, where the
     * catch block contains additional retry logic.
     */
    public Object invoke(MethodInvocation invocation) throws Throwable
    {
        try
        {            
            return invocation.proceed();
        } 
        catch (Exception exp)
        {
            DatabasePageManagerCache.rollbackTransactions();
            throw exp;
        }
    }

}
