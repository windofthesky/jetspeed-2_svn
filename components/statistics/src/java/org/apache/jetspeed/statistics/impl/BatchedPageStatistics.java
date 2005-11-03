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

import javax.sql.DataSource;

/**
 * <p>
 * BatchedPageStatistics
 * </p>
 * 
 * @author <a href="mailto:chris@bluesunrise.com">Chris Schaefer</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: TestPortletEntityDAO.java,v 1.3 2005/05/24 14:43:19 ate Exp $
 */
public class BatchedPageStatistics extends BatchedStatistics
{

    public BatchedPageStatistics(DataSource ds, int batchSize,
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
        return (rec instanceof PageLogRecord);
    }

    /**
     * @param stm
     * @param recordIterator
     * @throws SQLException
     */
    protected void loadOneRecordToStatement(PreparedStatement stm, LogRecord rec)
            throws SQLException
    {
        PageLogRecord record = (PageLogRecord) rec;

        stm.setString(1, record.getIpAddress());
        stm.setString(2, record.getUserName());
        stm.setTimestamp(3, record.getTimeStamp());
        stm.setString(4, record.getPagePath());
        stm.setInt(5, record.getStatus());
        stm.setLong(6, record.getMsElapsedTime());

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
                .prepareStatement("INSERT INTO PAGE_STATISTICS VALUES(?,?,?,?,?,?)");
        return stm;
    }

}
