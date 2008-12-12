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
package org.apache.jetspeed.anttasks;


import java.sql.Connection;
import java.sql.SQLException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.SQLExec;
import java.util.Properties;
import org.apache.tools.ant.Project;;

public class ExecuteSQL extends SQLExec
{  

  
	public  void execute() throws BuildException 
	{
		
	
		try
		{
			System.out.println("Executing SQL statement");
			super.execute();
		}
		catch (Exception e)
		{
			throw new BuildException(e, getLocation());
		}
		finally
		{
			if (StartDerby.getConnection(getProject()) != null)
			{
				try
				{
					if (!(StartDerby.getConnection(getProject()).isClosed()))
						StartDerby.getConnection(getProject()).close();
				}
				catch (Exception e1)
				{
					// just a safetyguard
				}
				StartDerby.setConnection(getProject(),null);
			}
		}
	}
		

  /**
     * Creates a new Connection as using the driver, url, userid and password
     * specified.
     *
     * The calling method is responsible for closing the connection.
     *
     * @return Connection the newly created connection.
     * @throws BuildException if the UserId/Password/Url is not set or there
     * is no suitable driver or the driver fails to load.
     */
  protected Connection getConnection() throws BuildException 
  {
	 	if (StartDerby.getDriver(getProject()) == null)
	 		throw new BuildException("Derby driver not established!", getLocation());		
  	// reuse excisting connection:
  	if (StartDerby.getConnection(getProject())!= null)
  	{
  		System.out.println("Connection already established");
  		return StartDerby.getConnection(getProject());
  	}
  	// do almost the same as in the orignial JDBC tasks:
  	
        if (getUrl() == null) {
            throw new BuildException("Url attribute must be set!", getLocation());
        }
        try {

            log("connecting to " + getUrl(), Project.MSG_VERBOSE);
            Properties info = new Properties();
            if (getUserId() != null)
            	info.put("user", getUserId());
            if (getPassword() != null)
            	info.put("password", getPassword());
            
            Connection conn = StartDerby.getDriver(getProject()).connect(getUrl(), info);
            if (conn == null) {
                // Driver doesn't understand the URL
                throw new SQLException("No suitable Driver for " + getUrl());
            }

            conn.setAutoCommit(isAutocommit());
      		System.out.println("Connection to " + getUrl() + " established");
      		StartDerby.setConnection(getProject(),conn);
            return conn;
        } catch (SQLException e) {
            throw new BuildException(e, getLocation());
        }

    }
  
  
}
