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

package org.apache.jetspeed.sso;

/**
 * Interface SSOSite
 * 
 * @author rruttimann
 *
 */
public interface SSOSite {	
	/**
	 * @return Returns the isAllowUserSet.
	 */
	public boolean isAllowUserSet() ;
	
	/**
	 * @param isAllowUserSet The isAllowUserSet to set.
	 */
	public void setAllowUserSet(boolean isAllowUserSet);
	
	/**
	 * @return Returns the isCertificateRequired.
	 */
	public boolean isCertificateRequired();
	
	/**
	 * @param isCertificateRequired The isCertificateRequired to set.
	 */
	public void setCertificateRequired(boolean isCertificateRequired);
	
	/**
	 * @return Returns the name.
	 */
	public String getName() ;
	
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) ;
	
	/**
	 * @return Returns the id of the site.
	 */
	public int getId() ;
	
	/**
	 * @return Returns the siteURL.
	 */
	public String getURL() ;
	
	/**
	 * @param siteURL The siteURL to set.
	 */
	public void setURL(String siteURL) ;
	
    /**
     * Define the Authentication methods. 
     * Supported are: Challenge Response and From based
     */
    public void setFormAuthentication(boolean isFormAuthentication);
   
    /**
     * Form authentication requires two fields that hold the credential 
     * information for the request.
     */
   
    public void configFormAuthentication(String formUserField, String formPwdField);
    
    /*
     * Uses Challenge Response mechanism for authentication
     */
    public void setChallengeResponseAuthentication(boolean isChallengeResponseAuthentication);
    
    public boolean isChallengeResponseAuthentication();
	public boolean isFormAuthentication();

	public String getFormPwdField();
	public void setFormPwdField(String formPwdField);

	public String getFormUserField();
	public void setFormUserField(String formUserField);
	
	public void setRealm(String realm);
	public String getRealm();
	
    public Long getSecurityDomainId();
    public void setSecurityDomainId(Long securityDomain);
}
