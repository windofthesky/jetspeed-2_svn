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
import org.apache.jetspeed.sso.SSOSite;


import org.apache.jetspeed.sso.impl.SSOSiteImpl;
import org.apache.jetspeed.sso.impl.SSOPrincipalImpl;

import org.apache.jetspeed.security.SecurityHelper;
import org.apache.jetspeed.security.BasePrincipal;
import org.apache.jetspeed.security.om.InternalCredential;
import org.apache.jetspeed.security.om.InternalPrincipal;
import org.apache.jetspeed.security.om.impl.InternalCredentialImpl;
import org.apache.jetspeed.security.spi.impl.DefaultPasswordCredentialImpl;

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
		SSOSite ssoSite = getSSOSiteObject(site);
		
		if ( ssoSite == null)
		{
			return false;	// no entry for site
		}
		
		// Get the principal from the subject
		BasePrincipal principal = (BasePrincipal)SecurityHelper.getBestPrincipal(subject, UserPrincipal.class);
		String fullPath = principal.getFullPath();
		
		// Filter the credentials for the given principals
		InternalCredential  credential = getCredential(ssoSite, fullPath);	
		
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
		// TODO Needs to be done for SSO Final

	}

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.sso.SSOProvider#getCredentials(javax.security.auth.Subject, java.lang.String)
	 */
	public SSOContext getCredentials(Subject subject, String site)
			throws SSOException {
		
		// Initialization
		SSOSite ssoSite = getSSOSiteObject(site);
		
		if ( ssoSite == null)
			throw new SSOException(SSOException.NO_CREDENTIALS_FOR_SITE);	// no entry for site
		
		// Get the principal from the subject
		BasePrincipal principal = (BasePrincipal)SecurityHelper.getBestPrincipal(subject, UserPrincipal.class);
		String fullPath = principal.getFullPath();
		
		// Filter the credentials for the given principals
		InternalCredential  credential = getCredential(ssoSite, fullPath);	
		
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
		SSOSite ssoSite = getSSOSiteObject(site);
		if (ssoSite == null)
		{
			// Create a new site
			ssoSite = new SSOSiteImpl();
			ssoSite.setSiteURL(site);
			ssoSite.setName(site);
			ssoSite.setCertificateRequired(false);
			ssoSite.setAllowUserSet(true);
		}
		
		// Get the Principal information
		String fullPath = ((BasePrincipal)SecurityHelper.getBestPrincipal(subject, UserPrincipal.class)).getFullPath();
			
		InternalPrincipal principal = this.getPrincipalForPath(subject, fullPath);
		
		if (principal == null)
			throw new SSOException(SSOException.REQUESTED_PRINCIPAL_DOES_NOT_EXIST);
		
		// New credential object
		 InternalCredentialImpl credential = 
            new InternalCredentialImpl(principal.getPrincipalId(),
            		pwd, 0, DefaultPasswordCredentialImpl.class.getName());
		 
		// Add credential to mapping table
		 ssoSite.addCredential(credential);
		 ssoSite.addPrincipal(principal);
	
		// Update database and reset cache
		 try
         {
             getPersistenceBrokerTemplate().store(ssoSite);
          }
         catch (Exception e)
         {
         	e.printStackTrace();
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
		
		//Get the site
		SSOSite ssoSite = getSSOSiteObject(site);
		if (ssoSite == null)
		{
			throw new SSOException(SSOException.NO_CREDENTIALS_FOR_SITE);
		}
		
		// Get the Principal information
		String fullPath = ((BasePrincipal)SecurityHelper.getBestPrincipal(subject, UserPrincipal.class)).getFullPath();
			
		InternalPrincipal principal = this.getPrincipalForPath(subject, fullPath);
		
		/*
		 * Should never happen except if the function gets invoked from outside the current credential store
		 */
		if (principal == null)
			throw new SSOException(SSOException.REQUESTED_PRINCIPAL_DOES_NOT_EXIST);
		
		// New credential object
		 InternalCredential credential = getCredential(ssoSite, fullPath);
		 
		// Remove credential and principal from mapping
		 ssoSite.removeCredential(credential);
		 ssoSite.removePrincipal(principal.getPrincipalId());
	
		// Update database and reset cache
		 try
         {
             getPersistenceBrokerTemplate().store(ssoSite);
          }
         catch (Exception e)
         {
         	e.printStackTrace();
            throw new SSOException(SSOException.FAILED_STORING_SITE_INFO_IN_DB + e.toString() );
         }
         
         // Clear cache
         this.mapSite.clear();
	}
	
	/*
	 * Helper utilities
	 * 
	 */
	
	/*
	 * getSSOSiteObject
	 * Obtains the Site information including the credentials for a site (url).
	 */
	
	private SSOSite getSSOSiteObject(String site)
	{
		//Initialization
		SSOSite ssoSite = null;
		
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
				    	ssoSite = (SSOSite) itSite.next();
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
			ssoSite = (SSOSite)mapSite.get(site);
		}
		
		return ssoSite;
	}
	
	/*
	 * getCredential
	 * returns the credentials for a given user
	 */
	private InternalCredential  getCredential(SSOSite ssoSite, String fullPath)
	{
		long  principalID = -1;
		InternalCredential credential = null;
				
		/* Error checking
		 * 1) should have at least one principal
		 * 2) should have at least one credential
		 * 
		 * If one of the above fails return null wich means that the user doesn't have credentials for that site
		 */
		Collection principals = ssoSite.getPrincipals();
		Collection credentials = ssoSite.getCredentials();
		
		if ( principals == null  || credentials == null)
		{
			return null;
		}
		// Iterate over the principals and extract the principal id for the given full path
		Iterator itPrincipals = principals.iterator();
		while (itPrincipals.hasNext() && principalID == -1 /*not found yet*/)
		{
			InternalPrincipal principal = (InternalPrincipal)itPrincipals.next();
			if ( principal != null && principal.getFullPath().compareToIgnoreCase(fullPath) == 0)
			{
				principalID = principal.getPrincipalId();
			}
		}
		
		if ( principalID == -1)
			return null;	// No principal found for that site
		
		// Last lookup to see if there are credentials for that user
		Iterator itCredentials = credentials.iterator();
		while (itCredentials.hasNext() && credential == null /*not found yet*/)
		{
			InternalCredential cred = (InternalCredential)itCredentials.next();
			
			if ( cred != null && cred.getPrincipalId() == principalID)
			{
				// Found credentials for Orincipals
				// TODO: Remove debug
				System.out.println("Found Credential: " + cred.getValue() + " for PrincipalID " + principalID);
				credential = cred;
			}
		}
		
		return credential;
	}
	
	private InternalPrincipal getPrincipalForPath(Subject subject, String fullPath)
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
		    	return (InternalPrincipal) itPrincipals.next();
		    }
	    }
	    
	    // Principal for path doesn't exist
	    return null;
	    
	}
}
