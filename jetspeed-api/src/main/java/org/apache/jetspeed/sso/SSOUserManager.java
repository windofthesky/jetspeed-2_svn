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
import org.apache.jetspeed.security.PasswordCredential;


/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public interface SSOUserManager
{

    /**
     * Retrieves a single SSO user, given the SSO user's site and name
     * @param site The SSO Site
     * @param remoteUserName the name of the SSO user
     * @return the SSO user
     */
    SSOUser getRemoteUser(SSOSite site, String remoteUserName);

    /**
     * Retrieves all Portal ("local") Principals connected to a given SSO User
     * @param user the SSO user
     * @return Portal principals
     */
    Collection<JetspeedPrincipal> getPortalPrincipals(SSOUser user);

    /**
     * Retrieves all SSO Users related to this subject, for the given site. A subject can contain
     * multiple Portal Principals, each of which can be related to multiple SSO users.
     * The result of calling this method would be the same as calling  getRemoteUsers(SSOSite site, JetspeedPrincipal portalPrincipal)
     * for every principal in the subject, and aggregating all the SSO users in one collection.
     * @param site the SSO Site for which to fetch the SSO users
     * @param subject 
     * @return the collection of SSO users
     * @throws SSOException
     */
    Collection<SSOUser> getRemoteUsers(SSOSite site, Subject subject) throws SSOException;

    /**
     * Retrieves all SSO users related to the Portal principal, for the given site.
     * @param site the SSO Site
     * @param portalPrincipal a Portal principal
     * @return a collection of SSO users
     * @throws SSOException
     */
    Collection<SSOUser> getRemoteUsers(SSOSite site, JetspeedPrincipal portalPrincipal) throws SSOException;

    /**
     * Retrieves the credentials for a SSO user
     * @param user the SSO user for which to return the credential
     * @return the SSO user's credential
     * @throws SSOException
     */
    PasswordCredential getCredentials(SSOUser user) throws SSOException;

    /**
     * Retrieves all SSO users which belong to a SSO site
     * @param site
     * @return the site's SSO users
     * @throws SSOException
     */
    Collection<SSOUser> getUsersForSite(SSOSite site) throws SSOException;
    
    /**
     * Removes the given SSO user
     * @param remoteUser the SSO user to be removed
     * @throws SSOException
     */
    void removeUser(SSOUser remoteUser) throws SSOException;

    /**
     * Adds a SSO user within the given SSO site. Each SSO user is 'owned' by exactly one portal principal called the 'owner principal'.
     * The owner principal will have two associations with the new SSO user: an owner relationship and a usage relationship.
     * @param site the SSO site for which to add the new user
     * @param ownerPrincipal the owner Portal principal
     * @param ssoUsername the name of the new user
     * @param ssoUserPassword the password of the new user
     * @return the new SSO user
     * @throws SSOException
     */
    SSOUser addUser(SSOSite site, JetspeedPrincipal ownerPrincipal, String ssoUsername, String ssoUserPassword) throws SSOException;
    
    /**
     * Updates a SSO user
     * @param user the SSO user to be updated
     * @throws SSOException
     */
    void updateUser(SSOUser user) throws SSOException;    
    
    /**
     * Adds an association between a SSO user and a Portal principal. This will allow the portal principal to 'use' the SSO user
     * to navigate the SSO site.
     * @param user a SSO user
     * @param principal a Portal principal
     * @throws SSOException
     */
    void addAssociation(SSOUser user, JetspeedPrincipal principal) throws SSOException;

    /**
     * Sets the password for a given SSO user
     * @param user the SSO user for which to set the password
     * @param password the new password
     * @throws SSOException
     */
    void setPassword(SSOUser user, String password) throws SSOException;

}
