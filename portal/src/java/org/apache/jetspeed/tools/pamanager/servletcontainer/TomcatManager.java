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
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
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
    private static final String EMBEDDED_CONTEXT_FILE_PATH = "/META-INF/tomcat-context.xml";
    protected static final Log log = LogFactory.getLog("deployment");

    private String catalinaBase;
    private String catalinaEngine;
    private int catalinaVersionMajor;
    private String hostUrl;
    private int hostPort;
    private String userName;
    private String password;
    
    private String catalinaContextPath;
    
    private String managerAppPath = DEFUALT_MANAGER_APP_PATH;
    private String stopPath = managerAppPath + "/stop";
    private String startPath = managerAppPath + "/start";
    private String removePath = managerAppPath + "/remove";
    private String deployPath = managerAppPath + "/deploy";
    private String undeployPath = managerAppPath + "/undeploy";
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

    public TomcatManager(String catalinaBase, String catalinaEngine, int catalinaVersionMajor, String hostName, int hostPort, String userName, String password) throws HttpException, IOException
    {
        super();
        
        if ( !catalinaBase.endsWith("/") )
        {
            this.catalinaBase = catalinaBase + "/";
        }
        else
        {
            this.catalinaBase = catalinaBase;
        }    
        this.catalinaEngine = catalinaEngine;
        this.catalinaVersionMajor = catalinaVersionMajor;
        this.hostUrl = hostName;
        this.hostPort = hostPort;
        this.userName = userName;
        this.password = password;        
        
        if ( catalinaVersionMajor > 4 )
        {
            catalinaContextPath = this.catalinaBase + "/conf/" + this.catalinaEngine + "/" + this.hostUrl + "/";
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

        if ( catalinaVersionMajor > 4 )
        {
            // Tomcat 5 deprecated manager/install and manager/remove.
            // Those are now handled by manager/deploy and manager/undeploy respectively.
            installPath = deployPath;
            removePath = undeployPath;
        }
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
           // reload.setQueryString(buildPathQueryArgs(appPath));
            // This is the only way to get changes made to web.xml to
            // be picked up, reload DOES NOT reload the web.xml
            stop(appPath);
            Thread.sleep(1500);
            return start(appPath);
        }
        catch (InterruptedException e)
        {
            return "FAIL - "+e.toString();
        }
        finally
        {
            stop.recycle();
            stop.setPath(reloadPath);
            start.recycle();
            start.setPath(reloadPath);
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

    public String install(String warPath, String contextPath) throws HttpException, IOException
    {
        try
        {
            File contextFile = new File(warPath+EMBEDDED_CONTEXT_FILE_PATH);
            File warPathFile = new File(warPath);
            String canonicalWarPath = warPathFile.getCanonicalPath();

            if ( contextPath == null )
            {
                contextPath = "/"+ warPathFile.getName();
            }
            else if (!contextPath.startsWith("/"))
            {
                contextPath = "/" + contextPath;
            }

            if ( contextFile.exists() )
            {
                FileInputStream fileInputStream = null;
                FileOutputStream fileOutputStream = null;
                
                try
                {
                    SAXBuilder saxBuilder = new SAXBuilder();
                    fileInputStream = new FileInputStream(contextFile);
                    Document document = saxBuilder.build(fileInputStream);
                    if (!document.getRootElement().getName().equals("Context"))
                    {
                        throw new IOException(EMBEDDED_CONTEXT_FILE_PATH+" invalid!!!");
                    }
                    document.getRootElement().setAttribute("path", contextPath);
                    document.getRootElement().setAttribute("docBase", canonicalWarPath);
                    XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());
                    
                    File newContextFile = null;
                    if ( catalinaVersionMajor > 4 )
                    {
                        newContextFile = new File( catalinaContextPath+warPathFile.getName()+".xml");
                    }
                    else 
                    {
                        newContextFile = new File( warPathFile.getParentFile(), warPathFile.getName()+".xml");
                    }
                    fileOutputStream = new FileOutputStream(newContextFile);
                    output.output(document, fileOutputStream);
                    fileOutputStream.flush();
                    
                    install.setQueryString(buildConfigQueryArgs(newContextFile.getCanonicalPath(), contextPath));
                }
                catch (JDOMException e)
                {
                    IOException ioe = new IOException(EMBEDDED_CONTEXT_FILE_PATH+" invalid");
                    ioe.initCause(e);
                    throw ioe;
                }
                finally
                {
                    if ( fileInputStream != null )
                        fileInputStream.close();
                    if ( fileOutputStream != null )
                        fileOutputStream.close();
                }
            }
            else
                install.setQueryString(buildWarQueryArgs(canonicalWarPath, contextPath));

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
    
    public String getAppServerTarget(String appName)
    {
        return appName;
    }
}
