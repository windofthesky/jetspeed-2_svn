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
package org.apache.jetspeed.mockobjects.request;

import java.util.HashMap;
import java.util.Map;

import org.apache.jetspeed.PortalContext;
import org.apache.jetspeed.request.JetspeedRequestContext;
import org.apache.jetspeed.request.RequestContext;

/**
 * MockRequestContext
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class MockRequestContext extends JetspeedRequestContext implements RequestContext
{
    private Map requestParameters = new HashMap();
    private Map requestAttributes = new HashMap();
    private Map sessionAttributes = new HashMap();
    private String path;
        
    public MockRequestContext(PortalContext pc)
    {
        super(pc, null, null, null);
    }

    public MockRequestContext(PortalContext pc, String path)
    {
        super(pc, null, null, null);
        this.path = path;
    }
        
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getRequestParameter(java.lang.String)
     */
    public String getRequestParameter(String key)
    {
        return (String)requestParameters.get(key);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getParameterMap()
     */
    public Map getParameterMap()
    {
        return requestParameters;    
    }
            
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#setSessionAttribute(java.lang.String, java.lang.Object)
     */
    public void setSessionAttribute(String key, Object value)
    {
        this.sessionAttributes.put(key, value);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getSessionAttribute(java.lang.String)
     */
    public Object getSessionAttribute(String key)
    {
        return this.sessionAttributes.get(key);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#setAttribute(java.lang.String, java.lang.Object)
     */
    public void setAttribute(String key, Object value)
    {
        requestAttributes.put(key, value);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getAttribute(java.lang.String)
     */
    public Object getAttribute(String key)
    {
        return requestAttributes.get(key);    
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getPath()
     */
    public String getPath()
    {
        return path;
    }
    
}
