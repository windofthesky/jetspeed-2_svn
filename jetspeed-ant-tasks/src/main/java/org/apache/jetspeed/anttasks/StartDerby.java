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
import java.sql.Driver;
import java.sql.Connection;
import org.apache.tools.ant.Project;
import java.util.Hashtable;


public class StartDerby 
  extends Task {
 
//	public static Driver DERBY_DRIVER = null;
//	public static Connection  DERBY_CONNECTION = null;

  public void execute() 
    throws BuildException {
      try {
  	   	if (StartDerby.getDriver(getProject()) != null)
       	{
        		System.out.println("Derby Driver has ALREADY BEEN ESTABLISHED!");
        		return;
       	}
  	  StartDerby.setDriver(getProject(),(java.sql.Driver)(Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance()));
        System.out.println("Derby Driver has been started!");
      } catch (Exception e) {
        System.out.println(
          "Derby could not start. This is most likely " +
          "due to missing Derby JAR files. Please check your classpath" +
          "and try again.");
        throw new BuildException(e);
      }
  }
  
  public static void setDriver(Project project, Driver d)
  {
	  try
	  {
		  if (d != null)
		  {
			  project.addReference("DRIVER",d);
			  System.out.println("Driver reference in Project set ");
		  }
		  else
		  {
			  Hashtable h = project.getReferences();
			  h.remove("DRIVER");
			  System.out.println("Driver reference in Project removed ");
		  }
	  }
	  catch (Exception e)
	  {
		  System.out.println("Could't SET Driver reference in Project : " + e.getLocalizedMessage());
		  
	  }
  }
  public static Driver getDriver(Project project)
  {
	  try
	  {
		  Object o = project.getReference("DRIVER");
		  if (o != null)
			  return (Driver)o;
		  else return null;
	  }
	  catch (Exception e)
	  {
		  System.out.println("Could't get Driver reference in Project : " + e.getLocalizedMessage());
		  return null;
	  }
  }
  
  public static void setConnection(Project project, Connection d)
  {
	  try
	  {
		  if (d != null)
		  {
			  project.addReference("Connection",d);
			  System.out.println("Connection reference in Project set ");
		  }
		  else
		  {
			  Hashtable h = project.getReferences();
			  h.remove("Connection");
			  System.out.println("Connection reference in Project removed ");
		  }
	  }
	  catch (Exception e)
	  {
		  System.out.println("Could't SET Connection reference in Project : " + e.getLocalizedMessage());
		  
	  }
  }
  public static Connection getConnection(Project project)
  {
	  try
	  {
		  Object o = project.getReference("Connection");
		  if (o != null)
			  return (Connection)o;
		  else return null;
	  }
	  catch (Exception e)
	  {
		  System.out.println("Could't get Connection reference in Project : " + e.getLocalizedMessage());
		  return null;
	  }
  }
}
