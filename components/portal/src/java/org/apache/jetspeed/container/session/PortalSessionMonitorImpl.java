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

import org.apache.jetspeed.services.JetspeedPortletServices;
import org.apache.jetspeed.services.PortletServices;

/**
 * PortalSessionMonitorImpl
 *
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id: $
 */
public class PortalSessionMonitorImpl implements PortalSessionMonitor
{
    private static final long serialVersionUID = 1239564779524373742L;

    private long sessionKey;
    private transient String sessionId;
    private transient HttpSession session;
    private boolean forceInvalidate;
    
    public PortalSessionMonitorImpl(long sessionKey)
    {
        this(sessionKey,true);
    }
    
    public PortalSessionMonitorImpl(long sessionKey, boolean forceInvalidate)
    {
        this.sessionKey = sessionKey;
        this.forceInvalidate = forceInvalidate;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.session.PortalSessionMonitor#getSessionId()
     */
    public String getSessionId()
    {
        return sessionId;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.session.PortalSessionMonitor#getSessionKey()
     */
    public long getSessionKey()
    {
        return sessionKey;
    }
    
    public HttpSession getSession()
    {
        return session;
    }


    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.session.PortalSessionMonitor#invalidateSession()
     */
    public void invalidateSession()
    {
        HttpSession thisSession = session;
        if ( thisSession != null )
        {
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
        this.sessionId = session.getId();
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSessionBindingListener#valueUnbound(javax.servlet.http.HttpSessionBindingEvent)
     */
    public void valueUnbound(HttpSessionBindingEvent event)
    {
        if ( session != null )
        {
            PortalSessionsManager manager = getManager();
            if (manager != null)
            {
                manager.portalSessionDestroyed(this);
            }
            session = null;
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSessionActivationListener#sessionDidActivate(javax.servlet.http.HttpSessionEvent)
     */
    public void sessionDidActivate(HttpSessionEvent event)
    {
        session = event.getSession();
        sessionId = session.getId();
        PortalSessionsManager manager = getManager();
        if (manager != null)
        {
            manager.portalSessionDidActivate(this);
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSessionActivationListener#sessionWillPassivate(javax.servlet.http.HttpSessionEvent)
     */
    public void sessionWillPassivate(HttpSessionEvent event)
    {
        PortalSessionsManager manager = getManager();
        if (manager != null)
        {
            manager.portalSessionWillPassivate(this);
        }
        session = null;
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
