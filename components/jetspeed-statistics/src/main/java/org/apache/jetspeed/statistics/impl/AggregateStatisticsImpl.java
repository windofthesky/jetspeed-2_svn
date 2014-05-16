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

import org.apache.jetspeed.statistics.AggregateStatistics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * AggregateStatisticsImpl
 * 
 * @author <a href="mailto:chris@bluesunrise.com">Chris Schaefer </a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id: $
 */
public class AggregateStatisticsImpl implements AggregateStatistics
{

    private float avgProcessingTime;

    private float maxProcessingTime;

    private float minProcessingTime;

    private float stddevProcessingTime;

    private int hitcount;

    private List<Map<String,String>> statlist;

    public AggregateStatisticsImpl()
    {
        statlist = new ArrayList<Map<String,String>>();
    }

    public void addRow(Map<String,String> row)
    {
        statlist.add(row);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.statistics.AggregateStatistics#getAvgProcessingTime()
     */
    public float getAvgProcessingTime()
    {
        return this.avgProcessingTime;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.statistics.AggregateStatistics#getHitCount()
     */
    public int getHitCount()
    {
        return this.hitcount;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.statistics.AggregateStatistics#getMaxProcessingTime()
     */
    public float getMaxProcessingTime()
    {
        return this.maxProcessingTime;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.statistics.AggregateStatistics#getMinProcessingTime()
     */
    public float getMinProcessingTime()
    {
        return this.minProcessingTime;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.statistics.AggregateStatistics#setHitCount(int)
     */
    public void setHitCount(int hitCount)
    {
        
        this.hitcount = hitCount;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.statistics.AggregateStatistics#setMaxProcessingTime(float)
     */
    public void setMaxProcessingTime(float time)
    {
        this.maxProcessingTime = Math.round(time);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.statistics.AggregateStatistics#setMinProcessingTime(float)
     */
    public void setMinProcessingTime(float time)
    {
        this.minProcessingTime = Math.round(time);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.statistics.AggregateStatistics#setAvgProcessingTime(float)
     */
    public void setAvgProcessingTime(float time)
    {
        this.avgProcessingTime = Math.round(time);

    }


    public String toString()
    {
        String s = "hit count = " + this.hitcount + "\n";
        s = s + "max time = " + this.maxProcessingTime + "\n";
        s = s + "min time = " + this.minProcessingTime + "\n";
        s = s + "avg time = " + this.avgProcessingTime + "\n";
        s = s + "stddev   = " + this.stddevProcessingTime + "\n";
        String listStr ="";
        Iterator it = this.statlist.iterator();
        int count = 0;
        int size = statlist.size();
        int max = 5;
        while((it.hasNext()) && (count++<max)) {
            Object o = it.next();
            listStr = listStr+"\t"+o+"\n";
        }
        if(size > max) {
            s = s + "\tlist (top "+max+"):\n"+listStr;
        } else {
            s = s + "\tlist ("+size+" entries):\n"+listStr;
        }
        return s;
    }

    /**
     * @return Returns the statlist.
     */
    public List<Map<String,String>> getStatlist()
    {
        return statlist;
    }

    /**
     * @param statlist
     *            The statlist to set.
     */
    public void setStatlist(List<Map<String,String>> statlist)
    {
        this.statlist = statlist;
    }
}
