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
package org.apache.jetspeed.cps.hsql;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import javax.naming.Context;


import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.fulcrum.InitializationException;
import org.apache.jetspeed.cps.BaseCommonService;
import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.cps.jndi.JNDIService;
import org.hsqldb.Server;
import org.hsqldb.jdbcDriver;


/**
 * <p>
 * HSQLDBServer
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class HSQLDBServer extends BaseCommonService
{
    public static final String KEY_USE_JNDI_DS = "use.jndi.datasource";
    public static final String SERVICE_NAME = "HSQLDBServer";
    public static final String NAMING_ROOT = "java:comp/env/";

    private static final Log log = LogFactory.getLog(HSQLDBServer.class);

    private int port;

    private String user;

    private String password;

    private boolean silent;

    private boolean trace;

    protected PoolingDataSource dataSource;
    private PoolableConnectionFactory dsConnectionFactory;
    /**
     * @see org.apache.fulcrum.Service#init()
     */
    public void init() throws InitializationException
    {

        if (!isInitialized())
        {

            try
            {
                port = getConfiguration().getInt("port", 9001);
                user = getConfiguration().getString("user", "sa");
                password = getConfiguration().getString("password", "");        
                // allow override by a system property        
                String dbPath = System.getProperty("services.HSQLDBServer.db.path", getConfiguration().getString("db.path", "WEB-INF/db/hsql/Registry"));
				String fqPath = null;
				if(System.getProperty("services.HSQLDBServer.db.path") == null)
				{
					fqPath = new File(getRealPath(dbPath)).getCanonicalPath(); 
				}
				else
				{
					fqPath = dbPath;
				}
                
                log.info("Using HSQL script file: "+fqPath);
                trace = getConfiguration().getBoolean("trace", false);
                silent = getConfiguration().getBoolean("silent", true);
                boolean useJndi = getConfiguration().getBoolean(KEY_USE_JNDI_DS, false);

                HSQLServer serverThread = new HSQLServer(port, fqPath);
                serverThread.start();

                if (useJndi)
                {
                    setupJNDIDatasource();
                }

                setInit(true);

            }
            catch (Exception e)
            {

                String msg = "Unable to start HSQLDB server " + e.toString();
                log.error(msg, e);
                throw new InitializationException(msg);
            }
        }
    }

    /**
     * @see org.apache.fulcrum.Service#shutdown()
     */
    public void shutdown()
    {
        try
        {
            Class.forName("org.hsqldb.jdbcDriver");
            String url = "jdbc:hsqldb:hsql://127.0.0.1:" + port;
            Connection con = DriverManager.getConnection(url, user, password);
            String sql = "SHUTDOWN";
            Statement stmt = con.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            if (dsConnectionFactory != null)
            {
                dsConnectionFactory.getPool().close();
            }

        }
        catch (Exception e)
        {
            log.error("Unable to safely shutdown HSQLDB Server: " + e.toString(), e);
        }
        finally
        {
            setInit(false);
        }

        super.shutdown();
    }

    class HSQLServer extends Thread
    {

        private String[] args;

        HSQLServer(int port, String dbPath)
        {
            args =
                new String[] {
                    "-database",
                    dbPath,
                    "-port",
                    String.valueOf(port),
                    "-no_system_exit",
                    "true",
                    "-silent",
                    String.valueOf(silent),
                    "-trace",
                    String.valueOf(trace)};
            setName("Jetspeed HSQLDB Thread");
        }

        /**
        * @see java.lang.Runnable#run()
        */
        public void run()
        {
            log.info("Starting HSQLDB server on localhost: " + args);
            Server.main(args);
            try
            {
                join();
            }
            catch (InterruptedException e)
            {

            }
        }

    }

    protected void setupJNDIDatasource() throws Exception
    {
        Configuration conf = getConfiguration();
        log.info("Setting JNDI data source pooling for HSQL.");
        int maxActive = conf.getInt("jndi.datasource.maxActive", 10);
        log.info("Max active connnections set to: " + maxActive);
        int maxWait = conf.getInt("jndi.datasource.max.block.wait", 0);
        String whenExhausted = conf.getString("jndi.datasource.when.exhausted", "grow");
        log.info("Pool is set to \"" + whenExhausted + "\" when all connections are exhausted.");
        String jndiName = conf.getString("jndi.datasource.name", "jdbc/jetspeed");
        log.info("HSQL data source name set to: " + jndiName);
        byte whenExhaustedByte = GenericObjectPool.WHEN_EXHAUSTED_GROW;
        boolean autoCommit = conf.getBoolean("jndi.datasource.autocommit", true);
        if (whenExhausted.equals("grow"))
        {
            whenExhaustedByte = GenericObjectPool.WHEN_EXHAUSTED_GROW;
        }
        else if (whenExhausted.equals("block"))
        {
            whenExhaustedByte = GenericObjectPool.WHEN_EXHAUSTED_BLOCK;
            log.info("The pool will block for connections for a maximum of: " + maxWait);
        }
        else if (whenExhausted.equals("fail"))
        {
            whenExhaustedByte = GenericObjectPool.WHEN_EXHAUSTED_FAIL;
        }
        
        // load the HSQL Driver
        Class.forName(jdbcDriver.class.getName());

        ObjectPool connectionPool = new GenericObjectPool(null, maxActive, whenExhaustedByte, maxWait);

        String connectURI = "jdbc:hsqldb:hsql://127.0.0.1:" + port;

        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(connectURI, user, password);

        dsConnectionFactory = new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, true);

        dataSource = new PoolingDataSource(connectionPool);

		JNDIService jndiServ = (JNDIService) CommonPortletServices.getPortalService(JNDIService.SERVICE_NAME);
        Context ctx = jndiServ.getRootContext();
        ctx.bind("comp/env/"+jndiName, dataSource);
     

        String fqJndiName = NAMING_ROOT + jndiName;
        log.info("Pooled data source successfully created, binding to fully qualified name: " + fqJndiName);
        
        log.info("JNDI binding succesful.");

    }

}
