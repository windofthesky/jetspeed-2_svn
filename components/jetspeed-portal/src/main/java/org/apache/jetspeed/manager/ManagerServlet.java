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
package org.apache.jetspeed.manager;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.components.portletregistry.RegistryException;
import org.apache.jetspeed.deployment.DeploymentManager;
import org.apache.jetspeed.deployment.DeploymentStatus;
import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.tools.pamanager.servletcontainer.ApplicationServerManager;
import org.apache.jetspeed.tools.pamanager.servletcontainer.ApplicationServerManagerResult;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

/**
 * ManagerServlet ala Tomcat ManagerServlet 
 *
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class ManagerServlet extends HttpServlet
{
    private static int               OK                    = 0;
    private static int               ERROR_NO_DATA         = 1;
    private static int               ERROR_UNKNOWN_COMMAND = 2;
    private static int               ERROR_UNKNOWN_PA      = 3;
    private static int               ERROR_INVALID         = 4;
    private static int               ERROR_UNSUPPORTED     = 5;
    private static int               ERROR_UNAVAILABLE     = 6;
    private static int               ERROR_SERVER          = 7;
    private static int               ERROR_UNEXPECTED      = 8;
    private static int               ERROR_IGNORED         = 9;

    private ApplicationServerManager asm;
    private PortletRegistry          registry;
    private PortletFactory           portletFactory;
    private DeploymentManager        dm;

    public void init() throws ServletException
    {
        super.init();
        asm = Jetspeed.getComponentManager().lookupComponent(ApplicationServerManager.class);
        registry = Jetspeed.getComponentManager().lookupComponent(PortletRegistry.class);
        portletFactory = Jetspeed.getComponentManager().lookupComponent("portletFactory");
        dm =  Jetspeed.getComponentManager().lookupComponent("deploymentManager");
    }

    public void destroy()
    {
        super.destroy();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        process(request, response, false);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        process(request, response, true);
    }

    protected void process(HttpServletRequest request, HttpServletResponse response, boolean posted)
                    throws ServletException, IOException
    {
        // Prepare our output writer to generate the response message
        response.setContentType("text/plain; charset=utf-8");
        CharArrayWriter buffer = new CharArrayWriter();
        PrintWriter writer = new PrintWriter(buffer);

        // Identify the request parameters that we need
        String command = request.getPathInfo();
        int result = OK;

        if (command == null)
        {
            result = OK;
        }
        else if (command.equals("/list"))
        {
            result = list(writer);
        }
        else if (command.equals("/start"))
        {
            result = start(writer, request.getParameter("pa"));
        }
        else if (command.equals("/stop"))
        {
            result = stop(writer, request.getParameter("pa"));
        }
        else if (command.equals("/undeploy"))
        {
            result = undeploy(writer, request.getParameter("pa"));
        }
        else if (command.equals("/unregister"))
        {
            result = unregister(writer, request.getParameter("pa"));
        }
        else if (command.equals("/deploy"))
        {
            if (posted)
            {
                result = deploy(writer, request);
            }
            else
            {
                writer.println("Error: /deploy is only available through POST");
                result = ERROR_INVALID;
            }
        }
        else
        {
            writer.println("Error: Unknown command " + command);
            result = ERROR_UNKNOWN_COMMAND;
        }
        writer.flush();
        writer.close();
        writer = response.getWriter();
        if (result == OK)
        {
            writer.println("OK");
        }
        else
        {
            writer.println("FAIL - CODE: " + result);
        }
        writer.print(buffer.toString());
        writer.flush();
        writer.close();
    }

    protected int list(PrintWriter writer)
    {
        writer.println("Listed Portlet Applications");
        Iterator iter = registry.getPortletApplications().iterator();
        PortletApplication pa;
        while (iter.hasNext())
        {
            pa = (PortletApplication) iter.next();
            writer.println(pa.getName() + ":" + pa.getContextPath()
                           + ":" + (portletFactory.isPortletApplicationRegistered(pa) ? "ACTIVE" : "INACTIVE"));
        }
        return OK;
    }

    protected int start(PrintWriter writer, String paName)
    {
        PortletApplication pa = null;
        if (paName != null)
        {
            pa = registry.getPortletApplication(paName);
        }
        if (pa == null)
        {
            writer.println("Error: Unknown Portlet Application " + paName);
            return ERROR_UNKNOWN_PA;
        }
        if (portletFactory.isPortletApplicationRegistered(pa))
        {
            writer.println("Warning: Portlet Application " + paName + " already started");
            return OK;
        }
        else if (pa.getApplicationType() == PortletApplication.LOCAL)
        {
            writer.println("Error: Starting LOCAL Portlet Application " + paName + " not supported");
            return ERROR_UNSUPPORTED;
        }
        else if (!asm.isConnected())
        {
            writer.println("Error: Not connected to the server");
            return ERROR_UNAVAILABLE;
        }
        else
        {
            try
            {
                ApplicationServerManagerResult result = asm.start(pa.getContextPath());
                if (result.isOk())
                {
                    writer.println("Portlet Application " + paName + " started");
                    writer.println(result.getResponse());
                    return OK;
                }
                else
                {
                    writer.println("Error: Portlet Application " + paName + " could not be started");
                    writer.println(result.getResponse());
                    return ERROR_SERVER;
                }
            }
            catch (Exception e)
            {
                writer.println("Error: Failed to start Portlet Application " + paName + ": " + e.getMessage());
                e.printStackTrace(writer);
                return ERROR_UNEXPECTED;
            }
        }
    }

    protected int stop(PrintWriter writer, String paName)
    {
        PortletApplication pa = null;
        if (paName != null)
        {
            pa = registry.getPortletApplication(paName);
        }
        if (pa == null)
        {
            writer.println("Error: Unknown Portlet Application " + paName);
            return ERROR_UNKNOWN_PA;
        }
        if (!portletFactory.isPortletApplicationRegistered(pa))
        {
            writer.println("Portlet Application " + paName + " already stopped");
            return OK;
        }
        else if (pa.getApplicationType() == PortletApplication.LOCAL)
        {
            writer.println("Error: Stopping LOCAL Portlet Application " + paName + " not supported");
            return ERROR_UNSUPPORTED;
        }
        else if (!asm.isConnected())
        {
            writer.println("Error: Not connected to the server");
            return ERROR_UNAVAILABLE;
        }
        else
        {
            try
            {
                ApplicationServerManagerResult result = asm.stop(pa.getContextPath());
                if (result.isOk())
                {
                    writer.println("Portlet Application " + paName + " stopped");
                    writer.println(result.getResponse());
                    return OK;
                }
                else
                {
                    writer.println("Error: Portlet Application " + paName + " could not be stopped");
                    writer.println(result.getResponse());
                    return ERROR_SERVER;
                }
            }
            catch (Exception e)
            {
                writer.println("Error: Failed to stop Portlet Application " + paName + ": " + e.getMessage());
                e.printStackTrace(writer);
                return ERROR_UNEXPECTED;
            }
        }
    }

    protected int undeploy(PrintWriter writer, String paName)
    {
        int stopResult = stop(writer, paName);
        if (stopResult != OK)
        {
            return stopResult;
        }
        else if (!asm.isConnected())
        {
            writer.println("Error: Not connected to the server");
            return ERROR_UNAVAILABLE;
        }

        PortletApplication pa = registry.getPortletApplication(paName);
        try
        {
            ApplicationServerManagerResult result = asm.undeploy(pa.getContextPath());
            if (result.isOk())
            {
                writer.println("Portlet Application " + paName + " undeployed");
                writer.println(result.getResponse());
                return OK;
            }
            else
            {
                writer.println("Error: Portlet Application " + paName + " could not be undeployed");
                writer.println(result.getResponse());
                return ERROR_SERVER;
            }
        }
        catch (Exception e)
        {
            writer.println("Error: Failed to undeploy Portlet Application " + paName + ": " + e.getMessage());
            e.printStackTrace(writer);
            return ERROR_UNEXPECTED;
        }
    }

    protected int unregister(PrintWriter writer, String paName)
    {
        int result = stop(writer, paName);

        if (result != OK)
        {
            return result;
        }

        PortletApplication pa = registry.getPortletApplication(paName);
        try
        {
            registry.removeApplication(pa);
            writer.println("Portlet Application " + paName + " unregistered");
            return OK;
        }
        catch (RegistryException e)
        {
            writer.println("Error: Failed to unregister Portlet Application " + paName + ": " + e.getMessage());
            e.printStackTrace(writer);
            return ERROR_UNEXPECTED;
        }
    }

    protected int deploy(PrintWriter writer, HttpServletRequest request)
    {
        if (  !FileUpload.isMultipartContent(request) )
        {
            writer.println("Error: No file multipart content provided");
            return ERROR_NO_DATA;
        }
        File tempDir = null;
        File tempFile = null;

        try
        {
            DiskFileUpload upload = new DiskFileUpload();
            tempDir = File.createTempFile("upload", null);
            tempDir.deleteOnExit();
            tempDir.delete();
            tempDir.mkdirs();
            tempDir.deleteOnExit();
            List items = upload.parseRequest(request,0,-1L,tempDir.getAbsolutePath());
            Iterator iter = items.iterator();
            while ( iter.hasNext() )
            {
                FileItem item = (FileItem)iter.next();
                if (!item.isFormField())
                {
                    String fileName = item.getName();
                    tempFile = new File(tempDir, fileName );
                    tempFile.deleteOnExit();
                    item.write(tempFile);

                    try
                    {
                        DeploymentStatus status = dm.deploy(tempFile);
                        if ( status.getStatus() == DeploymentStatus.STATUS_OKAY )
                        {
                            writer.println("Deployed " + fileName);
                            return OK;
                        }
                        else if ( status.getStatus() == DeploymentStatus.STATUS_EVAL )
                        {
                            writer.println("Error: Unrecognized file "+ fileName);
                            return ERROR_IGNORED;
                        }
                        else
                        {
                            writer.println("Error: Failed to deploy file "+ fileName);
                            return ERROR_IGNORED;
                        }                    
                    }
                    catch (Throwable e)
                    {
                        writer.println("Error: Failed to deploy file " + fileName + ": " + e.getMessage());
                        e.printStackTrace(writer);
                        return ERROR_UNEXPECTED;
                    }
                }
            }

        }
        catch (Throwable e)
        {
            writer.println("Error: Failed to process uploaded data: "+e.getMessage());
            e.printStackTrace(writer);
            return ERROR_UNEXPECTED;
        }
        finally
        {
            if (tempFile != null)
            {
                tempFile.delete();
            }
            if (tempDir != null)
            {
                tempDir.delete();
            }
        }
        return OK;
    }
}