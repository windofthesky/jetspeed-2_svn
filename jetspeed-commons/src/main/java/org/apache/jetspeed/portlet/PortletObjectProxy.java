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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;

import java.io.IOException;

import javax.portlet.EventPortlet;
import javax.portlet.Portlet;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.ResourceServingPortlet;
import javax.portlet.WindowState;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.portlet.SupportsHeaderPhase;
import org.apache.jetspeed.util.BaseObjectProxy;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.portlet.UnavailableException;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.container.JetspeedPortletConfig;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.om.portlet.Supports;
import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.factory.PortletInstance;

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
    private boolean genericPortletInvocable;
    private Method portletDoEditMethod;
    private boolean autoSwitchEditDefaultsModeToEditMode;
    private boolean autoSwitchConfigMode;
    private String customConfigModePortletUniqueName;
    private List<Supports> supports;
    
    @SuppressWarnings("unchecked")
    public static Object createProxy(Object proxiedObject, boolean autoSwitchEditDefaultsModeToEditMode, boolean autoSwitchConfigMode, String customConfigModePortletUniqueName)
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
        
        InvocationHandler handler = new PortletObjectProxy(proxiedObject, autoSwitchEditDefaultsModeToEditMode, autoSwitchConfigMode, customConfigModePortletUniqueName);
        return Proxy.newProxyInstance(classLoader, interfaces.toArray(new Class[interfaces.size()]), handler);
    }

    private PortletObjectProxy(Object portletObject, boolean autoSwitchEditDefaultsModeToEditMode, boolean autoSwitchConfigMode, String customConfigModePortletUniqueName)
    {
        this.portletObject = portletObject;
        this.autoSwitchEditDefaultsModeToEditMode = autoSwitchEditDefaultsModeToEditMode;
        this.autoSwitchConfigMode = autoSwitchConfigMode;
        this.customConfigModePortletUniqueName = customConfigModePortletUniqueName;
        
        if (portletObject instanceof GenericPortlet)
        {
            try
            {
                this.portletDoEditMethod = this.portletObject.getClass().getMethod("doEdit", new Class [] { RenderRequest.class, RenderResponse.class });
                
                if (Modifier.isPublic(this.portletDoEditMethod.getModifiers()))
                {
                    this.genericPortletInvocable = true;
                }
            }
            catch (NoSuchMethodException e)
            {
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
        
        if (this.autoSwitchConfigMode && JetspeedActions.CONFIG_MODE.equals(mode))
        {
            autoSwitchConfigMode = true;
        }
        
        if (this.autoSwitchEditDefaultsModeToEditMode && this.genericPortletInvocable)
        {
            if (JetspeedActions.EDIT_DEFAULTS_MODE.equals(mode))
            {
                if (!isSupportingEditDefaultsMode((GenericPortlet) this.portletObject))
                {
                    autoSwitchToEditMode = true;
                }
            }
        }
        
        if (autoSwitchConfigMode)
        {
            try
            {
                if (this.customConfigModePortletInstance == null)
                {
                    refreshCustomConfigModePortletInstance();
                }
                
                this.customConfigModePortletInstance.render(request, response);
            }
            catch (UnavailableException e)
            {
                refreshCustomConfigModePortletInstance();
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
                    refreshCustomConfigModePortletInstance();
                }
                
                this.customConfigModePortletInstance.processAction(request, response);
            }
            catch (UnavailableException e)
            {
                refreshCustomConfigModePortletInstance();
                this.customConfigModePortletInstance.processAction(request, response);
            }
        }
        else
        {
            ((Portlet) this.portletObject).processAction(request, response);
        }
    }
    
    private boolean isSupportingEditDefaultsMode(GenericPortlet portlet)
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
            String pm = JetspeedActions.EDIT_DEFAULTS_MODE.toString();
            for (Supports s : supports)
            {
                if (s.getPortletModes().contains(pm))
                {
                    return true;
                }
            }
            return false;
        }
        
        return false;
    }
       
    private void refreshCustomConfigModePortletInstance() throws PortletException
    {
        PortletRegistry registry = (PortletRegistry) Jetspeed.getComponentManager().getComponent("portletRegistry");
        PortletFactory portletFactory = (PortletFactory) Jetspeed.getComponentManager().getComponent("portletFactory");
        ServletContext portalAppContext = ((ServletConfig) Jetspeed.getComponentManager().getComponent("ServletConfig")).getServletContext();
        
        PortletDefinition portletDef = registry.getPortletDefinitionByUniqueName(this.customConfigModePortletUniqueName, true);
        PortletApplication portletApp = portletDef.getApplication();
        ServletContext portletAppContext = portalAppContext.getContext(portletApp.getContextPath());
        
        this.customConfigModePortletInstance = portletFactory.getPortletInstance(portletAppContext, portletDef, false);
    }
    
}
