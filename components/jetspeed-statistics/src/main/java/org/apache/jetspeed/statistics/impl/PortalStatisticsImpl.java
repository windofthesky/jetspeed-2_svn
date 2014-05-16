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

import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.statistics.AggregateStatistics;
import org.apache.jetspeed.statistics.InvalidCriteriaException;
import org.apache.jetspeed.statistics.PortalStatistics;
import org.apache.jetspeed.statistics.StatisticsQueryCriteria;
import org.apache.jetspeed.statistics.UserStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.security.Principal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * <p>
 * PortalStatisticsImpl
 * </p>
 * 
 * @author <a href="mailto:chris@bluesunrise.com">Chris Schaefer </a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */
public class PortalStatisticsImpl implements PortalStatistics
{
    /* CLF logger */
    protected final static Logger logger = LoggerFactory.getLogger(PortalStatisticsImpl.class);

    /* batch of portlet statistics */
    protected BatchedStatistics portletBatch;

    /* batch if page statistics */
    protected BatchedStatistics pageBatch;

    /* batch of user statistics */
    protected BatchedStatistics userBatch;

    /* format string for a portlet access log entry */
    protected static final String portletLogFormat = "{0} {1} {2} [{3}] \"{4} {5} {6}\" {7} {8}";

    /* format string for a page access log entry */
    protected static final String pageLogFormat = "{0} {1} {2} [{3}] \"{4} {5}\" {6} {7}";

    /* Format string for a User Logout log entry */
    protected static final String logoutLogFormat = "{0} {1} {2} [{3}] \"{4}\" {5} {6}";

    protected static final int STATUS_LOGGED_IN = 1;

    protected static final int STATUS_LOGGED_OUT = 2;

    /* the following fields should be settable with Spring injection */
    protected boolean logToCLF = true;

    protected boolean logToDatabase = true;

    protected int maxRecordToFlush_Portlet = 30;

    protected int maxRecordToFlush_User = 30;

    protected int maxRecordToFlush_Page = 30;

    protected long maxTimeMsToFlush_Portlet = 10 * 1000;

    protected long maxTimeMsToFlush_User = 10 * 1000;

    protected long maxTimeMsToFlush_Page = 10 * 1000;

    //protected ConnectionRepositoryEntry jetspeedDSEntry;
    
    /* after this is NOT for injection */

    protected DataSource ds;

    protected int currentUserCount = 0;

    protected Map<String,Map<String,UserStats>> currentUsers;

    /* date formatter */
    protected SimpleDateFormat formatter = null;
    
    private boolean loggingDisabled;

    /**
     * <p>
     * Default constructor.
     * </p>
     */
    
    public PortalStatisticsImpl(boolean logToCLF, boolean logToDatabase,
            int maxRecordToFlush_Portal, int maxRecordToFlush_User,
            int maxRecordToFlush_Page, long maxTimeMsToFlush_Portal,
            long maxTimeMsToFlush_User, long maxTimeMsToFlush_Page,
            DataSource dataSource)
            //ConnectionRepositoryEntry jetspeedDSEntry)
    {

        this.logToCLF = logToCLF;
        this.logToDatabase = logToDatabase;
        this.loggingDisabled = (!logToCLF && !logToDatabase);
        this.maxRecordToFlush_Portlet = maxRecordToFlush_Portal;
        this.maxRecordToFlush_User = maxRecordToFlush_User;
        this.maxRecordToFlush_Page = maxRecordToFlush_Page;
        this.maxTimeMsToFlush_Portlet = maxTimeMsToFlush_Portal;
        this.maxTimeMsToFlush_User = maxTimeMsToFlush_User;
        this.maxTimeMsToFlush_Page = maxTimeMsToFlush_Page;
        //this.jetspeedDSEntry = jetspeedDSEntry;
        this.ds = dataSource;        
        currentUsers = Collections.synchronizedMap(new TreeMap<String,Map<String,UserStats>>());
    }

    public void springInit() throws NamingException
    {
        formatter = new SimpleDateFormat("dd/MM/yyyy:hh:mm:ss z");
        currentUserCount = 0;
    }

    public DataSource getDataSource()
    {
        return ds;
    }

    public void logPortletAccess(RequestContext request, String portletName,
            String statusCode, long msElapsedTime)
    {
        if (loggingDisabled)
            return;
        
        try
        {
            HttpServletRequest req = request.getRequest();
            Principal principal = req.getUserPrincipal();
            String userName = (principal != null) ? principal.getName()
                    : "guest";
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            PortletLogRecord record = new PortletLogRecord();

            record.setPortletName(portletName);
            record.setUserName(userName);
            if (req.getRemoteAddr() != null)
            {
                record.setIpAddress(req.getRemoteAddr());
            }
            ContentPage cp = request.getPage();
            if (cp != null)
            {
                if (cp.getPath() != null)
                {
                    record.setPagePath(cp.getPath());
                }
            }
            record.setStatus(Integer.parseInt(statusCode));
            record.setTimeStamp(timestamp);
            record.setMsElapsedTime(msElapsedTime);

            if (logToCLF)
            {
                saveAccessToCLF(record);
            }
            if (logToDatabase)
            {
                storeAccessToStats(record);
            }
        } catch (Exception e)
        {
            logger.error("Exception", e);
        }
    }

    protected void storeAccessToStats(LogRecord record)
    {

        if (record instanceof PortletLogRecord)
        {
            if (portletBatch == null)
            {
                synchronized (this)
                {
                    if (portletBatch == null)
                    {
                        portletBatch = new BatchedPortletStatistics(ds,
                                this.maxRecordToFlush_Portlet,
                                this.maxTimeMsToFlush_Portlet, "portletLogBatcher");
                        portletBatch.startThread();
                    }
                }
            }
            portletBatch.addStatistic(record);

        }
        if (record instanceof PageLogRecord)
        {
            if (pageBatch == null)
            {
                synchronized (this)
                {
                    if (pageBatch == null)
                    {
                        pageBatch = new BatchedPageStatistics(ds,
                                this.maxRecordToFlush_Page, this.maxTimeMsToFlush_Page,
                                "pageLogBatcher");
                        pageBatch.startThread();
                    }
                }
            }
            pageBatch.addStatistic(record);

        }
        if (record instanceof UserLogRecord)
        {
            if (userBatch == null)
            {
                synchronized (this)
                {
                    if (userBatch == null)
                    {
                        userBatch = new BatchedUserStatistics(ds,
                                this.maxRecordToFlush_User, this.maxTimeMsToFlush_User,
                                "userLogBatcher");
                        userBatch.startThread();
                    }
                }
            }
            userBatch.addStatistic(record);

        }
    }

    protected void saveAccessToCLF(LogRecord record)
    {
        Object[] args =  {""};
        String logMessage = "";
        if (record instanceof PortletLogRecord)
        {
            PortletLogRecord rec = (PortletLogRecord) record;
            Object[] args1 =
            { rec.getIpAddress(), "-", rec.getUserName(), rec.getTimeStamp(),
                    rec.getLogType(), formatter.format(rec.getTimeStamp()),
                    rec.getPortletName(),
                    new Integer(rec.getStatus()).toString(),
                    new Long(rec.getMsElapsedTime())};
            args = args1;
            logMessage = MessageFormat.format(portletLogFormat, args)
                    .toString();
        }
        if (record instanceof PageLogRecord)
        {
            PageLogRecord rec = (PageLogRecord) record;
            Object[] args1 =
            { rec.getIpAddress(), "-", rec.getUserName(), rec.getTimeStamp(),
                    rec.getLogType(), formatter.format(rec.getTimeStamp()),
                    new Integer(rec.getStatus()).toString(),
                    new Long(rec.getMsElapsedTime())};
            args = args1;
            logMessage = MessageFormat.format(pageLogFormat, args).toString();
        }
        if (record instanceof UserLogRecord)
        {
            UserLogRecord rec = (UserLogRecord) record;
            Object[] args1 =
            { rec.getIpAddress(), "-", rec.getUserName(), rec.getTimeStamp(),
                    rec.getLogType(), formatter.format(rec.getTimeStamp()),
                    new Integer(rec.getStatus()).toString(),
                    new Long(rec.getMsElapsedTime())};
            args = args1;
            logMessage = MessageFormat.format(logoutLogFormat, args).toString();
        }
        logger.info(logMessage);
    }

    public void logPageAccess(RequestContext request, String statusCode,
            long msElapsedTime)
    {
        if (loggingDisabled)
            return;

        try
        {
            HttpServletRequest req = request.getRequest();
            Principal principal = req.getUserPrincipal();
            String userName = (principal != null) ? principal.getName()
                    : "guest";
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            PageLogRecord record = new PageLogRecord();

            record.setUserName(userName);
            record.setIpAddress(req.getRemoteAddr());
            ContentPage cp = request.getPage();
            if (cp != null)
            {
                if (cp.getPath() != null)
                {
                    record.setPagePath(cp.getPath());
                }
            }
            record.setStatus(Integer.parseInt(statusCode));
            record.setTimeStamp(timestamp);
            record.setMsElapsedTime(msElapsedTime);

            if (logToCLF)
            {
                saveAccessToCLF(record);
            }
            if (logToDatabase)
            {
                storeAccessToStats(record);
            }

        } catch (Exception e)
        {
            logger.error("Exception", e);
        }
    }

    public void logUserLogout(String ipAddress, String userName,
            long msSessionLength)
    {
        if (loggingDisabled)
            return;

        try
        {

            if (userName == null)
            {
                userName = "guest";
            }

            if (!"guest".equals(userName))
            {
                if (ipAddress == null)
                {
                    ipAddress = "";
                }
                synchronized (currentUsers)
                {
                	UserStats userStats = null;
                	
                	Map<String,UserStats> statsPerUser = currentUsers.get(userName);
                	if(statsPerUser != null && statsPerUser.size() > 0)
                	{
                		userStats = statsPerUser.get(ipAddress);
                	}                	
            	
                	if(userStats != null)
                    {
                    	// only decrement if user has been logged in
                    	currentUserCount = currentUserCount - 1;
                    	userStats.setNumberOfSession(userStats.getNumberOfSessions() - 1);
                        if (userStats.getNumberOfSessions() <= 0)
                        {
                        	statsPerUser.remove(ipAddress);
                            currentUsers.put(userName, statsPerUser);
                        }
                    }
                }
            }

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            UserLogRecord record = new UserLogRecord();

            record.setUserName(userName);
            record.setIpAddress(ipAddress);
            record.setStatus(STATUS_LOGGED_OUT);
            record.setTimeStamp(timestamp);
            record.setMsElapsedTime(msSessionLength);

            if (logToCLF)
            {
                saveAccessToCLF(record);
            }
            if (logToDatabase)
            {
                storeAccessToStats(record);
            }

        } catch (Exception e)
        {
            logger.error("Exception", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.statistics.PortalStatistics#logUserLogin(org.apache.jetspeed.request.RequestContext,
     *      long)
     */
    public void logUserLogin(RequestContext request, long msElapsedLoginTime)
    {
        if (loggingDisabled)
            return;

        try
        {
            HttpServletRequest req = request.getRequest();
            Principal principal = req.getUserPrincipal();
            String userName = (principal != null) ? principal.getName()
                    : "guest";
            String ipAddress = req.getRemoteAddr();
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            UserLogRecord record = new UserLogRecord();

            if (!"guest".equals(userName))
            {
                currentUserCount = currentUserCount + 1;
                
                if (ipAddress == null)
                {
                    ipAddress = "";
                }
                synchronized (currentUsers)
                {
                	UserStats userStats = null;
                    Map<String,UserStats> statsPerUser = currentUsers.get(userName);
                	if(statsPerUser != null && statsPerUser.size() > 0)
                	{
                		userStats = statsPerUser.get(ipAddress);
                	}
                	else
                	{
                		statsPerUser = new TreeMap();
                	}
                	
                	if(userStats == null)
                    {
                        userStats = new UserStatsImpl();
                        userStats.setNumberOfSession(0);
                        userStats.setUsername(userName);
                        userStats.setInetAddressFromIp(ipAddress);                        
                    }
                    
                    userStats.setNumberOfSession(userStats.getNumberOfSessions() + 1);
                    statsPerUser.put(ipAddress, userStats);
            		currentUsers.put(userName, statsPerUser);
                }
            }

            record.setUserName(userName);
            record.setIpAddress(ipAddress);
            record.setStatus(STATUS_LOGGED_IN);
            record.setTimeStamp(timestamp);
            record.setMsElapsedTime(msElapsedLoginTime);

            if (logToCLF)
            {
                saveAccessToCLF(record);
            }
            if (logToDatabase)
            {
                storeAccessToStats(record);
            }

        } catch (Exception e)
        {
            logger.error("Exception", e);
        }

    }

    /**
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    public void springDestroy()
    {
        if (portletBatch != null)
        {
            portletBatch.tellThreadToStop();
            synchronized (portletBatch.thread)
            {
                portletBatch.thread.notify();
            }

        }
        if (userBatch != null)
        {
            userBatch.tellThreadToStop();
            synchronized (userBatch.thread)
            {
                userBatch.thread.notify();
            }
        }
        if (pageBatch != null)
        {
            pageBatch.tellThreadToStop();
            synchronized (pageBatch.thread)
            {
                pageBatch.thread.notify();
            }
        }

        if ((this.currentUserCount != 0) && logger.isDebugEnabled())
        {
            logger.debug("destroying while users are logged in");
        }
        boolean done = false;
        while (!done)
        {
            done = true;
            if (portletBatch != null)
            {
                if (!portletBatch.isDone())
                {
                    done = false;
                }
            }
            if (userBatch != null)
            {
                if (!userBatch.isDone())
                {
                    done = false;
                }
            }
            if (pageBatch != null)
            {
                if (!pageBatch.isDone())
                {
                    done = false;
                }
            }

            try
            {
                Thread.sleep(2);
            } catch (InterruptedException ie)
            {
            }
        }

    }

    protected Date getStartDateFromPeriod(String period, Date end)
    {
        GregorianCalendar gcEnd = new GregorianCalendar();
        gcEnd.setTime(end);
        if (period != null)
        {
            if (period.endsWith("m"))
            {
                // months
                String p = period.substring(0, period.length() - 1);
                int ret = Integer.parseInt(p);
                gcEnd.add(Calendar.MONTH, (ret * -1));
            } else if (period.endsWith("d"))
            {
                // days
                String p = period.substring(0, period.length() - 1);
                int ret = Integer.parseInt(p);
                gcEnd.add(Calendar.HOUR, (ret * 24 * -1));
            } else if (period.endsWith("h"))
            {
                // hours
                String p = period.substring(0, period.length() - 1);
                int ret = Integer.parseInt(p);
                gcEnd.add(Calendar.HOUR, (ret * -1));
            } else if (period.equals("all"))
            {
                gcEnd = new GregorianCalendar();
                gcEnd.set(1968, 07, 15);
            } else
            {
                // minutes
                int ret = Integer.parseInt(period);
                gcEnd.add(Calendar.MINUTE, (ret * -1));
            }
        } else
        {
            gcEnd = new GregorianCalendar();
            gcEnd.set(1968, 07, 15);

        }
        return gcEnd.getTime();
    }

    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.statistics.PortalStatistics#getDefaultEmptyStatisticsQueryCriteria()
     */
    public StatisticsQueryCriteria createStatisticsQueryCriteria()
    {
        return new StatisticsQueryCriteriaImpl();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.statistics.PortalStatistics#getDefaultEmptyAggregateStatistics()
     */
    public AggregateStatistics getDefaultEmptyAggregateStatistics()
    {
        return new AggregateStatisticsImpl();
    }

    /**
     * @see org.apache.jetspeed.statistics.PortalStatistics#queryStatistics(org.apache.jetspeed.statistics.StatisticsQueryCriteria)
     */
    public AggregateStatistics queryStatistics(StatisticsQueryCriteria criteria)
            throws InvalidCriteriaException
    {
        AggregateStatistics as = new AggregateStatisticsImpl();
        String query1;
        String query2;

        String tableName;
        String groupColumn;

        Date end = new Date();
        Date start = getStartDateFromPeriod(criteria.getTimePeriod(), end);

        String queryType = criteria.getQueryType();
        

        if (PortalStatistics.QUERY_TYPE_USER.equals(queryType))
        {
            tableName = "USER_STATISTICS";
            groupColumn = "USER_NAME";
        } else if (PortalStatistics.QUERY_TYPE_PORTLET.equals(queryType))
        {
            tableName = "PORTLET_STATISTICS";
            groupColumn = "PORTLET";
        } else if (PortalStatistics.QUERY_TYPE_PAGE.equals(queryType))
        {
            tableName = "PAGE_STATISTICS";
            groupColumn = "PAGE";
        } else
        {
            throw new InvalidCriteriaException(
                    " invalid queryType passed to queryStatistics");
        }
        String orderColumn = "itemcount";

        String ascDesc = "DESC";

        if (!PortalStatistics.QUERY_TYPE_USER.equals(queryType))
        {
            query1 = "select count(*) as itemcount , MIN(ELAPSED_TIME) as amin ,AVG(ELAPSED_TIME) as aavg ,MAX(ELAPSED_TIME) as amax from "
                    + tableName + " where time_stamp > ? and time_stamp < ?";
            query2 = "select count(*) as itemcount ,"
                    + groupColumn
                    + ", MIN(ELAPSED_TIME) as amin ,AVG(ELAPSED_TIME) as aavg ,MAX(ELAPSED_TIME) as amax "
                    + "from " + tableName
                    + " where time_stamp > ? and time_stamp < ? group by "
                    + groupColumn + "  order by " + orderColumn + " " + ascDesc;
        } else
        {
            query1 = "select count(*) as itemcount , MIN(ELAPSED_TIME) as amin,AVG(ELAPSED_TIME) as aavg ,MAX(ELAPSED_TIME) as amax from "
                    + tableName
                    + " where time_stamp > ? and time_stamp < ? and status = 2";
            query2 = "select count(*) as itemcount ,"
                    + groupColumn
                    + ", MIN(ELAPSED_TIME) as amin ,AVG(ELAPSED_TIME) as aavg ,MAX(ELAPSED_TIME) as amax "
                    + "from "
                    + tableName
                    + " where time_stamp > ? and time_stamp < ? and status = 2 group by "
                    + groupColumn + "  order by " + orderColumn + " " + ascDesc;
        }
        
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try
        {
            con = ds.getConnection();
            
            // query 1
            pstmt = con.prepareStatement(query1);
            pstmt.setTimestamp(1, new Timestamp(start.getTime()));
            pstmt.setTimestamp(2, new Timestamp(end.getTime()));
            rs = pstmt.executeQuery();
            float denominator = 1.0f;
            if (PortalStatistics.QUERY_TYPE_USER.equals(queryType))
            {
                denominator = 1000f * 60f; // this should convert from mS to
                                           // minutes
            }
            if (rs.next())
            {
                as.setHitCount(rs.getInt("itemcount"));

                as.setMinProcessingTime(rs.getFloat("amin") / denominator);
                as.setAvgProcessingTime(rs.getFloat("aavg") / denominator);
                as.setMaxProcessingTime(rs.getFloat("amax") / denominator);
            }
            
            rs.close();
            rs = null;
            pstmt.close();
            pstmt = null;
            
            // query 2
            pstmt = con.prepareStatement(query2);
            pstmt.setTimestamp(1, new Timestamp(start.getTime()));
            pstmt.setTimestamp(2, new Timestamp(end.getTime()));
            rs = pstmt.executeQuery();

            int rowCount = 0;
            int totalRows = 5;
            String listsizeStr = criteria.getListsize();
            int temp = -1;
            try 
            {
                temp = Integer.parseInt(listsizeStr);
            } 
            catch (NumberFormatException e) 
            {
            }
            if(temp != -1) {
                totalRows = temp;
            }
            
            while ((rs.next()) && (rowCount < totalRows))
            {
                Map<String,String> row = new HashMap<String,String>();
                row.put("count", "" + rs.getInt("itemcount"));
                String col = rs.getString(groupColumn);
                int maxColLen = 35;
                if (col != null)
                {

                    if (col.length() > maxColLen)
                    {
                        col = col.substring(0, maxColLen);
                    }
                }

                row.put("groupColumn", col);
                row.put("min", ""
                        + floatFormatter(rs.getFloat("amin") / denominator));
                row.put("avg", ""
                        + floatFormatter(rs.getFloat("aavg") / denominator));
                row.put("max", ""
                        + floatFormatter(rs.getFloat("amax") / denominator));
                as.addRow(row);
                rowCount++;
            }

        } 
        catch (SQLException e)
        {
            throw new InvalidCriteriaException(e.toString());
        }
        finally 
        {
            if(rs != null) 
            {
                try 
                {
                    rs.close();
                }
                catch (Exception ignore) 
                {
                }
            }
            if(pstmt != null) 
            {
                try 
                {
                    pstmt.close();
                }
                catch (Exception ignore) 
                {
                }
            }
            if(con != null) 
            {
                try 
                {
                    con.close();
                }
                catch (Exception e) 
                {
                    logger.error("error releasing the connection",e);
                }
            }
        }

        return as;
    }

    protected String floatFormatter(float f)
    {
        // for now we'll just truncate as int
        int f2 = new Float(f).intValue();
        return Integer.toString(f2);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.statistics.PortalStatistics#getListOfLoggedInUsers()
     */
    public List<Map<String,UserStats>> getListOfLoggedInUsers()
    {
        List<Map<String,UserStats>> list = new ArrayList<Map<String,UserStats>>();

        synchronized (currentUsers)
        {
            list.addAll(currentUsers.values());
        }
        return list;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.statistics.PortalStatistics#getNumberOfLoggedInUsers()
     */
    public int getNumberOfLoggedInUsers()
    {
        return this.currentUserCount;
    }
    

    /**
     * @see org.apache.jetspeed.statistics.PortalStatistics#forceFlush()
     */
    public void forceFlush()
    {
        if (pageBatch != null)
        {
            this.pageBatch.flush();
        }
        if (portletBatch != null)
        {
            this.portletBatch.flush();
        }
        if (userBatch != null)
        {
            this.userBatch.flush();
        }
    }
    
}
