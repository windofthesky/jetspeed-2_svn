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
package org.apache.jetspeed.portlets.perl;

/**
* PerlParameters
* Class holding information about the perl script to execute.This class is typically attached to a PortletAction (as an attribute).
*
* @author <a href="mailto:rogerrut@apache.org">Roger Ruttimann</a>
* @version $Id$
*/

public class PerlParameters {
	private String requestMethod = "GET";
	private String queryString = null;
	private String perlScript = null;
	private String displayMessage = null;
	

	// Getters and setters
	public void setRequestMethod(String requestMethod)
	{
		this.requestMethod = requestMethod;
	}
	
	public String getRequestMethod()
	{
		return this.requestMethod;
	}
	
	public void addQueryArgument(String query)
	{
		if (queryString == null)
		{
			queryString = query;
		}
		else
		{
			queryString += '&';
			queryString += query;
		}	
	}
	
	public String getQueryString()
	{
		return this.queryString;
	}
	
	public void setPerlScript(String script)
	{
		this.perlScript = script;
	}
	
	public String getPerlScript()
	{
		return this.perlScript;
	}
	
	public void setDisplayMessage(String msg)
	{
		this.displayMessage = msg;
	}
	
	public String getDisplayMessage()
	{
		return this.displayMessage;
	}
}