/**
 * Created on Sep 9, 2003
 *
 * 
 * @author
 */
package org.apache.jetspeed.tools.pamanager.servletcontainer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;

/**
 * <p>
 * TomcatManager
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class TomcatManager 
{
    private static final String DEFUALT_MANAGER_APP_PATH = "/manager";

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

    public TomcatManager(String hostName, int hostPort, String userName, String password) throws HttpException, IOException
    {
        super();
        init(hostName, hostPort, userName, password);
    }

    /**
     * do nothing constructor
     *
     */
    protected TomcatManager()
    {
        super();
    }

    protected void init(String hostName, int hostPort, String userName, String password) throws IOException, HttpException
    {
        this.hostUrl = hostName;
        this.hostPort = hostPort;
        this.userName = userName;
        this.password = password;

        client = new HttpClient();

        HostConfiguration hostConfig = new HostConfiguration();
        hostConfig.setHost(hostUrl, hostPort, "http");

        client.setHostConfiguration(hostConfig);
        // Fix for non-buffereing large WAR files during deploy
        client.getState().setAuthenticationPreemptive(true);
        client.getState().setCredentials(null, hostUrl, new UsernamePasswordCredentials(userName, password));

        // perform a test, we can use this to close a
        GetMethod test = new GetMethod(serverInfoPath);
        try
        {
            client.executeMethod(test);
        }
        finally
        {
            test.releaseConnection();
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
            install.setQueryString(buildWarQueryArgs(warPath));
            if (contexPath != null)
            {
                install.setQueryString(buildPathQueryArgs(contexPath));
            }

            client.executeMethod(install);
            return deploy.getResponseBodyAsString();
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
        return new NameValuePair[] { new NameValuePair("path", appPath)};
    }

    protected NameValuePair[] buildWarQueryArgs(String appPath)
    {
        return new NameValuePair[] { new NameValuePair("war", appPath)};
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

}
