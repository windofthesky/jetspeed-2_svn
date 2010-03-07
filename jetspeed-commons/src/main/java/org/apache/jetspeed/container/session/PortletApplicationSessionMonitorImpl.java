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
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;

import org.apache.jetspeed.container.session.PortalSessionsManager;
import org.apache.jetspeed.container.session.PortletApplicationSessionMonitor;
import org.apache.jetspeed.services.JetspeedPortletServices;
import org.apache.jetspeed.services.PortletServices;

/**
 * PortletApplicationSessionMonitorImpl
 *
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id: $
 */
public class PortletApplicationSessionMonitorImpl implements PortletApplicationSessionMonitor 
{
    private static final long serialVersionUID = -6729032046828426324L;
    
    private String contextPath;    
    private String portalSessionId;
    private long portalSessionKey;
    private transient HttpSession session;
    private boolean forceInvalidate;

    public PortletApplicationSessionMonitorImpl(String contextPath, String portalSessionId, long portalSessionKey)
    {
        this(contextPath, portalSessionId, portalSessionKey, true);
    }
    
    public PortletApplicationSessionMonitorImpl(String contextPath, String portalSessionId, long portalSessionKey, boolean forceInvalidate)
    {
        this.contextPath = contextPath;
        this.portalSessionId = portalSessionId;
        this.portalSessionKey = portalSessionKey;
        this.forceInvalidate = forceInvalidate;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.session.PortletApplicationSessionMonitor#getPortalSessionKey()
     */
    public long getPortalSessionKey()
    {
        return portalSessionKey;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.session.PortletApplicationSessionMonitor#getPortalSessionId()
     */
    public String getPortalSessionId()
    {
        return portalSessionId;
    }
    
    public HttpSession getSession()
    {
        return session;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.session.PortletApplicationSessionMonitor#getContextPath()
     */
    public String getContextPath()
    {
        return contextPath;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.session.PortletApplicationSessionMonitor#invalidateSession()
     */
    public void invalidateSession()
    {
        if ( session != null )
        {
            HttpSession thisSession = session;
            session = null;
            if (forceInvalidate)
            {
                try
                {
                    thisSession.invalidate();
                }
                catch (Exception ise)
                {
                    // ignore
                }
            }
        }        
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSessionBindingListener#valueBound(javax.servlet.http.HttpSessionBindingEvent)
     */
    public void valueBound(HttpSessionBindingEvent event)
    {
        this.session = event.getSession();
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSessionBindingListener#valueUnbound(javax.servlet.http.HttpSessionBindingEvent)
     */
    public void valueUnbound(HttpSessionBindingEvent event)
    {
        if ( session != null )
        {
            PortalSessionsManager manager = getManager(); 
            if ( manager != null )
            {
                manager.sessionDestroyed(this);
            }
            session = null;
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSessionActivationListener#sessionDidActivate(javax.servlet.http.HttpSessionEvent)
     */
    public void sessionDidActivate(HttpSessionEvent event)
    {
        this.session = event.getSession();
        PortalSessionsManager manager = getManager(); 
        if ( manager != null )
        {
            manager.sessionDidActivate(this);
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSessionActivationListener#sessionWillPassivate(javax.servlet.http.HttpSessionEvent)
     */
    public void sessionWillPassivate(HttpSessionEvent event)
    {
        PortalSessionsManager manager = getManager(); 
        if ( manager != null )
        {
            manager.sessionWillPassivate(this);
        }
        session = null;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.session.PortletApplicationSessionMonitor#portalSessionUpdated(org.apache.jetspeed.container.session.PortalSessionMonitor)
     */
    public void syncPortalSessionId(PortalSessionMonitor psm)
    {
        if (psm != null && psm.getSessionKey() == getPortalSessionKey())
        {
            this.portalSessionId = psm.getSessionId();
        }
    }

    private PortalSessionsManager getManager()
    {
        PortletServices services = JetspeedPortletServices.getSingleton();
        if (services != null)
        {
            return (PortalSessionsManager)services.getService(PortalSessionsManager.SERVICE_NAME);
        }
        return null;
    }
}
