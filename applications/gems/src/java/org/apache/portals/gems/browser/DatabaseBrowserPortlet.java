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
package org.apache.portals.gems.browser;

import java.io.IOException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.security.auth.Subject;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.jetspeed.sso.SSOContext;
import org.apache.jetspeed.sso.SSOException;
import org.apache.portals.bridges.util.PreferencesHelper;
import org.apache.portals.gems.util.StatusMessage;
import org.apache.portals.messaging.PortletMessaging;

/**
 * DatabaseBrowserPortlet
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */
public class DatabaseBrowserPortlet 
    extends BrowserPortlet
    implements Browser
{    
    /**
     * Execute the sql statement as specified by the user or the default, and
     * store the resultSet in a vector.
     * 
     * @param sql
     *            The sql statement to be executed.
     * @param data
     *            The turbine rundata context for this request.
     */
    public void getRows(RenderRequest request, String sql, int windowSize)
            throws Exception
    {
        List resultSetList = new ArrayList();
        List resultSetTitleList = new ArrayList();
        List resultSetTypeList = new ArrayList();
        
        Connection con = null;
        PreparedStatement selectStmt = null;
        ResultSet rs = null;
        
        PortletSession session = request.getPortletSession();
        try
        {
            String poolname = getPreference(request, POOLNAME, null);
            if (poolname == null || poolname.length() == 0)
            {
                con = getConnection(request);
            } 
            else
            {
                con = getConnection(poolname);
            }
            selectStmt = con.prepareStatement(sql);

            readSqlParameters(request);
            Iterator it = sqlParameters.iterator();
            int ix = 0;
            while (it.hasNext())
            {
                ix++;
                Object object = it.next();
                selectStmt.setObject(ix, object);
            }
            rs = selectStmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnNum = rsmd.getColumnCount();
            /*
             * get the user object types to be displayed and add them to the
             * title list as well as the result set list
             */
            List userObjList = (List) session.getAttribute(USER_OBJECTS);
            int userObjListSize = 0;
            if (userObjList != null)
            {
                userObjListSize = userObjList.size();
            }
            //System.out.println("User List Size = "+ userObjListSize);
            /*
             * the array columnDisplayed maintains a boolean value for each
             * column index. Only the columns that are set to true are added to
             * the resultSetList, resultSetTitleList and resultSetTypeList.
             */
            boolean[] columnDisplayed = new boolean[columnNum + userObjListSize];

            /*
             * this for loop constructs the columnDisplayed array as well as
             * adds to the resultSetTitleList and resultSetTypeList
             */
            for (int i = 1; i <= columnNum; i++)
            {
                int type = rsmd.getColumnType(i);
                if (!((type == Types.BLOB) || (type == Types.CLOB)
                        || (type == Types.BINARY)
                        || (type == Types.LONGVARBINARY) || (type == Types.VARBINARY)))
                {
                    resultSetTitleList.add(rsmd.getColumnName(i));
                    resultSetTypeList.add(String.valueOf(type));
                    columnDisplayed[i - 1] = true;
                } else
                {
                    columnDisplayed[i - 1] = false;
                }
            }

            for (int i = columnNum; i < columnNum + userObjListSize; i++)
            {
                ActionParameter usrObj = (ActionParameter) userObjList.get(i
                        - columnNum);
                resultSetTitleList.add(usrObj.getName());
                resultSetTypeList.add(usrObj.getType());
                columnDisplayed[i] = true;
                //System.out.println("User List Name = "+ usrObj.getName()+"
                // Type = "+usrObj.getType());
            }
            /*
             * this while loop adds each row to the resultSetList
             */
            int index = 0;
            while (rs.next())
            {
                List row = new ArrayList(columnNum);

                for (int i = 1; i <= columnNum; i++)
                {
                    if (columnDisplayed[i - 1])
                    {
                        Object obj = rs.getObject(i);
                        if (obj == null)
                        {
                            obj = VELOCITY_NULL_ENTRY;
                        }
                        row.add(obj);
                    }
                }
                for (int i = columnNum; i < columnNum + userObjListSize; i++)
                {
                    ActionParameter usrObj = (ActionParameter) userObjList
                            .get(i - columnNum);
                    if (columnDisplayed[i])
                    {
                        Class c = Class.forName(usrObj.getType());
                        row.add(c.newInstance());
                        populate(index, i, row);
                    }
                }

                if (filter(row, request))
                {
                    continue;
                }

                resultSetList.add(row);
                index++;
            }
            BrowserIterator iterator = new DatabaseBrowserIterator(
                    resultSetList, resultSetTitleList, resultSetTypeList,
                    windowSize);
            setBrowserIterator(request, iterator);

        } catch (SQLException e)
        {
            throw e;
        } finally
        {
            try
            {
                if (null != selectStmt) selectStmt.close();
                if (null != rs) rs.close();
                if (null != con) //closes con also
                {
                    closeConnection(con);
                }

            } catch (Exception e)
            {
                throw e;
            }
        }

    }
    

    /*
     * Connection Management
     */
        
    public Connection getConnection(PortletRequest request)
    throws Exception 
    {
        Connection con = null;
        try
        {
            PortletPreferences prefs = request.getPreferences();
            String dsType = prefs.getValue("DatasourceType", null);
            if (dsType == null)
            {
                throw new SQLException("No DataSource provided"); 
            }
            if (dsType.equals("jndi"))
            {
                Context ctx = new InitialContext();
                String dsName = prefs.getValue("JndiDatasource", "");
                Context envContext  = (Context)ctx.lookup("java:/comp/env");
                DataSource ds = (DataSource)envContext.lookup(dsName);                
                con = ds.getConnection();
            }
            else if (dsType.equals("dbcp"))
            {
                BasicDataSource ds = new BasicDataSource();
                  ds.setDriverClassName(prefs.getValue("JdbcDriver", ""));
                  ds.setUrl(prefs.getValue("JdbcConnection", ""));                                                                                      
                  ds.setUsername(prefs.getValue("JdbcUsername", ""));
                  ds.setPassword(prefs.getValue("JdbcPassword", ""));
    //            ds.setUrl("jdbc:mysql://j2-server/j2");
                  con = ds.getConnection();                  
            }
            else if (dsType.equals("sso"))
            {
                /*
                 * For SSO the user has to define the JDBCdriver and JdbcConnection (URL)
                 * but the credentials for the db come from the SSO storage
                 */
                BasicDataSource ds = new BasicDataSource();
                ds.setDriverClassName(prefs.getValue("SSOJdbcDriver", ""));
                ds.setUrl(prefs.getValue("SSOJdbcConnection", ""));  
                String ssoURL = prefs.getValue("SSOSite", "");
                
                // SSO API lookup
                SSOContext credentials = null;
                try
                {
                    credentials = sso.getCredentials(getSubject(), ssoURL);
                }
                catch(SSOException ssoex)
                {
                    throw new Exception("SSO credential lookup failed. Error: " + ssoex.getMessage());
                }
                
                String ssoUserName = credentials.getRemotePrincipalName();
                String ssoPWD = credentials.getRemoteCredential();
                ds.setUsername(ssoUserName);
                ds.setPassword( ssoPWD );
                con = ds.getConnection();
            }
            else
            {
                throw new SQLException("No DataSource provided");                 
            }
                        
        }
        catch (Exception e)
        {
            throw new Exception("Failed to connect", e); // TODO: complete this 
        }
        return con;
    }
        
    public Connection getConnection(String poolName)
    {
        return null;
    }
    
    public void closeConnection(Connection con)
    {
        try
        {
            con.close();
        }
        catch (SQLException e) 
        {
            log.error("Cant close connection", e);
        }         
        
    }
    
    public void doEdit(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        response.setContentType("text/html");
        StatusMessage msg = (StatusMessage)PortletMessaging.consume(request, "DatabaseBrowserPortlet", "dbConnectTest");
        if (msg != null)
        {
            this.getContext(request).put("statusMsg", msg);            
        }
        super.doEdit(request, response);
    }
    
    
    public void processAction(ActionRequest request, ActionResponse response)
    throws PortletException, IOException
    {
        if (request.getPortletMode() == PortletMode.EDIT)
        {
            String test = request.getParameter("Test");
            if (test != null && test.equals("Test"))
            {
                try
                {
                    PortletPreferences prefs = request.getPreferences();
                    PreferencesHelper.requestParamsToPreferences(request);
                    prefs.store();                    
                    getConnection(request);
                    StatusMessage msg = new StatusMessage("Connection made successfully.", StatusMessage.SUCCESS);                    
                    PortletMessaging.publish(request, "DatabaseBrowserPortlet", "dbConnectTest", msg);                    
                }
                catch (Exception e)
                {
                    String msg = e.toString();
                    Throwable cause = e.getCause();
                    if (cause != null)
                    {
                        msg = msg + ", " + cause.getMessage();
                    }
                    StatusMessage sm = new StatusMessage(msg, StatusMessage.ERROR);
                    PortletMessaging.publish(request, "DatabaseBrowserPortlet", "dbConnectTest", sm);
                }
                response.setPortletMode(PortletMode.EDIT);
                return;
            }
        }
        super.processAction(request, response);
    }
    
    private Subject getSubject()
    {
        AccessControlContext context = AccessController.getContext();
        return Subject.getSubject(context);         
    }
}
