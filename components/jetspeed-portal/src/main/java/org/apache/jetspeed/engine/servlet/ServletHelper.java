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
package org.apache.jetspeed.engine.servlet;

import java.io.File;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;

/**
 * Servlet Helper functions
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 */
public class ServletHelper
{
    public static final String CONFIG_NAMESPACE = "org.apache.jetspeed";

    /** Default Value for the Logging Directory, relative to the webroot */
    public static final String LOGGING_ROOT_DEFAULT = "/logs";
    public static final String LOGGING_ROOT = "loggingRoot";

    /**
     * Used to get the real path of configuration and resource
     * information. 
     *
     * @param path path translated to the application root
     * @return the real path
     */
    public static String getRealPath(ServletConfig config, String path)
    {
        if (path.startsWith("/"))
        {
            path = path.substring(1);
        }
        else if (path.startsWith("file:"))
        {
            return new File(path.substring(5)).getAbsolutePath();
        }
        return new File(config.getServletContext().getRealPath(""), path).getAbsolutePath();
    }

    /**
     * Finds the specified servlet configuration/initialization
     * parameter, looking first for a servlet-specific parameter, then
     * for a global parameter, and using the provided default if not
     * found.
     */
    public static final String findInitParameter(ServletContext context,
                                                    ServletConfig config,
                                                    String name,
                                                    String defaultValue)
    {
        String path = null;

        // Try the name as provided first.
        boolean usingNamespace = name.startsWith(CONFIG_NAMESPACE);
        while (true)
        {
            path = config.getInitParameter(name);
            if (StringUtils.isEmpty(path))
            {
                path = context.getInitParameter(name);
                if (StringUtils.isEmpty(path))
                {
                    // The named parameter didn't yield a value.
                    if (usingNamespace)
                    {
                        path = defaultValue;
                    }
                    else
                    {
                        // Try again using Jetspeed's namespace.
                        name = CONFIG_NAMESPACE + '.' + name;
                        usingNamespace = true;
                        continue;
                    }
                }
            }
            break;
        }

        return path;
    }

    /**
     * Create any directories that might be needed during
     *
     */
    public static void createRuntimeDirectories(ServletContext context,
                                                 ServletConfig config)
        throws ServletException
    {
        String path = findInitParameter(context, config, LOGGING_ROOT, LOGGING_ROOT_DEFAULT);
        File logDir = new File(getRealPath(config, path));
        if (!logDir.exists())
        {
            // Create the logging directory
            if (!logDir.mkdirs())
            {
                throw new ServletException("Cannot create directory for logs!");
            }
        }
    }
 }