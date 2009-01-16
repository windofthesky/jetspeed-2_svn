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
package org.apache.jetspeed.portlet.sso;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.util.Collection;

import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.security.auth.Subject;

import org.apache.jetspeed.security.JSSubject;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.sso.SSOException;
import org.apache.jetspeed.sso.SSOManager;
import org.apache.jetspeed.sso.SSOSite;
import org.apache.jetspeed.sso.SSOUser;


/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public abstract class SSOPortletUtil
{

    public static Subject getSubject()
    {
        AccessControlContext context = AccessController.getContext();
        return JSSubject.getSubject(context);         
    }
    
    public static SSOUser getRemoteUser(SSOManager sso, PortletRequest request, SSOSite site) throws SSOException {
        Subject subject = getSubject();
        if (subject != null){
            Collection<SSOUser> remoteUsers = sso.getRemoteUsers(site, subject);
            // keep backwards compatibility : enforce a relationship (ssouser : user) of 1-to-n. 
            // TODO: support multiple SSO users and select 1 that is used for browsing.
            if (remoteUsers.size() == 1){
                return remoteUsers.iterator().next();
            }
        }
        return null;
    }
    
    public static void updateUser(SSOManager sso, PortletRequest request, SSOSite site, String newPrincipal, String newPassword) throws SSOException {
        SSOUser remoteUser = getRemoteUser(sso,request,site);
        if (remoteUser != null){
            if (!remoteUser.getName().equals(newPrincipal)){
                // rename SSO user and update
                remoteUser.setName(newPrincipal);
                sso.updateUser(remoteUser);
            }
            sso.setPassword(remoteUser, newPassword);
       }
    }
    
    public static PasswordCredential getCredentialsForSite(SSOManager sso, String siteName, RenderRequest request){
        PasswordCredential pwc = null;
        SSOSite site = sso.getSiteByName(siteName);        
        if (site != null){
            return getCredentialsForSite(sso, site, request);
        }
        return pwc;
    }
    
    public static PasswordCredential getCredentialsForSite(SSOManager sso, SSOSite site, RenderRequest request){
        PasswordCredential pwc = null;
        try{
            SSOUser remoteUser = getRemoteUser(sso,request,site);
            if (remoteUser != null){
                pwc=sso.getCredentials(remoteUser);    
            }
            
        } catch (SSOException sx){
            
        }
        return pwc;
    }
}
