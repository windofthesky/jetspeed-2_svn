/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.container.state.impl;

import java.util.HashMap;
import java.util.Map;

/**
 * PortletWindowExtendedNavigationalState
 *
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id: PortletWindowExtendedNavigationalState.java 187753 2004-10-15 21:47:25Z ate $
 */
public class PortletWindowExtendedNavigationalState extends PortletWindowBaseNavigationalState
{
    private Map parametersMap;
        
    public Map getParametersMap()
    {
        return parametersMap;
    }

    public void setParameters(String name, String[] values)
    {
        if ( parametersMap == null )
        {
            parametersMap = new HashMap();
        }
        parametersMap.put(name, values);
    }    
    
    public void setParametersMap(Map parametersMap)
    {
        this.parametersMap = parametersMap;
    }
}
