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
package org.apache.jetspeed.dbutil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.hsqldb.Server;


/**
 * @author Sweaver
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class HSQLServer
{

    public static void main(String[] args)
    {
        if(args[0].equals("kill"))
        {
            kill(Integer.parseInt(args[1]), args[2], args[3]);
            return;
        }
        
        try
        {
            System.out.println("Starting server: " + args[1]);
            Thread hsql = new HSQLServerThread(args);
            hsql.start();
            System.out.println("Exiting HSQL");
            
        }
        catch (Exception e)
        {
            
        }
    }
    
    private static void kill(int port, String user, String password)
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
           
        }
        
        
    }
}

class HSQLServerThread extends Thread
{

    private String[] args;

    HSQLServerThread(String args[])
    {
       
        this.args = args; 
        setDaemon(true);
      
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        System.out.println("Starting HSQLDB server");
        Server.main(args);
        
    }
    
   
}
