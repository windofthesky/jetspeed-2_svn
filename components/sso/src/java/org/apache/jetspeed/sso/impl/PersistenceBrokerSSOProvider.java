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

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

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
import org.apache.jetspeed.security.impl.GroupPrincipalImpl;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;
import org.apache.jetspeed.security.om.InternalCredential;
import org.apache.jetspeed.security.om.InternalGroupPrincipal;
import org.apache.jetspeed.security.om.InternalUserPrincipal;
import org.apache.jetspeed.security.om.impl.InternalCredentialImpl;
import org.apache.jetspeed.security.om.impl.InternalGroupPrincipalImpl;
import org.apache.jetspeed.security.om.impl.InternalUserPrincipalImpl;
import org.apache.jetspeed.security.spi.impl.DefaultPasswordCredentialImpl;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;

/**
* <p>Utility component to handle SSO requests</p>
* 
* @author <a href="mailto:rogerrut@apache.org">Roger Ruttimann</a>
*/
public class PersistenceBrokerSSOProvider extends
		InitablePersistenceBrokerDaoSupport implements SSOProvider 
{	
	private Hashtable mapSite = new Hashtable();	
	
    private String USER_PATH = "/user/";
    private String GROUP_PATH = "/group/";

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

	public Iterator getSites(String filter)
    {
        Criteria queryCriteria = new Criteria();
        Query query = QueryFactory.newQuery(SSOSiteImpl.class, queryCriteria);
        Collection c = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        return c.iterator();        
    }
	
	/**
     * addCredentialsForSite()
     * @param fullPath
     * @param remoteUser
     * @param site
     * @param pwd
     * @throws SSOException
     */
    public void addCredentialsForSite(String fullPath, String remoteUser, String site, String pwd) throws SSOException
    {
        // Create a Subject for the given path and forward it to the API addCredentialsForSite()
        Principal principal = null;
        String name = null;
        
        // Group or User
        if (fullPath.indexOf("/group/") > -1 )
        {
            name = fullPath.substring(GROUP_PATH.length());
            principal = new GroupPrincipalImpl(name);
        }
        else
        {
            name = fullPath.substring(USER_PATH.length());
            principal = new UserPrincipalImpl(name);
        }
 
        // Create Subject
        Set principals = new HashSet();
        principals.add(principal);
        Subject subject = new Subject(true, principals, new HashSet(), new HashSet());	
        
        // Call into the API
        addCredentialsForSite(subject, remoteUser, site, pwd);
    }
    
    /**
     * removeCredentialsForSite()
     * @param fullPath
     * @param site
     * @throws SSOException
     */
    public void removeCredentialsForSite(String fullPath, String site) throws SSOException
    {
        // Create a Subject for the given path and forward it to the API addCredentialsForSite()
        Principal principal = null;
        String name = null;
        
        // Group or User
        if (fullPath.indexOf("/group/") > -1 )
        {
            name = fullPath.substring(GROUP_PATH.length());
            principal = new GroupPrincipalImpl(name);
        }
        else
        {
            name = fullPath.substring(USER_PATH.length());
            principal = new UserPrincipalImpl(name);
        }
 
        // Create Subject
        Set principals = new HashSet();
        principals.add(principal);
        Subject subject = new Subject(true, principals, new HashSet(), new HashSet());	
    
        // Call into the API
        this.removeCredentialsForSite(subject,site);
    }
    
    
    /** Retrive site information
     * 
     *  getSiteURL
     */
    
    public String getSiteURL(String site)
    {
        // The site is the URL
        return site;
    }
    
    /**
     * getSiteName
     */
    public String getSiteName(String site)
    {
        SSOSite ssoSite = getSSOSiteObject(site);
		
		if ( ssoSite == null)
		{
			return ssoSite.getName();
		}
		else
		{
		    return null;
		}
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
		
				
		// Get remotePrincipals for Site and match them with the Remote Principal for the Principal attached to site
		Collection remoteForSite		= ssoSite.getRemotePrincipals();
		Collection principalsForSite	= ssoSite.getPrincipals();	// Users
		
		// If any of them don't exist just return
		if (principalsForSite == null || remoteForSite== null )
		    return false;	// no entry
		
		Collection remoteForPrincipals = getRemotePrincipalsForPrincipal(principalsForSite, fullPath);
		
		if ( remoteForPrincipals == null)
		    return false;	// no entry
		
		// Get remote Principal that matches the site and the principal
		if (findRemoteMatch(remoteForPrincipals, remoteForSite) == null )
		{
		    return false;	// No entry
		}
		else
		{
		    return true;	// Has an entry
		}
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
		
		if (principal == null )
		{
		    principal = getSSOPrincipal(fullPath);
		    ssoSite.addPrincipal(principal);
		}
		else
		{
		    // Check if the entry the user likes to update exists already
		    Collection remoteForSite = ssoSite.getRemotePrincipals();
		    Collection principalsForSite = ssoSite.getPrincipals();
		    
		    if ( remoteForSite != null && principalsForSite != null)
		    {
		        Collection remoteForPrincipals = this.getRemotePrincipalsForPrincipal(principalsForSite, fullPath);
		        if ( remoteForPrincipals != null)
		        {
			        if (findRemoteMatch(remoteForPrincipals, remoteForSite) != null )
			        {
			            // Entry exists can't to an add has to call update
			            throw new SSOException(SSOException.REMOTE_PRINCIPAL_EXISTS_CALL_UPDATE);
			        }
		        }
		    }
		}
		
		if (principal == null)
			throw new SSOException(SSOException.FAILED_ADDING_PRINCIPAL_TO_MAPPING_TABLE_FOR_SITE);
		
		// Create a remote principal and credentials
		InternalUserPrincipalImpl remotePrincipal = new InternalUserPrincipalImpl(remoteUser);
		
		/*
		 * The RemotePrincipal (class InternalUserPrincipal) will have a fullPath that identifies the entry as an SSO credential.
		 * The entry has to be unique for a site and principal  (GROUP -or- USER ) an therefore it needs to be encoded as following:
		 * The convention for the path is the following: /sso/SiteID/{user|group}/{user name | group name}/remote user name
		 */
		if ( fullPath.indexOf("/group/") > -1)
		    remotePrincipal.setFullPath("/sso/" + ssoSite.getSiteId() + "/group/"+  principalName + "/" + remoteUser);
		else
		    remotePrincipal.setFullPath("/sso/" + ssoSite.getSiteId() + "/user/"+ principalName + "/" + remoteUser);
	
		// New credential object for remote principal
		 InternalCredentialImpl credential = 
            new InternalCredentialImpl(remotePrincipal.getPrincipalId(),
            		pwd, 0, DefaultPasswordCredentialImpl.class.getName());
		 
		 if ( remotePrincipal.getCredentials() == null)
		 	remotePrincipal.setCredentials(new ArrayList(0));
		 
		remotePrincipal.getCredentials().add( credential);
		
		// Add it to Principals remotePrincipals list
		principal.addRemotePrincipal(remotePrincipal);
		
		// Update the site remotePrincipals list
		ssoSite.getRemotePrincipals().add(remotePrincipal);
		 	
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
         // Add to site
         this.mapSite.put(site, ssoSite);
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
			//	Get remotePrincipals for Site and match them with the Remote Principal for the Principal attached to site
			Collection principalsForSite = ssoSite.getPrincipals();
			Collection remoteForSite = ssoSite.getRemotePrincipals();
			
			// If any of them don't exist just return
			if (principalsForSite == null || remoteForSite== null )
			    throw new SSOException(SSOException.NO_CREDENTIALS_FOR_SITE);
			
			Collection remoteForPrincipals = getRemotePrincipalsForPrincipal(principalsForSite, fullPath);
			
			if ( remoteForPrincipals == null)
			    throw new SSOException(SSOException.NO_CREDENTIALS_FOR_SITE);
			
			// Get remote Principal that matches the site and the principal
			if ((remotePrincipal = findRemoteMatch(remoteForPrincipals, remoteForSite)) == null )
			{
			    throw new SSOException(SSOException.NO_CREDENTIALS_FOR_SITE);
			}
			
			// Update assocation tables
			ssoSite.getRemotePrincipals().remove(remotePrincipal);
			remoteForPrincipals.remove(remotePrincipal);
		    
			// delete the remote Principal from the SECURITY_PRINCIPAL table
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
								
		// Update database
		 try
         {
             getPersistenceBrokerTemplate().store(ssoSite);
          }
         catch (Exception e)
         {
         	e.printStackTrace();
            throw new SSOException(SSOException.FAILED_STORING_SITE_INFO_IN_DB + e.toString() );
         }
         
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
			
			//	Get remotePrincipals for Site and match them with the Remote Principal for the Principal attached to site
			Collection principalsForSite	= ssoSite.getPrincipals();
			Collection remoteForSite		= ssoSite.getRemotePrincipals();
			
			// If any of them don't exist just return
			if (principalsForSite == null || remoteForSite== null )
			    throw new SSOException(SSOException.NO_CREDENTIALS_FOR_SITE);
			
			Collection remoteForPrincipals = getRemotePrincipalsForPrincipal(principalsForSite, fullPath);
			
			if ( remoteForPrincipals == null)
			    throw new SSOException(SSOException.NO_CREDENTIALS_FOR_SITE);
			
			// Get remote Principal that matches the site and the principal
			if ((remotePrincipal = findRemoteMatch(remoteForPrincipals, remoteForSite)) == null )
			{
			    throw new SSOException(SSOException.NO_CREDENTIALS_FOR_SITE);
			}
						
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
		InternalUserPrincipal remotePrincipal = null;
		String remoteUser = null;
		String remoteFullPath = null;
		
		//	Get remotePrincipals for Site and match them with the Remote Principal for the Principal attached to site
		Collection principalsForSite = ssoSite.getPrincipals();
		Collection remoteForSite = ssoSite.getRemotePrincipals();
		
		// If any of them don't exist just return
		if ( principalsForSite == null  || remoteForSite== null )
		    return null;	// no entry
		
		Collection remoteForPrincipals = getRemotePrincipalsForPrincipal(principalsForSite, fullPath);
				
		if ( remoteForPrincipals == null)
		    return null;	// no entry
		
		// Get remote Principal that matches the site and the principal
		if ((remotePrincipal = findRemoteMatch(remoteForPrincipals, remoteForSite)) == null )
		{
		    return null;	// No entry
		}
		else
		{
		    // Has an entry
			if ( remotePrincipal.getCredentials() != null)
				credential = (InternalCredential)remotePrincipal.getCredentials().iterator().next();
			
			// Error checking  -- should have a credential at this point
			if ( credential == null)
			{
				System.out.println("Warning: Remote User " + remotePrincipal.getFullPath() + " doesn't have a credential");
				return null; 
			}
		}
		
		//	Create new context
		String name = stripPrincipalName(remotePrincipal.getFullPath());
		
		SSOContext context = new SSOContextImpl(credential.getPrincipalId(), name, credential.getValue());
		
		return context;
	}
	
    private String stripPrincipalName(String fullPath)
    {
        String name;
        int ix = fullPath.lastIndexOf('/');
        if ( ix != -1)
            name = fullPath.substring(ix + 1);
        else
            name = new String(fullPath);
        
        return name;        
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
			if (         principal != null 
			        && principal.getFullPath().compareToIgnoreCase(fullPath) == 0 )
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
		Collection principalsForSite = ssoSite.getPrincipals();
		
		if ( principalsForSite != null)
		{
			Iterator itPrincipals = principalsForSite.iterator();
			while (itPrincipals.hasNext() && principal == null)
			{
				SSOPrincipal tmp  = (SSOPrincipal)itPrincipals.next();
				if ( 		 tmp != null 
				       && tmp.getFullPath().compareToIgnoreCase(fullPath) == 0 )
					principal = tmp;	// Found existing entry
			}
		}
		
		return principal;
	}
	
	private SSOPrincipal getSSOPrincipal(String fullPath)
	{
	    // FInd if the principal exists in the SECURITY_PRINCIPAL table
	    SSOPrincipal principal = null;
	    
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
		    }
	    }
	    
		return principal;		
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
				if (tmp.getFullPath().compareToIgnoreCase(fullPath) == 0)
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
	
	/*
	 * 
	 * 
	 */
	private InternalUserPrincipal findRemoteMatch(Collection remoteForPrincipals, Collection remoteForSite)
	{
	    // Iterate over the lists and find match
	    Iterator itRemoteForPrincipals = remoteForPrincipals.iterator();
	    while ( itRemoteForPrincipals.hasNext())
	    {
	        InternalUserPrincipal remoteForPrincipal = (InternalUserPrincipal)itRemoteForPrincipals.next();
	        
	        // Find a match in the site list
	        Iterator itRemoteForSite = remoteForSite.iterator();
		    while ( itRemoteForSite.hasNext())
		    {
		        InternalUserPrincipal tmp = (InternalUserPrincipal)itRemoteForSite.next();
		        
		        if ( tmp.getPrincipalId() == remoteForPrincipal.getPrincipalId() )
		            return remoteForPrincipal;
		    }
	    }
	    // No match found
	    return null;
	}
	
	/*
	 * getRemotePrincipalsForPrincipals
	 * Checks if the user has any remote principals. If the principal is a group expand the group and
	 * check if the requesting user is a part of the group.
	 */
	private Collection getRemotePrincipalsForPrincipal(Collection principalsForSite, String fullPath)
	{
	    if (principalsForSite != null )
	    {
		    Iterator itPrincipalsForSite = principalsForSite.iterator();
		    while (itPrincipalsForSite.hasNext())
		    {
		        String principalFullPath = null;
		        SSOPrincipal principal = (SSOPrincipal)itPrincipalsForSite.next();
		        principalFullPath = principal.getFullPath();
		        
		        /* If the Principal is for a Group expand the Group and check if the user identified
		        * by the fullPath is a member of the Group. If the user is a member of the Group
		        * return the remote Credentials for the current Principal.
		        */
		        if ( principalFullPath.indexOf("/group/") == -1)
		        {
		            // USER
		            if ( principalFullPath.compareToIgnoreCase(fullPath) == 0)
		                return principal.getRemotePrincipals();
		        }
		        else
		        {
		            /* GROUP 
		             * If the full path is for a group (delete/add) just return the the list of remotePrincipals
		             * For a lookup (hasCredentials) the user needs to be mapped against each member of the group
		            */
		            if ( principalFullPath.compareToIgnoreCase(fullPath) == 0)
		                return principal.getRemotePrincipals();
		            
		            /* Expand the Group and find a match */
			        InternalGroupPrincipal  groupPrincipal = getGroupPrincipals(principalFullPath);
			        
			        // Found Group that matches the name
			        if (groupPrincipal != null)
		            {
			            Collection usersInGroup = groupPrincipal.getUserPrincipals();
			            Iterator itUsers = usersInGroup.iterator();
		                while (itUsers.hasNext())
		                {
		                    InternalUserPrincipal user = (InternalUserPrincipal)itUsers.next();
		                    if (user.getFullPath().compareToIgnoreCase(fullPath) == 0)
		                    {
		                        // User is member of the group
		                        return principal.getRemotePrincipals();
		                    }
		                }
		            }
		        }  
		    }
	    }
	    
	    // No match found
	    return null;
	}
    
    public SSOSite getSite(String siteUrl)
    {
        Criteria filter = new Criteria();
        filter.addEqualTo("url", siteUrl);
        Query query = QueryFactory.newQuery(SSOSiteImpl.class, filter);
        SSOSite site = (SSOSite) getPersistenceBrokerTemplate().getObjectByQuery(query);
        return site;       
    }
    
    public void updateSite(SSOSite site)
    throws SSOException
    {
        try
        {
            getPersistenceBrokerTemplate().store(site);
            this.mapSite.put(site.getName(), site);                        
        }
        catch (Exception e)
        {
            String msg = "Unable to remove SSO Site: " + site.getName();
            logger.error(msg, e);
            throw new SSOException(msg, e);
        }        
    }
    
    public void addSite(String siteName, String siteUrl)
    throws SSOException
    {
        try
        {
            SSOSite ssoSite = new SSOSiteImpl();
            ssoSite.setSiteURL(siteUrl);
            ssoSite.setName(siteName);
            ssoSite.setCertificateRequired(false);
            ssoSite.setAllowUserSet(true);            
            getPersistenceBrokerTemplate().store(ssoSite);
            this.mapSite.put(siteName, ssoSite);            
        }
        catch (Exception e)
        {
            String msg = "Unable to remove SSO Site: " + siteName;
            logger.error(msg, e);
            throw new SSOException(msg, e);
        }                
    }
    
    public void removeSite(SSOSite site)
    throws SSOException
    {
        try
        {
            getPersistenceBrokerTemplate().delete(site);
            this.mapSite.remove(site);

        }
        catch (Exception e)
        {
            String msg = "Unable to remove SSO Site: " + site.getName();
            logger.error(msg, e);
            throw new SSOException(msg, e);
        }        
    }
        
    public List getPrincipalsForSite(SSOSite site)
    {
        List list = new ArrayList();
        Iterator principals = site.getRemotePrincipals().iterator();
        while (principals.hasNext())
        {
            InternalUserPrincipal remotePrincipal = (InternalUserPrincipal)principals.next();
            String fullpath = remotePrincipal.getFullPath();
            Iterator creds = remotePrincipal.getCredentials().iterator();
            while (creds.hasNext())
            {
                InternalCredential cred = (InternalCredential) creds.next();
                SSOContext context = new SSOContextImpl(remotePrincipal.getPrincipalId(), 
                                                stripPrincipalName(remotePrincipal.getFullPath()), 
                                                cred.getValue(), 
                                                stripPortalPrincipalName(remotePrincipal.getFullPath()));
                list.add(context);
            }
        }
        return list;
    }

    
    private String stripPortalPrincipalName(String fullPath)
    {
        StringTokenizer tokenizer = new StringTokenizer(fullPath, "/");
        while (tokenizer.hasMoreTokens())
        {
            String token = tokenizer.nextToken();
            if (token.equals("user") || token.equals("group"))
            {
                 if (tokenizer.hasMoreTokens())
                {
                    return tokenizer.nextToken();
                }
            }
        }
        return fullPath;        
    }
    
    private InternalGroupPrincipal  getGroupPrincipals(String principalFullPath)
    {
        // Get to the backend to return the group that matches the full path
        Criteria filter = new Criteria();
        filter.addEqualTo("fullPath", principalFullPath);
        Query query = QueryFactory.newQuery(InternalGroupPrincipalImpl.class, filter);
        InternalGroupPrincipal group = (InternalGroupPrincipal) getPersistenceBrokerTemplate().getObjectByQuery(query);
        return group;       
    }
    
}
