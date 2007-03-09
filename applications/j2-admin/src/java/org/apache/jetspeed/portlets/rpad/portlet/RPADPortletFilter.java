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
package org.apache.jetspeed.portlets.rpad.portlet;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.portlets.rpad.RPADException;
import org.apache.jetspeed.portlets.rpad.RepositoryManager;
import org.apache.portals.bridges.portletfilter.PortletFilter;
import org.apache.portals.bridges.portletfilter.PortletFilterChain;
import org.apache.portals.bridges.portletfilter.PortletFilterConfig;

public class RPADPortletFilter implements PortletFilter
{
    /**
     * Logger for this class
     */
    private static final Log log = LogFactory.getLog(RPADPortletFilter.class);

    protected final static String CONFIG_FILE = "config-file";

    protected final static String DEFAULT_CONFIG_FILE = "/WEB-INF/rpad-config.xml";

    protected final static String WEBAPP_ROOT_PREFIX = "${webapp}";

    public void destroy()
    {
        try
        {
            RepositoryManager.getInstance().store();
        }
        catch (RPADException e)
        {
            log.error("Could not store the configuration.", e);
        }
    }

    public void init(PortletFilterConfig filterConfig) throws PortletException
    {
        PortletConfig portletConfig = filterConfig.getPortletConfig();
        PortletContext portletContext = portletConfig.getPortletContext();
        String configFile = portletConfig.getInitParameter(CONFIG_FILE);
        if (configFile == null)
        {
            configFile = portletContext.getRealPath(DEFAULT_CONFIG_FILE);
        }
        else if (configFile.startsWith(WEBAPP_ROOT_PREFIX))
        {
            configFile = portletContext.getRealPath(configFile
                    .substring(WEBAPP_ROOT_PREFIX.length()));
        }

        if (log.isDebugEnabled())
        {
            log.debug("init(PortletConfig) - configFile=" + configFile);
        }

        // Create RepositoryManager
        try
        {
            RepositoryManager.init(configFile);
        }
        catch (Exception e)
        {
            throw new PortletException(
                    "Could not create RepositoryManager. The config file is "
                            + configFile, e);
        }
    }

    public void processActionFilter(ActionRequest request,
            ActionResponse response, PortletFilterChain chain)
            throws PortletException, IOException
    {
        chain.processActionFilter(request, response);
    }

    public void renderFilter(RenderRequest request, RenderResponse response,
            PortletFilterChain chain) throws PortletException, IOException
    {
        chain.renderFilter(request, response);
    }

}
