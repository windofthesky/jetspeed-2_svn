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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.jetspeed.security.UserPrincipal;

import javax.security.auth.Subject;

import org.apache.jetspeed.components.dao.InitablePersistenceBrokerDaoSupport;

import org.apache.jetspeed.sso.SSOContext;
import org.apache.jetspeed.sso.SSOException;
import org.apache.jetspeed.sso.SSOProvider;
import org.apache.jetspeed.sso.SSOSite;
import org.apache.jetspeed.sso.SSOPrincipal;

import org.apache.jetspeed.sso.impl.SSOSiteImpl;
import org.apache.jetspeed.sso.impl.SSOPrincipalImpl;


import org.apache.jetspeed.security.SecurityHelper;
import org.apache.jetspeed.security.BasePrincipal;
import org.apache.jetspeed.security.om.InternalCredential;
import org.apache.jetspeed.security.om.InternalUserPrincipal;
import org.apache.jetspeed.security.om.impl.InternalCredentialImpl;
import org.apache.jetspeed.security.om.impl.InternalUserPrincipalImpl;
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
		
		//	Check if the principal has any remote principals
		Collection remotePrincipals = getRemotePrincipalsForPrincipal(ssoSite, fullPath);
		
		if (remotePrincipals == null || remotePrincipals.size() < 1)
			return false;	// No remote credentials for Principal
		else
			return true;	// User has credentials for site
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
		SSOContext context = getCredential(ssoSite, fullPath);	
		
		if ( context == null)
			throw new SSOException(SSOException.NO_CREDENTIALS_FOR_SITE);	// no entry for site
		
		return context;
	}

	/* addCredential()
		 * Adds credentials for a user to the site. If the site doesn't exist it will be created
	 * @see org.apache.jetspeed.sso.SSOProvider#addCredentialsForSite(javax.security.auth.Subject, java.lang.String, java.lang.String)
	 */
	public void addCredentialsForSite(Subject subject, String remoteUser, String site, String pwd)
			throws SSOException {
		
		// Check if an entry for the site already exists otherwise create a new one
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
		
		// Get the Principal information (logged in user)
		String fullPath = ((BasePrincipal)SecurityHelper.getBestPrincipal(subject, UserPrincipal.class)).getFullPath();
		String principalName = ((BasePrincipal)SecurityHelper.getBestPrincipal(subject, UserPrincipal.class)).getName();
		
		// Add an entry for the principal to the site if it doesn't exist
		SSOPrincipal principal = this.getPrincipalForSite(ssoSite, fullPath);
		
		if (principal == null)
			throw new SSOException(SSOException.FAILED_ADDING_PRINCIPAL_TO_MAPPING_TABLE_FOR_SITE);
		
		// Create a remote principal and add it to the principal
		InternalUserPrincipalImpl remotePrincipal = new InternalUserPrincipalImpl(remoteUser);
		remotePrincipal.setFullPath("/sso/user/"+ principalName + "/" + remoteUser);
	
		// New credential object for remote principal
		 InternalCredentialImpl credential = 
            new InternalCredentialImpl(remotePrincipal.getPrincipalId(),
            		pwd, 0, DefaultPasswordCredentialImpl.class.getName());
		 
		 if ( remotePrincipal.getCredentials() == null)
		 	remotePrincipal.setCredentials(new ArrayList(0));
		 
		remotePrincipal.getCredentials().add( credential);
		 
		 
		 principal.addRemotePrincipal(remotePrincipal);
		 	
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
		
		// Initailization
		InternalUserPrincipal remotePrincipal = null;
		SSOPrincipal principal = null;
		
		//Get the site
		SSOSite ssoSite = getSSOSiteObject(site);
		if (ssoSite == null)
		{
			throw new SSOException(SSOException.NO_CREDENTIALS_FOR_SITE);
		}
		
		// Get the Principal information
		String fullPath = ((BasePrincipal)SecurityHelper.getBestPrincipal(subject, UserPrincipal.class)).getFullPath();
		
		try
		{
			// Remove remote principal from the association table
			remotePrincipal = removeRemotePrincipalForPrincipal(ssoSite, fullPath);
			
			// Remove the principal association
			principal = this.getPrincipalForSite(ssoSite, fullPath);
			if ( principal != null )
				ssoSite.getPrincipals().remove(principal);
			
			// Remove Remote principal and associated credential from persistence store
			if (remotePrincipal != null)
				getPersistenceBrokerTemplate().delete(remotePrincipal);
			
		}
		catch(SSOException ssoex)
		{
			throw new SSOException(ssoex);
		}
		catch (Exception e)
        {
        	e.printStackTrace();
           throw new SSOException(SSOException.FAILED_STORING_SITE_INFO_IN_DB + e.toString() );
        }
								
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
	
	/**
	 * updateCredentialsForSite
	 * @param subject	Current subject
	 * @param remoteUser	remote user login
	 * @param site		URL or description of site
	 * @param pwd	Password for credentail
	 */
	public void  updateCredentialsForSite(Subject subject, String remoteUser, String site, String pwd)  
	    throws SSOException
	    {
	        // Check if the the current user has a credential for the site
		
			// Update the credential
			//		 Initailization
			InternalUserPrincipal remotePrincipal = null;
			
			//Get the site
			SSOSite ssoSite = getSSOSiteObject(site);
			if (ssoSite == null)
			{
				throw new SSOException(SSOException.NO_CREDENTIALS_FOR_SITE);
			}
			
			// Get the Principal information
			String fullPath = ((BasePrincipal)SecurityHelper.getBestPrincipal(subject, UserPrincipal.class)).getFullPath();
			String principalName  = ((BasePrincipal)SecurityHelper.getBestPrincipal(subject, UserPrincipal.class)).getName();
			
			// Get collection of remote principals and find a match for the one to remove
			Collection remotePrincipals = getRemotePrincipalsForPrincipal(ssoSite, fullPath);
			if ( remotePrincipals == null || remotePrincipals.size() < 1)
				throw new SSOException(SSOException.REQUESTED_PRINCIPAL_DOES_NOT_EXIST);
			
			// User can have one remote user per site
			Iterator itRemotePrincipals = remotePrincipals.iterator();
			remotePrincipal = (InternalUserPrincipal)itRemotePrincipals.next();
			
			// Update principal information
			remotePrincipal.setFullPath("/sso/user/"+ principalName + "/" + remoteUser);
			InternalCredential credential = (InternalCredential)remotePrincipal.getCredentials().iterator().next();
					
			// New credential object
			 if ( credential != null) 
				// Remove credential and principal from mapping
				 credential.setValue(pwd);
			
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
	private SSOContext  getCredential(SSOSite ssoSite, String fullPath)
	{
		InternalCredential credential = null;
		String remoteUser = null;
		String remoteFullPath = null;
				
		/* Error checking
		 * 1) should have at least one principal
		 * 
		 * If one of the above fails return null wich means that the user doesn't have credentials for that site
		 */
		Collection principals = ssoSite.getPrincipals();
		
		if ( principals == null )
		{
			return null;
		}
		
		// Iterate over the principals and extract the principal id for the given full path
		SSOPrincipal principal = null;
		
		Iterator itPrincipals = principals.iterator();
		while (itPrincipals.hasNext() && principal == null /*not found yet*/)
		{
			SSOPrincipal tmp = (SSOPrincipal)itPrincipals.next();
			if ( tmp != null && tmp.getFullPath().compareToIgnoreCase(fullPath) == 0)
			{
				// Found it stop iteration
				principal = tmp;
			}
		}
		
		if ( principal == null)
			return null;	// No principal found for that site
		
		// Extract the remote principal
		Collection remotePrincipals = principal.getRemotePrincipals();
		if (remotePrincipals == null || remotePrincipals.size() < 1)
			return null;	// no remote principals
		
		InternalUserPrincipal remotePrincipal = (InternalUserPrincipal)remotePrincipals.iterator().next();
		
		// Get credentail  for this remote user
		if ( remotePrincipal.getCredentials() != null)
			credential = (InternalCredential)remotePrincipal.getCredentials().iterator().next();
		
		// Error checking  -- should have a credential at this point
		if ( credential == null)
		{
			System.out.println("Warning: Remote User " + remotePrincipal.getFullPath() + " doesn't have a credential");
			return null; 
		}
		else
		{
			System.out.println("Found Credential: " + credential.getValue() + " for PrincipalID " + remotePrincipal.getPrincipalId() + " Name: "+remotePrincipal.getFullPath() );
		}
		
		// Create new context
		String name = remotePrincipal.getFullPath();
		int ix = name.lastIndexOf('/');
		if ( ix != -1)
			name = name.substring(ix + 1);
		
		SSOContext context = new SSOContextImpl(credential.getPrincipalId(), name, credential.getValue());
		
		return context;
	}
	
	/*
	 * Get a Collection of remote Principals for the logged in principal identified by the full path
	 */
	private Collection getRemotePrincipalsForPrincipal(SSOSite ssoSite, String fullPath)
	{
		// The site orincipals list contains a list of remote principals for the user
		Collection principals = ssoSite.getPrincipals();
		
		if ( principals == null )
			return null;	// No principals for this site
		
		Iterator ixPrincipals = principals.iterator();
		while (ixPrincipals.hasNext())
		{
			SSOPrincipal principal = (SSOPrincipal)ixPrincipals.next();
			if ( principal != null && principal.getFullPath().compareToIgnoreCase(fullPath) == 0)
			{
				// Found Principal -- extract remote principals 
				return principal.getRemotePrincipals();
			}
		}
		
		// Principal is not in list
		return null;
	}
	
	/*
	 * getPrincipalForSite()
	 * returns a principal that matches the full path for the site or creates a new entry if it doesn't exist
	 */
	private SSOPrincipal getPrincipalForSite(SSOSite ssoSite, String fullPath)
	{
		SSOPrincipal principal = null;
		
		if ( ssoSite.getPrincipals() != null)
		{
			Iterator itPrincipals = ssoSite.getPrincipals().iterator();
			while (itPrincipals.hasNext() && principal == null)
			{
				SSOPrincipal tmp  = (SSOPrincipal)itPrincipals.next();
				if ( tmp != null && tmp.getFullPath().compareToIgnoreCase(fullPath) == 0)
					principal = tmp;	// Found existing entry
			}
		}
		
		// Not yest in the site list. Add it but make sure that a user exists
		if ( principal == null)
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
		    		principal = (SSOPrincipal) itPrincipals.next();
		    		try
					{
		    			ssoSite.addPrincipal(principal);
					}
		    		catch (SSOException ssoex)
					{
		    			System.out.println("ERROR-SSO: Failed adding principal to principla map. Error: " + ssoex.getMessage());
					}
			    }
		    }
		}
	    		
		return principal;		
	}
	
	/**
	 * getCredentialForPrincipal
	 * @param site
	 * @param principalId
	 * @return InternalCredential for the principal ID
	 */
	private InternalCredential getCredentialForPrincipal(SSOSite site, long principalId)
	{
		if ( site.getCredentials() != null)
		{
			Iterator itCredentials = site.getCredentials().iterator();
			while(itCredentials.hasNext() )
			{
				InternalCredential tmp = (InternalCredential)itCredentials.next();
				if ( tmp != null && tmp.getPrincipalId() == principalId)
					return tmp;
			}
		}
	
		return null;
	}
	
	/**
	 * removeRemotePrincipalForPrincipal
	 * @param site
	 * @param fullPath
	 * @return
	 * 
	 * removes remotePrincipal for a site & principal
	 */
	private InternalUserPrincipal  removeRemotePrincipalForPrincipal(SSOSite site, String fullPath) throws SSOException
	{
		if (site.getPrincipals() != null)
		{
			Iterator itPrincipals = site.getPrincipals().iterator();
			while (itPrincipals.hasNext())
			{
				SSOPrincipal tmp = (SSOPrincipal)itPrincipals.next();
				if ( tmp.getFullPath().compareToIgnoreCase(fullPath) == 0)
				{
					// Found -- get the remotePrincipal
					Collection collRemotePrincipals = tmp.getRemotePrincipals() ;
					if (collRemotePrincipals != null)
					{
					
						Iterator itRemotePrincipals = collRemotePrincipals.iterator();
						if  (itRemotePrincipals.hasNext())
						{
							InternalUserPrincipal remotePrincipal = (InternalUserPrincipal)itRemotePrincipals.next();
							// Found remove the object
							collRemotePrincipals.remove(remotePrincipal);
							return remotePrincipal;
						}
					}
				}
			}
		}		
		
		throw new SSOException(SSOException.REQUESTED_PRINCIPAL_DOES_NOT_EXIST);
	}
}
