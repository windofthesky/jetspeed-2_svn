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

import java.util.ArrayList;
import java.util.List;

import org.apache.cornerstone.framework.action.ActionManager;
import org.apache.cornerstone.framework.api.action.ActionException;
import org.apache.cornerstone.framework.api.action.IAction;
import org.apache.cornerstone.framework.api.context.IContext;
import org.apache.cornerstone.framework.util.Util;
import org.apache.log4j.Logger;

/**
Action controller that invokes its actions in sequence.
*/

public class SequentialActionController extends BaseActionController
{
    public static final String REVISION = "$Revision$";

    public static final String SEQUENCE = "sequence";
    public static final String CLASS_NAME = "className";
    public static final String FACTORY_CLASS_NAME = "factoryClassName";
    public static final String REGISTRY_NAME = "name";

    /**
     * Invokes all my actions in sequence.
     * @param context context object that has all input and output
     *   parameters.
     * @return value of action.
     * @exception ActionException
     */
    protected Object invokeMiddle(IContext context) throws ActionException
    {
        List actionList = getActionList();
        Object result = null;

        if (actionList != null)
        {
            for (int i = 0; i < actionList.size(); i++)
            {
                IAction action = (IAction) actionList.get(i);
                result = action.invoke(context);
            }
        }

        return result;
    }

    /**
     * Gets list of action instances.
     * @return list of action instances.
     * @throws ActionException
     */
    protected List getActionList() throws ActionException
    {
        if (_actionList == null)
        {
            _actionList = new ArrayList();
            String actionListString = getConfigProperty(SEQUENCE);
            List actionNameList = Util.convertStringsToList(actionListString);
            for (int i = 0; i < actionNameList.size(); i++)
            {
                String actionName = (String) actionNameList.get(i);
                String actionRegistryName = getConfigProperty(SEQUENCE, actionName, REGISTRY_NAME);
                String actionFactoryClassName = getConfigProperty(SEQUENCE, actionName, FACTORY_CLASS_NAME);
                String actionClassName = getConfigProperty(SEQUENCE, actionName, CLASS_NAME);

                if (actionRegistryName != null)
                {
                    _actionList.add(ActionManager.getSingleton().createActionByName(actionRegistryName));
                }
                else if (actionFactoryClassName != null)
                {
                    _actionList.add(ActionManager.getSingleton().createActionByFactoryClassName(actionFactoryClassName));
                }
                else
                {
                    _actionList.add(ActionManager.getSingleton().createActionByFactoryClassName(actionClassName));
                }
            }
        }

        return _actionList;
    }

    protected List _actionList = null;
    private static Logger _Logger = Logger.getLogger(SequentialActionController.class);
}
