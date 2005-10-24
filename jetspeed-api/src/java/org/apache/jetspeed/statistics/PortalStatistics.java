/* Copyright 2004 Apache Software Foundation
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.jetspeed.statistics;

import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.om.portlet.PortletDefinition;

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
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 * @author <a href="mailto:rklein@bluesunrise.com">Richard Klein</a>
 * @version $Id: $
 */
public interface PortalStatistics
{
    /**
     * Returns sevice enabled state
     * 
     * @return true if service is enabled
     */
    public boolean isEnabled();

    /**
     * Sets service enabled state
     * 
     * @param state  new state
     * @return original service enabled state
     */
    public boolean setEnabled(boolean state);

    /**
     * Logs portlet access using default load time.
     * 
     * @param data       Current request info object
     * @param portlet    Portlet being logged
     * @param statusCode HTTP status code. For now, either 200 (successfull) or 401 (unauthorized)
     */
    public void logAccess(RequestContext request, PortletDefinition portlet, String statusCode);

    /**
     * Logs portlet access.
     * 
     * @param data       Current request info object
     * @param portlet    Portlet being logged
     * @param statusCode HTTP status code. For now, either 200 (successfull) or 401 (unauthorized)
     */
    public void logAccess(RequestContext request, PortletDefinition portlet, String statusCode, long time);
    
    // TODO: define remaining APIs for Pages, User access
}