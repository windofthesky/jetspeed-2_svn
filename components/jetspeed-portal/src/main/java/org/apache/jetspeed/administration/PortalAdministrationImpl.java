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

import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.mockobjects.MockHttpServletRequest;
import org.apache.jetspeed.mockobjects.request.MockRequestContext;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.folder.InvalidFolderException;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.portalsite.PortalSite;
import org.apache.jetspeed.portalsite.PortalSiteRequestContext;
import org.apache.jetspeed.portalsite.PortalSiteSessionContext;
import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.profiler.rules.ProfilingRule;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.GroupManager;
import org.apache.jetspeed.security.JSSubject;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.SecurityAttributes;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import java.io.FileReader;
import java.io.StringWriter;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * PortalAdministrationImpl
 * Implements aggregate portal administration functions:
 * - Emails
 * - Registration
 * - Password Generation
 * -
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:chris@bluesunrise.com">Chris Schaefer</a>
 * @version $Id$
 */
public class PortalAdministrationImpl implements PortalAdministration {
    private final static Logger log = LoggerFactory.getLogger(PortalAdministrationImpl.class);

    /**
     * administration services
     */
    protected PortalConfiguration configuration;
    protected UserManager userManager;
    protected RoleManager roleManager;
    protected GroupManager groupManager;
    protected PageManager pageManager;
    protected Profiler profiler;
    protected PortalSite portalSite;
    protected JavaMailSender mailSender;
    protected VelocityEngine velocityEngine;
    protected PasswordGenerator passwordGenerator;

    /**
     * list of default roles for a registered user
     */
    protected List<String> defaultRoles;
    /**
     * list of default groups for a registered user
     */
    protected List<String> defaultGroups;
    /**
     * map of default profiling rules for a registered user
     */
    protected Map<String, String> defaultRules;
    /**
     * name of PSML Folder Template to clone from when registering new user
     */
    protected String folderTemplate;
    /**
     * default administrative user
     */
    protected String adminUser;
    /**
     * default administrative role
     */
    protected String adminRole;

    public PortalAdministrationImpl(UserManager userManager,
                                    RoleManager roleManager,
                                    GroupManager groupManager,
                                    PageManager pageManager,
                                    Profiler profiler,
                                    PortalSite portalSite,
                                    JavaMailSender mailSender,
                                    VelocityEngine velocityEngine) {
        this.userManager = userManager;
        this.roleManager = roleManager;
        this.groupManager = groupManager;
        this.pageManager = pageManager;
        this.profiler = profiler;
        this.portalSite = portalSite;
        this.mailSender = mailSender;
        this.velocityEngine = velocityEngine;
        this.passwordGenerator = new SimplePasswordGeneratorImpl();
    }

    public void setPasswordGenerator(PasswordGenerator passwordGenerator) {
        if (passwordGenerator != null) {
            this.passwordGenerator = passwordGenerator;
        }
    }

    public PortalConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(PortalConfiguration configuration) {
        this.configuration = configuration;
    }

    public void start() {
        this.defaultRoles = configuration.getList(PortalConfigurationConstants.REGISTRATION_ROLES_DEFAULT);
        this.defaultGroups = configuration.getList(PortalConfigurationConstants.REGISTRATION_GROUPS_DEFAULT);

        String[] profileRuleNames = configuration.getStringArray(PortalConfigurationConstants.PROFILER_RULE_NAMES_DEFAULT);
        String[] profileRuleValues = configuration.getStringArray(PortalConfigurationConstants.PROFILER_RULE_VALUES_DEFAULT);
        defaultRules = new HashMap<String, String>();
        if (profileRuleNames != null && profileRuleValues != null) {
            for (int ix = 0; ix < ((profileRuleNames.length < profileRuleValues.length) ? profileRuleNames.length : profileRuleValues.length); ix++) {
                defaultRules.put(profileRuleNames[ix], profileRuleValues[ix]);
            }
        }
        this.folderTemplate =
                configuration.getString(PortalConfigurationConstants.PSML_TEMPLATE_FOLDER);
        this.adminUser = configuration.getString(PortalConfigurationConstants.USERS_DEFAULT_ADMIN);
        this.adminRole = configuration.getString(PortalConfigurationConstants.ROLES_DEFAULT_ADMIN);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.administration.PortalAdministration#registerUser(java.lang.String, java.lang.String)
     */
    public void registerUser(String userName, String password)
            throws RegistrationException {
        registerUser(userName, password, (List) null, null, null, null, null);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.administration.PortalAdministration#registerUser(java.lang.String, java.lang.String, java.util.List, java.util.List, java.util.Map, java.util.Map, java.lang.String)
     */
    public void registerUser(
            String userName,
            String password,
            List<String> roles,
            List<String> groups,
            Map<String, String> userInfo,
            Map<String, String> rules,
            String folderTemplate)
            throws RegistrationException {
        registerUser(userName, password, roles, groups, userInfo, rules, folderTemplate, null);
    }

    /* (non-Javadoc)

     * @see org.apache.jetspeed.administration.PortalAdministration#registerUser(java.lang.String, java.lang.String, java.util.List, java.util.List, java.util.Map, java.util.Map, java.lang.String, java.lang.String)
     */
    public void registerUser(
            String userName,
            String password,
            List<String> roles,
            List<String> groups,
            Map<String, String> userInfo,
            Map<String, String> rules,
            String folderTemplate,
            String subsite)
            throws RegistrationException {
        registerUser(userName, password, roles, groups, userInfo, rules, folderTemplate, subsite, null, null);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.administration.PortalAdministration#registerUser(java.lang.String, java.lang.String, java.util.List, java.util.List, java.util.Map, java.util.Map, java.lang.String, java.lang.String, java.util.Locale, java.lang.String)
     */
    public void registerUser(
            String userName,
            String password,
            List<String> roles,
            List<String> groups,
            Map<String, String> userInfo,
            Map<String, String> rules,
            String folderTemplate,
            String subsite,
            Locale locale,
            String serverName)
            throws RegistrationException {
        try {
            // create the user
            User user = userManager.addUser(userName);
            PasswordCredential pwc = userManager.getPasswordCredential(user);
            pwc.setPassword(null, password);
            userManager.storePasswordCredential(pwc);

            // assign roles to user
            if (roles == null || roles.isEmpty()) {
                roles = this.defaultRoles;
            }
            if (roles != null) {
                for (String role : roles) {
                    if (role.trim().length() > 0) {
                        roleManager.addRoleToUser(userName, role);
                    }
                }
            }

            // assign groups to user
            if (groups == null || groups.isEmpty()) {
                groups = this.defaultGroups;
            }
            if (groups != null) {
                for (String group : groups) {
                    if (group.trim().length() > 0) {
                        groupManager.addUserToGroup(userName, group);
                    }

                }
            }

            // assign user attributes to user
            if (userInfo != null) {
                SecurityAttributes userAttrs = user.getSecurityAttributes();
                for (Map.Entry<String, String> entry : userInfo.entrySet()) {
                    userAttrs.getAttribute(entry.getKey(), true).setStringValue(entry.getValue());
                }
            }

            // assign profiling rules to user
            if (rules == null || rules.isEmpty()) {
                rules = this.defaultRules;
            }
            if (rules != null) {
                for (Map.Entry<String, String> entry : rules.entrySet()) {
                    ProfilingRule rule = profiler.getRule(entry.getValue());
                    if (rule != null) {
                        profiler.setRuleForPrincipal(user, rule, entry.getKey());
                    }
                }
            }

            // get template folders
            if (folderTemplate == null) {
                folderTemplate = this.folderTemplate;
            }
            String userFolderPath = null;
            if ((subsite == null) && (serverName != null)) {
                userFolderPath = invokeGetUserFolderPath(user, locale, serverName);
            } else if (subsite != null) {
                userFolderPath = subsite + Folder.USER_FOLDER + userName;
            } else {
                userFolderPath = Folder.USER_FOLDER + userName;
            }

            // This next chunk of code is the fancy way to force the creation of the user
            // template pages to be created with subject equal to the new user
            // otherwise it would be created as guest, and guest does not have enough privs.
            final String innerFolderTemplate = folderTemplate;
            final String innerUserFolderPath = userFolderPath;
            final PageManager innerPageManager = pageManager;
            final String innerUserName = userName;
            final User innerUser = user;
            User powerUser = userManager.getUser(this.adminUser);
            JetspeedException pe = (JetspeedException) JSSubject.doAsPrivileged(userManager.getSubject(powerUser), new PrivilegedAction() {
                public Object run() {
                    try {
                        if (innerUserFolderPath != null) {
                            innerUser.getSecurityAttributes().getAttribute(User.JETSPEED_USER_SUBSITE_ATTRIBUTE, true).setStringValue(innerUserFolderPath);
                            userManager.updateUser(innerUser);
                        }
                        // ensure user folder parents are created
                        Folder makeFolder = innerPageManager.getFolder("/");
                        for (; ; ) {
                            String path = makeFolder.getPath();
                            if (!path.endsWith("/")) {
                                path += "/";
                            }
                            if (innerUserFolderPath.startsWith(path)) {
                                String makeFolderName = innerUserFolderPath.substring(path.length());
                                int endFolderNameIndex = makeFolderName.indexOf('/');
                                if (endFolderNameIndex != -1) {
                                    makeFolderName = makeFolderName.substring(0, endFolderNameIndex);
                                    String makeFolderPath = path + makeFolderName;
                                    if (!innerPageManager.folderExists(makeFolderPath)) {
                                        makeFolder = innerPageManager.newFolder(makeFolderPath);
                                        innerPageManager.updateFolder(makeFolder);
                                    } else {
                                        makeFolder = innerPageManager.getFolder(makeFolderPath);
                                    }
                                } else {
                                    break;
                                }
                            } else {
                                throw new FolderNotFoundException("Cannot make parent folders for user folder: " + innerUserFolderPath);
                            }
                        }
                        // create user's home folder
                        // deep copy from the default folder template tree, creating a deep-copy of the template
                        // in the new user's folder tree
                        Folder source = innerPageManager.getFolder(innerFolderTemplate);
                        innerPageManager.deepCopyFolder(source, innerUserFolderPath, innerUserName);
                        Folder newFolder = pageManager.getFolder(innerUserFolderPath);
                        newFolder.setTitle("My Home Space");
                        newFolder.setShortTitle("My Space");
                        return null;
                    } catch (SecurityException s1) {
                        return s1;
                    } catch (FolderNotFoundException e1) {
                        return e1;
                    } catch (InvalidFolderException e1) {
                        return e1;
                    } catch (NodeException e1) {
                        return e1;
                    }
                }
            }, null);

            if (pe != null) {
                // rollback user creation and cascade roles, groups, etc
                try {
                    if (userManager.getUser(userName) != null) {
                        userManager.removeUser(userName);
                    }
                } catch (Exception e) {
                    log.error("Registration Error: Failed to rollback user " + userName);
                }
                log.error("Registration Error: Failed to create user folders for " + userName + ", " + pe.toString());
                throw pe;
            }

        } catch (Exception e) {
            log.error("Registration Error: Failed to create registered user " + userName + ", " + e.toString());
            throw new RegistrationException(e);
        }
    }


    /* (non-Javadoc)
     * @see org.apache.jetspeed.administration.PortalAdministration#generatePassword()
     */
    public String generatePassword() {
        return passwordGenerator.generatePassword();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.administration.PortalAdministration#sendPassword(java.lang.String)
     */
    public void sendEmail(PortletConfig portletConfig,
                          String emailAddress,
                          String localizedSubject,
                          String localizedTemplatePath,
                          Map<String, String> userAttributes)
            throws AdministrationEmailException {

        String from = configuration.getString(PortalConfigurationConstants.EMAIL_SENDER);
        String subject = localizedSubject;
        String to = emailAddress;
        String text = mergeEmailTemplate(portletConfig, userAttributes, "map", localizedTemplatePath);
        sendEmail(from, subject, to, text);

    }

    /**
     * @param from
     * @param subject
     * @param to
     * @param text
     * @throws AdministrationEmailException
     */
    public void sendEmail(String from, String subject, String to, String text) throws AdministrationEmailException {
        SimpleMailMessage msg = new SimpleMailMessage();
        if (from == null) {
            from = "jetspeed-admin@apache.org";
        }
        msg.setFrom(from);
        if (subject == null) {
            subject = "message from jetspeed";
        }
        msg.setSubject(subject);
        msg.setTo(to);
        msg.setText(text);

        ClassLoader currentCL = Thread.currentThread().getContextClassLoader();

        try {
            // JS2-1256: Needs to set context classloader to null to let geronimo-javamail find provider class properly.
            Thread.currentThread().setContextClassLoader(null);
            mailSender.send(msg);
        } catch (Exception ex) {
            String message = "Failed to send forgotten password email to user with email address because " + ex.getMessage();
            log.error(message, ex);
            throw new AdministrationEmailException(message, ex
            ); //+ user.getEmail());
        } finally {
            Thread.currentThread().setContextClassLoader(currentCL);
        }
    }

    public String mergeEmailTemplate(PortletConfig portletConfig, Map<String, String> attributes, String attributesName, String template)
            throws AdministrationEmailException {
        VelocityContext context = new VelocityContext();
        context.put(attributesName, attributes);
        StringWriter writer = new StringWriter();

        try {
            String realTemplatePath = portletConfig.getPortletContext().getRealPath(template);
            FileReader templateReader = new FileReader(realTemplatePath);
            velocityEngine.evaluate(context, writer, "UserEmailProcessor", templateReader);
        } catch (Exception e) {
            throw new AdministrationEmailException(
                    "Failed to generate email text for email template "
                            + template, e);
        }

        String buffer = writer.getBuffer().toString();

        return buffer;
    }

    private static final String USER_NOT_FOUND_FROM_EMAIL = "User not found for Email address: ";

    public User lookupUserFromEmail(String email)
            throws AdministrationEmailException {
        Collection<User> users;
        try {
            users = userManager.lookupUsers("user.business-info.online.email", email);
        } catch (SecurityException e) {
            throw new AdministrationEmailException(e);
        }
        if (users.isEmpty()) {
            throw new AdministrationEmailException(USER_NOT_FOUND_FROM_EMAIL + email);
        }
        return users.iterator().next(); // return the first one and hopefully the only (FIXME: need unique constraints)
    }

    /**
     * Helper for admin portlets to generate portal urls
     */
    public String getPortalURL(PortletRequest request, PortletResponse response, String path) {
        // get internal request context
        RequestContext context = (RequestContext)
                request.getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
        String baseUrl = context.getPortalURL().getBaseURL();
        String basePath = context.getPortalURL().getBasePath();
        if (basePath != null && basePath.endsWith("/action")) {
            basePath = basePath.replace("/action", "/desktop");
        }
        String jetspeedPath = AdminUtil.concatenatePaths(baseUrl, basePath);
        if (path == null)
            return jetspeedPath;
        return AdminUtil.concatenatePaths(jetspeedPath, response.encodeURL(path));
    }


    final Map<String, Map<String, String>> forgottenPasswordData = new HashMap<String, Map<String, String>>();

    /* (non-Javadoc)
     * @see org.apache.jetspeed.administration.PortalAdministration#getNewLoginInfo(java.lang.String)
     */
    public Map<String, String> getNewLoginInfo(String guid) {
        synchronized (forgottenPasswordData) {
            return forgottenPasswordData.get(guid);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.administration.PortalAdministration#setNewLoginInfo(java.lang.String, org.apache.jetspeed.administration.PortalAdministration.ResetPasswordInfo)
     */
    public void putNewLoginInfo(String guid, Map<String, String> info) {
        synchronized (forgottenPasswordData) {
            forgottenPasswordData.put(guid, info);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.administration.PortalAdministration#removeNewLoginInfo(java.lang.String)
     */
    public void removeNewLoginInfo(String guid) {
        synchronized (forgottenPasswordData) {
            forgottenPasswordData.remove(guid);
        }
    }

    public boolean isAdminUser(PortletRequest request) {
        if (adminUser == null) {
            throw new IllegalStateException("PortalAdministration component is not started or misconfigured for the default admin user.");
        }

        Principal principal = request.getUserPrincipal();
        if (null != principal) {
            return adminUser.equals(principal.getName());
        }
        return false;
    }

    public boolean isUserInAdminRole(PortletRequest request) {
        if (adminRole == null) {
            throw new IllegalStateException("PortalAdministration component is not started or misconfigured for the default admin role.");
        }
        return request.isUserInRole(adminRole);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.administration.PortalAdministration#getUserFolderPath(java.lang.String, java.util.Locale, java.lang.String)
     */
    public String getUserFolderPath(String userName, Locale locale, String serverName) {
        try {
            User user = userManager.getUser(userName);
            return invokeGetUserFolderPath(user, locale, serverName);
        } catch (Exception e) {
            log.error("Unexpected exception getting user folder path for " + userName + ": " + e, e);
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.administration.PortalAdministration#getBaseFolderPath(java.lang.String, java.util.Locale, java.lang.String)
     */
    public String getBaseFolderPath(String userName, Locale locale, String serverName) {
        try {
            User user = userManager.getUser(userName);
            return invokeGetBaseFolderPath(user, locale, serverName);
        } catch (Exception e) {
            log.error("Unexpected exception getting base folder path for " + userName + ": " + e, e);
            return null;
        }
    }

    /**
     * Returns PSML user folder path for specified user by
     * running full profiler and portal site rules within a
     * JSSubject.doAsPrivileged() block with a subject matching
     * the specified user.
     *
     * @param user       existing portal user
     * @param locale     optional locale, (defaults to system locale, for language
     *                   profiling rules)
     * @param serverName server name, (required for subsite profiling rules)
     * @return PSML user folder path
     * @throws Exception
     */
    private String invokeGetUserFolderPath(final User user, final Locale locale, final String serverName) throws Exception {
        if (configuration.getString(PortalConfigurationConstants.JETUI_CUSTOMIZATION_METHOD).equals(PortalConfigurationConstants.JETUI_CUSTOMIZATION_AJAX)) {
            return Folder.USER_FOLDER + user.getName();
        }
        Object doneAs = JSSubject.doAsPrivileged(userManager.getSubject(user), new PrivilegedAction() {
            public Object run() {
                try {
                    PortalSiteRequestContext requestContext = getMockPortalSiteRequestContext(user, locale, serverName);
                    return requestContext.getUserFolderPath();
                } catch (Exception e) {
                    return e;
                }
            }
        }, null);
        if (doneAs instanceof Exception) {
            throw (Exception) doneAs;
        }
        return (String) doneAs;
    }

    /**
     * Returns PSML base folder path for specified user by
     * running full profiler and portal site rules within a
     * JSSubject.doAsPrivileged() block with a subject matching
     * the specified user.
     *
     * @param user       existing portal user
     * @param locale     optional locale, (defaults to system locale, for language
     *                   profiling rules)
     * @param serverName server name, (required for subsite profiling rules)
     * @return PSML base folder path
     * @throws Exception
     */
    private String invokeGetBaseFolderPath(final User user, final Locale locale, final String serverName) throws Exception {
        Object doneAs = JSSubject.doAsPrivileged(userManager.getSubject(user), new PrivilegedAction() {
            public Object run() {
                try {
                    PortalSiteRequestContext requestContext = getMockPortalSiteRequestContext(user, locale, serverName);
                    return requestContext.getBaseFolderPath();
                } catch (Exception e) {
                    return e;
                }
            }
        }, null);
        if (doneAs instanceof Exception) {
            throw (Exception) doneAs;
        }
        return (String) doneAs;
    }

    /**
     * Returns temporary mock portal site request context for
     * specified user for use in constructing user or base PSML
     * folder paths or accessing other profiled site data. This
     * method invocation should be wrapped in a
     * JSSubject.doAsPrivileged() block with a subject matching
     * the specified user.
     *
     * @param user       portal user
     * @param locale     optional locale, (defaults to system locale, for language
     *                   profiling rules)
     * @param serverName server name, (required for subsite profiling rules)
     * @return portal site request context
     * @throws Exception
     */
    private PortalSiteRequestContext getMockPortalSiteRequestContext(User user, Locale locale, String serverName) throws Exception {
        // setup profiler and portal site to determine template
        // folders paths generate mock request for new user to profile
        RequestContext request = new MockRequestContext("/");
        request.setSubject(userManager.getSubject(user));
        request.setLocale((locale != null) ? locale : Locale.getDefault());
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        if (serverName != null) {
            servletRequest.setServerName(serverName);
        }
        request.setRequest(servletRequest);

        // get profile locators map for new user request, (taken from
        // ProfilerValveImpl)
        Map<String, ProfileLocator> locators = profiler.getProfileLocators(request, user);
        if (locators.size() == 0) {
            locators = profiler.getDefaultProfileLocators(request);
        }
        if (locators.size() == 0) {
            locators.put(ProfileLocator.PAGE_LOCATOR, profiler.getProfile(request, ProfileLocator.PAGE_LOCATOR));
        }

        // get new portal site request context from portal site
        // component using the profile locators for new user request
        PortalSiteSessionContext sessionContext = portalSite.newSessionContext();
        return sessionContext.newRequestContext(locators, user.getName());
    }

}
