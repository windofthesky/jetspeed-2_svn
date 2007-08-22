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
package org.apache.jetspeed.serializer;

import java.lang.reflect.Constructor;
import java.security.Permission;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.jetspeed.security.BasePrincipal;
import org.apache.jetspeed.security.FolderPermission;
import org.apache.jetspeed.security.FragmentPermission;
import org.apache.jetspeed.security.Group;
import org.apache.jetspeed.security.GroupManager;
import org.apache.jetspeed.security.PagePermission;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.PermissionManager;
import org.apache.jetspeed.security.PortalResourcePermission;
import org.apache.jetspeed.security.PortletPermission;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.om.InternalPermission;
import org.apache.jetspeed.security.om.InternalPrincipal;
import org.apache.jetspeed.security.spi.PasswordCredentialProvider;
import org.apache.jetspeed.serializer.objects.JSGroup;
import org.apache.jetspeed.serializer.objects.JSGroups;
import org.apache.jetspeed.serializer.objects.JSNVPElements;
import org.apache.jetspeed.serializer.objects.JSPermission;
import org.apache.jetspeed.serializer.objects.JSRole;
import org.apache.jetspeed.serializer.objects.JSRoles;
import org.apache.jetspeed.serializer.objects.JSSnapshot;
import org.apache.jetspeed.serializer.objects.JSUser;
import org.apache.jetspeed.serializer.objects.JSUserAttributes;
import org.apache.jetspeed.serializer.objects.JSUserGroups;
import org.apache.jetspeed.serializer.objects.JSUserRoles;
import org.apache.jetspeed.serializer.objects.JSUserUsers;
import org.apache.jetspeed.serializer.objects.JSUsers;

/**
 * JetspeedSecuritySerializer - Security component serializer
 *
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class JetspeedSecuritySerializer extends AbstractJetspeedComponentSerializer
{
    private static String ENCODING_STRING = "JETSPEED 2.1 - 2006";

    private static String JETSPEED = "JETSPEED";

    private static class Refs
    {
        private HashMap roleMap = new HashMap();

        private HashMap groupMap = new HashMap();

        private HashMap userMap = new HashMap();

        private HashMap permissionMap = new HashMap();
    }

    protected GroupManager groupManager;

    protected RoleManager roleManager;

    protected UserManager userManager;

    protected PasswordCredentialProvider pcp;

    protected PermissionManager pm;

    public JetspeedSecuritySerializer(GroupManager groupManager, RoleManager roleManager, UserManager userManager,
            PasswordCredentialProvider pcp, PermissionManager pm)
    {
        this.groupManager = groupManager;
        this.roleManager = roleManager;
        this.userManager = userManager;
        this.pcp = pcp;
        this.pm = pm;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.serializer.JetspeedComponentSerializer#processExport(org.apache.jetspeed.serializer.objects.JSSnapshot,
     *      java.util.Map, org.apache.commons.logging.Log)
     */
    protected void processExport(JSSnapshot snapshot, Map settings, Log log) throws SerializerException
    {
        if (isSettingSet(settings, JetspeedSerializer.KEY_PROCESS_USERS))
        {
            log.info("collecting users/roles/groups and permissions");
            Refs refs = new Refs();
            exportRolesGroupsUsers(refs, snapshot, settings, log);
            exportPermissions(refs, snapshot, settings, log);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.serializer.JetspeedComponentSerializer#processImport(org.apache.jetspeed.serializer.objects.JSSnapshot,
     *      java.util.Map, org.apache.commons.logging.Log)
     */
    protected void processImport(JSSnapshot snapshot, Map settings, Log log) throws SerializerException
    {
        if (isSettingSet(settings, JetspeedSerializer.KEY_PROCESS_USERS))
        {
            log.info("creating users/roles/groups and permissions");
            Refs refs = new Refs();
            recreateRolesGroupsUsers(refs, snapshot, settings, log);
            recreatePermissions(refs, snapshot, settings, log);
        }
    }

    protected void deleteData(Map settings, Log log) throws SerializerException
    {
        if (isSettingSet(settings, JetspeedSerializer.KEY_PROCESS_USERS))
        {
            log.info("deleting users/roles/groups and permissions");
            try
            {
                Iterator _it = pm.getPermissions().iterator();
                while ( _it.hasNext() )
                {
                    InternalPermission ip = (InternalPermission)_it.next();
                    Class permissionClass = Class.forName(ip.getClassname());
                    Class[] parameterTypes = { String.class, String.class };
                    Constructor permissionConstructor = permissionClass.getConstructor(parameterTypes);
                    Object[] initArgs = { ip.getName(), ip.getActions() };
                    Permission permission = (Permission) permissionConstructor.newInstance(initArgs);            
                    pm.removePermission(permission);
                }
                
                String anonymousUser = userManager.getAnonymousUser();
                _it = userManager.getUserNames("");
                while (_it.hasNext())
                {
                    String userName = (String)_it.next();
                    if ( !anonymousUser.equals(userName) )
                    {
                        userManager.removeUser((String)userName);
                    }
                }
                
                _it = groupManager.getGroups("");
                while (_it.hasNext())
                {
                    groupManager.removeGroup(((Group)_it.next()).getPrincipal().getName());
                }
                
                _it = roleManager.getRoles("");
                while (_it.hasNext())
                {
                    roleManager.removeRole(((Role)_it.next()).getPrincipal().getName());
                }
            }
            catch (Exception e)
            {
                throw new SerializerException(e);
            }
        }
    }

    /**
     * import the groups, roles and finally the users to the current environment
     * 
     * @throws SerializerException
     */
    private void recreateRolesGroupsUsers(Refs refs, JSSnapshot snapshot, Map settings, Log log)
            throws SerializerException
    {
        log.debug("recreateRolesGroupsUsers");

        JSGroups groups = null;
        JSRoles roles = null;

        groups = snapshot.getGroups();

        Iterator _it = groups.iterator();
        while (_it.hasNext())
        {
            String name = ((JSGroup) _it.next()).getName();

            try
            {
                if (!(groupManager.groupExists(name)))
                    groupManager.addGroup(name);
                Group group = groupManager.getGroup(name);
                refs.groupMap.put(name, group.getPrincipal());
            }
            catch (Exception e)
            {
                throw new SerializerException(SerializerException.CREATE_OBJECT_FAILED.create(new String[] { "Group",
                        e.getMessage() }));
            }
        }
        log.debug("recreateGroups - done");
        log.debug("processing roles");

        roles = snapshot.getRoles();

        _it = roles.iterator();
        while (_it.hasNext())
        {
            String name = ((JSRole) _it.next()).getName();

            try
            {
                if (!(roleManager.roleExists(name)))
                    roleManager.addRole(name);
                Role role = roleManager.getRole(name);
                refs.roleMap.put(name, role.getPrincipal());
            }
            catch (Exception e)
            {
                throw new SerializerException(SerializerException.CREATE_OBJECT_FAILED.create(new String[] { "Role",
                        e.getMessage() }));
            }
        }
        log.debug("recreateRoles - done");
        log.debug("processing users");

        /** determine whether passwords can be reconstructed or not */
        int passwordEncoding = compareCurrentSecurityProvider(snapshot);
        JSUsers users = null;
        users = snapshot.getUsers();

        _it = users.iterator();
        while (_it.hasNext())
        {

            JSUser jsuser = (JSUser) _it.next();

            try
            {
                User user = null;
                if (userManager.userExists(jsuser.getName()))
                {
                    user = userManager.getUser(jsuser.getName());
                }
                if ((isSettingSet(settings, JetspeedSerializer.KEY_OVERWRITE_EXISTING)) || (user == null))
                {
                    if (user == null) // create new one
                    {
                        String password = recreatePassword(jsuser.getPassword());
                        log.debug("add User " + jsuser.getName() + " with password " + password);
                        userManager.importUser(jsuser.getName(), password,
                                (passwordEncoding == JetspeedSerializer.PASSTHRU_REQUIRED));
                        log.debug("add User done ");
                        user = userManager.getUser(jsuser.getName());
                    }
                    try
                    {
                        userManager.setPasswordEnabled(jsuser.getName(), jsuser.getPwEnabled());
                        userManager.setPasswordUpdateRequired(jsuser.getName(), jsuser.getPwRequiredUpdate());
                        java.sql.Date d = jsuser.getPwExpirationDate();
                        if (d != null)
                            userManager.setPasswordExpiration(jsuser.getName(), d);
                    }
                    catch (Exception e)
                    {
                        // most likely caused by protected users (like "guest")
                        log.debug("setting userinfo for " + jsuser.getName() + " failed because of "
                                + e.getLocalizedMessage());
                    }

                    // credentials
                    Subject subject = user.getSubject();

                    ArrayList listTemp = jsuser.getPrivateCredentials();
                    if ((listTemp != null) && (listTemp.size() > 0))
                    {
                        Iterator _itTemp = listTemp.iterator();
                        while (_itTemp.hasNext())
                        {
                            subject.getPrivateCredentials().add(_itTemp.next());
                        }
                    }
                    listTemp = jsuser.getPublicCredentials();
                    if ((listTemp != null) && (listTemp.size() > 0))
                    {
                        Iterator _itTemp = listTemp.iterator();
                        while (_itTemp.hasNext())
                        {
                            subject.getPublicCredentials().add(_itTemp.next());
                        }
                    }
                    JSUserGroups jsUserGroups = jsuser.getGroupString();
                    if (jsUserGroups != null)
                        listTemp = getTokens(jsUserGroups.toString());
                    else
                        listTemp = null;
                    if ((listTemp != null) && (listTemp.size() > 0))
                    {
                        Iterator _itTemp = listTemp.iterator();
                        while (_itTemp.hasNext())
                        {
                            groupManager.addUserToGroup(jsuser.getName(), (String) _itTemp.next());
                        }
                    }
                    JSUserRoles jsUserRoles = jsuser.getRoleString();
                    if (jsUserRoles != null)
                        listTemp = getTokens(jsUserRoles.toString());
                    else
                        listTemp = null;
                    if ((listTemp != null) && (listTemp.size() > 0))
                    {
                        Iterator _itTemp = listTemp.iterator();
                        while (_itTemp.hasNext())
                        {
                            roleManager.addRoleToUser(jsuser.getName(), (String) _itTemp.next());
                        }
                    }
                    JSUserAttributes attributes = jsuser.getUserInfo();
                    if (attributes != null)
                    {
                        Preferences userAttributes = user.getUserAttributes();
                        HashMap map = attributes.getMyMap();
                        if (map != null)
                        {
                            Iterator _itTemp = map.keySet().iterator();
                            while (_itTemp.hasNext())
                            {
                                String userAttrName = (String) _itTemp.next();
                                // if ( userAttributes.get(userAttrName,
                                // "").equals("")
                                String userAttrValue = (String) map.get(userAttrName);
                                userAttributes.put(userAttrName, userAttrValue);
                            }
                        }

                    }

                    JSNVPElements jsNVP = jsuser.getPreferences();
                    if ((jsNVP != null) && (jsNVP.getMyMap() != null))
                    {
                        Preferences preferences = user.getPreferences();
                        Iterator _itTemp = jsNVP.getMyMap().keySet().iterator();
                        while (_itTemp.hasNext())
                        {
                            String prefKey = (String) _itTemp.next();
                            String prefValue = (String) (jsNVP.getMyMap().get(prefKey));
                            preferences.put(prefKey, prefValue);
                        }
                    }

                    refs.userMap.put(jsuser.getName(), getUserPrincipal(user));

                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                throw new SerializerException(SerializerException.CREATE_OBJECT_FAILED.create(new String[] { "User",
                        e.getMessage() }));
            }
        }
        log.debug("recreateUsers - done");
    }

    /**
     * recreates all permissions from the current snapshot
     * 
     * @throws SerializerException
     */
    private void recreatePermissions(Refs refs, JSSnapshot snapshot, Map settings, Log log) throws SerializerException
    {
        log.debug("recreatePermissions - started");

        Iterator list = null;
        try
        {
            list = snapshot.getPermissions().iterator();
        }
        catch (Exception e)
        {
            throw new SerializerException(SerializerException.GET_EXISTING_OBJECTS.create(new String[] { "Permissions",
                    e.getMessage() }));
        }

        while (list.hasNext())
        {
            JSPermission _js = (JSPermission) list.next();
            PortalResourcePermission perm = getPermissionForType(_js);
            if ((perm != null) && (perm instanceof PortalResourcePermission))
            {
                try
                {
                    pm.addPermission(perm);
                    ArrayList listTemp = null;
                    JSUserGroups jsUserGroups = _js.getGroupString();
                    if (jsUserGroups != null)
                        listTemp = getTokens(jsUserGroups.toString());
                    else
                        listTemp = null;
                    if ((listTemp != null) && (listTemp.size() > 0))
                    {
                        Iterator _itTemp = listTemp.iterator();
                        while (_itTemp.hasNext())
                        {
                            Principal p = (Principal) refs.groupMap.get((String) _itTemp.next());
                            if (p != null)
                                pm.grantPermission(p, perm);
                        }
                    }
                    JSUserRoles jsUserRoles = _js.getRoleString();
                    if (jsUserRoles != null)
                        listTemp = getTokens(jsUserRoles.toString());
                    else
                        listTemp = null;
                    if ((listTemp != null) && (listTemp.size() > 0))
                    {
                        Iterator _itTemp = listTemp.iterator();
                        while (_itTemp.hasNext())
                        {
                            Principal p = (Principal) refs.roleMap.get((String) _itTemp.next());
                            if (p != null)
                                pm.grantPermission(p, perm);
                        }
                    }
                    JSUserUsers jsUserUsers = _js.getUserString();
                    if (jsUserUsers != null)
                        listTemp = getTokens(jsUserUsers.toString());
                    else
                        listTemp = null;
                    if ((listTemp != null) && (listTemp.size() > 0))
                    {
                        Iterator _itTemp = listTemp.iterator();
                        while (_itTemp.hasNext())
                        {
                            Principal p = (Principal) refs.userMap.get((String) _itTemp.next());
                            if (p != null)
                                pm.grantPermission(p, perm);
                        }
                    }

                }
                catch (Exception e)
                {
                    throw new SerializerException(SerializerException.CREATE_SERIALIZED_OBJECT_FAILED
                            .create(new String[] { "Permissions", e.getMessage() }));
                }
            }
        }
        log.debug("recreatePermissions - done");
    }

    private PortalResourcePermission getPermissionForType(JSPermission _js)
    {
        PortalResourcePermission newPermission = null; 
        if ((_js.getType() == null) || (_js.getType() == JSPermission.TYPE_UNKNOWN))
            return null;
        try
        {
        if (_js.getType().equals(JSPermission.TYPE_FOLDER))
            newPermission = new FolderPermission(_js.getResource(),_js.getActions());
        else if (_js.getType().equals(JSPermission.TYPE_FRAGMENT))
            newPermission = new FragmentPermission(_js.getResource(),_js.getActions());
            else if (_js.getType().equals(JSPermission.TYPE_PAGE))
                newPermission = new PagePermission(_js.getResource(),_js.getActions());
                else if (_js.getType().equals(JSPermission.TYPE_PORTAL))
                    newPermission = new PortletPermission(_js.getResource(),_js.getActions());
                    else return null;
            return newPermission;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Establish whether incoming passwords are "clear" text or whether they are
     * to be decoded. That however depends on whether the passwords were encoded
     * with the current active provider or not.
     * 
     * @return
     */
    protected int compareCurrentSecurityProvider(JSSnapshot snapshot)
    {
        String _fileEncryption = snapshot.getEncryption();
        if ((_fileEncryption == null) || (_fileEncryption.length() == 0))
            return JetspeedSerializer.NO_DECODING; // passwords are in clear
                                                    // text

        if (_fileEncryption.equals(getEncryptionString()))
            return JetspeedSerializer.PASSTHRU_REQUIRED;
        else
            return JetspeedSerializer.NO_DECODING;
    }

    private String getEncryptionString()
    {
        if (pcp == null)
        {
            System.err.println("Error!!! PasswordCredentialProvider not available");
            return ENCODING_STRING;
        }
        try
        {
            PasswordCredential credential = pcp.create(JETSPEED, ENCODING_STRING);
            if ((credential != null) && (credential.getPassword() != null))
                return new String(credential.getPassword());
            else
                return ENCODING_STRING;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return ENCODING_STRING;
        }
    }

    protected String recreatePassword(char[] savedPassword)
    {
        if (savedPassword == null)
            return null;
        return new String(savedPassword);
    }

    private Principal getUserPrincipal(User user)
    {
        Subject subject = user.getSubject();
        // get the user principal
        Set principals = subject.getPrincipals();
        Iterator list = principals.iterator();
        while (list.hasNext())
        {
            BasePrincipal principal = (BasePrincipal) list.next();
            String path = principal.getFullPath();
            if (path.startsWith("/user/"))
                return principal;
        }
        return null;
    }

    /**
     * Collect all the roles, groups and users from the current environment.
     * Include the current SecurityProvider to understand, whether the password
     * collected can be used upon import
     * 
     * @throws SerializerException
     */
    private void exportRolesGroupsUsers(Refs refs, JSSnapshot snapshot, Map settings, Log log)
            throws SerializerException
    {
        /** set the security provider info in the snapshot file */
        snapshot.setEncryption(getEncryptionString());
        /** get the roles */

        Iterator list = null;
        try
        {
            list = roleManager.getRoles("");
        }
        catch (Exception e)
        {
            throw new SerializerException(SerializerException.GET_EXISTING_OBJECTS.create(new String[] { "Role",
                    e.getMessage() }));
        }
        while (list.hasNext())
        {
            try
            {
                Role role = (Role) list.next();
                JSRole _tempRole = (JSRole) getObjectBehindPrinicpal(refs.roleMap,
                        (BasePrincipal) (role.getPrincipal()));
                if (_tempRole == null)
                {
                    _tempRole = createJSRole(role);
                    refs.roleMap.put(_tempRole.getName(), _tempRole);
                    snapshot.getRoles().add(_tempRole);
                }

            }
            catch (Exception e)
            {
                throw new SerializerException(SerializerException.CREATE_SERIALIZED_OBJECT_FAILED.create(new String[] {
                        "Role", e.getMessage() }));
            }
        }

        /** get the groups */
        try
        {
            list = groupManager.getGroups("");
        }
        catch (Exception e)
        {
            throw new SerializerException(SerializerException.GET_EXISTING_OBJECTS.create(new String[] { "Group",
                    e.getMessage() }));
        }
        while (list.hasNext())
        {

            try
            {
                Group group = (Group) list.next();
                JSGroup _tempGroup = (JSGroup) getObjectBehindPrinicpal(refs.groupMap, (BasePrincipal) (group
                        .getPrincipal()));
                if (_tempGroup == null)
                {
                    _tempGroup = createJSGroup(group);
                    refs.groupMap.put(_tempGroup.getName(), _tempGroup);
                    snapshot.getGroups().add(_tempGroup);
                }

            }
            catch (Exception e)
            {
                throw new SerializerException(SerializerException.CREATE_SERIALIZED_OBJECT_FAILED.create(new String[] {
                        "Group", e.getMessage() }));
            }
        }

        /** users */
        try
        {
            list = userManager.getUsers("");
        }
        catch (Exception e)
        {
            throw new SerializerException(SerializerException.GET_EXISTING_OBJECTS.create(new String[] { "User",
                    e.getMessage() }));
        }
        while (list.hasNext())
        {

            try
            {
                User _user = (User) list.next();
                JSUser _tempUser = createJSUser(refs, _user);
                refs.userMap.put(_tempUser.getName(), _tempUser);
                snapshot.getUsers().add(_tempUser);
            }
            catch (Exception e)
            {
                throw new SerializerException(SerializerException.CREATE_SERIALIZED_OBJECT_FAILED.create(new String[] {
                        "User", e.getMessage() }));
            }

        }
        return;

    }

    /**
     * extract all permissions from the current environment
     * 
     * @throws SerializerException
     */
    private void exportPermissions(Refs refs, JSSnapshot snapshot, Map settings, Log log) throws SerializerException
    {
        Object o = null;

        Iterator list = null;
        try
        {
            list = pm.getPermissions().iterator();
        }
        catch (Exception e)
        {
            throw new SerializerException(SerializerException.GET_EXISTING_OBJECTS.create(new String[] { "Permissions",
                    e.getMessage() }));
        }

        while (list.hasNext())
        {
            try
            {
                JSPermission _js = new JSPermission();

                InternalPermission p = (InternalPermission) list.next();
                _js.setResource(p.getName());
                _js.setActions(p.getActions());
                _js.setId(p.getPermissionId());
                _js.setType(_js.getTypeForClass(p.getClassname()));

                Iterator list2 = p.getPrincipals().iterator();
                while (list2.hasNext())
                {
                    o = list2.next();
                    InternalPrincipal principal = (InternalPrincipal) o;
                    String path = principal.getFullPath();
                    if (path.startsWith("/role/"))
                    {
                        JSRole _tempRole = (JSRole) this.getObjectBehindPath(refs.roleMap, removeFromString(path,
                                "/role/"));
                        if (_tempRole != null)
                        {
                            _js.addRole(_tempRole);
                        }

                    }
                    else
                    {
                        if (path.startsWith("/group/"))
                        {
                            JSGroup _tempGroup = (JSGroup) this.getObjectBehindPath(refs.groupMap, removeFromString(
                                    path, "/group/"));
                            if (_tempGroup != null)
                            {
                                _js.addGroup(_tempGroup);
                            }

                        }
                        else
                        {
                            if (path.startsWith("/user/"))
                            {
                                JSUser _tempUser = (JSUser) this.getObjectBehindPath(refs.userMap, removeFromString(
                                        path, "/user/"));
                                if (_tempUser != null)
                                {
                                    _js.addUser(_tempUser);
                                }

                            }

                        }

                    }
                }
                refs.permissionMap.put(_js.getType(), _js);
                snapshot.getPermissions().add(_js);

            }
            catch (Exception e)
            {
                throw new SerializerException(SerializerException.CREATE_SERIALIZED_OBJECT_FAILED.create(new String[] {
                        "Permissions", e.getMessage() }));
            }
        }
        return;

    }

    /**
     * simple lookup for principal object from a map
     * 
     * @param map
     * @param _fullPath
     * @return
     */

    private Object getObjectBehindPrinicpal(Map map, BasePrincipal _principal)
    {
        return getObjectBehindPath(map, _principal.getFullPath());
    }

    /**
     * simple lookup for object from a map
     * 
     * @param map
     * @param _fullPath
     * @return
     */
    protected final Object getObjectBehindPath(Map map, String _fullPath)
    {
        return map.get(_fullPath);
    }

    /**
     * remove a given sequence from the beginning of a string
     */
    protected final String removeFromString(String base, String excess)
    {
        return base.replaceFirst(excess, "").trim();
    }

    /**
     * create a serializable wrapper for role
     * 
     * @param role
     * @return
     */
    private JSRole createJSRole(Role role)
    {
        JSRole _role = new JSRole();
        _role.setName(role.getPrincipal().getName());
        return _role;
    }

    /**
     * create a wrapper JSGroup object
     */
    private JSGroup createJSGroup(Group group)
    {
        JSGroup _group = new JSGroup();
        _group.setName(group.getPrincipal().getName());
        return _group;
    }

    /**
     * Add the credentials to the JSUser object.
     * <p>
     * If the credential provided is a PasswordCredential, userid and password
     * are extracted and set explcitely
     * 
     * @param isPublic
     *            public or private credential
     * @param newUser
     *            the JS user object reference
     * @param credential
     *            the credential object
     */

    private void addJSUserCredentials(boolean isPublic, JSUser newUser, Object credential)
    {
        if (credential == null)
            return;
        if (credential instanceof PasswordCredential)
        {
            PasswordCredential pw = (PasswordCredential) credential;
            newUser.setUserCredential(pw.getUserName(), pw.getPassword(), pw.getExpirationDate(), pw.isEnabled(), pw
                    .isExpired(), pw.isUpdateRequired());
            return;
        }
        else if (isPublic)
            newUser.addPublicCredential(credential);
        else
            newUser.addPrivateCredential(credential);
    }

    /**
     * create a new JSUser object
     * 
     * @param user
     * @return a new JSUser object
     */
    private JSUser createJSUser(Refs refs, User user)
    {
        JSUser _newUser = new JSUser();

        Subject subject = user.getSubject();
        // get the user principal
        Set principals = subject.getPrincipals();
        Iterator list = principals.iterator();
        while (list.hasNext())
        {
            BasePrincipal principal = (BasePrincipal) list.next();
            String path = principal.getFullPath();
            if (path.startsWith("/role/"))
            {
                JSRole _tempRole = (JSRole) this.getObjectBehindPath(refs.roleMap, principal.getName());
                if (_tempRole != null)
                {
                    _newUser.addRole(_tempRole);
                }

            }
            else
            {
                if (path.startsWith("/group/"))
                {
                    JSGroup _tempGroup = (JSGroup) this.getObjectBehindPath(refs.groupMap, principal.getName());
                    if (_tempGroup != null)
                    {
                        _newUser.addGroup(_tempGroup);
                    }

                }
                else if (path.startsWith("/user/"))
                    _newUser.setPrincipal(principal);

            }

        }
        // System.out.println("User Public Credentials");
        Set credentials = subject.getPublicCredentials();
        list = credentials.iterator();
        while (list.hasNext())
        {
            Object credential = list.next();
            addJSUserCredentials(true, _newUser, credential);
        }
        // System.out.println("User Private Credentials");
        credentials = subject.getPrivateCredentials();
        list = credentials.iterator();
        while (list.hasNext())
        {
            Object credential = list.next();
            addJSUserCredentials(false, _newUser, credential);
        }

        Preferences preferences = user.getPreferences();
        _newUser.setPreferences(preferences);
        preferences = user.getUserAttributes();
        _newUser.setUserInfo(preferences);
        // TODO: HJB, fix preferences...userinfo doesn't return values in
        // prefs_property_value (in fact preferences.keys() is []
        return _newUser;
    }
}
