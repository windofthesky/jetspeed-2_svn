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
