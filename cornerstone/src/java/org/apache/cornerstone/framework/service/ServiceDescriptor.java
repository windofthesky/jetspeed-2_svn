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

package org.apache.cornerstone.framework.service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.cornerstone.framework.api.service.IService;
import org.apache.cornerstone.framework.api.service.IServiceDescriptor;
import org.apache.cornerstone.framework.api.service.InvalidServiceException;
import org.apache.cornerstone.framework.core.BaseObject;
import org.apache.cornerstone.framework.util.Util;

public class ServiceDescriptor extends BaseObject implements IServiceDescriptor
{
    public static final String REVISION = "$Revision$";

    public static final String INVOKE_DIRECT_INPUTS = "INVOKE_DIRECT_INPUTS";
    public static final String INVOKE_DIRECT_OUTPUT = "INVOKE_DIRECT_OUTPUT";

    public Class getServiceClass()
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

    public ServiceDescriptor()
    {
    }

    public ServiceDescriptor(IService service, Method invokeDirect)
        throws InvalidServiceException
    {
        _class = service.getClass();
        _method = invokeDirect;

        String invokeDirectInputs = service.getConfigProperty(IService.META_INVOKE_DIRECT_INPUTS);
        if (invokeDirectInputs == null)
        {
            Field invokeDirectInputsField = getField(INVOKE_DIRECT_INPUTS);
            invokeDirectInputs = (String) getStaticFieldValue(INVOKE_DIRECT_INPUTS, invokeDirectInputsField);
        }
        _inputNames =
            (invokeDirectInputs == null) ?
            new String[0] :
            Util.convertStringsToArray(invokeDirectInputs);

        _outputName = service.getConfigProperty(IService.META_INVOKE_DIRECT_OUTPUT);
        if (_outputName == null)
        {
            Field invokeDirectOutputField = getField(INVOKE_DIRECT_OUTPUT);
            _outputName = (String) getStaticFieldValue(INVOKE_DIRECT_OUTPUT, invokeDirectOutputField);
        }
        if (_outputName == null || _outputName.length() == 0)
        {
            throw new InvalidServiceException(_class + ".INVOKE_DIRECT_OUTPUT must be a non-empty string");
        }

        _inputTypes = _method.getParameterTypes();
        if (_inputTypes.length != _inputNames.length)
        {
            throw new InvalidServiceException("Number of input parameters described in " + _class + ".INVOKE_DIRECT_INPUTS inconsistent with method " + _method.getName());
        }

        _outputType = _method.getReturnType();
        _valid = true;
    }

    protected static Map _FieldErrorMap = new HashMap();
    static
    {
        _FieldErrorMap.put(
            INVOKE_DIRECT_INPUTS, 
            "must define a 'public static final String INVOKE_DIRECT_INPUTS = \"<comma separated names of input parameters of invokeDirect()>\";'"
        );
        _FieldErrorMap.put(
            INVOKE_DIRECT_OUTPUT, 
            "must define a 'public static final String INVOKE_DIRECT_OUTPUT = \"<name of output parameter (i.e. return value) of invokeDirect()>\";'"
        );
    }

    protected Field getField(String fieldName)
        throws InvalidServiceException
    {
        try
        {
            return _class.getField(fieldName);
        }
        catch(Exception e)
        {
            throw new InvalidServiceException(_class + " " + _FieldErrorMap.get(fieldName));
        }
    }

    protected Object getStaticFieldValue(String fieldName, Field field)
        throws InvalidServiceException
    {
        try
        {
            return field.get(null);
        }
        catch(Exception e)
        {
            throw new InvalidServiceException(_class + " " + _FieldErrorMap.get(fieldName));
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