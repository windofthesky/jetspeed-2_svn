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
package org.apache.jetspeed.portlet;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.container.JetspeedPortletConfig;
import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.factory.PortletInstance;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.om.portlet.Supports;
import org.apache.jetspeed.util.BaseObjectProxy;
import org.apache.jetspeed.util.GenericPortletUtils;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventPortlet;
import javax.portlet.GenericPortlet;
import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceServingPortlet;
import javax.portlet.UnavailableException;
import javax.portlet.WindowState;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.List;

/**
 * PortletObjectProxy
 * 
 * @author <a href="mailto:woonsan@apache.org">Woonsan Ko</a>
 * @version $Id: PortletObjectProxy.java 516448 2007-03-09 16:25:47Z ate $
 */
public class PortletObjectProxy extends BaseObjectProxy
{
    
    private static Method renderMethod;
    private static Method processActionMethod;
    
    static 
    {
    	try 
        {
            renderMethod = Portlet.class.getMethod("render", new Class [] { RenderRequest.class, RenderResponse.class });
            processActionMethod = Portlet.class.getMethod("processAction", new Class [] { ActionRequest.class, ActionResponse.class });
        } 
        catch (NoSuchMethodException e) 
        {
    	    throw new NoSuchMethodError(e.getMessage());
    	}
    }
    
    private Object portletObject;
    private PortletInstance customConfigModePortletInstance;
    private PortletInstance customPreviewModePortletInstance;
    private boolean genericPortletInvocable;
    private Method portletDoEditMethod;
    private boolean autoSwitchEditDefaultsModeToEditMode;
    private boolean autoSwitchConfigMode;
    private String customConfigModePortletUniqueName;
    private boolean autoSwitchPreviewMode;
    private String customPreviewModePortletUniqueName;
    private List<Supports> supports;
    
    public static Object createProxy(Object proxiedObject, 
                                     boolean autoSwitchEditDefaultsModeToEditMode, 
                                     boolean autoSwitchConfigMode, String customConfigModePortletUniqueName)
    {
        return createProxy(proxiedObject, autoSwitchEditDefaultsModeToEditMode, autoSwitchConfigMode, customConfigModePortletUniqueName, false, null);
    }
    
    @SuppressWarnings("unchecked")
    public static Object createProxy(Object proxiedObject, 
                                     boolean autoSwitchEditDefaultsModeToEditMode, 
                                     boolean autoSwitchConfigMode, String customConfigModePortletUniqueName,
                                     boolean autoSwitchPreviewMode, String customPreviewModePortletUniqueName)
    {
        HashSet<Class> interfaces = new HashSet<Class>();
        interfaces.add(Portlet.class);
        Class current = proxiedObject.getClass();
        while (current != null)
        {
            try
            {
                Class[] currentInterfaces = current.getInterfaces();
                for (int i = 0; i < currentInterfaces.length; i++)
                {
                    if (currentInterfaces[i] != Portlet.class)
                    {
                        interfaces.add(currentInterfaces[i]);
                    }
                }
                current = current.getSuperclass();
            }
            catch (Exception e)
            {
                current = null;
            }
        }
        
        Class proxiedClass = proxiedObject.getClass();
        ClassLoader classLoader = proxiedClass.getClassLoader();
        
        InvocationHandler handler = 
            new PortletObjectProxy(proxiedObject, 
                                   autoSwitchEditDefaultsModeToEditMode, 
                                   autoSwitchConfigMode, customConfigModePortletUniqueName,
                                   autoSwitchPreviewMode, customPreviewModePortletUniqueName);
        return Proxy.newProxyInstance(classLoader, interfaces.toArray(new Class[interfaces.size()]), handler);
    }

    private PortletObjectProxy(Object portletObject, 
                               boolean autoSwitchEditDefaultsModeToEditMode, 
                               boolean autoSwitchConfigMode, String customConfigModePortletUniqueName,
                               boolean autoSwitchPreviewMode, String customPreviewModePortletUniqueName)
    {
        this.portletObject = portletObject;
        this.autoSwitchEditDefaultsModeToEditMode = autoSwitchEditDefaultsModeToEditMode;
        this.autoSwitchConfigMode = autoSwitchConfigMode;
        this.customConfigModePortletUniqueName = customConfigModePortletUniqueName;
        this.autoSwitchPreviewMode = autoSwitchPreviewMode;
        this.customPreviewModePortletUniqueName = customPreviewModePortletUniqueName;
        
        if (portletObject instanceof GenericPortlet)
        {
            this.portletDoEditMethod = GenericPortletUtils.getRenderModeHelperMethod((GenericPortlet) portletObject, PortletMode.EDIT);
            
            if (this.portletDoEditMethod != null)
            {
                this.genericPortletInvocable = true;
            }
        }
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        Object result = null;
        Class<? extends Object> declaringClass = method.getDeclaringClass();
        
        try
        {
            if (declaringClass == Portlet.class || declaringClass == ResourceServingPortlet.class || declaringClass == EventPortlet.class)
            {
                if (renderMethod.equals(method))
                {
                    proxyRender((RenderRequest) args[0], (RenderResponse) args[1]);
                    return null;
                }
                else if (processActionMethod.equals(method))
                {
                    proxyProcessAction((ActionRequest) args[0], (ActionResponse) args[1]);
                }
                else
                {
                    result = method.invoke(this.portletObject, args);
                }
            }
            else if (declaringClass == SupportsHeaderPhase.class)
            {
                result = method.invoke(this.portletObject, args);
            }
            else
            {
                result = super.invoke(proxy, method, args);
            }
        }
        catch (InvocationTargetException ite)
        {
            throw ite.getTargetException();
        }
        return result;
    }

    protected void proxyRender(RenderRequest request, RenderResponse response) throws PortletException, IOException, Exception
    {
        PortletMode mode = request.getPortletMode();
        
        boolean autoSwitchConfigMode = false;
        boolean autoSwitchToEditMode = false;
        boolean autoSwitchPreviewMode = false;
        
        if (this.autoSwitchConfigMode && JetspeedActions.CONFIG_MODE.equals(mode))
        {
            autoSwitchConfigMode = true;
        }
        
        if (this.autoSwitchEditDefaultsModeToEditMode && this.genericPortletInvocable && JetspeedActions.EDIT_DEFAULTS_MODE.equals(mode))
        {
            if (!isSupportingPortletMode((GenericPortlet) this.portletObject, JetspeedActions.EDIT_DEFAULTS_MODE))
            {
                autoSwitchToEditMode = true;
            }
        }
        
        if (this.autoSwitchPreviewMode && JetspeedActions.PREVIEW_MODE.equals(mode))
        {
            if (!isSupportingPortletMode((GenericPortlet) this.portletObject, JetspeedActions.PREVIEW_MODE))
            {
                autoSwitchPreviewMode = true;
            }
        }
        
        if (autoSwitchConfigMode)
        {
            try
            {
                if (this.customConfigModePortletInstance == null)
                {
                    this.customConfigModePortletInstance = getPortletInstance(this.customConfigModePortletUniqueName);
                }
                
                this.customConfigModePortletInstance.render(request, response);
            }
            catch (UnavailableException e)
            {
                this.customConfigModePortletInstance = getPortletInstance(this.customConfigModePortletUniqueName);
                this.customConfigModePortletInstance.render(request, response);
            }
        }
        else if (autoSwitchToEditMode)
        {
            GenericPortlet genericPortlet = (GenericPortlet) this.portletObject;
            
            // Override GenericPortlet#render....
            WindowState state = request.getWindowState();
            
            if (!WindowState.MINIMIZED.equals(state))
            {
                String title = genericPortlet.getPortletConfig().getResourceBundle(request.getLocale()).getString("javax.portlet.title");
                response.setTitle(title);
                
                this.portletDoEditMethod.invoke(genericPortlet, new Object [] { request, response });
            }
        }
        else if (autoSwitchPreviewMode)
        {
            try
            {
                if (this.customPreviewModePortletInstance == null)
                {
                    this.customPreviewModePortletInstance = getPortletInstance(this.customPreviewModePortletUniqueName);
                }
                
                this.customPreviewModePortletInstance.render(request, response);
            }
            catch (UnavailableException e)
            {
                this.customPreviewModePortletInstance = getPortletInstance(this.customPreviewModePortletUniqueName);
                this.customPreviewModePortletInstance.render(request, response);
            }
        }
        else
        {
            ((Portlet) this.portletObject).render(request, response);
        }
    }

    protected void proxyProcessAction(ActionRequest request, ActionResponse response) throws PortletException, IOException, Exception
    {
        PortletMode mode = request.getPortletMode();
        
        boolean autoSwitchConfigMode = false;
        
        if (this.autoSwitchConfigMode && JetspeedActions.CONFIG_MODE.equals(mode))
        {
            autoSwitchConfigMode = true;
        }
        
        if (autoSwitchConfigMode)
        {
            try
            {
                if (this.customConfigModePortletInstance == null)
                {
                    this.customConfigModePortletInstance = getPortletInstance(this.customConfigModePortletUniqueName);
                }
                
                this.customConfigModePortletInstance.processAction(request, response);
            }
            catch (UnavailableException e)
            {
                this.customConfigModePortletInstance = getPortletInstance(this.customConfigModePortletUniqueName);
                this.customConfigModePortletInstance.processAction(request, response);
            }
        }
        else
        {
            ((Portlet) this.portletObject).processAction(request, response);
        }
    }
    
    private boolean isSupportingPortletMode(GenericPortlet portlet, PortletMode portletMode)
    {
        if (supports == null)
        {
            try
            {
                JetspeedPortletConfig config = (JetspeedPortletConfig) portlet.getPortletConfig();
                PortletDefinition portletDef = config.getPortletDefinition();
                this.supports = portletDef.getSupports();
            }
            catch (Exception e)
            {
            }
        }
        
        if (supports != null)
        {
            String portletModeName = portletMode.toString();
            
            for (Supports s : supports)
            {
                if (s.getPortletModes().contains(portletModeName))
                {
                    return true;
                }
            }
            
            return false;
        }
        
        return false;
    }
       
    private PortletInstance getPortletInstance(String portletUniqueName) throws PortletException
    {
        PortletRegistry registry =  Jetspeed.getComponentManager().lookupComponent("portletRegistry");
        PortletFactory portletFactory = Jetspeed.getComponentManager().lookupComponent("portletFactory");
        ServletContext portalAppContext = ((ServletConfig) Jetspeed.getComponentManager().lookupComponent("ServletConfig")).getServletContext();
        
        PortletDefinition portletDef = registry.getPortletDefinitionByUniqueName(portletUniqueName, true);
        PortletApplication portletApp = portletDef.getApplication();
        ServletContext portletAppContext = portalAppContext.getContext(portletApp.getContextPath());
        
        return portletFactory.getPortletInstance(portletAppContext, portletDef, false);
    }
    
}
