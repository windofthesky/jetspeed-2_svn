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

package org.apache.cornerstone.framework.service.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.cornerstone.framework.api.context.IContext;
import org.apache.cornerstone.framework.api.service.IService;
import org.apache.cornerstone.framework.api.service.IServiceDescriptor;
import org.apache.cornerstone.framework.api.service.ServiceException;
import org.apache.cornerstone.framework.constant.Constant;
import org.apache.cornerstone.framework.init.Cornerstone;
import org.apache.cornerstone.framework.util.Util;
import org.apache.log4j.Logger;

/**
Service controller that invokes its services in sequence.
*/

public class SequenceServiceController extends BaseServiceController
{
    public static final String REVISION = "$Revision$";

    public static final String CONFIG_SEQUENCE = "sequence";

    /**
     * Invokes all my services in sequence.
     * @param context context object that has all input and output parameters.
     * @return value of service.
     * @exception ServiceException
     */
    protected Object invokeMiddle(IContext context) throws ServiceException
    {
        List serviceList = getServiceList();
        Object result = null;

        if (serviceList != null)
        {
            for (int i = 0; i < serviceList.size(); i++)
            {
                IService service = (IService) serviceList.get(i);
                result = service.invoke(context);

                IServiceDescriptor sd = service.getDescriptor();
                String outputName = sd.getOutputName();
                context.setValue(outputName, result);
            }
        }

        return result;
    }

    /**
     * Gets list of service instances.
     * @return list of service instances.
     * @throws ServiceException
     */
    protected List getServiceList() throws ServiceException
    {
        if (_serviceList == null)
        {
            _serviceList = new ArrayList();
            String serviceListString =
                getConfigProperty(CONFIG_SEQUENCE);
            List serviceNameList =
                Util.convertStringsToList(serviceListString);
            for (int i = 0; i < serviceNameList.size(); i++)
            {
                String sequenceElemetnName = (String) serviceNameList.get(i);
                String serviceLogicalName = getConfigProperty(CONFIG_SEQUENCE, sequenceElemetnName, Constant.PARENT_NAME);
                _serviceList.add(Cornerstone.getServiceManager().createServiceByName(serviceLogicalName));
            }
        }

        return _serviceList;
    }

    protected List _serviceList = null;
    private static Logger _Logger = Logger.getLogger(SequenceServiceController.class);
}