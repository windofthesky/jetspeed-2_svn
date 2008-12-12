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
package org.apache.jetspeed.ajax;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Request used for AJAX services.
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 *
 */
public class AJAXRequestImpl implements AJAXRequest
{
    public static final String AJAX_SERVICE = "ajax_service";
    public static final String AJAX_PARAM_PREFIX = "ajax_param_";
    
    private final HttpServletRequest request;
    private List ajaxParams;
    private final String serviceName;
    private final String methodName;
    private HttpServletResponse response;
    private ServletContext context;

    public AJAXRequestImpl(HttpServletRequest request, HttpServletResponse response, ServletContext context) throws AJAXException
    {
        this.request = request;
        this.response = response;
        this.context = context;
        String serviceRequest =  request.getParameter(AJAX_SERVICE);
        if(serviceRequest == null )
        {
            throw new AJAXException("No '"+AJAX_SERVICE+"' parameter could be found in the request or it was not in the '{service_name}.{method_name}' format.");
        }
        final String split = serviceRequest.split("\\.")[0];
        serviceName = split;
        methodName = serviceRequest.split("\\.")[1];
        
        parseRequestArguments();
        
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.ajax.AJAXRequest#getParameters()
     */
    public List getParameters()
    {
        return ajaxParams;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.ajax.AJAXRequest#getServiceName()
     */
    public String getServiceName()
    {
        return serviceName;
    }

    protected List parseRequestArguments() throws AJAXException
    {
        try
        {
            ajaxParams = new ArrayList();
            Map rawParams = request.getParameterMap();
            Iterator entryItr = rawParams.entrySet().iterator();
            while(entryItr.hasNext())
            {
                Map.Entry entry = (Map.Entry) entryItr.next();
                String key = entry.getKey().toString();
                
                if(key.startsWith(AJAX_PARAM_PREFIX))
                {
                    String[] paramInfo = key.split("_");
                    int index = Integer.parseInt(paramInfo[2]);
                    String type = paramInfo[3]; 
                    AJAXParameter ajaxParam = new AJAXParameter(type, (String[])entry.getValue());
                    ajaxParams.add(index, ajaxParam);
                }
            }
            return ajaxParams;
        }
        catch (Throwable e)
        {
            throw new AJAXException("Errors were encountered parsing request parameters for the AJAX service "+serviceName+": "+e.getMessage(), e);
        }
    }
    
    public class AJAXParameter
    {
        private Object value;
             
        public AJAXParameter(String typeName, String[] paramValues)
        {
            if(typeName.equals("int"))
            {                
                if(paramValues.length > 1)
                {
                    int[] intValues = new int[paramValues.length];
                    for(int i=0; i<paramValues.length; i++)
                    {
                        intValues[i] = Integer.parseInt(paramValues[i]);
                    }
                }
                else
                {
                    value = new Integer(paramValues[0]);
                }
            }
            else if(typeName.equals("str"))
            {
              if(paramValues.length > 1)
              {    
                  value = paramValues;
              }
              else
              {
                  value = paramValues[0];
              }
            }
        }
        
        public Object getValue()
        {
            return value;
        }
    }


    /* (non-Javadoc)
     * @see org.apache.jetspeed.ajax.AJAXRequest#getMethodName()
     */
    public String getMethodName()
    {
        return methodName;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.ajax.AJAXRequest#getContext()
     */
    public ServletContext getContext()
    {
        return context;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.ajax.AJAXRequest#getServletRequest()
     */
    public HttpServletRequest getServletRequest()
    {
        return request;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.ajax.AJAXRequest#getServletResponse()
     */
    public HttpServletResponse getServletResponse()
    {
        return response;
    }
}
