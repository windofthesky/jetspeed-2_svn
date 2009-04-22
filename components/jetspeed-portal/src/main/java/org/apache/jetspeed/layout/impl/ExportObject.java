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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.request.RequestContext;
/**
 * Exporting the object using Ajax command
 * 
 * @author <a href="mailto:firevelocity@gmail.com">Vivek Kumar</a>
 * @version $Id$
 */
public class ExportObject extends BaseGetResourceAction implements AjaxAction, AjaxBuilder, Constants {
	protected Logger log = LoggerFactory.getLogger(GetFolderAction.class);

	protected PageManager castorPageManager;

	protected String pageRoot;

	private static final String OBJECT_NAME = "objName";

	private static final String OBJECT_TYPE = "objType";

	private static final String OBJECT_PATH = "objPath";

	private static final String RECURSIVE = "exptRecusive";
	String pathSeprator = System.getProperty("file.separator");
	public ExportObject(String template, String errorTemplate, PageManager pageManager,
			PortletActionSecurityBehavior securityBehavior, PageManager castorpagemanager, String dir) {
		super(template, errorTemplate, pageManager, securityBehavior);
		this.castorPageManager = castorpagemanager;
		this.pageRoot = dir;
	}

	public boolean run(RequestContext requestContext, Map resultMap) {
		boolean success = true;
		String status = "success";
		String userName = requestContext.getUserPrincipal().getName();
		try {
			resultMap.put(ACTION, "export");
			if (false == checkAccess(requestContext, JetspeedActions.VIEW)) {
				success = false;
				resultMap.put(REASON, "Insufficient access to get portlets");
				return success;
			}
			String objectName = getActionParameter(requestContext, OBJECT_NAME);
			String objectType = getActionParameter(requestContext, OBJECT_TYPE);
			String objectPath = getActionParameter(requestContext, OBJECT_PATH);
			String recursive = getActionParameter(requestContext, RECURSIVE);
			boolean isRecursive = recursive != null && recursive.equals("1") ? true : false;

			if (!cleanUserFolder(userName))
				success = false;
			if (success) {
				if (objectType.equalsIgnoreCase("folder")) {
					Folder folder = pageManager.getFolder(objectPath);
					if (isRecursive) {
							importFolder(folder,userName,getRealPath(folder.getPath()));
					} else {
						Folder destFolder = castorPageManager.copyFolder(folder, getUserFolder(userName, true)
								+ objectName);
						castorPageManager.updateFolder(destFolder);
					}
				} else if (objectType.equalsIgnoreCase("page")) {
					objectPath = getParentPath(objectPath);
					Folder folder = pageManager.getFolder(objectPath);
					Page page = folder.getPage(objectName);
					Page destPage = castorPageManager.copyPage(page, getUserFolder(userName, true) + objectName);
					castorPageManager.updatePage(destPage);
				} else if (objectType.equalsIgnoreCase("link")) {
					objectPath = getParentPath(objectPath);
					Folder folder = pageManager.getFolder(objectPath);
					Link link = folder.getLink(objectName);
					Link destLink = castorPageManager.copyLink(link, getUserFolder(userName, true) + objectName);
					castorPageManager.updateLink(destLink);
				}
				String link = userName + "_" + objectName;
				if (objectType.equalsIgnoreCase("folder")) link = userName + ".zip";
				requestContext.getRequest().getSession().setAttribute("file", link);
				resultMap.put("link", getDownloadLink(requestContext, objectName, userName, objectType));
			}
			if (!success)
				status = "failure";

			resultMap.put(STATUS, status);
		} catch (Exception e) {
			// Log the exception
			e.printStackTrace();
			log.error("exception while getting folder info", e);

			// Return a failure indicator
			success = false;
		}

		return success;
	}

	private String getDownloadLink(RequestContext requestContext, String ObjectName, String userName, String objectType)
			throws Exception {
		String link = "";
		String basePath = requestContext.getRequest().getContextPath() + "/fileserver/_content/";
		if (objectType.equalsIgnoreCase("folder")) {
			String sourcePath = getUserFolder(userName, false);
			String target = sourcePath + ".zip";
			boolean success = zipObject(sourcePath, target);
			if (!success)
				throw new Exception("Error Occurered in zipping the file");

			link = basePath + ObjectName+".zip";
		} else {
			link = basePath + userName + "/" +  ObjectName;
		}
		return link;
	}

	private boolean cleanUserFolder(String userName) {
		boolean success = false;
		synchronized (this) {
			String folder = getUserFolder(userName, false);
			File dir = new File(pageRoot+pathSeprator+userName+".zip");
			if(dir.exists()) dir.delete();
			
			dir = new File(folder);
			if (dir.exists()) {
                success = deleteDir(dir);
			} 
            success = dir.mkdir();
		}
		return success;
	}

    private boolean deleteDir(File dir) {
        if( dir.exists() ) {
            File[] files = dir.listFiles();
            for(int i=0; i<files.length; i++) {
               if(files[i].isDirectory()) {
                   deleteDir(files[i]);
               }
               else {
                 files[i].delete();
               }
            }
          }
          return( dir.delete() );
	}

	private String getUserFolder(String userName, boolean fullPath) {
		if (pathSeprator == null || pathSeprator.equals(""))
			pathSeprator = "/";
		if (fullPath) {
			return userName + pathSeprator;
		} else {
			return pageRoot + pathSeprator + userName;
		}
	}

	private String getParentPath(String path) {
		int index = path.lastIndexOf("/");

		if (index == 0) {
			return "/";
		} else {
			return path.substring(0, index);
		}
	}

	private boolean zipObject(String sourcePath, String target) {
		ZipOutputStream cpZipOutputStream = null;
		try {
			File cpFile = new File(sourcePath);
			if (!cpFile.isDirectory()) {
				return false;
			}
			cpZipOutputStream = new ZipOutputStream(new FileOutputStream(target));
			cpZipOutputStream.setLevel(9);
			zipFiles(cpFile, sourcePath, cpZipOutputStream);
			cpZipOutputStream.finish();
			cpZipOutputStream.close();
		} catch (Exception e) {	
			e.printStackTrace();
			return false;
		}
		finally{
		}
		return true;
	}

	private void zipFiles(File cpFile, String sourcePath, ZipOutputStream cpZipOutputStream) {

		if (cpFile.isDirectory()) {
			File[] fList = cpFile.listFiles();
			for (int i = 0; i < fList.length; i++) {
				zipFiles(fList[i], sourcePath, cpZipOutputStream);
			}
		} else {
			try {
				String strAbsPath = cpFile.getAbsolutePath();
				String strZipEntryName = strAbsPath.substring(sourcePath.length() + 1, strAbsPath.length());
				byte[] b = new byte[(int) (cpFile.length())];
				FileInputStream cpFileInputStream = new FileInputStream(cpFile);
				int i = cpFileInputStream.read(b, 0, (int) cpFile.length());
				ZipEntry cpZipEntry = new ZipEntry(strZipEntryName);
				cpZipOutputStream.putNextEntry(cpZipEntry);
				cpZipOutputStream.write(b, 0, (int) cpFile.length());
				cpZipOutputStream.closeEntry();
				cpFileInputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private Folder importFolder(Folder srcFolder,String userName,String destination) throws JetspeedException {
        String newPath="";
        Folder dstFolder = lookupFolder(srcFolder.getPath());
		dstFolder = castorPageManager.copyFolder(srcFolder, getUserFolder(userName, true) + destination);
		castorPageManager.updateFolder(dstFolder);

		Iterator pages = srcFolder.getPages().iterator();
		while (pages.hasNext()) {
			Page srcPage = (Page) pages.next();
			Page dstPage = lookupPage(srcPage.getPath());
            newPath = getUserFolder(userName, true) +destination+ getRealPath(srcPage.getPath()); 
			dstPage = castorPageManager.copyPage(srcPage, newPath);
			castorPageManager.updatePage(dstPage);
		}

		Iterator links = srcFolder.getLinks().iterator();
		while (links.hasNext()) {
			Link srcLink = (Link) links.next();
			Link dstLink = lookupLink(srcLink.getPath());
            newPath = getUserFolder(userName, true) +destination+ getRealPath(srcLink.getPath());
			dstLink = castorPageManager.copyLink(srcLink,newPath);
			castorPageManager.updateLink(dstLink);
		}
		Iterator folders = srcFolder.getFolders().iterator();
		while (folders.hasNext()) {
			Folder folder = (Folder) folders.next();
            newPath = destination+getRealPath(folder.getPath());
			importFolder(folder,userName,newPath);
		}

		return dstFolder;
	}

	private Page lookupPage(String path) {
		try {
			return castorPageManager.getPage(path);
		} catch (Exception e) {
			return null;
		}
	}

	private Link lookupLink(String path) {
		try {
			return castorPageManager.getLink(path);
		} catch (Exception e) {
			return null;
		}
	}

	private Folder lookupFolder(String path) {
		try {
			return castorPageManager.getFolder(path);
		} catch (Exception e) {
			return null;
		}
	}
    private String getRealPath(String path){
        int index = path.lastIndexOf("/");
        if (index>0)
        {
            return path.substring(index);
        }
        return path;
         
    }
}
