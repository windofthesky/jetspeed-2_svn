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

import java.util.Map;

import org.apache.jetspeed.request.RequestContext;

import org.apache.pluto.om.common.ObjectID;

/**
 * <p>The {@link UserInfoManager} retrieve the Map that will be set as a 
 * <code>(PortletRequest.USER_INFO</code> request attribute for a specific
 * portlet application</p>
 * <p>The portlet specification defines user info as follow (PLT 17):</p>
 * <p>Portlets can obtain an unmodifiable Map object containing the user attributes,
 * of user associated with the current request, from the request attributes.
 * The Map object can be retrieved using the USER_INFO constant defined in the
 * PortletRequest interface. If the request is done in the context of an
 * un-authenticated user, calls to the getAttribute method of the request 
 * using the USER_INFO constant must return null. If the user is
 * authenticated and there are no user attributes available, the Map must
 * be an empty Map. The Map object must contain a String name value pair for each available user
 * attribute. The Map object should only contain user attributes that have been mapped
 * during deployment.</p>
 * <p>Portlets can obtain an unmodifiable Map object containing the user attributes, of user
 * associated with the current request, from the request attributes. The Map object can be
 * retrieved using the USER_INFO constant defined in the PortletRequest interface. If the
 * request is done in the context of an un-authenticated user, calls to the getAttribute
 * method of the request using the USER_INFO constant must return null. If the user is
 * authenticated and there are no user attributes available, the Map must be an empty Map.
 * The Map object must contain a String name value pair for each available user attribute.
 * The Map object should only contain user attributes that have been mapped during
 * deployment.</p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public interface UserInfoManager
{
    
    /**
     * <p>Provide the user info map of user attributes for a given portlet application.</p>
     * <p>The MutablePortletApplication can be retrieved from a Fragment through:</p>
     * <p><code>MutablePortletApplication pa = getPortletApplication(portletFragment);</code></p>
     * @param oid The portlet application object id.
     * @param context The request context.
     * @return The {@link PortletRequest.USER_INFO} map.
     */
    Map getUserInfoMap(ObjectID oid, RequestContext context);
}
