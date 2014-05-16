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
 * 
 * <p>
 * ApplicationServerManager
 * </p>
 * <p>
 * Implementations of this interface are used primarily by the
 * {@link org.apache.jetspeed.tools.pamanager.ApplicationServerPAM}
 * to interact with the servlet container that is supporting the web
 * appliaction portion of deployed the portlet applications.
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface ApplicationServerManager
{
    /**
     * 
     * <p>
     * start
     * </p>
     * Starts the application represented by the context path, <code>appPath</code>
     *
     * @param appPath path to restart
     * @return container-specific status message
     * @throws IOException
     */
    ApplicationServerManagerResult start( String appPath ) throws IOException;
    
    /**
     * 
     * <p>
     * stop
     * </p>
     * Stops the application represented by the context path, <code>appPath</code>
     * 
     * @param appPath
     * @return container-specific status message
     * @throws IOException
     */
    ApplicationServerManagerResult stop( String appPath ) throws IOException;
    
    /**
     * 
     * <p>
     * reload
     * </p>
     * Reloads the application represented by the context path, <code>appPath</code>.  This
     * must included re-reading the web.xml and reloading all classpath resources.
     *
     * @param appPath
     * @return container-specific status message
     * @throws IOException
     */
    ApplicationServerManagerResult reload( String appPath ) throws IOException;
    
    /**
     * 
     * <p>
     * undeploy
     * </p>
     * Undeploys the application represented by the context path, <code>appPath</cod
     * @param appPath
     * @return container-specific status message
     * @throws IOException
     */
    ApplicationServerManagerResult undeploy( String appPath ) throws IOException;
    
    /**
     * 
     * <p>
     * deploy
     * </p>
     *
     * Deploys the contents of the InputStream, <code>is</code>, into the parent servlet
     * container using the specified <code>appPath</code> as the context path.
     * 
     * @param appPath
     * @param is
     * @param size size (in bytes) of InputStream <code>is</code>
     * @return
     * @throws IOException
     */
    ApplicationServerManagerResult deploy( String appPath, InputStream is, int size ) throws IOException;

    /**
     * 
     * <p>
     * getHostPort
     * </p>
     *
     * @return
     */
    int getHostPort();

    /**
     * 
     * <p>
     * getHostUrl
     * </p>
     *
     * @return
     */
    String getHostUrl();
    
    /**
     * 
     * <p>
     * isConnected
     * </p>
     *
     * @return
     */
    boolean isConnected();
    
    /**
     * <p> Returns the name of the target directory or archive where the portlet app will be 
     *     deployed as known to the application server
     * </p>
     */
    String getAppServerTarget(String appName);
    
}