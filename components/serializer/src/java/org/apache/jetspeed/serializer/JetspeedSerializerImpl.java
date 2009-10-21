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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.security.auth.Subject;

import javolution.xml.XMLBinding;

import org.apache.jetspeed.capabilities.Capabilities;
import org.apache.jetspeed.capabilities.Capability;
import org.apache.jetspeed.capabilities.Client;
import org.apache.jetspeed.capabilities.MediaType;
import org.apache.jetspeed.capabilities.MimeType;
import org.apache.jetspeed.components.ComponentManager;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.profiler.ProfilerException;
import org.apache.jetspeed.profiler.rules.PrincipalRule;
import org.apache.jetspeed.profiler.rules.ProfilingRule;
import org.apache.jetspeed.profiler.rules.RuleCriterion;
import org.apache.jetspeed.security.BasePrincipal;
import org.apache.jetspeed.security.Group;
import org.apache.jetspeed.security.GroupManager;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.PermissionManager;
import org.apache.jetspeed.security.PortalResourcePermission;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.om.InternalCredential;
import org.apache.jetspeed.security.om.InternalPermission;
import org.apache.jetspeed.security.om.InternalPrincipal;
import org.apache.jetspeed.security.om.InternalUserPrincipal;
import org.apache.jetspeed.security.spi.PasswordCredentialProvider;
import org.apache.jetspeed.serializer.objects.JSCapabilities;
import org.apache.jetspeed.serializer.objects.JSCapability;
import org.apache.jetspeed.serializer.objects.JSClient;
import org.apache.jetspeed.serializer.objects.JSClientCapabilities;
import org.apache.jetspeed.serializer.objects.JSClientMimeTypes;
import org.apache.jetspeed.serializer.objects.JSClients;
import org.apache.jetspeed.serializer.objects.JSGroup;
import org.apache.jetspeed.serializer.objects.JSGroups;
import org.apache.jetspeed.serializer.objects.JSMediaType;
import org.apache.jetspeed.serializer.objects.JSMediaTypes;
import org.apache.jetspeed.serializer.objects.JSMimeType;
import org.apache.jetspeed.serializer.objects.JSMimeTypes;
import org.apache.jetspeed.serializer.objects.JSNVPElements;
import org.apache.jetspeed.serializer.objects.JSPWAttributes;
import org.apache.jetspeed.serializer.objects.JSPermission;
import org.apache.jetspeed.serializer.objects.JSPermissions;
import org.apache.jetspeed.serializer.objects.JSPrincipalRule;
import org.apache.jetspeed.serializer.objects.JSPrincipalRules;
import org.apache.jetspeed.serializer.objects.JSProfilingRule;
import org.apache.jetspeed.serializer.objects.JSProfilingRules;
import org.apache.jetspeed.serializer.objects.JSRole;
import org.apache.jetspeed.serializer.objects.JSRoles;
import org.apache.jetspeed.serializer.objects.JSRuleCriterion;
import org.apache.jetspeed.serializer.objects.JSRuleCriterions;
import org.apache.jetspeed.serializer.objects.JSSeedData;
import org.apache.jetspeed.serializer.objects.JSSnapshot;
import org.apache.jetspeed.serializer.objects.JSSSOSite;
import org.apache.jetspeed.serializer.objects.JSSSOSiteRemoteUser;
import org.apache.jetspeed.serializer.objects.JSSSOSiteRemoteUsers;
import org.apache.jetspeed.serializer.objects.JSSSOSites;
import org.apache.jetspeed.serializer.objects.JSUser;
import org.apache.jetspeed.serializer.objects.JSUserAttributes;
import org.apache.jetspeed.serializer.objects.JSUserGroups;
import org.apache.jetspeed.serializer.objects.JSUserRoles;
import org.apache.jetspeed.serializer.objects.JSUserUsers;
import org.apache.jetspeed.serializer.objects.JSUsers;
import org.apache.jetspeed.sso.SSOException;
import org.apache.jetspeed.sso.SSOProvider;
import org.apache.jetspeed.sso.SSOSite;

/**
 * Jetspeed Serializer
 * <p>
 * The Serializer is capable of reading and writing the current content of the
 * Jetspeed environment to and from XML files. The component can be used from a
 * standalone java application for seeding a new database or from a running
 * portal as an administrative backup/restore function.
 * <p>
 * The XML file needs to indicate whether passwords used in credentials
 * are plain text or whether they are encoded. The import algoritm can determine -
 * prior to reading users - which encode/decode scheme was used and if <none> or
 * <implements PasswordEncodingService> then we store plain passwords (Note that
 * that alone requires the resulting XML to be encoded!!!!!)
 * 
 * @author <a href="mailto:hajo@bluesunrise.com">Hajo Birthelmer</a>
 * @version $Id: $
 */
public class JetspeedSerializerImpl extends JetspeedSerializerBase implements JetspeedSerializer
{

 
    private HashMap roleMap = new HashMap();

    private HashMap groupMap = new HashMap();

    private HashMap userMap = new HashMap();

    private HashMap mimeMap = new HashMap();
    private HashMap mimeMapInt = new HashMap();

    private HashMap mediaMap = new HashMap();

    private HashMap capabilityMap = new HashMap();
    private HashMap capabilityMapInt = new HashMap();

    private HashMap clientMap = new HashMap();

    private HashMap permissionMap = new HashMap();

    private HashMap rulesMap = new HashMap();

    private HashMap ssoSitesMap = new HashMap();

    int refCouter = 0;

 
    private static String ENCODING_STRING = "JETSPEED-SERIALIZER-ENCODING";
    private static String JETSPEED = "JETSPEED";
    
    
    protected Class getSerializerDataClass()
    {
    	return JSSeedData.class;
    }    

    protected String getSerializerDataTag()
    {
    	return TAG_SNAPSHOT;
    }    
    
    public JetspeedSerializerImpl()
    {
    	super();
    }

    /**
     * hand over existing component manager
     * 
     * @param cm
     */
    public JetspeedSerializerImpl(ComponentManager cm)
    {
    	super(cm);
    }

    /**
     * This constructor takes the application root, the search path for the boot
     * component configuration files and the search path for the application
     * component configuration files.
     * <p>
     * For example: new JetspeedSerializerImpl("./", "assembly/boot/*.xml",
     * "assembly/*.xml") will establish the current directory as the root,
     * process all xml files in the assembly/boot directory before processing
     * all xml files in the assembly directory itself.
     * 
     * @param appRoot
     *            working directory
     * @param bootConfig
     *            boot (primary) file or files (wildcards are allowed)
     * @param appConfig
     *            application (secondary) file or files (wildcards are allowed)
     */
    public JetspeedSerializerImpl(String appRoot, String[] bootConfig,
            String[] appConfig) throws SerializerException
    {
    	super(appRoot,bootConfig,appConfig);
    }

     /**
     * reset instruction flags to default settings (all true)
     * 
     */
    protected void resetSettings()
    {
        setSetting(JetspeedSerializer.KEY_PROCESS_USERS, true);
        setSetting(JetspeedSerializer.KEY_PROCESS_CAPABILITIES, true);
        setSetting(JetspeedSerializer.KEY_PROCESS_PROFILER, true);
        setSetting(JetspeedSerializer.KEY_PROCESS_PERMISSIONS, true);
        setSetting(JetspeedSerializer.KEY_PROCESS_SSO, true);
        setSetting(JetspeedSerializer.KEY_OVERWRITE_EXISTING, true);
        setSetting(JetspeedSerializer.KEY_BACKUP_BEFORE_PROCESS, true);
    }

 
    /**
     * On import, get the basic SnapShot data
     * 
     */
    protected void getSnapshotData()
    {
        logMe("date created : " + getSnapshot().getDateCreated());
        logMe("software Version : " + getSnapshot().getSavedVersion());
        logMe("software SUbVersion : " + getSnapshot().getSavedSubversion());
    }

  
    
    private void recreateCapabilities (Capabilities caps) throws SerializerException
    {
    	logMe("recreateCapabilities - processing");
    	JSCapabilities capabilities = ((JSSeedData)getSnapshot()).getCapabilities();
    	if ((capabilities != null) && (capabilities.size() > 0))
    	{
    		Iterator _it = capabilities.iterator();
    		while (_it.hasNext())
    		{
    			JSCapability _c = (JSCapability)_it.next();
// create a new Capability
    			try
    			{
    				Capability capability = caps.createCapability(_c.getName());
					/** THE KEY_OVERWRITE_EXISTING test is not required for capabilites, since they carry no other information than the name
					 *  Used here for consistency, though
					 */   				
    				if ((this.getSetting(JetspeedSerializer.KEY_OVERWRITE_EXISTING)) || (capability.getCapabilityId() == 0))
    				{
    					caps.storeCapability(capability);
    				}
    				this.capabilityMap.put(_c.getName(), capability);
    			}
    			catch (Exception e)
    			{
    				throw new SerializerException(
    			            SerializerException.CREATE_OBJECT_FAILED
    			            	.create("org.apache.jetspeed.capabilities.Capabilities",e.getLocalizedMessage()));
 			}
    		}
    	}
    	else
    		logMe("NO CAPABILITES?????");
    	logMe("recreateCapabilities - done");
    }
    private void recreateMimeTypes (Capabilities caps) throws SerializerException
    {
    	logMe("recreateMimeTypes - processing");
    	JSMimeTypes mimeTypes = ((JSSeedData)getSnapshot()).getMimeTypes();
    	if ((mimeTypes != null) && (mimeTypes.size() > 0))
    	{
    		Iterator _it = mimeTypes.iterator();
    		while (_it.hasNext())
    		{
    			JSMimeType _c = (JSMimeType)_it.next();
// create a new Mime Type
    			try
    			{
    				MimeType mimeType = caps.createMimeType(_c.getName());
					/** THE KEY_OVERWRITE_EXISTING test is not required for mime types, since they carry no other information than the name
					 *  Used here for consistency, though
					 */   				
    				if ((this.getSetting(JetspeedSerializer.KEY_OVERWRITE_EXISTING)) || (mimeType.getMimetypeId() == 0))
    				{
    					caps.storeMimeType(mimeType);
    				}
    				this.mimeMap.put(_c.getName(), mimeType);

    			}
    			catch (Exception e)
    			{
    				throw new SerializerException(
    			            SerializerException.CREATE_OBJECT_FAILED
    			                    .create("org.apache.jetspeed.capabilities.MimeType",e.getLocalizedMessage()));
    			}
    		}
    	}
    	else
    		logMe("NO MIME TYPES?????");
    	logMe("recreateMimeTypes - done");
    }

    private void recreateMediaTypes (Capabilities caps) throws SerializerException
    {
    	 String _line;
    	 
    	logMe("recreateMediaTypes - processing");
    	JSMediaTypes mediaTypes = ((JSSeedData)getSnapshot()).getMediaTypes();
    	if ((mediaTypes != null) && (mediaTypes.size() > 0))
    	{
    		Iterator _it = mediaTypes.iterator();
    		while (_it.hasNext())
    		{
    			JSMediaType _c = (JSMediaType)_it.next();
// create a new Media
    			try
    			{
    				MediaType mediaType = caps.createMediaType(_c.getName());
					/** THE KEY_OVERWRITE_EXISTING test IS required for media types, since they carry no other information than the name
					 *  Used here for consistency, though
					 */   				
    				if ((this.getSetting(JetspeedSerializer.KEY_OVERWRITE_EXISTING)) || (mediaType.getMediatypeId() == 0))
    				{
//    					 set object fields               
    			        mediaType.setCharacterSet(_c.getCharacterSet());
    			        mediaType.setTitle(_c.getTitle());
    			        mediaType.setDescription(_c.getDescription());
    			       
    			        try
    			        {
    			        	_line = _c.getMimeTypesString().toString();
    			        	ArrayList list = this.getTokens(_line);
    			        	if ((list != null) && (list.size()>0))
    			        	{
    			        		Iterator _it1 = list.iterator();
	        			        int added = 0;
	        			        while (_it1.hasNext())
	        			        {
	        			        	MimeType _mt = caps.createMimeType((String)_it1.next());
	        			        	if (_mt != null)
	        			        		mediaType.addMimetype(_mt);
	        			        	added++;
	        			        }
    			        	}
    			        }
    			        catch (Exception e1)
    			        {
    			        	e1.printStackTrace();
    			        }
    			        try
    			        {
    			        	_line  = _c.getCapabilitiesString().toString();
    			        	ArrayList list = this.getTokens(_line);
    			        	if ((list != null) && (list.size()>0))
    			        	{
	    			        	Iterator _it1 = list.iterator();
	    			        	if ((list != null) && (list.size()>0))
	    			        	{
		        			        int added = 0;
		        			        while (_it1.hasNext())
		        			        {
		        			        	Capability _ct = caps.createCapability((String)_it1.next());
		        			        	if (_ct != null)
		        			        		mediaType.addCapability(_ct);
		        			        	added++;
		        			        }
	    			        	}
    			        	}
    			        }
    			        catch (Exception e1)
    			        {
    			        	e1.printStackTrace();
    			        }
    					caps.storeMediaType(mediaType);
    				}
    				this.mediaMap.put(_c.getName(), mediaType);
    			}
    			catch (Exception e)
    			{
    				throw new SerializerException(
    			            SerializerException.CREATE_OBJECT_FAILED
    			                    .create("org.apache.jetspeed.capabilities.MediaType",e.getLocalizedMessage()));
    			}
    		}
    	}
    	else
    		logMe("NO MEDIA TYPES?????");
    	logMe("recreateMediaTypes - done");
    }
 
    
    private void recreateClients (Capabilities caps) throws SerializerException
    {
    	 String _line;
    	 
    	logMe("recreateClients - processing");
    	JSClients clients = ((JSSeedData)getSnapshot()).getClients();
    	if ((clients != null) && (clients.size() > 0))
    	{
    		Iterator _it = clients.iterator();
    		while (_it.hasNext())
    		{
    			JSClient _c = (JSClient)_it.next();
// create a new Media
    			try
    			{
    				Client client = caps.createClient(_c.getName());
					/** THE KEY_OVERWRITE_EXISTING test IS required for media types, since they carry no other information than the name
					 *  Used here for consistency, though
					 */   				
    				if ((this.getSetting(JetspeedSerializer.KEY_OVERWRITE_EXISTING)) || (client.getClientId() == 0))
    				{
//    					 set object fields               
    			        client.setUserAgentPattern(_c.getUserAgentPattern());
    			        client.setManufacturer(_c.getManufacturer());
    			        client.setModel(_c.getModel());
    			        client.setEvalOrder(_c.getEvalOrder());
    			        String myPrefMimeType = _c.getPreferredMimeTypeID();
    			        client.setVersion(_c.getVersion());
    			        try
    			        {
    			        	_line = _c.getMimeTypesString().toString();
    			        	ArrayList list = this.getTokens(_line);
    			        	if ((list != null) && (list.size()>0))
    			        	{
    			        		Iterator _it1 = list.iterator();
	        			        int added = 0;
	        			        while (_it1.hasNext())
	        			        {
	        			        	MimeType _mt = caps.createMimeType((String)_it1.next());
	        			        	if (_mt != null)
	        			        	{
	        			        		client.getMimetypes().add(_mt);
	        			        		if (_mt.getMimetypeId() == 0)
	        			        		{
	        			        			caps.storeMimeType(_mt);
	        			        		}
	        			        		if (myPrefMimeType.equalsIgnoreCase(_mt.getName()))
	        			        				client.setPreferredMimeTypeId(_mt.getMimetypeId());	
	        			        			
	        			        	}
	        			        	added++;
	        			        }
    			        	}
    			        }
    			        catch (Exception e1)
    			        {
    			        	e1.printStackTrace();
    			        }
    			        try
    			        {
    			        	_line  = _c.getCapabilitiesString().toString();
    			        	ArrayList list = this.getTokens(_line);
    			        	if ((list != null) && (list.size()>0))
    			        	{
	    			        	Iterator _it1 = list.iterator();
	    			        	if ((list != null) && (list.size()>0))
	    			        	{
		        			        int added = 0;
		        			        while (_it1.hasNext())
		        			        {
		        			        	Capability _ct = caps.createCapability((String)_it1.next());
		        			        	if (_ct != null)
		        			        		client.getCapabilities().add(_ct);
		        			        	added++;
		        			        }
	    			        	}
    			        	}
    			        }
    			        catch (Exception e1)
    			        {
    			        	e1.printStackTrace();
    			        }
    					caps.storeClient(client);
    				}
    				this.clientMap.put(_c.getName(), client);
    			}
    			catch (Exception e)
    			{
    				throw new SerializerException(
    			            SerializerException.CREATE_OBJECT_FAILED
    			                    .create("org.apache.jetspeed.capabilities.Client",e.getLocalizedMessage()));
    			}
    		}
    	}
    	else
    		logMe("NO MEDIA TYPES?????");
    	logMe("recreateClients - done");
    }

    
    private void importCapabilitiesInfrastructure() throws SerializerException
    {
    	logMe("importCapabilitiesInfrastructure - processing");
        Capabilities caps = (Capabilities) getCM()
        .getComponent("org.apache.jetspeed.capabilities.Capabilities");
        if (caps == null)
        	throw new SerializerException(
            SerializerException.COMPONENTMANAGER_DOES_NOT_EXIST
                    .create("org.apache.jetspeed.capabilities.Capabilities"));
        
        recreateCapabilities(caps);
        recreateMimeTypes(caps);
        recreateMediaTypes(caps);
        recreateClients(caps);
        
        
    	logMe("importCapabilitiesInfrastructure - processing done");
    }


    /**
     * import the groups, roles and finally the users to the current environment
     * 
     * @throws SerializerException
     */
    private void recreateRolesGroupsUsers() throws SerializerException
    {
    	logMe("recreateRolesGroupsUsers");
        GroupManager groupManager = (GroupManager) getCM()
                .getComponent("org.apache.jetspeed.security.GroupManager");
        if (groupManager == null)
            throw new SerializerException(
                    SerializerException.COMPONENTMANAGER_DOES_NOT_EXIST
                            .create("org.apache.jetspeed.security.GroupManager"));
        RoleManager roleManager = (RoleManager) getCM()
        .getComponent("org.apache.jetspeed.security.RoleManager");
        if (roleManager == null)
            throw new SerializerException(
                    SerializerException.COMPONENTMANAGER_DOES_NOT_EXIST
                            .create("org.apache.jetspeed.security.RoleManager"));
        UserManager userManager = (UserManager) getCM()
        .getComponent("org.apache.jetspeed.security.UserManager");
        if (userManager == null)
        	throw new SerializerException(
            SerializerException.COMPONENTMANAGER_DOES_NOT_EXIST
                    .create("org.apache.jetspeed.security.UserManager"));

        
        
        
        JSGroups groups = null;
        JSRoles roles = null;
        Iterator _it = null;
        groups = ((JSSeedData)getSnapshot()).getGroups();
        if (groups != null)
        {
         _it = groups.iterator();
        while (_it.hasNext())
        {
        	String name = ((JSGroup)_it.next()).getName();

	        try
	        {
	        	if (!(groupManager.groupExists(name)))
	        		groupManager.addGroup(name);
	        	Group group = groupManager.getGroup(name);
	        	this.groupMap.put(name, group.getPrincipal());
	        } catch (Exception e)
	        {
	            throw new SerializerException(
	                    SerializerException.CREATE_OBJECT_FAILED
	                            .create(new String[]
	                            { "Group", e.getMessage()}));
	        }
        }
        }
    	logMe("recreateGroups - done");
    	logMe("processing roles");

        roles = ((JSSeedData)getSnapshot()).getRoles();
        if (roles!= null)
        {      
        _it = roles.iterator();
        
        while (_it.hasNext())
        {
        	String name = ((JSRole)_it.next()).getName();

	        try
	        {
	        	if (!(roleManager.roleExists(name)))
	        		roleManager.addRole(name);
	        	Role role = roleManager.getRole(name);
	        	this.roleMap.put(name, role.getPrincipal());
	        } catch (Exception e)
	        {
	            throw new SerializerException(
	                    SerializerException.CREATE_OBJECT_FAILED
	                            .create(new String[]
	                            { "Role", e.getMessage()}));
	        }
        }
        }
        logMe("recreateRoles - done");
    	logMe("processing users");

    	/** determine whether passwords can be reconstructed or not */
    	int passwordEncoding = compareCurrentSecurityProvider((JSSeedData)getSnapshot());
        JSUsers users = null;
        users = ((JSSeedData)getSnapshot()).getUsers();
        if(users!=null)
        {_it = users.iterator();
        while (_it.hasNext())
        {
        	
        	JSUser jsuser = (JSUser)_it.next();

	        try
	        {
	        	User user = null;
	        	if (userManager.userExists(jsuser.getName()))
	        	{
	        		user = userManager.getUser(jsuser.getName());
	        	}
				if ((this.getSetting(JetspeedSerializer.KEY_OVERWRITE_EXISTING)) || (user == null))
				{
					if (user == null) //create new one
					{
    					String password = recreatePassword(jsuser.getPassword());
    			    	logMe("add User "+ jsuser.getName() + " with password " + password);
   			    		userManager.importUser(jsuser.getName(), password,(passwordEncoding == PASSTHRU_REQUIRED));
    			    	logMe("add User done ");
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
						logMe("setting userinfo for "+ jsuser.getName() + " failed because of " + e.getLocalizedMessage());
					}
					
				//credentials
			        Subject subject = user.getSubject();

					ArrayList listTemp = jsuser.getPrivateCredentials();
					if ((listTemp != null) && (listTemp.size()>0))
					{
						Iterator _itTemp = listTemp.iterator();
						while (_itTemp.hasNext())
						{
							subject.getPrivateCredentials().add(_itTemp.next());
						}
					}
					listTemp = jsuser.getPublicCredentials();
					if ((listTemp != null) && (listTemp.size()>0))
					{
						Iterator _itTemp = listTemp.iterator();
						while (_itTemp.hasNext())
						{
							subject.getPublicCredentials().add(_itTemp.next());
						}
					}
					JSUserGroups jsUserGroups = jsuser.getGroupString();
					if (jsUserGroups != null)
						listTemp = this.getTokens(jsUserGroups.toString());
					else
						listTemp = null;
					if ((listTemp != null) && (listTemp.size()>0))
					{
						Iterator _itTemp = listTemp.iterator();
						while (_itTemp.hasNext())
						{
							groupManager.addUserToGroup(jsuser.getName(), (String)_itTemp.next());
						}
					}
					JSUserRoles jsUserRoles = jsuser.getRoleString();
					if (jsUserRoles != null)
						listTemp = this.getTokens(jsUserRoles.toString());
					else
						listTemp = null;
					if ((listTemp != null) && (listTemp.size()>0))
					{
						Iterator _itTemp = listTemp.iterator();
						while (_itTemp.hasNext())
						{
							roleManager.addRoleToUser(jsuser.getName(), (String)_itTemp.next());
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
						         String userAttrName = (String)_itTemp.next();
//						         if ( userAttributes.get(userAttrName, "").equals("") 
						         String userAttrValue = (String)map.get(userAttrName);
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
							String prefKey = (String)_itTemp.next();
							String prefValue = (String)(jsNVP.getMyMap().get(prefKey));
							preferences.put(prefKey,prefValue);
						}
					}
		        	
		        	this.userMap.put(jsuser.getName(), getUserPrincipal(user));

				}    					
	        } catch (Exception e)
	        {
	            e.printStackTrace();
                throw new SerializerException(
	                    SerializerException.CREATE_OBJECT_FAILED
	                            .create(new String[]
	                            { "User", e.getMessage()}));
	        }
        }
        }
    	logMe("recreateUsers - done");
    	return;
    }
    
    /**
     * called only after users have been established
     * @throws SerializerException
     */
    private void recreateUserPrincipalRules() throws SerializerException
    {
    	logMe("recreateUserPrincipalRules - started");
    	
        Profiler pm = (Profiler) getCM()
        .getComponent("org.apache.jetspeed.profiler.Profiler");
        if (pm == null)

        	throw new SerializerException(
            SerializerException.COMPONENTMANAGER_DOES_NOT_EXIST
                    .create("org.apache.jetspeed.profiler.Profiler"));
        UserManager userManager = (UserManager) getCM()
        .getComponent("org.apache.jetspeed.security.UserManager");
        if (userManager == null)
        	throw new SerializerException(
            SerializerException.COMPONENTMANAGER_DOES_NOT_EXIST
                    .create("org.apache.jetspeed.security.UserManager"));

        // get Rules for each user

        Iterator _itUsers = ((JSSeedData)getSnapshot()).getUsers().iterator();
        while (_itUsers.hasNext())
        {
            JSUser _user = (JSUser) _itUsers.next();
            JSPrincipalRules jsRules = _user.getRules();
            try
            {
	            User user = userManager.getUser(_user.getName());
	            Principal principal = getUserPrincipal(user);
	            if (jsRules != null)
	            {
	            	Iterator _itRoles = jsRules.iterator();
	                while (_itRoles.hasNext())
	                {
	                	JSPrincipalRule pr = (JSPrincipalRule) _itRoles.next();
	                	ProfilingRule pRule = pm.getRule(pr.getRule());
	                	
	                	try
	                	{
	                		PrincipalRule p1 = pm.createPrincipalRule();
	                		p1.setLocatorName(pr.getLocator());
	                		p1.setProfilingRule(pRule);
	                		p1.setPrincipalName(principal.getName());
	                		pm.storePrincipalRule(p1);
	                	}
	                	catch (Exception eRole)
	                	{
	                		eRole.printStackTrace();
	                	}
	                }
	            }
            }
        	catch (Exception eUser)
        	{
        		eUser.printStackTrace();
        	}
        }
    	logMe("recreateUserPrincipalRules - done");

    }
    /**
     * recreates all permissions from the current snapshot
     * 
     * @throws SerializerException
     */
    private void recreatePermissions() throws SerializerException
    {
    	logMe("recreatePermissions - started");
        PermissionManager pm = (PermissionManager) getCM()
                .getComponent("org.apache.jetspeed.security.PermissionManager");
        if (pm == null)
            throw new SerializerException(
                    SerializerException.COMPONENTMANAGER_DOES_NOT_EXIST
                            .create("org.apache.jetspeed.security.PermissionManager"));

        Iterator list = null;
        JSPermissions permissions = ((JSSeedData)getSnapshot()).getPermissions();
 
        if (permissions != null)
        {
           list =  permissions.iterator();
            while (list.hasNext())
            {
                JSPermission _js = (JSPermission)list.next();
                PortalResourcePermission perm = _js.getPermissionForType();
                if ((perm != null) && (perm instanceof PortalResourcePermission))
                {
                	try
                    {
                        pm.addPermission(perm);
                        ArrayList listTemp = null;
    					JSUserGroups jsUserGroups = _js.getGroupString();
    					if (jsUserGroups != null)
    						listTemp = this.getTokens(jsUserGroups.toString());
    					else
    						listTemp = null;
    					if ((listTemp != null) && (listTemp.size()>0))
    					{
    						Iterator _itTemp = listTemp.iterator();
    						while (_itTemp.hasNext())
    						{
    							Principal p = (Principal)this.groupMap.get((String)_itTemp.next());
    							if (p != null)
    								pm.grantPermission(p, perm);
    						}
    					}
    					JSUserRoles jsUserRoles = _js.getRoleString();
    					if (jsUserRoles != null)
    						listTemp = this.getTokens(jsUserRoles.toString());
    					else
    						listTemp = null;
    					if ((listTemp != null) && (listTemp.size()>0))
    					{
    						Iterator _itTemp = listTemp.iterator();
    						while (_itTemp.hasNext())
    						{
       							Principal p = (Principal)this.roleMap.get((String)_itTemp.next());
    							if (p != null)
    								pm.grantPermission(p, perm);
    						}
    					}
    					JSUserUsers jsUserUsers = _js.getUserString();
    					if (jsUserUsers != null)
    						listTemp = this.getTokens(jsUserUsers.toString());
    					else
    						listTemp = null;
    					if ((listTemp != null) && (listTemp.size()>0))
    					{
    						Iterator _itTemp = listTemp.iterator();
    						while (_itTemp.hasNext())
    						{
       							Principal p = (Principal)this.userMap.get((String)_itTemp.next());
    							if (p != null)
    								pm.grantPermission(p, perm);
    						}
    					}
    
    	            } 
    	           	catch (Exception e)
    	            {
    	                throw new SerializerException(
    	                        SerializerException.CREATE_SERIALIZED_OBJECT_FAILED
    	                                .create(new String[]
    	                                { "Permissions", e.getMessage()}));
    	            }
                }
            }
        }
    	logMe("recreatePermissions - done");
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
    
    private void importProfiler()
    {
        System.out.println("importProfiler - processing");
        try
        {
        	recreateProfilingRules();
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }  
    }

    private void importUserGroupRoles()
    {
        System.out.println("importUserGroupRoles - processing");
        try
        {
            recreateRolesGroupsUsers();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }  
    }

    private void importUserPrincipals()
    {
        System.out.println("importUserPrincipals - processing");
        try
        {
            recreateUserPrincipalRules();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }  
    }
    
    private void importSSO()
    {
        System.out.println("importSSO - processing");
        try
        {
            recreateSSOSites();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }  
    }


    /**
     * The workhorse for importing data
     * 
     * @param binding
     *            established XML binding
     * @return
     * @throws SerializerException
     */
    protected void processImport() throws SerializerException
    {
        this.logMe("*********reinstalling data*********");
        
        // TODO: HJB Make sure to clean lookup tables before next run

        if (this.getSetting(JetspeedSerializer.KEY_PROCESS_CAPABILITIES))
        {
            logMe("creating clients, mediatypes and mimetypes");
            importCapabilitiesInfrastructure();
        }        
        /**
         * the order is important, since profiling rules are referenced by the user 
         * 
         */        
        if (this.getSetting(JetspeedSerializer.KEY_PROCESS_PROFILER))
        {
            logMe("collecting permissions, profiling rules and users/proups etc. etc.");
            importProfiler();
        } 
        if (this.getSetting(JetspeedSerializer.KEY_PROCESS_USERS))
        {
            logMe("creating users/roles/groups");
            this.importUserGroupRoles();
        }
        if (this.getSetting(JetspeedSerializer.KEY_PROCESS_PROFILER) || this.getSetting(JetspeedSerializer.KEY_PROCESS_USERS))
        {
            logMe("collecting user principals");
            this.importUserPrincipals();
        } 
        
        if (this.getSetting(JetspeedSerializer.KEY_PROCESS_PERMISSIONS))        
        {
            logMe("permissions, rules etc. skipped ");
            recreatePermissions();            
        }        

        if (this.getSetting(JetspeedSerializer.KEY_PROCESS_SSO))        
        {
            logMe("importing sso");
            this.importSSO();            
        }        
    }

    /**
     * The workhorse for exporting data
     * 
     * @param binding
     *            established XML binding
     * @return
     * @throws SerializerException
     */
    protected void processExport(String name, XMLBinding binding)
            throws SerializerException
    {
        this.logMe("*********collecting data*********");
        /** first create the snapshot file */

       setSnapshot(new JSSeedData(name));

        setSnapshotData();

        if (this.getSetting(JetspeedSerializer.KEY_PROCESS_CAPABILITIES))
        {
            logMe("collecting clients, mediatypes and mimetypes");
            exportCapabilitiesInfrastructure();
        } else
            logMe("capabilities skipped");

        if (this.getSetting(JetspeedSerializer.KEY_PROCESS_USERS))
        {
            logMe("collecting users");
            exportUsers();
        } else
            logMe("users skipped");

        if (this.getSetting(JetspeedSerializer.KEY_PROCESS_PROFILER))
        {
            logMe("collecting profiling rules");
            this.getProfilingRules();
        } else
            logMe(" profiling rules skipped");

        if (this.getSetting(JetspeedSerializer.KEY_PROCESS_PERMISSIONS))
        {
            logMe("collecting permissions");
            this.getPermissions();
        } else
            logMe(" permissions skipped");
        
        if (this.getSetting(JetspeedSerializer.KEY_PROCESS_SSO))
        {
            logMe("collecting sso sites");
            this.getSSOSites();
        } else
            logMe(" sso skipped");
        
    }

    /**
     * Setup the binding for the different classes, mapping each extracted class
     * to a unique tag name in the XML
     * 
     * @param binding
     */
    protected void setupAliases(XMLBinding binding)
    {
        binding.setAlias(JSRole.class, "Role");
        binding.setAlias(JSRoles.class, "Roles");
        binding.setAlias(JSGroup.class, "Group");
        binding.setAlias(JSGroups.class, "Groups");
        binding.setAlias(JSUser.class, "User");
        binding.setAlias(JSUsers.class, "Users");
        binding.setAlias(JSNVPElements.class, "preferences");
        binding.setAlias(JSUserAttributes.class, "userinfo");
        binding.setAlias(JSSnapshot.class, "snapshot");
        binding.setAlias(JSUserRoles.class, "roles");
        binding.setAlias(JSUserGroups.class, "groups");
        binding.setAlias(JSClient.class, "Client");
        binding.setAlias(JSClients.class, "Clients");
        binding.setAlias(JSClientCapabilities.class, "capabilities");
        binding.setAlias(JSClientMimeTypes.class, "mimeTypes");
        binding.setAlias(JSMimeTypes.class, "MimeTypes");
        binding.setAlias(JSMimeType.class, "MimeType");
        binding.setAlias(JSCapabilities.class, "Capabilities");
        binding.setAlias(JSCapability.class, "Capability");
        binding.setAlias(JSMediaTypes.class, "MediaTypes");
        binding.setAlias(JSMediaType.class, "MediaType");
        binding.setAlias(JSUserUsers.class, "users");

        binding.setAlias(JSPermissions.class, "Permissions");
        binding.setAlias(JSPermission.class, "Permission");
        binding.setAlias(JSProfilingRules.class, "ProfilingRules");
        binding.setAlias(JSProfilingRule.class, "ProfilingRule");
        binding.setAlias(JSRuleCriterions.class, "Criteria");
        binding.setAlias(JSRuleCriterion.class, "Criterion");

        binding.setAlias(JSPrincipalRule.class, "Rule");
        binding.setAlias(JSPrincipalRules.class, "Rules");

        binding.setAlias(String.class, "String");
        binding.setAlias(Integer.class, "int");
        
        binding.setAlias(JSPWAttributes.class,"credentials");

        binding.setAlias(JSSSOSite.class, "Site");
        binding.setAlias(JSSSOSiteRemoteUser.class, "RemoteUser");
        binding.setAlias(JSSSOSiteRemoteUsers.class, "RemoteUsers");
        binding.setAlias(JSSSOSites.class, "SSOSites");

        binding.setClassAttribute(null);

    }

    /**
     * simple lookup for principal object from a map
     * @param map
     * @param _fullPath
     * @return
     */

    private Object getObjectBehindPrinicpal(Map map, BasePrincipal _principal)
    {
        return getObjectBehindPath(map, _principal.getFullPath());
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
	 * export roles 
	 * 
	 * @return
	 */
    private void exportRoles() throws SerializerException
    {
        RoleManager roleManager = (RoleManager) getCM()
                .getComponent("org.apache.jetspeed.security.RoleManager");
        if (roleManager == null)
            throw new SerializerException(
                    SerializerException.COMPONENTMANAGER_DOES_NOT_EXIST
                            .create("org.apache.jetspeed.security.RoleManager"));

        Iterator list = null;
        try
        {
            list = roleManager.getRoles("");
        } catch (Exception e)
        {
            throw new SerializerException(
                    SerializerException.GET_EXISTING_OBJECTS
                            .create(new String[]
                            { "Role", e.getMessage()}));
        }
        while (list.hasNext())
        {
            try
            {
                Role role = (Role) list.next();
                JSRole _tempRole = (JSRole) getObjectBehindPrinicpal(roleMap,
                        (BasePrincipal) (role.getPrincipal()));
                if (_tempRole == null)
                {
                    _tempRole = createJSRole(role);
                    roleMap.put(_tempRole.getName(), _tempRole);
                    ((JSSeedData)getSnapshot()).getRoles().add(_tempRole);
                }

            } catch (Exception e)
            {
                throw new SerializerException(
                        SerializerException.CREATE_SERIALIZED_OBJECT_FAILED
                                .create(new String[]
                                { "Role", e.getMessage()}));
            }
        }
        return;
    }

    /** Groups -------------------------- */
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
     * extract the groups from the current environment
     * 
     * @throws SerializerException
     */
    private void exportGroups() throws SerializerException
    {
        GroupManager groupManager = (GroupManager) getCM()
                .getComponent("org.apache.jetspeed.security.GroupManager");
        if (groupManager == null)
            throw new SerializerException(
                    SerializerException.COMPONENTMANAGER_DOES_NOT_EXIST
                            .create("org.apache.jetspeed.security.GroupManager"));
        Iterator list = null;
        try
        {
            list = groupManager.getGroups("");
        } catch (Exception e)
        {
            throw new SerializerException(
                    SerializerException.GET_EXISTING_OBJECTS
                            .create(new String[]
                            { "Group", e.getMessage()}));
        }
        while (list.hasNext())
        {

            try
            {
                Group group = (Group) list.next();
                JSGroup _tempGroup = (JSGroup) getObjectBehindPrinicpal(
                        groupMap, (BasePrincipal) (group.getPrincipal()));
                if (_tempGroup == null)
                {
                    _tempGroup = createJSGroup(group);
                    groupMap.put(_tempGroup.getName(), _tempGroup);
                    ((JSSeedData)getSnapshot()).getGroups().add(_tempGroup);
                }

            } catch (Exception e)
            {
                throw new SerializerException(
                        SerializerException.CREATE_SERIALIZED_OBJECT_FAILED
                                .create(new String[]
                                { "Group", e.getMessage()}));
            }
        }
        return;
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

    private void addJSUserCredentials(boolean isPublic, JSUser newUser,
            Object credential)
    {
        if (credential == null) return;
        if (credential instanceof PasswordCredential)
        {
            PasswordCredential pw = (PasswordCredential) credential;
            newUser.setUserCredential(pw.getUserName(), pw.getPassword(),pw.getExpirationDate(),pw.isEnabled(), pw.isExpired(), pw.isUpdateRequired());
            return;
        } else if (isPublic)
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
    private JSUser createJSUser(User user)
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
                JSRole _tempRole = (JSRole) this.getObjectBehindPath(roleMap,
                        principal.getName());
                if (_tempRole != null)
                {
                    _newUser.addRole(_tempRole);
                }

            } else
            {
                if (path.startsWith("/group/"))
                {
                    JSGroup _tempGroup = (JSGroup) this.getObjectBehindPath(
                            groupMap, principal.getName());
                    if (_tempGroup != null)
                    {
                        _newUser.addGroup(_tempGroup);
                    }

                } else if (path.startsWith("/user/"))
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
        //TODO: HJB, fix preferences...userinfo doesn't return values in prefs_property_value (in fact preferences.keys() is []
        return _newUser;
    }


    /**
     * Collect all the roles, groups and users from the current environment.
     * Include the current SecurityProvider to understand, whether the password
     * collected can be used upon import
     * 
     * @throws SerializerException
     */

    private void exportUsers() throws SerializerException
    {
        /** set the security provider info in the snapshot file */
        ((JSSeedData)getSnapshot()).setEncryption(getEncryptionString());
        /** get the roles */
        exportRoles();
        /** get the groups */
        exportGroups();

        /** users */
        UserManager userManager = (UserManager) getCM()
                .getComponent("org.apache.jetspeed.security.UserManager");
        if (userManager == null)
            throw new SerializerException(
                    SerializerException.COMPONENTMANAGER_DOES_NOT_EXIST
                            .create("org.apache.jetspeed.security.UserManager"));
        Iterator list = null;
        try
        {
            list = userManager.getUsers("");
        } catch (Exception e)
        {
            throw new SerializerException(
                    SerializerException.GET_EXISTING_OBJECTS
                            .create(new String[]
                            { "User", e.getMessage()}));
        }
        while (list.hasNext())
        {

            try
            {
                User _user = (User) list.next();
                JSUser _tempUser = createJSUser(_user);
                userMap.put(_tempUser.getName(), _tempUser);
                ((JSSeedData)getSnapshot()).getUsers().add(_tempUser);
            } catch (Exception e)
            {
                throw new SerializerException(
                        SerializerException.CREATE_SERIALIZED_OBJECT_FAILED
                                .create(new String[]
                                { "User", e.getMessage()}));
            }

        }
        return;

    }

    /**
     * extract the capabilities and save in snapshot file
     * 
     * @param caps
     *            capability-container
     * @throws SerializerException
     */
    private void exportCapabilites(Capabilities caps)
            throws SerializerException
    {

        Iterator list = caps.getCapabilities();

        while (list.hasNext())
        {
            try
            {
                Capability _cp = (Capability) list.next();
                JSCapability _jsC = new JSCapability();
                _jsC.setName(_cp.getName());
                this.capabilityMap.put(_jsC.getName(), _jsC);
                this.capabilityMapInt.put(new Integer(_cp.getCapabilityId()), _jsC);
                ((JSSeedData)getSnapshot()).getCapabilities().add(_jsC);
            } catch (Exception e)
            {
                throw new SerializerException(
                        SerializerException.CREATE_SERIALIZED_OBJECT_FAILED
                                .create(new String[]
                                { "C", e.getMessage()}));
            }
        }
        return;

    }

    /**
     * exstract the mime types anmd save in snapshot file
     * 
     * @param caps
     *            capability container
     * @throws SerializerException
     */
    private void exportMimeTypes(Capabilities caps) throws SerializerException
    {

        Iterator list = caps.getMimeTypes();

        while (list.hasNext())
        {
            try
            {
                MimeType _mt = (MimeType) list.next();
                JSMimeType _jsM = new JSMimeType();
                _jsM.setName(_mt.getName());
                this.mimeMap.put(_jsM.getName(), _jsM);
                this.mimeMapInt.put(new Integer(_mt.getMimetypeId()), _jsM);

                ((JSSeedData)getSnapshot()).getMimeTypes().add(_jsM);
            } catch (Exception e)
            {
                throw new SerializerException(
                        SerializerException.CREATE_SERIALIZED_OBJECT_FAILED
                                .create(new String[]
                                { "MimeType", e.getMessage()}));
            }
        }
        return;

    }

    /**
     * create a JS CLient
     * 
     * @param c
     *            the existing Client object
     * @return
     * @throws SerializerException
     */

    private JSClient createJSClient(Client c) throws SerializerException
    {
        try
        {
            JSClient jsC = new JSClient(c);
            // find the mimeTypes
            Iterator _itM = c.getMimetypes().iterator();
            while (_itM.hasNext())
            {
                MimeType _m = (MimeType) _itM.next();
                JSMimeType _mt = (JSMimeType) mimeMap.get(_m.getName());
                if (_mt != null) jsC.getMimeTypes().add(_mt);
            }
            
    		Integer id = new Integer(c.getPreferredMimeTypeId());
    		JSMimeType _mt = (JSMimeType) mimeMapInt.get(id);
    		if (_mt != null)
    			jsC.setPreferredMimeTypeID(_mt.getName());
    		else
    			jsC.setPreferredMimeTypeID("???");

            // find the capabilities
            Iterator _itC = c.getCapabilities().iterator();
            while (_itC.hasNext())
            {
                Capability _c = (Capability) _itC.next();
                JSCapability _ct = (JSCapability) capabilityMap.get(_c
                        .getName());
                if (_ct != null) jsC.getCapabilities().add(_ct);
            }

            return jsC;
        } catch (Exception e)
        {
            throw new SerializerException(
                    SerializerException.CREATE_SERIALIZED_OBJECT_FAILED
                            .create(new String[]
                            { "Client", e.getMessage()}));
        }

    }

    /**
     * extract the current clients and save in the snapshot file
     * 
     * @param list
     * @param caps
     * @return
     * @throws SerializerException
     */
    private void exportClients(Capabilities caps) throws SerializerException
    {

        /** first the the mime types */
        exportMimeTypes(caps);

        /** second get the capabilities */
        this.exportCapabilites(caps);

        /** now get the clients */
        Iterator _it = caps.getClients();
        while (_it.hasNext())
        {
            Client c = (Client) _it.next();
            JSClient jsC = createJSClient(c);
            if (jsC == null)
                throw new SerializerException(
                        SerializerException.CREATE_SERIALIZED_OBJECT_FAILED
                                .create(new String[]
                                { "Client", "createClient returned NULL"}));
            this.clientMap.put(jsC.getName(), jsC);
            ((JSSeedData)getSnapshot()).getClients().add(jsC);
        }
        return;
    }

    /**
     * extract the media types and save in snapshot file
     * 
     * @param caps
     *            capabilit container
     * @throws SerializerException
     */
    private void exportMediaTypes(Capabilities caps) throws SerializerException
    {
        Iterator list = caps.getMediaTypes();

        while (list.hasNext())
        {
            try
            {
                MediaType _mt = (MediaType) list.next();
                JSMediaType _jsM = new JSMediaType(_mt);
                // find the mimeTypes
                Iterator _itM = _mt.getMimetypes().iterator();
                while (_itM.hasNext())
                {
                    MimeType _m = (MimeType) _itM.next();
                    JSMimeType _mttype = (JSMimeType) mimeMap.get(_m.getName());
                    if (_mttype != null) _jsM.getMimeTypes().add(_mttype);
                }
                // find the capabilities
                Iterator _itC = _mt.getCapabilities().iterator();
                while (_itC.hasNext())
                {
                    Capability _c = (Capability) _itC.next();
                    JSCapability _ct = (JSCapability) capabilityMap.get(_c
                            .getName());
                    if (_ct != null) _jsM.getCapabilities().add(_ct);
                }
                this.mediaMap.put(_jsM.getName(), _jsM);
                ((JSSeedData)getSnapshot()).getMediaTypes().add(_jsM);
            } catch (Exception e)
            {
                // do whatever
                throw new SerializerException(
                        SerializerException.CREATE_SERIALIZED_OBJECT_FAILED
                                .create(new String[]
                                { "MediaType", e.getMessage()}));
            }
        }
        return;
    }

    /**
     * Extracts all capability related objects (clients, mimetypes and
     * mediatypes) and their relationships
     * 
     * @return
     */

    private void exportCapabilitiesInfrastructure() throws SerializerException
    {
        Capabilities caps = (Capabilities) getCM()
                .getComponent("org.apache.jetspeed.capabilities.Capabilities");
        if (caps == null)
            throw new SerializerException(
                    SerializerException.COMPONENTMANAGER_DOES_NOT_EXIST
                            .create("org.apache.jetspeed.capabilities.Capabilities"));

        /**
         * get the clients (which in turn will get the mime types and
         * capailities)
         */

        exportClients(caps);
        // get the mediatTypes, too

        exportMediaTypes(caps);

    }

    /**
     * extract all permissions from the current environment
     * 
     * @throws SerializerException
     */
    private void getPermissions() throws SerializerException
    {
        Object o = null;
        PermissionManager pm = (PermissionManager) getCM()
                .getComponent("org.apache.jetspeed.security.PermissionManager");
        if (pm == null)
            throw new SerializerException(
                    SerializerException.COMPONENTMANAGER_DOES_NOT_EXIST
                            .create("org.apache.jetspeed.security.PermissionManager"));

        Iterator list = null;
        try
        {
            list = pm.getPermissions().iterator();
        } catch (Exception e)
        {
            throw new SerializerException(
                    SerializerException.GET_EXISTING_OBJECTS
                            .create(new String[]
                            { "Permissions", e.getMessage()}));
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
                        JSRole _tempRole = (JSRole) this.getObjectBehindPath(
                                roleMap, removeFromString(path, "/role/"));
                        if (_tempRole != null)
                        {
                            _js.addRole(_tempRole);
                        }

                    } else
                    {
                        if (path.startsWith("/group/"))
                        {
                            JSGroup _tempGroup = (JSGroup) this
                                    .getObjectBehindPath(groupMap,
                                            removeFromString(path, "/group/"));
                            if (_tempGroup != null)
                            {
                                _js.addGroup(_tempGroup);
                            }

                        } else
                        {
                            if (path.startsWith("/user/"))
                            {
                                JSUser _tempUser = (JSUser) this
                                        .getObjectBehindPath(
                                                userMap,
                                                removeFromString(path, "/user/"));
                                if (_tempUser != null)
                                {
                                    _js.addUser(_tempUser);
                                }

                            }

                        }

                    }
                }
                this.permissionMap.put(_js.getType(), _js);
                ((JSSeedData)getSnapshot()).getPermissions().add(_js);

            } catch (Exception e)
            {
                throw new SerializerException(
                        SerializerException.CREATE_SERIALIZED_OBJECT_FAILED
                                .create(new String[]
                                { "Permissions", e.getMessage()}));
            }
        }
        return;

    }

    /**
     * Create the Profiling Rule Wrapper
     * 
     * @param p
     * @return
     */
    private JSProfilingRule createProfilingRule(ProfilingRule p, boolean standard)
    {
        JSProfilingRule rule = new JSProfilingRule();
        
        
        
        rule.setStandardRule(standard);
        rule.setDescription(p.getTitle());
        rule.setId(p.getId());

        Collection col = p.getRuleCriteria();
        Iterator keys = col.iterator();
        while (keys.hasNext())
        {
            RuleCriterion rc = (RuleCriterion) keys.next();
            rule.getCriterions().add(new JSRuleCriterion(rc));
        }
        return rule;

    }

    /**
     * read the permissions and then the profiling rules.
     * <p>
     * after that update the cross reference with the users
     * 
     * @throws SerializerException
     */
    private void getProfilingRules() throws SerializerException
    {
        Profiler pm = (Profiler) getCM()
                .getComponent("org.apache.jetspeed.profiler.Profiler");
        if (pm == null)

            throw new SerializerException(
                    SerializerException.COMPONENTMANAGER_DOES_NOT_EXIST
                            .create("org.apache.jetspeed.profiler.Profiler"));
        Class standardRuleClass = null;
        try
        {
        	ProfilingRule tempStandardRule = pm.createProfilingRule(true);
        	standardRuleClass = tempStandardRule.getClass();
        }
        catch (Exception e)
        {
        	throw new SerializerException(
                SerializerException.CREATE_OBJECT_FAILED
                        .create(new String[]
                        { "Standard Rule", e.getMessage()}));
        }
        
        Iterator list = null;
        try
        {
            list = pm.getRules().iterator();
        } catch (Exception e)
        {
            throw new SerializerException(
                    SerializerException.GET_EXISTING_OBJECTS
                            .create(new String[]
                            { "ProfilingRules", e.getMessage()}));
        }
        while (list.hasNext())
        {
            try
            {
                ProfilingRule p = (ProfilingRule) list.next();
                if (!(this.rulesMap.containsKey(p.getId())))
                {
                    JSProfilingRule rule = createProfilingRule(p, (standardRuleClass == p.getClass()));
                    rulesMap.put(rule.getId(), rule);
                    ((JSSeedData)getSnapshot()).getRules().add(rule);

                }
            } catch (Exception e)
            {
                throw new SerializerException(
                        SerializerException.CREATE_SERIALIZED_OBJECT_FAILED
                                .create(new String[]
                                { "ProfilingRules", e.getMessage()}));
            }
        }

        // determine the defualt rule
        ProfilingRule defaultRule = pm.getDefaultRule();
        if (defaultRule != null)
        	((JSSeedData)getSnapshot()).setDefaultRule(defaultRule.getId());

        // get Rules for each user

        Iterator _itUsers = this.userMap.values().iterator();
        while (_itUsers.hasNext())
        {
            JSUser _user = (JSUser) _itUsers.next();
            Principal principal = _user.getPrincipal();
            if (principal != null)
            {
                Collection col = pm.getRulesForPrincipal(principal);
                Iterator _itCol = col.iterator();
                while (_itCol.hasNext())
                {
                    PrincipalRule p1 = (PrincipalRule) _itCol.next();
                    JSPrincipalRule pr = new JSPrincipalRule(p1
                            .getLocatorName(), p1.getProfilingRule().getId());
                    _user.getRules().add(pr);
                }
            }
        }

        return;

    }

    /**
     * Establish whether incoming passwords are "clear" text or whether they are
     * to be decoded. That however depends on whether the passwords were encoded
     * with the current active provider or not.
     * 
     * @return
     */
    protected int compareCurrentSecurityProvider(JSSeedData file)
    {
        String _fileEncryption = file.getEncryption();
        if ((_fileEncryption == null) || (_fileEncryption.length() == 0))
            return NO_DECODING; // passwords are in clear text
        
        if (_fileEncryption.equals(getEncryptionString()))
        	return PASSTHRU_REQUIRED;
        else
        	return NO_DECODING;
    }

    private String getEncryptionString()
    {
        PasswordCredentialProvider provider = (PasswordCredentialProvider) getCM()
        .getComponent("org.apache.jetspeed.security.spi.PasswordCredentialProvider");
		if (provider == null)
		{
		    System.err
		            .println("Error!!! PasswordCredentialProvider not available");
		    return ENCODING_STRING;
		}
		try
		{
			PasswordCredential credential = provider.create(JETSPEED,ENCODING_STRING);
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
    
    /**
     * Create the SSO Site Wrapper
     * 
     * @param s sso site
     * @return sso site wrapper
     */
    private JSSSOSite createSSOSite(SSOSite s)
    {
        JSSSOSite site = new JSSSOSite();
        site.setName(s.getName());
        site.setSiteURL(s.getSiteURL());
        site.setAllowUserSet(s.isAllowUserSet());
        site.setCertificateRequired(s.isCertificateRequired());
        site.setChallengeResponseAuthentication(s.isChallengeResponseAuthentication());
        site.setRealm(s.getRealm());
        site.setFormAuthentication(s.isFormAuthentication());
        site.setFormUserField(s.getFormUserField());
        site.setFormPwdField(s.getFormPwdField());

        Iterator rupIter = s.getRemotePrincipals().iterator();
        while (rupIter.hasNext())
        {
            InternalUserPrincipal rup = (InternalUserPrincipal)rupIter.next();
            InternalCredential rupCredential = null;
            Collection rupCredentials = rup.getCredentials();
            if (rupCredentials != null)
            {
                rupCredential = (InternalCredential)rupCredentials.iterator().next();
            }
            if (rupCredential != null)
            {
                String rupPath = rup.getFullPath();
                String rupPrincipalType = null;
                String rupPrincipalName = null;
                String rupName = null;
                String [] names = null;
                if (rupPath.startsWith("/sso/") && (rupPath.indexOf("/user/") != -1))
                {
                    rupPrincipalType = "user";
                    names = rupPath.replaceAll("^/sso/[0-9]+/user/", "").split("/");
                }
                else if (rupPath.startsWith("/sso/") && (rupPath.indexOf("/group/") != -1))
                {
                    rupPrincipalType = "group";
                    names = rupPath.replaceAll("^/sso/[0-9]+/group/", "").split("/");
                }
                if ((names != null) && (names.length == 2))
                {
                    rupPrincipalName = names[0];
                    rupName = names[1];
                }
                if ((rupPrincipalType != null) && (rupPrincipalName != null) && (rupPrincipalName.length() > 0) && (rupName != null) && (rupName.length() > 0))
                {
                    JSSSOSiteRemoteUser siteRemoteUser = new JSSSOSiteRemoteUser();
                    siteRemoteUser.setPrincipalName(rupPrincipalName);
                    siteRemoteUser.setPrincipalType(rupPrincipalType);
                    siteRemoteUser.setUserCredential(rupName, rupCredential.getValue().toCharArray());
                    site.addRemoteUser(siteRemoteUser);
                }
            }
        }
            
        return site;
    }

    /**
     * extract SSO sites and save in snapshot file
     * 
     * @throws SerializerException
     */
    private void getSSOSites() throws SerializerException
    {
        SSOProvider ssoProvider = (SSOProvider) getCM().getComponent("org.apache.jetspeed.sso.SSOProvider");
        if (ssoProvider == null)
        {
            throw new SerializerException(SerializerException.COMPONENTMANAGER_DOES_NOT_EXIST.create("org.apache.jetspeed.sso.SSOProvider"));
        }

        Iterator list = null;
        try
        {
            list = ssoProvider.getSites("");
        }
        catch (Exception e)
        {
            throw new SerializerException(SerializerException.GET_EXISTING_OBJECTS.create(new String[]{"SSOSites", e.getMessage()}));
        }
        while (list.hasNext())
        {
            try
            {
                SSOSite s = (SSOSite) list.next();                
                if (!(ssoSitesMap.containsKey(s.getSiteURL())))
                {
                    JSSSOSite site = createSSOSite(s);
                    ssoSitesMap.put(site.getSiteURL(), site);
                    ((JSSeedData)getSnapshot()).getSSOSites().add(site);
                }
            }
            catch (Exception e)
            {
                throw new SerializerException(SerializerException.CREATE_SERIALIZED_OBJECT_FAILED.create(new String[]{"SSOSites", e.getMessage()}));
            }
        }
    }
    
    /**
     * ++++++++++++++++++++++++++++++HELPERS
     * +++++++++++++++++++++++++++++++++++++++++++++
     */

  


    /**
     * recreate a rule criterion object from the deserialized wrapper
     * @param profiler established profile manager
     * @param jsr deserialized object
     * @return new RuleCriterion with content set to deserialized wrapepr
     * @throws SerializerException
     */
	protected RuleCriterion recreateRuleCriterion(Profiler profiler, JSRuleCriterion jsr, ProfilingRule rule)
		throws SerializerException, ClassNotFoundException
	
	{
		try
		{
	
	    	RuleCriterion c = profiler.createRuleCriterion();
	    	if (c == null)
	    		throw new SerializerException(
	    				SerializerException.CREATE_OBJECT_FAILED
	    					.create("org.apache.jetspeed.profiler.rules.RuleCriterion","returned null"));
	    	c.setFallbackOrder(jsr.getFallBackOrder());
	    	c.setFallbackType(jsr.getFallBackType());
	    	c.setName(jsr.getName());
	    	c.setType(jsr.getType());
	    	c.setValue(jsr.getValue());
	    	c.setRuleId(rule.getId());
	    	return c;
		}
		catch (Exception e)
		{
			SerializerException.CREATE_OBJECT_FAILED
			.create("org.apache.jetspeed.profiler.rules.RuleCriterion",e.getLocalizedMessage());
			return null;
		}
	}
    	
	   /**
     * recreate a profiling rule object from the deserialized wrapper and store it
     * @param profiler established profile manager
     * @param jsp deserialized object
     * @
     * @throws SerializerException, ClassNotFoundException, ProfilerException
     */
	   protected ProfilingRule recreateRule(Profiler profiler, ProfilingRule existingRule, JSProfilingRule jsp) throws SerializerException, ClassNotFoundException, ProfilerException
	   {
		   ProfilingRule rule = null;
		   boolean existing = false;
		   
		   if (existingRule == null)
		   {
			   rule = profiler.getRule(jsp.getId());
			   if (jsp.isStandardRule())
			  	   rule = profiler.createProfilingRule(true);   
			   else
			  	   rule = profiler.createProfilingRule(false); 
			   rule.setId(jsp.getId());
		   }
		   else
		   {
			   rule = existingRule;
			   existing = true;
		   }
			   
		   rule.setTitle(jsp.getDescription());
		   
		   JSRuleCriterions col = jsp.getCriterions();
			   
		   Iterator _it = col.iterator();
		   while (_it.hasNext())
		   {
				   RuleCriterion c = recreateRuleCriterion(profiler, (JSRuleCriterion) _it.next(),rule);
				   if (c != null)
				   {
					   Collection cHelp = rule.getRuleCriteria();
					   if ((existing) && (cHelp.contains(c)))
						   cHelp.remove(c); //remove existing duplicate
					   cHelp.add(c); // add the current version back in
				   }
		   }
		   return rule;

	   }  	
	
    	
    	
	   private void recreateProfilingRules () throws SerializerException
	    {
	    	logMe("recreateProfilingRules - processing");
	        Profiler pm = (Profiler) getCM()
            .getComponent("org.apache.jetspeed.profiler.Profiler");
	        if (pm == null)
	        	throw new SerializerException(
	        			SerializerException.COMPONENTMANAGER_DOES_NOT_EXIST
                        	.create("org.apache.jetspeed.profiler.Profiler"));
	    	JSProfilingRules rules = ((JSSeedData)getSnapshot()).getRules();
	    	if ((rules != null) && (rules.size() > 0))
	    	{
	    		Iterator _it = rules.iterator();
	    		while (_it.hasNext())
	    		{
	    			JSProfilingRule _c = (JSProfilingRule)_it.next();

	    			try
	    			{
	    				ProfilingRule rule = null;
	    				   
	    				   rule = pm.getRule(_c.getId());
	    				   if ((rule == null) || (this.getSetting(JetspeedSerializer.KEY_OVERWRITE_EXISTING)))
	    				   {
	    					   rule = recreateRule(pm,rule, _c);
	    					   pm.storeProfilingRule(rule);
	    				   }
	    			}
	    			catch (Exception e)
	    			{
	    				throw new SerializerException(
	    			            SerializerException.CREATE_OBJECT_FAILED
	    			            	.create("org.apache.jetspeed.capabilities.Capabilities",e.getLocalizedMessage()));
	 			}
	    		}
	    		/** reset the default profiling rule */
	    		String defaultRuleID = ((JSSeedData)getSnapshot()).getDefaultRule();
	    		if (defaultRuleID != null)
	    		{
	    			ProfilingRule defaultRule = pm.getRule(defaultRuleID);
	    			if (defaultRule != null)
	    				pm.setDefaultRule(defaultRuleID);
	    		}
	    	}
	    	else
	    		logMe("NO PROFILING RULES?????");
	    	logMe("recreateProfilingRules - done");
	    }	


    /**
     * Construct SSO site from (JS) SSOSite.
     * 
     * @param ssoProvider SSO provider
     * @param site SSO site
     * @param s existing SSO site
     * @return created SSO site
     * @throws SerializerException
     * @throws SSOException
     */
    private SSOSite recreateSSOSite(SSOProvider ssoProvider, JSSSOSite site, SSOSite s) throws SerializerException, SSOException
    {
        if (s != null)
        {
            ssoProvider.removeSite(s);
        }
        
        ssoProvider.addSite(site.getName(), site.getSiteURL());
        s = ssoProvider.getSite(site.getSiteURL());
        s.setAllowUserSet(site.isAllowUserSet());
        s.setCertificateRequired(site.isCertificateRequired());
        s.setChallengeResponseAuthentication(site.isChallengeResponseAuthentication());
        s.setRealm(site.getRealm());
        s.setFormAuthentication(site.isFormAuthentication());
        s.setFormUserField(site.getFormUserField());
        s.setFormPwdField(site.getFormPwdField());

        if (site.getRemoteUsers() != null)
        {
            Iterator ruIter = site.getRemoteUsers().iterator();
            while (ruIter.hasNext())
            {
                JSSSOSiteRemoteUser rUser = (JSSSOSiteRemoteUser)ruIter.next();
                if (rUser.getPassword() != null)
                {
                    String pName = rUser.getPrincipalName();
                    String pFullPath = "/"+rUser.getPrincipalType()+"/"+pName;
                    String rName = rUser.getName();
                    String rPassword = new String(rUser.getPassword());
                    ssoProvider.addCredentialsForSite(s, pFullPath, pName, rName, rPassword);
                }
            }
        }
        
        return s;
    }
    
    /**
     * Create imported SSO sites.
     * 
     * @throws SerializerException
     */
    private void recreateSSOSites() throws SerializerException
    {
        logMe("recreateSSOSites - processing");
        
        SSOProvider ssoProvider = (SSOProvider) getCM().getComponent("org.apache.jetspeed.sso.SSOProvider");
        if (ssoProvider == null)
        {
            throw new SerializerException(SerializerException.COMPONENTMANAGER_DOES_NOT_EXIST.create("org.apache.jetspeed.sso.SSOProvider"));
        }

        JSSSOSites sites = ((JSSeedData)getSnapshot()).getSSOSites();
        if ((sites != null) && (sites.size() > 0))
        {
            Iterator sitesIter = sites.iterator();
            while (sitesIter.hasNext())
            {
                JSSSOSite site = (JSSSOSite)sitesIter.next();
                try
                {
                    SSOSite s = ssoProvider.getSite(site.getSiteURL());
                    if ((s == null) || getSetting(JetspeedSerializer.KEY_OVERWRITE_EXISTING))
                    {
                        s = recreateSSOSite(ssoProvider, site, s);
                        ssoProvider.updateSite(s);        
                    }
                }
                catch (Exception e)
                {
                    throw new SerializerException(SerializerException.CREATE_OBJECT_FAILED.create("SSOSite",e.getLocalizedMessage()));
                }
            }
        }
        else
        {
            logMe("NO SSO SITES?????");
        }
        
        logMe("recreateSSOSites - done");        
    }   
}
