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
import java.util.Iterator;
import java.util.Vector;

import org.apache.jetspeed.sso.SSOException;
import org.apache.jetspeed.sso.SSOSite;
import org.apache.jetspeed.sso.SSOPrincipal;

/**
* SSOSiteImpl
* 	Class holding information about the Site and credentials for Single Sign on SSO.
*	OJB will map the database entries into this class
*
* @author <a href="mailto:rogerrut@apache.org">Roger Ruttimann</a>
* @version $Id$
*/

public class SSOSiteImpl implements SSOSite {
	
	// Private member for OJB mapping
	private int		siteId;
	private String	name;
	private String	siteURL;
	private boolean	isAllowUserSet;
	private boolean isCertificateRequired;
	
	private boolean	isChallangeResponseAuthentication;
	
	/* Realm used to do ChallengeResponse Authentication */
	private String	realm;
	
	private boolean	isFormAuthentication;
	
	/* Names of fields for User and Password values. The names are up to the
	 * application developer and therefore it must be configurable for SSO*/
	private String	formUserField;
	private String	formPwdField;
	
	private Collection	principals = new Vector();
	private Collection	remotePrincipals = new Vector();
	
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
		return this.principals;
	}
	/**
	 * @param principals The principals to set.
	 */
	public void setPrincipals(Collection principals) {
		this.principals.addAll(principals);
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
	
	
	
		/**
		 * addPrincipal
		 * Adds the SSOPrincipal to the principals collection
		 *
		 */
		public void addPrincipal(SSOPrincipal principal) throws SSOException {
			boolean bStatus = false;
			
			try
			{
				bStatus = principals.add(principal);
			}
			catch(Exception e)
			{
				// Adding credentail to coollection failed -- notify caller with SSOException
				throw new SSOException(SSOException.FAILED_ADDING_PRINCIPAL_TO_MAPPING_TABLE_FOR_SITE + e.getMessage()); 
			}
			
			if ( bStatus == false)
				throw new SSOException(SSOException.FAILED_ADDING_PRINCIPAL_TO_MAPPING_TABLE_FOR_SITE ); 	
		}
		
		/**
		* removePrincipal()
		 * removes a principal from the principals collection
		 *
		 */
		public void removePrincipal(long principalId) throws SSOException
		{
			boolean bStatus = false;
			SSOPrincipal principalObj = null;
			Iterator itSitePrincipals = principals.iterator();
			
			while (itSitePrincipals.hasNext() )
			{
				principalObj = (SSOPrincipal)itSitePrincipals.next();
				if ( principalObj.getPrincipalId() == principalId)
				{
				
					try
					{
						bStatus = principals.remove(principalObj);
					}
					catch(Exception e)
					{
						// Adding credentail to coollection failed -- notify caller with SSOException
						throw new SSOException(SSOException.FAILED_REMOVING_PRINCIPAL_FROM_MAPPING_TABLE_FOR_SITE + e.getMessage()); 
					}
					
					if ( bStatus == false)
						throw new SSOException(SSOException.FAILED_REMOVING_PRINCIPAL_FROM_MAPPING_TABLE_FOR_SITE ); 
				}
					
			}
		}
    /**
     * @return Returns the remotePrincipals.
     */
    public Collection getRemotePrincipals() {
        return remotePrincipals;
    }
    /**
     * @param remotePrincipals The remotePrincipals to set.
     */
    public void setRemotePrincipals(Collection remotePrincipals) {
        this.remotePrincipals = remotePrincipals;
    }
    
    /**
     * Define the Authentication methods. 
     * Supported are: Challenge Response and From based
     */
    /**
     * Form authentication requires two fields that hold the credential 
     * information for the request.
     */
    public void setFormAuthentication(String formUserField, String formPwdField)
    {
    	// Set the fields for Form Authentication and clear other authentication methods
    	
    }
    
    /*
     * Uses Challenge Response mechanism for authentication
     */
    public void setChallengeResponseAuthentication()
    {
    	// Set the fields for ChallengeResponse and clear other authentication methods
    	
    }

    /* Setters/Getters for Authentication settings */
	public String getFormPwdField() {
		return formPwdField;
	}

	public void setFormPwdField(String formPwdField) {
		this.formPwdField = formPwdField;
	}

	public String getFormUserField() {
		return formUserField;
	}

	public void setFormUserField(String formUserField) {
		this.formUserField = formUserField;
	}

	public boolean isChallangeResponseAuthentication() {
		return isChallangeResponseAuthentication;
	}

	public void setChallengeResponseAuthentication(
			boolean isChallangeResponseAuthentication) {
		this.isChallangeResponseAuthentication = isChallangeResponseAuthentication;
	}

	public boolean isFormAuthentication() {
		return isFormAuthentication;
	}

	public void setFormAuthentication(boolean isFormAuthentication) {
		this.isFormAuthentication = isFormAuthentication;
	}
	
	public void configFormAuthentication(String formUserField, String formPwdField)
	{
		this.isFormAuthentication = true;
		this.setChallengeResponseAuthentication(false);
		
		this.formPwdField	=	formPwdField;
		this.formUserField	=	formUserField;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.apache.jetspeed.sso.SSOSite#setRealm(java.lang.String)
	 */
	public void setRealm(String realm)
	{
		this.realm = realm;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.apache.jetspeed.sso.SSOSite#getRealm()
	 */
	public String getRealm()
	{
		return this.realm;
	}    
}
