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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PortalSessionsManagerImpl
 *
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id: $
 */
public class PortalSessionsManagerImpl implements PortalSessionsManager
{

    private static Logger log = LoggerFactory.getLogger(PortalSessionsManagerImpl.class);
    
    private static final class PortalSessionRegistry
    {
        long portalSessionKey;
        PortalSessionMonitor psm;
        Map<String,PortletApplicationSessionMonitor> sessionMonitors;
        
        PortalSessionRegistry()
        {
            sessionMonitors = Collections.synchronizedMap(new HashMap<String,PortletApplicationSessionMonitor>());
        }
    }
    
    private long portalSessionKeySequence;
    private Map<String,PortalSessionRegistry> portalSessionsRegistry;
    private boolean forceInvalidate;
    
    public PortalSessionsManagerImpl()
    {
        this(true);        
    }
    
    public PortalSessionsManagerImpl(boolean forceInvalidate)
    {
        portalSessionKeySequence = System.currentTimeMillis();
        portalSessionsRegistry = Collections.synchronizedMap(new HashMap<String,PortalSessionRegistry>());
        this.forceInvalidate = forceInvalidate;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.session.PortalSessionsManager#portalSessionCreated(javax.servlet.http.HttpSession)
     */
    public void portalSessionCreated(HttpSession portalSession)
    {
        PortalSessionMonitor psm = null;
        boolean newMonitor = false;
        
        synchronized (this) 
        {
            psm = (PortalSessionMonitor)portalSession.getAttribute(PortalSessionMonitor.SESSION_KEY);
            if (psm != null)
            {
                // An existing session is been "recreated", or a new sessionID has been set
                // Tomcat 5.5.29+/6.0.21+ has a new feature called changeSessionIdOnAuthentication, see: https://issues.apache.org/bugzilla/show_bug.cgi?id=45255
                // which can cause this
                if (psm.getSessionId() != portalSession.getId())
                {
                    // update all sessionID keys and the portal session registry key  
                    PortalSessionRegistry psr = portalSessionsRegistry.remove(psm.getSessionId());
                    if (psr != null)
                    {
                        // update session and specifically the sessionId in psm
                        psm.valueBound(new HttpSessionBindingEvent(portalSession, null));
                        for (PortletApplicationSessionMonitor pasm : valuesShallowCopy(psr.sessionMonitors.values()))
                        {
                            pasm.syncPortalSessionId(psm);
                        }
                        portalSessionsRegistry.put(psm.getSessionId(), psr);                       
                    }
                    else
                    {
                        psm = null;
                    }
                }
            }            
            if (psm == null)
            {
                psm = new PortalSessionMonitorImpl(++portalSessionKeySequence, forceInvalidate);
                newMonitor = true;
            }
        }
        if (newMonitor)
        {
            portalSession.setAttribute(PortalSessionMonitor.SESSION_KEY, psm);
        }
        // register it as if activated
        portalSessionDidActivate(psm);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.session.PortalSessionsManager#portalSessionWillPassivate(org.apache.jetspeed.container.session.PortalSessionMonitor)
     */
    public void portalSessionWillPassivate(PortalSessionMonitor psm)
    {
        portalSessionsRegistry.remove(psm.getSessionId());
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.session.PortalSessionsManager#portalSessionDidActivate(org.apache.jetspeed.container.session.PortalSessionMonitor)
     */
    public void portalSessionDidActivate(PortalSessionMonitor restoredPsm)
    {
        PortalSessionRegistry psr = portalSessionsRegistry.get(restoredPsm.getSessionId());
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
        // we iterate with shallow copy of paSessions to avoid conflicts with concurrent updates of paSessions
        for (PortletApplicationSessionMonitor pasm : valuesShallowCopy(psr.sessionMonitors.values()))
        {
            if ( pasm.getPortalSessionKey() != psr.portalSessionKey )
            {
                pasm.invalidateSession();
                // remove from original map !
                psr.sessionMonitors.remove(pasm.getContextPath());
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.session.PortalSessionsManager#portalSessionDestroyed(org.apache.jetspeed.container.session.PortalSessionMonitor)
     */
    public void portalSessionDestroyed(PortalSessionMonitor psm)
    {
        PortalSessionRegistry psr = portalSessionsRegistry.remove(psm.getSessionId());
        if ( psr != null )
        {
            // we iterate with shallow copy of paSessions to avoid conflicts with concurrent updates of paSessions
            for (PortletApplicationSessionMonitor pasm : valuesShallowCopy(psr.sessionMonitors.values()))
            {
                pasm.invalidateSession();
            }
            
            try
            {
                // To make sure its gone.
                // You better not remove the psm from the portal session yourself ;)
                psm.invalidateSession();
            }
            catch (IllegalStateException ise)
            {
                // pSession already invalid, ignore
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.session.PortalSessionsManager#checkMonitorSession(java.lang.String, javax.servlet.http.HttpSession, javax.servlet.http.HttpSession)
     */
    public void checkMonitorSession(String contextPath, HttpSession portalSession, HttpSession paSession)
    {
        if ( portalSession != null && paSession != null )
        {
            if (portalSession == paSession)
            {
                // On WebSphere 6.1.0.11, strange symptoms like this occur...
                log.warn("servlet context name of paSession(" + paSession.getId() + "): " + paSession.getServletContext().getServletContextName());
                return;
            }

            PortalSessionRegistry psr = portalSessionsRegistry.get(portalSession.getId());
            if (psr == null)
            {
                // On Tomcat 7, by default after authentication it will change the session.getId() without notifying anything...
                // On Tomcat 6 this at least still lead to a SessionCreated event, but for Tomcat 7 we only can check for a similar condition
                // and then just emulate as if it happened. 
                PortalSessionMonitor psm = (PortalSessionMonitor)portalSession.getAttribute(PortalSessionMonitor.SESSION_KEY);
                // the psm better be null here, otherwise something really is corrupt or not playing by the listeners contracts
                if ( psm == null || portalSessionsRegistry.containsKey(psm.getSessionId()) )
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
                psr = portalSessionsRegistry.get(portalSession.getId());
            }
            PortletApplicationSessionMonitor pasm = psr.sessionMonitors.get(contextPath);
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
                pasm = new PortletApplicationSessionMonitorImpl(contextPath,portalSession.getId(),psr.portalSessionKey, forceInvalidate);
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
        PortalSessionRegistry psr = portalSessionsRegistry.get(pasm.getPortalSessionId());
        if (psr != null )
        {
            psr.sessionMonitors.remove(pasm.getContextPath());
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.session.PortalSessionsManager#sessionDidActivate(org.apache.jetspeed.container.session.PortletApplicationSessionMonitor)
     */
    public void sessionDidActivate(PortletApplicationSessionMonitor restoredPasm)
    {
        PortalSessionRegistry psr = portalSessionsRegistry.get(restoredPasm.getPortalSessionId());
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
        restoredPasm.getSession().setAttribute(PortletApplicationSessionMonitor.SESSION_KEY, restoredPasm);
        psr.sessionMonitors.put(restoredPasm.getContextPath(), restoredPasm);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.session.PortalSessionsManager#sessionDestroyed(org.apache.jetspeed.container.session.PortletApplicationSessionMonitor)
     */
    public void sessionDestroyed(PortletApplicationSessionMonitor pasm)
    {
        PortalSessionRegistry psr = portalSessionsRegistry.get(pasm.getPortalSessionId());
        if ( psr != null )
        {
            psr.sessionMonitors.remove(pasm.getContextPath());

            try
            {
                // To make sure its gone.
                // You better not remove the pasm from the session yourself ;)
                pasm.invalidateSession();
            }
            catch (IllegalStateException ise)
            {
                // paSession already invalid, ignore
            }
        }
    }

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.container.session.PortalSessionsManager#sessionCount()
	 */
	public int sessionCount() {
		
		return portalSessionsRegistry.size();
	}

    /**
     * Returns a shallow copy of the given Collection.
     * @param inValues
     * @return shallow copy
     */
    private Collection<PortletApplicationSessionMonitor> valuesShallowCopy(Collection<PortletApplicationSessionMonitor> inValues) {
        return Arrays.asList(inValues.toArray(new PortletApplicationSessionMonitor[0]));
    }
}
