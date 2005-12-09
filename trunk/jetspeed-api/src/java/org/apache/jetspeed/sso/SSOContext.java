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

package org.apache.jetspeed.sso;

/**
* <p>Represents SSO Remote and Portal principal and credentials</p>
* 
* @author <a href="mailto:rogerrut@apache.org">Roger Ruttimann</a>
*/

public interface SSOContext 
{

	// Getters only. The interface shouldn't allow any changes
	public long		getRemotePrincipalId();
    public String   getPortalPrincipalName();
	public String	getRemotePrincipalName();
	public String	getRemoteCredential();
}
