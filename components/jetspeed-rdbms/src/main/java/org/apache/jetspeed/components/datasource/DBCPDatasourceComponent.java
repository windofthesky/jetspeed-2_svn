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
package org.apache.jetspeed.components.datasource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * <p>
 * DBCPDatasourceComponent
 * </p>
 * 
 * 
 * @
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 *
 */
public class DBCPDatasourceComponent implements DatasourceComponent
{

    private static final Logger log = LoggerFactory.getLogger(DBCPDatasourceComponent.class);

    protected PoolingDataSource dataSource;
    //protected DataSource dataSource;
    
    private String user;
    
    private String password;
    
    private String driverName;
    
    private String connectURI;    
    
    private int maxActive;
    
    private int maxWait;
    
    private byte whenExhausted;
    
    private boolean autoCommit;
    
    private PoolableConnectionFactory dsConnectionFactory;
    /**
     * 
     * Creates a simple commons DBCP connection pool using the following
     * parameters.
     * <p>
     * If you need to bind the datasource of this component to 
     * JNDI please @see org.apache.jetspeed.components.jndi.JNDIComponent
     * </p>
     * 
     * @param user User name that will be used to connect to the DB
     * @param password Password that will be used to connect to the DB
     * @param driverName Fully qualified driver to be used by the connection pool
     * @param connectURI Fully qualified URI to the DB.
     * @param maxActive Maximum active connection
     * @param maxWait if <code>whenExhausted</code> is set to GenericObjectPool.WHEN_EXHAUSTED_BLOCK
     * the length of time to block while waiting for a connection to become 
     * available.
     * @param whenExhausted GenericObjectPool.WHEN_EXHAUSTED_BLOCK, GenericObjectPool.WHEN_EXHAUSTED_GROW or
     * GenericObjectPool.WHEN_EXHAUSTED_FAIL. @see org.apache.commons.pooling.GenericObjectPool
     * for more information on these settings
     * @param autoCommit Whether or not this datasource will autocommit
     * @throws ClassNotFoundException If the <code>driverName</code> could not be
     * located within any classloaders.
     */
    public DBCPDatasourceComponent(
        String user,
        String password,
        String driverName,
        String connectURI,
        int maxActive,
        int maxWait,
        byte whenExhausted,
        boolean autoCommit)
        
    {

        log.info("Setting up data source pooling for " + driverName);

        log.info("Max active connnections set to: " + maxActive);

        log.info("Pool is set to \"" + whenExhausted + "\" when all connections are exhausted.");
		
		this.user = user;
		this.password = password;
		this.driverName = driverName;
		this.connectURI = connectURI;
		this.maxActive = maxActive;
		this.maxWait = maxWait;
		this.autoCommit = autoCommit;
    }

    /**
     * 
     * <p>
     * getDatasource
     * </p>
     * 
     * <p>
     *   returns the datasource created by this component
     * </p>
     * @return
     *
     */
    public DataSource getDatasource()
    {
        return dataSource;
    }

    public void start()
    {

        try
        {
            log.info("Attempting to start DBCPCDatasourceComponent.");            
            Class.forName(driverName);
            
            // Validate the connection before we go any further
            try
            {
                Connection conn = null;
                if (user == null || user.trim().isEmpty()) {
                    conn = DriverManager.getConnection(connectURI);
                }
                else {
                    if (password == null)
                        password = "";
                    conn = DriverManager.getConnection(connectURI, user, password);
                }

                conn.close();
            }
            catch(Exception e)
            {
                log.error("Unable to obtain a connection database via URI: "+connectURI, e);
                throw e;
            }
            
            ObjectPool connectionPool = new GenericObjectPool(null, maxActive, whenExhausted, maxWait);

            ConnectionFactory connectionFactory = null;
            if (user == null || user.trim().isEmpty()) {
                connectionFactory = new DriverManagerConnectionFactory(connectURI, new Properties());
            }
            else {
                if (password == null)
                    password = "";
                connectionFactory = new DriverManagerConnectionFactory(connectURI, user, password);
            }
            dsConnectionFactory = new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, autoCommit);
            
            dataSource = new PoolingDataSource(connectionPool);            
            
            log.info("DBCPCDatasourceComponent successfuly started!");
        }
        catch (Throwable e)
        {
            CharArrayWriter caw = new CharArrayWriter();
            e.printStackTrace(new PrintWriter(caw));
            String msg = "Unable to start DBCPCDatasourceComponent: ";
            log.error(msg+e.toString(), e);
            throw new IllegalStateException(msg+caw.toString());
        }
    }

    public void stop()
    {
        try
        {
        	if (log.isInfoEnabled()) {
	        	log.info("Stopping DBCPCDatasourceComponent");
        	}
            dsConnectionFactory.getPool().close();

            // Support for using an embedded Derby database multiple times from one JVM
            // by properly shutting it down after each (test) run
            if (driverName.equals("org.apache.derby.jdbc.EmbeddedDriver"))
            {
                if (log.isInfoEnabled()) {
                	log.info("Shutting down derby ...");
                }
                String shutDownURI = null;
                int parIndex = connectURI.indexOf(";");
                if (parIndex > -1)
                {
                    shutDownURI = connectURI.substring(0, parIndex)+";shutdown=true";
                }
                else
                {
                    shutDownURI = connectURI+";shutdown=true";
                }
                Class dc = Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
                Driver driver = (Driver)dc.newInstance();                
                Properties info = new Properties();
                info.put( "user", user );
                info.put( "password", password );
                
                driver.connect(shutDownURI, info);
            }

        }
		 catch (SQLException e)  {
		    if ( driverName.equals("org.apache.derby.jdbc.EmbeddedDriver") && (e.getSQLState().equals("XJ015") ||  e.getSQLState().equals("08006"))) {
		    	if (log.isDebugEnabled()) {
		    		log.debug("Database shut down normally with sql state '" + e.getSQLState() + "'.");
		    	}
		    } else {
	        	IllegalStateException ise =
	                new IllegalStateException("Unable to safely shutdown the DBCPConnection pool: " + e.toString());
	            ise.initCause(e);
	            try
	            {
	                log.error("Unable to safely shutdown the DBCPConnection pool", ise);
	            }
	            catch (Exception e1)
	            {
	                // ignore if logger itself is gone too
	            }
		    }
		 }
        catch (Exception e)
        {
        	IllegalStateException ise =
                new IllegalStateException("Unable to safely shutdown the DBCPConnection pool: " + e.toString());
            ise.initCause(e);
            try
            {
                log.error("Unable to safely shutdown the DBCPConnection pool", ise);
            }
            catch (Exception e1)
            {
                // ignore if logger itself is gone too
            }
        }
    }
}
