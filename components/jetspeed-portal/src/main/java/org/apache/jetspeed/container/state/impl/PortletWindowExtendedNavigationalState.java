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
package org.apache.jetspeed.container.state.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

/**
 * PortletWindowExtendedNavigationalState
 *
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id$
 */
public class PortletWindowExtendedNavigationalState extends PortletWindowBaseNavigationalState
{
    private static final long serialVersionUID = -504769105830572875L;

    private static final class ModeStateKey implements Serializable
    {
        private static final long serialVersionUID = 1419744882315564633L;
        
        private final String mode;
        private final String state;
        private final int hashCode;
        
        public ModeStateKey(PortletMode mode, WindowState state)
        {
            this.mode = (mode != null ? mode.toString() : PortletMode.VIEW.toString()).intern() ;
            this.state = (state != null ? state.toString() : WindowState.NORMAL.toString()).intern();
            hashCode = this.mode.hashCode()+this.state.hashCode();
        }
        
        public boolean equals(Object obj)
        {
            if (obj != null && obj instanceof ModeStateKey)
            {
                ModeStateKey key = (ModeStateKey)obj;
                return mode.equals(key.mode) && state.equals(key.state);
            }
            return false;
        }

        public int hashCode()
        {
            return hashCode;
        }
    }
    
    private Map<String, String[]> parametersMap;

    private String actionScopeId;
    
    private boolean actionScopeRendered;
    
    private Map<ModeStateKey, String> decoratorActionEncodings;

    public Map<String, String[]> getParametersMap()
    {
        return parametersMap;
    }

    public void setParameters(String name, String[] values)
    {
        if ( parametersMap == null )
        {
            parametersMap = new HashMap<String, String[]>();
        }
        parametersMap.put(name, values);
    }    
    
    public void setParametersMap(Map<String, String[]> parametersMap)
    {
        this.parametersMap = parametersMap;
    }
    
    public String getActionScopeId()
    {
        return actionScopeId;
    }
    
    public void setActionScopeId(String actionScopeId)
    {
        this.actionScopeId = actionScopeId;
    }
    
    public boolean isActionScopeRendered()
    {
        return actionScopeRendered;
    }
    
    public void setActionScopeRendered(boolean actionScopeRendered)
    {
        this.actionScopeRendered = actionScopeRendered;
    }
    
    public void resetDecoratorActionEncodings()
    {
        if (decoratorActionEncodings != null)
        {
            decoratorActionEncodings.clear();
        }
    }
    
    public void setDecoratorActionEncoding(PortletMode mode, WindowState state, String encoding)
    {
        if (decoratorActionEncodings == null)
        {
            decoratorActionEncodings = new HashMap<ModeStateKey, String>(4);
        }
        decoratorActionEncodings.put(new ModeStateKey(mode,state), encoding);
    }
    
    public String getDecoratorActionEncoding(PortletMode mode, WindowState state)
    {
        if (decoratorActionEncodings != null)
        {
            return decoratorActionEncodings.get(new ModeStateKey(mode,state));
        }
        return null;
    }
}
