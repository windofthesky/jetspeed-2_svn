/*
 * Copyright 2004 The Apache Software Foundation.
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
package org.apache.jetspeed.tools.pamanager.servletcontainer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.picocontainer.Startable;

/**
 * <p>
 * TomcatManager
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class TomcatManager implements ApplicationServerManager, Startable
{
    private static final String DEFUALT_MANAGER_APP_PATH = "/manager";
    protected static final Log log = LogFactory.getLog("deployment");

    private String hostUrl;
    private int hostPort;
    private String userName;
    private String password;

    private String managerAppPath = DEFUALT_MANAGER_APP_PATH;
    private String stopPath = managerAppPath + "/stop";
    private String startPath = managerAppPath + "/start";
    private String removePath = managerAppPath + "/remove";
    private String deployPath = managerAppPath + "/deploy";
    private String installPath = managerAppPath + "/install";
    private String reloadPath = managerAppPath + "/reload";
    private String serverInfoPath = managerAppPath + "/serverinfo";

    private HttpClient client;

    private HttpMethod start;

    private HttpMethod stop;

    private HttpMethod reload;

    private HttpMethod remove;

    private PutMethod deploy;

    private HttpMethod install;

    protected GetMethod testConnectionMethod;

    public TomcatManager(String hostName, int hostPort, String userName, String password) throws HttpException, IOException
    {
        super();
        this.hostUrl = hostName;
        this.hostPort = hostPort;
        this.userName = userName;
        this.password = password;        
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

        testConnectionMethod = new GetMethod(serverInfoPath);
        //        try
        //        {
        //            client.executeMethod(test);
        //        }
        //        finally
        //        {
        //            test.releaseConnection();
        //        }
        start = new GetMethod(startPath);
        stop = new GetMethod(stopPath);
        remove = new GetMethod(removePath);
        install = new GetMethod(installPath);
        reload = new GetMethod(reloadPath);
        deploy = new PutMethod(deployPath);
    }

    public String start(String appPath) throws HttpException, IOException
    {
        try
        {
            start.setQueryString(buildPathQueryArgs(appPath));
            client.executeMethod(start);
            return start.getResponseBodyAsString();
        }
        finally
        {
            start.recycle();
            start.setPath(startPath);
        }
    }

    public String stop(String appPath) throws HttpException, IOException
    {
        try
        {
            stop.setQueryString(buildPathQueryArgs(appPath));
            client.executeMethod(stop);
            return stop.getResponseBodyAsString();
        }
        finally
        {
            stop.recycle();
            stop.setPath(stopPath);
        }
    }

    public String reload(String appPath) throws HttpException, IOException
    {
        try
        {
            reload.setQueryString(buildPathQueryArgs(appPath));
            client.executeMethod(reload);
            return reload.getResponseBodyAsString();
        }
        finally
        {
            reload.recycle();
            reload.setPath(reloadPath);
        }
    }

    public String remove(String appPath) throws HttpException, IOException
    {

        try
        {
            remove.setQueryString(buildPathQueryArgs(appPath));
            client.executeMethod(remove);
            return remove.getResponseBodyAsString();
        }
        finally
        {
            remove.recycle();
            remove.setPath(removePath);
        }
    }

    public String install(String warPath, String contexPath) throws HttpException, IOException
    {
        try
        {
            install.setQueryString(buildWarQueryArgs(warPath, contexPath));

            client.executeMethod(install);
            return install.getResponseBodyAsString();
        }
        finally
        {
            install.recycle();
            install.setPath(installPath);
        }

    }

    public String deploy(String appPath, InputStream is, int size) throws HttpException, IOException
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
            return deploy.getResponseBodyAsString();
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
        if (appPath != null)
        {
            if (!appPath.startsWith("/"))
            {
                appPath = "/" + appPath;
            }
            return new NameValuePair[] {
                new NameValuePair("war", new File(warPath).toURL().toString()),
                new NameValuePair("path", appPath)};
        }
        else
        {
            return new NameValuePair[] { new NameValuePair("war", warPath)};
        }

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
            log.warn("Unknown server, CatalinaPAM will only function as FileSystemPAM: " + e1.toString());

            return false;
        }
        catch (IOException e1)
        {
            log.warn("IOException, CatalinaPAM will only function as FileSystemPAM: " + e1.toString());

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
}
