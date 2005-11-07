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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.rdbms.ojb.ConnectionRepositoryEntry;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.statistics.AggregateStatistics;
import org.apache.jetspeed.statistics.InvalidCriteriaException;
import org.apache.jetspeed.statistics.PortalStatistics;
import org.apache.jetspeed.statistics.StatisticsQueryCriteria;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * <p>
 * PortalStatisticsImpl
 * </p>
 * 
 * @author <a href="mailto:chris@bluesunrise.com">Chris Schaefer</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
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

    private int currentUsers = 0;

    /* date formatter */
    protected SimpleDateFormat formatter = null;

    /**
     * <p>
     * Default Constructor.
     * </p>
     */
    public PortalStatisticsImpl()
    {
    }
    
    public PortalStatisticsImpl(
             boolean logToCLF,
             boolean logToDatabase,
             int maxRecordToFlush_Portal,
             int maxRecordToFlush_User,
             int maxRecordToFlush_Page,
             long maxTimeMsToFlush_Portal,
             long maxTimeMsToFlush_User,
             long maxTimeMsToFlush_Page,
            ConnectionRepositoryEntry jetspeedDSEntry
            )
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
        
    }

    public void springInit() throws NamingException
    {
        formatter = new SimpleDateFormat("dd/MM/yyyy:hh:mm:ss z");

        if (jetspeedDSEntry != null )
        {
            if (jetspeedDSEntry.getJndiName() != null) {
                try
                {
                    Context initialContext = new InitialContext();
                    ds = (DataSource) initialContext.lookup(jetspeedDSEntry.getJndiName());
                } catch (NamingException e)
                {
                    e.printStackTrace();
                    throw e;
                }
            }
            else
            {
                BasicDataSource bds = new BasicDataSource();
                bds.setDriverClassName(jetspeedDSEntry.getDriverClassName());
                bds.setUrl(jetspeedDSEntry.getUrl());
                bds.setUsername(jetspeedDSEntry.getUsername());
                bds.setPassword(jetspeedDSEntry.getPassword());
                ds = (DataSource) bds;
            }
        } 
        
        
        currentUsers = 0;

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
            record.setIpAddress(req.getRemoteAddr());
            record.setPagePath(req.getPathInfo());
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
                portletBatch = new BatchedPortletStatistics(ds, this.maxRecordToFlush_Portlet, this.maxTimeMsToFlush_Portlet,
                        "portletLogBatcher");
            }
            portletBatch.addStatistic(record);

        }
        if (record instanceof PageLogRecord)
        {
            if (pageBatch == null)
            {
                pageBatch = new BatchedPageStatistics(ds, this.maxRecordToFlush_Page, this.maxTimeMsToFlush_Page,
                        "pageLogBatcher");
            }
            pageBatch.addStatistic(record);

        }
        if (record instanceof UserLogRecord)
        {
            if (userBatch == null)
            {
                userBatch = new BatchedUserStatistics(ds, this.maxRecordToFlush_User, this.maxTimeMsToFlush_User,
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
            record.setPagePath(req.getPathInfo());
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

    public void logUserLogout(RequestContext request, long msElapsedTime)
    {
        try
        {
            currentUsers = currentUsers - 1;

            HttpServletRequest req = request.getRequest();
            Principal principal = req.getUserPrincipal();
            String userName = (principal != null) ? principal.getName()
                    : "guest";
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            UserLogRecord record = new UserLogRecord();

            record.setUserName(userName);
            record.setIpAddress(req.getRemoteAddr());
            record.setStatus(STATUS_LOGGED_OUT);
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
            currentUsers = currentUsers + 1;

            HttpServletRequest req = request.getRequest();
            Principal principal = req.getUserPrincipal();
            String userName = (principal != null) ? principal.getName()
                    : "guest";
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            UserLogRecord record = new UserLogRecord();

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
            synchronized(portletBatch.thread) 
            {
                portletBatch.thread.notify();
            }
            
        }
        if (userBatch != null)
        {
            userBatch.tellThreadToStop();
            synchronized(userBatch.thread) 
            {
                userBatch.thread.notify();
            }
        }
        if (pageBatch != null)
        {
            pageBatch.tellThreadToStop();
            synchronized(pageBatch.thread) 
            {
                pageBatch.thread.notify();
            }
        }

        if ((this.currentUsers != 0) && logger.isDebugEnabled())
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
            } 
            catch (InterruptedException ie)
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
        return currentUsers;
    }

    private Date getStartDateFromPeriod(String period,Date end) 
    {
        GregorianCalendar gcEnd = new GregorianCalendar();
        gcEnd.setTime(end);
        if(period != null) 
        {
            if(period.endsWith("m")) 
            {
                // months
                String p = period.substring(0,period.length()-1);                
                int ret = Integer.parseInt(p);
                gcEnd.add(Calendar.MONTH,(ret * -1));
            } 
            else if(period.endsWith("h")) 
            {
                // hours
                String p = period.substring(0,period.length()-1);
                int ret = Integer.parseInt(p);
                gcEnd.add(Calendar.HOUR,(ret * -1));
            } 
            else if(period.equals("all"))
            {
                gcEnd = new GregorianCalendar();
                gcEnd.set(1968,07,15);
            } 
            else 
            {
                // minutes
                int ret = Integer.parseInt(period);
                gcEnd.add(Calendar.MINUTE,(ret * -1));
            }
        } 
        else 
        {
            gcEnd = new GregorianCalendar();
            gcEnd.set(1968,07,15);
        
        }
        return gcEnd.getTime();
    }
    
    /**
     * @see org.apache.jetspeed.statistics.PortalStatistics#queryStatistics(org.apache.jetspeed.statistics.StatisticsQueryCriteria)
     */
    public AggregateStatistics queryStatistics(StatisticsQueryCriteria criteria) throws InvalidCriteriaException
    {
        AggregateStatistics as = new AggregateStatisticsImpl();
        String query;
        String query2;
        
        String tableName;
        String groupColumn;
        
        Date end = new Date();
        Date start = getStartDateFromPeriod(criteria.getTimePeriod(),end);
        
        
        String queryType = criteria.getQueryType();
        if ("user".equals(queryType))
        {
            tableName = "USER_STATISTICS";
            groupColumn = "USER_NAME";
        }
        else if ("portlet".equals(queryType))
        {
            tableName = "PORTLET_STATISTICS";
            groupColumn = "PORTLET";
        }
        else if ("page".equals(queryType))
        {
            tableName = "PAGE_STATISTICS";
            groupColumn = "PAGE";
        }
        else 
        {
            throw new InvalidCriteriaException(" invalid queryType passed to queryStatistics");
        }
        String orderColumn = "count";
        
        //String orderColumn = "MAX(ELAPSSE_TIME)";
        //String orderColumn = "AVG(ELAPSED_TIME)";
        //String orderColumn = "MIN(ELAPSED_TIME)";
        
        String ascDesc = "DESC";
        
        query= "select count(*) as count , STDDEV(ELAPSED_TIME),MIN(ELAPSED_TIME),AVG(ELAPSED_TIME),MAX(ELAPSED_TIME) from "+tableName+" where time_stamp > ? and time_stamp < ?";
        query2= "select count(*) as count ,"+groupColumn+", MIN(ELAPSED_TIME) as min ,AVG(ELAPSED_TIME) as avg ,MAX(ELAPSED_TIME) as max "
                    +"from "+tableName+" where time_stamp > ? and time_stamp < ? group by "+groupColumn+"  order by "+orderColumn+" "+ascDesc+" limit 5";
        try
        {
            Connection con = ds.getConnection();
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setTimestamp(1,new Timestamp(start.getTime()));
            pstmt.setTimestamp(2,new Timestamp(end.getTime()));
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) 
            {
                as.setHitCount(rs.getInt("count"));
                as.setStdDevProcessingTime(rs.getFloat("STDDEV(ELAPSED_TIME)"));
                as.setMinProcessingTime(rs.getFloat("MIN(ELAPSED_TIME)"));
                as.setAvgProcessingTime(rs.getFloat("AVG(ELAPSED_TIME)"));
                as.setMaxProcessingTime(rs.getFloat("MAX(ELAPSED_TIME)"));
            }
            PreparedStatement pstmt2 = con.prepareStatement(query2);
            pstmt2.setTimestamp(1,new Timestamp(start.getTime()));
            pstmt2.setTimestamp(2,new Timestamp(end.getTime()));
            ResultSet rs2 = pstmt2.executeQuery();
            while(rs2.next()) 
            {
                Map row = new HashMap();
                row.put("count",""+rs2.getInt("count"));
                row.put("groupColumn",rs2.getString(groupColumn));
                row.put("min",""+rs2.getFloat("min"));
                row.put("avg",""+rs2.getFloat("avg"));
                row.put("max",""+rs2.getFloat("max"));
                as.addRow(row);
                //as.setHitCount(rs.getInt("count"));
                //as.setStdDevProcessingTime(rs.getFloat("STDDEV(ELAPSED_TIME)"));
                //as.setMinProcessingTime(rs.getFloat("MIN(ELAPSED_TIME)"));
                //as.setAvgProcessingTime(rs.getFloat("AVG(ELAPSED_TIME)"));
                //as.setMaxProcessingTime(rs.getFloat("MAX(ELAPSED_TIME)"));
            }
        
        } 
        catch (SQLException e)
        {
            throw new InvalidCriteriaException(e.toString());
        }
        
        return as;
    }
}
