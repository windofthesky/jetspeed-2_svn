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

import java.util.Collection;
import org.apache.jetspeed.sso.SSOException;
import org.apache.jetspeed.security.om.impl.InternalCredentialImpl;

/**
* SSOSiteImpl
* 	Class holding information about the Site and credentials for Single Sign on SSO.
*	OJB will map the database entries into this class
*
* @author <a href="mailto:rogerrut@apache.org">Roger Ruttimann</a>
* @version $Id$
*/

public class SSOSiteImpl {
	
	// Private member for OJB mapping
	private int		siteId;
	private String	name;
	private String	siteURL;
	private boolean	isAllowUserSet;
	private boolean isCertificateRequired;
	
	private Collection	credentials;
	private Collection	principals;
	
	/**
	 * 
	 */
	public SSOSiteImpl() {
		super();
		
	}

	/*
	 * Setters and getters for member variables
	 */
	
	/**
	 * @return Returns the credentials.
	 */
	public Collection getCredentials() {
		return credentials;
	}
	/**
	 * @param credentials The credentials to set.
	 */
	public void setCredentials(Collection credentials) {
		this.credentials = credentials;
	}
	/**
	 * @return Returns the isAllowUserSet.
	 */
	public boolean isAllowUserSet() {
		return isAllowUserSet;
	}
	/**
	 * @param isAllowUserSet The isAllowUserSet to set.
	 */
	public void setAllowUserSet(boolean isAllowUserSet) {
		this.isAllowUserSet = isAllowUserSet;
	}
	/**
	 * @return Returns the isCertificateRequired.
	 */
	public boolean isCertificateRequired() {
		return isCertificateRequired;
	}
	/**
	 * @param isCertificateRequired The isCertificateRequired to set.
	 */
	public void setCertificateRequired(boolean isCertificateRequired) {
		this.isCertificateRequired = isCertificateRequired;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return Returns the principals.
	 */
	public Collection getPrincipals() {
		return principals;
	}
	/**
	 * @param principals The principals to set.
	 */
	public void setPrincipals(Collection principals) {
		this.principals = principals;
	}
	/**
	 * @return Returns the siteId.
	 */
	public int getSiteId() {
		return siteId;
	}
	/**
	 * @param siteId The siteId to set.
	 */
	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}
	/**
	 * @return Returns the siteURL.
	 */
	public String getSiteURL() {
		return siteURL;
	}
	/**
	 * @param siteURL The siteURL to set.
	 */
	public void setSiteURL(String siteURL) {
		this.siteURL = siteURL;
	}
	
	/**
	 * Utility functions
	 * addCredential()
	 * Adds the credentail to the credentials collection
	 *
	 */
	public void addCredential(InternalCredentialImpl credential) throws SSOException
	{
		boolean bStatus = false;
		
		try
		{
			bStatus = credentials.add(credential);
		}
		catch(Exception e)
		{
			// Adding credentail to coollection failed -- notify caller with SSOException
			throw new SSOException(SSOException.FAILED_ADDING_CREDENTIALS_FOR_SITE + e.getMessage()); 
		}
		
		if ( bStatus == false)
			throw new SSOException(SSOException.FAILED_ADDING_CREDENTIALS_FOR_SITE ); 
	}
	
	/**
	* removeCredential()
	 * removes a credentail from the credentials collection
	 *
	 */
	public void removeCredential(InternalCredentialImpl credential) throws SSOException
	{
		boolean bStatus = false;
		
		try
		{
			bStatus = credentials.remove(credential);
		}
		catch(Exception e)
		{
			// Adding credentail to coollection failed -- notify caller with SSOException
			throw new SSOException(SSOException.FAILED_REMOVING_CREDENTIALS_FOR_SITE + e.getMessage()); 
		}
		
		if ( bStatus == false)
			throw new SSOException(SSOException.FAILED_REMOVING_CREDENTIALS_FOR_SITE ); 
	}
}
