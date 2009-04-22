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
package org.apache.jetspeed.layout.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.serializer.JetspeedSerializer;

/**
 * Exporting the object using Ajax command
 * 
 * @author <a href="mailto:firevelocity@gmail.com">Vivek Kumar</a>
 * @version $Id$
 */
/*
 * TODO: commenting out this class for now as it is still based upon the 2.1.3 JetspeedSerialzer while in trunk the
 * JetspeedSerializer has been refactored largely so it doesn't even compile. Additionally, some related new 2.1.3
 * features haven't been ported over to trunk yet either (e.g. r592266 and more) Will revisit this class and the
 * JetspeedSerializer enhancements once 2.1.3 is released
 */
public class ExportJetspeedSchema extends BaseGetResourceAction implements AjaxAction, AjaxBuilder, Constants
{
    protected Logger log = LoggerFactory.getLogger(GetFolderAction.class);
    protected PageManager castorPageManager;
    protected JetspeedSerializer serializer;
    protected String pageRoot;
    // categories of export
    private static final String USERS = "users";
    private static final String PERMISSIONS = "permissions";
    private static final String PROFILES = "profiles";
    private static final String CAPABILITIES = "capabilities";
    private static final String USER_PREFS = "uprefs";
    private static final String ENTITIES = "entities";
    String pathSeprator = System.getProperty("file.separator");

    public ExportJetspeedSchema(String template, String errorTemplate, PageManager pageManager,
                                PortletActionSecurityBehavior securityBehavior, JetspeedSerializer serializer,
                                String dir)
    {
        super(template, errorTemplate, pageManager, securityBehavior);
        this.serializer = serializer;
        this.pageRoot = dir;
    }

    public boolean run(RequestContext requestContext, Map resultMap)
    {
        boolean success = true;
        String status = "success";
        String userName = requestContext.getUserPrincipal().getName();
        Map settings = new HashMap();
        String exportFileName = getUserFolder(userName, false) + pathSeprator + "ldapExport.xml";
        try
        {
            resultMap.put(ACTION, "export");
            if (false == checkAccess(requestContext, JetspeedActions.VIEW))
            {
                success = false;
                resultMap.put(REASON, "Insufficient access to get portlets");
                return success;
            }
            settings.put(JetspeedSerializer.KEY_PROCESS_USERS,
                         getNonNullActionParameter(requestContext, USERS).equalsIgnoreCase("y") ? Boolean.TRUE
                                                                                               : Boolean.FALSE);
            Boolean value = getNonNullActionParameter(requestContext, PERMISSIONS).equalsIgnoreCase("y") ? Boolean.TRUE
                                                                                                        : Boolean.FALSE;
            settings.put(JetspeedSerializer.KEY_PROCESS_PERMISSIONS, value);
            if (value.booleanValue())
            {
                // export of permissions requires export of USERS too
                settings.put(JetspeedSerializer.KEY_PROCESS_USERS, Boolean.TRUE);
            }
            settings.put(JetspeedSerializer.KEY_PROCESS_PROFILER,
                         getNonNullActionParameter(requestContext, PROFILES).equalsIgnoreCase("y") ? Boolean.TRUE
                                                                                                  : Boolean.FALSE);
            settings.put(JetspeedSerializer.KEY_PROCESS_CAPABILITIES,
                         getNonNullActionParameter(requestContext, CAPABILITIES).equalsIgnoreCase("y") ? Boolean.TRUE
                                                                                                      : Boolean.FALSE);
            settings.put(JetspeedSerializer.KEY_PROCESS_ENTITIES,
                         getNonNullActionParameter(requestContext, ENTITIES).equalsIgnoreCase("y") ? Boolean.TRUE
                                                                                                  : Boolean.FALSE);
            value = getNonNullActionParameter(requestContext, USER_PREFS).equalsIgnoreCase("y") ? Boolean.TRUE : Boolean.FALSE;
            settings.put(JetspeedSerializer.KEY_PROCESS_USER_PREFERENCES, value);
            if (value.booleanValue())
            {
                // export of user preferences requires export of ENTITIES too
                settings.put(JetspeedSerializer.KEY_PROCESS_ENTITIES, Boolean.TRUE);
            }
            if (!cleanUserFolder(userName))
            {
                resultMap.put(STATUS, "failure");
                resultMap.put(REASON, "Could not create temp files on disk.");
                success = false;
                return success;
            }
            settings.put(JetspeedSerializer.KEY_EXPORT_INDENTATION, "\t");
            settings.put(JetspeedSerializer.KEY_OVERWRITE_EXISTING, Boolean.TRUE);
            settings.put(JetspeedSerializer.KEY_BACKUP_BEFORE_PROCESS, Boolean.FALSE);
            serializer.exportData("jetspeedadmin_export_process", exportFileName, settings);
            requestContext.getRequest().getSession().setAttribute("file", userName + "_ldapExport.xml");
            resultMap.put("link", getDownloadLink(requestContext, "tmpExport.xml", userName));
            resultMap.put(STATUS, status);
        }
        catch (Exception e)
        {
            // Log the exception
            log.error("exception while getting folder info", e);
            resultMap.put(STATUS, "failure");
            resultMap.put(REASON, e.getMessage());
            // Return a failure indicator
            success = false;
        }
        return success;
    }

    private String getDownloadLink(RequestContext requestContext, String ObjectName, String userName) throws Exception
    {
        String link = "";
        String basePath = requestContext.getRequest().getContextPath() + "/fileserver/_content/";
        link = basePath + userName + "/" + ObjectName;
        return link;
    }

    private boolean cleanUserFolder(String userName)
    {
        boolean success = false;
        
        synchronized (this)
        {
            File zipFile = new File(pageRoot + pathSeprator + userName + ".zip");
            
            if (zipFile.isFile())
                zipFile.delete();
            
            String folder = getUserFolder(userName, false);
            File dir = new File(folder);
            
            if (dir.isDirectory())
            {
                try
                {
                    FileUtils.cleanDirectory(dir);
                    success = true;
                }
                catch (IOException e)
                {
                }
            }
            else
            {
                success = dir.mkdir();
            }
        }
        
        return success;
    }

    private String getUserFolder(String userName, boolean fullPath)
    {
        if (pathSeprator == null || pathSeprator.equals(""))
            pathSeprator = "/";
        if (fullPath)
        {
            return userName + pathSeprator;
        }
        else
        {
            return pageRoot + pathSeprator + userName;
        }
    }
}
