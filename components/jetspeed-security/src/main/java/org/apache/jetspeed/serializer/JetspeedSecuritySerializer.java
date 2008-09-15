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

import java.security.Principal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.jetspeed.security.Credential;
import org.apache.jetspeed.security.CredentialPasswordEncoder;
import org.apache.jetspeed.security.Group;
import org.apache.jetspeed.security.GroupManager;
import org.apache.jetspeed.security.JetspeedPermission;
import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalManager;
import org.apache.jetspeed.security.JetspeedPrincipalManagerProvider;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.PermissionManager;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.SecurityAttributeType;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.SecurityAttribute;
import org.apache.jetspeed.security.SecurityAttributes;
import org.apache.jetspeed.serializer.objects.JSGroup;
import org.apache.jetspeed.serializer.objects.JSNVPElement;
import org.apache.jetspeed.serializer.objects.JSNVPElements;
import org.apache.jetspeed.serializer.objects.JSPermission;
import org.apache.jetspeed.serializer.objects.JSPermissions;
import org.apache.jetspeed.serializer.objects.JSPrincipal;
import org.apache.jetspeed.serializer.objects.JSRole;
import org.apache.jetspeed.serializer.objects.JSSnapshot;
import org.apache.jetspeed.serializer.objects.JSUser;
import org.apache.jetspeed.serializer.objects.JSUserAttributes;
import org.apache.jetspeed.serializer.objects.JSUserGroups;
import org.apache.jetspeed.serializer.objects.JSUserRoles;
import org.apache.jetspeed.serializer.objects.JSUserUsers;

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

    private static class ImportRefs
    {
        private HashMap<String, HashMap<String, Principal>> principalMapByType = new HashMap<String, HashMap<String, Principal>>();
        private HashMap<String, Principal> roleMap = new HashMap<String, Principal>();
        private HashMap<String, Principal> groupMap = new HashMap<String, Principal>();
        private HashMap<String, Principal> userMap = new HashMap<String, Principal>();
        private HashMap<String, JSPermission> permissionMap = new HashMap<String, JSPermission>();
        
        public ImportRefs()
        {
            principalMapByType.put(JetspeedPrincipalType.USER_TYPE_NAME, userMap);
            principalMapByType.put(JetspeedPrincipalType.GROUP_TYPE_NAME, userMap);
            principalMapByType.put(JetspeedPrincipalType.ROLE_TYPE_NAME, userMap);
        }
        
        public HashMap<String, Principal> getPrincipalMap(String principalTypeName)
        {
            if (principalMapByType.containsKey(principalTypeName))
            {
                return principalMapByType.get(principalTypeName);
            }
            else
            {
                return principalMapByType.put(principalTypeName, new HashMap<String, Principal>());
            }
        }
    }
    
    private static class ExportRefs
    {
        private HashMap<String, HashMap<String, JSPrincipal>> principalMapByType = new HashMap<String, HashMap<String, JSPrincipal>>();
        private HashMap<String, JSRole> roleMap = new HashMap<String, JSRole>();
        private HashMap<String, JSGroup> groupMap = new HashMap<String, JSGroup>();
        private HashMap<String, JSUser> userMap = new HashMap<String, JSUser>();
        private HashMap<String, JSPermission> permissionMap = new HashMap<String, JSPermission>();
        
        public HashMap<String, JSPrincipal> getPrincipalMap(String principalTypeName)
        {
            if (principalMapByType.containsKey(principalTypeName))
            {
                return principalMapByType.get(principalTypeName);
            }
            else
            {
                return principalMapByType.put(principalTypeName, new HashMap<String, JSPrincipal>());
            }
        }
    }

    protected JetspeedPrincipalManagerProvider principalManagerProvider;
    protected GroupManager groupManager;
    protected RoleManager roleManager;
    protected UserManager userManager;
    protected CredentialPasswordEncoder cpe;
    protected PermissionManager pm;

    public JetspeedSecuritySerializer(JetspeedPrincipalManagerProvider principalManagerProvider, GroupManager groupManager, RoleManager roleManager, UserManager userManager,
            CredentialPasswordEncoder cpe, PermissionManager pm)
    {
        this.principalManagerProvider = principalManagerProvider;
        this.groupManager = groupManager;
        this.roleManager = roleManager;
        this.userManager = userManager;
        this.cpe = cpe;
        this.pm = pm;
    }

    protected void processExport(JSSnapshot snapshot, Map settings, Log log) throws SerializerException
    {
        if (isSettingSet(settings, JetspeedSerializer.KEY_PROCESS_USERS))
        {
            try
            {
                log.info("collecting users/roles/groups");
                ExportRefs refs = new ExportRefs();
                // TODO: exporting can be dangerous this time..
                //exportJetspeedPrincipals(refs, snapshot, settings, log);
                exportRolesGroupsUsers(refs, snapshot, settings, log);
                if (isSettingSet(settings, JetspeedSerializer.KEY_PROCESS_PERMISSIONS))
                {
                    log.info("collecting permissions");
                    // TODO: uncomment and fix after permission refactoring
                    //exportPermissions(refs, snapshot, settings, log);
                }
            }
            catch (SecurityException se)
            {
                throw new SerializerException(se);
            }
        }
    }

    protected void processImport(JSSnapshot snapshot, Map settings, Log log) throws SerializerException
    {
        if (isSettingSet(settings, JetspeedSerializer.KEY_PROCESS_USERS))
        {
            log.info("creating users/roles/groups and permissions");
            ImportRefs refs = new ImportRefs();
            recreateJetspeedPrincipals(refs, snapshot, settings, log);
            recreateRolesGroupsUsers(refs, snapshot, settings, log);
            if (isSettingSet(settings, JetspeedSerializer.KEY_PROCESS_PERMISSIONS))
            {
                log.info("creating permissions");
                recreatePermissions(refs, snapshot, settings, log);
            }
        }
    }

    protected void deleteData(Map settings, Log log) throws SerializerException
    {
        if (isSettingSet(settings, JetspeedSerializer.KEY_PROCESS_USERS))
        {
            log.info("deleting users/roles/groups and permissions");
            try
            {
                for (JetspeedPermission permission : pm.getPermissions())
                {
                    pm.removePermission(permission);
                }                
                String anonymousUser = userManager.getAnonymousUser();
                for (String userName : userManager.getUserNames(""))
                {
                    if (!anonymousUser.equals(userName))
                    {
                        userManager.removeUser((String)userName);
                    }
                }                
                for (Group group : groupManager.getGroups(""))
                {
                    groupManager.removeGroup(group.getName());
                }
                for (Role role : roleManager.getRoles(""))
                {
                    roleManager.removeRole(role.getName());
                }
            }
            catch (Exception e)
            {
                throw new SerializerException(e);
            }
        }
    }

    /**
     * import the Jetspeed principals to the current environment
     * TODO: how about associations?
     * 
     * @throws SerializerException
     */
    private void recreateJetspeedPrincipals(ImportRefs refs, JSSnapshot snapshot, Map settings, Log log)
            throws SerializerException
    {
        log.debug("recreateJetspeedPrincipals");
        
        Map<String, JetspeedPrincipalType> principalTypeMap = principalManagerProvider.getPrincipalTypeMap();
        
        for (JSPrincipal jsPrincipal : snapshot.getJetspeedPrincipals())
        {
            try
            {
                JetspeedPrincipalType type = principalTypeMap.get(jsPrincipal.getType());
                JetspeedPrincipalManager principalManager = principalManagerProvider.getManager(type);
                
                String name = jsPrincipal.getName();
                
                if (!principalManager.principalExists(name))
                {
                    JetspeedPrincipal principal = principalManager.newPrincipal(name, jsPrincipal.isMapped());
                    principal.setEnabled(jsPrincipal.isEnabled());
                    principalManager.addPrincipal(principal, null);
                    
                    boolean updated = false;
                    
                    SecurityAttributes secAttrs = principal.getSecurityAttributes();
                    
                    for (JSNVPElement elem : jsPrincipal.getSecurityAttributes().getValues())
                    {
                        secAttrs.getAttribute(elem.getKey(), true).setStringValue(elem.getValue());
                        updated = true;
                    }
                    
                    Map<String, SecurityAttributeType> secAttrTypeMap = secAttrs.getSecurityAttributeTypes().getAttributeTypeMap();
                    
                    for (JSNVPElement elem : jsPrincipal.getInfoAttributes().getValues())
                    {
                        if (secAttrTypeMap.containsKey(elem.getKey()))
                        {
                            secAttrs.getAttribute(elem.getKey(), true).setStringValue(elem.getValue());
                        }
                        else
                        {
                            secAttrs.addNewInfoAttribute(elem.getKey(), SecurityAttributeType.DataType.STRING).setStringValue(elem.getValue());
                        }
                        
                        updated = true;
                    }
                    
                    if (updated)
                    {
                        principalManager.updatePrincipal(principal);
                    }
                    
                    refs.getPrincipalMap(type.getName()).put(name, principal);
                }
            }
            catch (Exception e)
            {
                throw new SerializerException(SerializerException.CREATE_OBJECT_FAILED.create(new String[] { "JetspeedPrincipal",
                        e.getMessage() }));
            }
        }
        
        log.debug("recreateJetspeedPrincipals - done");
    }
    
    /**
     * import the groups, roles and finally the users to the current environment
     * 
     * @throws SerializerException
     */
    private void recreateRolesGroupsUsers(ImportRefs refs, JSSnapshot snapshot, Map settings, Log log)
            throws SerializerException
    {
        log.debug("recreateRolesGroupsUsers");
        for (JSGroup jsGroup : snapshot.getGroups())
        {
            String name = jsGroup.getName();
            try
            {
                if (!(groupManager.groupExists(name)))
                    groupManager.addGroup(name);
                Group group = groupManager.getGroup(name);
                refs.groupMap.put(name, (Principal) group);
            }
            catch (Exception e)
            {
                throw new SerializerException(SerializerException.CREATE_OBJECT_FAILED.create(new String[] { "Group",
                        e.getMessage() }));
            }
        }
        log.debug("recreateGroups - done");
        log.debug("processing roles");

        for (JSRole jsRole : snapshot.getRoles())
        {
            String name = jsRole.getName();
            try
            {
                if (!(roleManager.roleExists(name)))
                    roleManager.addRole(name);
                Role role = roleManager.getRole(name);
                refs.roleMap.put(name, (Principal) role);
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
        for (JSUser jsuser : snapshot.getUsers())
        {
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
                        
                        user = userManager.addUser(jsuser.getName());
                        PasswordCredential pwc = userManager.getPasswordCredential(user);
                        pwc.setPassword(null, password);
                        pwc.setEncoded((passwordEncoding == JetspeedSerializer.PASSTHRU_REQUIRED));
                        userManager.updateUser(user);
                        log.debug("add User done ");
                    }
                    try
                    {
                        PasswordCredential pwc = userManager.getPasswordCredential(user);
                        pwc.setEnabled(jsuser.getPwEnabled());
                        pwc.setUpdateRequired(jsuser.getPwRequiredUpdate());
                        java.sql.Date d = jsuser.getPwExpirationDate();
                        if (d != null)
                            pwc.setExpirationDate(d);
                        userManager.storePasswordCredential(pwc);
                    }
                    catch (Exception e)
                    {
                        // most likely caused by protected users (like "guest")
                        log.debug("setting userinfo for " + jsuser.getName() + " failed because of "
                                + e.getLocalizedMessage());
                    }
                    // credentials
                    Subject subject = userManager.getSubject(user);
                    List<Credential> listTemp = jsuser.getPrivateCredentials();
                    if ((listTemp != null) && (listTemp.size() > 0))
                    {
                        Iterator<Credential> _itTemp = listTemp.iterator();
                        while (_itTemp.hasNext())
                        {
                            subject.getPrivateCredentials().add(_itTemp.next());
                        }
                    }
                    listTemp = jsuser.getPublicCredentials();
                    if ((listTemp != null) && (listTemp.size() > 0))
                    {
                        Iterator<Credential> _itTemp = listTemp.iterator();
                        while (_itTemp.hasNext())
                        {
                            subject.getPublicCredentials().add(_itTemp.next());
                        }
                    }
                    JSUserGroups jsUserGroups = jsuser.getGroupString();
                    List<String> listUserGroups = null;
                    if (jsUserGroups != null)
                        listUserGroups = getTokens(jsUserGroups.toString());
                    if ((listUserGroups != null) && (listUserGroups.size() > 0))
                    {
                        Iterator<String> _itTemp = listUserGroups.iterator();
                        while (_itTemp.hasNext())
                        {
                            groupManager.addUserToGroup(jsuser.getName(), (String) _itTemp.next());
                        }
                    }
                    JSUserRoles jsUserRoles = jsuser.getRoleString();
                    List<String> listUserRoles = null;
                    if (jsUserRoles != null)
                        listUserRoles = getTokens(jsUserRoles.toString());
                    else
                        listUserRoles = null;
                    if ((listUserRoles != null) && (listUserRoles.size() > 0))
                    {
                        Iterator<String> _itTemp = listUserRoles.iterator();
                        while (_itTemp.hasNext())
                        {
                            roleManager.addRoleToUser(jsuser.getName(), (String) _itTemp.next());
                        }
                    }
                    JSUserAttributes attributes = jsuser.getUserInfo();
                    if (attributes != null)
                    {
                        SecurityAttributes userSecAttrs = user.getSecurityAttributes();
                        Map<String, SecurityAttributeType> userSecAttrTypeMap = userSecAttrs.getSecurityAttributeTypes().getAttributeTypeMap();
                        
                        for (JSNVPElement element : attributes.getValues())
                        {
                            String attrName = element.getKey();
                            String attrValue = element.getValue();
                            
                            if (userSecAttrTypeMap.containsKey(attrName))
                            {
                                userSecAttrs.getAttribute(attrName, true).setStringValue(attrValue);
                            }
                            else
                            {
                                userSecAttrs.addNewInfoAttribute(attrName, SecurityAttributeType.DataType.STRING).setStringValue(attrValue);
                            }                            
                        }
                    }
                    JSNVPElements jsNVP = jsuser.getSecurityAttributes();
                    if ((jsNVP != null) && (jsNVP.getValues() != null))
                    {
                        SecurityAttributes userSecAttrs = user.getSecurityAttributes();
                        
                        for (JSNVPElement element : jsNVP.getValues())
                        {
                            userSecAttrs.getAttribute(element.getKey(), true).setStringValue(element.getValue());
                        }
                    }
                    refs.userMap.put(jsuser.getName(), (Principal) user);
                    userManager.updateUser(user);
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
    private void recreatePermissions(ImportRefs refs, JSSnapshot snapshot, Map settings, Log log) throws SerializerException
    {
        log.debug("recreatePermissions - started");
        JSPermissions permissionList = null ;
        try
        {
            permissionList = snapshot.getPermissions();
        }
        catch (Exception e)
        {
            throw new SerializerException(SerializerException.GET_EXISTING_OBJECTS.create(new String[] { "Permissions",
                    e.getMessage() }));
        }
        for (JSPermission jsPermission : permissionList)
        {
            JetspeedPermission perm = null;
            if (jsPermission.getType().equals(JSPermission.TYPE_PORTAL))
            {
                perm = pm.newPermission(pm.PORTLET_PERMISSION, jsPermission.getResource(), jsPermission.getActions());
            }
            else
            {
                perm = pm.newPermission(jsPermission.getType(), jsPermission.getResource(), jsPermission.getActions());
            }
            if (perm != null && !pm.permissionExists(perm))
            {
                try
                {
                    pm.addPermission(perm);
                    List<String> listTemp = null;
                    JSUserGroups jsUserGroups = jsPermission.getGroupString();
                    if (jsUserGroups != null)
                        listTemp = getTokens(jsUserGroups.toString());
                    if ((listTemp != null) && (listTemp.size() > 0))
                    {
                        Iterator<String> _itTemp = listTemp.iterator();
                        while (_itTemp.hasNext())
                        {
                            JetspeedPrincipal p = (JetspeedPrincipal) refs.groupMap.get((String) _itTemp.next());
                            if (p != null)
                                pm.grantPermission(perm, p);
                        }
                    }
                    JSUserRoles jsUserRoles = jsPermission.getRoleString();
                    if (jsUserRoles != null)
                        listTemp = getTokens(jsUserRoles.toString());
                    else
                        listTemp = null;
                    if ((listTemp != null) && (listTemp.size() > 0))
                    {
                        Iterator<String> _itTemp = listTemp.iterator();
                        while (_itTemp.hasNext())
                        {
                            JetspeedPrincipal p = (JetspeedPrincipal) refs.roleMap.get((String) _itTemp.next());
                            if (p != null)
                                pm.grantPermission(perm, p);
                        }
                    }
                    JSUserUsers jsUserUsers = jsPermission.getUserString();
                    if (jsUserUsers != null)
                        listTemp = getTokens(jsUserUsers.toString());
                    else
                        listTemp = null;
                    if ((listTemp != null) && (listTemp.size() > 0))
                    {
                        Iterator<String> _itTemp = listTemp.iterator();
                        while (_itTemp.hasNext())
                        {
                            JetspeedPrincipal p = (JetspeedPrincipal) refs.userMap.get((String) _itTemp.next());
                            if (p != null)
                                pm.grantPermission(perm, p);
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
        if (cpe == null)
        {
            System.err.println("Error!!! CredentialPasswordEncoder not available");
            return ENCODING_STRING;
        }
        try
        {
            return cpe.encode(JETSPEED, ENCODING_STRING);
        }
        catch (SecurityException e)
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

    /**
     * Collect all the principals from the current environment.
     * 
     * @throws SerializerException
     * @throws SecurityException 
     */
    private void exportJetspeedPrincipals(ExportRefs refs, JSSnapshot snapshot, Map settings, Log log)
            throws SerializerException, SecurityException
    {
        /** set the security provider info in the snapshot file */
        snapshot.setEncryption(getEncryptionString());
        
        Map<String, JetspeedPrincipalType> principalTypeMap = principalManagerProvider.getPrincipalTypeMap();
        
        for (Map.Entry<String, JetspeedPrincipalType> entry : principalTypeMap.entrySet())
        {
            JetspeedPrincipalType principalType = entry.getValue();
            JetspeedPrincipalManager jpm = principalManagerProvider.getManager(principalType);
            List<? extends JetspeedPrincipal> principals = jpm.getPrincipals("");
            
            for (JetspeedPrincipal principal : principals)
            {
                Map<String, JSPrincipal> refMap = refs.getPrincipalMap(principal.getType().getName());
                JSPrincipal _tmpPrincipal = refMap.get(principal.getName());
                
                if (_tmpPrincipal == null)
                {
                    _tmpPrincipal = createJSPrincipal(principal);
                    refs.getPrincipalMap(principal.getType().getName()).put(_tmpPrincipal.getName(), _tmpPrincipal);
                    snapshot.getJetspeedPrincipals().add(_tmpPrincipal);
                }
            }
        }
    }
    
    /**
     * Collect all the roles, groups and users from the current environment.
     * Include the current SecurityProvider to understand, whether the password
     * collected can be used upon import
     * 
     * @throws SerializerException
     * @throws SecurityException 
     */
    private void exportRolesGroupsUsers(ExportRefs refs, JSSnapshot snapshot, Map settings, Log log)
            throws SerializerException, SecurityException
    {
        /** set the security provider info in the snapshot file */
        snapshot.setEncryption(getEncryptionString());
        for (Role role : roleManager.getRoles(""))
        {
            try
            {
                JSRole _tempRole = (JSRole) getObjectBehindPrinicpal(refs.roleMap, role);
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
        for (Group group : groupManager.getGroups(""))
        {

            try
            {
                JSGroup _tempGroup = (JSGroup) getObjectBehindPrinicpal(refs.groupMap, group);
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

        for (User user : userManager.getUsers(""))
        {
            try
            {
                JSUser _tempUser = createJSUser(refs, user);
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
    // TODO: uncomment and fix after permission refactoring
//    private void exportPermissions(ExportRefs refs, JSSnapshot snapshot, Map settings, Log log) throws SerializerException, SecurityException
//    {
//        for (InternalPermission p : pm.getInternalPermissions())
//        {
//            try
//            {
//                JSPermission _js = new JSPermission();
//                _js.setResource(p.getName());
//                _js.setActions(p.getActions());
//                _js.setId(p.getPermissionId());
//                _js.setType(_js.getTypeForClass(p.getClassname()));
//
//                Iterator<InternalPrincipal> list2 = p.getPrincipals().iterator();
//                while (list2.hasNext())
//                {
//                    InternalPrincipal o = list2.next();
//                    InternalPrincipal principal = (InternalPrincipal) o;
//                    if (principal.getType().equals(InternalRolePrincipal.ROLE_TYPE))
//                    {
//                        JSRole _tempRole = (JSRole) this.getObjectBehindPath(refs.roleMap, principal.getName());
//                        if (_tempRole != null)
//                        {
//                            _js.addRole(_tempRole);
//                        }
//                    }
//                    else if (principal.getType().equals(InternalGroupPrincipal.GROUP_TYPE))
//                    {
//                        JSGroup _tempGroup = (JSGroup) this.getObjectBehindPath(refs.groupMap, principal.getName());
//                        if (_tempGroup != null)
//                        {
//                            _js.addGroup(_tempGroup);
//                        }
//                    }
//                    else if (principal.getType().equals(InternalUserPrincipal.USER_TYPE))
//                    {
//                        JSUser _tempUser = (JSUser) this.getObjectBehindPath(refs.userMap, principal.getName());
//                        if (_tempUser != null)
//                        {
//                            _js.addUser(_tempUser);
//                        }
//                    }
//                }
//                refs.permissionMap.put(_js.getType(), _js);
//                snapshot.getPermissions().add(_js);
//            }
//            catch (Exception e)
//            {
//                throw new SerializerException(SerializerException.CREATE_SERIALIZED_OBJECT_FAILED.create(new String[] {
//                        "Permissions", e.getMessage() }));
//            }
//        }
//    }

    /**
     * simple lookup for principal object from a map
     * 
     * @param map
     * @param _fullPath
     * @return
     */

    private Object getObjectBehindPrinicpal(Map map, Principal principal)
    {
        return getObjectBehindPath(map, principal.getName());
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
    
    private JSPrincipal createJSPrincipal(JetspeedPrincipal principal)
    {
        JSPrincipal _jsPrincipal = new JSPrincipal();
        _jsPrincipal.setType(principal.getType().getName());
        _jsPrincipal.setName(principal.getName());
        _jsPrincipal.setMapped(principal.isMapped());
        _jsPrincipal.setEnabled(principal.isEnabled());
        _jsPrincipal.setReadonly(principal.isReadOnly());
        _jsPrincipal.setRemovable(principal.isRemovable());
        _jsPrincipal.setSecurityAttributes(principal.getSecurityAttributes().getAttributeMap(SecurityAttribute.JETSPEED_CATEGORY));
        _jsPrincipal.setInfoAttributes(principal.getSecurityAttributes().getInfoAttributeMap());
        return _jsPrincipal;
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
        _role.setName(role.getName());
        return _role;
    }

    /**
     * create a wrapper JSGroup object
     */
    private JSGroup createJSGroup(Group group)
    {
        JSGroup _group = new JSGroup();
        _group.setName(group.getName());
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

    private void addJSUserCredentials(boolean isPublic, JSUser newUser, Credential credential)
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
     * @throws SecurityException 
     */
    private JSUser createJSUser(ExportRefs refs, User user) throws SecurityException
    {
        JSUser _newUser = new JSUser();
        Subject subject = userManager.getSubject(user);
        for (Principal principal : subject.getPrincipals())
        {
            if (principal instanceof Role)
            {
                JSRole _tempRole = (JSRole) this.getObjectBehindPath(refs.roleMap, principal.getName());
                if (_tempRole != null)
                {
                    _newUser.addRole(_tempRole);
                }

            }
            else if (principal instanceof Group)
            {
                JSGroup _tempGroup = (JSGroup) this.getObjectBehindPath(refs.groupMap, principal.getName());
                if (_tempGroup != null)
                {
                    _newUser.addGroup(_tempGroup);
                }
            }
            else if (principal instanceof User)
            {
                _newUser.setPrincipal(principal);
            }
        }
        for (Object o : subject.getPublicCredentials())
        {
            Credential credential = (Credential)o;
            addJSUserCredentials(true, _newUser, credential);
        }
        for (Object o : subject.getPrivateCredentials())
        {
            Credential credential = (Credential)o;
            addJSUserCredentials(false, _newUser, credential);
        }
        _newUser.setSecurityAttributes(user.getSecurityAttributes().getAttributeMap(SecurityAttribute.JETSPEED_CATEGORY));
        _newUser.setUserInfo(user.getSecurityAttributes().getInfoAttributeMap());
        return _newUser;
    }
}
