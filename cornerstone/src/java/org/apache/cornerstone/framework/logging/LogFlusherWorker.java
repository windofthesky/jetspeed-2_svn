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

package org.apache.cornerstone.framework.logging;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import org.apache.cornerstone.framework.api.logging.ILogEntry;
import org.apache.cornerstone.framework.api.worker.IWorkerManager;
import org.apache.cornerstone.framework.bean.helper.BeanHelper;
import org.apache.cornerstone.framework.persistence.connection.BaseConnectionManager;
import org.apache.cornerstone.framework.util.Util;
import org.apache.cornerstone.framework.worker.BaseWorker;
import org.apache.log4j.Logger;

/**
 * 
 * The LogFlusherWorker is responsible for processing
 * a batch of logs by saving then into the DB.
 *
 */

public class LogFlusherWorker extends BaseWorker
{
    public static final String REVISION = "$Revision$";

    public static final String CONFIG_DATA_SOURCE_NAME = "dataSource.name";
    public static final String QUERY_INSERT = "query.insert";
    public static final String PROPERTIES = "properties";

    /**
     * Constructor
     * @param workerManager
     */    
    public LogFlusherWorker(IWorkerManager workerManager)
    {
        super(workerManager);
    }
    
    /**
     * This nethod processes the inMemeoryLog object
     * passed to it, using a batch nechanism to write it
     * out to the DB.
     * @param Object job the job to be processed
     */
    public void processJob(Object job)
    {
        long startTime = 0;
        long endTime = 0;
        int[] insertSuccess;
        Connection connection = null;
        PreparedStatement prpdStmt = null;
        String sqlStr = getConfigProperty(QUERY_INSERT);

        try
        {
            String dataSourceName = getConfigProperty(CONFIG_DATA_SOURCE_NAME);
            connection = BaseConnectionManager.getSingleton().getConnection(dataSourceName);
            prpdStmt = (PreparedStatement)connection.prepareStatement(sqlStr);
            //TODO: remove this only for testing start time 
            startTime = System.currentTimeMillis();
            List logEntryList = (List) job;
            String[] logEntryProperties = Util.convertStringsToArray(getConfigProperty(PROPERTIES));
            for (int i = 0; i < logEntryList.size(); i++ )
            {
                ILogEntry logEntry = (ILogEntry) logEntryList.get(i);
                for (int j = 1; j <= logEntryProperties.length; j++)
                {
                    String propertyName = logEntryProperties[j - 1];
                    Object value = BeanHelper.getSingleton().getProperty(logEntry, propertyName);
                    prpdStmt.setObject(j, value);
                }
                prpdStmt.addBatch();
            }
            insertSuccess = prpdStmt.executeBatch();
            endTime = System.currentTimeMillis();
        }
        catch (Exception e)
        {
            _Logger.error("failed to store log entry", e);
        }
        finally
        {
            try
            {
                prpdStmt.close();
                connection.close();
            }
            catch(java.sql.SQLException se)
            {
                _Logger.error("failed to close connection", se);
            }
        }

        if (_Logger.isInfoEnabled()) _Logger.info("processing time = " + String.valueOf(endTime - startTime));
    }

    private static Logger _Logger = Logger.getLogger(LogFlusherWorker.class);
}