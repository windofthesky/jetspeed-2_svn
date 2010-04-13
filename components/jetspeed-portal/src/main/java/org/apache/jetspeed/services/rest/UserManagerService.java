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
package org.apache.jetspeed.services.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.administration.PortalConfigurationConstants;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.profiler.rules.ProfilingRule;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.Group;
import org.apache.jetspeed.security.GroupManager;
import org.apache.jetspeed.security.JetspeedPrincipalQueryContext;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserResultList;
import org.apache.jetspeed.services.beans.UserDataTableBean;
import org.apache.jetspeed.services.beans.UserDetailBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UserManagerService. This REST service provides access to the jetspeed user manager. The access of all methods are restricted to the users with the 'admin'
 * role.
 * 
 * @version $Id$
 */
@Path("/usermanager/")
public class UserManagerService
{
    
    private static Logger log = LoggerFactory.getLogger(UserManagerService.class);
    
    private UserManager userManager;
    private RoleManager roleManager;
    private GroupManager groupManager;
    private Profiler profiler;
    private PageManager pageManager;
    private PortletActionSecurityBehavior securityBehavior;

    public UserManagerService(UserManager userManager, RoleManager roleManager, GroupManager groupManager, Profiler profiler, PageManager pageManager,
                              PortletActionSecurityBehavior securityBehavior)
    {
        this.userManager = userManager;
        this.roleManager = roleManager;
        this.groupManager = groupManager;
        this.profiler = profiler;
        this.pageManager = pageManager;
        this.securityBehavior = securityBehavior;
    }

    /**
     * Find users according to query parameters.
     * 
     * @param servletRequest
     * @param uriInfo
     * @param userName
     * @param roles
     * @param groups
     * @param startIndex
     * @param results
     * @param sortDirection
     * @param attributeKeys
     * @param attributeValues
     * @return
     */
    @GET
    @Path("/users/")
    public UserDataTableBean findUsers(@Context HttpServletRequest servletRequest, @Context UriInfo uriInfo, @QueryParam("name") String userName,
                                       @QueryParam("roles") List<String> roles, @QueryParam("groups") List<String> groups,
                                       @QueryParam("start") long startIndex, @QueryParam("results") long results, @QueryParam("sort") String sortDirection,
                                       @QueryParam("attribute_key") List<String> attributeKeys, @QueryParam("attribute_value") List<String> attributeValues)
    {
        checkPrivilege(servletRequest, JetspeedActions.VIEW);
        
        Map<String, String> attributeMap = null;
        
        if (attributeKeys != null && attributeKeys.size() > 0 && attributeKeys.size() == attributeValues.size())
        {
            attributeMap = new HashMap<String, String>();
            
            for (int i = 0; i < attributeKeys.size(); i++)
            {
                if (attributeValues.get(i) != null && attributeValues.get(i).length() > 0)
                {
                    attributeMap.put(attributeKeys.get(i), attributeValues.get(i));
                }
            }
        }
        
        JetspeedPrincipalQueryContext ctx = new JetspeedPrincipalQueryContext(userName, startIndex, results, sortDirection, roles, groups, null, attributeMap);
        
        try
        {
            UserResultList resultList = userManager.getUsersExtended(ctx);
            UserDataTableBean result = new UserDataTableBean(resultList);
            result.setStartIndex(startIndex);
            result.setPageSize(results);
            result.setRecordsReturned(results);
            
            return result;
        }
        catch (SecurityException e)
        {
            if (log.isDebugEnabled())
            {
                log.error("Error searching users:" + ctx, e);
            }
            else
            {
                log.error("Error searching users:" + ctx + ". " + e);
            }
        }
        
        return null;
    }

    /**
     * Get users detail data, according to the users name. This method just gets all data that will be displayed in the view.
     * 
     * @param servletRequest
     * @param uriInfo
     * @param userName
     * @return
     */
    @GET
    @Path("/users/{name}/")
    public UserDetailBean getUserByName(@Context HttpServletRequest servletRequest, @Context UriInfo uriInfo, @PathParam("name") String userName)
    {
        checkPrivilege(servletRequest, JetspeedActions.VIEW);
        
        try
        {
            User user = userManager.getUser(userName);
            PasswordCredential credential = userManager.getPasswordCredential(user);
            List<Role> roles = roleManager.getRolesForUser(user.getName());
            List<String> availableRoles = roleManager.getRoleNames(null);
            List<Group> groups = groupManager.getGroupsForUser(user.getName());
            List<String> availableGroups = groupManager.getGroupNames(null);
            
            return new UserDetailBean(user, credential, roles, groups, availableRoles, availableGroups);
        }
        catch (Exception e)
        {
            if (log.isDebugEnabled())
            {
                log.debug("Error requesting users datail data:" + userName, e);
            }
        }
        
        return null;
    }

    /**
     * Update user data.
     * 
     * @param servletRequest
     * @param uriInfo
     * @param userName
     * @return
     */
    @POST
    @Path("/users/{name}/")
    public Boolean updateUserDetail(@Context HttpServletRequest servletRequest, @Context UriInfo uriInfo, @PathParam("name") String userName,
                                    @FormParam("user_name_given") String userNameGiven, @FormParam("user_name_family") String userNameFamily,
                                    @FormParam("user_email") String userEmail, @FormParam("password") String password,
                                    @FormParam("password_confirm") String passwordConfirm, @FormParam("user_enabled") Boolean userEnabled,
                                    @FormParam("credential_update_required") Boolean credentialUpdateRequired, @FormParam("roles") List<String> roles,
                                    @FormParam("groups") List<String> groups)
    {
        checkPrivilege(servletRequest, JetspeedActions.VIEW);
        
        try
        {
            boolean changePassword = false;
            
            if (password != null && password.length() > 0)
            {
                if (!password.equals(passwordConfirm))
                {
                    ResponseBuilder builder = Response.status(Status.BAD_REQUEST);
                    builder.type("text/plain");
                    builder.entity("password.confirmation.failed");
                    throw new WebApplicationException(builder.build());
                }
                
                changePassword = true;
            }
            
            User user = userManager.getUser(userName);
            user.getSecurityAttributes().getAttribute("user.name.given", true).setStringValue(userNameGiven);
            user.getSecurityAttributes().getAttribute("user.name.family", true).setStringValue(userNameFamily);
            user.getSecurityAttributes().getAttribute("user.email", true).setStringValue(userEmail);
            
            if (userEnabled == null)
            {
                userEnabled = false;
            }
            
            user.setEnabled(userEnabled);
            
            userManager.updateUser(user);
            
            if (credentialUpdateRequired == null)
            {
                credentialUpdateRequired = false;
            }
            
            PasswordCredential credential = userManager.getPasswordCredential(user);
            
            if (changePassword)
            {
                credential.setPassword(password, false);
            }
            
            credential.setUpdateRequired(credentialUpdateRequired);
            
            userManager.storePasswordCredential(credential);
            
            // merge roles
            List<Role> currentRoles = roleManager.getRolesForUser(user.getName());
            for (Role currentRole : currentRoles)
            {
                if (roles != null && roles.contains(currentRole.getName()))
                {
                    roles.remove(currentRole.getName());
                }
                else
                {
                    roleManager.removeRoleFromUser(userName, currentRole.getName());
                }
            }
            
            if (roles != null)
            {
                for (String roleName : roles)
                {
                    roleManager.addRoleToUser(userName, roleName);
                }
            }
            
            // merge groups
            List<Group> currentGroups = groupManager.getGroupsForUser(user.getName());
            
            for (Group currentGroup : currentGroups)
            {
                if (groups != null && groups.contains(currentGroup.getName()))
                {
                    groups.remove(currentGroup.getName());
                }
                else
                {
                    groupManager.removeUserFromGroup(userName, currentGroup.getName());
                }
            }
            
            if (groups != null)
            {
                for (String groupName : groups)
                {
                    groupManager.addUserToGroup(userName, groupName);
                }
            }
            
            return new Boolean(true);
        }
        catch (WebApplicationException e)
        {
            // re-throw exception
            throw e;
        }
        catch (SecurityException e)
        {
            ResponseBuilder builder = Response.status(Status.BAD_REQUEST);
            builder.type("text/plain");
            builder.entity(e.getKeyedMessage().getKey());
            
            throw new WebApplicationException(builder.build());
        }
        catch (Exception e)
        {
            // handle other exceptions
            if (log.isErrorEnabled())
            {
                log.error("Error updating users :" + userName, e);
            }
            
            throw new WebApplicationException(e);
        }
    }

    /**
     * <p> Create user data. </p> <p> It uses the property "registration.roles.default" from jetspeed.properties as default roles for new created users. </p>
     * <p> It uses the property "registration.rules.default" from jetspeed.properties as default profiling rules for new created users. The locator name is
     * always "default". </p> <p> It uses the property "psml.template.folder" from jetspeed.properties as PSML template folder to create new users. </p>
     * 
     * @param servletRequest
     * @param uriInfo
     * @param userName
     * @param userNameGiven
     * @param userNameFamily
     * @param userEmail
     * @param password
     * @param passwordConfirm
     * @param credentialUpdateRequired
     * @return
     */
    @POST
    @Path("/users/")
    public Boolean createUser(@Context HttpServletRequest servletRequest, @Context UriInfo uriInfo, @FormParam("name") String userName,
                              @FormParam("user_name_given") String userNameGiven, @FormParam("user_name_family") String userNameFamily,
                              @FormParam("user_email") String userEmail, @FormParam("password") String password,
                              @FormParam("password_confirm") String passwordConfirm, @FormParam("credential_update_required") Boolean credentialUpdateRequired)
    {
        checkPrivilege(servletRequest, JetspeedActions.VIEW);
        
        try
        {
            boolean changePassword = false;
            
            if (password != null && password.length() > 0)
            {
                if (!password.equals(passwordConfirm))
                {
                    ResponseBuilder builder = Response.status(Status.BAD_REQUEST);
                    builder.type("text/plain");
                    builder.entity("password.confirmation.failed");
                    throw new WebApplicationException(builder.build());
                }
                
                changePassword = true;
            }
            
            User user = userManager.addUser(userName);
            
            user.getSecurityAttributes().getAttribute("user.name.given", true).setStringValue(userNameGiven);
            user.getSecurityAttributes().getAttribute("user.name.family", true).setStringValue(userNameFamily);
            user.getSecurityAttributes().getAttribute("user.email", true).setStringValue(userEmail);
            
            userManager.updateUser(user);
            
            if (credentialUpdateRequired == null)
            {
                credentialUpdateRequired = false;
            }
            
            PasswordCredential credential = userManager.getPasswordCredential(user);
            
            if (changePassword)
            {
                credential.setPassword(password, false);
            }
            
            credential.setUpdateRequired(credentialUpdateRequired);
            userManager.storePasswordCredential(credential);
            
            // add default user roles
            String[] defaultUserRoles = Jetspeed.getConfiguration().getStringArray(PortalConfigurationConstants.REGISTRATION_ROLES_DEFAULT);
            
            for (String defaultUserRole : defaultUserRoles)
            {
                roleManager.addRoleToUser(userName, defaultUserRole);
            }
            
            // add default user profiling rules
            String[] defaultUserProfilingRules = Jetspeed.getConfiguration().getStringArray(PortalConfigurationConstants.REGISTRATION_ROLES_DEFAULT);
            
            for (String defaultUserProfilingRule : defaultUserProfilingRules)
            {
                ProfilingRule profilingRule = profiler.getRule(defaultUserProfilingRule);
                
                if (profilingRule != null)
                {
                    profiler.setRuleForPrincipal(user, profilingRule, "default");
                }
                else
                {
                    log.error("Failed to set profiling rule for principal. Invalid profiling rule: " + defaultUserProfilingRule);
                }
            }
            
            // copy the entire directory tree from the template folder
            String templateFolder = Jetspeed.getConfiguration().getString(PortalConfigurationConstants.PSML_TEMPLATE_FOLDER);
            
            if (!(templateFolder == null || templateFolder.trim().length() == 0))
            {
                Folder source = pageManager.getFolder(templateFolder);
                pageManager.deepCopyFolder(source, Folder.USER_FOLDER + userName, userName);
            }
            
            return new Boolean(true);
        }
        catch (WebApplicationException e)
        {
            // re-throw exception
            throw e;
        }
        catch (SecurityException e)
        {
            ResponseBuilder builder = Response.status(Status.BAD_REQUEST);
            builder.type("text/plain");
            builder.entity(e.getKeyedMessage().getKey());
            
            throw new WebApplicationException(builder.build());
        }
        catch (Exception e)
        {
            // handle other exceptions
            if (log.isErrorEnabled())
            {
                log.error("Error creating users :" + userName, e);
            }
            
            throw new WebApplicationException(e);
        }
    }

    /**
     * Get users detail data, according to the users name. This method just gets all data that will be displayed in the view.
     * 
     * @param servletRequest
     * @param uriInfo
     * @param userName
     * @return
     */
    @DELETE
    @Path("/users/{name}/")
    public Boolean deleteUserByName(@Context HttpServletRequest servletRequest, @Context UriInfo uriInfo, @PathParam("name") String userName)
    {
        checkPrivilege(servletRequest, JetspeedActions.VIEW);
        
        try
        {
            userManager.removeUser(userName);
            return true;
        }
        catch (SecurityException e)
        {
            ResponseBuilder builder = Response.status(Status.BAD_REQUEST);
            builder.type("text/plain");
            builder.entity(e.getKeyedMessage().getKey());
            throw new WebApplicationException(builder.build());
        }
        catch (Exception e)
        {
            // handle other exceptions
            if (log.isErrorEnabled())
            {
                log.error("Error creating users :" + userName, e);
            }
            throw new WebApplicationException(e);
        }
    }

    protected void checkPrivilege(HttpServletRequest servletRequest, String action)
    {
        RequestContext requestContext = (RequestContext) servletRequest.getAttribute(RequestContext.REQUEST_PORTALENV);
        
        if (securityBehavior != null && !securityBehavior.checkAccess(requestContext, action))
        {
            throw new WebApplicationException(new JetspeedException("Insufficient privilege to access this REST service."));
        }
    }
}
