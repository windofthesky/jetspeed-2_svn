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
package org.apache.portals.bridges.perl;

import org.apache.portals.bridges.common.ScriptRuntimeData;

/**
* PerlParameters
* Class holding information about the perl script to execute.This class is typically attached to a PortletAction (as an attribute).
*
* @author <a href="mailto:rogerrut@apache.org">Roger Ruttimann</a>
* @version $Id$
*/

public class PerlParameters extends ScriptRuntimeData{
	
	/** 
	 * Action Parameter for Perl requests
	 */   
    public static  final String ACTION_PARAMETER_PERL = "_PERL";
    
    /**
     * Session variable for Perl Parameters
     */
    public static  final String PERL_PARAMETER = "PerlParameter";
	
	private String requestMethod = "GET";
	private String queryString = null;
	private String perlScript = null;
	private String displayMessage = null;
	
	// Constructor
	public  PerlParameters() {
		setSessionParameterName(PERL_PARAMETER);
	}
	
	// Getters and setters
	public void setRequestMethod(String requestMethod)
	{
		this.requestMethod = requestMethod;
	}
	
	public String getRequestMethod()
	{
		return this.requestMethod;
	}
	
}