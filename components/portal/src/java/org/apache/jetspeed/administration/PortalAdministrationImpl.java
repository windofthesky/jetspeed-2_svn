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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import org.apache.commons.configuration.Configuration;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.prefs.PreferencesProvider;
import org.apache.jetspeed.prefs.om.Node;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.profiler.rules.ProfilingRule;
import org.apache.jetspeed.security.GroupManager;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.SecurityHelper;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;


/**
 * PortalAdministrationImpl
 * Implements aggregate portal administration functions:
 *  - Emails
 *  - Registration
 *  - Password Generation
 *  - 
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */

public class PortalAdministrationImpl implements PortalAdministration
{
    /** administration services */
    protected Configuration config;
    protected UserManager userManager;
    protected RoleManager roleManager;
    protected GroupManager groupManager;
    protected PageManager pageManager;
    private PreferencesProvider preferences;    
    protected Profiler profiler;
    protected JavaMailSender mailSender;
    protected VelocityEngine velocityEngine;
    
    /** list of default roles for a registered user */
    protected List defaultRoles;
    /** list of default groups for a registered user */
    protected List defaultGroups;
    /** map of default profiling rules for a registered user */
    protected Map defaultRules;
    /** name of PSML Folder Template to clone from when registering new user */
    protected String folderTemplate;
    
    public PortalAdministrationImpl( UserManager userManager,
                                     RoleManager roleManager,
                                     GroupManager groupManager, 
                                     PageManager pageManager,
                                     PreferencesProvider preferences,
                                     Profiler profiler,
                                     JavaMailSender mailSender,
                                     VelocityEngine velocityEngine)
    {
        this.userManager = userManager;
        this.roleManager = roleManager;
        this.groupManager = groupManager;
        this.pageManager = pageManager;
        this.preferences = preferences;
        this.mailSender = mailSender;
        this.velocityEngine = velocityEngine;                        
    }

    public void start()
    {
        this.config = (Configuration) Jetspeed.getComponentManager().getComponent("PortalConfiguration");
        
        this.defaultRoles = 
            config.getList(PortalConfigurationConstants.REGISTRATION_ROLES_DEFAULT);
        this.defaultGroups = 
            config.getList(PortalConfigurationConstants.REGISTRATION_GROUPS_DEFAULT);
    
        Object[] profileRuleNames = (Object[]) 
            config.getList(PortalConfigurationConstants.PROFILER_RULE_NAMES_DEFAULT).toArray();
        Object[] profileRuleValues = (Object[])
            config.getList(PortalConfigurationConstants.PROFILER_RULE_VALUES_DEFAULT).toArray();
        defaultRules = new HashMap();
        if (profileRuleNames != null && profileRuleValues != null)
        {
            for (int ix = 0; ix < ((profileRuleNames.length < profileRuleValues.length) ? profileRuleNames.length : profileRuleValues.length); ix++)
            {
                defaultRules.put(profileRuleNames[ix], profileRuleValues[ix]);
            }
        }
        this.folderTemplate = 
            config.getString(PortalConfigurationConstants.PSML_TEMPLATE_FOLDER);
    }
    
    public void registerUser(String userName, String password)
    throws RegistrationException
    {
        registerUser(userName, password, (List)null, null, null, null, null);
    }
        
    /* (non-Javadoc)
     * @see org.apache.jetspeed.administration.PortalAdministration#registerUser(java.lang.String, java.lang.String, java.util.Map, java.awt.List, java.awt.List, java.lang.String)
     */    
    public void registerUser(
            String userName, 
            String password, 
            List roles, 
            List groups, 
            Map userInfo, 
            Map rules, 
            String folderTemplate)
    throws RegistrationException    
    {
        try 
        {
            // create the user
            userManager.addUser(userName, password);
            User user = userManager.getUser(userName);
                        
            // assign roles to user
            if (roles == null)
            {
                roles = this.defaultRoles;
            }
            if (roles != null)
            {
                Iterator roleList = roles.iterator();
                while (roleList.hasNext())
                {
                    String role = (String)roleList.next();
                    if (!role.startsWith(Folder.ROLE_FOLDER))
                    {
                        role = Folder.ROLE_FOLDER + role;
                    }
                    roleManager.addRoleToUser(userName, role);
                }
            }
            
            // assign groups to user
            if (groups == null)
            {
                groups = this.defaultGroups;
            }
            if (groups != null)
            {
                Iterator groupsList = groups.iterator();
                while (groupsList.hasNext())
                {
                    String group = (String)groupsList.next();
                    if (!group.startsWith(Folder.GROUP_FOLDER))
                    {
                        group = Folder.GROUP_FOLDER + group;
                    }                    
                    groupManager.addUserToGroup(userName, group);
                }
            }
            
            // assign user attributes to user
            if (userInfo != null)
            {
                Iterator info = userInfo.entrySet().iterator();
                while (info.hasNext())
                {           
                    Map.Entry entry = (Map.Entry)info.next();
                    user.getUserAttributes().put((String)entry.getKey(), (String)entry.getValue());
                }
            }
            
            // assign profiling rules to user
            if (rules == null)
            {
                rules = this.defaultRules;
            }
            if (rules != null)
            {
                Iterator ruleEntries = rules.entrySet().iterator();
                while (ruleEntries.hasNext())
                {           
                    Map.Entry entry = (Map.Entry)ruleEntries.next();
                    ProfilingRule rule = profiler.getRule((String)entry.getKey());
                    if (rule != null)
                    {
                        Principal principal = SecurityHelper.getBestPrincipal(user.getSubject(), UserPrincipal.class);
                        profiler.setRuleForPrincipal(principal, rule, (String)entry.getValue());
                    }
                }
            }
            
            // create user's home folder                        
            // deep copy from the default folder template tree, creating a deep-copy of the template
            // in the new user's folder tree
            if (folderTemplate == null)
            {
                folderTemplate = this.folderTemplate; 
            }
            Folder source = pageManager.getFolder(folderTemplate);
            pageManager.deepCopyFolder(source, Folder.USER_FOLDER + userName);
                        
        } 
        catch (Exception e) 
        {
            throw new RegistrationException(e); 
        }        
    }
    
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.administration.PortalAdministration#generatePassword()
     */
    public String generatePassword()
    {
        // TODO: Find a replacement for Ostermiller's utility
        // http://ostermiller.org/utils/RandPass.html
        // almost screwed up and checked in a GPL licensed piece of code
        // had to remove
        return "secret";
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.administration.PortalAdministration#sendPassword(java.lang.String)
     */
    public void sendEmail(String emailAddress, 
                          String localizedSubject, 
                          String localizedTemplatePath,
                          Map userAttributes)                            
    throws AdministrationEmailException    
    {       
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(config.getString(PortalConfigurationConstants.EMAIL_SENDER));
        msg.setSubject(localizedSubject);
        msg.setTo(emailAddress);
        msg.setText(mergeEmailTemplate(userAttributes, "map", localizedTemplatePath));

        try
        {
            mailSender.send(msg);
        } 
        catch (MailException ex)
        {
            throw new AdministrationEmailException(
                    "Failed to send forgotten password email to user with email address "
                            ); //+ user.getEmail());
        }
    }
    
    public void sendEmail(String recipient,
            String localizedSubject,
            String message)
    throws AdministrationEmailException
    {
        
    }
    
    
    public String mergeEmailTemplate(Map attributes, String attributesName, String template)
    throws AdministrationEmailException
    {
        VelocityContext context = new VelocityContext();
        context.put(attributesName, attributes);
        
        StringWriter writer = new StringWriter();
        final InputStream templateResource = this.getClass()
                .getResourceAsStream(template);
        Reader templateReader = new InputStreamReader(templateResource);
        try
        {
            velocityEngine.evaluate(context, writer, "UserEmailProcessor",
                    templateReader);
        } catch (Exception e)
        {
            throw new AdministrationEmailException(
                    "Failed to generate email text for email template "
                            + template, e);
        }
        
        String buffer = writer.getBuffer().toString();
        
        return buffer;
    }
    
    private static final String USER_NOT_FOUND_FROM_EMAIL = "User not found for Email address: ";
    
    public User lookupUserFromEmail(String email)
        throws AdministrationEmailException    
    {
        Collection result = preferences.lookupPreference("userinfo", "user.email", email);
        if (result.size() == 0)
        {
            throw new AdministrationEmailException(USER_NOT_FOUND_FROM_EMAIL + email);
        }
        Iterator nodes = result.iterator();
        Node node = (Node)nodes.next();
        String nodePath = node.getFullPath();
        if (nodePath == null)
        {
            throw new AdministrationEmailException(USER_NOT_FOUND_FROM_EMAIL + email);
        }
        String[] paths = nodePath.split("/");
        if (paths == null || paths.length != 4)
        {
            throw new AdministrationEmailException(USER_NOT_FOUND_FROM_EMAIL + email);
        }
        String userName = paths[2];
        try
        {
            return userManager.getUser(userName);
        }
        catch (Exception e)
        {
            throw new AdministrationEmailException(USER_NOT_FOUND_FROM_EMAIL + email);
        }
    }

}
