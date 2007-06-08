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

package org.apache.jetspeed.cache.impl;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.apache.jetspeed.cache.ContentCacheKey;
import org.apache.jetspeed.desktop.JetspeedDesktop;
import org.apache.jetspeed.request.RequestContext;

/**
 * The content cache key holds an immutable cache key definition.
 * Cache key definitions are based on the following required properties:
 * <ul>
 * <li>username</li>
 * <li>windowid</li>
 * <li>pipeline</li>
 * </ul>
 * and the following optional properties:
 * <ul>
 * <li>sessionid</li>
 * <li></li>
 * <li>request.{parameter}</li>
 * <li>session.{attribute}</li>
 * </ul>
 * The string representation of this key is calculated once upon construction.
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class JetspeedContentCacheKey implements ContentCacheKey, Serializable
{
    private String username = null;
    private String pipeline = null;
    private String windowId = null;
    private String sessionId = null;
    private String requestParameter = null;
    private String sessionAttribute = null;
    private String key = "";
    
    public JetspeedContentCacheKey(List segments, RequestContext context, String windowId)
    {
        boolean first = true;
        Iterator si = segments.iterator();
        while (si.hasNext())
        {
            String segment = (String)si.next();
            if (segment.equals("username"))
            {
                this.username = context.getUserPrincipal().getName();
                key = (first) ? this.username : key + EhPortletContentCacheElementImpl.KEY_SEPARATOR + this.username;                 
            }
            else if (segment.equals("windowid"))
            {
                this.windowId = windowId;
                key = (first) ? this.windowId : key + EhPortletContentCacheElementImpl.KEY_SEPARATOR + this.windowId;
            }
            else if (segment.equals("sessionid"))
            {
                this.sessionId = context.getRequest().getSession().getId();
                key = (first) ? this.sessionId : key + EhPortletContentCacheElementImpl.KEY_SEPARATOR + this.sessionId;                
            }
            else if (segment.equals("pipeline"))
            {
                String encoder = context.getRequestParameter(JetspeedDesktop.DESKTOP_ENCODER_REQUEST_PARAMETER);
                if (encoder != null && encoder.equals(JetspeedDesktop.DESKTOP_ENCODER_REQUEST_PARAMETER_VALUE))
                {
                    this.pipeline = "desktop";
                }
                else
                {
                    this.pipeline = "portal";
                }
                key = (first) ? this.pipeline : key + EhPortletContentCacheElementImpl.KEY_SEPARATOR + this.pipeline;                
            }
            else if (segment.startsWith("request"))
            {
                int pos = segment.indexOf(".");
                if (pos > -1)
                {
                    String parameterName = segment.substring(pos);
                    this.requestParameter = context.getRequestParameter(parameterName);
                    if (this.requestParameter != null)
                    {
                        key = (first) ? this.requestParameter : key + EhPortletContentCacheElementImpl.KEY_SEPARATOR + this.requestParameter;
                    }
                }
            }
            else if (segment.startsWith("session"))
            {
                int pos = segment.indexOf(".");
                if (pos > -1)
                {
                    String attributeName = segment.substring(pos);
                    this.sessionAttribute = (String)context.getSessionAttribute(attributeName);
                    if (this.sessionAttribute != null)
                    {
                        key = (first) ? this.sessionAttribute : key + EhPortletContentCacheElementImpl.KEY_SEPARATOR + this.sessionAttribute;
                    }
                }                
            }                              
            first = false;
        }
        //System.out.println("*** CACHE KEY IS [" + key + "]");
    }
    
    public JetspeedContentCacheKey()
    {        
    }
    
    public void createFromUser(String username, String pipeline, String windowId)
    {
        this.setUsername(username);
        this.setPipeline(pipeline);
        this.setWindowId(windowId);
        this.key = this.getUsername() + 
                    EhPortletContentCacheElementImpl.KEY_SEPARATOR + 
                    this.getPipeline() +
                    EhPortletContentCacheElementImpl.KEY_SEPARATOR + 
                    this.getWindowId();
    }

    public void createFromSession(String sessionId, String pipeline, String windowId)
    {
        this.setSessionId(sessionId);
        this.setPipeline(pipeline);
        this.setWindowId(windowId);
        this.key = this.getSessionId() + 
        EhPortletContentCacheElementImpl.KEY_SEPARATOR + 
        this.getPipeline() +
        EhPortletContentCacheElementImpl.KEY_SEPARATOR + 
        this.getWindowId();        
    }
    
    public String getKey()
    {
        return this.key;
    }

    public String getPipeline()
    {
        return this.pipeline;
    }

    public String getRequestParameter()
    {
        return this.requestParameter;
    }

    public String getSessionAttribute()
    {
        return this.sessionAttribute;
    }

    public String getSessionId()
    {
        return this.sessionId;
    }

    public String getUsername()
    {
        return this.username;
    }

    public String getWindowId()
    {
        return this.windowId;
    }

    
    public void setPipeline(String pipeline)
    {
        this.pipeline = pipeline;
    }

    
    public void setRequestParameter(String requestParameter)
    {
        this.requestParameter = requestParameter;
    }

    
    public void setSessionAttribute(String sessionAttribute)
    {
        this.sessionAttribute = sessionAttribute;
    }

    
    public void setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
    }

    
    public void setUsername(String username)
    {
        this.username = username;
    }

    
    public void setWindowId(String windowId)
    {
        this.windowId = windowId;
    }
}
