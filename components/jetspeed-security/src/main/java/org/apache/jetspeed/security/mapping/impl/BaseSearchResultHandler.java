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
public class BaseSearchResultHandler<T,R> implements SearchResultCallbackHandler
{
    private final int maxCount;
    private int searchPageSize = -1; // disabled
    private int count;
    private int size;
    private boolean aborted;
    private Object feedback;
        
    public BaseSearchResultHandler()
    {
        this(0);
    }
    
    public BaseSearchResultHandler(int maxCount)
    {
        this.maxCount = maxCount < 1 ? 0 : maxCount;
        if (maxCount == 1)
        {
            searchPageSize = 0;
        }
    }
    
    public void setSearchPageSize(int searchPageSize)
    {
        if (searchPageSize < 0)
        {
            this.searchPageSize = 0;
        }
        else if (maxCount > 1 && searchPageSize > maxCount)
        {
            this.searchPageSize = maxCount+1;
        }
        else
        {
            this.searchPageSize = searchPageSize;
        }
    }
    
    public int getSearchPageSize()
    {
        return searchPageSize;
    }
    
    @SuppressWarnings("unchecked")
    public final boolean handleSearchResult(Object result, int pageSize, int pageIndex, int index)
    {
        count++;
        if (!aborted)
        {
            boolean noExceptions = false;
            try
            {
                T mappedResult = mapResult((R)result, pageSize, pageIndex, index);
                if (mappedResult != null)
                {
                    aborted = !processSearchResult(mappedResult, pageSize, pageIndex, index);
                    if (!aborted)
                    {
                        aborted = !postHandleSearchResult(mappedResult, pageSize, pageIndex, index);
                    }
                }
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
                if (maxCount > 0 && count > maxCount)
                {
                    aborted = true;
                }
            }
        }
        return !aborted;
    }
    
    public void setFeedback(Object feedback)
    {
        this.feedback = feedback;
    }
        
    public Object getFeedback()
    {
        return feedback;
    }
    
    public final boolean isAborted()
    {
        return aborted;
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
    
    @SuppressWarnings("unchecked")
    protected T mapResult(R result, int pageSize, int pageIndex, int index)
    {
        return (T)result;
    }

    protected boolean processSearchResult(T result, int pageSize, int pageIndex, int index)
    {
        return true;
    }
    
    protected boolean postHandleSearchResult(T result, int pageSize, int pageIndex, int index)
    {
        return true;
    }
}
