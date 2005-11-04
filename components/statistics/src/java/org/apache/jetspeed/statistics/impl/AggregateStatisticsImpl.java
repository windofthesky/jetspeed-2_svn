/* Copyright 2005 Apache Software Foundation
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
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

import javax.sql.DataSource;

import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.statistics.AggregateStatistics;
import org.apache.jetspeed.statistics.StatisticsQueryCriteria;



/**
 * AggregateStatisticsImpl
 * 
 * @author <a href="mailto:chris@bluesunrise.com">Chris Schaefer</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class AggregateStatisticsImpl implements AggregateStatistics
{

    float avgProcessingTime;
    float maxProcessingTime;
    float minProcessingTime;
    float stddevProcessingTime;
    int hitcount;
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.statistics.AggregateStatistics#getAvgProcessingTime()
     */
    public float getAvgProcessingTime()
    {
        return this.avgProcessingTime;
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.statistics.AggregateStatistics#getHitCount()
     */
    public int getHitCount()
    {
         return this.hitcount;
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.statistics.AggregateStatistics#getMaxProcessingTime()
     */
    public float getMaxProcessingTime()
    {
        return this.maxProcessingTime;
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.statistics.AggregateStatistics#getMinProcessingTime()
     */
    public float getMinProcessingTime()
    {
        return this.minProcessingTime;
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.statistics.AggregateStatistics#getStdDevProcessingTime()
     */
    public float getStdDevProcessingTime()
    {
        return this.stddevProcessingTime;
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.statistics.AggregateStatistics#setAvgProcessingTime(float)
     */
    public void setAvgProcessingTime(float time)
    {
        this.avgProcessingTime = time;
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.statistics.AggregateStatistics#setHitCount(int)
     */
    public void setHitCount(int hitCount)
    {
        this.hitcount = hitCount;
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.statistics.AggregateStatistics#setMaxProcessingTime(float)
     */
    public void setMaxProcessingTime(float time)
    {
        this.maxProcessingTime = time;
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.statistics.AggregateStatistics#setMinProcessingTime(float)
     */
    public void setMinProcessingTime(float time)
    {
        this.minProcessingTime = time;
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.statistics.AggregateStatistics#setStdDevProcessingTime(float)
     */
    public void setStdDevProcessingTime(float time)
    {
        this.stddevProcessingTime = time;
    }
    
    
    public String toString() {
        String s = "hit count = "+this.hitcount+"\n";
        s = s + "max time = "+this.maxProcessingTime+"\n";
        s = s + "min time = "+this.minProcessingTime+"\n";
        s = s + "avg time = "+this.avgProcessingTime+"\n";
        s = s + "stddev   = "+this.stddevProcessingTime+"\n";
        return s;
    }
}
