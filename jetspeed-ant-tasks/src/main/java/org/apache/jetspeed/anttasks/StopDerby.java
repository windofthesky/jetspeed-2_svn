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
import org.apache.tools.ant.Task;
import java.sql.SQLException;
import java.util.Properties;


public class StopDerby 
  extends Task {
  

  public void execute() 
    throws BuildException 
    {
	  	if (StartDerby.getDriver(getProject()) == null)
		{
			System.out.println("Derby Driver has NOT BEEN ESTABLISHED!");
			return ; // already closed;
		}
		if (StartDerby.getConnection(getProject()) != null)
		{
			try
			{
				StartDerby.getConnection(getProject()).close();
				System.out.println("Derby Connection successfully closed!");
			}
			catch (SQLException e)
			{
				throw new BuildException(e, getLocation());
			}
			StartDerby.setConnection(getProject(),null);
			
		}
		else
    		System.out.println("Derby Connection has already been closed!");
    	
//    	getConnection("jdbc:derby:;shutdown=true");
        Properties info = new Properties();
      	info.put("shutdown",Boolean.TRUE);
        System.out.println("Derby Driver sutting down!");
        try
        {
        	StartDerby.getDriver(getProject()).connect("jdbc:derby:",info);
    	} catch (Exception e) 
    	{
    		System.out.println("Derby has been shutdown!");
        }

    	StartDerby.setDriver(getProject(),null);
        System.out.println("Derby Driver has been shutdown!");
     }
  
}
