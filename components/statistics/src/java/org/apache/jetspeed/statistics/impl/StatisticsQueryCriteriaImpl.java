package org.apache.jetspeed.statistics.impl;

import org.apache.jetspeed.statistics.StatisticsQueryCriteria;

public class StatisticsQueryCriteriaImpl implements StatisticsQueryCriteria
{

    String user;

    String timePeriod;

    String queryType;

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
}
