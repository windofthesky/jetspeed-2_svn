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

package org.apache.cornerstone.framework.service;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.cornerstone.framework.api.context.IContext;
import org.apache.cornerstone.framework.api.service.IService;
import org.apache.cornerstone.framework.api.service.IServiceDescriptor;
import org.apache.cornerstone.framework.api.service.InvalidServiceException;
import org.apache.cornerstone.framework.api.service.ServiceException;
import org.apache.cornerstone.framework.api.service.metric.IServiceMetric;
import org.apache.log4j.Logger;

public class LogicalService implements IService
{
    public static final String METRIC_CLASS_NAME = "metric.className";
    public static final String METRIC_ENABLED = "metric.enabled";
    public static final String METRIC = "metric";

    public LogicalService(String logicalName, IService parent)
    {
        _logicalName = logicalName;
        _parent = parent;

        String metricEnabledString = getConfigPropertyWithDefault(METRIC_ENABLED, "false");
        Boolean metricEnabled = new Boolean(metricEnabledString);
        if (metricEnabled.booleanValue())
        {
            IServiceMetric metric = getMetric();
            if (metric == null)
            {
                String metricClassName = getConfigProperty(METRIC_CLASS_NAME);
                try
                {
                    Class[] paramTypes = {IService.class};
                    Class metricClass = Class.forName(metricClassName);
                    Constructor cons = metricClass.getConstructor(paramTypes);
                    //Object[] params = {_logicalName};
                    Object[] params = {this};
                    metric = (IServiceMetric) cons.newInstance(params);
                }
                catch (Exception e)
                {
                    _Logger.error("failed to create service metric", e);
                }
            }

            setMetric(metric);
        }
    }

    /* (non-Javadoc)
     * @see com.cisco.salesit.framework.service.core.IService#invoke(com.cisco.salesit.framework.common.core.IContext)
     */
    public Object invoke(IContext context) throws ServiceException
    {
        return _parent.invoke(context);
    }

    /* (non-Javadoc)
     * @see com.cisco.salesit.framework.service.core.IService#getDescriptor()
     */
    public IServiceDescriptor getDescriptor() throws InvalidServiceException
    {
        return _parent.getDescriptor();
    }

    /* (non-Javadoc)
     * @see com.cisco.salesit.framework.service.core.IService#getMetric()
     */
    public IServiceMetric getMetric()
    {
        return (IServiceMetric) getClassVariable(getMetricClassVariableName());
    }

    public void setMetric(IServiceMetric metric)
    {
        setClassVariable(getMetricClassVariableName(), metric);
        _parent.setMetric(metric);
    }

    public String getName()
    {
        return _logicalName;
    }

    /* (non-Javadoc)
     * @see com.cisco.salesit.framework.common.core.IConfigurable#getConfigProperty(java.lang.String)
     */
    public String getConfigProperty(String p)
    {
        return _parent.getConfigProperty(p);
    }

    /* (non-Javadoc)
     * @see com.cisco.salesit.framework.common.core.IConfigurable#getConfigProperty(java.lang.String, java.lang.String)
     */
    public String getConfigProperty(String p1, String p2)
    {
        return _parent.getConfigProperty(p1, p2);
    }

    /* (non-Javadoc)
     * @see com.cisco.salesit.framework.common.core.IConfigurable#getConfigProperty(java.lang.String, java.lang.String, java.lang.String)
     */
    public String getConfigProperty(String p1, String p2, String p3)
    {
        return _parent.getConfigProperty(p1, p2, p3);
    }

    /* (non-Javadoc)
     * @see com.cisco.salesit.framework.common.core.IConfigurable#getConfigProperty(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public String getConfigProperty(String p1, String p2, String p3, String p4)
    {
        return _parent.getConfigProperty(p1, p2, p3, p4);
    }

    /* (non-Javadoc)
     * @see com.cisco.salesit.framework.common.core.IConfigurable#getConfigPropertyWithDefault(java.lang.String, java.lang.String)
     */
    public String getConfigPropertyWithDefault(String p, String defaultValue)
    {
        return _parent.getConfigPropertyWithDefault(p, defaultValue);
    }

    /* (non-Javadoc)
     * @see com.cisco.salesit.framework.common.core.IConfigurable#getConfigPropertyWithDefault(java.lang.String, java.lang.String, java.lang.String)
     */
    public String getConfigPropertyWithDefault(String p1, String p2, String defaultValue)
    {
        return _parent.getConfigPropertyWithDefault(p1, p2, defaultValue);
    }

    /* (non-Javadoc)
     * @see com.cisco.salesit.framework.common.core.IConfigurable#getConfigPropertyWithDefault(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public String getConfigPropertyWithDefault(String p1, String p2, String p3, String defaultValue)
    {
        return _parent.getConfigPropertyWithDefault(p1, p2, p3, defaultValue);
    }

    /* (non-Javadoc)
     * @see com.cisco.salesit.framework.common.core.IConfigurable#getConfigPropertyWithDefault(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public String getConfigPropertyWithDefault(String p1, String p2, String p3, String p4, String defaultValue)
    {
        return _parent.getConfigPropertyWithDefault(p1, p2, p3, p4, defaultValue);
    }

    /* (non-Javadoc)
     * @see com.cisco.salesit.framework.common.core.IConfigurable#overwriteConfig(java.util.Properties)
     */
    public void overwriteConfig(Properties overwrites)
    {
        _parent.overwriteConfig(overwrites);
    }

    public Object getClassVariable(String name)
    {
        return _ClassVariableMap.get(getClassVariableKey(name));
    }
                                        
    public void setClassVariable(String name, Object value)
    {
        _ClassVariableMap.put(getClassVariableKey(name), value);
    }

    protected String getClassVariableKey(String name)
    {
        // TODO: shouldn't this be _parent.getClass().getName() + ":" + name?
        return getClass().getName() + ":" + name;
    }

    protected String getMetricClassVariableName()
    {
        return METRIC + "-" + _logicalName;
    }

    protected IService _parent;
    protected String _logicalName;

    private static Logger _Logger = Logger.getLogger(LogicalService.class);
    protected static Map _ClassVariableMap = new HashMap();
}