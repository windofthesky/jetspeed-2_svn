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

import java.io.IOException;

import javax.portlet.Portlet;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.portlet.ContentTypeSet;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.portlet.SupportsHeaderPhase;
import org.apache.jetspeed.util.BaseObjectProxy;
import org.apache.jetspeed.container.JetspeedPortletConfig;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.portlet.UnavailableException;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.pluto.om.servlet.WebApplicationDefinition;
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
    
    private static ThreadLocal tlPortletObjectProxied =
        new ThreadLocal() {
            protected synchronized Object initialValue() {
                return new boolean [] { false };
            }
        };
    
    public static void setPortletObjectProxied(boolean portletObjectProxied)
    {
        ((boolean []) tlPortletObjectProxied.get())[0] = portletObjectProxied;
    }
        
    public static boolean isPortletObjectProxied()
    {
        return ((boolean []) tlPortletObjectProxied.get())[0];
    }
    
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
    private ContentTypeSet portletContentTypeSet;
    private boolean autoSwitchEditDefaultsModeToEditMode;
    private boolean autoSwitchConfigMode;
    private String customConfigModePortletUniqueName;
    
    public static Object createProxy(Object proxiedObject, boolean autoSwitchEditDefaultsModeToEditMode, boolean autoSwitchConfigMode, String customConfigModePortletUniqueName)
    {
        Class proxiedClass = proxiedObject.getClass();
        ClassLoader classLoader = proxiedClass.getClassLoader();
        Class [] proxyInterfaces = null;
        
        if (proxiedObject instanceof SupportsHeaderPhase)
        {
            proxyInterfaces = new Class [] { Portlet.class, SupportsHeaderPhase.class };
        }
        else
        {
            proxyInterfaces = new Class [] { Portlet.class };
        }
        
        InvocationHandler handler = new PortletObjectProxy(proxiedObject, autoSwitchEditDefaultsModeToEditMode, autoSwitchConfigMode, customConfigModePortletUniqueName);
        return Proxy.newProxyInstance(classLoader, proxyInterfaces, handler);
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
        boolean handledHere = false;
        Class declaringClass = method.getDeclaringClass();
        
        if (declaringClass == Portlet.class)
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
        if (this.portletContentTypeSet == null)
        {
            try
            {
                JetspeedPortletConfig config = (JetspeedPortletConfig) portlet.getPortletConfig();
                PortletDefinition portletDef = config.getPortletDefinition();
                this.portletContentTypeSet = portletDef.getContentTypeSet();
            }
            catch (Exception e)
            {
            }
        }
        
        if (this.portletContentTypeSet != null)
        {
            return this.portletContentTypeSet.supportsPortletMode(JetspeedActions.EDIT_DEFAULTS_MODE);
        }
        
        return false;
    }
       
    private void refreshCustomConfigModePortletInstance()
    {
        try
        {
            PortletRegistry registry = (PortletRegistry) Jetspeed.getComponentManager().getComponent("portletRegistry");
            PortletFactory portletFactory = (PortletFactory) Jetspeed.getComponentManager().getComponent("portletFactory");
            ServletContext portalAppContext = ((ServletConfig) Jetspeed.getComponentManager().getComponent("ServletConfig")).getServletContext();
            
            PortletDefinitionComposite portletDef = (PortletDefinitionComposite) registry.getPortletDefinitionByUniqueName(this.customConfigModePortletUniqueName);
            MutablePortletApplication portletApp = (MutablePortletApplication) portletDef.getPortletApplicationDefinition();
            WebApplicationDefinition webAppDef = portletApp.getWebApplicationDefinition();
            String portletAppName = webAppDef.getContextRoot();
            ServletContext portletAppContext = portalAppContext.getContext(portletAppName);
            
            setPortletObjectProxied(true);
            this.customConfigModePortletInstance = portletFactory.getPortletInstance(portletAppContext, portletDef);
        }
        catch (Exception e)
        {
        }
        finally
        {
            setPortletObjectProxied(false);
        }
    }
    
}
