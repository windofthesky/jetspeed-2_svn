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
package org.apache.jetspeed.container.invoker;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Local servlet request wrapper. The purpose of this wrapper is to hold 
 * attribute information that is need for each request.  In a threaded environment, 
 * each thread needs to have its own copy of this information so that there is 
 * not a timing issue with the original request object.  
 * Also, since the original request is no longer "holding" the attributes, 
 * there is no reason to remove them in the finally block.  
 * The LocalServletRequest object is automatically garbage collected at then 
 * end of this method.
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @author <a href="">David Gurney</a>
 * @version $Id: $
 */
public class LocalServletRequest extends HttpServletRequestWrapper
{
    private Map attributeMap = new HashMap();

    private HttpServletRequest originalRequest = null;

    public LocalServletRequest(HttpServletRequest request)
    {
        super(request);
        originalRequest = request;
    }

    public Object getAttribute(String p_sKey)
    {
        Object a_oValue = attributeMap.get(p_sKey);
        if (a_oValue == null)
        {
            a_oValue = originalRequest.getAttribute(p_sKey);
        }

        return a_oValue;
    }

    public void removeAttribute(String key)
    {
        Object value = attributeMap.remove(key);
        if (value == null)
        {
            originalRequest.removeAttribute(key);
        }
    }

    public void setAttribute(String key, Object value)
    {
        attributeMap.put(key, value);
    }

}