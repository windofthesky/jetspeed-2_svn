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

package org.apache.jetspeed.sso.impl;

import org.apache.jetspeed.sso.SSOContext;

/**
* SSOContextImpl
* 	Class holding credential information 
*
* @author <a href="mailto:rogerrut@apache.org">Roger Ruttimann</a>
* @version $Id$
*/
public class SSOContextImpl implements SSOContext {

	private long	userID;
	private String password;
	private String userName;
	
	/**
	 * Constructor takes all arguments since members can't be altered
	 */
	public SSOContextImpl(long userID, String userName, String pwd) {
		super();
		
		this.userID			=	userID;
		this.userName	=	userName;
		this.password		=	pwd;
	}

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.sso.SSOContext#getUserID()
	 */
	public long  getUserID() {
		
		return this.userID;
	}

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.sso.SSOContext#getUserName()
	 */
	public String getUserName() {
		return this.userName;
	}

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.sso.SSOContext#getPassword()
	 */
	public String getPassword() {
		
		return this.password;
	}

}
