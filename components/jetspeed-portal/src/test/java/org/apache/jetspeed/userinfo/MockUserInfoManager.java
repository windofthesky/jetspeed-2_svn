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
package org.apache.jetspeed.userinfo;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletRequest;

import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.PortletContainerException;
import org.apache.pluto.PortletWindow;


public class MockUserInfoManager implements UserInfoManager
{
    private Map<String, String> fake = new HashMap<String, String>();
    
    public MockUserInfoManager()
    {}
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.userinfo.UserInfoManager#getUserInfoMap(org.apache.pluto.om.common.ObjectID, org.apache.jetspeed.request.RequestContext)
     */
    public Map<String, String> getUserInfoMap(String appName, RequestContext context)
    {
        return fake;
    }

    public Map<String, String> getUserInfo(PortletRequest request, PortletWindow window) throws PortletContainerException
    {
        return fake;
    }
    
}