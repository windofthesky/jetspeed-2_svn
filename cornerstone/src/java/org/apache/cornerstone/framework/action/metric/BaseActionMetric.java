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
import java.util.HashMap;
import java.util.Map;

import org.apache.cornerstone.framework.action.BaseAction;
import org.apache.cornerstone.framework.api.action.IAction;
import org.apache.cornerstone.framework.api.action.IActionDescriptor;
import org.apache.cornerstone.framework.api.action.InvalidActionException;
import org.apache.cornerstone.framework.api.action.metric.IActionMetric;
import org.apache.cornerstone.framework.api.context.IContext;
import org.apache.cornerstone.framework.bean.visitor.BeanJSConverter;
import org.apache.cornerstone.framework.logging.InMemoryLog;
import org.apache.cornerstone.framework.logging.InMemoryLogFactory;
import org.apache.cornerstone.framework.pubsub.AutoPublication;

/**
 * This is an AutoPublished object for monitoring by JMX.
 * it is responsible for measuirng the timing parameters for
 * action invocation.
 */

public class BaseActionMetric extends AutoPublication implements IActionMetric
{
    public static final String REVISION = "$Revision$";

    public static final String USER_ID = BaseAction.USER_NAME;
    public static final String PROXY_ID = "proxyId";
    public static final String SESSION_ID = "sessionId";
    public static final String REQUEST_ID = "requestId";
    public static final String PORTLET_ID = "portletId";
    public static final String ACTION_ID = "actionId";

    public BaseActionMetric(IAction action)
    {
        super(action.getName());
        _action = action;
        _inMemoryLog = (InMemoryLog) InMemoryLogFactory.getSingleton().createInstance(_action.getName());
    }

    /**
     * Adds responseTime
     * @param long time
     * 
     */
    public void addTimeStamp(IContext context, long startTime, long endTime)
    {
        _callCount++;
        long responseTime = endTime - startTime;
        if (_minimumResponseTime > responseTime)
        {
            _minimumResponseTime = responseTime;
        }

        if (_maximumResponseTime < responseTime)
        {
            _maximumResponseTime = responseTime;
        }

        _totalResponseTime += responseTime;

        // create InMemoryLogEntry and add it to the InMemorylog
        ActionLogEntry logEntry = new ActionLogEntry();

        // TODO:
        // in here you could use configuration levels to turn-off
        // or on various log levels
        //

        logEntry.setUserId((String) context.getValue(USER_ID));

        // TODO: uncomment these once the values are made available on the context

        //        logEntry.setProxyId((String)context.getValue(PROXY_ID));
        //        logEntry.setSessionId((String)context.getValue(SESSION_ID));
        //        logEntry.setRequestId((String)context.getValue(REQUEST_ID));
        //        logEntry.setPortletId((String)context.getValue(PORTLET_ID));

        logEntry.setActionId(_action.getName());

        logEntry.setStartTime(new Timestamp(startTime));
        logEntry.setEndTime(new Timestamp(endTime));

        try
        {
            logEntry.setActionContext(flattenContext(_action, context));
        }
        catch (InvalidActionException e)
        {
            logEntry.setActionContext("exeception: " + e);
        }

        _inMemoryLog.addEntry(logEntry);
    }

    /**
     * Gets the minimum response time for the action
     */
    public long getMinimumResponseTime()
    {
        return _minimumResponseTime;
    }

    /**
     * Gets the Maximum Response time for the action
     */
    public long getMaximumResponseTime()
    {
        return _maximumResponseTime;
    }

    /**
     * Gets the average response time for the action.
     */
    public double getAverageResponseTime()
    {
        return (double) _totalResponseTime / (double) _callCount;
    }

    /**
     * gets the call count for the action.
     */
    public long getCallCount()
    {
        return _callCount;
    }

    protected String flattenContext(IAction action, IContext context) throws InvalidActionException
    {
        IActionDescriptor actionDescriptor = _action.getDescriptor();
        String[] inputNames = actionDescriptor.getInputNames();

        Map map = new HashMap();
        for (int i = 0; i < inputNames.length; i++)
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
    private IAction _action = null;
}