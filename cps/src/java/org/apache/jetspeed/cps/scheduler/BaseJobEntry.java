/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.cps.scheduler;

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
