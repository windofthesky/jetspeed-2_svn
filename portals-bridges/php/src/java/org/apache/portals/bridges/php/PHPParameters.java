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

package org.apache.portals.bridges.php;

import org.apache.portals.bridges.common.ScriptRuntimeData;

/**
* PHPParameters
* Class holding information about the PHP scripts to execute.This class is typically attached to a PortletAction (as an attribute).
*
* @author <a href="mailto:rogerrut@apache.org">Roger Ruttimann</a>
* @version $Id$
*/
public class PHPParameters extends ScriptRuntimeData {

	/** 
	 * Action Parameter for PHP  requests
	 */   
    public static  final String ACTION_PARAMETER_PHP = "_PHP";
    
    /**
     * Session variable for PHP Parameters
     */
    public static  final String PHP_PARAMETER = "PHPParameter";
    
	/**
	 * 
	 */
	public PHPParameters() {
		super();
		
		setSessionParameterName(PHP_PARAMETER);
	}

}
