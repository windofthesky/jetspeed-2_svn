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
import java.util.HashMap;
import java.util.Map;

import org.apache.cornerstone.framework.api.context.IContext;
import org.apache.cornerstone.framework.api.service.IService;
import org.apache.cornerstone.framework.api.service.IServiceDescriptor;
import org.apache.cornerstone.framework.api.service.InvalidServiceException;
import org.apache.cornerstone.framework.api.service.metric.IServiceMetric;
import org.apache.cornerstone.framework.bean.visitor.BeanJSConverter;
import org.apache.cornerstone.framework.logging.InMemoryLog;
import org.apache.cornerstone.framework.logging.InMemoryLogFactory;
import org.apache.cornerstone.framework.pubsub.AutoPublication;
import org.apache.cornerstone.framework.service.BaseService;

/**
 * This is an AutoPublished object for monitoring by JMX.
 * it is responsible for measuirng the timing parameters for
 * service invocation.
 */

public class BaseServiceMetric extends AutoPublication implements IServiceMetric
{
    public static final String REVISION = "$Revision$";

    public static final String USER_ID = BaseService.USER_NAME;
    public static final String PROXY_ID = "proxyId";
    public static final String SESSION_ID = "sessionId";    
    public static final String REQUEST_ID = "requestId";
    public static final String PORTLET_ID = "portletId";
    public static final String SERVICE_ID = "serviceId";

    public BaseServiceMetric(IService service)
    {
        super(service.getName());
        _service = service;
        _inMemoryLog = (InMemoryLog) InMemoryLogFactory.getSingleton().createInstance(_service.getName());
    }

    /**
     * Adds responseTime
     * @param long time
     * 
     */    
    public void addTimeStamp(IContext context,long startTime,long endTime)
    {
        _callCount++;
        long responseTime = endTime - startTime;
        if ( _minimumResponseTime > responseTime)
        {
            _minimumResponseTime = responseTime;    
        }
        
        if ( _maximumResponseTime < responseTime)
        {
            _maximumResponseTime = responseTime;    
        }
        
        _totalResponseTime += responseTime;
        
        // create InMemoryLogEntry and add it to the InMemorylog
        ServiceLogEntry logEntry = new ServiceLogEntry();
        
        // TODO:
        // in here you could use configuration levels to turn-off
        // or on various log levels
        //
        
        logEntry.setUserId((String)context.getValue(USER_ID));

        // TODO: uncomment these once the values are made available on the context
        
//        logEntry.setProxyId((String)context.getValue(PROXY_ID));
//        logEntry.setSessionId((String)context.getValue(SESSION_ID));
//        logEntry.setRequestId((String)context.getValue(REQUEST_ID));
//        logEntry.setPortletId((String)context.getValue(PORTLET_ID));
    
        logEntry.setServiceId(_service.getName());
        
        logEntry.setStartTime(new Timestamp(startTime));
        logEntry.setEndTime(new Timestamp(endTime));
        
        try {
            logEntry.setServiceContext(flattenContext(_service, context));
        } catch (InvalidServiceException e) {
            logEntry.setServiceContext("exeception: " + e);
        }
        
        _inMemoryLog.addEntry(logEntry);
    }
    
    /**
     * Gets the minimum response time for the service
     */
    public long getMinimumResponseTime()
    {
        return _minimumResponseTime;
    }
    
    /**
     * Gets the Maximum Response time for the service
     */
    public long getMaximumResponseTime()
    {
        return _maximumResponseTime;
    }
    
    /**
     * Gets the average response time for the service.
     */
    public double getAverageResponseTime()
    {
        return (double)_totalResponseTime/(double)_callCount;
    }
    
    /**
     * gets the call count for the service.
     */
    public long getCallCount()
    {
        return _callCount;
    }
    
    protected String flattenContext(IService service, IContext context) throws InvalidServiceException
    {
        IServiceDescriptor serviceDescriptor = _service.getDescriptor();
        String[] inputNames =  serviceDescriptor.getInputNames();
        
        Map map = new HashMap();
        for ( int i =0; i < inputNames.length; i++ )
        {
            String inputName = inputNames[i];
            Object inputValue = context.getValue(inputName);
            map.put(inputName, inputValue);
        }

        return BeanJSConverter.convertToJS(map);
    }

    protected long _totalResponseTime;
    protected long _callCount;
    protected long _minimumResponseTime = Long.MAX_VALUE;
    protected long _maximumResponseTime;
    
    private InMemoryLog _inMemoryLog = null;
    private IService _service = null;
}