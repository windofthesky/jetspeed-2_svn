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
 * Aggregate portal administration functions are controlled with this service. Administrative services include:
 * <ul>
 *  <li>- Emails delivery services</li>
 *  <li>- User Registration services and options</li>
 *  <li>- Password Generation services</li>
 * </ul>
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
     * @throws RegistrationException
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
     * @param template The full PSML path name of a folder to be deep
     *               copied as the new user's set of folders, pages, links
     * @param subsiteFolder The subsite folder to place the new user in
     * @throws RegistrationException
     */
    void registerUser(String userName, 
                      String password, 
                      List<String> roles,
                      List<String> groups,
                      Map<String,String> userInfo,
                      Map<String,String> rules,
                      String template,
                      String subsiteFolder)
        throws RegistrationException;

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
     * @param template The full PSML path name of a folder to be deep
     *               copied as the new user's set of folders, pages, links
     * @throws RegistrationException
     */
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
     * @param userName Unique user principal identifier
     * @param password Password for this user
     * @throws RegistrationException
     */
    void registerUser(String userName, String password)
        throws RegistrationException;
    
    /**
     * Generate a unique password following the credential policy of the Password Generator service
     * 
     * @return the unique password
     */
    String generatePassword();
    
    /**
     * Using the portal's email configuration settings, sends an email to a mail recipient. Does a mail merge using
     * the <code>userAttributes</code> parameter for merged values, merging into the mail body from
     * the configured email <code>template</code>
     *
     * @param portletConfig portlet configuration
     * @param emailAddress the email address of the recipient
     * @param localizedSubject the subject of the email as a localized string
     * @param template portal relative path to the template used to do the mail merge
     * @param userAttributes map of user attributes to substitute into template
     * @throws AdministrationEmailException
     */
     void sendEmail(PortletConfig portletConfig,
                          String emailAddress, 
                          String localizedSubject, 
                          String template,
                          Map<String,String> userAttributes)
        throws AdministrationEmailException;
    
    /**
     * Using the portal's email configuration, sends a simple email to a mail recipient. Does not do a mail merge,
     * simply sends the <code>text</code> parameter as the mail body.
     * 
     * @param from the email address of the sender
     * @param subject the subject of the email
     * @param to the recipient email address
     * @param text the message text
     * @throws AdministrationEmailException
     */
     void sendEmail(String from, String subject, String to, String text) throws AdministrationEmailException;
    
    /**
     * Lookup a Jetspeed user, given an email address
     * 
     * @param email Given email address
     * @return a Jetspeed User or throw exception if not found
     * @throws AdministrationEmailException
     */
     User lookupUserFromEmail(String email)
        throws AdministrationEmailException;
    
    /**
     * Provides a common way to generating portal URLs.
     * Generates return URLs for features such as forgotten password. The URL generated will be a combination
     * of the Jetspeed base URL plus the path parameter appended.  For example, the following idiom
     * <pre>
     * Example:
     *  base URL = http://www.apache.org/jetspeed/portal
     *      path = /system/forgotten-password.psml
     *  Returns: 
     *     http://www.apache.org/jetspeed/portal/system/forgotten-password.psml
     * </pre>
     * @param request The portlet request.
     * @param response The portlet response, used to encode the path
     * @param path The relative path to a portal resource
     * @return the base Jetspeed portal URL plus the appended path parameter
     */
    String getPortalURL(PortletRequest request, PortletResponse response, String path);
    
    
    /**
     * Administrative portlets, like the forgotten password admin, need to track login information such as
     * temporary links to recovery temporary passwords. This method takes a <code>GUID</code> and looks up
     * the application specific login information associated with that GUID. This information is normally
     * temporary and may have a short-lived lifespan.
     *
     * @param guid the temporary identifier to associate with the login information
     * @return the new login information associated with the <code>guid</code>
     */
     Map<String,String> getNewLoginInfo(String guid);

    /**
     * Administrative portlets, like the forgotten password admin, need to track login information such as
     * temporary links to recovery temporary passwords. This method takes a <code>GUID</code> and stores the
     * the provided application specific login information associated with that GUID. This information is normally
     * temporary and may have a short-lived lifespan.
     *
     * @param guid    the temporary identifier to associate with the login information
     * @param info    a <code>Map</code> of login information specific to application
     */
     void putNewLoginInfo(String guid, Map<String,String> info);
    
    /**
     * Administrative portlets, like the forgotten password admin, need to track login information such as
     * temporary links to recovery temporary passwords. This method takes a <code>GUID</code> and removes
     * application specific login information associated with that GUID. This information is normally
     * temporary and may have a short-lived lifespan.
     *
     * @param guid the temporary identifier to associate with the login information
     */
     void removeNewLoginInfo(String guid);
    
    /**
     * Returns true if the current request is made by the special portal admin user.
     *
     * @see PortalConfigurationConstants#USERS_DEFAULT_ADMIN
     * @param request the PortletRequest to check the user principal on
     * @return <tt>true</tt> if this request is made by the portal admin user
     */
    boolean isAdminUser(PortletRequest request);
    
    /**
     * Returns true if the current request user principal is made by a user in the portal admin role
     *
     * @see PortalConfigurationConstants#ROLES_DEFAULT_ADMIN
     * @param request the PortletRequest to check the user principal on
     * @return <tt>true</tt> if this request is made by a user with the portal admin role
     */
    boolean isUserInAdminRole(PortletRequest request);
    
    /**
     * Returns a PSML path to the root user folder for the specified user by
     * running the full profiler and portal site rules.
     * 
     * @param userName the portal user name
     * @param locale optional locale, (defaults to system locale, for language
     *               profiling rules)
     * @param serverName server name, (required for subsite profiling rules)
     * @return a normalized PSML user folder path
     * @deprecated
     */
    String getUserFolderPath(String userName, Locale locale, String serverName);

    /**
     * Returns a PSML path to the base user folder for the specified user by
     * running the full profiler and portal site rules.
     * 
     * @param userName existing portal user name
     * @param locale optional locale, (defaults to system locale, for language
     *               profiling rules)
     * @param serverName server name, (required for subsite profiling rules)
     * @return a normalized PSML user folder path
     * @deprecated
     */
    String getBaseFolderPath(String userName, Locale locale, String serverName);
}
