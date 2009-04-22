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
package org.apache.jetspeed.sso.impl;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.SecurityDomain;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.impl.SecurityDomainImpl;
import org.apache.jetspeed.security.spi.SecurityDomainAccessManager;
import org.apache.jetspeed.security.spi.SecurityDomainStorageManager;
import org.apache.jetspeed.security.spi.impl.PasswordCredentialImpl;
import org.apache.jetspeed.sso.SSOClient;
import org.apache.jetspeed.sso.SSOException;
import org.apache.jetspeed.sso.SSOManager;
import org.apache.jetspeed.sso.SSOSite;
import org.apache.jetspeed.sso.SSOUser;
import org.apache.jetspeed.sso.spi.SSOSiteManagerSPI;
import org.apache.jetspeed.sso.spi.SSOUserManagerSPI;


/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public class SSOManagerImpl implements SSOManager
{
    /* Logging */
    private static final Logger log = LoggerFactory.getLogger(SSOManagerImpl.class);
    
    private UserManager userManager;
    private SSOUserManagerSPI ssoUserManagerSPI;
    
    private SecurityDomainAccessManager domainAccessManager;
    private SecurityDomainStorageManager domainStorageManager;
    private SSOSiteManagerSPI ssoSiteManagerSPI;
    
    private Long defaultDomainId;
    
    public SSOClient getClient(SSOSite site, SSOUser remoteUser) throws SSOException {
        PasswordCredential pwdCred = getCredentials(remoteUser);
        return new SSOClientImpl(site,pwdCred);
    }
    
    protected User getUser(String username) {
        User user = null;
        try{
            user = userManager.getUser(username);
        } catch (SecurityException secex){
        }
        return user;
    }
    
    protected Collection<SSOUser> getRemoteUsers(JetspeedPrincipal p) throws SSOException {
        try{
            return ssoUserManagerSPI.getUsers(p);   
        } catch (SecurityException secex){            
            throw new SSOException(secex);
        }
    }
    
    public Collection<SSOUser> getRemoteUsers(SSOSite site, Subject subject) throws SSOException {
        Map<Long,SSOUser> resultUsers = new HashMap<Long,SSOUser>();
        for (Principal p : subject.getPrincipals()){
            if (p instanceof JetspeedPrincipal){
                try{
                    Collection<SSOUser> usersForThisPrincipal = getRemoteUsers(site,(JetspeedPrincipal)p);
                    for (SSOUser user : usersForThisPrincipal)
                    {
                        if (!resultUsers.containsKey(user.getId())){
                            resultUsers.put(user.getId(), user);
                        }
                    }
                } catch (SSOException se){            
                    throw new SSOException(se);
                }
            }
        }
        return resultUsers.values();
    }
    
    public Collection<SSOUser> getRemoteUsers(SSOSite site, JetspeedPrincipal portalPrincipal) throws SSOException {
        try{
            return ssoUserManagerSPI.getUsers(portalPrincipal,site.getSecurityDomainId());   
        } catch (SecurityException secex){            
            throw new SSOException(secex);
        }
    }
    
    
    public void setPassword(SSOUser user, String pwd) throws SSOException
    {
    	PasswordCredential pwdCred = null;
    	
    	try{
    	    pwdCred=ssoUserManagerSPI.getPasswordCredential(user);
    	} catch (SecurityException secex){
    	    
    	}
    	if (pwdCred != null){
    	    pwdCred.setPassword(pwd, false);
    	} else {
            pwdCred=new PasswordCredentialImpl(user,pwd);
    	}
    	
    	try{
    	    ssoUserManagerSPI.storePasswordCredential(pwdCred);
    	} catch (SecurityException sx){
    		throw new SSOException(sx);
    	}
    }

    public Collection<JetspeedPrincipal> getPortalPrincipals(SSOUser user)
    {
        SSOSite site = ssoSiteManagerSPI.getSite(user);
        if (site != null){
            SecurityDomain ssoDomain = domainAccessManager.getDomain(site.getSecurityDomainId());
            SecurityDomain ownerDomain = domainAccessManager.getDomain(ssoDomain.getOwnerDomainId());
            return ssoUserManagerSPI.getPortalPrincipals(user,ownerDomain.getDomainId());    
        } else {
            return Collections.emptyList();
        }            
    }

    protected Long getDefaultDomainId(){
        if (defaultDomainId==null){
            SecurityDomain domain=domainAccessManager.getDomainByName(SecurityDomain.DEFAULT_NAME);
            if (domain == null){
                throw new RuntimeException("Could not find default security domain.");
            }
            defaultDomainId=domain.getDomainId();
        }
        return defaultDomainId;    
    }
    
    public SSOSite addSite(SSOSite site) throws SSOException {
        
    	return this.addSite(getDefaultDomainId(), site);
    }

    protected SSOSite addSite(Long ownerDomainId, SSOSite site) throws SSOException
    {    	
    	if (domainAccessManager.getDomainByName(site.getName()) != null){
    		throw new SSOException(SSOException.SITE_ALREADY_EXISTS);
    	}
    	
		SecurityDomainImpl sd = new SecurityDomainImpl();
    	sd.setName(site.getName());
    	sd.setOwnerDomainId(ownerDomainId);
    	sd.setEnabled(true);    	
    	sd.setRemote(true);
    	
    	try{
    		domainStorageManager.addDomain(sd);
    	} catch(SecurityException sx){
    		log.error("Could not add remote security domain with name "+site.getName()+" for owner domain "+ownerDomainId);
    		throw new SSOException(SSOException.SITE_COULD_NOT_BE_CREATED,sx);
    	}
    	SecurityDomain storedDomain = domainAccessManager.getDomainByName(site.getName());
    	if (storedDomain == null || storedDomain.getDomainId() == null){
    		throw new SSOException(SSOException.SITE_COULD_NOT_BE_CREATED);
    	}
    	
    	site.setSecurityDomainId(storedDomain.getDomainId());
    	
    	try{
            return ssoSiteManagerSPI.add(site);
    	} catch (SSOException se){
    	    // catch SSO Exception to remove already stored domain.
    	    try{
    	        domainStorageManager.removeDomain(storedDomain);
    	    } catch (SecurityException secex){    	
    	        
    	    }
    	    // rethrow exception
    	    throw new SSOException(se);
    	}
    }

    public PasswordCredential getCredentials(SSOUser user) throws SSOException
    {
        try{
            return ssoUserManagerSPI.getPasswordCredential(user);
        } catch (SecurityException secex){
            // TODO provide meaningful message
            throw new SSOException(secex);
        }
    }
    
    public Collection<SSOSite> getSites(String filter)
    {
        return ssoSiteManagerSPI.getSites(filter);
    }

    public Collection<SSOUser> getUsersForSite(SSOSite site) throws SSOException
    {
        try{
            return ssoUserManagerSPI.getUsers("", site.getSecurityDomainId());
        } catch (SecurityException e){
            throw new SSOException("Could not fetch SSO users for site "+site.getName(),e);
        }        
    }

    @SuppressWarnings("unchecked")
    public Collection<SSOSite> getSitesForPrincipal(JetspeedPrincipal localPrincipal) throws SSOException
    {
        Collection<SSOSite> sitesFound = null;
        if (localPrincipal != null){
            Collection<SSOUser> ssoUsers = getRemoteUsers(localPrincipal);
            if (ssoUsers != null && ssoUsers.size() > 0){
                sitesFound = ssoSiteManagerSPI.getSites(ssoUsers);
            }
        }
        return sitesFound != null ? sitesFound : Collections.EMPTY_SET;
    }
    
    public Collection<SSOSite> getSitesForSubject(Subject subject) throws SSOException {
        Map<Integer,SSOSite> siteIdToSite = new HashMap<Integer,SSOSite>();
        for (Principal p : subject.getPrincipals()){
            if (p instanceof JetspeedPrincipal){
                try {
                    Collection<SSOSite> sitesForThisPrincipal = getSitesForPrincipal((JetspeedPrincipal)p);
                    for (SSOSite site : sitesForThisPrincipal)
                    {
                        if (!siteIdToSite.containsKey(site.getId())){
                            siteIdToSite.put(site.getId(), site);
                        }
                    }
                } catch (SSOException se){
                    
                }
            }
        }        
        return siteIdToSite.values();
    }

	public void addAssociation(SSOUser user, JetspeedPrincipal principal) throws SSOException
    {
        try{
            ssoUserManagerSPI.addSSOUserToPrincipal(user, principal);    
        } catch (SecurityException secex){
            throw new SSOException("Unable to associate principal "+principal.getName() + " with SSO user "+user.getName());
        }
	    
    }

    public SSOUser addUser(SSOSite site, JetspeedPrincipal ownerPrincipal,
            String ssoUsername, String ssoUserPassword) throws SSOException
    {
	    SSOUser newUser = null;
        try{
            // step 1. create new SSO user
            newUser = ssoUserManagerSPI.addUser(ssoUsername, site.getSecurityDomainId(), ownerPrincipal);
            // step 2. store new user's credentials
            setPassword(newUser, ssoUserPassword);
            // step 3. relate owner to SSO user
            addAssociation(newUser, ownerPrincipal);
        } catch (SecurityException secex){
            // revert changes, if applicable
            if (newUser != null){
                removeUser(newUser);
            }
            throw new SSOException("Unable to add new SSO User "+ssoUsername,secex);
        }
        return newUser;
    }
    
    public void updateUser(SSOUser user) throws SSOException{
        try{
            ssoUserManagerSPI.updateUser(user);
        } catch (SecurityException secex){
            throw new SSOException("Unable to update user:",secex);
        }
    }

    public void removeUser(SSOUser remoteUser)
            throws SSOException
    {
        try{
            ssoUserManagerSPI.removeUser(remoteUser.getName(), remoteUser.getDomainId());    
        } catch (SecurityException secex){
            throw new SSOException("Unable to remove SSO User "+remoteUser.getName(),secex);
        }
        
        
    }

    public void removeSite(SSOSite site) throws SSOException
    {
        SecurityDomain domain = domainAccessManager.getDomain(site.getSecurityDomainId());
        if (domain != null){
            try{
                domainStorageManager.removeDomain(domain);
            } catch (SecurityException secex){
                throw new SSOException("Unable to remove security domain (id:"+site.getSecurityDomainId()+") associated with the SSO Site "+site.getName());
            }
        }
        
        ssoSiteManagerSPI.remove(site);
    }

    public void updateSite(SSOSite site) throws SSOException
    {
        SSOSite currentSite = ssoSiteManagerSPI.getById(site.getId());
        if (currentSite == null){
            throw new SSOException("Unable to update site: site doesn't exist.");
        }
        if (!currentSite.getName().equals(site.getName())){
            if (domainAccessManager.getDomainByName(site.getName()) != null){
                throw new SSOException("Unable to rename site to '"+site.getName()+"': a security domain with that name already exists!");
            }
            SecurityDomain domain = domainAccessManager.getDomain(currentSite.getSecurityDomainId());
            SecurityDomainImpl renamedDomain = new SecurityDomainImpl(domain);
            renamedDomain.setName(site.getName());
            try{
                domainStorageManager.updateDomain(renamedDomain);    
            } catch (SecurityException secex){
                throw new SSOException("Unable to rename security domain "+domain.getName()+" to "+site.getName()+".",secex);
            }            
        }
        ssoSiteManagerSPI.update(site);
    }
    
    public SSOUser getRemoteUser(SSOSite site, String remoteUsername ) {
        try{
            return ssoUserManagerSPI.getUser(remoteUsername, site.getSecurityDomainId());
        } catch (SecurityException secex){
            log.debug("Could not find SSO user with name "+remoteUsername+" from remote site "+site.getName()+" (domain id: "+site.getSecurityDomainId()+")", secex);
            return null;
        }
    }
    
    public SSOSite getSiteByUrl(String siteUrl)
    {        
        return ssoSiteManagerSPI.getByUrl(siteUrl);
    }

    public SSOSite getSiteByName(String siteName)
    {        
        return ssoSiteManagerSPI.getByName(siteName);
    }

    public SSOSite getSiteById(int id)
    {        
        return ssoSiteManagerSPI.getById(id);
    }
    
    public SSOSite newSite(String name, String url){
        return new SSOSiteImpl(name,url);
    }
    
    public void setUserManager(UserManager userMan)
    {
        this.userManager = userMan;
    }

    
    public void setSSOUserManagerSPI(SSOUserManagerSPI ssoUserManSPI)
    {
        this.ssoUserManagerSPI = ssoUserManSPI;
    }

    
    public void setDomainAccessManager(SecurityDomainAccessManager domainAccess)
    {
        this.domainAccessManager = domainAccess;
    }

    
    public void setDomainStorageManager(SecurityDomainStorageManager domainStore)
    {
        this.domainStorageManager = domainStore;
    }

    public void setSSOSiteManagerSPI(SSOSiteManagerSPI ssoSiteManSPI)
    {
        this.ssoSiteManagerSPI = ssoSiteManSPI;
    }

    
}
