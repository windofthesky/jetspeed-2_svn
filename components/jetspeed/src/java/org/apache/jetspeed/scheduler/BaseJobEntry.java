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
package org.apache.jetspeed.scheduler;

/**
 * BaseJobEntry
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public abstract class BaseJobEntry
{   
    protected int jobId = 0;
    protected int jobSecond = -1;
    protected int jobMinute = -1;
    protected int jobHour = -1;
    protected int weekDay = -1;
    protected int dayOfMonth = -1;
    protected String task;
    protected String email;      
      
    /**
     * Get the JobId
     *
     * @return 
     */
    public int getJobId()
    {
        return jobId;
    }

                                            
    /**
     * Set the value of JobId
     *
     * @param v new value
     */
    public void setJobId(int v) 
    {
        this.jobId = v;          
    }
  

    /**
     * Get the Second
     *
     * @return int
     */
    public int getSecond()
    {
        return jobSecond;
    }

                                            
    /**
     * Set the value of Second
     *
     * @param v new value
     */
    public void setSecond(int v) 
    {
         this.jobSecond = v;
    }
  

    /**
     * Get the Minute
     *
     * @return int
     */
    public int getMinute()
    {
        return jobMinute;
    }

                                            
    /**
     * Set the value of Minute
     *
     * @param v new value
     */
    public void setMinute(int v) 
    {
         this.jobMinute = v;
    }  

    /**
     * Get the Hour
     *
     * @return int
     */
    public int getHour()
    {
        return jobHour;
    }

                                            
    /**
     * Set the value of Hour
     *
     * @param v new value
     */
    public void setHour(int v) 
    {
        this.jobHour = v;
    }

  
    /**
     * Get the WeekDay
     *
     * @return int
     */
    public int getWeekDay()
    {
        return weekDay;
    }
                                          
    /**
     * Set the value of WeekDay
     *
     * @param v new value
     */
    public void setWeekDay(int v) 
    {
         this.weekDay = v;
    }
  

    /**
     * Get the DayOfMonth
     *
     * @return int
     */
    public int getDayOfMonth()
    {
        return dayOfMonth;
    }

                                            
    /**
     * Set the value of DayOfMonth
     *
     * @param v new value
     */
    public void setDayOfMonth(int v) 
    {
        this.dayOfMonth = v;
    }

    /**
     * Get the Task
     *
     * @return String
     */
    public String getTask()
    {
        return task;
    }

                                            
    /**
     * Set the value of Task
     *
     * @param v new value
     */
    public void setTask(String v) 
    {
         this.task = v;
    }

    /**
     * Get the Email
     *
     * @return String
     */
    public String getEmail()
    {
        return email;
    }

                                            
    /**
     * Set the value of Email
     *
     * @param v new value
     */
    public void setEmail(String v) 
    {
         this.email = v;
    }
}
