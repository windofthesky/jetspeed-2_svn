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

import java.util.ArrayList;
import java.util.List;


/**
 * @author <a href="mailto:ate@douma.nu>Ate Douma</a>
 * @version $Id$
 */
public class CollectingSearchResultHandler<T,R> extends BaseSearchResultHandler<T,R>
{
    private T singleResult;
    private List<T> results;
        
    public CollectingSearchResultHandler()
    {
        super();
    }
    
    public CollectingSearchResultHandler(List<T> results)
    {
        super();
        this.results = results;
    }
    
    public CollectingSearchResultHandler(int maxSize)
    {
        super(maxSize);
    }

    public CollectingSearchResultHandler(int maxSize, List<T> results)
    {
        super(maxSize);
        this.results = results;
    }

    public T getSingleResult()
    {
        return singleResult;
    }
    
    public List<T> getResults()
    {
        if (results == null)
        {
            results = new ArrayList<T>();
        }
        return results;
    }
    
    protected boolean postHandleSearchResult(T mappedResult, int pageSize, int pageIndex, int index)
    {
        if (getMaxCount() == 1)
        {
            singleResult = mappedResult;
        }
        else if (getMaxCount() == 0 || getCount() <= getMaxCount())
        {
            getResults().add(mappedResult);
        }
        return true;
    }    
}
