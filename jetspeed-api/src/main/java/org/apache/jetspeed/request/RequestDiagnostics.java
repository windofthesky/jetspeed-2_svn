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

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.apache.jetspeed.container.url.PortalURL;

/**
 * Base Portal Request Diagnostics view to be provided to a pluggable DiagnosticsServlet/JSP
 * to handle a Portal Request error.
 * <p>
 * Because this RequestDiagnostics view might get stored (temporarily) in the session and the
 * DiagnosticsServlet might get redirected too, this view captures most common and important
 * request state as Serializable data.
 * </p>
 * <p>
 * In addition, there is also an auxillary attributes Map which can be used to add additional
 * data to be passed on to the DiagnosticsServlet without immediate need to extend this interface,
 * although that's possible too of course.
 * </p>
 * @version $Id$
 */
public interface RequestDiagnostics extends Serializable
{
    String getId();
    String getTimeRecorded();
    String getThread();
    boolean isInternal();
    void setInternal(boolean internal);
    String getServer();
    void setServer(String value);
    String getRemoteAddr();
    void setRemoteAddr(String value);
    String getLocalAddr();
    void setLocalAddr(String value);
    String getContextPath();
    void setContextPath(String value);
    String getServletPath();
    void setServletPath(String value);
    String getPathInfo();
    void setPathInfo(String value);
    String getQueryString();
    void setQueryString(String value);
    String getRequestURI();
    void setRequestURI(String value);
    String getRequestMethod();
    void setRequestMethod(String value);
    String getPagePath();
    void setPagePath(String value);
    String getPageId();
    void setPageId(String value);
    String getPath();
    PortalURL.URLType getPortalURLType();
    void setPortalURLType(PortalURL.URLType value);
    String getPortletApplicationName();
    void setPortletApplicationName(String value);
    String getPortletName();
    void setPortletName(String value);
    String getPortletWindowId();
    void setPortletWindowId(String value);
    String getUserPrincipalName();
    void setUserPrincipalName(String value);
    Throwable getException();
    void setException(Throwable value);
    Throwable getCause();
    void setCause(Throwable value);
    String getErrorMessage();
    void setErrorMessage(String value);
    String getErrorDescription();
    void setErrorDescription(String value);
    boolean isAttributesEmpty();
    Map<String, Serializable> getAttributes();
    void logAsInfo();
    void logAsWarning();
    void logAsError();
    void logAsTrace();
    void logAsDebug();
}
