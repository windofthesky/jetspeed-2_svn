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
package org.apache.jetspeed.cluster;

import java.util.Date;

/**
 * Node Information Interface
 *
 * @author <a href="mailto:hajo@bluesunrise.com">Hajo Birthelmer</a>
 * @version 
 */

public interface NodeInformation
{

	/**
	 * Getter for ContextName
	 * @return
	 */
	public String getContextName();
	/**
	 * setter for context name
	 * 
	 * @param id
	 */	
	public void setContextName(String contextName);

	/**
	 * Getter for ObjectID 
	 * @return
	 */
	public Long getId();

	/**
	 * setter for ObjectID 
	 * 
	 * @param id
	 */	
	public void setId(Long id);
	/**
	 * setter for ObjectID 
	 * 
	 * @param id
	 */	
	public void setId(long id);

	/**
	 * Getter for Last Deploy Date
	 * @return
	 */
	public Date getLastDeployDate();


	/**
	 * setter for last deploy date 
	 * 
	 * @param id
	 */	
	public void setLastDeployDate(Date lastDeployDate);
	
	public String toString ();
}