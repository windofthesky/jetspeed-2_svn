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
package org.apache.struts.portlet;

import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;


/**
 * PortletServletConfigImpl
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
class PortletServletConfigImpl implements ServletConfig
{
    private ServletConfig config;
    private ServletContext context;
    public PortletServletConfigImpl(ServletConfig config)
    {
        this.config = config;
    }
    public String getInitParameter(String arg0)
    {
        return config.getInitParameter(arg0);
    }
    public Enumeration getInitParameterNames()
    {
        return config.getInitParameterNames();
    }
    public synchronized ServletContext getServletContext()
    {
        if (context == null)
            context = new PortletServletContextImpl(config.getServletContext());
        return context;
    }
    public String getServletName()
    {
        return config.getServletName();
    }
    public String toString()
    {
        return config.toString();
    }
}