/*
 * Created on Feb 24, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.dbutil;

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
        
        try
        {
            new HSQLServerThread(args).start();
            Thread.sleep(3000);
        }
        catch (InterruptedException e)
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
