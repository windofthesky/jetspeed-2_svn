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

package org.apache.cornerstone.framework.action.controller;

import org.apache.cornerstone.framework.action.ActionManager;
import org.apache.cornerstone.framework.api.action.ActionException;
import org.apache.cornerstone.framework.api.action.IAction;
import org.apache.cornerstone.framework.api.context.IContext;

/*
 * Sample registry entry (mySwitchAction.reg.properties):
 * _.className=com.cisco.salesit.framework.action.core.SwitchActionController
 * switch.condition.name=switchVar
 * switch.case.switchVarCase1.name=actionForCase1
 * switch.case.switchVarCase2.name=actionForCase2
 * switch.case.default.name=actionForAllOtherCases
 */

public class SwitchActionController extends BaseActionController
{
    public static final String REVISION = "$Revision$";

    public static final String SWITCH = "switch";
    public static final String CONDITION = "condition";
    public static final String CASE = "case";
    public static final String DEFAULT = "default";
    public static final String DEFAULT_CASE_CONFIG_NAME = SWITCH + "." + CASE + "." + DEFAULT + "." + ActionManager.NAME;
    public static final String SWITCH_CONDITION_NAME = SWITCH + "." + CONDITION + "." + ActionManager.NAME;

    // TODO: dummy
    public static final String INVOKE_DIRECT_INPUTS = "";
    // TODO: dummy
    public static final String INVOKE_DIRECT_OUTPUT = "switchResult";
    // TODO: dummy
    public Object invokeDirect() throws ActionException
    {
        return null;
    }

    protected Object invokeMiddle(IContext context) throws ActionException
    {
        String switchCase = DEFAULT_CASE_CONFIG_NAME;
        String switchConditionName = getConfigProperty(SWITCH_CONDITION_NAME);
        if (switchConditionName == null)
        {
            throw new ActionException("config property '" + SWITCH_CONDITION_NAME + "' undefined");
        }
        String switchConditionValue = (String) context.getValue(switchConditionName);
        if (switchConditionValue != null)
        {
            switchCase = SWITCH + "." + CASE + "." + switchConditionValue + "." + ActionManager.NAME;
        }
        String actionName = getConfigProperty(switchCase);
        IAction action = ActionManager.getSingleton().createActionByName(actionName);
        if (action != null)
            return action.invoke(context);
        else
            return null;
    }
}