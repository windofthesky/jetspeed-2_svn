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

import java.io.BufferedInputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.security.auth.Subject;


/**
* <p>Utility component to handle SSO requests</p>
* 
* @author <a href="mailto:rogerrut@apache.org">Roger Ruttimann</a>
*/
public interface SSOProvider
{   
	/**
	 * Init
	 * Called from the Spring Framework to initialize SSO Provider component
	 * @throws Exception
	 */
   void init() throws Exception;
   
   /**
    * This method first authenticates the the SSOSite and then forwards the request
    * to the destination URL. The content will be returned as a string.
    * If the SSOSite and the url match only one call will be executed since the
    * authentication will be done while getting the result page.
    * 
    * @param userID
    * @param url
    * @param SSOSite
    * @param bRefresh if true it refreshes the proxy connection if false a cached proxy will be used
    * @return
    * @throws SSOException
    */
   public BufferedInputStream useSSO(Subject subject, String url, String SSOSite, boolean bRefresh) throws SSOException;
   
   /**
    * Same as the method above except that the user will be authenticated against all
    * SSOSites defined for the user before going to the destination site.
    * 
    * @param userID
    * @param url
    * @param bRefresh if true it refreshes the proxy connection if false a cached proxy will be used
    * @return
    * @throws SSOException
    */
   public BufferedInputStream useSSO(Subject subject, String url, boolean bRefresh) throws SSOException;
   
    
   /**
    * Retrive cookies for an user by User full path
    * @param fullPath
    * @return
    */
   Collection getCookiesForUser(String fullPath);
   
   /**
    * Retrive Cookies by Subject
    * @param user
    * @return
    */
   Collection getCookiesForUser(Subject user);
   
   
   /**
    * Public API's for SSO functinality
    * @return
    */
	boolean	hasSSOCredentials(Subject subject, String site);
        
	SSOContext getCredentials(Subject subject, String site)  
        throws SSOException;
    
	void  addCredentialsForSite(Subject subject, String remoteUser, String site, String pwd)  
        throws SSOException;
    
    void  updateCredentialsForSite(Subject subject, String remoteUser, String site, String pwd)  
    throws SSOException;
    
	void removeCredentialsForSite(Subject subject, String site)  
        throws SSOException;
    
    /**
     * return a list of SSOContext objects containing 
     * both the portal principal, remote principal, and credentials
     * 
     * @param site
     * @return list SSOContext objects 
     */
    List getPrincipalsForSite(SSOSite site);
    
    Iterator getSites(String filter);
    
    SSOSite getSite(String siteUrl);
    
    void updateSite(SSOSite site) throws SSOException;
    
    void addSite(String siteName, String siteUrl) throws SSOException; 
    
    void removeSite(SSOSite site) throws SSOException;
    
    /**
     * addCredentialsForSite()
     * @param fullPath
     * @param remoteUser
     * @param site
     * @param pwd
     * @throws SSOException
     */
    void addCredentialsForSite(String fullPath, String remoteUser, String site, String pwd) throws SSOException;
    
    /**
     * removeCredentialsForSite()
     * @param fullPath
     * @param site
     * @throws SSOException
     */
    void removeCredentialsForSite(String fullPath, String site) throws SSOException;

    /* Retrive site information */
    String getSiteURL(String site);
    String getSiteName(String site); 
    
    void	setRealmForSite(String site, String realm) throws SSOException;
    String	getRealmForSite(String site) throws SSOException;
    
    /**
     * Get all SSOSites that the principal has access to
     * @param userId
     * @return
     */
    public Collection getSitesForPrincipal(String userId);
    
}
