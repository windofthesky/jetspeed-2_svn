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

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
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
import javax.naming.NamingException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.sql.DataSource;

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
                con = getConnection();
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
     * Connection Management: TODO: rethink this, current impl is a quick prototype
     */
    
    private boolean driverRegistered = false;
    
    public Connection getConnection()
    {
        
        Connection con = null;
        try 
        {
            if (driverRegistered == false)
            {
                Class driverClass = Class.forName("com.mysql.jdbc.Driver");
                //Class driverClass = Class.forName("org.hsqldb.jdbcDriver");
                Driver driver = (Driver)driverClass.newInstance();
                DriverManager.registerDriver(driver);
                driverRegistered = true;
            }
            //con = DriverManager.getConnection("jdbc:mysql://192.168.2.55/GWLogDB", "david", "david");
            //con = DriverManager.getConnection("jdbc:hsqldb:hsql://127.0.0.1:9001", "sa", "");
            con = DriverManager.getConnection("jdbc:mysql://j2-server/j2", "j2", "digital");
            
            Context ctx = new InitialContext();
            DataSource ds = (DataSource)ctx.lookup("java:/jdbc/jetspeed");            
            System.out.println("Got DataSource: " + ds);
            
        }
        catch (NamingException ne)
        {
            System.err.println("error getting datas source " + ne);
            log.error("Cant get jetspeed data source", ne);                                   
        }
        catch (ClassNotFoundException cnfe) 
        {
            log.error("Cant get class for JDBC driver", cnfe);            
        }
        catch (InstantiationException ie) 
        {
            log.error("Cant instantiate class for JDBC driver", ie);            
        }
        catch (IllegalAccessException iae) 
        {
            log.error("Illegal Access for JDBC driver", iae);            
        }        
        catch (SQLException se) 
        {
            log.error("Cant get connection", se);
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
}
