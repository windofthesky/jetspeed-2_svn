/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.container.state.impl;

import java.util.Enumeration;
import java.util.HashMap;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.PortalContext;
import org.apache.jetspeed.container.state.FailedToCreateNavStateException;
import org.apache.jetspeed.container.state.NavigationalState;
import org.apache.jetspeed.container.state.NavigationalStateComponent;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.util.ArgUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * JetspeedNavigationalStateComponent
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */
public class JetspeedNavigationalStateComponent implements NavigationalStateComponent, BeanFactoryAware
{
    private final String navBeanName;
    private final String urlBeanName;    
    
    // maps containing allowed PortletMode and WindowState objects on their lowercase name
    // ensuring only allowed, and always the same objects are returned and allowing comparision by value
    private final HashMap supportedPortletModes = new HashMap();
    private final HashMap supportedWindowStates = new HashMap();

    private final static Log log = LogFactory.getLog(JetspeedNavigationalStateComponent.class);
    private BeanFactory beanFactory;
    

    /**
     * @param navBeanName
     *            name of the bean implementing Navigational State instances
     * @param urlBeanName
     *            name of the bean implementing Portal URL instances
     * @param navCodecBeanName
     *            name of the bean implementing Navigational State Codec instance
     * @throws ClassNotFoundException
     *             if <code>navClassName</code> or <code>urlClassName</code>
     *             do not exist.
     */
    public JetspeedNavigationalStateComponent( String  navBeanName, String urlBeanName, PortalContext portalContext )
            throws ClassNotFoundException
    {
        ArgUtil.assertNotNull(String.class, navBeanName, this);
        ArgUtil.assertNotNull(String.class, urlBeanName, this);        
        this.navBeanName = navBeanName;
        this.urlBeanName = urlBeanName;        

        Enumeration portletModesEnum = portalContext.getSupportedPortletModes();
        PortletMode portletMode;
        while ( portletModesEnum.hasMoreElements() )
        {
            portletMode = (PortletMode)portletModesEnum.nextElement();
            supportedPortletModes.put(portletMode.toString(), portletMode);
        }
        Enumeration windowStatesEnum = portalContext.getSupportedWindowStates();
        WindowState windowState;
        while ( windowStatesEnum.hasMoreElements() )
        {
            windowState = (WindowState)windowStatesEnum.nextElement();
            supportedWindowStates.put(windowState.toString(), windowState);
        }
    }

    /**
     *
     * <p>
     * create
     * </p>
     *
     * @see org.apache.jetspeed.container.state.NavigationalStateComponent#create(org.apache.jetspeed.request.RequestContext)
     * @return @throws
     *         FailedToCreateNavStateException if the nav state could not be
     *         created. Under normal circumstances, this should not happen.
     */
    public NavigationalState create() throws FailedToCreateNavStateException
    {
        try
        {
            return (NavigationalState) beanFactory.getBean(navBeanName, NavigationalState.class);
        }
        catch (BeansException e)
        {           
            throw new FailedToCreateNavStateException("Spring failed to create the NavigationalState bean.", e);
        }
    }

   /**
    *
    * <p>
    * createURL
    * </p>
    *
    * @see org.apache.jetspeed.container.state.NavigationalStateComponent#createURL(org.apache.jetspeed.request.RequestContext)
    * @param context
    * @return
    */
    public PortalURL createURL( HttpServletRequest request, String characterEncoding )
    {
        PortalURL url = (PortalURL) beanFactory.getBean(urlBeanName, PortalURL.class);
        url.setRequest(request);
        url.setCharacterEncoding(characterEncoding);
        return url;
    }

    public WindowState lookupWindowState( String name )
    {
        return (WindowState)supportedWindowStates.get(name.toLowerCase());
    }

    public PortletMode lookupPortletMode( String name )
    {
        return (PortletMode)supportedPortletModes.get(name.toLowerCase());
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException
    {
        this.beanFactory = beanFactory;        
    }
    

}
