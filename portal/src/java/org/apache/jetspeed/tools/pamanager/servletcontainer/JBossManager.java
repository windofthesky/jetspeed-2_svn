/*
 * Copyright 2000-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.jetspeed.tools.pamanager.servletcontainer;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpException;

import groovy.swing.impl.Startable;

/**
 * JBoss application server management
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 */
public class JBossManager implements ApplicationServerManager, Startable
{

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.pamanager.servletcontainer.ApplicationServerManager#start(java.lang.String)
     */
    public String start(String appPath) throws HttpException, IOException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.pamanager.servletcontainer.ApplicationServerManager#stop(java.lang.String)
     */
    public String stop(String appPath) throws HttpException, IOException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.pamanager.servletcontainer.ApplicationServerManager#reload(java.lang.String)
     */
    public String reload(String appPath) throws HttpException, IOException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.pamanager.servletcontainer.ApplicationServerManager#remove(java.lang.String)
     */
    public String remove(String appPath) throws HttpException, IOException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.pamanager.servletcontainer.ApplicationServerManager#install(java.lang.String, java.lang.String)
     */
    public String install(String warPath, String contexPath)
            throws HttpException, IOException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.pamanager.servletcontainer.ApplicationServerManager#deploy(java.lang.String, java.io.InputStream, int)
     */
    public String deploy(String appPath, InputStream is, int size)
            throws HttpException, IOException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.pamanager.servletcontainer.ApplicationServerManager#getHostPort()
     */
    public int getHostPort()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.pamanager.servletcontainer.ApplicationServerManager#getHostUrl()
     */
    public String getHostUrl()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.pamanager.servletcontainer.ApplicationServerManager#isConnected()
     */
    public boolean isConnected()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see groovy.swing.impl.Startable#start()
     */
    public void start()
    {
        // TODO Auto-generated method stub

    }

}
