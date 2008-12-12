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
package org.apache.jetspeed.pipeline.valve.impl;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.Valve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;

/**
 * The purpose of this valve is to load a property file and make the information
 * available on the request as an attribute. The name of the attribute is the
 * key that is passed into the constructor.
 * 
 * There are 3 different ways to use this object: 1. Provide a key and a
 * PropertiesConfiguration object 2. Provide a key and a path to a properties
 * file 3. Provide a string which is used for both the key and as an environment
 * lookup to find the path to a properties file.
 * 
 * The PropertiesConfiguration object is put on the request and can be consumed
 * by anyone downstream of this valve
 * 
 * Multiple valves can be configured via Spring and put into the pipeline if
 * more than one property file is needed.
 * 
 * Spring configuration samples are shown below: <bean
 * id="ProductionConfiguration"
 * class="org.apache.commons.configuration.PropertiesConfiguration">
 * <constructor-arg> <value>/apps/jetspeed/etc/jetspeed-production.properties</value>
 * </constructor-arg> </bean>
 * 
 * <bean id="propertyLoaderValve_1"
 * class="com.fmr.portal.pipeline.impl.PropertyLoaderValve"
 * init-method="initialize"> <constructor-arg index="0"> <value>php-properties</value>
 * </constructor-arg> <constructor-arg index="1"
 * type="org.apache.commons.configuration.PropertiesConfiguration"> <ref
 * bean="ProductionConfiguration"/> </constructor-arg> </bean>
 * 
 * <bean id="propertyLoaderValve_2"
 * class="com.fmr.portal.pipeline.impl.PropertyLoaderValve"
 * init-method="initialize"> <constructor-arg index="0"> <value>php-properties</value>
 * </constructor-arg>
 * 
 * <constructor-arg index="1">
 * <value>/apps/jetspeed/etc/jetspeed-production.properties</value>
 * </constructor-arg> </bean>
 * 
 * <bean id="propertyLoaderValve_3"
 * class="com.fmr.portal.pipeline.impl.PropertyLoaderValve"
 * init-method="initialize"> <constructor-arg index="0"> <value>app.props</value>
 * </constructor-arg> </bean>
 * 
 * For this last one, an environment variable with the name "app.props" would
 * contain the file path to the properties file.
 * 
 * 
 * @author David Gurney
 * 
 */
public class PropertyLoaderValve implements Valve
{
    protected String m_sKey = null;

    protected PropertiesConfiguration m_oPropertiesConfiguration = null;

    protected String m_sPropertyFilePath = null;

    public PropertyLoaderValve(String p_sKey,
            PropertiesConfiguration p_oPropertiesConfiguration)
    {
        m_sKey = p_sKey;
        m_oPropertiesConfiguration = p_oPropertiesConfiguration;
    }

    public PropertyLoaderValve(String p_sKey, String p_sPropertyFilePath)
    {
        m_sKey = p_sKey;
        m_sPropertyFilePath = p_sPropertyFilePath;
    }

    /**
     * 
     * @param p_sEnvironmentKey -
     *            This value will be used both as the storage key and as the
     *            name to use when looking up an environment variable. The
     *            environment variable should contain the file path of the
     *            properties file to be loaded
     */
    public PropertyLoaderValve(String p_sEnvironmentKey)
    {
        m_sKey = p_sEnvironmentKey;
    }

    public void initialize() throws PipelineException
    {
        // Get the property file path if necessary
        if (m_sPropertyFilePath == null && m_oPropertiesConfiguration == null)
        {
            m_sPropertyFilePath = System.getProperty(m_sKey);
        }

        // Load the file if the path is provided
        if (m_sPropertyFilePath != null && m_oPropertiesConfiguration == null)
        {
            try
            {
                m_oPropertiesConfiguration = new PropertiesConfiguration(
                        m_sPropertyFilePath);
            } catch (ConfigurationException e)
            {
                throw new PipelineException(e);
            }
        }

        // If we still have a null, create an empty properties configuration
        // anyway
        if (m_oPropertiesConfiguration == null)
        {
            m_oPropertiesConfiguration = new PropertiesConfiguration();
        }
    }

    public void invoke(RequestContext p_oRequest, ValveContext p_oContext)
            throws PipelineException
    {
        p_oRequest.getRequest()
                .setAttribute(m_sKey, m_oPropertiesConfiguration);

        if (p_oContext != null)
        {
            p_oContext.invokeNext(p_oRequest);
        }
    }
}