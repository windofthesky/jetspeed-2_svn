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

package org.apache.jetspeed.statistics.impl;



// javax stuff
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.statistics.PortalStatistics;
import org.apache.pluto.om.portlet.PortletDefinition;

// java stuff
import java.security.Principal;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Simple implementation of the PortletStatsService. This implementation
 * uses <A HREF="http://httpd.apache.org/docs/logs.html">Apache Common Log Format (CLF)</A> as its default log format.
 * This format uses the following pattern string: "%h %l %u %t \"%r\" %>s %b",
 * where:
 * <UL>
 * <LI><B>%h</B> - remote host</LI>
 * <LI><B>%l</B> - remote log name</LI>
 * <LI><B>%u</B> - remote user</LI>
 * <LI><B>%t</B> - time in common log time format</LI>
 * <LI><B>%r</B> - first line of request</LI>
 * <LI><B>%s</B> - status (either 200 or 401)</LI>
 * <LI><B>%b</B> - bytes sent (always "-" for no bytes sent). Optionally, portlet load time may be logged (see logLoadTime property)</LI>
 * </UL>
 * <P>
 * Here's an example log entry:
 * <P>
 * <CODE>127.0.0.1 - turbine [26/Aug/2002:11:44:40 -0500] "GET /jetspeed/DatabaseBrowserTest HTTP/1.1" 200 -</CODE>
 * <P>
 * TODO:
 * <UL>
 * <LI>Statistics cache (by portlet and by user)</LI>
 * <LI>Portlet exclusion</LI>
 * <LI>Configurable format pattern</LI>
 * </UL>
 * 
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 * @author <a href="mailto:rklein@bluesunrise.com">Richard Klein</a>
 * @version $Id: $
 */
public class PortalStatisticsImpl implements PortalStatistics
{
    /**
     * Static initialization of the logger for this class
     */    
    protected final static Log logger = LogFactory.getLog(PortalStatisticsImpl.class);
    
    /**
     * The default log format pattern string to use with the following elements:
     * <OL START="0">
     * <LI>remote address</LI>
     * <LI>always "-"</LI>
     * <LI>user name</LI>
     * <LI>timestamp</LI>
     * <LI>request method</LI>
     * <LI>context</LI>     
     * <LI>portlet name</LI>
     * <LI>request protocol</LI>
     * <LI>status code</LI>
     * <LI>always "-" unless logLoadTime is true</LI>
     * </OL>
     */
    protected static final String defaultLogFormat = "{0} {1} {2} [{3}] \"{4} {5}/{6} {7}\" {8} {9}";

    /**
     * Logging enabled flag. If TRUE, the logging will occur. To improve performance,
     * the application should use isEnabled() method before calling logAccess().
     */
    private boolean enabled = false;

    /**
     * Date format to use in the log entry. Should conform to standard
     * format used by the SimpleDateFormat class.
     */
    protected String dateFormat = null;

    /** Date formatter */
    protected SimpleDateFormat formatter = null;

    /** Log portlet load time instead of bytes sent (which is always zero) */
    protected boolean logLoadTime = false;

    public PortalStatisticsImpl(boolean enabled, String dateFormat, boolean logLoadTime) 
    {
        this.enabled = enabled;
        this.dateFormat = dateFormat; //"dd/MM/yyyy:hh:mm:ss z");
        this.formatter = new SimpleDateFormat(this.dateFormat);
        this.logLoadTime = logLoadTime;
    }
            
    /**
     * @see org.apache.jetspeed.services.portletstats.PortletStatsService#isEnabled
     */
    public boolean isEnabled()
    {
        return this.enabled;
    }

    /**
     * @see org.apache.jetspeed.services.portletstats.PortletStatsService#setEnabled
     */
    public boolean setEnabled(boolean state)
    {
        boolean oldState = this.enabled;
        this.enabled = state;

        return oldState;
    }

    /**
     * @see org.apache.jetspeed.services.portletstats.PortletStatsService#logAccess
     */
    public void logAccess(RequestContext request, PortletDefinition portlet, String statusCode, long time)
    {
        
        if (!this.isEnabled())
        {
            return;
        }
        
        try 
        {
            logger.info(this.getLogMessage(request, portlet, statusCode, time));
        }
        catch (Exception e)
        {
            logger.error("Exception", e);
        }
    }

    /**
     * Formats log message
     * 
     * @param data
     * @param portlet
     * @param statusCode
     * @param time
     * @return Formatted message
     * @exception Exception
     */
    protected String getLogMessage(RequestContext rc, PortletDefinition portlet, String statusCode, long time) 
    throws Exception
    {        
        HttpServletRequest req = rc.getRequest();
        Principal principal = req.getUserPrincipal();
        String userName = "guest";
        String portletName = "unknown";
        if (portlet != null)
        {
            portletName = portlet.getName();
        }
        if (principal != null)
        {
            userName = principal.getName();
        }
        Object[] args = {
            req.getRemoteAddr(),
            "-",
            userName,
            this.formatter.format(new Date()),
            req.getMethod(),
            req.getContextPath(),
            portletName,
            req.getProtocol(),
            statusCode,
            this.logLoadTime == true ? String.valueOf(time) : "-"
        }; 

        return MessageFormat.format(defaultLogFormat, args).toString();

    }

    /**
     * Formats log message using default load time
     * 
     * @param data
     * @param portlet
     * @param statusCode
     */
    public void logAccess(RequestContext request, PortletDefinition portlet, String statusCode) 
    {
        logAccess(request, portlet, statusCode, 0);
    }
    
}

