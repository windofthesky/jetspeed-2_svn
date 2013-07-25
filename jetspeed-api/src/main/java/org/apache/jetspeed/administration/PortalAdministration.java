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
package org.apache.jetspeed.administration;

import org.apache.jetspeed.security.User;

import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
     * @param subsite The subsite folder to place the new user in
     * @param locale Optional locale used to compute new user folder path
     *               if subsite not specified
     * @param serverName Server name used to compute new user folder path
     *                   if subsite not specified
     * @since 2.1.2              
     */
     void registerUser(
            String userName, 
            String password, 
            List<String> roles,
            List<String> groups,
            Map<String,String> userInfo,
            Map<String,String> rules,
            String folderTemplate,
            String subsite,
            Locale locale,
            String serverName)
        throws RegistrationException;

    void registerUser(String userName, 
                      String password, 
                      List<String> roles,
                      List<String> groups,
                      Map<String,String> userInfo,
                      Map<String,String> rules,
                      String template,
                      String subsiteFolder)
        throws RegistrationException;

    void registerUser(String userName, 
            String password, 
            List<String> roles,
            List<String> groups,
            Map<String,String> userInfo,
            Map<String,String> rules,
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
     * @param portletConfig portlet configuration
     * @param emailAddress the email address of the recipient
     * @param localizedSubject the subject of the email as a localized string
     * @param templatePath path to templates
     * @parm userAttributes map of user attributes
     * @throws AdministrationEmailException
     */
     void sendEmail(PortletConfig portletConfig,
                          String emailAddress, 
                          String localizedSubject, 
                          String templatePath,
                          Map<String,String> userAttributes)
        throws AdministrationEmailException;
    
    /**
     * Helper to send an email to a recipient without the portal default sender, and without mail merge
     * 
     * @param from the email address of the sender
     * @param subject the subject of the email
     * @param to the recipient email address
     * @param text the message text
     * @throws AdministrationEmailException
     */
     void sendEmail(String from, String subject, String to, String text) throws AdministrationEmailException;
    
    /**
     * Lookup a user given an email address
     * 
     * @param email Given email address
     * @return a Jetspeed <code>User</code>, or throw exception if not found
     * @throws AdministrationEmailException
     */
     User lookupUserFromEmail(String email)
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
    
    
    /**
     * @param guid    The ID which is passed throughte URL to the user
     * @return
     */
     Map<String,String> getNewLoginInfo(String guid);

    /**
     * @param guid    the ID which is passed through the URL to the user.. 
     * @param info    a Map, info from which will be used to reset the password
     *                the password in this case is NOT encrypted, but this should probably
     *                change if this information is stored on disk... ie a database
     */
     void putNewLoginInfo(String guid, Map<String,String> info);
    
    /**
     * @param guid    the ID which will be removed from the storage when the info is no longer valid
     */
     void removeNewLoginInfo(String guid);
    
    /**
     * Returns true if the current request user principal's name is the name of the portal admin user.
     * @param request
     * @return
     */
    boolean isAdminUser(PortletRequest request);
    
    /**
     * Returns true if the current request user principal is in the portal admin role.
     * @param request
     * @return
     */
    boolean isUserInAdminRole(PortletRequest request);
    
    /**
     * Returns PSML user folder path for specified user by
     * running full profiler and portal site rules.
     * 
     * @param userName existing portal user name
     * @param locale optional locale, (defaults to system locale, for language
     *               profiling rules)
     * @param serverName server name, (required for subsite profiling rules)
     * @return PSML user folder path
     */
    String getUserFolderPath(String userName, Locale locale, String serverName);

    /**
     * Returns PSML base folder path for specified user by
     * running full profiler and portal site rules.
     * 
     * @param userName existing portal user name
     * @param locale optional locale, (defaults to system locale, for language
     *               profiling rules)
     * @param serverName server name, (required for subsite profiling rules)
     * @return PSML base folder path
     */
    String getBaseFolderPath(String userName, Locale locale, String serverName);
}
