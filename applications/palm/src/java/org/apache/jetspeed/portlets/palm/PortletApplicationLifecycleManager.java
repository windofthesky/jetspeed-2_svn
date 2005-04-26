/* Copyright 2004 Apache Software Foundation
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.jetspeed.portlets.palm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.components.portletregistry.RegistryException;
import org.apache.jetspeed.deployment.DeploymentManager;
import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.tools.pamanager.servletcontainer.ApplicationServerManager;
import org.apache.jetspeed.tools.pamanager.servletcontainer.ApplicationServerManagerResult;
import org.apache.portals.bridges.common.GenericServletPortlet;
import org.apache.portals.gems.util.StatusMessage;
import org.apache.portals.messaging.PortletMessaging;

/**
 * PALM Portlet
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class PortletApplicationLifecycleManager extends GenericServletPortlet
{
    private ApplicationServerManager asm;
    private PortletRegistry          registry;
    private PortletFactory           portletFactory;
    private DeploymentManager        dm;
    private boolean serverManagerAvailable;
    
    public void init(PortletConfig config)
    throws PortletException 
    {
        super.init(config);
        PortletContext context = getPortletContext();                
        registry = (PortletRegistry)context.getAttribute(CommonPortletServices.CPS_REGISTRY_COMPONENT);
        portletFactory = (PortletFactory)context.getAttribute(CommonPortletServices.CPS_PORTLET_FACTORY_COMPONENT);
        dm = (DeploymentManager)context.getAttribute(CommonPortletServices.CPS_DEPLOYMENT_MANAGER_COMPONENT);
        asm = (ApplicationServerManager)context.getAttribute(CommonPortletServices.CPS_APPLICATION_SERVER_MANAGER_COMPONENT);
        if (null == registry)
        {
            throw new PortletException("Failed to find the Portlet Registry on portlet initialization");
        }
        if (null == portletFactory)
        {
            throw new PortletException("Failed to find the Portlet Factory on portlet initialization");
        }
        serverManagerAvailable = (asm != null && asm.isConnected());
    }
           
    public void doView(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        request.setAttribute("serverManagerAvailable",serverManagerAvailable?Boolean.TRUE:Boolean.FALSE);
        
        StatusMessage msg = (StatusMessage)PortletMessaging.consume(request, "PALM", "status");
        if (msg != null)
        {
            request.setAttribute("statusMsg", msg);
        }
        if ( request.getPortletSession().getAttribute("list") == null )
        {
            List list = new ArrayList();
            Iterator apps = registry.getPortletApplications().iterator();
            while (apps.hasNext())
            {
                MutablePortletApplication pa = (MutablePortletApplication)apps.next();
                PortletApplicationStatusBean bean = new PortletApplicationStatusBean(pa, portletFactory.isPortletApplicationRegistered(pa));
                list.add(bean);
            }            
            request.getPortletSession().setAttribute("list",list);
        }
        
        super.doView(request, response);
    }
    
    public void processAction(ActionRequest request, ActionResponse response)
    throws PortletException, IOException
    {
        if (request.getPortletMode() == PortletMode.VIEW)
        {
            String action = request.getParameter("action");
            String value = request.getParameter("value");
            
            if ( !isEmpty(action))
            {
                // enforce list is rebuild next doView
                request.getPortletSession().removeAttribute("list");
                
                if (!action.equals("refresh") && !isEmpty(value))
                {
                    MutablePortletApplication pa = registry.getPortletApplication(value);
                    if ( pa == null )
                    {
                        publishStatusMessage(request, "PALM", "status", null, "Portlet Application "+pa.getName()+" no longer exists");
                    }
                    else if ( pa.getApplicationType() == MutablePortletApplication.LOCAL )
                    {
                        // TODO
                    }
                    else // ( pa.getApplicationType() == MutablePortletApplication.WEBAPP )
                    {
                        if (action.equals("start"))
                        {
                            startPA(request,pa);
                        }
                        else if (action.equals("stop"))
                        {
                            stopPA(request,pa);
                        }
                        else if (action.equals("undeploy"))
                        {
                            undeployPA(request,pa);
                        }
                        else if (action.equals("delete"))
                        {
                            deletePA(request,pa);
                        }
                    }
                }
            }
        }
    }
    
    protected void publishStatusMessage(PortletRequest request, String portlet, String topic, Throwable e, String message)
    {
        if ( e != null )
        {
            message = message + ": " + e.toString();
            Throwable cause = e.getCause();
            if (cause != null)
            {
                message = message + ", " + cause.getMessage();
            }
        }
        StatusMessage sm = new StatusMessage(message, StatusMessage.ERROR);
        try
        {
            // TODO: fixme, bug in Pluto on portlet session
            PortletMessaging.publish(request, portlet, topic, sm);
        }
        catch (Exception ee)
        {
            System.err.println("Failed to publish message: " + message);
        }        
    }

    protected void startPA(ActionRequest request, MutablePortletApplication pa)
    {
        if ( portletFactory.isPortletApplicationRegistered(pa))
        {
            publishStatusMessage(request, "PALM", "status", null, "Portlet Application "+pa.getName()+" already running");
        }
        else if ( !serverManagerAvailable || !asm.isConnected() )
        {
            publishStatusMessage(request, "PALM", "status", null, "Application Server Manager not available");
        }
        else
        {
            try
            {
                ApplicationServerManagerResult result = asm.start(pa.getWebApplicationDefinition().getContextRoot());
                if ( !result.isOk() )
                {
                    publishStatusMessage(request, "PALM", "status", null, result.getMessage());
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                publishStatusMessage(request, "PAM", "status", e, "Could not start Portlet Application "+pa.getName());
            }
        }
    }

    protected void stopPA(ActionRequest request, MutablePortletApplication pa)
    {
        if ( !portletFactory.isPortletApplicationRegistered(pa))
        {
            publishStatusMessage(request, "PALM", "status", null, "Portlet Application "+pa.getName()+" no longer running");
        }
        else if ( !serverManagerAvailable || !asm.isConnected() )
        {
            publishStatusMessage(request, "PALM", "status", null, "Application Server Manager not available");
        }
        else
        {
            try
            {
                ApplicationServerManagerResult result = asm.stop(pa.getWebApplicationDefinition().getContextRoot());
                if ( !result.isOk() )
                {
                    publishStatusMessage(request, "PALM", "status", null, result.getMessage());
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                publishStatusMessage(request, "PALM", "status", e, "Could not stop Portlet Application "+pa.getName());
            }
        }
    }

    protected void undeployPA(ActionRequest request, MutablePortletApplication pa)
    {
        if ( !serverManagerAvailable || !asm.isConnected() )
        {
            publishStatusMessage(request, "PALM", "status", null, "Application Server Manager not available");
        }
        else
        {
            try
            {
                ApplicationServerManagerResult result = asm.undeploy(pa.getWebApplicationDefinition().getContextRoot());
                if ( !result.isOk() )
                {
                    publishStatusMessage(request, "PALM", "status", null, result.getMessage());
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                publishStatusMessage(request, "PALM", "status", e, "Could not undeploy Portlet Application "+pa.getName());
            }
        }
    }

    protected void deletePA(ActionRequest request, MutablePortletApplication pa)
    {
        if ( portletFactory.isPortletApplicationRegistered(pa))
        {
            publishStatusMessage(request, "PALM", "status", null, "Portlet Application "+pa.getName()+" is still running");
        }
        else
        {
            try
            {
                registry.removeApplication(pa);
            }
            catch (RegistryException e)
            {
                e.printStackTrace();
                publishStatusMessage(request, "PALM", "status", e, "Could not delete Portlet Application "+pa.getName());
            }
        }
    }

    private boolean isEmpty(String s)
    {
        if (s == null) return true;
        
        if (s.trim().equals("")) return true;
        
        return false;
    }    
}
