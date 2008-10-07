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

package org.apache.cornerstone.framework.api.service;

import org.apache.cornerstone.framework.api.config.IConfigurable;
import org.apache.cornerstone.framework.api.context.IContext;
import org.apache.cornerstone.framework.api.service.metric.IServiceMetric;

/**
Interface for all services.
*/

public interface IService extends IConfigurable
{
    public static final String REVISION = "$Revision$";

    public static final String META_INVOKE_DIRECT_INPUTS = "_.invokeDirect.inputs";
    public static final String META_INVOKE_DIRECT_OUTPUT = "_.invokeDirect.output";

    /**
     * Invokes service.  A service can have any number of input parameters
     * and returns exactly one object as the value.  It can also make
     * changes to any number of properties of context as side effects.
     * @param context context object that has all input and output
     *   parameters.
     * @return value of service.
     * @exception ServiceException
     */
    public Object invoke(IContext context) throws ServiceException;

    public IServiceDescriptor getDescriptor() throws InvalidServiceException;

    public String getName();
    public void setName(String name);

    public IServiceMetric getMetric();

    public void setMetric(IServiceMetric metric);
}