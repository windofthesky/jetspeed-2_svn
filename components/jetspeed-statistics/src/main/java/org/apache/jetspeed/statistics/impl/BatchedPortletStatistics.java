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

import javax.sql.DataSource;

/**
 * Batches up LogRecord statistics, and flushes them periodically to the
 * appropriate table in the database.
 * <P>
 * IMPORTANT: It is the caller's responsibility to insure that the LogRecord
 * instances added to a BatchedStatistics instance are all of the same type
 * (Portlet Access, Page Access, or User Logout).
 * 
 * @author <a href="mailto:chris@bluesunrise.com">Chris Schaefer </a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */
public class BatchedPortletStatistics extends BatchedStatistics
{

    public BatchedPortletStatistics(DataSource ds, int batchSize,
            long msElapsedTimeThreshold, String name)
    {
        super(ds, batchSize, msElapsedTimeThreshold, name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.statistics.impl.BatchedStatistics#canDoRecordType(org.apache.jetspeed.statistics.impl.LogRecord)
     */
    public boolean canDoRecordType(LogRecord rec)
    {
        return (rec instanceof PortletLogRecord);
    }

    /**
     * @param stm
     * @param recordIterator
     * @throws SQLException
     */
    protected void loadOneRecordToStatement(PreparedStatement stm, LogRecord rec)
            throws SQLException
    {
        PortletLogRecord record = (PortletLogRecord) rec;

        stm.setString(1, record.getIpAddress());
        stm.setString(2, record.getUserName());
        stm.setTimestamp(3, record.getTimeStamp());
        stm.setString(4, record.getPagePath());
        stm.setString(5, record.getPortletName());
        stm.setInt(6, record.getStatus());
        stm.setLong(7, record.getMsElapsedTime());

    }

    /**
     * @param con
     * @return
     * @throws SQLException
     */
    protected PreparedStatement getPreparedStatement(Connection con)
            throws SQLException
    {
        PreparedStatement stm;
        stm = con
                .prepareStatement("INSERT INTO PORTLET_STATISTICS VALUES(?,?,?,?,?,?,?)");
        return stm;
    }

}
