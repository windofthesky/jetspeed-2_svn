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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.apache.jetspeed.container.url.PortalURL;
import org.slf4j.Logger;

/**
 * Default implementation of the RequestDiagnostics
 * @version $Id$
 *
 */
public class RequestDiagnosticsImpl implements RequestDiagnostics
{
    private static final long serialVersionUID = -2516862911706710811L;
    
    private static final Logger logger = RequestDiagnosticsFactory.getLogger();
    
    protected static enum LogLevel { INFO, WARNING, ERROR, DEBUG, TRACE };
    
    private static long idBase = System.currentTimeMillis();
    
    protected static final String LOG_LINE_PREFIX = "  ";
    protected static final String LOG_FIELD_POSTFIX = ": ";
    protected static final String DATE_FORMAT = "z yyyy-MM-dd HH:mm:ss:SSS";
    protected static final String LINE_SEPARATOR = System.getProperty("line.separator");
    
    private final String id;
    private final String timeRecorded;
    private final String thread;
    private boolean internal;
    private String server;
    private String remoteAddr;
    private String localAddr;
    private String contextPath;
    private String servletPath;
    private String pathInfo;
    private String queryString;
    private String requestURI;
    private String requestMethod;
    private String pagePath;
    private String pageId;
    private PortalURL.URLType portalURLType;
    private String portletApplicationName;
    private String portletName;
    private String portletWindowId;
    private String userPrincipalName;
    private Throwable exception;
    private Throwable cause;
    private String errorMessage;
    private String errorDescription;
    private Map<String,Serializable> attributes;

    private transient StringBuilder logBuilder;
    private transient LogLevel logLevel;
    
    public RequestDiagnosticsImpl()
    {
        this(nextId());
    }
    
    private static String nextId()
    {
        long nextId;
        synchronized (RequestDiagnosticsImpl.class)
        {
            nextId = idBase++;
        }
        // create shortest possible long.toString() representation
        return Long.toString(nextId, Character.MAX_RADIX).toUpperCase();
    }
    
    protected RequestDiagnosticsImpl(String id)
    {
        this.id = id;
        this.timeRecorded = formatDate(new Date());
        this.thread = Thread.currentThread().toString();
    }
    
    public String getId()
    {
        return id;
    }
    public String getTimeRecorded()
    {
        return timeRecorded;
    }
    public String getThread()
    {
        return thread;
    }
    public boolean isInternal()
    {
        return internal;
    }
    public void setInternal(boolean internal)
    {
        this.internal = internal;
    }
    public String getServer()
    {
        return server;
    }
    public void setServer(String server)
    {
        this.server = server;
    }
    public String getRemoteAddr()
    {
        return remoteAddr;
    }
    public void setRemoteAddr(String remoteAddr)
    {
        this.remoteAddr = remoteAddr;
    }
    public String getLocalAddr()
    {
        return localAddr;
    }
    public void setLocalAddr(String localAddr)
    {
        this.localAddr = localAddr;
    }
    public String getContextPath()
    {
        return contextPath;
    }
    public void setContextPath(String contextPath)
    {
        this.contextPath = contextPath;
    }
    public String getServletPath()
    {
        return servletPath;
    }
    public void setServletPath(String servletPath)
    {
        this.servletPath = servletPath;
    }
    public String getPathInfo()
    {
        return pathInfo;
    }
    public void setPathInfo(String pathInfo)
    {
        this.pathInfo = pathInfo;
    }
    public String getQueryString()
    {
        return queryString;
    }
    public void setQueryString(String queryString)
    {
        this.queryString = queryString;
    }
    public String getRequestURI()
    {
        return requestURI;
    }
    public void setRequestURI(String requestURI)
    {
        this.requestURI = requestURI;
    }
    public String getRequestMethod()
    {
        return requestMethod;
    }
    public void setRequestMethod(String method)
    {
        this.requestMethod = method;
    }
    public String getPagePath()
    {
        return pagePath;
    }
    public void setPagePath(String pagePath)
    {
        this.pagePath = pagePath;
    }
    public String getPageId()
    {
        return pageId;
    }
    public void setPageId(String pageId)
    {
        this.pageId = pageId;
    }
    public String getPath()
    {
        if (contextPath != null)
        {
            StringBuilder sb = new StringBuilder(contextPath);
            if (servletPath != null)
            {
                sb.append(servletPath);
            }
            if (pagePath != null)
            {
                sb.append(pagePath);
            }
            return sb.toString();
        }
        return null;
    }
    public PortalURL.URLType getPortalURLType()
    {
        return portalURLType;
    }
    public void setPortalURLType(PortalURL.URLType portalURLType)
    {
        this.portalURLType = portalURLType;
    }
    public String getPortletApplicationName()
    {
        return portletApplicationName;
    }
    public void setPortletApplicationName(String portletApplicationName)
    {
        this.portletApplicationName = portletApplicationName;
    }
    public String getPortletName()
    {
        return portletName;
    }
    public void setPortletName(String portletName)
    {
        this.portletName = portletName;
    }
    public String getPortletWindowId()
    {
        return portletWindowId;
    }
    public void setPortletWindowId(String portletWindowId)
    {
        this.portletWindowId = portletWindowId;
    }
    public String getUserPrincipalName()
    {
        return userPrincipalName;
    }
    public void setUserPrincipalName(String userPrincipalName)
    {
        this.userPrincipalName = userPrincipalName;
    }
    public Throwable getException()
    {
        return exception;
    }
    public void setException(Throwable exception)
    {
        this.exception = exception;
    }
    public Throwable getCause()
    {
        return cause;
    }
    public void setCause(Throwable cause)
    {
        this.cause = cause;
    }
    public String getErrorMessage()
    {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }    
    public String getErrorDescription()
    {
        return errorDescription != null ? errorDescription : errorMessage;
    }
    public void setErrorDescription(String value)
    {
        this.errorDescription = value;
    }
    public boolean isAttributesEmpty()
    {
        return attributes == null || attributes.isEmpty();
    }
    public Map<String, Serializable> getAttributes()
    {
        if (attributes == null)
        {
            attributes = new TreeMap<String,Serializable>();
        }
        return attributes;
    }
    
    public void logAsInfo()
    {
        if (logger.isInfoEnabled())
        {
            log(LogLevel.INFO);
        }
    }

    public void logAsWarning()
    {
        if (logger.isWarnEnabled())
        {
            log(LogLevel.WARNING);
        }
    }
    
    public void logAsError()
    {
        if (logger.isErrorEnabled())
        {
            log(LogLevel.ERROR);
        }
    }

    public void logAsDebug()
    {
        if (logger.isDebugEnabled())
        {
            log(LogLevel.DEBUG);
        }
    }

    public void logAsTrace()
    {
        if (logger.isTraceEnabled())
        {
            log(LogLevel.TRACE);
        }
    }
    
    protected String formatDate(Date date)
    {
        return new SimpleDateFormat(DATE_FORMAT).format(date);
    }

    protected void createLog(LogLevel level)
    {
        logBuilder = new StringBuilder();
        logLevel = level;
    }
    
    protected LogLevel getCurrentLogLevel()
    {
        return logLevel;
    }
    
    protected void initLog()
    {
        logBuilder.append(LINE_SEPARATOR).append(LINE_SEPARATOR);
    }
    
    protected void writeLog()
    {
        if (logBuilder != null && logBuilder.length() > 0)
        {
            LogLevel level = getCurrentLogLevel();
            if (LogLevel.INFO == level)
            {
                logger.info(logBuilder.toString());
            }
            else if (LogLevel.WARNING == level)
            {
                logger.warn(logBuilder.toString());
            }
            else if (LogLevel.ERROR == level)
            {
                logger.error(logBuilder.toString());
            }
            else if (LogLevel.DEBUG == level)
            {
                logger.debug(logBuilder.toString());
            }
            else if (LogLevel.TRACE == level)
            {
                logger.trace(logBuilder.toString());
            }
        }
    }
    
    protected void finalizeLog()
    {
    }
    
    protected void closeLog()
    {
        logBuilder = null;
        logLevel = null;
    }
    
    protected void log(LogLevel level)
    {
        try
        {            
            createLog(level);
            initLog();
            logElements();
            finalizeLog();
            writeLog();
        }
        finally
        {
            closeLog();
        }
    }

    protected void logElements()
    {
        logId();
        logTimeRecorded();
        logThread();
        logInternal();
        logUserPrincipalName();
        logServer();
        logRemoteAddr();
        logLocalAddr();
        logContextPath();
        logServletPath();
        logPathInfo();
        logQueryString();
        logRequestURI();
        logPortalURLType();
        logRequestMethod();
        logPagePath();
        logPageId();
        logPortletApplicationName();
        logPortletName();
        logPortletWindowId();
        logErrorDescription();
        logErrorMessage();
        logException();
        logCause();
        logAttributes();
    }
    
    protected void logId()
    {
        addElementToLog("Id", id, true);
    }
    
    protected void logTimeRecorded()
    {
        addElementToLog("Time", timeRecorded, true);
    }
    
    protected void logThread()
    {
        addElementToLog("Thread", thread, true);
    }
    
    protected void logInternal()
    {
        addElementToLog("Internal", Boolean.toString(internal), true);
    }
    
    protected void logServer()
    {
        addElementToLog("Server", server, false);
    }
    
    protected void logRemoteAddr()
    {
        addElementToLog("Remote IP address", remoteAddr, false);
    }
    
    protected void logLocalAddr()
    {
        addElementToLog("Local IP address", localAddr, false);
    }
    
    protected void logContextPath()
    {
        addElementToLog("Context path", contextPath, false);
    }
    
    protected void logServletPath()
    {
        addElementToLog("Servlet path", servletPath, false);
    }
    
    protected void logPathInfo()
    {
        addElementToLog("Path info", pathInfo, false);
    }
    
    protected void logQueryString()
    {
        addElementToLog("Query string", queryString, false);
    }
    
    protected void logRequestURI()
    {
        addElementToLog("Request URI", requestURI, false);
    }
    
    protected void logRequestMethod()
    {
        addElementToLog("Request method", requestMethod, false);
    }
    
    protected void logPagePath()
    {
        addElementToLog("Page path", pagePath, false);
    }
    
    protected void logPageId()
    {
        addElementToLog("Page id", pageId, false);
    }
    
    protected void logPortalURLType()
    {
        if (portalURLType != null)
        {
            addElementToLog("URL type", portalURLType.toString(), true);
        }
    }
    
    protected void logPortletApplicationName()
    {
        addElementToLog("Portlet application", portletApplicationName, false);
    }
    
    protected void logPortletName()
    {
        addElementToLog("Portlet", portletName, false);
    }
    
    protected void logPortletWindowId()
    {
        addElementToLog("Portlet window id", portletWindowId, false);
    }
    
    protected void logUserPrincipalName()
    {
        addElementToLog("User", userPrincipalName, false);
    }
    
    protected void logErrorDescription()
    {
        addElementToLog("Error description", errorDescription, false);
    }
    
    protected void logErrorMessage()
    {
        addElementToLog("Error message", errorMessage, false);
    }
    
    protected void logException()
    {
        if (exception != null)
        {
            addElementExceptionToLog("Exception", exception);
        }
    }
    
    protected void logCause()
    {
        if (cause != null && exception == null)
        {
            addElementExceptionToLog("Cause", cause);
        }
    }
    
    protected void logAttributes()
    {
        if (!isAttributesEmpty())
        {
            addElementToLog("Attributes", null, true);
            int seq = 0;
            for (Map.Entry<String,Serializable> entry : getAttributes().entrySet())
            {
                addElementDetailToLog("Attributes", 
                                      entry.getKey(), 
                                      entry.getValue() != null ? entry.getValue().toString() : null, 
                                      seq++);
            }
        }
    }
    

    protected void addLinePrefixToLog(int num)
    {
        for (int i = 0; i < num; i++)
        {
            logBuilder.append(LOG_LINE_PREFIX);
        }
    }
    
    protected void addElementToLog(String element, String value, boolean empty)
    {
        if (empty || value != null)
        {
            logBuilder.append(LOG_LINE_PREFIX);
            logBuilder.append(element);
            logBuilder.append(LOG_FIELD_POSTFIX);
            if (value != null)
            {
                logBuilder.append(value);
            }
            logBuilder.append(LINE_SEPARATOR);
        }
    }
    
    protected void addElementDetailToLog(String element, String detail, String value, int seq)
    {
        logBuilder.append(LOG_LINE_PREFIX);
        logBuilder.append(LOG_LINE_PREFIX);
        logBuilder.append(detail);
        logBuilder.append(LOG_FIELD_POSTFIX);
        if (value != null)
        {
            logBuilder.append(value);
        }
        logBuilder.append(LINE_SEPARATOR);
    }    
    
    protected void addElementExceptionToLog(String element, Throwable t)
    {
        try
        {
            logBuilder.append(LOG_LINE_PREFIX);
            logBuilder.append(element);
            logBuilder.append(LOG_FIELD_POSTFIX);
            if (t != null)
            {
                logBuilder.append(t.toString());
                logBuilder.append(LINE_SEPARATOR);
                
                // Throwable logging borrowed from Harmony Throwable.java
                StackTraceElement[] stackTrace = t.getStackTrace();
                if (stackTrace != null && stackTrace.length != 0)
                {
                    for (int i = 0; i < stackTrace.length; i++)
                    {
                        logBuilder.append(LOG_LINE_PREFIX).append(LOG_LINE_PREFIX);
                        logBuilder.append("at: ").append(stackTrace[i].toString());
                        logBuilder.append(LINE_SEPARATOR);
                    }
                }
                else
                {
                    logBuilder.append(LOG_LINE_PREFIX).append(LOG_LINE_PREFIX);
                    logBuilder.append("<no stack trace available>");
                    logBuilder.append(LINE_SEPARATOR);
                }
                Throwable wCause = t;
                int prefixLevel = 2;
                while (wCause != wCause.getCause() && wCause.getCause() != null)
                {
                    StackTraceElement[] parentStackTrace = wCause.getStackTrace();
                    wCause = wCause.getCause();
                    addLinePrefixToLog(prefixLevel);
                    logBuilder.append("caused by: ").append(wCause.toString());
                    logBuilder.append(LINE_SEPARATOR);
                    prefixLevel++;
                    StackTraceElement[] causeStackTrace = wCause.getStackTrace();
                    if (causeStackTrace != null && causeStackTrace.length != 0)
                    {
                        if (parentStackTrace == null || parentStackTrace.length == 0)
                        {
                            for (int i = 0; i < causeStackTrace.length; i++)
                            {
                                addLinePrefixToLog(prefixLevel);
                                logBuilder.append("at: ").append(causeStackTrace[i].toString());
                                logBuilder.append(LINE_SEPARATOR);
                            }
                        }
                        else
                        {
                            int thisCount = causeStackTrace.length - 1;
                            int parentCount = parentStackTrace.length - 1;
                            int framesEqual = 0;
                            while (parentCount > -1 && thisCount > -1) 
                            {
                                if (causeStackTrace[thisCount].equals(parentStackTrace[parentCount]))
                                {
                                    framesEqual++;
                                    thisCount--;
                                    parentCount--;
                                } 
                                else
                                {
                                    break;
                                }
                            }
                            if (framesEqual > 1)
                            { //to conform with the spec and the common practice (F1F1EE)
                                framesEqual--;
                            }
                            int len = causeStackTrace.length - framesEqual;
                            for (int i = 0; i < len; i++)
                            {
                                addLinePrefixToLog(prefixLevel);
                                logBuilder.append("at: ").append(causeStackTrace[i].toString());
                                logBuilder.append(LINE_SEPARATOR);
                            }
                            if (framesEqual > 0)
                            {
                                addLinePrefixToLog(prefixLevel);
                                logBuilder.append("... ").append(framesEqual).append(" more");
                                logBuilder.append(LINE_SEPARATOR);
                            }
                        }
                    } 
                    else 
                    {
                        addLinePrefixToLog(prefixLevel);
                        logBuilder.append("<no stack trace available>");
                        logBuilder.append(LINE_SEPARATOR);
                    }
                }
            }
            else
            {
                logBuilder.append(LINE_SEPARATOR);
            }
        }
        catch (Exception e)
        {
            // what to do???
        }
    }
}
