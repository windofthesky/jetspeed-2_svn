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

package org.apache.cornerstone.framework.logging;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
        catch (SQLException se)
        {
            _Logger.error("failed to store log entry", se);
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