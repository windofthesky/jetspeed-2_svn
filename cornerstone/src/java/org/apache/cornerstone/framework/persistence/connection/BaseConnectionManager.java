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

package org.apache.cornerstone.framework.persistence.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.cornerstone.framework.api.implementation.ImplementationException;
import org.apache.cornerstone.framework.api.persistence.connection.ConnectionException;
import org.apache.cornerstone.framework.api.persistence.connection.IConnectionManager;
import org.apache.cornerstone.framework.api.persistence.datasource.IDataSource;
import org.apache.cornerstone.framework.core.BaseObject;
import org.apache.cornerstone.framework.init.Cornerstone;

public class BaseConnectionManager extends BaseObject implements IConnectionManager
{
    public static final String REVISION = "$Revision$";

    public static BaseConnectionManager getSingleton()
    {
        return _Singleton;
    }

    public Connection getConnection(String dataSourceName) throws ConnectionException
    {
        return getConnection(dataSourceName, true);
    }

    public Connection getConnection(String dataSourceName, boolean autoCommit) throws ConnectionException
    {
        Connection connectionInThread = (Connection) _connectionThreadLocal.get();
        try
		{
			if (connectionInThread == null || connectionInThread.isClosed())
			{
			    connectionInThread = createConnection(dataSourceName);
			    connectionInThread.setAutoCommit(autoCommit);
			    _connectionThreadLocal.set(connectionInThread);
			}
		}
		catch (SQLException se)
		{
            throw new ConnectionException(se);
		}

        return connectionInThread;
    }

    public Connection createConnection(String dataSourceName) throws ConnectionException
    {
    	try
		{
			IDataSource dataSource = (IDataSource) Cornerstone.getImplementationManager().createImplementation(IDataSource.class, dataSourceName);
            String driverClassName = dataSource.getDriverClassName();
            Class.forName(driverClassName);
            String url = dataSource.getConnectionUrl();
            String userName = dataSource.getConnectionUserName();
            String password = dataSource.getConnectionPassword();
            Connection connection = DriverManager.getConnection(url, userName, password);
            return connection;
		}
		catch (ImplementationException ie)
		{
			throw new ConnectionException(ie.getCause());
		}
		catch (ClassNotFoundException cnfe)
		{
            throw new ConnectionException(cnfe);
		}
		catch (SQLException se)
		{
            throw new ConnectionException(se);
		}
    }

    /* (non-Javadoc)
     * @see cornerstone.framework.database.IConnectionManager#closeConnection(java.sql.Connection)
     */
    public void closeConnection(Connection connection) throws ConnectionException
    {
        if (connection != null)
        {
            try
			{
				if (!connection.isClosed())
				{
				    if (!connection.getAutoCommit()) connection.commit();
				    connection.close();
				}
			}
			catch (SQLException se)
			{
                throw new ConnectionException(se);
			}
        }
        _connectionThreadLocal.set(null);
    }

    private static BaseConnectionManager _Singleton = new BaseConnectionManager();
    protected ThreadLocal _connectionThreadLocal = new ThreadLocal();
}