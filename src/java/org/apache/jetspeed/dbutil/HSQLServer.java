/*
 * Created on Feb 24, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.dbutil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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
            new HSQLServerThread(args).start();
            Thread.sleep(3000);
        }
        catch (InterruptedException e)
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
        System.out.println("Starting HSQLDB server on localhost: " + args);
        Server.main(args);
        
    }
    
   
}
