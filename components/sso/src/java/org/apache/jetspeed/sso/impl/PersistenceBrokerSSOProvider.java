/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.jetspeed.security.UserPrincipal;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.components.dao.InitablePersistenceBrokerDaoSupport;

import org.apache.jetspeed.sso.SSOContext;
import org.apache.jetspeed.sso.SSOException;
import org.apache.jetspeed.sso.SSOProvider;

import org.apache.jetspeed.security.SecurityHelper;
import org.apache.jetspeed.security.BasePrincipal;
import org.apache.jetspeed.security.om.impl.InternalCredentialImpl;
import org.apache.jetspeed.security.om.impl.InternalPrincipalImpl;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;

/**
* <p>Utility component to handle SSO requests</p>
* 
* @author <a href="mailto:rogerrut@apache.org">Roger Ruttimann</a>
*/
public class PersistenceBrokerSSOProvider extends
		InitablePersistenceBrokerDaoSupport implements SSOProvider {
	
	private Hashtable mapSite = new Hashtable();	
    	/**
     * PersitenceBrokerSSOProvider()
     * @param repository Location of repository mapping file.  Must be available within the classpath.
     * @param prefsFactoryImpl <code>java.util.prefs.PreferencesFactory</code> implementation to use.
     * @param enablePropertyManager  Whether or not we chould be suing the property manager.
     * @throws ClassNotFoundException if the <code>prefsFactoryImpl</code> argument does not reperesent
     * a Class that exists in the current classPath.
     */
    public PersistenceBrokerSSOProvider(String repositoryPath) throws ClassNotFoundException
    {
       super(repositoryPath);
    }

	
	/* (non-Javadoc)
	 * @see org.apache.jetspeed.sso.SSOProvider#hasSSOCredentials(javax.security.auth.Subject, java.lang.String)
	 */
	public boolean hasSSOCredentials(Subject subject, String site) {
		// Initialization
		SSOSiteImpl ssoSite = getSSOSiteObject(site);
		
		if ( ssoSite == null)
			return false;	// no entry for site
		
		// Get the principal from the subject
		BasePrincipal principal = (BasePrincipal)SecurityHelper.getBestPrincipal(subject, UserPrincipal.class);
		String fullPath = principal.getFullPath();
		
		// Filter the credentials for the given principals
		InternalCredentialImpl  credential = getCredential(ssoSite, fullPath);	
		
		if (credential == null)
			return false;
		else
			return true;
	}

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.sso.SSOProvider#addBasicAuthenticationForSite(javax.servlet.http.HttpServletRequest, javax.security.auth.Subject, java.lang.String)
	 */
	public void addBasicAuthenticationForSite(HttpServletRequest request,
			Subject subject, String site) throws SSOException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.sso.SSOProvider#getCredentials(javax.security.auth.Subject, java.lang.String)
	 */
	public SSOContext getCredentials(Subject subject, String site)
			throws SSOException {
		
		// Initialization
		SSOSiteImpl ssoSite = getSSOSiteObject(site);
		
		if ( ssoSite == null)
			throw new SSOException(SSOException.NO_CREDENTIALS_FOR_SITE);	// no entry for site
		
		// Get the principal from the subject
		BasePrincipal principal = (BasePrincipal)SecurityHelper.getBestPrincipal(subject, UserPrincipal.class);
		String fullPath = principal.getFullPath();
		
		// Filter the credentials for the given principals
		InternalCredentialImpl  credential = getCredential(ssoSite, fullPath);	
		
		if ( credential == null)
			throw new SSOException(SSOException.NO_CREDENTIALS_FOR_SITE);	// no entry for site
		
		SSOContext context = new SSOContextImpl(credential.getPrincipalId(), principal.getName(),credential.getValue());
		
		return context;
	}

	/* addCredential()
		 * Adds credentials for a user to the site. If the site doesn't exist it will be created
	 * @see org.apache.jetspeed.sso.SSOProvider#addCredentialsForSite(javax.security.auth.Subject, java.lang.String, java.lang.String)
	 */
	public void addCredentialsForSite(Subject subject, String site, String pwd)
			throws SSOException {
		
		// Check if the site already exists
		SSOSiteImpl ssoSite = getSSOSiteObject(site);
		if (ssoSite == null)
		{
			// Create a new site
			ssoSite = new SSOSiteImpl();
			ssoSite.setSiteURL(site);
		}
		
		// Get the Principal information
		String fullPath = ((BasePrincipal)SecurityHelper.getBestPrincipal(subject, UserPrincipal.class)).getFullPath();
			
		SSOPrincipalImpl principal = this.getPrincipalForPath(subject, fullPath);
		
		// New credential object
		InternalCredentialImpl credential = new InternalCredentialImpl();
		ssoSite.addCredential(credential);
		
		// Populate the credential information
		credential.setValue(pwd);
		credential.setPrincipalId(principal.getPrincipalId());
		
		// Update database and reset cache
		 try
         {
             getPersistenceBrokerTemplate().store(ssoSite);
          }
         catch (Exception e)
         {
            throw new SSOException(SSOException.FAILED_STORING_SITE_INFO_IN_DB + e.toString() );
         }
         
         // Clear cache
         this.mapSite.clear();

	}

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.sso.SSOProvider#removeCredentialsForSite(javax.security.auth.Subject, java.lang.String)
	 */
	public void removeCredentialsForSite(Subject subject, String site)
			throws SSOException {
		// TODO Auto-generated method stub

	}
	
	/*
	 * Helper utilities
	 * 
	 */
	
	/*
	 * getSSOSiteObject
	 * Obtains the Site information including the credentials for a site (url).
	 */
	
	private SSOSiteImpl getSSOSiteObject(String site)
	{
		//Initialization
		SSOSiteImpl ssoSite = null;
		
		//Check if the site is in the map
		if (mapSite.containsKey(site) == false )
		{
			//	Go to the database and fetch the information for this site
			//	Find the MediaType by matching the Mimetype
		            
		    Criteria filter = new Criteria();       
		    filter.addEqualTo("siteURL", site);
		    
		    QueryByCriteria query = QueryFactory.newQuery(SSOSiteImpl.class, filter);
		    Collection ssoSiteCollection = getPersistenceBrokerTemplate().getCollectionByQuery(query);                    
		    
		    if ( ssoSiteCollection != null && ssoSiteCollection.isEmpty() != true)
		    {
		    	Iterator itSite = ssoSiteCollection.iterator();
		    	// Get the site from the collection. There should be only one entry (uniqueness)
		    	if (itSite.hasNext())
			    {
			    	ssoSite = (SSOSiteImpl) itSite.next();
			    }
		    	
		    	// Add it to the map
		    	mapSite.put(site, ssoSite);
		    }
		    else
		    {
		    	// No entry for this site
		    	return null;
		    }
		}
		else
		{
			ssoSite = (SSOSiteImpl)mapSite.get(site);
		}
		
		return ssoSite;
	}
	
	/*
	 * getCredential
	 * returns the credentials for a given user
	 */
	private InternalCredentialImpl  getCredential(SSOSiteImpl ssoSite, String fullPath)
	{
		long  principalID = -1;
		InternalCredentialImpl credential = null;
		
		/* Error checking
		 * 1) should have at least one principal
		 * 2) should have at least one credential
		 * 
		 * If one of the above fails return null wich means that the user doesn't have credentials for that site
		 */
		if ( ssoSite.getPrincipals() == null || ssoSite.getCredentials() == null)
			return null;
		
		// Iterate over the principals and extract the principal id for the given full path
		Iterator itPrincipals = ssoSite.getPrincipals().iterator();
		while (itPrincipals.hasNext() && principalID == -1 /*not found yet*/)
		{
			InternalPrincipalImpl principal = (InternalPrincipalImpl)itPrincipals.next();
			if ( principal != null && principal.getFullPath().compareToIgnoreCase(fullPath) == 0)
			{
				principalID = principal.getPrincipalId();
			}
		}
		
		if ( principalID == -1)
			return null;	// No principal found for that site
		
		// Last lookup to see if there are credentials for that user
		Iterator itCredentials = ssoSite.getCredentials().iterator();
		while (itCredentials.hasNext() && credential == null /*not found yet*/)
		{
			InternalCredentialImpl cred = (InternalCredentialImpl)itCredentials.next();
			if ( cred != null && cred.getPrincipalId() == principalID)
			{
				// Found credentials for Orincipals
				credential = cred;
			}
		}
		
		return credential;
	}
	
	private SSOPrincipalImpl getPrincipalForPath(Subject subject, String fullPath)
	{
		Criteria filter = new Criteria();       
	    filter.addEqualTo("fullPath", fullPath);
	    
	    QueryByCriteria query = QueryFactory.newQuery(SSOPrincipalImpl.class, filter);
	    Collection principals = getPersistenceBrokerTemplate().getCollectionByQuery(query);                    
	    
	    if ( principals != null && principals.isEmpty() != true)
	    {
	    	Iterator itPrincipals = principals.iterator();
	    	// Get the site from the collection. There should be only one entry (uniqueness)
	    	if (itPrincipals.hasNext())
		    {
		    	return (SSOPrincipalImpl) itPrincipals.next();
		    }
	    }
	    
	    // Principal for path doesn't exist
	    return null;
	    
	}
}
