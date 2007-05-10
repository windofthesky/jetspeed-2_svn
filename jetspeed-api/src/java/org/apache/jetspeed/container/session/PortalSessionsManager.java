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
package org.apache.jetspeed.container.session;

import javax.servlet.http.HttpSession;

/**
 * PortalSessionsMonitor
 *
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id: $
 */
public interface PortalSessionsManager
{
    String SERVICE_NAME = PortalSessionsManager.class.getName();
    
    void portalSessionCreated(HttpSession portalSession);
    void portalSessionWillPassivate(PortalSessionMonitor psm);
    void portalSessionDidActivate(PortalSessionMonitor psm);
    void portalSessionDestroyed(PortalSessionMonitor psm);
    void checkMonitorSession(String contextPath, HttpSession portalSession, HttpSession paSession);    
    void sessionWillPassivate(PortletApplicationSessionMonitor pasm);    
    void sessionDidActivate(PortletApplicationSessionMonitor pasm);    
    void sessionDestroyed(PortletApplicationSessionMonitor pasm);    
    /**
     * Returns the number of current sessions. Used to track the number guest users in portal.
     * @return Number of currently created sessions in the registry
     */
    int sessionCount();
}
