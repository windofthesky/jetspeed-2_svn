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
import org.apache.cornerstone.framework.constant.Constant;
import org.apache.log4j.Logger;

public class LogicalService implements IService
{
    public static final String METRIC = "metric";
    public static final String METRIC_DOT = METRIC + Constant.DOT;

    public static final String CONFIG_METRIC_INSTANCE_CLASS_NAME = METRIC_DOT + Constant.INSTANCE_CLASS_NAME;
    public static final String CONFIG_METRIC_ENABLED = METRIC_DOT + "enabled";

    public LogicalService(String logicalName, IService parent)
    {
        _parent = parent;

        String metricEnabledString = getConfigPropertyWithDefault(CONFIG_METRIC_ENABLED, "false");
        Boolean metricEnabled = new Boolean(metricEnabledString);
        if (metricEnabled.booleanValue())
        {
            IServiceMetric metric = getMetric();
            if (metric == null)
            {
                String metricClassName = getConfigProperty(CONFIG_METRIC_INSTANCE_CLASS_NAME);
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
        return _parent.getName();
    }

    public void setName(String name)
    {
    	_parent.setName(name);
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
        return _parent.getClass().getName() + ":" + name;
    }

    protected String getMetricClassVariableName()
    {
        return METRIC + "-" + getName();
    }

    protected IService _parent;

    private static Logger _Logger = Logger.getLogger(LogicalService.class);
    protected static Map _ClassVariableMap = new HashMap();
}