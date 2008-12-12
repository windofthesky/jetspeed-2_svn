/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.jetspeed.container.state.impl;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.jetspeed.cache.JetspeedContentCache;
import org.apache.jetspeed.container.PortletWindow;

/**
 * HybridNavigationalState
 * 
 * Only encodes render parameters that start with a given prefix
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: AbstractNavigationalState.java 333093 2005-11-13 18:42:42Z taylor $
 */
public class HybridNavigationalState extends SessionNavigationalState
{
    protected String prefix;
    
    public HybridNavigationalState(NavigationalStateCodec codec, String prefix, JetspeedContentCache cache)
    {
        super(codec, cache);
        this.prefix = prefix;
    }
    
    public String encode(PortletWindow window, Map parameters, PortletMode mode, WindowState state, boolean action)
    throws UnsupportedEncodingException
    {
        Map subset = new HashMap();
        Iterator params = parameters.keySet().iterator();
        while (params.hasNext())
        {
            String key = (String)params.next();
            if (key.startsWith(prefix))
            {
                // only encode params that start with prefix
                subset.put(key, parameters.get(key));
            }
        }
        return super.encode(window, subset, mode, state, action);
    }

    public boolean isNavigationalParameterStateFull()
    {
        return true;
    }

    public boolean isRenderParameterStateFull()
    {
        return false;
    }
    
    
}
