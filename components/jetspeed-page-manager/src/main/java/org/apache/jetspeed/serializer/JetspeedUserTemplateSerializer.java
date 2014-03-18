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

import java.security.PrivilegedAction;
import java.util.Map;

import javax.security.auth.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.administration.PortalConfigurationConstants;
import org.apache.jetspeed.components.util.ConfigurationProperties;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.folder.InvalidFolderException;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.security.JSSubject;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.serializer.objects.JSSnapshot;
import org.apache.jetspeed.serializer.objects.JSPrincipal;

/**
 * JetspeedSecuritySerializer - Security component serializer
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class JetspeedUserTemplateSerializer extends AbstractJetspeedComponentSerializer
{
    private final static Logger log = LoggerFactory.getLogger(JetspeedUserTemplateSerializer.class);
    private PageManager pageManager;
    private UserManager userManager;
    private String adminUserName = null;
    
    public JetspeedUserTemplateSerializer(PageManager pageManager, UserManager userManager, ConfigurationProperties config)
    {
        this.pageManager = pageManager;
        this.userManager = userManager;
        this.adminUserName = config.getString(PortalConfigurationConstants.USERS_DEFAULT_ADMIN);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.serializer.JetspeedComponentSerializer#processExport(org.apache.jetspeed.serializer.objects.JSSnapshot,
     *      java.util.Map, org.apache.commons.logging.Log)
     */
    protected void processExport(JSSnapshot snapshot, Map<String,Object> settings, Logger log) throws SerializerException
    {
        if (isSettingSet(settings, JetspeedSerializer.KEY_PROCESS_USER_TEMPLATES))
        {
            log.info("collecting user template info");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.serializer.JetspeedComponentSerializer#processImport(org.apache.jetspeed.serializer.objects.JSSnapshot,
     *      java.util.Map, org.apache.commons.logging.Log)
     */
    protected void processImport(JSSnapshot snapshot, Map<String,Object> settings, Logger log) throws SerializerException
    {
        if (isSettingSet(settings, JetspeedSerializer.KEY_PROCESS_USER_TEMPLATES))
        {
            log.info("creating user templates");
            User adminUser = null;
            
            try
            {
                adminUser = userManager.getUser(this.adminUserName);
            }
            catch (Exception e)
            {
                System.out.println("admin user failed to retrieve " + adminUserName);
                e.printStackTrace();
                adminUser = null;
            }

            if (adminUser == null)
                throw new SerializerException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.USER, "admin"));
            
//            for (JSPrincipal user : snapshot.getUsers())
//            {
                // TODO: should have a specific user JSPrincipal class?
//                String folderTemplate = user.getUserTemplate();
//                String ssubsite = user.getSubsite();
//                if (folderTemplate != null)
//                {
//                    String userTemplate = null;
//                    String subsite = null;
//                    if (user.getSubsite() != null)
//                    {
//                        subsite = user.getSubsite();
//                        String path = PageManagerUtils.concatenatePaths(Folder.SUBSITE_ROOT_FOLDER, subsite);
//                        userTemplate = PageManagerUtils.concatenatePaths(path, Folder.USER_FOLDER + user.getName());
//                        //userTemplate = Folder.SUBSITE_ROOT_FOLDER + subsite + Folder.USER_FOLDER + user.getName();
//                    } 
//                    else
//                    {
//                        userTemplate = Folder.USER_FOLDER + user.getName();
//                    }
//                    this.createUserTemplate(folderTemplate, userTemplate, subsite, this.pageManager, user.getName(), adminUser);
//                }

//            }
        }
    }

    protected void deleteData(Map<String,Object> settings, Logger log) throws SerializerException
    {
        if (isSettingSet(settings, JetspeedSerializer.KEY_PROCESS_USER_TEMPLATES))
        {
            log.info("deleting user templates");
        }
    }

    // creating userhome pages from usertemplates
    private void createUserTemplate(String folderTemplate, String userTemplate, String subsite, final PageManager pageManager, String userName, User adminUser)
    {
        final String innerFolderTemplate = folderTemplate;
        final String templateFolder = userTemplate;
        final String innerSubsite = subsite;
        final PageManager innerPageManager = pageManager;
        final String innerUserName = userName;
        Subject adminSubject;
        try{
        	adminSubject = userManager.getSubject(adminUser);
        }
        catch (SecurityException se) {
        	//TODO handle error in much better way
        	return;
		}
        JetspeedException pe = (JetspeedException) JSSubject.doAsPrivileged(adminSubject, new PrivilegedAction()
        {

            public Object run()
            {
                try
                {
                    if (innerSubsite != null)
                    {
                        User innerUser = userManager.getUser(innerUserName);
                        innerUser.getSecurityAttributes().getAttribute(User.JETSPEED_USER_SUBSITE_ATTRIBUTE, true).setStringValue(innerSubsite);
                        userManager.updateUser(innerUser);
                    }
                    Folder source = innerPageManager.getFolder(innerFolderTemplate);
                    innerPageManager.deepMergeFolder(source, templateFolder, innerUserName);
                    Folder newFolder = pageManager.getFolder(templateFolder);
                    newFolder.setTitle("Home Folder");
                    newFolder.setShortTitle("Home");
                    return null;
                } 
                catch (FolderNotFoundException e1)
                {
                    return e1;
                } 
                catch (InvalidFolderException e1)
                {
                    return e1;
                } 
                catch (NodeException e1)
                {
                    e1.printStackTrace();
                    return e1;
                }
                catch (SecurityException se)
                {
                    se.printStackTrace();
                    return se;                    
                } 
            }
        }, null);

        if (pe != null)
        {
        }
    }

}
