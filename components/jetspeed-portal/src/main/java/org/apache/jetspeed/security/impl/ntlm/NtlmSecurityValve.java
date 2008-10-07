/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.security.impl.ntlm;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.Subject;

import org.apache.commons.lang.StringUtils;
import org.apache.jetspeed.administration.PortalAuthenticationConfiguration;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.SubjectHelper;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.impl.AbstractSecurityValve;
import org.apache.jetspeed.statistics.PortalStatistics;
/**
 * NTLMSecurityValve provides Subject creation based on the
 * NTLM provided request.getRemoteUser() user name. When request.getRemoteUser() holds
 * a valid value, then this user is authorized. Otherwise the username is retrieved
 * from the Principal name in the request. In this way you can use NTLM authentication, with
 * a fallback authentication method in case the user is not properly authenticated / authorized using
 * NTLM. 
 * 
 * There are basically three authentication scenarios:
 * <ol>
 *   <li>
 *     <p><b>The user is successfully authenticated and authorized by Ntml authentication</b></p>
 *     <p>A Subject is created, with Principal derived from the remoteUser value from Ntlm authentication</p>
 *   </li>
 *   <li> 
 *     <p><b>The user is not authenticated by Ntlm, or the authenticated (can be NTLM or any other method) user cannot be authorized by Jetspeed.</b></p>
 *     <p>An anonymous Subject is created. The user can then be redirected to a login page for example.</p>
 *   </li>
 *   <li> 
 *     <p><b>The user is authenticated by a (non-NTLM) authentication method, e.g. container-based form authentication.</b></p>
 *     <p>
 *       A subject is created based on the Principal name in the request.
 *     </p>
 *   </li>
 * </ol>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @author <a href="mailto:rwatler@finali.com">Randy Walter </a>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @author <a href="mailto:d.dam@hippo.nl">Dennis Dam</a>
 * @version $Id$
 */
public class NtlmSecurityValve extends AbstractSecurityValve 
{
    private UserManager userMgr;
    private PortalStatistics statistics;
    private String networkDomain;
    private boolean ntlmAuthRequired;
    private boolean omitDomain;
    
    
    /**
     * @param userMgr A UserManager
     * @param statistics Portal Statistics
     * @param networkDomain The network domain is used in combination with the <code>omitDomain</code> flag. 
     * @param omitDomain If <code>true</code>, then the network domain is stripped from the remoteUser name.
     * @param ntlmAuthRequired if <code>true</code>, then an exception is thrown when there is no valid remoteUser,
     * or the remoteUser cannot be authorized.
     * 
     */
    public NtlmSecurityValve(UserManager userMgr, String networkDomain, boolean omitDomain, boolean ntlmAuthRequired, 
            PortalStatistics statistics, PortalAuthenticationConfiguration authenticationConfiguration) 
    {
        this.userMgr = userMgr;
        this.statistics = statistics;
        this.networkDomain = networkDomain;
        this.ntlmAuthRequired = ntlmAuthRequired;
        this.omitDomain = omitDomain;
        this.authenticationConfiguration = authenticationConfiguration;
    }

    public NtlmSecurityValve(UserManager userMgr, String networkDomain, boolean omitDomain, boolean ntlmAuthRequired, PortalStatistics statistics)
    {
        this(userMgr, networkDomain, omitDomain, ntlmAuthRequired, statistics, null);        
    }
    
    public NtlmSecurityValve(UserManager userMgr, String networkDomain, boolean omitDomain, boolean ntlmAuthRequired)
    {
        this(userMgr, networkDomain, omitDomain, ntlmAuthRequired, null);
    }

    public String toString()
    {
        return "NtlmSecurityValve";
    }
 
    protected Principal getUserPrincipal(RequestContext context) throws Exception 
    {
        Subject subject = getSubjectFromSession(context);
        if (subject != null)
        {
            return SubjectHelper.getPrincipal(subject, User.class);
        } 
        // otherwise return anonymous principal
        
        return  userMgr.newTransientUser(userMgr.getAnonymousUser());
    }

    protected Subject getSubject(RequestContext context) throws Exception 
    {
        Subject subject = getSubjectFromSession(context);
        // Get remote user name set by web container
        String userName = context.getRequest().getRemoteUser();
        if ( userName == null )
        {            
            if (ntlmAuthRequired){
                throw new PipelineException("Authorization failed.");    
            } else if (context.getRequest().getUserPrincipal() != null){
                userName = context.getRequest().getUserPrincipal().getName();
            }             
        } else {
            if (omitDomain && networkDomain != null){
                userName = StringUtils.stripStart( userName , networkDomain+"\\");
            }
        }
        
        // check whether principal name stored in session subject equals the remote user name passed by the web container
        if (subject != null)
        {
            Principal subjectUserPrincipal = SubjectHelper.getPrincipal(subject, User.class);
            if ((subjectUserPrincipal == null) || !subjectUserPrincipal.getName().equals(userName))
            {
                subject = null;
            }
        }
        if ( subject == null ){
            if (userName != null){
                try
                {                    
                    User user = userMgr.getUser(userName);
                    if ( user != null )
                    {
                        subject = userMgr.getSubject(user);
                    }
                } catch (SecurityException sex)
                {
                    subject = null;
                }
                
                if (subject == null && this.ntlmAuthRequired){
                    throw new PipelineException("Authorization failed for user '"+userName+"'.");
                }
            }  
            if (subject == null){
                // create anonymous user
                subject = userMgr.getSubject(userMgr.getUser(userMgr.getAnonymousUser()));
            }
            
            // create a new statistics *user* session
            if (statistics != null)
            {
                statistics.logUserLogin(context, 0);
            }
            // put IP address in session for logout
            context.setSessionAttribute(IP_ADDRESS, context.getRequest().getRemoteAddr());            
        }        
        
        return subject;
    }
}
