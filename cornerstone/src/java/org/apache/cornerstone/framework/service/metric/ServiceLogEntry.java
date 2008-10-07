/*
 * Copyright 2000-2004 The Apache Software Foundation.
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

package org.apache.cornerstone.framework.service.metric;

import java.sql.Timestamp;

import org.apache.cornerstone.framework.api.logging.ILogEntry;

public class ServiceLogEntry implements ILogEntry
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
     * Sets the serviceID
     * @param serviceId
     */
    public void setServiceId(String serviceId)
    {
        _serviceId = serviceId;
    }

    /**
     * Gets the serviceID
     * @return String serviceID
     */
    public String getServiceId()
    {
        return _serviceId;
    }
    
    /**
     * Sets the serviceContext
     * @param serviceContext
     */
    public void setServiceContext(String serviceContext)
    {
        _serviceContext = serviceContext;
    }

    /**
     * Gets the serviceContext
     * @return String serviceContext
     */
    public String getServiceContext()
    {
        return _serviceContext;
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
    protected String _serviceId;
    protected String _serviceContext;
    protected Timestamp _startTime;
    protected Timestamp _endTime;
}