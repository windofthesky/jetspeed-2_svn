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
package org.apache.jetspeed.tools.pamanager.servletcontainer;

import java.io.IOException;
import java.io.InputStream;

/**
 * JBoss application server management
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 */
public class JBossManager implements ApplicationServerManager
{

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.pamanager.servletcontainer.ApplicationServerManager#start(java.lang.String)
     */
    public ApplicationServerManagerResult start(String appPath) throws IOException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.pamanager.servletcontainer.ApplicationServerManager#stop(java.lang.String)
     */
    public ApplicationServerManagerResult stop(String appPath) throws IOException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.pamanager.servletcontainer.ApplicationServerManager#reload(java.lang.String)
     */
    public ApplicationServerManagerResult reload(String appPath) throws IOException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.pamanager.servletcontainer.ApplicationServerManager#undeploy(java.lang.String)
     */
    public ApplicationServerManagerResult undeploy(String appPath) throws IOException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.pamanager.servletcontainer.ApplicationServerManager#deploy(java.lang.String, java.io.InputStream, int)
     */
    public ApplicationServerManagerResult deploy(String appPath, InputStream is, int size)
            throws IOException
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

    public String getAppServerTarget(String appName)
    {
        return appName + ".war";
    }    
}
