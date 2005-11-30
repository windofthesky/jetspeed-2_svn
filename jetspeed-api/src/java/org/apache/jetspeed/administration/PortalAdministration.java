/*
 * Copyright 2000-2004 The Apache Software Foundation.
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
package org.apache.jetspeed.administration;

import java.util.List;
import java.util.Map;

import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.apache.jetspeed.security.User;

/**
 * PortalAdministration
 * 
 * Aggregate portal administration functions:
 *  - Emails
 *  - Registration
 *  - Password Generation
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public interface PortalAdministration
{    
    /**
     * Registers and creates a new user, assigning userInfo, roles, groups, 
     * profiling rules and a folder template. If any values are null, defaults
     * are used from the system wide configuration.
     * 
     * @param userName Unique user principal identifier
     * @param password Password for this user
     * @param roles A list of roles to assign to this user
     * @param groups A list of groups to assign to this user
     * @param userInfo Portlet API User Information Attributes name value pairs (PLT.D) 
     * @param rules A map of name value pairs of profiling rules. 
     *              Well known rules names are 'page' and 'menu' 
     * @param folderTemplate The full PSML path name of a folder to be deep
     *               copied as the new user's set of folders, pages, links
     */
    void registerUser(String userName, 
                      String password, 
                      List roles, 
                      List groups,
                      Map userInfo,                       
                      Map rules,
                      String template)
        throws RegistrationException;
 
    /**
     * Register a new user using all default values
     * 
     * @param userName
     * @param password
     */
    void registerUser(String userName, String password)
        throws RegistrationException;
    
    /**
     * Generate a unique password
     * 
     * @return unique password
     */
    String generatePassword();
    
    /**
     * Helper to send an email to a recipient
     * 
     * @param recipient the email address of the recipient
     * @param localizedSubject the subject of the email as a localized string
     * @param message the email message content
     * @parm userAttributes map of user attributes
     * @throws AdministrationEmailException
     */
    public void sendEmail(PortletConfig portletConfig,
                          String emailAddress, 
                          String localizedSubject, 
                          String templatePath,
                          Map userAttributes)
        throws AdministrationEmailException;
    
    /**
     * Lookup a user given an email address
     * 
     * @param email Given email address
     * @return a Jetspeed <code>User</code>, or throw exception if not found
     * @throws AdministrationEmailException
     */
    public User lookupUserFromEmail(String email)
    throws AdministrationEmailException;
    
    /**
     * Provide a common way to get portal URLs
     * Necessary for generating return URLs for features such as 
     * forgotten password. The URL generated will be a combination
     * of the Jetspeed base URL plus the path parameter appended 
     * Example:
     *  base URL = http://www.apache.org/jetspeed/portal
     *      path = /system/forgotten-password.psml
     *  Returns: 
     *     http://www.apache.org/jetspeed/portal/system/forgotten-password.psml
     *     
     * @param request The portlet request.
     * @param response The portlet response, used to encode the path
     * @param path The relative path to a portal resource
     * @return the base Jetspeed portal URL plus the appended path parameter
     */
    String getPortalURL(PortletRequest request, PortletResponse response, String path);
}

