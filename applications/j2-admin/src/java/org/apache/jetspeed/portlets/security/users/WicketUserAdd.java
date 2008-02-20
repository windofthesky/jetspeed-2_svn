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
package org.apache.jetspeed.portlets.security.users;

import java.io.Serializable;
import java.io.NotSerializableException;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.prefs.Preferences;
import java.security.Principal;

import javax.portlet.PortletContext;
import javax.portlet.PortletSession;
import javax.portlet.PortletRequest;

import org.apache.wicket.RequestContext;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.protocol.http.portlet.PortletRequestContext;

import org.apache.portals.messaging.PortletMessaging;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.audit.AuditActivity;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.document.NodeSet;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.profiler.rules.PrincipalRule;
import org.apache.jetspeed.profiler.rules.ProfilingRule;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.portlets.security.SecurityResources;
import org.apache.jetspeed.portlets.security.SecurityUtil;

import org.apache.jetspeed.portlets.wicket.component.LinkPropertyColumn;
import org.apache.jetspeed.portlets.wicket.component.PortletOddEvenItem;

/**
 * User Add Wicket WebPage
 * 
 * @author <a href="mailto:woonsan@apache.org">Woonsan Ko</a>
 * @version $Id: $
 */
public class WicketUserAdd extends WicketUserAdmin
{
    
    protected String userName;
    protected String password;
    protected boolean changePasswordRequiredOnFirstLogin;
    protected String role;
    protected String rule;
    protected String subsite;

	public WicketUserAdd()
	{
        Form userAddForm = new Form("userAddForm")
        {
            protected void onSubmit()
            {
                if (!SecurityUtil.isEmpty(getUserName()))
                {
                    try
                    {            
                        if (SecurityUtil.isEmpty(getPassword()))
                        {
                            throw new SecurityException(SecurityException.PASSWORD_REQUIRED);
                        }
                        
                        getUserManager().addUser(getUserName(), getPassword());
                        getAuditActivity().logAdminUserActivity(getPortletRequest().getUserPrincipal().getName(), getIPAddress(), getUserName(), AuditActivity.USER_CREATE, USER_ADMINISTRATION);            
                        
                        PortletMessaging.publish(getPortletRequest(), SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_REFRESH, "true");
                        PortletMessaging.publish(getPortletRequest(), SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_SELECTED, getUserName());
                                                        
                        User user = getUserManager().getUser(getUserName());
                        
                        PasswordCredential credential = getCredential(user);
                        
                        if ( credential != null )
                        {
                            if (getChangePasswordRequiredOnFirstLogin() != credential.isUpdateRequired())
                            {
                                getUserManager().setPasswordUpdateRequired(getUserName(), getChangePasswordRequiredOnFirstLogin());
                            }                    
                        }
                        
                        if (!SecurityUtil.isEmpty(getRole()) && user != null) 
                        {
                            getRoleManager().addRoleToUser(getUserName(), getRole());
                        }
                        
                        String templateFolder = getPortletRequest().getPreferences().getValue("newUserTemplateDirectory", "/_user/template/");

                        String subsite = getSubsite();
                        
                        if (SecurityUtil.isEmpty(subsite))
                        {
                            subsite = Folder.USER_FOLDER + getUserName();
                        }
                        else
                        {
                            subsite  = subsite + Folder.USER_FOLDER + getUserName();
                            Preferences attributes = user.getUserAttributes();
                            attributes.put(User.USER_INFO_SUBSITE, subsite);                    
                        }
                        
                        // copy the entire dir tree from the template folder
                        if (!(templateFolder == null || templateFolder.trim().length() == 0))
                        {
                            Folder source = getPageManager().getFolder(templateFolder);                
                            getPageManager().deepCopyFolder(source, subsite, getUserName());
                        }
                        // TODO: send message that site tree portlet invalidated
                        
                        if (!SecurityUtil.isEmpty(getRule()) && user != null) 
                        {
                            Principal principal = SecurityUtil.getPrincipal(user.getSubject(), UserPrincipal.class);                         
                            getProfiler().setRuleForPrincipal(principal, getProfiler().getRule(getRule()), "page");
                        }
                        
                        setResponsePage(WicketUserDetails.class);
                    }
                    catch (SecurityException sex)
                    {
                        SecurityUtil.publishErrorMessage(getPortletRequest(), SecurityResources.TOPIC_USER, sex.getMessage());
                    }
                    catch (Exception ex)
                    {
                        SecurityUtil.publishErrorMessage(getPortletRequest(), SecurityResources.TOPIC_USER, ex.getMessage());
                    }
                }
            }
        };

        TextField userNameField = new TextField("jetspeedUserName", new PropertyModel(this, "userName"));
        userAddForm.add(userNameField);
        
        PasswordTextField passwordField = new PasswordTextField("jetspeedPassword", new PropertyModel(this, "password"));
        userAddForm.add(passwordField);
        
        CheckBox changePasswordRequiredOnFirstLoginField = new CheckBox("jetspeedChangePasswordRequiredOnFirstLogin", new PropertyModel(this, "changePasswordRequiredOnFirstLogin"));
        userAddForm.add(changePasswordRequiredOnFirstLoginField);
        
        List roles = new LinkedList();
        
        try
        {
            for (Iterator it = getRoleManager().getRoles(""); it.hasNext(); )
            {
                Role role = (Role) it.next();
                roles.add(role.getPrincipal().getName());
            }
        }
        catch (SecurityException e)
        {
            SecurityUtil.publishErrorMessage(getPortletRequest(), SecurityResources.TOPIC_USERS, e.getMessage());
        }                                    
        
        DropDownChoice roleField = new DropDownChoice("jetspeedRole", new PropertyModel(this, "role"), roles);
        userAddForm.add(roleField);
        
        List rules = new LinkedList();
        
        for (Iterator it = getProfiler().getRules().iterator(); it.hasNext(); )
        {
            ProfilingRule rule = (ProfilingRule) it.next();
            rules.add(rule.getId());
        }
        
        DropDownChoice ruleField = new DropDownChoice("jetspeedRule", new PropertyModel(this, "rule"), rules);
        userAddForm.add(ruleField);
        
        List subsites = new ArrayList();
        
        SubsiteInfo emptyone = new SubsiteInfo("","");
        subsites.add(emptyone.getPath());
        
        String subsiteRoot = getPortletRequest().getPreferences().getValue("subsiteRootFolder", "");
        
        if (!subsiteRoot.equals(""))
        {
            try
            {
                Folder subsiteFolder = getPageManager().getFolder(subsiteRoot);
                NodeSet set = getPageManager().getFolders(subsiteFolder);
                
                if (set != null && !set.isEmpty())
                {
                    for (Iterator setIterator = set.iterator(); setIterator.hasNext(); )
                    {
                        Folder f = (Folder) setIterator.next();
                        SubsiteInfo subsiteInfo = new SubsiteInfo(f.getPath(), f.getTitle());
                        subsites.add(subsiteInfo.getPath());
                    }
                }
            }
            catch (FolderNotFoundException fnfe)
            {
                // subsites not used, ignore
            }
            catch (Exception e)
            {
                
            }
        }
        subsites.add("Localhost");
        
        DropDownChoice subsiteField = new DropDownChoice("jetspeedSubsite", new PropertyModel(this, "subsite"), subsites);
        userAddForm.add(subsiteField);
        
        add(userAddForm);
	}
    
    public void setUserName(String userName)
    {
        this.userName = userName;
    }
    
    public String getUserName()
    {
        return this.userName;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public String getPassword()
    {
        return this.password;
    }
    
    public void setChangePasswordRequiredOnFirstLogin(boolean changePasswordRequiredOnFirstLogin)
    {
        this.changePasswordRequiredOnFirstLogin = changePasswordRequiredOnFirstLogin;
    }
    
    public boolean getChangePasswordRequiredOnFirstLogin()
    {
        return this.changePasswordRequiredOnFirstLogin;
    }
    
    public void setRole(String role)
    {
        this.role = role;
    }
    
    public String getRole()
    {
        return this.role;
    }
    
    public void setRule(String rule)
    {
        this.rule = rule;
    }
    
    public String getRule()
    {
        return this.rule;
    }
    
    public void setSubsite(String subsite)
    {
        this.subsite = subsite;
    }
    
    public String getSubsite()
    {
        return this.subsite;
    }

}