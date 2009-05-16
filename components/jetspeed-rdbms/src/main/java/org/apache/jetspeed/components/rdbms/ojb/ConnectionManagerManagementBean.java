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
package org.apache.jetspeed.components.rdbms.ojb;

import java.util.ArrayList;
import java.util.List;

/**
 * Manage OJB ConnectionManagerImpl instance state.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class ConnectionManagerManagementBean
{
	private static List<ConnectionManagerImpl> connectionManagers = new ArrayList<ConnectionManagerImpl>();
	
	/**
	 * Add connection manager to list of ConnectionManagerImpl instances.
	 * 
	 * @param connectionManager connection manager instance
	 */
	protected static void addConnectionManager(ConnectionManagerImpl connectionManager)
	{
	    // manage connection manager instances
		synchronized (connectionManagers)
		{
			connectionManagers.add(connectionManager);
		}
	}
	
    /**
     * Manage connection manager instances on bean creation.
     */
    public ConnectionManagerManagementBean()
    {
    	// reset connection manager instances
		synchronized (connectionManagers)
		{
		    ConnectionManagerImpl.resetConnectionFactories();
			for (ConnectionManagerImpl connectionManager : connectionManagers)
			{
				connectionManager.reset();
			}
		}
    }
}
