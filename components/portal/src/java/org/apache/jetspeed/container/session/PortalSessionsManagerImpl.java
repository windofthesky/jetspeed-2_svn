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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpSession;

/**
 * PortalSessionsManagerImpl
 *
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id: $
 */
public class PortalSessionsManagerImpl implements PortalSessionsManager
{
    private static final class PortalSessionRegistry
    {
        long portalSessionKey;
        PortalSessionMonitor psm;
        Map sessionMonitors;
        
        PortalSessionRegistry()
        {
            sessionMonitors = Collections.synchronizedMap(new HashMap());
        }
    }
    
    private long portalSessionKeySequence;
    private Map portalSessionsRegistry;
    
    public PortalSessionsManagerImpl()
    {
        portalSessionKeySequence = System.currentTimeMillis();
        portalSessionsRegistry = Collections.synchronizedMap(new HashMap());
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.session.PortalSessionsManager#portalSessionCreated(javax.servlet.http.HttpSession)
     */
    public synchronized void portalSessionCreated(HttpSession portalSession)
    {
        PortalSessionMonitor psm = new PortalSessionMonitorImpl(++portalSessionKeySequence);
        portalSession.setAttribute(PortalSessionMonitor.SESSION_KEY, psm);
        // register it as if activated
        portalSessionDidActivate(psm);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.session.PortalSessionsManager#portalSessionWillPassivate(org.apache.jetspeed.container.session.PortalSessionMonitor)
     */
    public synchronized void portalSessionWillPassivate(PortalSessionMonitor psm)
    {
        portalSessionsRegistry.remove(psm.getSessionId());
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.session.PortalSessionsManager#portalSessionDidActivate(org.apache.jetspeed.container.session.PortalSessionMonitor)
     */
    public synchronized void portalSessionDidActivate(PortalSessionMonitor restoredPsm)
    {
        PortalSessionRegistry psr = (PortalSessionRegistry)portalSessionsRegistry.get(restoredPsm.getSessionId());
        if ( psr != null && psr.portalSessionKey != -1 && psr.portalSessionKey != restoredPsm.getSessionKey() )
        {
            // looks like Client didn't join the previous portal session while the sessionId is reused (cookies disabled?)
            // destroy the "old" portal Session and any (probably also not-joined) registered paSessions
            portalSessionDestroyed(psr.psm);
            psr = null;
        }
        if ( psr == null )
        {
            psr = new PortalSessionRegistry();
            portalSessionsRegistry.put(restoredPsm.getSessionId(), psr);
        }
        // save the restored instance
        psr.psm = restoredPsm;
        psr.portalSessionKey = restoredPsm.getSessionKey();
        // validate registered paSessions are in sync
        Iterator iter = psr.sessionMonitors.values().iterator();
        PortletApplicationSessionMonitor pasm;
        while (iter.hasNext())
        {
            pasm = (PortletApplicationSessionMonitor)iter.next();
            if ( pasm.getPortalSessionKey() != psr.portalSessionKey )
            {
                pasm.invalidateSession();
                iter.remove();
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.session.PortalSessionsManager#portalSessionDestroyed(org.apache.jetspeed.container.session.PortalSessionMonitor)
     */
    public synchronized void portalSessionDestroyed(PortalSessionMonitor psm)
    {
        PortalSessionRegistry psr = (PortalSessionRegistry)portalSessionsRegistry.remove(psm.getSessionId());
        if ( psr != null )
        {
            Iterator iter = psr.sessionMonitors.values().iterator();
            while (iter.hasNext())
            {
                ((PortletApplicationSessionMonitor)iter.next()).invalidateSession();
            }
            // To make sure its gone.
            // You better not remove the psm from the portal session yourself ;)
            psm.invalidateSession();
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.session.PortalSessionsManager#checkMonitorSession(java.lang.String, javax.servlet.http.HttpSession, javax.servlet.http.HttpSession)
     */
    public synchronized void checkMonitorSession(String contextPath, HttpSession portalSession, HttpSession paSession)
    {
        if ( portalSession != null && paSession != null )
        {
            PortalSessionRegistry psr = (PortalSessionRegistry)portalSessionsRegistry.get(portalSession.getId());
            if (psr == null)
            {
                // yet unexplained condition: the HttpSessionListener on the portal application *should* have registered the session!!!
                // Alas, it has been reported to happen...
                // Now trying to do some recovering here
                PortalSessionMonitor psm = (PortalSessionMonitor)portalSession.getAttribute(PortalSessionMonitor.SESSION_KEY);
                // the psm better be null here, otherwise something really is corrupt or not playing by the listeners contracts
                if ( psm == null )
                {
                    portalSessionCreated(portalSession);
                }
                else
                {
                    // Now we have discovered a really strange situation here
                    // Only explanation I can see is that a passivation of the portalSession occurred, 
                    // but that the activation again didn't trigger the sessionDidActivate event handler???
                   // Lets just try to accomodate this situation for now:
                    portalSessionDidActivate(psm);
                }
                // now retrieve the just created psr again
                psr = (PortalSessionRegistry)portalSessionsRegistry.get(portalSession.getId());
            }
            PortletApplicationSessionMonitor pasm = (PortletApplicationSessionMonitor)psr.sessionMonitors.get(contextPath);
            if ( pasm != null )
            {
                try
                {
                    if ( paSession.getAttribute(PortletApplicationSessionMonitor.SESSION_KEY) == null )
                    {
                        // looks like Client didn't join the previous pa session
                        // destroy the "old" paSession
                        pasm.invalidateSession();                    
                        pasm = null;
                        // no need to remove the "old" pasm from the sessionMonitors as it will be replaced right below
                    }
                }
                catch (IllegalStateException ise)
                {
                    // paSession already invalid, ignore
                }
            }
            if ( pasm == null )
            {
                pasm = new PortletApplicationSessionMonitorImpl(contextPath,portalSession.getId(),psr.portalSessionKey);
                try
                {
                    paSession.setAttribute(PortletApplicationSessionMonitor.SESSION_KEY, pasm);
                    psr.sessionMonitors.put(contextPath, pasm);
                }
                catch (IllegalStateException ise)
                {
                    // paSession already invalid, ignore
                }
            }
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.session.PortalSessionsManager#sessionWillPassivate(org.apache.jetspeed.container.session.PortletApplicationSessionMonitor)
     */
    public void sessionWillPassivate(PortletApplicationSessionMonitor pasm)
    {
        PortalSessionRegistry psr = (PortalSessionRegistry)portalSessionsRegistry.get(pasm.getPortalSessionId());
        if (psr != null )
        {
            psr.sessionMonitors.remove(pasm.getContextPath());
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.session.PortalSessionsManager#sessionDidActivate(org.apache.jetspeed.container.session.PortletApplicationSessionMonitor)
     */
    public synchronized void sessionDidActivate(PortletApplicationSessionMonitor restoredPasm)
    {
        PortalSessionRegistry psr = (PortalSessionRegistry)portalSessionsRegistry.get(restoredPasm.getPortalSessionId());
        if ( psr == null )
        {
            // looks like the portalSession was passivated or the paSession was replicated to another JVM while its related portalSession wasn't (yet)
            // so, we're gonna anticipate future activation of the portalSession:
            // create a temporary psr with an "empty" psm for now (portalSessionKey == -1)
            // once the portalSession is replicated/Activated, it will validate registered paSessions having the correct portalSessionKey
            psr = new PortalSessionRegistry();
            psr.psm = new PortalSessionMonitorImpl(-1);
            portalSessionsRegistry.put(restoredPasm.getPortalSessionId(), psr);
        }
        // save the restored instance
        psr.sessionMonitors.put(restoredPasm.getContextPath(), restoredPasm);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.session.PortalSessionsManager#sessionDestroyed(org.apache.jetspeed.container.session.PortletApplicationSessionMonitor)
     */
    public synchronized void sessionDestroyed(PortletApplicationSessionMonitor pasm)
    {
        PortalSessionRegistry psr = (PortalSessionRegistry)portalSessionsRegistry.get(pasm.getPortalSessionId());
        if ( psr != null )
        {
            psr.sessionMonitors.remove(pasm.getContextPath());
            // To make sure its gone.
            // You better not remove the pasm from the session yourself ;)
            pasm.invalidateSession();
        }
    }
}
