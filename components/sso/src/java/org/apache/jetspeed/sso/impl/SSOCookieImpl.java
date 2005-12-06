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

package org.apache.jetspeed.sso.impl;

import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Vector;

import org.apache.jetspeed.sso.SSOCookie;

/**
 * @author Roger Ruttimann <rogerrut@apache.org>
 *
 */
public class SSOCookieImpl implements SSOCookie {
	
	/**
	 * Internal for storing object values
	 */
	
	private int cookieId;
	private String cookie;
	private Timestamp createDate;
	private Collection remotePrincipals = new Vector();

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.sso.SSOCookie#setCookieId(int)
	 */
	public void setCookieId(int cookieId) {
		this.cookieId = cookieId;
	}

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.sso.SSOCookie#getCookieId()
	 */
	public int getCookieId() {
		return this.cookieId;
	}

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.sso.SSOCookie#setCookie(java.lang.String)
	 */
	public void setCookie(String cookieValue) {
		this.cookie = cookieValue;
	}

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.sso.SSOCookie#getCookie()
	 */
	public String getCookie() {
		return this.cookie;
	}

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.sso.SSOCookie#setCreateDate(java.sql.Timestamp)
	 */
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.sso.SSOCookie#getCreateDate()
	 */
	public Timestamp getCreateDate() {
		return this.createDate;
	}
	
	/**
	 * 
	 * @return
	 */
	public Collection getRemotePrincipals()
	{
		return this.remotePrincipals;
	}
	
	/**
	 * 
	 * @param remotePrincipals
	 */
	public void setRemotePrincipals(Collection remotePrincipals)
	{
		this.remotePrincipals = remotePrincipals;
	}
}
