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
package org.apache.jetspeed.request;

import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RequestDiagnosticsFactory provides a static factory method for creating a RequestDiagnostics instance.
 * <p>
 * In addition, it provides some convenience methods for filling in part of the diagnostics state.
 * </p>
 * @version $Id$
 *
 */
public class RequestDiagnosticsFactory
{
    private static final Logger logger = LoggerFactory.getLogger("diagnostics");
    
    static Logger getLogger()
    {
        return logger;
    }
    
    public static boolean isInfoLoggingEnabled()
    {
        return logger.isInfoEnabled();
    }

    public static boolean isWarnLoggingEnabled()
    {
        return logger.isWarnEnabled();
    }
    
    public static boolean isErrorLoggingEnabled()
    {
        return logger.isErrorEnabled();
    }

    public static boolean isDebugLoggingEnable()
    {
        return logger.isDebugEnabled();
    }

    public static boolean isTraceLoggingEnable()
    {
        return logger.isTraceEnabled();
    }
    
    public static RequestDiagnostics newRequestDiagnostics()
    {
        return new RequestDiagnosticsImpl();
    }
    
    public static void fillInPortletWindow(RequestDiagnostics rd, PortletWindow pw, Throwable e)
    {
        if (pw != null)
        {
            rd.setPortletWindowId(pw.getWindowId());
            PortletDefinition pd = pw.getPortletDefinition();
            rd.setPortletApplicationName(pd.getApplication().getName());
            rd.setPortletName(pd.getPortletName());
        }
        if (e != null)
        {
            fillInException(rd, e);
        }
    }
    
    public static void fillInRequestContext(RequestDiagnostics rd, HttpServletRequest req, RequestContext context, Throwable e)
    {
        if (req != null)
        {
            if (rd.getServer() == null)
            {           
                StringBuilder sb = new StringBuilder();
                sb.append(req.getScheme()).append("://").append(req.getServerName()).append(":").append(req.getServerPort());
                rd.setServer(sb.toString());
            }
            if (rd.getRemoteAddr() == null)
            {
                rd.setRemoteAddr(req.getRemoteAddr());
            }
            if (rd.getLocalAddr() == null)
            {
                rd.setLocalAddr(req.getLocalAddr());
            }
            if (rd.getContextPath() == null)
            {
                rd.setContextPath(req.getContextPath());
            }
            if (rd.getServletPath() == null)
            {
                rd.setServletPath(req.getServletPath());            
            }
            if (rd.getPathInfo() == null)
            {
                rd.setPathInfo(req.getPathInfo());
            }
            if (rd.getQueryString() == null)
            {
                rd.setQueryString(req.getQueryString());
            }
            if (rd.getRequestURI() == null)
            {
                rd.setRequestURI(req.getRequestURI());
            }
            if (rd.getRequestMethod() == null)
            {
                rd.setRequestMethod(req.getMethod());
            }
            if (rd.getPagePath() == null && context != null && context.getPortalURL() != null)
            {
                rd.setPagePath(context.getPortalURL().getPath());
            }
            if (rd.getPageId() == null && context != null && context.getPage() != null)
            {
                rd.setPageId(context.getPage().getId());
            }
            if (rd.getPortalURLType() == null && context != null && context.getPortalURL() != null)
            {
                rd.setPortalURLType(context.getPortalURL().getNavigationalState().getURLType());
            }
            if (rd.getUserPrincipalName() == null && req.getUserPrincipal() != null)
            {
                rd.setUserPrincipalName(req.getUserPrincipal().getName());
            }
        }
        if (e != null)
        {
            fillInException(rd, e);
        }
    }
    
    public static void fillInException(RequestDiagnostics rd, Throwable e)
    {
        if (e != null)
        {
            if (rd.getException() == null)
            {
                rd.setException(e);
            }
            if (rd.getCause() == null)
            {
                Throwable t = rd.getException();
                while (t.getCause() != null)
                {
                    t = t.getCause();
                }
                rd.setCause(t);
            }
            if (rd.getErrorMessage() == null)
            {
                rd.setErrorMessage(rd.getCause().toString());
            }
        }
    }
}
