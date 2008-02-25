/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.container;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ListResourceBundle;

import javax.servlet.ServletConfig;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;

import org.apache.pluto.om.common.Parameter;
import org.apache.pluto.om.common.ParameterSet;
import org.apache.pluto.om.portlet.PortletDefinition;

/**
 * Implements the Portlet API Portlet Config class
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 */
public class JetspeedPortletConfig implements PortletConfig, InternalPortletConfig
{
    private ServletConfig servletConfig;
    private PortletContext portletContext;
    private PortletDefinition portletDefinition;
    private ResourceBundle resources;

    public JetspeedPortletConfig(ServletConfig servletConfig, PortletContext portletContext, PortletDefinition portletEntity)
    {
        this.servletConfig = servletConfig;
        this.portletContext = portletContext;
        this.portletDefinition = portletEntity;
        this.resources = new Resources(this);
    }

    public String getPortletName()
    {
        return portletDefinition.getName();
    }

    public PortletContext getPortletContext()
    {
        return portletContext;
    }

    public ResourceBundle getResourceBundle(Locale locale)
    {
        return this.resources;
    }

    public String getInitParameter(java.lang.String name)
    {
        ParameterSet parameters = portletDefinition.getInitParameterSet();

        Parameter param = parameters.get(name);

        if (param != null)
        {
            return param.getValue();
        }

        return null;
    }

    public Enumeration getInitParameterNames()
    {
        return new java.util.Enumeration()
        {
            private ParameterSet parameters = portletDefinition.getInitParameterSet();
            private Iterator iterator = parameters.iterator();

            public boolean hasMoreElements()
            {
                return iterator.hasNext();
            }

            public Object nextElement()
            {
                if (iterator.hasNext())
                    return ((Parameter) iterator.next()).getName();
                else
                    return null;
            }
        };

    }

    //  internal portlet config implementation

    public javax.servlet.ServletConfig getServletConfig()
    {
        return servletConfig;
    }

    public PortletDefinition getPortletDefinition()
    {
        return portletDefinition;
    }

    private static class Resources extends ListResourceBundle
    {
        private Object[][] resources = null;

        public Resources(PortletConfig config) // TODO: PortletInfo info
        {
            // once Portlet Info is sorted out and implemented, change this to use
            // portlet info
            resources = new Object[][] { { "javax.portlet.title", config.getPortletName()}, {
                    "javax.portlet.short-title", config.getPortletName()
                    }, {
                    "javax.portlet.keywords", "no keywords" }
            };
        }

        public Object[][] getContents()
        {
            return resources;
        }
    }

}