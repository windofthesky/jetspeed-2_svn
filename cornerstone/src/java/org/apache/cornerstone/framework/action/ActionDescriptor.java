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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.cornerstone.framework.api.action.IAction;
import org.apache.cornerstone.framework.api.action.IActionDescriptor;
import org.apache.cornerstone.framework.api.action.InvalidActionException;
import org.apache.cornerstone.framework.core.BaseObject;
import org.apache.cornerstone.framework.util.Util;

public class ActionDescriptor extends BaseObject implements IActionDescriptor
{
    public static final String REVISION = "$Revision$";

    public static final String INVOKE_DIRECT_INPUTS = "INVOKE_DIRECT_INPUTS";
    public static final String INVOKE_DIRECT_OUTPUT = "INVOKE_DIRECT_OUTPUT";

    public Class getActionClass()
    {
        return _class;
    }

    public Method getMethod()
    {
        return _method;
    }

    public String[] getInputNames()
    {
        return _inputNames;
    }

    public Class[] getInputTypes()
    {
        return _inputTypes;
    }

    public String getOutputName()
    {
        return _outputName;
    }

    public Class getOutputType()
    {
        return _outputType;
    }

    public boolean isValid()
    {
        return _valid;
    }

    public void setValid(boolean valid)
    {
        _valid = valid;
    }

    public ActionDescriptor()
    {
    }

    public ActionDescriptor(IAction action, Method invokeDirect) throws InvalidActionException
    {
        _class = action.getClass();
        _method = invokeDirect;

        String invokeDirectInputs = action.getConfigProperty(IAction.META_INVOKE_DIRECT_INPUTS);
        if (invokeDirectInputs == null)
        {
            Field invokeDirectInputsField = getField(INVOKE_DIRECT_INPUTS);
            invokeDirectInputs = (String) getStaticFieldValue(INVOKE_DIRECT_INPUTS, invokeDirectInputsField);
        }
        _inputNames = (invokeDirectInputs == null) ? new String[0] : Util.convertStringsToArray(invokeDirectInputs);

        _outputName = action.getConfigProperty(IAction.META_INVOKE_DIRECT_OUTPUT);
        if (_outputName == null)
        {
            Field invokeDirectOutputField = getField(INVOKE_DIRECT_OUTPUT);
            _outputName = (String) getStaticFieldValue(INVOKE_DIRECT_OUTPUT, invokeDirectOutputField);
        }
        if (_outputName == null || _outputName.length() == 0)
        {
            throw new InvalidActionException(_class + ".INVOKE_DIRECT_OUTPUT must be a non-empty string");
        }

        _inputTypes = _method.getParameterTypes();
        if (_inputTypes.length != _inputNames.length)
        {
            throw new InvalidActionException(
                "Number of input parameters described in "
                    + _class
                    + ".INVOKE_DIRECT_INPUTS inconsistent with method "
                    + _method.getName());
        }

        _outputType = _method.getReturnType();
        _valid = true;
    }

    protected static Map _FieldErrorMap = new HashMap();
    static {
        _FieldErrorMap.put(
            INVOKE_DIRECT_INPUTS,
            "must define a 'public static final String INVOKE_DIRECT_INPUTS = \"<comma separated names of input parameters of invokeDirect()>\";'");
        _FieldErrorMap.put(
            INVOKE_DIRECT_OUTPUT,
            "must define a 'public static final String INVOKE_DIRECT_OUTPUT = \"<name of output parameter (i.e. return value) of invokeDirect()>\";'");
    }

    protected Field getField(String fieldName) throws InvalidActionException
    {
        try
        {
            return _class.getField(fieldName);
        }
        catch (Exception e)
        {
            throw new InvalidActionException(_class + " " + _FieldErrorMap.get(fieldName));
        }
    }

    protected Object getStaticFieldValue(String fieldName, Field field) throws InvalidActionException
    {
        try
        {
            return field.get(null);
        }
        catch (Exception e)
        {
            throw new InvalidActionException(_class + " " + _FieldErrorMap.get(fieldName));
        }
    }

    protected Class _class;
    protected Method _method;
    protected String[] _inputNames;
    protected Class[] _inputTypes;
    protected String _outputName;
    protected Class _outputType;
    protected boolean _valid = false;
}