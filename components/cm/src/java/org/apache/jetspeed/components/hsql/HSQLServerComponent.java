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
package org.apache.jetspeed.components.hsql;

import java.io.File;
import java.io.IOException;
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
 * </p>
 * 
 * 
 * @
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
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


    public HSQLServerComponent(int port, String user, String password, String dbScriptPath, boolean trace, boolean silent)
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
        HSQLServer serverThread = new HSQLServer(port, fqPath);
        serverThread.start();
        // verify

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
        try
        {
            Class.forName("org.hsqldb.jdbcDriver");
            String url = "jdbc:hsqldb:hsql://127.0.0.1:" + port;
            Connection con = DriverManager.getConnection(url, user, password);
            String sql = "SHUTDOWN";
            Statement stmt = con.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
        }
        catch (Exception e)
        {
            log.error("Unable to safely shutdown HSQLDB Server: " + e.toString(), e);
        }
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
}