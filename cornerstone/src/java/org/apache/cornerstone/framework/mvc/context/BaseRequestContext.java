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

package org.apache.cornerstone.framework.mvc.context;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.cornerstone.framework.api.util.INameValueList;
import org.apache.cornerstone.framework.context.BaseContext;

public class BaseRequestContext extends BaseContext implements INameValueList
{
    public static final String REVISION = "$Revision$";

    public BaseRequestContext(HttpServletRequest request)
    {
        _request = request;
    }

    /* (non-Javadoc)
     * @see com.cisco.salesit.framework.common.util.INameValueList#getNameSet()
     */
    public Set getNameSet()
    {
        Set nameSet = new HashSet();
        for (Enumeration e = _request.getAttributeNames(); e.hasMoreElements();)
        {
            nameSet.add(e.nextElement());
        }
        return nameSet;
    }

    /* (non-Javadoc)
     * @see com.cisco.salesit.framework.common.util.INameValueList#getValue(java.lang.String)
     */
    public Object getValue(String name)
    {
        return _request.getAttribute(name);
    }

    /* (non-Javadoc)
     * @see com.cisco.salesit.framework.common.util.INameValueList#setValue(java.lang.String, java.lang.Object)
     */
    public void setValue(String name, Object value)
    {
        _request.setAttribute(name, value);
    }

    protected HttpServletRequest _request;
}