/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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

package org.apache.jetspeed.components.hsql;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hsqldb.Server;
import org.hsqldb.jdbcDriver;
import org.picocontainer.Startable;

/**
 * <p>
 * HSQLServerComponent
 * </p>@
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $ $
 *  
 */
public class HSQLServerComponent implements Startable
{

    public static final String KEY_USE_JNDI_DS = "use.jndi.datasource";
    public static final String SERVICE_NAME = "HSQLDBServer";
    public static final String NAMING_ROOT = "java:comp/env/";
    private static final Log log = LogFactory.getLog(HSQLServerComponent.class);
    private String password;
    private int port;
    private boolean silent;
    private boolean trace;
    private String user;
    private String fqPath;
    private HSQLServer HSQLthread;
    private boolean serverStarted;

    public HSQLServerComponent(int port, String user, String password, String dbScriptPath, boolean trace, 
    boolean silent)
    throws IOException
    {
        this.port = port;
        this.user = user;
        this.password = password;
        // allow override by a system property
        String dbPath = System.getProperty("services.HSQLDBServer.db.path", dbScriptPath);
        fqPath = null;
        if (System.getProperty("services.HSQLDBServer.db.path") == null)
        {
            fqPath = new File(dbPath).getCanonicalPath();
        }
        else
        {
            fqPath = dbPath;
        }
        log.info("Using HSQL script file: " + fqPath);
        this.trace = trace;
        this.silent = silent;
    }

    /**
     * <p>
     * start
     * </p>
     * 
     * @see org.picocontainer.Startable#start()
     *  
     */
    public void start()
    {
        // if socket in use warn and skip
        if (socketInUse())
        {
            log.warn("HSQL server port " + port + " is already in use.  Server not started.");
            serverStarted = false;
            return;
        }
        HSQLthread = new HSQLServer(port, fqPath);
        boolean started = false;
        int startCount = 0;
        while (!started && startCount < 5)
        {
            try
            {
                startCount++;
                HSQLthread.start();
                started = true;
            }
            catch (Exception e1)
            {
            }
        }
        try
        {
            Class.forName(jdbcDriver.class.getName());
        }
        catch (ClassNotFoundException e)
        {
            // this SHOULD NOT happen
            e.printStackTrace();
        }
        boolean connected = false;
        int retries = 5;
        int attempt = 0;
        log.info("Verifying HSQL server is up will try 5 times then give up.");
        while (!connected && attempt < retries)
        {
            try
            {
                attempt++;
                log.info("Attempt " + attempt + ".");
                Connection conn = DriverManager.getConnection("jdbc:hsqldb:hsql://127.0.0.1", user, password);
                connected = true;
                log.info("Attempt " + attempt + " successful!");
            }
            catch (SQLException e1)
            {
                connected = false;
                log.info("Attempt " + attempt + " failed!");
                try
                {
                    Thread.sleep(2000);
                }
                catch (InterruptedException e2)
                {
                }
            }
        }
        if (!connected)
        {
            log.warn("Unable to successfuly verify HSQL was successfuly started.");
        }
        serverStarted = true;
    }

    /**
     * <p>
     * stop
     * </p>
     * 
     * @see org.picocontainer.Startable#stop()
     *  
     */
    public void stop()
    {
        if (!serverStarted)
        {
            // we never started so just return
            log.info("HSQL Server WAS NOT started by this component, so it WILL NOT attempt to stop it.");
            return;
        }
        
        try
        {
            log.info("====== SHUTTING DOWN HSQL Server ========");
            Class.forName("org.hsqldb.jdbcDriver");
            String url = "jdbc:hsqldb:hsql://127.0.0.1:" + port;
            Connection con = DriverManager.getConnection(url, user, password);
            String sql = "SHUTDOWN";
            Statement stmt = con.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();

            // block while shutting down
            while (socketInUse())
            {                
               Thread.sleep(1000);
            }
            log.info("HSQL Socket successfully closed.");
        }
        catch (Exception e)
        {
            log.error("Unable to safely shutdown HSQLDB Server: " + e.toString(), e);
        }
    }

    protected boolean socketInUse()
    {
        try
        {
            Socket socket;
            socket = new Socket("127.0.0.1", port);
            socket.close();
            return true;
        }
        catch (ConnectException e)
        {
            return false;
        }
        catch (IOException e1)
        {
            return false;
        }
    }
    class HSQLServer extends Thread
    {

        private String[] args;

        HSQLServer(int port, String dbPath)
        {
            args = 
            new String[]{
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
            setDaemon(true);
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
}