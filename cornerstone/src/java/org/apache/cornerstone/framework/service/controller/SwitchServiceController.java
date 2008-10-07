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

import org.apache.cornerstone.framework.api.context.IContext;
import org.apache.cornerstone.framework.api.service.IService;
import org.apache.cornerstone.framework.api.service.IServiceManager;
import org.apache.cornerstone.framework.api.service.ServiceException;
import org.apache.cornerstone.framework.constant.Constant;
import org.apache.cornerstone.framework.init.Cornerstone;

/*
 * Sample registry entry (mySwitchService.reg.properties):
 * _.className=com.cisco.salesit.framework.service.core.SwitchServiceController
 * switch.condition.name=switchVar
 * switch.case.switchVarCase1.name=serviceForCase1
 * switch.case.switchVarCase2.name=serviceForCase2
 * switch.case.default.name=serviceForAllOtherCases
 */

public class SwitchServiceController extends BaseServiceController
{
    public static final String REVISION = "$Revision$";

    public static final String SWITCH = "switch";
    public static final String CONDITION = "condition";
    public static final String CASE = "case";
    public static final String DEFAULT = "default";

    public static final String CONFIG_DEFAULT_CASE_CONFIG_NAME = SWITCH + Constant.DOT + CASE + Constant.DOT + DEFAULT + Constant.DOT + Constant.PARENT_NAME;
    public static final String CONFIG_SWITCH_CONDITION_NAME = SWITCH + Constant.DOT + CONDITION + Constant.DOT + Constant.PARENT_NAME;

    // TODO: dummy
    public static final String INVOKE_DIRECT_INPUTS = "";
    // TODO: dummy
    public static final String INVOKE_DIRECT_OUTPUT = "switchResult";
    // TODO: dummy
    public Object invokeDirect() throws ServiceException
    {
        return null;
    }

    protected Object invokeMiddle(IContext context) throws ServiceException
    {
        String switchCase = CONFIG_DEFAULT_CASE_CONFIG_NAME;
        String switchConditionName = getConfigProperty(CONFIG_SWITCH_CONDITION_NAME);
        if (switchConditionName == null)
        {
            throw new ServiceException("config property '" + CONFIG_SWITCH_CONDITION_NAME + "' undefined");
        }
        String switchConditionValue = (String) context.getValue(switchConditionName);
        if (switchConditionValue != null) {
            switchCase = SWITCH + Constant.DOT + CASE + Constant.DOT + switchConditionValue + Constant.DOT + Constant.PARENT_NAME;
        }
        String serviceName = getConfigProperty(switchCase);
        IServiceManager serviceManager = (IServiceManager) Cornerstone.getImplementation(IServiceManager.class);
        IService service = serviceManager.createServiceByName(serviceName);
        if (service != null)
            return service.invoke(context);
        else
            return null;
    }
}