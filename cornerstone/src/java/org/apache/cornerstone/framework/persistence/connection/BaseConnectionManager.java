/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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