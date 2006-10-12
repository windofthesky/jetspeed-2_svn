/* Copyright 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.om.common.ObjectID;


public class MockUserInfoManager implements UserInfoManager
{
    private Map fake = new HashMap();
    
    public MockUserInfoManager()
    {}
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.userinfo.UserInfoManager#getUserInfoMap(org.apache.pluto.om.common.ObjectID, org.apache.jetspeed.request.RequestContext)
     */
    public Map getUserInfoMap(ObjectID oid, RequestContext context)
    {
        return fake;
    }
    
}