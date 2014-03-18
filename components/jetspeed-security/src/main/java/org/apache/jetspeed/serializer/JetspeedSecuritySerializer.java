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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;

import org.slf4j.Logger;
import org.apache.jetspeed.security.Credential;
import org.apache.jetspeed.security.CredentialPasswordEncoder;
import org.apache.jetspeed.security.Group;
import org.apache.jetspeed.security.GroupManager;
import org.apache.jetspeed.security.JetspeedPermission;
import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalAssociationType;
import org.apache.jetspeed.security.JetspeedPrincipalManager;
import org.apache.jetspeed.security.JetspeedPrincipalManagerProvider;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.PermissionFactory;
import org.apache.jetspeed.security.PermissionManager;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.SecurityAttributes;
import org.apache.jetspeed.security.SecurityDomain;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.impl.SecurityDomainImpl;
import org.apache.jetspeed.security.spi.SecurityDomainAccessManager;
import org.apache.jetspeed.security.spi.SecurityDomainStorageManager;
import org.apache.jetspeed.security.spi.impl.SynchronizationStateAccess;
import org.apache.jetspeed.serializer.objects.JSGroup;
import org.apache.jetspeed.serializer.objects.JSNVPElement;
import org.apache.jetspeed.serializer.objects.JSNVPElements;
import org.apache.jetspeed.serializer.objects.JSPermission;
import org.apache.jetspeed.serializer.objects.JSPermissions;
import org.apache.jetspeed.serializer.objects.JSPrincipal;
import org.apache.jetspeed.serializer.objects.JSPrincipalAssociation;
import org.apache.jetspeed.serializer.objects.JSRole;
import org.apache.jetspeed.serializer.objects.JSSecurityAttributes;
import org.apache.jetspeed.serializer.objects.JSSecurityDomain;
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
    private static String ENCODING_STRING = "JETSPEED-SERIALIZER-ENCODING";
    private static String JETSPEED = "JETSPEED";

    // legacy user info keys
    private static final String USER_INFO_SUBSITE = "subsite";

    private static class ImportRefs
    {
        private HashMap<String, HashMap<String, Principal>> principalMapByType = new HashMap<String, HashMap<String, Principal>>();
        
        public HashMap<String, Principal> getPrincipalMap(String principalTypeName)
        {
            HashMap<String, Principal> principalMap = principalMapByType.get(principalTypeName);
            if (principalMap == null)
            {
                principalMap = new HashMap<String, Principal>();
                principalMapByType.put(principalTypeName, principalMap);
            }
            return principalMap;
        }
    }
    
    private static class ExportRefs
    {
        private HashMap<String, HashMap<String, JSPrincipal>> principalMapByType = new HashMap<String, HashMap<String, JSPrincipal>>();
        
        public HashMap<String, JSPrincipal> getPrincipalMap(String principalTypeName)
        {
            HashMap<String, JSPrincipal> jsPrincipalMap = principalMapByType.get(principalTypeName);
            if (jsPrincipalMap == null)
            {
                jsPrincipalMap = new HashMap<String, JSPrincipal>();
                principalMapByType.put(principalTypeName, jsPrincipalMap);
            }
            return jsPrincipalMap;
        }
    }

    protected SecurityDomainStorageManager domainStorageManager; 
    protected SecurityDomainAccessManager domainAccessManager;
    protected JetspeedPrincipalManagerProvider principalManagerProvider;
    protected GroupManager groupManager;
    protected RoleManager roleManager;
    protected UserManager userManager;
    protected CredentialPasswordEncoder cpe;
    protected PermissionManager pm;
    
    public JetspeedSecuritySerializer(JetspeedPrincipalManagerProvider principalManagerProvider, GroupManager groupManager, RoleManager roleManager, UserManager userManager,
            CredentialPasswordEncoder cpe, PermissionManager pm, SecurityDomainStorageManager sdsm, SecurityDomainAccessManager sdam )
    {
        this.principalManagerProvider = principalManagerProvider;
        this.groupManager = groupManager;
        this.roleManager = roleManager;
        this.userManager = userManager;
        this.cpe = cpe;
        this.pm = pm;
        this.domainAccessManager=sdam;
        this.domainStorageManager=sdsm;
    }

    protected void processExport(JSSnapshot snapshot, Map<String,Object> settings, Logger log) throws SerializerException
    {
        if (isSettingSet(settings, JetspeedSerializer.KEY_PROCESS_USERS))
        {
            try
            {
                log.info("collecting principals and principal associations");
                ExportRefs refs = new ExportRefs();
                exportJetspeedPrincipals(refs, snapshot, settings, log);
                exportJetspeedPrincipalAssociations(refs, snapshot, settings, log);
                
                if (isSettingSet(settings, JetspeedSerializer.KEY_PROCESS_PERMISSIONS))
                {
                    log.info("collecting permissions");
                    exportPermissions(refs, snapshot, settings, log);
                }
            }
            catch (SecurityException se)
            {
                throw new SerializerException(se);
            }
        }
    }

    protected void processImport(JSSnapshot snapshot, Map<String,Object> settings, Logger log) throws SerializerException
    {
        if (isSettingSet(settings, JetspeedSerializer.KEY_PROCESS_USERS))
        {
            log.info("creating principals and permissions");
            try
            {
                SynchronizationStateAccess.setSynchronizing(Boolean.TRUE);
                ImportRefs refs = new ImportRefs();
                
                recreateSecurityDomains(refs, snapshot, settings, log);
                recreateJetspeedPrincipals(refs, snapshot, settings, log);
                recreateJetspeedPrincipalAssociations(refs, snapshot, settings, log);

                if (isSettingSet(settings, JetspeedSerializer.KEY_PROCESS_PERMISSIONS))
                {
                    log.info("creating permissions");
                    recreatePermissions(refs, snapshot, settings, log);
                }
            }
            finally
            {
                SynchronizationStateAccess.setSynchronizing(Boolean.FALSE);
            }
        }
    }

    protected void deleteData(Map<String,Object> settings, Logger log) throws SerializerException
    {
        if (isSettingSet(settings, JetspeedSerializer.KEY_PROCESS_USERS))
        {
            log.info("deleting users/roles/groups and permissions");
            try
            {
                SynchronizationStateAccess.setSynchronizing(Boolean.TRUE);
                for (JetspeedPermission permission : pm.getPermissions())
                {
                    pm.removePermission(permission);
                }                
                
                String anonymousUser = userManager.getAnonymousUser();
                boolean userType;
                
                for (JetspeedPrincipalType type : principalManagerProvider.getPrincipalTypeMap().values())
                {
                    String typeName = type.getName();
                    userType = JetspeedPrincipalType.USER.equals(typeName);
                    
                    JetspeedPrincipalManager principalManager = principalManagerProvider.getManager(type);
                    
                    for (JetspeedPrincipal principal : principalManager.getPrincipals(""))
                    {
                        if (!(userType && anonymousUser.equals(principal.getName())))
                        {
                            principalManager.removePrincipal(principal);
                        }
                    }
                }
            }
            catch (Exception e)
            {
                throw new SerializerException(e);
            }
            finally
            {
                SynchronizationStateAccess.setSynchronizing(Boolean.FALSE);
            }
        }
    }

    protected SecurityDomain checkDomainExistsOtherwiseCreate(String domainName) throws SecurityException{
        SecurityDomain domain = domainAccessManager.getDomainByName(domainName);
        if (domain == null){
            SecurityDomainImpl newDomain = new SecurityDomainImpl();
            newDomain.setName(domainName);
            newDomain.setEnabled(true);
            newDomain.setRemote(false);
            
            domainStorageManager.addDomain(newDomain);
            domain = domainAccessManager.getDomainByName(domainName);
        }
        return domain;
    }
    
    private void recreateSecurityDomains(ImportRefs refs, JSSnapshot snapshot, Map<String,Object> settings, Logger log) throws SerializerException {
        log.debug("recreateSecurityDomains");
    
     // create system and default domain. Adding them to the seed is not necessary!           
        Long systemDomainId=null;            
        Long defaultDomainId=null;
        try{
            defaultDomainId=checkDomainExistsOtherwiseCreate(SecurityDomain.DEFAULT_NAME).getDomainId();
            systemDomainId=checkDomainExistsOtherwiseCreate(SecurityDomain.SYSTEM_NAME).getDomainId();
        } catch (Exception e){
            throw new SerializerException(SerializerException.CREATE_OBJECT_FAILED.create(new String[] { "SecurityDomains",
                    "Could not create default and / or system domains!\n"+e.getMessage() }), e);
        }
        
        if (snapshot.getSecurityDomains() != null && snapshot.getSecurityDomains().size() > 0){
            
            // sort the domains according to whether they have an owner domain
            // domains without owner domains ( = base or parent domains) should be created first
            ArrayList<JSSecurityDomain> sortedDomains = new ArrayList<JSSecurityDomain>(snapshot.getSecurityDomains());
            
            Collections.sort(sortedDomains, new Comparator<JSSecurityDomain>(){
                public int compare(JSSecurityDomain o1, JSSecurityDomain o2)
                {
                    boolean o1HasOwner = o1.getOwnerDomain() != null;
                    boolean o2HasOwner = o2.getOwnerDomain() != null;
                    
                    if (o1HasOwner==o2HasOwner){
                        return 0;
                    } else if (o1HasOwner){
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });
            
            
            
            // create other domains
            for (JSSecurityDomain jsDomain : sortedDomains){      
                // do some checks first
                
                // if domain is the system domain or the default domain, skip creation (they exist already)
                if (jsDomain.getName().equals(SecurityDomain.SYSTEM_NAME) || jsDomain.getName().equals(SecurityDomain.DEFAULT_NAME)){
                    break;
                }
                if (jsDomain.getName().length() == 0){
                    throw new SerializerException(SerializerException.CREATE_OBJECT_FAILED.create(new String[] { "SecurityDomain",
                            "Name of Security Domain must not be empty!" }));
                }
                Long ownerDomainId = null;
                if (jsDomain.getOwnerDomain() != null){                    
                    if (jsDomain.getOwnerDomain().equals(SecurityDomain.SYSTEM_NAME)){
                        ownerDomainId=defaultDomainId;
                    } else if (jsDomain.getOwnerDomain().equals(SecurityDomain.SYSTEM_NAME)) {
                        ownerDomainId=systemDomainId;
                    } else {
                        SecurityDomain ownerDomain = domainAccessManager.getDomainByName(jsDomain.getOwnerDomain());
                        if (ownerDomain == null){
                            throw new SerializerException(SerializerException.CREATE_OBJECT_FAILED.create(new String[] { "SecurityDomain","Could not find owner domain with name "+jsDomain.getOwnerDomain()+"for domain with name "+jsDomain.getName()}));
                        }
                        ownerDomainId=ownerDomain.getDomainId();
                    }
                } else {
                    // remote domains always need an owner domain. Set the default domain if owner domain is not specified
                    if (jsDomain.isRemote()){
                        ownerDomainId=defaultDomainId;
                    }
                }
                
                SecurityDomainImpl newDomain = new SecurityDomainImpl();
                newDomain.setName(jsDomain.getName());
                newDomain.setOwnerDomainId(ownerDomainId);
                newDomain.setRemote(jsDomain.isRemote());
                newDomain.setEnabled(jsDomain.isEnabled());
                try{
                    domainStorageManager.addDomain(newDomain);    
                  } catch (Exception e){
                  throw new SerializerException(SerializerException.CREATE_OBJECT_FAILED.create(new String[] { "SecurityDomain",
                          e.getMessage() }), e);
              }
                
            }
            
        }
    }
    
    /**
     * import the groups, roles and finally the users to the current environment
     * 
     * @throws SerializerException
     */
    private void recreateJetspeedPrincipals(ImportRefs refs, JSSnapshot snapshot, Map<String,Object> settings, Logger log)
            throws SerializerException
    {
        log.debug("recreateJetspeedPrincipals");
        
        log.debug("processing old groups");
        
        for (JSGroup jsGroup : snapshot.getOldGroups())
        {
            String name = jsGroup.getName();
            
            try
            {
                if (!(groupManager.groupExists(name)))
                    groupManager.addGroup(name);
                Group group = groupManager.getGroup(name);
                refs.getPrincipalMap(JetspeedPrincipalType.GROUP).put(name, group);
            }
            catch (Exception e)
            {
                throw new SerializerException(SerializerException.CREATE_OBJECT_FAILED.create(new String[] { "Group",
                        e.getMessage() }), e);
            }
        }
        
        log.debug("recreateOldGroups - done");
        
        log.debug("processing old roles");

        for (JSRole jsRole : snapshot.getOldRoles())
        {
            String name = jsRole.getName();
            try
            {
                if (!(roleManager.roleExists(name)))
                    roleManager.addRole(name);
                Role role = roleManager.getRole(name);
                refs.getPrincipalMap(JetspeedPrincipalType.ROLE).put(name, role);
            }
            catch (Exception e)
            {
                throw new SerializerException(SerializerException.CREATE_OBJECT_FAILED.create(new String[] { "Role",
                        e.getMessage() }));
            }
        }
        
        log.debug("recreateOldRoles - done");
        
        /** determine whether passwords can be reconstructed or not */
        int passwordEncoding = compareCurrentSecurityProvider(snapshot);
                
        log.debug("processing old users");

        for (JSUser jsuser : snapshot.getOldUsers())
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
                    boolean doPwData = jsuser.getPwData() != null;
                    if (user == null) // create new one
                    {
                        log.debug("add User " + jsuser.getName());
                        user = userManager.addUser(jsuser.getName());
                        if (doPwData)
                        {
                            String pwdString = (jsuser.getPwDataValue("password"));
                            char [] pwdChars = (pwdString != null ? pwdString.toCharArray() : null);
                            String password = recreatePassword(pwdChars);
                            
                            if (password != null && password.length() > 0)
                            {
                                PasswordCredential pwc = userManager.getPasswordCredential(user);
                                pwc.setPassword(password, (passwordEncoding == JetspeedSerializer.PASSTHRU_REQUIRED));
                                log.debug("storing password for User " + jsuser.getName());
                                userManager.storePasswordCredential(pwc);
                            }
                        }
                        log.debug("add User done ");
                    }
                    if (doPwData)
                    {
                        try
                        {
                            PasswordCredential pwc = userManager.getPasswordCredential(user);
                            pwc.setEnabled(jsuser.getPwDataValueAsBoolean("enabled"));
                            pwc.setUpdateRequired(jsuser.getPwDataValueAsBoolean("requiresUpdate"));
                            java.sql.Date d = jsuser.getPwExpirationDate();
                            if (d != null)
                                pwc.setExpirationDate(d);
                            userManager.storePasswordCredential(pwc);
                        }
                        catch (Exception e)
                        {
                            // most likely caused by protected users (like "guest")
                            log.error("setting userinfo for " + jsuser.getName() + " failed because of "
                                    + e.getLocalizedMessage());
                        }
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
                            groupManager.addUserToGroup(jsuser.getName(), _itTemp.next());
                        }
                    }
                    JSUserRoles jsUserRoles = jsuser.getRoleString();
                    List<String> listUserRoles = null;
                    if (jsUserRoles != null)
                    {
                        listUserRoles = getTokens(jsUserRoles.toString());
                    }
                    if ((listUserRoles != null) && (listUserRoles.size() > 0))
                    {
                        Iterator<String> _itTemp = listUserRoles.iterator();
                        while (_itTemp.hasNext())
                        {
                            roleManager.addRoleToUser(jsuser.getName(), _itTemp.next());
                        }
                    }
                    JSUserAttributes attributes = jsuser.getUserInfo();
                    if (attributes != null)
                    {
                        SecurityAttributes userSecAttrs = user.getSecurityAttributes();
                        
                        for (JSNVPElement element : attributes.getValues())
                        {
                            // assume old-style user info comes from 2.1.X exports: convert
                            // user info keys into equivalent 2.2.X security attribute keys
                            String userInfoKey = element.getKey();
                            String securityAttributeKey = userInfoKey;
                            if (userInfoKey.equals(USER_INFO_SUBSITE))
                            {
                                securityAttributeKey = User.JETSPEED_USER_SUBSITE_ATTRIBUTE;
                            }
                            String securityAttributeValue = element.getValue();
                            // set security attribute
                            userSecAttrs.getAttribute(securityAttributeKey, true).setStringValue(securityAttributeValue);
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
                    refs.getPrincipalMap(JetspeedPrincipalType.USER).put(jsuser.getName(), user);
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
        log.debug("recreateOldUsers - done");
        
        log.debug("processing jetspeed principals");
        
        JetspeedPrincipalManager principalManager = null;
        
        for (JSPrincipal jsPrincipal : snapshot.getPrincipals())
        {
            String typeName = jsPrincipal.getType();
            if (JetspeedPrincipalType.USER.equals(typeName))
            {
                recreateUserPrincipal(refs, snapshot, settings, log, jsPrincipal, passwordEncoding);
            }
            else
            {
                String name = jsPrincipal.getName();
                
                try
                {
                    JetspeedPrincipalType type = this.principalManagerProvider.getPrincipalType(typeName);
                    principalManager = this.principalManagerProvider.getManager(type);
                    JetspeedPrincipal principal = null;
                    
                    if (!(principalManager.principalExists(name)))
                    {
                        principal = principalManager.newPrincipal(name, jsPrincipal.isMapped());
                        JSSecurityAttributes jsSecAttrs = jsPrincipal.getSecurityAttributes();
                        if (jsSecAttrs != null)
                        {
                            for (JSNVPElement elem : jsSecAttrs.getValues())
                            {
                                principal.getSecurityAttributes().getAttribute(elem.getKey(), true).setStringValue(elem.getValue());
                            }
                        }
                        principalManager.addPrincipal(principal, null);
                    }
                    
                    principal = principalManager.getPrincipal(name);
                    refs.getPrincipalMap(typeName).put(name, principal);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    throw new SerializerException(SerializerException.CREATE_OBJECT_FAILED.create(new String[] { typeName,
                            e.getMessage() }), e);
                }
            }
        }
        
        log.debug("recreate jetspeed principals - done");
    }
    
    private void recreateUserPrincipal(ImportRefs refs, JSSnapshot snapshot, Map<String,Object> settings, Logger log, JSPrincipal jsuser, int passwordEncoding)
    throws SerializerException
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
                boolean doPwData = jsuser.getPwData() != null;
                if (user == null) // create new one
                {
                    log.debug("add User " + jsuser.getName());
                    user = userManager.addUser(jsuser.getName(), jsuser.isMapped());
                    
                    if (doPwData)
                    {
                        String pwdString = jsuser.getPwDataValue("password");
                        char [] pwdChars = (pwdString != null ? pwdString.toCharArray() : null);
                        String password = recreatePassword(pwdChars);
                        
                        if (password != null && password.length() > 0)
                        {
                            PasswordCredential pwc = userManager.getPasswordCredential(user);
                            pwc.setPassword(password, (passwordEncoding == JetspeedSerializer.PASSTHRU_REQUIRED));
                            log.debug("storing password for " + jsuser.getName());
                            userManager.storePasswordCredential(pwc);
                        }
                    }
                    log.debug("add User done ");
                }
                if (doPwData)
                {
                    try
                    {
                        PasswordCredential pwc = userManager.getPasswordCredential(user);
                        pwc.setEnabled(jsuser.getPwDataValueAsBoolean("enabled"));
                        pwc.setUpdateRequired(jsuser.getPwDataValueAsBoolean("requiresUpdate"));
                        java.sql.Date d = jsuser.getPwDataValueAsDate("expirationDate");
                        if (d != null)
                            pwc.setExpirationDate(d);
                        userManager.storePasswordCredential(pwc);
                    }
                    catch (Exception e)
                    {
                        // most likely caused by protected users (like "guest")
                        log.error("setting userinfo for " + jsuser.getName() + " failed because of "
                                + e.getLocalizedMessage());
                    }
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
                
                JSSecurityAttributes jsSecAttrs = jsuser.getSecurityAttributes();
                if (jsSecAttrs != null)
                {
                    for (JSNVPElement elem : jsSecAttrs.getValues())
                    {
                        user.getSecurityAttributes().getAttribute(elem.getKey(), true).setStringValue(elem.getValue());
                    }
                }
                refs.getPrincipalMap(JetspeedPrincipalType.USER).put(jsuser.getName(), user);
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

    private void recreateJetspeedPrincipalAssociations(ImportRefs refs, JSSnapshot snapshot, Map<String,Object> settings, Logger log)
            throws SerializerException
    {
        log.debug("recreateJetspeedPrincipalAssociations");
        
        Map<String, JetspeedPrincipalType> principalTypes = this.principalManagerProvider.getPrincipalTypeMap();
        JetspeedPrincipalManager principalManager = null;
        JetspeedPrincipalManager fromPrincipalManager = null;
        JetspeedPrincipal from = null;
        JetspeedPrincipal to = null;
        
        try
        {
            for (JSPrincipalAssociation jsAssoc : snapshot.getPrincipalAssociations())
            {
                principalManager = this.principalManagerProvider.getManager(principalTypes.get(jsAssoc.getToType()));
                to = principalManager.getPrincipal(jsAssoc.getToName());
                fromPrincipalManager = this.principalManagerProvider.getManager(principalTypes.get(jsAssoc.getFromType()));
                from = fromPrincipalManager.getPrincipal(jsAssoc.getFromName());
                principalManager.addAssociation(from, to, jsAssoc.getName());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new SerializerException(SerializerException.CREATE_OBJECT_FAILED.create(new String[] { "User",
                    e.getMessage() }));
        }
        
        log.debug("recreateJetspeedPrincipalAssociations - done");
    }
    
    /**
     * recreates all permissions from the current snapshot
     * 
     * @throws SerializerException
     */
    private void recreatePermissions(ImportRefs refs, JSSnapshot snapshot, Map<String,Object> settings, Logger log) throws SerializerException
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
                perm = pm.newPermission(PermissionFactory.PORTLET_PERMISSION, jsPermission.getResource(), jsPermission.getActions());
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
                    // TODO handle permission principals generically
                    List<String> listTemp = null;
                    JSUserGroups jsUserGroups = jsPermission.getGroupString();
                    if (jsUserGroups != null)
                        listTemp = getTokens(jsUserGroups.toString());
                    if ((listTemp != null) && (listTemp.size() > 0))
                    {
                        Iterator<String> _itTemp = listTemp.iterator();
                        while (_itTemp.hasNext())
                        {
                            JetspeedPrincipal p = (JetspeedPrincipal) refs.getPrincipalMap(JetspeedPrincipalType.GROUP).get(_itTemp.next());
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
                            JetspeedPrincipal p = (JetspeedPrincipal) refs.getPrincipalMap(JetspeedPrincipalType.ROLE).get(_itTemp.next());
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
                            JetspeedPrincipal p = (JetspeedPrincipal) refs.getPrincipalMap(JetspeedPrincipalType.USER).get(_itTemp.next());
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
     * Collect all the roles, groups and users from the current environment.
     * Include the current SecurityProvider to understand, whether the password
     * collected can be used upon import
     * 
     * @throws SerializerException
     * @throws SecurityException 
     */
    private void exportJetspeedPrincipals(ExportRefs refs, JSSnapshot snapshot, Map<String,Object> settings, Logger log)
            throws SerializerException, SecurityException
    {
        /** set the security provider info in the snapshot file */
        snapshot.setEncryption(getEncryptionString());
        
        for (Map.Entry<String, JetspeedPrincipalType> entry : this.principalManagerProvider.getPrincipalTypeMap().entrySet())
        {
            String typeName = entry.getKey();
            
            JetspeedPrincipalType type = this.principalManagerProvider.getPrincipalType(typeName);
            JetspeedPrincipalManager principalManager = this.principalManagerProvider.getManager(type);
            
            for (JetspeedPrincipal principal : principalManager.getPrincipals(""))
            {
                try
                {
                    JSPrincipal _tempPrincipal = createJSPrincipal(principal);
                    refs.getPrincipalMap(typeName).put(_tempPrincipal.getName(), _tempPrincipal);
                    snapshot.getPrincipals().add(_tempPrincipal);
                }
                catch (Exception e)
                {
                    throw new SerializerException(SerializerException.CREATE_SERIALIZED_OBJECT_FAILED.create(new String[] {
                            typeName, e.getMessage() }));
                }
            }
        }
    }

    private void exportJetspeedPrincipalAssociations(ExportRefs refs, JSSnapshot snapshot, Map<String,Object> settings, Logger log) throws SecurityException, SerializerException
    {
        Map<String, JetspeedPrincipalType> principalTypes = this.principalManagerProvider.getPrincipalTypeMap();
        Map<String, JetspeedPrincipalType> copiedPrincipalTypes = new HashMap<String, JetspeedPrincipalType>(principalTypes);
        JetspeedPrincipalManager principalManager = null;
        JetspeedPrincipalManager otherPrincipalManager = null;
        
        for (String principalTypeName : principalTypes.keySet())
        {
            principalManager = this.principalManagerProvider.getManager(this.principalManagerProvider.getPrincipalType(principalTypeName));
            
            for (JetspeedPrincipal principal : principalManager.getPrincipals(""))
            {
                Set<String> associationNames = new HashSet<String>();
                for (JetspeedPrincipalAssociationType assocType : principalManager.getAssociationTypes())
                {
                    String associationName = assocType.getAssociationName();
                    if (associationNames.add(associationName))
                    {
                        for (String otherPrincipalTypeName : copiedPrincipalTypes.keySet())
                        {
                            otherPrincipalManager = this.principalManagerProvider.getManager(this.principalManagerProvider.getPrincipalType(otherPrincipalTypeName));
                            
                            for (JetspeedPrincipal toPrincipal : otherPrincipalManager.getAssociatedFrom(principal.getName(), principal.getType(), associationName))
                            {
                                JSPrincipalAssociation jsAssoc = createJSPrincipalAssociation(associationName, principal, toPrincipal);
                                snapshot.addPrincipalAssociation(jsAssoc);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private JSPrincipalAssociation createJSPrincipalAssociation(String associationName, JetspeedPrincipal from, JetspeedPrincipal to)
    {
        JSPrincipalAssociation jsAssoc = new JSPrincipalAssociation();
        jsAssoc.setName(associationName);
        jsAssoc.setFromType(from.getType().getName());
        jsAssoc.setFromName(from.getName());
        jsAssoc.setToType(to.getType().getName());
        jsAssoc.setToName(to.getName());
        return jsAssoc;
    }
    
    /**
     * extract all permissions from the current environment
     * 
     * @throws SerializerException
     */
    private void exportPermissions(ExportRefs refs, JSSnapshot snapshot, Map<String,Object> settings, Logger log) throws SerializerException, SecurityException
    {
        for (JetspeedPermission perm : pm.getPermissions())
        {
            try
            {
                JSPermission _js = new JSPermission();
                _js.setResource(perm.getName());
                _js.setActions(perm.getActions());
                _js.setType(perm.getType());
                
                for (JetspeedPrincipal principal : pm.getPrincipals(perm))
                {
                    String principalTypeName = principal.getType().getName();
                    JSPrincipal jsPrincipal = refs.getPrincipalMap(principalTypeName).get(principal.getName());
                    if (jsPrincipal != null)
                    {
                        // TODO: handle permission principals generically
                        if (JetspeedPrincipalType.ROLE.equals(principalTypeName))
                        {
                            _js.addRole(jsPrincipal);
                        }
                        else if (JetspeedPrincipalType.GROUP.equals(principalTypeName))
                        {
                            _js.addGroup(jsPrincipal);
                        }
                        else if (JetspeedPrincipalType.USER.equals(principalTypeName))
                        {
                            _js.addUser(jsPrincipal);
                        }                    
                    }
                }
                
                snapshot.getPermissions().add(_js);
            }
            catch (Exception e)
            {
                throw new SerializerException(SerializerException.CREATE_SERIALIZED_OBJECT_FAILED.create(new String[] {
                        "Permissions", e.getMessage() }));
            }
        }
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

    private void addJSPrincipalCredentials(boolean isPublic, JSPrincipal newPrincipal, Credential credential)
    {
        if (credential == null)
            return;
        if (credential instanceof PasswordCredential)
        {
            PasswordCredential pw = (PasswordCredential) credential;
            char [] pwdChars = (pw.getPassword() != null ? pw.getPassword().toCharArray() : null);
            newPrincipal.setCredential(pw.getUserName(), pwdChars, pw.getExpirationDate(), pw.isEnabled(), 
                                      pw.isExpired(), pw.isUpdateRequired());
            return;
        }
        else if (isPublic)
            newPrincipal.addPublicCredential(credential);
        else
            newPrincipal.addPrivateCredential(credential);
    }
    
    private JSPrincipal createJSPrincipal(JetspeedPrincipal principal) throws SecurityException
    {
        JSPrincipal _jsPrincipal = new JSPrincipal();
        _jsPrincipal.setPrincipal(principal);
        _jsPrincipal.setType(principal.getType().getName());
        _jsPrincipal.setName(principal.getName());
        _jsPrincipal.setMapped(principal.isMapped());
        _jsPrincipal.setEnabled(principal.isEnabled());
        _jsPrincipal.setReadonly(principal.isReadOnly());
        _jsPrincipal.setRemovable(principal.isRemovable());
        _jsPrincipal.setExtendable(principal.isExtendable());
        
        if (JetspeedPrincipalType.USER.equals(principal.getType().getName()))
        {
            Credential credential = userManager.getPasswordCredential((User) principal);
            Subject subject = userManager.getSubject((User) principal);
            
            if (credential != null)
            {
                addJSPrincipalCredentials(true, _jsPrincipal, credential);
            }
            
            for (Object o : subject.getPublicCredentials())
            {
                credential = (Credential)o;
                addJSPrincipalCredentials(true, _jsPrincipal, credential);
            }
            
            for (Object o : subject.getPrivateCredentials())
            {
                credential = (Credential)o;
                addJSPrincipalCredentials(false, _jsPrincipal, credential);
            }
        }
        
        _jsPrincipal.setSecurityAttributes(principal.getSecurityAttributes().getAttributeMap());
        
        return _jsPrincipal;
    }
}
