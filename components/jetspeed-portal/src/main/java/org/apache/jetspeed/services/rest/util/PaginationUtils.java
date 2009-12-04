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
package org.apache.jetspeed.services.rest.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * PaginationUtils
 * 
 * @version $Id$
 */
public class PaginationUtils
{
    
    private PaginationUtils()
    {
        
    }
    
    public static List<? extends Object> subList(final List<? extends Object> list, int beginIndex, int maxResults)
    {
        if (beginIndex < 0 || (beginIndex == 0 && maxResults < 0))
        {
            return list;
        }
        else if (beginIndex >= list.size())
        {
            return Collections.emptyList();
        }
        else
        {
            if (maxResults < 0)
            {
                return list.subList(beginIndex, list.size());
            }
            else
            {
                return list.subList(beginIndex, Math.min(list.size(), beginIndex + maxResults));
            }
        }
    }
    
    public static Collection<? extends Object> subCollection(final Collection<? extends Object> collection, int beginIndex, int maxResults)
    {
        if (collection instanceof List)
        {
            return subList((List<? extends Object>) collection, beginIndex, maxResults);
        }
        else
        {
            return subList(new ArrayList<Object>(collection), beginIndex, maxResults);
        }
    }
    
}
