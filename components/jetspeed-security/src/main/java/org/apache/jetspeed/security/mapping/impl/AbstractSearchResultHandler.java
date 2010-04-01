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
package org.apache.jetspeed.security.mapping.impl;

import org.apache.jetspeed.security.mapping.SearchResultCallbackHandler;

/**
 * @author <a href="mailto:ate@douma.nu>Ate Douma</a>
 * @version $Id$
 */
public abstract class AbstractSearchResultHandler implements SearchResultCallbackHandler
{
    private final int maxCount;
    private int count;
    private int size;
    private boolean aborted;
        
    public AbstractSearchResultHandler()
    {
        this(Integer.MAX_VALUE);
    }
    
    public AbstractSearchResultHandler(int maxCount)
    {
        this.maxCount = maxCount < 1 ? Integer.MAX_VALUE : maxCount;
    }

    public final boolean handleSearchResult(Object result, int pageSize, int pageIndex, int index)
    {
        count++;
        if (!aborted)
        {
            boolean noExceptions = false;
            try
            {
                processSearchResult(result, pageSize, pageIndex, index);
                noExceptions = true;
            }
            finally
            {
                if (!noExceptions)
                {
                    aborted = true;
                }
            }
            if (!aborted)
            {
                size++;
                if (count > maxCount)
                {
                    aborted = true;
                }
            }
        }
        return !aborted;
    }
    
    public final boolean isAborted()
    {
        return aborted;
    }
    
    protected final void setAborted()
    {
        aborted = true;
    }
    
    public final int getMaxCount()
    {
        return maxCount;
    }
    
    public final int getCount()
    {
        return count;
    }
    
    public final int getSize()
    {
        return size;
    }
    
    protected abstract void processSearchResult(Object result, int pageSize, int pageIndex, int index);
}
