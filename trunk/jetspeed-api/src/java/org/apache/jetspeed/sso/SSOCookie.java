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

/*Created on: Nov 23, 2005 */

package org.apache.jetspeed.sso;

import java.sql.Timestamp;
import java.util.Collection;

/**
 * Interface SSOCookie
 * 
 * @author Roger Ruttimann <rogerrut@apache.org>
 * 
 * Class sthat handles the cookies created for SSO
 * principals
 *
 */
public interface SSOCookie {

	/** Setters and getters for cookie properties */
	
	/**
	 * 
	 * @param cookieId
	 */
	void setCookieId(int cookieId);
	/**
	 * 
	 * @return
	 */
	int	getCookieId();
	
	/**
	 * 
	 * @param cookieValue
	 */
	void setCookie(String cookieValue);
	/**
	 * 
	 * @return
	 */
	String getCookie();
	
	/**
	 * 
	 * @param createDate
	 */
	void setCreateDate(Timestamp createDate);
	/**
	 * 
	 * @return
	 */
	Timestamp getCreateDate();
	
	/**
	 * 
	 * @return
	 */
	Collection getRemotePrincipals();
	
	/**
	 * 
	 * @param remotePrincipals
	 */
	void setRemotePrincipals(Collection remotePrincipals);
}
