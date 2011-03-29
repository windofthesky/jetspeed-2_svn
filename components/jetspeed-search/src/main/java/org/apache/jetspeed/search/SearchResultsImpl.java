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
package org.apache.jetspeed.search;

import java.util.Iterator;
import java.util.List;

import org.apache.jetspeed.search.ParsedObject;
import org.apache.jetspeed.search.SearchResults;

/**
 * @author <a href="mailto: jford@apache.org">Jeremy Ford</a>
 *
 */
public class SearchResultsImpl implements SearchResults
{
    List results = null;
    
    public SearchResultsImpl(List results)
    {
        this.results = results;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.search.SearchResults#size()
     */
    public int size()
    {
        return results.size();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.search.SearchResults#iterator()
     */
    public Iterator iterator()
    {
        return results.iterator();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.search.SearchResults#get(int)
     */
    public ParsedObject get(int index)
    {
        return (ParsedObject)results.get(index);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.search.SearchResults#getResults()
     */
    public List getResults()
    {
        return results;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.search.SearchResults#getResults(int, int)
     */
    public List getResults(int fromIndex, int toIndex)
    {
        return results.subList(fromIndex, toIndex);
    }
}
