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

package org.apache.cornerstone.framework.action;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.cornerstone.framework.api.action.ActionException;
import org.apache.cornerstone.framework.api.action.IAction;
import org.apache.cornerstone.framework.api.action.IActionDescriptor;
import org.apache.cornerstone.framework.api.action.InvalidActionException;
import org.apache.cornerstone.framework.api.action.metric.IActionMetric;
import org.apache.cornerstone.framework.api.context.IContext;
import org.apache.cornerstone.framework.api.core.BaseException;
import org.apache.cornerstone.framework.core.BaseObject;
import org.apache.log4j.Logger;

/**
Superclass of all action classes.
*/

public abstract class BaseAction extends BaseObject implements IAction
{
    public static final String REVISION = "$Revision$";

    public static final String USER_NAME = "userName";
    public static final String COOKIES = "cookies";
    public static final String REQUEST = "request";

    public static final String INVOKE_DIRECT = "invokeDirect";

    public String getName()
    {
        return getClass().getName();
    }

    public IActionMetric getMetric()
    {
        return _metric;
    }

    public void setMetric(IActionMetric metric)
    {
        _metric = metric;
    }

    /**
     * Invokes action.  A action can have any number of input parameters
     * and returns exactly one object as the value.  It can also make
     * changes to any number of properties of context as side effects.
     * @param context context object that has all input and output
     *   parameters.
     * @return value of action.
     * @exception ActionException
     */
    public final Object invoke(IContext context) throws ActionException
    {
        invokeStart(context);
        Object result = invokeMiddle(context);
        return invokeEnd(context, result);
    }

    public IActionDescriptor getDescriptor() throws InvalidActionException
    {
        String actionName = getName();
        IActionDescriptor actionDescriptor = (IActionDescriptor) _ActionDescriptorMap.get(actionName);
        if (actionDescriptor == null)
        {
            actionDescriptor = buildDescriptor();
            _ActionDescriptorMap.put(actionName, actionDescriptor);
        }

        return actionDescriptor;
    }

    protected void invokeStart(IContext context) throws ActionException
    {
        _invokeStartTime = System.currentTimeMillis();
    }

    protected Object invokeMiddle(IContext context) throws ActionException
    {
        return callInvokeDirect(context);
    }

    protected Object invokeEnd(IContext context, Object result) throws ActionException
    {
        if (_metric != null)
        {
            long endTime = System.currentTimeMillis();
            _metric.addTimeStamp(context, _invokeStartTime, endTime);
        }

        return result;
    }

    protected Object callInvokeDirect(IContext context) throws ActionException
    {
        try
        {
            IActionDescriptor sd = getDescriptor();

            String[] inputNames = sd.getInputNames();
            Class[] inputTypes = sd.getInputTypes();
            Object[] args = new Object[inputNames.length];

            for (int i = 0; i < inputNames.length; i++)
            {
                String inputName = inputNames[i];
                Class inputType = inputTypes[i];
                Object input = context.getValue(inputName);
                if (input != null && !inputType.isAssignableFrom(input.getClass()))
                {
                    throw new ActionException(
                        new InvalidActionException(
                            sd.getActionClass() + ": parameter '" + inputName + "' is not assignable to type " + inputType));
                }

                args[i] = input;
            }

            Method invokeDirect = sd.getMethod();
            Object result = invokeDirect.invoke(this, args);

            String outputName = sd.getOutputName();
            context.setValue(outputName, result);
            return result;

        }
        catch (ActionException se)
        {
            throw se;
        }
        catch (InvalidActionException ise)
        {
            throw new ActionException(ise);
        }
        catch (InvocationTargetException ite)
        {
            Throwable targetException = ite.getTargetException();
            if (targetException instanceof ActionException)
            {
                throw (ActionException) targetException;
            }
            else if (targetException instanceof BaseException)
            {
                throw new ActionException(((BaseException) targetException).getCause());
            }
            else
            {
                throw new ActionException(targetException);
            }
        }
        catch (IllegalAccessException iae)
        {
            throw new ActionException(iae);
        }

    }

    protected IActionDescriptor buildDescriptor() throws InvalidActionException
    {
        Method[] methods = getClass().getMethods();
        Method invokeDirect = null;
        int invokeDirectCount = 0;
        for (int i = 0; i < methods.length; i++)
        {
            if (methods[i].getName().equals(INVOKE_DIRECT))
            {
                invokeDirectCount++;
                invokeDirect = methods[i];
            }
        }

        switch (invokeDirectCount)
        {
            case 0 :
                {
                    throw new InvalidActionException(INVOKE_DIRECT + "() method undefined in " + getClass().getName());
                }

            case 1 :
                {
                    return new ActionDescriptor(this, invokeDirect);
                }

            default :
                {
                    throw new InvalidActionException(INVOKE_DIRECT + "() method defined more than once in " + getClass().getName());
                }
        }
    }

    protected static Map _ActionDescriptorMap = new HashMap();

    protected long _invokeStartTime;
    protected IActionMetric _metric;

    private Logger _Logger = Logger.getLogger(BaseAction.class);
}