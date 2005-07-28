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
package org.apache.portals.bridges.util;

import java.util.Iterator;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;


/**
 * PreferencesHelper
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PreferencesHelper
{
    static public void requestParamsToPreferences(ActionRequest request) 
        throws PortletException
    {
        Map params = request.getParameterMap();
        PortletPreferences prefs = request.getPreferences();
        Map prefsMap = prefs.getMap();

        try
        {
            Iterator it = params.entrySet().iterator();
            while (it.hasNext())
            {
                Map.Entry entry = (Map.Entry) it.next();
                Object value = entry.getValue();
                String key = (String) entry.getKey();
                if (null == prefsMap.get(key))
                {
                    continue;
                }
                if (value instanceof String)
                {
                    prefs.setValue(key, (String)value);
                }
                else if (value instanceof String[])
                {
                    prefs.setValue(key, ((String[]) value)[0]);
                }
            }
        }
        catch (Exception e)
        {
            throw new PortletException("Exception mapping request Params to Preferences: ", e);
        }
    }

}
