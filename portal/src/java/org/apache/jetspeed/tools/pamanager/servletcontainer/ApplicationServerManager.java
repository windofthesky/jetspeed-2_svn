/*
 * Created on Jun 30, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.tools.pamanager.servletcontainer;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpException;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface ApplicationServerManager
{
    String start( String appPath ) throws HttpException, IOException;

    String stop( String appPath ) throws HttpException, IOException;

    String reload( String appPath ) throws HttpException, IOException;

    String remove( String appPath ) throws HttpException, IOException;

    String install( String warPath, String contexPath ) throws HttpException, IOException;

    String deploy( String appPath, InputStream is, int size ) throws HttpException, IOException;

    /**
     * @return
     */
    int getHostPort();

    /**
     * @return
     */
    String getHostUrl();
    
    boolean isConnected();
}