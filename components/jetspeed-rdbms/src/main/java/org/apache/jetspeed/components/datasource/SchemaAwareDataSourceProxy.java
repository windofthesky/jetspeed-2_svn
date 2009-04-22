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
package org.apache.jetspeed.components.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

/**
 * The SchemaAwareDataSourceProxy optionally injects a schema selection into an
 * existing database connection. It proxies a DataSource and executes an
 * injected sql statement on every getConnection() call.
 * 
 * Inspired by http://forum.springframework.org/showthread.php?t=10728, runtime
 * schema switching was stripped.
 * 
 * @author <a href="mailto:joachim@wemove.com">Joachim Müller</a>
 * @version $Id$
 */
public class SchemaAwareDataSourceProxy extends TransactionAwareDataSourceProxy
{
    private static final Logger log = LoggerFactory.getLogger(SchemaAwareDataSourceProxy.class);

    private String schemaSql = null;

    public void setSchemaSql(String schemaSql)
    {
        this.schemaSql = schemaSql;
    }

    public Connection getConnection() throws SQLException
    {
        Connection con = super.getConnection();

        if (schemaSql != null)
        {
            if (log.isDebugEnabled())
            {
                log.debug("Setting schema by executing sql '" + schemaSql + "' on connection " + con);
            }

            Statement stmt = con.createStatement();
            try
            {
                // database specific SQL.
                stmt.execute(schemaSql);
            }
            catch (Exception e)
            {
                log.error("Error executing table schema setting sql: '" + schemaSql + "'.", e);
            }
            finally
            {
                stmt.close();
            }
        }

        return con;
    }
}
