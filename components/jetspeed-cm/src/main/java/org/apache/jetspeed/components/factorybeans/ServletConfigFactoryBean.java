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
package org.apache.jetspeed.components.factorybeans;

import javax.servlet.ServletConfig;

import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * <p>
 * PreSetInstanceFactoryBean
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class ServletConfigFactoryBean extends AbstractFactoryBean
{

    private static ServletConfig servletConfig;


    /**
     * <p>
     * createInstance
     * </p>
     *
     * @see org.springframework.beans.factory.config.AbstractFactoryBean#createInstance()
     * @return
     * @throws Exception
     */
    protected final Object createInstance() throws Exception
    {  
        verifyState();
        return servletConfig;        
    }

    /**
     * <p>
     * getObjectType
     * </p>
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     * @return
     */
    public final Class getObjectType()
    {
        return ServletConfig.class;
    }
    
    public final static void setServletConfig(ServletConfig servletConfig)
    {
        ServletConfigFactoryBean.servletConfig = servletConfig;
    }
    
    protected final void verifyState() throws IllegalStateException
    {
        if(servletConfig == null)
        {
            throw new IllegalStateException("You invoke the ServletConfigFactoryBean.setServletConfig() "+
                  "method prior to attempting to get the ServletConfig.");
        }
    }
}
