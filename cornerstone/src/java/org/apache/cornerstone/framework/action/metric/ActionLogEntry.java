/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.cornerstone.framework.action.metric;

import java.sql.Timestamp;

import org.apache.cornerstone.framework.api.logging.ILogEntry;

public class ActionLogEntry implements ILogEntry
{
    public static final String REVISION = "$Revision$";

    // TODO: add a properties file which allows configuration of this
    // or look at how you can use JMX to configure this.
    //

    //public void setId(long id)
    //{    
    //    _id = id;
    //}

    /**
     * @return long id
     */
    public long getId()
    {
        return _id;
    }

    /**
     * Sets the UserID
     * @param userId
     */
    public void setUserId(String userId)
    {
        _userId = userId;
    }

    /**
     * Return sthe userID
     * @return String the user id
     */
    public String getUserId()
    {
        return _userId;
    }

    /**
     * Sets the ProxyID
     * @param proxyId
     */
    public void setProxyId(String proxyId)
    {
        _proxyId = proxyId;
    }

    /**
     * Returns the ProxyID
     * @return String proxyID
     */
    public String getProxyId()
    {
        return _proxyId;
    }

    /**
     * Sets the session ID
     * @param sessionId
     */
    public void setSessionId(String sessionId)
    {
        _sessionId = sessionId;
    }

    /**
     * Gets the sessionID
     * @return String sessionID
     */
    public String getSessionId()
    {
        return _sessionId;
    }

    /**
     * Sets the requestID
     * @param requestId
     */
    public void setRequestId(String requestId)
    {
        _requestId = requestId;
    }

    /**
     * Gets the requestID
     * @return String requestID
     */
    public String getRequestId()
    {
        return _requestId;
    }

    /**
     * Sets the protletID
     * @param portletId
     */
    public void setPortletId(String portletId)
    {
        _portletId = portletId;
    }

    /**
     * Gets the portletID
     * @return
     */
    public String getPortletId()
    {
        return _portletId;
    }

    /**
     * Sets the actionID
     * @param actionId
     */
    public void setActionId(String actionId)
    {
        _actionId = actionId;
    }

    /**
     * Gets the actionID
     * @return String actionID
     */
    public String getActionId()
    {
        return _actionId;
    }

    /**
     * Sets the actionContext
     * @param actionContext
     */
    public void setActionContext(String actionContext)
    {
        _actionContext = actionContext;
    }

    /**
     * Gets the actionContext
     * @return String actionContext
     */
    public String getActionContext()
    {
        return _actionContext;
    }

    /**
     * Sets the startTime
     * @param startTime
     */
    public void setStartTime(Timestamp startTime)
    {
        _startTime = startTime;
    }

    /**
     * Gets the startTime
     * @return String startTime
     */
    public Timestamp getStartTime()
    {
        return _startTime;
    }

    /**
     * Sets the endTime
     * @param endTime
     */
    public void setEndTime(Timestamp endTime)
    {
        _endTime = endTime;
    }

    /**
     * Gets the endTime
     * @return String endTime
     */
    public Timestamp getEndTime()
    {
        return _endTime;
    }

    protected long _id;
    protected String _userId;
    protected String _proxyId;
    protected String _sessionId;
    protected String _requestId;
    protected String _portletId;
    protected String _actionId;
    protected String _actionContext;
    protected Timestamp _startTime;
    protected Timestamp _endTime;
}