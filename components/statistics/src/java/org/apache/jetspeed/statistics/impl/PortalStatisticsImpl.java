/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.rdbms.ojb.ConnectionRepositoryEntry;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.statistics.AggregateStatistics;
import org.apache.jetspeed.statistics.InvalidCriteriaException;
import org.apache.jetspeed.statistics.PortalStatistics;
import org.apache.jetspeed.statistics.StatisticsQueryCriteria;
import org.apache.jetspeed.statistics.UserStats;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * <p>
 * PortalStatisticsImpl
 * </p>
 * 
 * @author <a href="mailto:chris@bluesunrise.com">Chris Schaefer </a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id: TestPortletEntityDAO.java,v 1.3 2005/05/24 14:43:19 ate Exp $
 */
public class PortalStatisticsImpl extends PersistenceBrokerDaoSupport implements
        PortalStatistics
{

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

    /* CLF logger */
    protected final static Log logger = LogFactory
            .getLog(PortalStatisticsImpl.class);

    /* batch of portlet statistics */
    private BatchedStatistics portletBatch;

    /* batch if page statistics */
    private BatchedStatistics pageBatch;

    /* batch of user statistics */
    private BatchedStatistics userBatch;

    /* format string for a portlet access log entry */
    protected static final String portletLogFormat = "{0} {1} {2} [{3}] \"{4} {5} {6}\" {7} {8}";

    /* format string for a page access log entry */
    protected static final String pageLogFormat = "{0} {1} {2} [{3}] \"{4} {5}\" {6} {7}";

    /* Format string for a User Logout log entry */
    protected static final String logoutLogFormat = "{0} {1} {2} [{3}] \"{4}\" {5} {6}";

    private static final int STATUS_LOGGED_IN = 1;

    private static final int STATUS_LOGGED_OUT = 2;

    /* the following fields should be settable with Spring injection */
    private boolean logToCLF = true;

    private boolean logToDatabase = true;

    private int maxRecordToFlush_Portlet = 30;

    private int maxRecordToFlush_User = 30;

    private int maxRecordToFlush_Page = 30;

    private long maxTimeMsToFlush_Portlet = 10 * 1000;

    private long maxTimeMsToFlush_User = 10 * 1000;

    private long maxTimeMsToFlush_Page = 10 * 1000;

    ConnectionRepositoryEntry jetspeedDSEntry;

    /* after this is NOT for injection */

    DataSource ds;

    private int currentUserCount = 0;

    private Map currentUsers;

    /* date formatter */
    protected SimpleDateFormat formatter = null;

    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public PortalStatisticsImpl()
    {
    }
    
    public PortalStatisticsImpl(boolean logToCLF, boolean logToDatabase,
            int maxRecordToFlush_Portal, int maxRecordToFlush_User,
            int maxRecordToFlush_Page, long maxTimeMsToFlush_Portal,
            long maxTimeMsToFlush_User, long maxTimeMsToFlush_Page,
            ConnectionRepositoryEntry jetspeedDSEntry)
    {

        this.logToCLF = logToCLF;
        this.logToDatabase = logToDatabase;
        this.maxRecordToFlush_Portlet = maxRecordToFlush_Portal;
        this.maxRecordToFlush_User = maxRecordToFlush_User;
        this.maxRecordToFlush_Page = maxRecordToFlush_Page;
        this.maxTimeMsToFlush_Portlet = maxTimeMsToFlush_Portal;
        this.maxTimeMsToFlush_User = maxTimeMsToFlush_User;
        this.maxTimeMsToFlush_Page = maxTimeMsToFlush_Page;
        this.jetspeedDSEntry = jetspeedDSEntry;
        currentUsers = new TreeMap();
    }

    public void springInit() throws NamingException
    {
        formatter = new SimpleDateFormat("dd/MM/yyyy:hh:mm:ss z");

        if (jetspeedDSEntry != null)
        {
            if (jetspeedDSEntry.getJndiName() != null)
            {
                try
                {
                    Context initialContext = new InitialContext();
                    ds = (DataSource) initialContext.lookup(jetspeedDSEntry
                            .getJndiName());
                } catch (NamingException e)
                {
                    e.printStackTrace();
                    throw e;
                }
            } else
            {
                BasicDataSource bds = new BasicDataSource();
                bds.setDriverClassName(jetspeedDSEntry.getDriverClassName());
                bds.setUrl(jetspeedDSEntry.getUrl());
                bds.setUsername(jetspeedDSEntry.getUsername());
                bds.setPassword(jetspeedDSEntry.getPassword());
                ds = (DataSource) bds;
            }
        }
        currentUserCount = 0;
    }

    public DataSource getDataSource()
    {
        return ds;
    }

    public void logPortletAccess(RequestContext request, String portletName,
            String statusCode, long msElapsedTime)
    {

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
                portletBatch = new BatchedPortletStatistics(ds,
                        this.maxRecordToFlush_Portlet,
                        this.maxTimeMsToFlush_Portlet, "portletLogBatcher");
            }
            portletBatch.addStatistic(record);

        }
        if (record instanceof PageLogRecord)
        {
            if (pageBatch == null)
            {
                pageBatch = new BatchedPageStatistics(ds,
                        this.maxRecordToFlush_Page, this.maxTimeMsToFlush_Page,
                        "pageLogBatcher");
            }
            pageBatch.addStatistic(record);

        }
        if (record instanceof UserLogRecord)
        {
            if (userBatch == null)
            {
                userBatch = new BatchedUserStatistics(ds,
                        this.maxRecordToFlush_User, this.maxTimeMsToFlush_User,
                        "userLogBatcher");
            }
            userBatch.addStatistic(record);

        }
    }

    protected void saveAccessToCLF(LogRecord record)
    {
        Object[] args =
        { ""};
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
        try
        {

            if (userName == null)
            {
                userName = "guest";
            }

            if (!"guest".equals(userName))
            {
                currentUserCount = currentUserCount - 1;

                synchronized (currentUsers)
                {
                    UserStats userStats = (UserStats) currentUsers
                            .get(userName);
                    if (userStats == null)
                    {
                        userStats = new UserStatsImpl();
                        userStats.setNumberOfSession(0);
                        userStats.setUsername(userName);
                        currentUsers.put(userName, userStats);
                    }
                    userStats.setNumberOfSession(userStats
                            .getNumberOfSessions() - 1);
                    if (userStats.getNumberOfSessions() <= 0)
                    {
                        currentUsers.remove(userName);
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
        try
        {

            HttpServletRequest req = request.getRequest();
            Principal principal = req.getUserPrincipal();
            String userName = (principal != null) ? principal.getName()
                    : "guest";
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            UserLogRecord record = new UserLogRecord();

            if (!"guest".equals(userName))
            {
                currentUserCount = currentUserCount + 1;

                synchronized (currentUsers)
                {
                    UserStats userStats = (UserStats) currentUsers
                            .get(userName);
                    if (userStats == null)
                    {
                        userStats = new UserStatsImpl();
                        userStats.setNumberOfSession(0);
                        userStats.setUsername(userName);
                        currentUsers.put(userName, userStats);
                    }
                    userStats.setNumberOfSession(userStats
                            .getNumberOfSessions() + 1);
                }
            }

            record.setUserName(userName);
            record.setIpAddress(req.getRemoteAddr());
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
        long end = System.currentTimeMillis();
        // new we're done

    }

    /**
     * @see org.apache.jetspeed.statistics.PortalStatistics#getNumberOfCurrentUsers()
     */
    public int getNumberOfCurrentUsers()
    {
        return currentUserCount;
    }

    private Date getStartDateFromPeriod(String period, Date end)
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

    /**
     * @see org.apache.jetspeed.statistics.PortalStatistics#queryStatistics(org.apache.jetspeed.statistics.StatisticsQueryCriteria)
     */
    public AggregateStatistics queryStatistics(StatisticsQueryCriteria criteria)
            throws InvalidCriteriaException
    {
        AggregateStatistics as = new AggregateStatisticsImpl();
        String query;
        String query2;

        String tableName;
        String groupColumn;

        Date end = new Date();
        Date start = getStartDateFromPeriod(criteria.getTimePeriod(), end);

        String queryType = criteria.getQueryType();
        String listsizeStr = criteria.getListsize();
        int listsize = 5;
        try
        {
            listsize = Integer.parseInt(listsizeStr);
        } catch (NumberFormatException e1)
        {
        }

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
        String orderColumn = "int_count";

        String ascDesc = "DESC";

        if (!PortalStatistics.QUERY_TYPE_USER.equals(queryType))
        {
            query = "select count(*) as int_count, MIN(ELAPSED_TIME) as min_elapsed_time, "
                    + "AVG(ELAPSED_TIME) as avg_elapsed_time, MAX(ELAPSED_TIME) as max_elapsed_time from "
                    + tableName + " where time_stamp > ? and time_stamp < ?";
            query2 = "select count(*) as int_count ,"
                    + groupColumn
                    + ", MIN(ELAPSED_TIME) as min_elapsed_time, AVG(ELAPSED_TIME) as avg_elapsed_time"
                    + ", MAX(ELAPSED_TIME) as max_elapsed_time "
                    + "from " + tableName
                    + " where time_stamp > ? and time_stamp < ? group by "
                    + groupColumn + "  order by " + orderColumn + " " + ascDesc
                    + " limit " + listsize;
        } else
        {
            query = "select count(*) as int_count, MIN(ELAPSED_TIME) as min_elapsed_time, "
                    + "AVG(ELAPSED_TIME) as avg_elapsed_time, MAX(ELAPSED_TIME) as max_elapsed_time from "
                    + tableName
                    + " where time_stamp > ? and time_stamp < ? and status = 2";
            query2 = "select count(*) as int_count ,"
                    + groupColumn
                    + ", MIN(ELAPSED_TIME) as min_elapsed_time, AVG(ELAPSED_TIME) as avg_elapsed_time"
                    + ", MAX(ELAPSED_TIME) as max_elapsed_time "
                    + "from "
                    + tableName
                    + " where time_stamp > ? and time_stamp < ? and status = 2 group by "
                    + groupColumn + "  order by " + orderColumn + " " + ascDesc
                    + " limit " + listsize;
        }
        try
        {
            Connection con = ds.getConnection();
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setTimestamp(1, new Timestamp(start.getTime()));
            pstmt.setTimestamp(2, new Timestamp(end.getTime()));
            ResultSet rs = pstmt.executeQuery();
            float denominator = 1.0f;
            if (PortalStatistics.QUERY_TYPE_USER.equals(queryType))
            {
                denominator = 1000f * 60f; // this should convert from mS to
                                           // minutes
            }
            if (rs.next())
            {
                as.setHitCount(rs.getInt("int_count"));

                as.setMinProcessingTime(rs.getFloat("min_elapsed_time")
                        / denominator);
                as.setAvgProcessingTime(rs.getFloat("avg_elapsed_time")
                        / denominator);
                as.setMaxProcessingTime(rs.getFloat("max_elapsed_time")
                        / denominator);

            }
            PreparedStatement pstmt2 = con.prepareStatement(query2);
            pstmt2.setTimestamp(1, new Timestamp(start.getTime()));
            pstmt2.setTimestamp(2, new Timestamp(end.getTime()));
            ResultSet rs2 = pstmt2.executeQuery();

            while (rs2.next())
            {
                Map row = new HashMap();
                row.put("count", "" + rs2.getInt("int_count"));
                String col = rs2.getString(groupColumn);
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
                        + floatFormatter(rs2.getFloat("min_elapsed_time") / denominator));
                row.put("avg", ""
                        + floatFormatter(rs2.getFloat("avg_elapsed_time") / denominator));
                row.put("max", ""
                        + floatFormatter(rs2.getFloat("max_elapsed_time") / denominator));
                as.addRow(row);
            }

        } catch (SQLException e)
        {
            throw new InvalidCriteriaException(e.toString());
        }

        return as;
    }

    private String floatFormatter(float f)
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
    public List getListOfLoggedInUsers()
    {
        List list = new ArrayList();

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
}
