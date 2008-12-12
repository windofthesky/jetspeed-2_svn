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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.JDBCTask;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.tools.ant.Project;

public class StartDatabase
  extends JDBCTask  {

  
  
  
  public void execute() 
    throws BuildException 
    {
	    if (StartDerby.getDriver(getProject()) == null)
	    	 throw new BuildException("Derby driver not established!", getLocation());
	  	// reuse excisting connection:
	  	if (StartDerby.getConnection(getProject()) != null)
	  	{
			System.out.println("Connection already established");
			return;
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
	                throw new SQLException("No suitable Driver for " +  getUrl());
	            }

	            conn.setAutoCommit(isAutocommit());
	            StartDerby.setConnection(getProject(),conn);
	            System.out.println("Derby connected to " + getUrl());
	            return;
	        } catch (SQLException e) {
	            throw new BuildException(e, getLocation());
	        }
 }
  
}
