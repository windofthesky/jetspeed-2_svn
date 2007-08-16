/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
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

import java.io.Serializable;

import org.apache.jetspeed.sso.SSOContext;

/**
* SSOContextImpl
* 	Class holding credential information 
*
* @author <a href="mailto:rogerrut@apache.org">Roger Ruttimann</a>
* @version $Id$
*/
public class SSOContextImpl implements SSOContext, Serializable
{
	private long	remotePrincipalId;
	private String remoteCredential;
	private String remotePrincipal;
    private String portalPrincipal;
	
	/**
	 * Constructor takes all arguments since members can't be altered
	 */
	public SSOContextImpl(long remotePrincipalId, String remotePrincipal, String remoteCredential) 
    {
		super();		
		this.remotePrincipalId = remotePrincipalId;
		this.remotePrincipal = remotePrincipal;
		this.remoteCredential = remoteCredential;
	}

    public SSOContextImpl(long remotePrincipalId, String remotePrincipal, String remoteCredential, String portalPrincipal) 
    {
        super();        
        this.remotePrincipalId = remotePrincipalId;
        this.remotePrincipal = remotePrincipal;
        this.remoteCredential = remoteCredential;
        this.portalPrincipal = portalPrincipal;
    }
    
	/* (non-Javadoc)
	 * @see org.apache.jetspeed.sso.SSOContext#getRemotePrincipalId()
	 */
	public long  getRemotePrincipalId() 
    {		
		return this.remotePrincipalId;
	}

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.sso.SSOContext#getRemotePrincipal()
	 */
	public String getRemotePrincipalName() 
    {
		return this.remotePrincipal;
	}

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.sso.SSOContext#getRemoteCredential()
	 */
	public String getRemoteCredential() 
    {		
		return this.remoteCredential;
	}

    /* (non-Javadoc)
     * @see org.apache.jetspeed.sso.SSOContext#getPortalPrincipal()
     */
    public String getPortalPrincipalName() 
    {
        return this.portalPrincipal;
    }
    
}
