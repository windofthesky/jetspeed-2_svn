/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

/**
 * <p>
 * BatchedStatistics
 * </p>
 * 
 * @author <a href="mailto:chris@bluesunrise.com">Chris Schaefer</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: TestPortletEntityDAO.java,v 1.3 2005/05/24 14:43:19 ate Exp $
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
            flush();
            msLastFlushTime = msCurrentTime;
        }
    }

    public void addStatistic(LogRecord logRecord)
    {
        synchronized (logRecords)
        {
            logRecords.add(logRecord);
            checkAndDoFlush();
        }
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
                synchronized(this.thread) {
                    this.thread.wait(msElapsedTimeThreshold / 4);
                }
            } 
            catch (InterruptedException ie)
            {
                keepRunning = false;
            }
            synchronized (logRecords)
            {
                checkAndDoFlush();
            }
        }
        // force a flush on the way out even if the constraints have not been
        // met
        synchronized (logRecords)
        {
            flush();
        }
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

        try
        {
            con = getConnection();
            boolean autoCommit = con.getAutoCommit();
            con.setAutoCommit(false);

            stm = getPreparedStatement(con);
            Iterator recordIterator = logRecords.iterator();
            while (recordIterator.hasNext())
            {
                LogRecord record = (LogRecord) recordIterator.next();

                loadOneRecordToStatement(stm, record);

                stm.addBatch();
            }
            int[] updateCounts = stm.executeBatch();
            con.commit();
            // only clear the records if we actually store them...
            logRecords.clear();
            con.setAutoCommit(autoCommit);
        } catch (SQLException e)
        {
            // todo log to standard Jetspeed logger
            e.printStackTrace();
        } finally
        {
            try
            {
                if (stm != null) stm.close();
            } catch (SQLException se)
            {
            }
            releaseConnection(con);
        }
    }

    abstract protected PreparedStatement getPreparedStatement(Connection con)
            throws SQLException;

    abstract protected void loadOneRecordToStatement(PreparedStatement stm,
            LogRecord rec) throws SQLException;

    void releaseConnection(Connection con)
    {
        try
        {
            if (con != null) con.close();
        } catch (SQLException e)
        {
        }
    }

    protected Thread thread;

    protected long msLastFlushTime = 0;

    protected int batchSize = 10;

    protected long msElapsedTimeThreshold = 5000;

    protected List logRecords = new LinkedList();

    protected DataSource ds = null;

    protected String name;

    public abstract boolean canDoRecordType(LogRecord rec);

}
