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

package org.apache.cornerstone.framework.api.persistence.connection;

import java.sql.Connection;

public interface IConnectionManager
{
    public static final String REVISION = "$Revision$";

    /**
     * Gets the current connection in auto-commit mode.  Calls next method.
     * @param dataSourceName
     * @return
     * @throws SQLException
     */
    public Connection getConnection(String dataSourceName) throws ConnectionException;

    /**
     * Gets the current transaction in thread.  If none, a new connection is created and placed in thread local.
     * @param dataSourceName
     * @param AutoCommit
     * @return
     * @throws SQLException
     */
    public Connection getConnection(String dataSourceName, boolean AutoCommit) throws ConnectionException;

    /**
     * Subclasses should overwrite this method to create a new connection.
     * @param dataSourceName
     * @return
     * @throws SQLException
     */
    public Connection createConnection(String dataSourceName) throws ConnectionException;

    /**
     * This method should be called instead of simply "connection.close()".
     * @param connection
     * @throws SQLException
     */
    public void closeConnection(Connection connection) throws ConnectionException;
}