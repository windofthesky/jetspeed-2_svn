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

package org.apache.cornerstone.framework.service.controller;

import org.apache.cornerstone.framework.api.context.IContext;
import org.apache.cornerstone.framework.api.service.IService;
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
        IService service = Cornerstone.getServiceManager().createServiceByName(serviceName);
        if (service != null)
            return service.invoke(context);
        else
            return null;
    }
}