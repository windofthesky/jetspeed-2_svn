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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * TomcatManager
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class TomcatManager implements ApplicationServerManager
{
    private static final String DEFAULT_MANAGER_APP_PATH = "/manager";
    protected static final Logger log = LoggerFactory.getLogger("deployment");

    private String hostUrl;
    private int hostPort;
    private String userName;
    private String password;
    
    
    private String managerAppPath = DEFAULT_MANAGER_APP_PATH;
    private String stopPath = managerAppPath + "/stop";
    private String startPath = managerAppPath + "/start";
    private String deployPath = managerAppPath + "/deploy";
    private String undeployPath = managerAppPath + "/undeploy";
    private HttpClient client;

    private HttpMethod start;

    private HttpMethod stop;

    private HttpMethod undeploy;

    private PutMethod deploy;

    public TomcatManager(String hostName, int hostPort, String userName, String password) throws IOException
    {
        this.hostUrl = hostName;
        this.hostPort = hostPort;
        this.userName = userName;
        this.password = password;
    }
    
    private ApplicationServerManagerResult parseResult(String responseBody)
    {
        if ( responseBody.startsWith("OK - "))
        {
            return new ApplicationServerManagerResult(true, responseBody.substring(5), responseBody);
        }
        else if ( responseBody.startsWith("FAIL - "))
        {
            return new ApplicationServerManagerResult(false, responseBody.substring(7), responseBody);
        }
        else
        {
            return new ApplicationServerManagerResult(false, responseBody, responseBody);
        }
    }

    public void start() 
    {     
        client = new HttpClient();

        HostConfiguration hostConfig = new HostConfiguration();
        hostConfig.setHost(hostUrl, hostPort, "http");

        client.setHostConfiguration(hostConfig);
        // Fix for non-buffereing large WAR files during deploy
        client.getState().setAuthenticationPreemptive(true);
        client.getState().setCredentials(null, hostUrl, new UsernamePasswordCredentials(userName, password));

        start = new GetMethod(startPath);
        stop = new GetMethod(stopPath);
        undeploy = new GetMethod(undeployPath);
        deploy = new PutMethod(deployPath);
    }

    public ApplicationServerManagerResult start(String appPath) throws IOException
    {
        try
        {
            start.setQueryString(buildPathQueryArgs(appPath));
            client.executeMethod(start);
            return parseResult(start.getResponseBodyAsString());
        }
        finally
        {
            start.recycle();
            start.setPath(startPath);
        }
    }

    public ApplicationServerManagerResult stop(String appPath) throws IOException
    {
        try
        {
            stop.setQueryString(buildPathQueryArgs(appPath));
            client.executeMethod(stop);
            return parseResult(stop.getResponseBodyAsString());
        }
        finally
        {
            stop.recycle();
            stop.setPath(stopPath);
        }
    }

    public ApplicationServerManagerResult reload(String appPath) throws IOException
    {
        try
        {
           // reload.setQueryString(buildPathQueryArgs(appPath));
            // This is the only way to get changes made to web.xml to
            // be picked up, reload DOES NOT reload the web.xml
            stop(appPath);
            Thread.sleep(1500);
            return start(appPath);
        }
        catch (InterruptedException e)
        {
            return parseResult("FAIL - "+e.toString());
        }
        finally
        {
            stop.recycle();
            stop.setPath(stopPath);
            start.recycle();
            start.setPath(startPath);
        }
    }

    public ApplicationServerManagerResult undeploy(String appPath) throws IOException
    {
        try
        {
            undeploy.setQueryString(buildPathQueryArgs(appPath));
            client.executeMethod(undeploy);
            return parseResult(undeploy.getResponseBodyAsString());
        }
        finally
        {
            undeploy.recycle();
            undeploy.setPath(undeployPath);
        }
    }

    public ApplicationServerManagerResult deploy(String appPath, InputStream is, int size) throws IOException
    {
        try
        {
            deploy.setQueryString(buildPathQueryArgs(appPath));

            //deploy.setRequestContentLength(PutMethod.CONTENT_LENGTH_CHUNKED);

            if (size != -1)
            {
                deploy.setRequestContentLength(size);
            }
            deploy.setRequestBody(is);

            client.executeMethod(deploy);
            return parseResult(deploy.getResponseBodyAsString());
        }
        finally
        {
            deploy.recycle();
            deploy.setPath(deployPath);
        }
    }

    protected NameValuePair[] buildPathQueryArgs(String appPath)
    {
        if (!appPath.startsWith("/"))
        {
            appPath = "/" + appPath;
        }
        return new NameValuePair[] { new NameValuePair("path", appPath)};
    }

    protected NameValuePair[] buildWarQueryArgs(String warPath, String appPath) throws MalformedURLException
    {
        return new NameValuePair[] {
                new NameValuePair("war", new File(warPath).toURL().toString()),
                new NameValuePair("path", appPath)};
    }

    protected NameValuePair[] buildConfigQueryArgs(String configPath, String appPath) throws MalformedURLException
    {
        return new NameValuePair[] {
                new NameValuePair("config", new File(configPath).toURL().toString()),
                new NameValuePair("path", appPath)};
    }

    /**
     * @return
     */
    public int getHostPort()
    {
        return hostPort;
    }

    /**
     * @return
     */
    public String getHostUrl()
    {
        return hostUrl;
    }

    /**
     * <p>
     * isConnected
     * </p>
     *
     * @see org.apache.jetspeed.tools.pamanager.servletcontainer.ApplicationServerManager#isConnected()
     * @return
     */
    public boolean isConnected()
    {
        Socket checkSocket = null;
        try
        {
            checkSocket = new Socket(hostUrl, hostPort);
            return true;
        }
        catch (UnknownHostException e1)
        {
            log.error("Unknown server: " + e1.toString());

            return false;
        }
        catch (IOException e1)
        {
            log.error("IOException: " + e1.toString());

            return false;
        }
        finally
        {
            try
            {
                // close the server check
                if (checkSocket != null)
                {
                    checkSocket.close();
                }
            }
            catch (IOException e2)
            {
                // do nothing
            }
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
    }
    
    public String getAppServerTarget(String appName)
    {
        return appName;
    }
}
