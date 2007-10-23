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

/**
 * PortletObjectProxy
 * 
 * @author <a href="mailto:woonsan@apache.org">Woonsan Ko</a>
 * @version $Id: PortletObjectProxy.java 516448 2007-03-09 16:25:47Z ate $
 */
public class PortletObjectProxy extends BaseObjectProxy
{

    private static Method renderMethod;
    
    static 
    {
    	try 
        {
            renderMethod = Portlet.class.getMethod("render", new Class [] { RenderRequest.class, RenderResponse.class });
        } 
        catch (NoSuchMethodException e) 
        {
    	    throw new NoSuchMethodError(e.getMessage());
    	}
    }
    
    private Object portletObject;
    private boolean genericPortletInvocable;
    private Method portletDoEditMethod;
    private ContentTypeSet portletContentTypeSet;
    
    public static Object createProxy(Object proxiedObject)
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
        
        InvocationHandler handler = new PortletObjectProxy(proxiedObject);
        return Proxy.newProxyInstance(classLoader, proxyInterfaces, handler);
    }

    private PortletObjectProxy(Object portletObject)
    {
        this.portletObject = portletObject;
        
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
        boolean autoSwitchToEditMode = false;
        
        if (this.genericPortletInvocable)
        {
            PortletMode mode = request.getPortletMode();
            
            if (JetspeedActions.EDIT_DEFAULTS_MODE.equals(mode))
            {
                if (!isSupportingEditDefaultsMode((GenericPortlet) this.portletObject))
                {
                    autoSwitchToEditMode = true;
                }
            }
        }
        
        if (autoSwitchToEditMode)
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
}
