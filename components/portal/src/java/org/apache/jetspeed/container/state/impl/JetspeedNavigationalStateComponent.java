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

import java.lang.reflect.Constructor;
import java.util.Enumeration;
import java.util.HashMap;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.container.state.FailedToCreateNavStateException;
import org.apache.jetspeed.container.state.FailedToCreatePortalUrlException;
import org.apache.jetspeed.container.state.NavigationalState;
import org.apache.jetspeed.container.state.NavigationalStateComponent;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.util.ArgUtil;

/**
 * JetspeedNavigationalStateComponent
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */
public class JetspeedNavigationalStateComponent implements NavigationalStateComponent
{
    private String navClassName = null;
    private String urlClassName = null;
    private Class navClass = null;
    private Class urlClass = null;
    private Class navCodecClass = null;
    private NavigationalStateCodec navStateCodec = null;

    // maps containing allowed PortletMode and WindowState objects on their lowercase name
    // ensuring only allowed, and always the same objects are returned and allowing comparision by value
    private HashMap supportedPortletModes = new HashMap();
    private HashMap supportedWindowStates = new HashMap();

    private final static Log log = LogFactory.getLog(JetspeedNavigationalStateComponent.class);

    /**
     * @param navClassName
     *            name of the class implementing Navigational State instances
     * @param urlClassName
     *            name of the class implementing Portal URL instances
     * @param navCodecClassName
     *            name of the class implementing Navigational State Codec instance
     * @throws ClassNotFoundException
     *             if <code>navClassName</code> or <code>urlClassName</code>
     *             do not exist.
     */
    public JetspeedNavigationalStateComponent( String navClassName, String urlClassName, String navCodecClassName )
            throws ClassNotFoundException
    {
        ArgUtil.assertNotNull(String.class, navClassName, this);
        ArgUtil.assertNotNull(String.class, urlClassName, this);
        ArgUtil.assertNotNull(String.class, navCodecClassName, this);
        this.urlClass = Class.forName(urlClassName);
        this.navClass = Class.forName(navClassName);
        this.navCodecClass = Class.forName(navCodecClassName);
        this.navClassName = navClassName;
        this.urlClassName = urlClassName;

        Enumeration portletModesEnum = Jetspeed.getContext().getSupportedPortletModes();
        PortletMode portletMode;
        while ( portletModesEnum.hasMoreElements() )
        {
            portletMode = (PortletMode)portletModesEnum.nextElement();
            supportedPortletModes.put(portletMode.toString(), portletMode);
        }
        Enumeration windowStatesEnum = Jetspeed.getContext().getSupportedWindowStates();
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
        NavigationalState state = null;

        try
        {
            if ( navStateCodec == null )
            {
                navStateCodec = (NavigationalStateCodec)navCodecClass.newInstance();
            }
            Constructor constructor = navClass.getConstructor(new Class[]{NavigationalStateCodec.class});
            state = (NavigationalState) constructor.newInstance(new Object[]{navStateCodec});
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new FailedToCreateNavStateException("Error invoking create() method.  "
                    + "There is more than likely a problem invoking navClass " + navClassName);
        }

        return state;
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
        PortalURL url = null;

        try
        {
            Constructor constructor = urlClass.getConstructor(new Class[]{HttpServletRequest.class, String.class,
                    NavigationalState.class});

            url = (PortalURL) constructor.newInstance(new Object[]{request, characterEncoding, create()});
            return url;
        }
        catch (Exception e)
        {
            e.printStackTrace();
           throw new FailedToCreatePortalUrlException("Error invoking createURL() method.  "
                    + "There is more than likely a problem invoking urlClass " + urlClassName);
        }
    }

    public WindowState lookupWindowState( String name )
    {
        return (WindowState)supportedWindowStates.get(name.toLowerCase());
    }

    public PortletMode lookupPortletMode( String name )
    {
        return (PortletMode)supportedPortletModes.get(name.toLowerCase());
    }
}
