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

import java.util.Collection;

import javax.security.auth.Subject;

import org.apache.jetspeed.security.JetspeedPrincipal;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public interface SSOSiteManager
{
    
    /**
     * Creates a new TRANSIENT site object. The site is not added to the persistent store yet.
     * It just creates a new Site object. Use the addSite(site) method to make the site persistent.
     * @param name the name of the new site
     * @param url the url of the new site
     * @return a new SSO site object
     * @throws SSOException
     */
    SSOSite newSite(String name, String url) throws SSOException;
    
    /**
     * Adds the site to the persistent store. 
     * @param site the transient site to be added (created with newSite())
     * @return the persistent site that was added
     * @throws SSOException
     */
    SSOSite addSite(SSOSite site) throws SSOException; 
    
    /**
     * Removes a SSO site
     * @param site the site to be removed
     * @throws SSOException
     */
    void removeSite(SSOSite site) throws SSOException;
    
    /**
     * Updates an existing SSO site
     * @param site the site to be updated
     * @throws SSOException
     */
    void updateSite(SSOSite site) throws SSOException;
    
    /**
     * Retrieves all SSO sites related to the given Subject. A Subject can contain multiple
     * Portal principals, each of which can be related with one or more SSO users.
     * @param subject
     * @return the collection of sites related to this subject
     * @throws SSOException
     */
    Collection<SSOSite> getSitesForSubject(Subject subject) throws SSOException;
    
    /**
     * Retrieves all sites directly related to this single principal. Indirect relations are not returned!
     * Use getSitesForSubject() if you want to get all sites which belong to one user. 
     * @param principal
     * @return
     * @throws SSOException
     */
    Collection<SSOSite> getSitesForPrincipal(JetspeedPrincipal principal) throws SSOException;
    
    /**
     * Retrieves sites, given a filter. The filter is matched as a substring of the name or the url of the site.
     * 
     * @param filter a string that should match part of the name or url of sites returned
     * @return a collection of sites which match the filter
     */
    Collection<SSOSite> getSites(String filter);
    
    /**
     * Retrieves a site by matching ID
     * @param id the id of a site
     * @return the site with the given ID
     */
    SSOSite getSiteById(int id);
    
    /**
     * Retrieves a site by matching the URL. The url has to be an exact match.
     * @param siteUrl the url of a site
     * @return the site with the given URL
     */
    SSOSite getSiteByUrl(String siteUrl);
    
    /**
     * Retrieves a site by matching the name of the site. The name should be an exact match.
     * @param siteName the name of a site
     * @return the site with the given name
     */
    SSOSite getSiteByName(String siteName);

}
