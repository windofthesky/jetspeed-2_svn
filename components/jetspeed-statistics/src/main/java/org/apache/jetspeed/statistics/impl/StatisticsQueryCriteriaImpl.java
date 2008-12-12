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
package org.apache.jetspeed.statistics.impl;

import org.apache.jetspeed.statistics.StatisticsQueryCriteria;

public class StatisticsQueryCriteriaImpl implements StatisticsQueryCriteria
{

    private String user;

    private String timePeriod;

    private String queryType;

    private String listsize;

    private String sorttype;

    private String sortorder;

    /**
     * @return Returns the ipAddress.
     */
    public String getTimePeriod()
    {
        return timePeriod;
    }

    /**
     * @param ipAddress
     *            The ipAddress to set.
     */
    public void setTimePeriod(String ipAddress)
    {
        this.timePeriod = ipAddress;
    }

    /**
     * @return Returns the user.
     */
    public String getUser()
    {
        return user;
    }

    /**
     * @param user
     *            The user to set.
     */
    public void setUser(String user)
    {
        this.user = user;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.statistics.StatisticsQueryCriteria#getQueryType()
     */
    public String getQueryType()
    {

        return queryType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.statistics.StatisticsQueryCriteria#setQueryType(java.lang.String)
     */
    public void setQueryType(String queryType)
    {
        this.queryType = queryType;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.statistics.StatisticsQueryCriteria#getListsize()
     */
    public String getListsize()
    {
        return this.listsize;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.statistics.StatisticsQueryCriteria#getSorttype()
     */
    public String getSorttype()
    {
        return this.sorttype;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.statistics.StatisticsQueryCriteria#setListsize(java.lang.String)
     */
    public void setListsize(String listsize)
    {
        this.listsize = listsize;

    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.statistics.StatisticsQueryCriteria#setSorttype(java.lang.String)
     */
    public void setSorttype(String sorttype)
    {
        this.sorttype = sorttype;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.statistics.StatisticsQueryCriteria#getSortorder()
     */
    public String getSortorder()
    {
        return this.sortorder;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.statistics.StatisticsQueryCriteria#setSortorder(java.lang.String)
     */
    public void setSortorder(String sortorder)
    {
        this.sortorder = sortorder;

    }
}
