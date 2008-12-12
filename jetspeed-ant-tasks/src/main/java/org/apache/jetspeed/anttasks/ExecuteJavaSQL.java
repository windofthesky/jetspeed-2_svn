/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
/**
 * 
 */
package org.apache.jetspeed.anttasks;

import java.io.File;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.taskdefs.SQLExec;

/**
 * @author hajo
 * 
 */
public class ExecuteJavaSQL
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
	    final class TEMPSQL extends SQLExec {
	        public TEMPSQL() 
	        {
		 	    setProject(new Project());
			    getProject().init();
			    super.setTaskType("sql");
			    super.setTaskName("sql");
			    super.target = new Target();
	        }	
	    }

	    boolean autocommit = true;
	    String driver = null;
	    String url = null;
	    String userid = null;
	    String password = null;
	    String source = null;
	    String onError = null;
	    
	    
	    
       if (args == null)
            throw new IllegalArgumentException("ExecuteSQL needs to know what to do - no arguments provided!!! ");

        
        // Parse all the command-line arguments
        for (int n = 0; n < args.length; n++)
        {
        	String argument = args[n].toLowerCase().trim();
        	
			if (argument.startsWith("driver="))	           
			{
				driver = args[n].substring("driver=".length());
			}
			else
				if (argument.startsWith("url="))
				{
					url = args[n].substring("url=".length());
				}
				else
					if (argument.startsWith("userid="))
					{
						userid = args[n].substring("userid=".length());
					}
					else
						if (argument.startsWith("password="))
						{
							password = args[n].substring("password=".length());
						}
						else
							if (argument.startsWith("src=")) 	       
							{
								source = args[n].substring("src=".length());
							}
							else
								if (argument.startsWith("autocommit="))
								{
									String s = args[n].substring("src=".length());
									try
									{
										autocommit = Boolean.valueOf(s).booleanValue();
									}
									catch (Exception e)
									{
										e.printStackTrace();
									}
								}
								else
									if (argument.startsWith("onerror="))
									{
										onError = args[n].substring("onerror=".length());
									}
								    else
							            {
							                throw new IllegalArgumentException("Unknown argument: "
							                        + args[n]);
							            }
        }
		TEMPSQL sql = new TEMPSQL();
		
		sql.setAutocommit(autocommit);
		sql.setDriver(driver);
		sql.setUrl(url);
		sql.setUserid(userid);
		sql.setPassword(password);
		File sqlFile = null;
		try
		{
			sqlFile = new File(source); 
		}
		catch (Exception e)
		{
			 throw new IllegalArgumentException("File parameter " + source + " invalid : " + e.getLocalizedMessage());
		}
		sql.setSrc(sqlFile);
		try
		{
			SQLExec.OnError errorHandling = new SQLExec.OnError();
			errorHandling.setValue(onError);
			sql.setOnerror(errorHandling);
		}
		catch (Exception e)
		{
			 throw new IllegalArgumentException("Error handling parameter " + onError + " invalid : " + e.getLocalizedMessage());
		}
			

		
		sql.execute();

	}
}
