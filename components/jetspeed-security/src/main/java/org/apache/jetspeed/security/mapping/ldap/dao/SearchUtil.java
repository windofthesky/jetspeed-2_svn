/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.security.mapping.ldap.dao;

import org.apache.commons.lang.StringUtils;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.OrFilter;

import org.apache.jetspeed.security.mapping.ldap.filter.SimpleFilter;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 */
public class SearchUtil
{

    public static Filter andFilters(Filter... filters)
    {
        AndFilter andFilter = new AndFilter();
        for (Filter filter : filters)
        {
            andFilter.and(filter);
        }
        return andFilter;
    }

    public static Filter constructMatchingFieldsFilter(Filter baseFilter,
            String[]... fieldNamesAndValues)
    {
        AndFilter filter = new AndFilter();
        for (String[] nameAndValues : fieldNamesAndValues)
        {
            if (nameAndValues.length > 1)
            {
                String name = nameAndValues[0];
                OrFilter fieldFilter = new OrFilter();
                for (int i = 1; i < nameAndValues.length; i++)
                {
                    fieldFilter.or(new EqualsFilter(name, nameAndValues[i]));
                }
                filter.and(fieldFilter);
            }
        }
        return baseFilter != null ? andFilters(baseFilter, filter) : filter;
    }

    public static Filter constructMatchingFieldsFilter(String baseFilter,
            String[]... fieldNamesAndValues)
    {
        return constructMatchingFieldsFilter(new SimpleFilter(baseFilter),
                fieldNamesAndValues);
    }

}
