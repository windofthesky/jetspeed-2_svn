/*
 * Copyright 2000-2004 The Apache Software Foundation.
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
package org.apache.jetspeed.engine.servlet;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.engine.core.PortalControlParameter;
import org.apache.pluto.om.window.PortletWindow;

/**
 * This request wrappers the servlet request and is used
 * within the container to communicate to the invoked servlet.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class ServletRequestImpl extends HttpServletRequestWrapper
{

    PortalControlParameter control = null;

    PortletWindow portletWindow = null;

    private Map portletParameters;

    public ServletRequestImpl(javax.servlet.http.HttpServletRequest servletRequest, PortletWindow window)
    {
        super(servletRequest);

        this.portletWindow = window;
        // control = new PortalControlParameter(new PortalURLImpl(servletRequest));
        control = new PortalControlParameter(Jetspeed.getCurrentRequestContext().getRequestedPortalURL());

    }

    private HttpServletRequest _getHttpServletRequest()
    {
        return (HttpServletRequest) super.getRequest();
    }

    //  HttpServletRequestWrapper overlay

    public String getContentType()
    {
        String contentType = "text/html";
        if (getCharacterEncoding() != null)
        {
            contentType += ";" + getCharacterEncoding();
        }
        return contentType;
    }

    //  ServletRequestWrapper overlay

    public String getParameter(String name)
    {
        return (String) this.getParameterMap().get(name);

    }

    public Map getParameterMap()
    {
        /*
         * DST TODO: REMOVING this code for now so that FORM post parameters are passed on.
         * I will schedule time to rewrite and ensure it works with the spec 
         * 
        //get control params
        if (portletParameters == null)
        {

            portletParameters = new HashMap();

            Iterator iterator = control.getRenderParamNames(portletWindow);
            while (iterator.hasNext())
            {
                String name = (String) iterator.next();

                String[] values = control.getRenderParamValues(portletWindow, name);

                portletParameters.put(name, values);

            }

            //get request params
            String pid = control.getPIDValue();
            String wid = portletWindow.getId().toString();
            if (pid.equals(wid))
            {
                for (Enumeration parameters = super.getParameterNames(); parameters.hasMoreElements();)
                {
                    String paramName = (String) parameters.nextElement();
                    String[] paramValues = (String[]) super.getParameterValues(paramName);
                    String[] values = (String[]) portletParameters.get(paramName);

                    if (values != null)
                    {
                        String[] temp = new String[paramValues.length + values.length];
                        System.arraycopy(paramValues, 0, temp, 0, paramValues.length);
                        System.arraycopy(values, 0, temp, paramValues.length, values.length);
                        paramValues = temp;
                    }
                    portletParameters.put(paramName, paramValues);
                }
            }
        }

        return Collections.unmodifiableMap(portletParameters);
        */
        return Collections.unmodifiableMap(super.getParameterMap());
    }

    public Enumeration getParameterNames()
    {
        return Collections.enumeration(this.getParameterMap().keySet());
    }

    public String[] getParameterValues(String name)
    {
        return (String[]) this.getParameterMap().get(name);
    }

}
