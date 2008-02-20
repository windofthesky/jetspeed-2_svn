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
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Date;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.security.Principal;
import java.util.prefs.Preferences;
import java.util.prefs.BackingStoreException;

import javax.portlet.PortletContext;
import javax.portlet.PortletSession;
import javax.portlet.PortletRequest;
import javax.security.auth.Subject;

import org.apache.wicket.RequestContext;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.OddEvenItem;
import org.apache.wicket.protocol.http.portlet.PortletRequestContext;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;

import org.apache.portals.messaging.PortletMessaging;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.administration.PortalConfiguration;
import org.apache.jetspeed.administration.PortalConfigurationConstants;
import org.apache.jetspeed.audit.AuditActivity;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.container.JetspeedPortletContext;
import org.apache.jetspeed.om.common.UserAttribute;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.document.NodeSet;
import org.apache.jetspeed.portlets.security.SecurityResources;
import org.apache.jetspeed.portlets.security.SecurityUtil;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.profiler.rules.PrincipalRule;
import org.apache.jetspeed.profiler.rules.ProfilingRule;
import org.apache.jetspeed.security.Group;
import org.apache.jetspeed.security.GroupManager;
import org.apache.jetspeed.security.InvalidNewPasswordException;
import org.apache.jetspeed.security.InvalidPasswordException;
import org.apache.jetspeed.security.PasswordAlreadyUsedException;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.om.InternalCredential;

import org.apache.jetspeed.portlets.wicket.component.LinkPropertyColumn;
import org.apache.jetspeed.portlets.wicket.component.PortletOddEvenItem;
import org.apache.jetspeed.portlets.wicket.component.SelectionImagePropertyColumn;
import org.apache.jetspeed.portlets.wicket.component.CheckBoxPropertyColumn;
import org.apache.jetspeed.portlets.wicket.component.TextFieldPropertyColumn;

import org.apache.jetspeed.portlets.wicket.AdminWicketPortlet;

/**
 * User Details Wicket WebPage
 * 
 * @author <a href="mailto:woonsan@apache.org">Woonsan Ko</a>
 * @version $Id: $
 */
public class WicketUserDetails extends WicketUserAdmin
{

    protected transient String userName;
    protected transient Principal userPrincipal;
    protected transient User user;
    protected transient PasswordCredential credential;
    protected List tabs;
    protected List anonTabs;
    
	public WicketUserDetails()
	{
        refreshData();
        
        Label userNameLabel = new Label("userName", new PropertyModel(this, "userName"));
        add(userNameLabel);
        
        this.tabs = new ArrayList();
        this.anonTabs = new ArrayList();
        
        ITab tab = 
            new AbstractTab(new ResourceModel("pam.details.tabs.user_attributes")) 
            {
                public Panel getPanel(String panelId)
                {
                    return new UserAttributesPanel(panelId);
                }
            };
        tabs.add(tab);

        tab =
            new AbstractTab(new ResourceModel("pam.details.tabs.user_credential")) 
            {
                public Panel getPanel(String panelId)
                {
                    return new UserCredentialPanel(panelId);
                }
            };
        tabs.add(tab);

        tab =
            new AbstractTab(new ResourceModel("pam.details.tabs.user_role")) 
            {
                public Panel getPanel(String panelId)
                {
                    return new UserRolesPanel(panelId);
                }
            };
        tabs.add(tab);
        anonTabs.add(tab);

        tab =
            new AbstractTab(new ResourceModel("pam.details.tabs.user_group")) 
            {
                public Panel getPanel(String panelId)
                {
                    return new UserGroupsPanel(panelId);
                }
            };
        tabs.add(tab);
        anonTabs.add(tab);

        tab =
            new AbstractTab(new ResourceModel("pam.details.tabs.user_profile")) 
            {
                public Panel getPanel(String panelId)
                {
                    return new UserProfilesPanel(panelId);
                }
            };
        tabs.add(tab);
        anonTabs.add(tab);

        add(new TabbedPanel("tabs", tabs));
        
        Form userActionForm = new Form("userActionForm");
        
        Button addNewUserButton = new Button("addNewUser")
        {
            public void onSubmit()
            {
                PortletMessaging.cancel(getPortletRequest(), SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_SELECTED);
                setResponsePage(WicketUserAdd.class);
            }
        };
        
        Button removeUserButton = new Button("removeUser")
        {
            public void onSubmit()
            {
                try
                {
                    Preferences attributes = user.getUserAttributes();
                    
                    String firstName = attributes.get("user.name.given", "n/a");
                    String lastName =  attributes.get("user.name.family", "n/a");
                    String subsite = attributes.get(User.USER_INFO_SUBSITE, null);
                    
                    getUserManager().removeUser(getUserName());
                    
                    PortletMessaging.publish(getPortletRequest(), SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_REFRESH, "true");
                    
                    if (subsite == null)
                    {
                        subsite = Folder.USER_FOLDER + getUserName();
                    }
                    
                    if (getPageManager().folderExists(subsite))
                    {
                        Folder folder = getPageManager().getFolder(subsite);                    
                        getPageManager().removeFolder(folder);
                    }
                    
                    getAuditActivity().logAdminAttributeActivity(getPortletRequest().getUserPrincipal().getName(), 
                            getIPAddress(), getUserName(), AuditActivity.USER_DELETE, "", firstName, lastName, USER_ADMINISTRATION);                                                                                                        
                    // remove selected user from USERS_TOPIC
                    PortletMessaging.cancel(getPortletRequest(), SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_SELECTED);
                    setResponsePage(WicketUserAdd.class);
                    
                    // TODO: send message to site manager portlet                
                }
                catch (Exception ex)
                {
                    SecurityUtil.publishErrorMessage(getPortletRequest(), SecurityResources.TOPIC_USER, ex.getMessage());
                }
            }
        };
        
        userActionForm.add(addNewUserButton);
        userActionForm.add(removeUserButton);
        
        add(userActionForm);
	}
    
    protected String getUserName()
    {
        return this.userName;
    }
    
    protected void refreshData()
    {
        this.userName = (String) PortletMessaging.receive(getPortletRequest(), SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_SELECTED);
        this.user = null;
        this.userPrincipal = null;
        this.credential = null;
        
        if (this.userName != null)
        {
            try
            {
                this.user = getUserManager().getUser(this.userName);
                
                if (this.user != null)
                {
                    this.userPrincipal = createPrincipal(this.user.getSubject(), UserPrincipal.class);
                    this.credential = getCredential(this.user);
                }
            }
            catch (SecurityException e)
            {
                SecurityUtil.publishErrorMessage(getPortletRequest(), SecurityResources.TOPIC_USERS, e.getMessage());
            }
        }
    }
    
    protected void onBeforeRender()
    {
        super.onBeforeRender();
        
        refreshData();
        
        boolean isAnonymousUser = getUserManager().getAnonymousUser().equals(getUserName());
        
        TabbedPanel current = (TabbedPanel) get("tabs");
        remove(current);
        
        TabbedPanel expected = new TabbedPanel("tabs", isAnonymousUser ? this.anonTabs : this.tabs);
        
        if (current.getTabs() == expected.getTabs())
        {
            expected.setSelectedTab(current.getSelectedTab());
        }
        
        add(expected);
    }
    
    protected Principal createPrincipal(Subject subject, Class classe)
    {
        Principal principal = null;
        Iterator principals = subject.getPrincipals().iterator();
        while (principals.hasNext())
        {
            Principal p = (Principal) principals.next();
            if (classe.isInstance(p))
            {
                principal = p;
                break;
            }
        }
        return principal;
    }
    
    protected class UserAttributesPanel extends Panel
    {
        protected String userName;
        protected String userAttrName;
        protected String userAttrValue;
        protected List userAttributes;
        
        protected UserAttributesPanel(String id)
        {
            super(id);
            
            if (getUserName() != this.userName)
            {
                refreshData();
                this.userName = getUserName();
            }
            
            Form userAttrsForm = new Form("userAttrsForm");

            SortableDataProvider dataProvider = new SortableDataProvider()
            {
                public int size()
                {
                    return getUserAttributes().size();
                }
                
                public IModel model(Object object)
                {
                    return new Model((Serializable) object);
                }
                
                public Iterator iterator(int first, int count)
                {
                    return getUserAttributes().subList(first, first + count).iterator();
                }
            };
            
            IColumn [] columns = 
            {
                new CheckBoxPropertyColumn(new Model(" "), "checked"),
                new PropertyColumn(new ResourceModel("security.name"), "name"),
                new TextFieldPropertyColumn(new ResourceModel("security.value"), "value")
            };
            
            DataTable userAttrsDataTable = new DataTable("entries", columns, dataProvider, 10)
            {
                protected Item newRowItem(String id, int index, IModel model)
                {
                    return new PortletOddEvenItem(id, index, model);
                }
            };
            
            userAttrsDataTable.addTopToolbar(new HeadersToolbar(userAttrsDataTable, dataProvider));
            userAttrsDataTable.addBottomToolbar(new NavigationToolbar(userAttrsDataTable));
            
            userAttrsForm.add(userAttrsDataTable);

            Button updateAttrButton = new Button("updateAttr")
            {
                public void onSubmit()
                {
                    Preferences prefs = user.getUserAttributes();
                    
                    for (Iterator it = userAttributes.iterator(); it.hasNext(); )
                    {
                        Map userAttrMap = (Map) it.next();
                        String userAttrName = (String) userAttrMap.get("name");
                        String userAttrValue = (String) userAttrMap.get("value");
                        String oldUserAttrValue = user.getUserAttributes().get(userAttrName, "");
                        prefs.put(userAttrName, userAttrValue);
                        
                        getAuditActivity().logAdminAttributeActivity(getPortletRequest().getUserPrincipal().getName(), getIPAddress(), getUserName(), AuditActivity.USER_UPDATE_ATTRIBUTE, userAttrName, oldUserAttrValue, userAttrValue, USER_ADMINISTRATION);
                    }
                    
                    refreshData();
                }
            };
            
            Button removeAttrButton = new Button("removeAttr")
            {
                public void onSubmit()
                {
                    Preferences prefs = user.getUserAttributes();
                    
                    for (Iterator it = userAttributes.iterator(); it.hasNext(); )
                    {
                        Map userAttrMap = (Map) it.next();
                        
                        if (Boolean.TRUE.equals(userAttrMap.get("checked")))
                        {
                            String userAttrName = (String) userAttrMap.get("name");
                            String userAttrValue = (String) userAttrMap.get("value");
                            prefs.remove(userAttrName);
                            
                            getAuditActivity().logAdminAttributeActivity(getPortletRequest().getUserPrincipal().getName(), getIPAddress(), getUserName(), AuditActivity.USER_DELETE_ATTRIBUTE, userAttrName, userAttrValue, "", USER_ADMINISTRATION);
                        }
                    }
                    
                    refreshData();
                }
            };
            
            userAttrsForm.add(updateAttrButton);
            userAttrsForm.add(removeAttrButton);
            
            add(userAttrsForm);
            
            Form addAttrForm = new Form("addAttrForm")
            {
                protected void onSubmit()
                {
                    String userAttrName = getUserAttrName();
                    String userAttrValue = getUserAttrValue();
                    
                    if (userAttrName != null && userAttrName.trim().length() > 0)
                    {
                        Preferences prefs = user.getUserAttributes();
                        prefs.put(userAttrName, userAttrValue);
                        getAuditActivity().logAdminAttributeActivity(getPortletRequest().getUserPrincipal().getName(), getIPAddress(), getUserName(), AuditActivity.USER_ADD_ATTRIBUTE, userAttrName, "", userAttrValue, USER_ADMINISTRATION);                                                
                    }
                    
                    refreshData();
                }
            };

            TextField userAttrNameField = new TextField("userAttrName", new PropertyModel(this, "userAttrName"));
            addAttrForm.add(userAttrNameField);
            
            TextField userAttrValueField = new TextField("userAttrValue", new PropertyModel(this, "userAttrValue"));
            addAttrForm.add(userAttrValueField);
            
            add(addAttrForm);
        }
        
        public List getUserAttributes()
        {
            return this.userAttributes;
        }
        
        public void setUserAttrName(String userAttrName)
        {
            this.userAttrName = userAttrName;
        }
        
        public String getUserAttrName()
        {
            return this.userAttrName;
        }
        
        public void setUserAttrValue(String userAttrValue)
        {
            this.userAttrValue = userAttrValue;
        }
        
        public String getUserAttrValue()
        {
            return this.userAttrValue;
        }
        
        protected void onBeforeRender()
        {
            super.onBeforeRender();
            
            if (getUserName() != this.userName)
            {
                refreshData();
                this.userName = getUserName();
            }
        }
        
        protected void refreshData()
        {
            this.userAttributes = new LinkedList();
            
            if (user != null)
            {
                try
                {
                    Preferences prefs = user.getUserAttributes();
                    String [] keys = prefs.keys();
                    
                    for (int i = 0; i < keys.length; i++)
                    {
                        Map item = new HashMap();
                        item.put("checked", Boolean.FALSE);
                        item.put("name", keys[i]);
                        item.put("value", prefs.get(keys[i], ""));
                        
                        this.userAttributes.add(item);
                    }
                }
                catch (BackingStoreException e)
                {
                }
            }
        }
    }

    protected class UserCredentialPanel extends Panel
    {
        protected String userName;
        protected String credentialValue;
        protected boolean credentialUpdateRequired;
        protected Date lastAuthenticationDate;
        protected boolean credentialEnabled;
        protected Date credentialExpirationDate;
        protected String userExpiredFlag;
        
        protected UserCredentialPanel(String id)
        {
            super(id);
            
            if (getUserName() != this.userName)
            {
                refreshData();
                this.userName = getUserName();
            }
            
            Form form = new Form("userCredentialForm")
            {
                protected void onSubmit()
                {
                    ResourceBundle bundle = ResourceBundle.getBundle("org.apache.jetspeed.portlets.security.resources.UsersResources",getPortletRequest().getLocale());
                    
                    try
                    {
                        boolean passwordSet = false;
                        
                        if ( getCredentialValue() != null && getCredentialValue().trim().length() > 0 )
                        {
                            getUserManager().setPassword(getUserName(), null, getCredentialValue());
                            getAuditActivity().logAdminCredentialActivity(getPortletRequest().getUserPrincipal().getName(), getIPAddress(), getUserName(), AuditActivity.PASSWORD_RESET, USER_ADMINISTRATION);
                            passwordSet = true;
                        }
                        
                        if (getCredentialUpdateRequired() != credential.isUpdateRequired())
                        {
                            getUserManager().setPasswordUpdateRequired(getUserName(), getCredentialUpdateRequired());
                            getAuditActivity().logAdminCredentialActivity(getPortletRequest().getUserPrincipal().getName(), getIPAddress(), getUserName(), AuditActivity.PASSWORD_UPDATE_REQUIRED, USER_ADMINISTRATION);
                        }
                        
                        if (getCredentialEnabled() != credential.isEnabled())
                        {
                            getUserManager().setPasswordEnabled(getUserName(), getCredentialEnabled());
                            String activity = (getCredentialEnabled() ? AuditActivity.PASSWORD_ENABLED : AuditActivity.PASSWORD_DISABLED);
                            getAuditActivity().logAdminCredentialActivity(getPortletRequest().getUserPrincipal().getName(), getIPAddress(), getUserName(), activity, USER_ADMINISTRATION);                                                                                                                                              
                        }
                        
                        String expiredFlagStr = getUserExpiredFlag();
                        
                        if (expiredFlagStr != null)
                        {
                            if (!passwordSet && expiredFlagStr.equals("expired"))
                            {
                                java.sql.Date today = new java.sql.Date(new Date().getTime());
                                getUserManager().setPasswordExpiration(getUserName(), today);                            
                                getAuditActivity().logAdminCredentialActivity(getPortletRequest().getUserPrincipal().getName(), getIPAddress(), getUserName(), AuditActivity.PASSWORD_EXPIRE, USER_ADMINISTRATION);
                            }
                            else if (expiredFlagStr.equals("extend"))
                            {
                                getUserManager().setPasswordExpiration(getUserName(), null);
                                getAuditActivity().logAdminCredentialActivity(getPortletRequest().getUserPrincipal().getName(), getIPAddress(), getUserName(), AuditActivity.PASSWORD_EXTEND, USER_ADMINISTRATION);
                            }
                            else if (expiredFlagStr.equals("unlimited"))
                            {
                                getUserManager().setPasswordExpiration(getUserName(), InternalCredential.MAX_DATE);
                                getAuditActivity().logAdminCredentialActivity(getPortletRequest().getUserPrincipal().getName(), getIPAddress(), getUserName(), AuditActivity.PASSWORD_UNLIMITED, USER_ADMINISTRATION);
                            }
                        }
                    }
                    catch ( InvalidPasswordException ipe )
                    {
                        SecurityUtil.publishErrorMessage(getPortletRequest(), SecurityResources.TOPIC_USER, bundle.getString("chgpwd.error.invalidPassword"));
                    }
                    catch ( InvalidNewPasswordException inpe )
                    {
                        SecurityUtil.publishErrorMessage(getPortletRequest(), SecurityResources.TOPIC_USER, bundle.getString("chgpwd.error.invalidNewPassword"));
                    }
                    catch ( PasswordAlreadyUsedException paue )
                    {
                        SecurityUtil.publishErrorMessage(getPortletRequest(), SecurityResources.TOPIC_USER, bundle.getString("chgpwd.error.passwordAlreadyUsed"));
                    }
                    catch (SecurityException e)
                    {
                        SecurityUtil.publishErrorMessage(getPortletRequest(), SecurityResources.TOPIC_USER, e.getMessage());
                    }
                    
                    refreshData();
                }
            };
            
            PasswordTextField credentialValueField = new PasswordTextField("credentialValue", new PropertyModel(this, "credentialValue"));
            form.add(credentialValueField);
            
            CheckBox credentialUpdateRequiredField = new CheckBox("credentialUpdateRequired", new PropertyModel(this, "credentialUpdateRequired"));
            form.add(credentialUpdateRequiredField);
            
            Label lastAuthenticationDateLabel = new Label("lastAuthenticationDate", new PropertyModel(this, "lastAuthenticationDate"));
            form.add(lastAuthenticationDateLabel);
            
            CheckBox credentialEnabledField = new CheckBox("credentialEnabled", new PropertyModel(this, "credentialEnabled"));
            form.add(credentialEnabledField);
            
            Label credentialExpirationDateLabel = new Label("credentialExpirationDate", new PropertyModel(this, "credentialExpirationDate"));
            form.add(credentialExpirationDateLabel);
            
            List expiredFlagChoices = new ArrayList();
            expiredFlagChoices.add("active");
            expiredFlagChoices.add("expired");
            expiredFlagChoices.add("extend");
            expiredFlagChoices.add("unlimited");
            RadioChoice userExpiredFlagField = new RadioChoice("userExpiredFlag", new PropertyModel(this, "userExpiredFlag"), expiredFlagChoices);
            form.add(userExpiredFlagField);
            
            add(form);
        }
        
        public void setCredentialValue(String credentialValue)
        {
            this.credentialValue = credentialValue;
        }
        
        public String getCredentialValue()
        {
            return this.credentialValue;
        }
        
        public void setCredentialUpdateRequired(boolean credentialUpdateRequired)
        {
            this.credentialUpdateRequired = credentialUpdateRequired;
        }
        
        public boolean getCredentialUpdateRequired()
        {
            return this.credentialUpdateRequired;
        }
        
        public void setLastAuthenticationDate(Date lastAuthenticationDate)
        {
            this.lastAuthenticationDate = lastAuthenticationDate;
        }
        
        public Date getLastAuthenticationDate()
        {
            return this.lastAuthenticationDate;
        }
        
        public void setCredentialEnabled(boolean credentialEnabled)
        {
            this.credentialEnabled = credentialEnabled;
        }
        
        public boolean getCredentialEnabled()
        {
            return this.credentialEnabled;
        }
        
        public void setCredentialExpirationDate(Date credentialExpirationDate)
        {
            this.credentialExpirationDate = credentialExpirationDate;
        }
        
        public Date getCredentialExpirationDate()
        {
            return this.credentialExpirationDate;
        }
        
        public void setUserExpiredFlag(String userExpiredFlag)
        {
            this.userExpiredFlag= userExpiredFlag;
        }
        
        public String getUserExpiredFlag()
        {
            return this.userExpiredFlag;
        }
        
        protected void onBeforeRender()
        {
            super.onBeforeRender();
            
            if (getUserName() != this.userName)
            {
                refreshData();
                this.userName = getUserName();
            }
        }
        
        protected void refreshData()
        {
            if (credential != null)
            {
                setCredentialUpdateRequired(credential.isUpdateRequired());
                setCredentialEnabled(credential.isEnabled());
                setLastAuthenticationDate(credential.getLastAuthenticationDate());
                setCredentialExpirationDate(credential.getExpirationDate());
                setUserExpiredFlag(credential.isExpired() ? "expired" : "active");            
            }
        }
    }

    protected class UserRolesPanel extends Panel
    {
        protected String userName;
        protected String roleName;
        protected List roleNames;
        protected List fullRoleNames;
        
        protected UserRolesPanel(String id)
        {
            super(id);
            
            if (getUserName() != this.userName)
            {
                refreshData();
                this.userName = getUserName();
            }

            Form userRolesForm = new Form("userRolesForm")
            {
                protected void onSubmit()
                {
                    for (Iterator it = getRoleNames().iterator(); it.hasNext(); )
                    {
                        Map roleMap = (Map) it.next();
                        String roleName = (String) roleMap.get("name");
                        
                        if (Boolean.TRUE.equals(roleMap.get("checked")))
                        {
                            try
                            {
                                if (getRoleManager().roleExists(roleName))
                                {
                                    getRoleManager().removeRoleFromUser(getUserName(), roleName);
                                    getAuditActivity().logAdminAuthorizationActivity(getPortletRequest().getUserPrincipal().getName(), getIPAddress(), getUserName(), AuditActivity.USER_DELETE_ROLE, roleName, USER_ADMINISTRATION);                                                                                                    
                                }
                            }
                            catch (SecurityException e)
                            {
                                SecurityUtil.publishErrorMessage(getPortletRequest(), SecurityResources.TOPIC_USER, e.getMessage());
                            }                
                        }
                    }
                    
                    refreshData();
                }
            };

            SortableDataProvider dataProvider = new SortableDataProvider()
            {
                public int size()
                {
                    return getRoleNames().size();
                }
                
                public IModel model(Object object)
                {
                    Map roleMap = (Map) object;
                    return new Model((Serializable) roleMap);
                }
                
                public Iterator iterator(int first, int count)
                {
                    return getRoleNames().subList(first, first + count).iterator();
                }
            };
            
            IColumn [] columns = 
            {
                new CheckBoxPropertyColumn(new Model(" "), "checked"),
                new PropertyColumn(new ResourceModel("security.rolename"), "name")
            };
            
            DataTable userRolesDataTable = new DataTable("entries", columns, dataProvider, 10)
            {
                protected Item newRowItem(String id, int index, IModel model)
                {
                    return new PortletOddEvenItem(id, index, model);
                }
            };
            
            userRolesDataTable.addTopToolbar(new HeadersToolbar(userRolesDataTable, dataProvider));
            userRolesDataTable.addBottomToolbar(new NavigationToolbar(userRolesDataTable));
            
            userRolesForm.add(userRolesDataTable);
            
            add(userRolesForm);
            
            Form addRoleForm = new Form("addRoleForm")
            {
                protected void onSubmit()
                {
                    String roleName = getRoleName();
                    
                    if (roleName != null && roleName.trim().length() > 0)
                    {
                        try
                        {
                            getRoleManager().addRoleToUser(getUserName(), roleName);
                            getAuditActivity().logAdminAuthorizationActivity(getPortletRequest().getUserPrincipal().getName(), getIPAddress(), getUserName(), AuditActivity.USER_ADD_ROLE, roleName, USER_ADMINISTRATION);
                        }
                        catch (SecurityException e)
                        {
                            SecurityUtil.publishErrorMessage(getPortletRequest(), SecurityResources.TOPIC_USER, e.getMessage());
                        }
                        
                        refreshData();
                    }
                }
            };
            
            DropDownChoice roleNameField = new DropDownChoice("roleName", new PropertyModel(this, "roleName"), getFullRoleNames());
            addRoleForm.add(roleNameField);
            
            add(addRoleForm);
        }
        
        public void setRoleName(String roleName)
        {
            this.roleName = roleName;
        }
        
        public String getRoleName()
        {
            return this.roleName;
        }

        public List getRoleNames()
        {
            return this.roleNames;
        }
        
        public List getFullRoleNames()
        {
            return this.fullRoleNames;
        }
        
        protected void onBeforeRender()
        {
            super.onBeforeRender();
            
            if (getUserName() != this.userName)
            {
                refreshData();
                this.userName = getUserName();
            }
        }
        
        protected void refreshData()
        {
            this.fullRoleNames = new LinkedList();
            this.roleNames = new LinkedList();
            
            try
            {
                for (Iterator it = getRoleManager().getRoles(""); it.hasNext(); )
                {
                    Role role = (Role) it.next();
                    this.fullRoleNames.add(role.getPrincipal().getName());
                }
            
                if (getUserName() != null)
                {
                    Collection rolesForUser = getRoleManager().getRolesForUser(getUserName());
                    
                    for (Iterator it = rolesForUser.iterator(); it.hasNext(); )
                    {
                        Role role = (Role) it.next();
                        Map roleMap = new HashMap();
                        roleMap.put("name", role.getPrincipal().getName());
                        roleMap.put("checked", Boolean.FALSE);
                        this.roleNames.add(roleMap);
                    }
                }
            }
            catch (SecurityException e)
            {
                SecurityUtil.publishErrorMessage(getPortletRequest(), SecurityResources.TOPIC_USERS, e.getMessage());
            }                                    
        }
    }

    protected class UserGroupsPanel extends Panel
    {
        protected String userName;
        protected String groupName;
        protected List groupNames;
        protected List fullGroupNames;
        
        protected UserGroupsPanel(String id)
        {
            super(id);
            
            if (getUserName() != this.userName)
            {
                refreshData();
                this.userName = getUserName();
            }

            Form userGroupsForm = new Form("userGroupsForm")
            {
                protected void onSubmit()
                {
                    for (Iterator it = getGroupNames().iterator(); it.hasNext(); )
                    {
                        Map groupMap = (Map) it.next();
                        String groupName = (String) groupMap.get("name");
                        
                        if (Boolean.TRUE.equals(groupMap.get("checked")))
                        {
                            try
                            {
                                if (getGroupManager().groupExists(groupName))
                                {
                                    getGroupManager().removeUserFromGroup(getUserName(), groupName);
                                    getAuditActivity().logAdminAuthorizationActivity(getPortletRequest().getUserPrincipal().getName(), getIPAddress(), getUserName(), AuditActivity.USER_DELETE_GROUP, groupName, USER_ADMINISTRATION);
                                }
                            }
                            catch (SecurityException e)
                            {
                                SecurityUtil.publishErrorMessage(getPortletRequest(), SecurityResources.TOPIC_USER, e.getMessage());
                            }                
                        }
                    }
                    
                    refreshData();
                }
            };

            SortableDataProvider dataProvider = new SortableDataProvider()
            {
                public int size()
                {
                    return getGroupNames().size();
                }
                
                public IModel model(Object object)
                {
                    Map groupMap = (Map) object;
                    return new Model((Serializable) groupMap);
                }
                
                public Iterator iterator(int first, int count)
                {
                    return getGroupNames().subList(first, first + count).iterator();
                }
            };
            
            IColumn [] columns = 
            {
                new CheckBoxPropertyColumn(new Model(" "), "checked"),
                new PropertyColumn(new ResourceModel("security.groupname"), "name")
            };
            
            DataTable userGroupsDataTable = new DataTable("entries", columns, dataProvider, 10)
            {
                protected Item newRowItem(String id, int index, IModel model)
                {
                    return new PortletOddEvenItem(id, index, model);
                }
            };
            
            userGroupsDataTable.addTopToolbar(new HeadersToolbar(userGroupsDataTable, dataProvider));
            userGroupsDataTable.addBottomToolbar(new NavigationToolbar(userGroupsDataTable));
            
            userGroupsForm.add(userGroupsDataTable);
            
            add(userGroupsForm);
            
            Form addGroupForm = new Form("addGroupForm")
            {
                protected void onSubmit()
                {
                    String groupName = getGroupName();
                    
                    if (groupName != null && groupName.trim().length() > 0)
                    {
                        try
                        {
                            getGroupManager().addUserToGroup(getUserName(), groupName);
                            getAuditActivity().logAdminAuthorizationActivity(getPortletRequest().getUserPrincipal().getName(), getIPAddress(), getUserName(), AuditActivity.USER_ADD_GROUP, groupName, USER_ADMINISTRATION);
                        }
                        catch (SecurityException e)
                        {
                            SecurityUtil.publishErrorMessage(getPortletRequest(), SecurityResources.TOPIC_USER, e.getMessage());
                        }
                        
                        refreshData();
                    }
                }
            };
            
            DropDownChoice groupNameField = new DropDownChoice("groupName", new PropertyModel(this, "groupName"), getFullGroupNames());
            addGroupForm.add(groupNameField);
            
            add(addGroupForm);
        }
        
        public void setGroupName(String groupName)
        {
            this.groupName = groupName;
        }
        
        public String getGroupName()
        {
            return this.groupName;
        }
        
        public List getGroupNames()
        {
            return this.groupNames;
        }
        
        public List getFullGroupNames()
        {
            return this.fullGroupNames;
        }
        
        protected void onBeforeRender()
        {
            super.onBeforeRender();
            
            if (getUserName() != this.userName)
            {
                refreshData();
                this.userName = getUserName();
            }
        }
        
        protected void refreshData()
        {
            this.fullGroupNames = new LinkedList();
            this.groupNames = new LinkedList();
            
            try
            {
                for (Iterator it = getGroupManager().getGroups(""); it.hasNext(); )
                {
                    Group group = (Group) it.next();
                    this.fullGroupNames.add(group.getPrincipal().getName());
                }
            
                if (getUserName() != null)
                {
                    Collection groupsForUser = getGroupManager().getGroupsForUser(getUserName());
                    
                    for (Iterator it = groupsForUser.iterator(); it.hasNext(); )
                    {
                        Group group = (Group) it.next();
                        Map groupMap = new HashMap();
                        groupMap.put("name", group.getPrincipal().getName());
                        groupMap.put("checked", Boolean.FALSE);
                        this.groupNames.add(groupMap);
                    }
                }
            }
            catch (SecurityException e)
            {
                SecurityUtil.publishErrorMessage(getPortletRequest(), SecurityResources.TOPIC_USERS, e.getMessage());
            }                                    
        }
    }

    protected class UserProfilesPanel extends Panel
    {
        protected String userName;
        protected String locatorName;
        protected String ruleName;
        protected List fullRules;
        protected List userRules;
        
        protected UserProfilesPanel(String id)
        {
            super(id);
            
            if (getUserName() != this.userName)
            {
                refreshData();
                this.userName = getUserName();
            }
            
            Form userRulesForm = new Form("userRulesForm")
            {
                protected void onSubmit()
                {
                    try
                    {
                        Collection rules = getProfiler().getRulesForPrincipal(userPrincipal);
                        
                        for (Iterator it = getUserRules().iterator(); it.hasNext(); )
                        {
                            Map ruleMap = (Map) it.next();
                            
                            if (Boolean.TRUE.equals(ruleMap.get("checked")))
                            {
                                String locatorName = ((PrincipalRule) ruleMap.get("rule")).getLocatorName();
                                
                                for (Iterator ruleIter = rules.iterator(); ruleIter.hasNext(); )
                                {
                                    PrincipalRule rule = (PrincipalRule) ruleIter.next();
                                    
                                    if (rule.getLocatorName().equals(locatorName))
                                    {
                                        getProfiler().deletePrincipalRule(rule);
                                        getAuditActivity().logAdminAuthorizationActivity(getPortletRequest().getUserPrincipal().getName(), getIPAddress(), getUserName(), AuditActivity.USER_DELETE_PROFILE, rule.getProfilingRule().getId() + "-" + rule.getLocatorName(), USER_ADMINISTRATION);
                                    }
                                }
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        SecurityUtil.publishErrorMessage(getPortletRequest(), SecurityResources.TOPIC_USER, e.getMessage());
                    }
                    
                    refreshData();
                }
            };

            SortableDataProvider dataProvider = new SortableDataProvider()
            {
                public int size()
                {
                    return getUserRules().size();
                }
                
                public IModel model(Object object)
                {
                    Map ruleMap = (Map) object;
                    return new Model((Serializable) ruleMap);
                }
                
                public Iterator iterator(int first, int count)
                {
                    return getUserRules().subList(first, first + count).iterator();
                }
            };
            
            IColumn [] columns = 
            {
                new CheckBoxPropertyColumn(new Model(" "), "checked"),
                new PropertyColumn(new ResourceModel("security.name"), "rule.locatorName"),
                new PropertyColumn(new ResourceModel("security.value"), "rule.profilingRule")
            };
            
            DataTable userRulesDataTable = new DataTable("entries", columns, dataProvider, 10)
            {
                protected Item newRowItem(String id, int index, IModel model)
                {
                    return new PortletOddEvenItem(id, index, model);
                }
            };
            
            userRulesDataTable.addTopToolbar(new HeadersToolbar(userRulesDataTable, dataProvider));
            userRulesDataTable.addBottomToolbar(new NavigationToolbar(userRulesDataTable));
            
            userRulesForm.add(userRulesDataTable);
            
            add(userRulesForm);
            
            Form addRuleForm = new Form("addRuleForm")
            {
                protected void onSubmit()
                {
                    String locatorName = getLocatorName();
                    
                    if (locatorName != null && locatorName.trim().length() > 0)
                    {
                        try
                        {
                            String ruleName = getRuleName();
                            getProfiler().setRuleForPrincipal(userPrincipal, getProfiler().getRule(ruleName), locatorName);
                            getAuditActivity().logAdminAuthorizationActivity(getPortletRequest().getUserPrincipal().getName(), getIPAddress(), getUserName(), AuditActivity.USER_ADD_PROFILE, ruleName + "-" + locatorName, USER_ADMINISTRATION);  
                        }
                        catch (Exception e)
                        {
                            SecurityUtil.publishErrorMessage(getPortletRequest(), SecurityResources.TOPIC_USER, e.getMessage());
                        }
                        
                        refreshData();
                    }
                }
            };
            
            TextField locatorNameField = new TextField("locatorName", new PropertyModel(this, "locatorName"));
            addRuleForm.add(locatorNameField);

            DropDownChoice ruleNameField = new DropDownChoice("ruleName", new PropertyModel(this, "ruleName"), getFullRules());
            addRuleForm.add(ruleNameField);
            
            add(addRuleForm);
        }
        
        public void setLocatorName(String locatorName)
        {
            this.locatorName = locatorName;
        }
        
        public String getLocatorName()
        {
            return this.locatorName;
        }
        
        public void setRuleName(String ruleName)
        {
            this.ruleName = ruleName;
        }
        
        public String getRuleName()
        {
            return this.ruleName;
        }
        
        public List getFullRules()
        {
            return this.fullRules;
        }
        
        public List getUserRules()
        {
            return this.userRules;
        }
        
        protected void onBeforeRender()
        {
            super.onBeforeRender();
            
            if (getUserName() != this.userName)
            {
                refreshData();
                this.userName = getUserName();
            }
        }

        protected void refreshData()
        {
            this.fullRules = new ArrayList();
            this.userRules = new ArrayList();
            
            for (Iterator it = getProfiler().getRules().iterator(); it.hasNext(); )
            {
                ProfilingRule rule = (ProfilingRule) it.next();
                this.fullRules.add(rule);
            }
            
            if (userPrincipal != null)
            {
                for (Iterator it = getProfiler().getRulesForPrincipal(userPrincipal).iterator(); it.hasNext(); )
                {
                    PrincipalRule rule = (PrincipalRule) it.next();
                    Map ruleMap = new HashMap();
                    ruleMap.put("rule", rule);
                    ruleMap.put("checked", Boolean.FALSE);
                    this.userRules.add(ruleMap);
                }
            }
        }
    }

}