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
package org.apache.jetspeed.statistics.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * BatchedStatistics
 * </p>
 * 
 * @author <a href="mailto:chris@bluesunrise.com">Chris Schaefer </a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */
public abstract class BatchedStatistics implements Runnable
{

    public BatchedStatistics(DataSource ds, int batchSize,
            long msElapsedTimeThreshold, String name)
    {
        this.ds = ds;
        this.msElapsedTimeThreshold = msElapsedTimeThreshold;
        this.batchSize = batchSize;
        this.name = name;
        if (this.name == null)
        {
            this.name = this.getClass().getName();
        }
        msLastFlushTime = System.currentTimeMillis();
        thread = new Thread(this, name);
        
    }
    
    public void startThread() {
        
        thread.start();
        
        // give a quick break until the thread is running
        // we know thread is running when done is false
        while (this.done)
        {
            try
            {
                Thread.sleep(1);
            } catch (InterruptedException e)
            {
            }
        }
    }

    protected Connection getConnection() throws SQLException
    {
        return ds.getConnection();
    }

    /**
     * should only be called from code synchronized to the linked list
     */
    private void checkAndDoFlush()
    {
        long msCurrentTime = System.currentTimeMillis();
        if ((logRecords.size() >= batchSize)
                || (msCurrentTime - msLastFlushTime > msElapsedTimeThreshold))
        {
      	   do {
      	   	flush();
      	   } 
      	   while (logRecords.size() >= batchSize);
      	   
            msLastFlushTime = msCurrentTime;
        }
    }

    public void addStatistic(LogRecord logRecord)
    {
            logRecords.add(logRecord);
            //checkAndDoFlush();
    }

    public boolean isDone()
    {
        return done;
    }

    public void tellThreadToStop()
    {
        keepRunning = false;
        //this.thread.notify();
    }

    private boolean done = true;

    private boolean keepRunning = true;

    public void run()
    {
        done = false;
        while (keepRunning)
        {
            try
            {
                synchronized (this.thread)
                {
                    this.thread.wait(msElapsedTimeThreshold / 4);
                }
            } catch (InterruptedException ie)
            {
                keepRunning = false;
            }
            checkAndDoFlush();
        }
        // force a flush on the way out even if the constraints have not been
        // met
        flush();
        done = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.statistics.impl.BatchedStatistics#flush() should
     *      only be called from code synchronized to the linked list
     */
    public void flush()
    {
        if (logRecords.isEmpty()) return;

        Connection con = null;
        PreparedStatement stm = null;
        boolean autoCommit = true;

        try
        {
            con = getConnection();
            autoCommit = con.getAutoCommit();
            con.setAutoCommit(false);

            stm = getPreparedStatement(con);
            Iterator<LogRecord> recordIterator = logRecords.iterator();
            while (recordIterator.hasNext())
            {
                LogRecord record = recordIterator.next();

                loadOneRecordToStatement(stm, record);

                stm.addBatch();
                
                recordIterator.remove();
            }
            stm.executeBatch();
            con.commit();
            log.debug(stm.getUpdateCount()+" "+name+" stratistics flushed.");
        } catch (SQLException e)
        {
            log.error(e.getMessage(), e);
            try
            {
                con.rollback();
            }
            catch (Exception e2) {}            
        } finally
        {
            try
            {
                if (stm != null) stm.close();
            } catch (SQLException se)
            {
            }
            releaseConnection(con, autoCommit);
        }
    }

    abstract protected PreparedStatement getPreparedStatement(Connection con)
            throws SQLException;

    abstract protected void loadOneRecordToStatement(PreparedStatement stm,
            LogRecord rec) throws SQLException;

    void releaseConnection(Connection con, boolean autoCommit)
    {
        try
        {
            if (con != null) {
            	con.setAutoCommit(autoCommit);
            	con.close();
            }
        } catch (SQLException e)
        {
        }
    }

    protected Thread thread;

    protected long msLastFlushTime = 0;

    protected int batchSize = 10;

    protected long msElapsedTimeThreshold = 5000;

    protected Collection<LogRecord> logRecords = new ConcurrentLinkedQueue<LogRecord>();

    protected DataSource ds = null;

    protected String name;

    public abstract boolean canDoRecordType(LogRecord rec);

    private static final Log log = LogFactory.getLog(BatchedStatistics.class);
}
