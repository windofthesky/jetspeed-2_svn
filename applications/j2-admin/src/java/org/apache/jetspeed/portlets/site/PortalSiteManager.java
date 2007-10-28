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
package org.apache.jetspeed.portlets.site;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.portlet.PortletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.decoration.DecorationFactory;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.headerresource.HeaderResource;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.request.RequestContext;
import org.apache.portals.gems.dojo.AbstractDojoVelocityPortlet;

/**
 * Manage the Portal Site
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class PortalSiteManager extends AbstractDojoVelocityPortlet 
{
    protected final Log log = LogFactory.getLog(this.getClass());
    
    // components
    protected PageManager pageManager;
    protected PortletRegistry registry;
    protected DecorationFactory decorationFactory;
    
    // session
    protected final static String SESSION_FOLDERS = "jetspeed.site.manager.folders";
    protected final static String SESSION_ROOT = "jetspeed.site.manager.root";
    
    // context
    public final static String FOLDERS = "folders";
    public final static String JSROOT = "jsroot";   
    public static final String ALL_SECURITY_REFS = "allSecurityRefs";
    
	protected PageManager castorPageManager;
    
    public void init(PortletConfig config)
    throws PortletException 
    {
        super.init(config);
        pageManager = (PageManager) getPortletContext().getAttribute(CommonPortletServices.CPS_PAGE_MANAGER_COMPONENT);        
        if (null == pageManager) 
        { 
            PortletException pe = new PortletException("Failed to find the Page Manager on SiteViewController initialization");
            throw new RuntimeException(pe); 
        }
        registry = (PortletRegistry) getPortletContext().getAttribute(CommonPortletServices.CPS_REGISTRY_COMPONENT);
        if (null == registry)
        {
            PortletException pe = new PortletException("Failed to find the Portlet Registry on SiteViewController initialization");
            throw new RuntimeException(pe);             
        }
        decorationFactory = (DecorationFactory) getPortletContext().getAttribute(CommonPortletServices.CPS_DECORATION_FACTORY);
        if (null == decorationFactory)
        {
            PortletException pe = new PortletException("Failed to find the Decoration Factory on SiteViewController initialization");
            throw new RuntimeException(pe);             
        }
        castorPageManager = (PageManager) getPortletContext().getAttribute(CommonPortletServices.CPS_IMPORTER_MANAGER);
        if (null == castorPageManager) {
            PortletException pe = new PortletException(
                    "Failed to find the castorPageManager on SiteViewController initialization");
            throw new RuntimeException(pe);
        }
    }
    
    public void doView(RenderRequest request, RenderResponse response)
            throws PortletException, IOException
    {
        try
        {
            String jsroot = determineRootFolder(request);
            RequestContext requestContext = 
                (RequestContext) request.getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
            this.getContext(request).put("page-decorations", decorationFactory.getPageDecorations(requestContext));
            this.getContext(request).put("portlet-decorations", decorationFactory.getPortletDecorations(requestContext));
            this.getContext(request).put("themes", decorationFactory.getDesktopPageDecorations(requestContext));
            this.getContext(request).put("treeName", "portal");
            this.getContext(request).put("userTree", determineuserTree(request));
            this.getContext(request).put("defaultLayout", request.getPreferences().getValue("defaultLayout", "jetspeed-layouts::VelocityTwoColumns"));            
            this.getContext(request).put(FOLDERS, retrieveFolders(request, jsroot));
            this.getContext(request).put(ALL_SECURITY_REFS, pageManager.getPageSecurity().getSecurityConstraintsDefs());
            if(request.getPortletSession().getAttribute("status") ==null){
                request.getPortletSession().setAttribute("status","");
            }
        }
        catch (Exception e)
        {
            log.error("Failed to get root folder", e);
            throw new PortletException("Failed to get root folder");
        }
        
        super.doView(request, response);
        request.getPortletSession().removeAttribute("status");
    }
    
    protected String determineRootFolder(RenderRequest request)
    {
        String jsroot = request.getParameter(JSROOT);
        if (jsroot == null || jsroot.equals(""))
        {
            jsroot = request.getPreferences().getValue("root", "/_user/" + request.getRemoteUser() + "/");
        }
        this.getContext(request).put(JSROOT, jsroot);
        return jsroot;
    }
    protected String determineuserTree(RenderRequest request)
    {
    	String userTree;
    	userTree = request.getPreferences().getValue("displayUserTree","false");
    	return userTree;
    }
    public Folder retrieveFolders(RenderRequest request, String root)
    throws PortletException
    {
        try
        {
            Folder folder = pageManager.getFolder(root);
            return folder;
        }
        catch (Exception e)
        {
            log.error("Failed to retrieve folders ", e);
            throw new PortletException("Failed to get root folder");
        }
    }
    
    protected void includeHeaderContent(HeaderResource headerResource)
    {
        headerResource.dojoAddCoreLibraryRequire("dojo.lang.*");
        //headerResource.dojoAddCoreLibraryRequire("dojo.dnd.*");
        headerResource.dojoAddCoreLibraryRequire("dojo.dnd.HtmlDragManager");
        headerResource.dojoAddCoreLibraryRequire("dojo.dnd.DragAndDrop");
        headerResource.dojoAddCoreLibraryRequire("dojo.dnd.HtmlDragAndDrop");
        
        headerResource.dojoAddCoreLibraryRequire("dojo.event.*");
        headerResource.dojoAddCoreLibraryRequire("dojo.io");
                
        headerResource.dojoAddCoreLibraryRequire("dojo.widget.ContentPane");
        headerResource.dojoAddCoreLibraryRequire("dojo.widget.LayoutContainer");
        
        headerResource.dojoAddCoreLibraryRequire("dojo.widget.Tree");
        headerResource.dojoAddCoreLibraryRequire("dojo.widget.TreeRPCController");        
        // headerResource.dojoAddCoreLibraryRequire("dojo.widget.TreeLoadingControllerV3");
        headerResource.dojoAddCoreLibraryRequire("dojo.widget.TreeSelector");
        headerResource.dojoAddCoreLibraryRequire("dojo.widget.TreeNode");
        headerResource.dojoAddCoreLibraryRequire("dojo.widget.TreeContextMenu");
        
        headerResource.dojoAddCoreLibraryRequire("dojo.widget.ValidationTextbox");
        headerResource.dojoAddCoreLibraryRequire("dojo.widget.ComboBox");
        headerResource.dojoAddCoreLibraryRequire("dojo.widget.Checkbox");
        headerResource.dojoAddCoreLibraryRequire("dojo.widget.Dialog");
        headerResource.dojoAddCoreLibraryRequire("dojo.widget.Button");
        
        headerResource.dojoAddModuleLibraryRequire( "jetspeed.desktop.core" );
        headerResource.dojoAddModuleLibraryRequire( "jetspeed.widget.EditorTable" );        
    }

    public void doEdit(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        response.setContentType("text/html");
        doPreferencesEdit(request, response);
    }
    
    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException, java.io.IOException
    {
        String add = request.getParameter("Save");
        String fileName ="";
        String destPath="";
        String fileType="";
        String path="";
        String usrFolder="";
        boolean success = false;

        if (add != null)
        { 
            processPreferencesAction(request, actionResponse);
        } else {
            cleanUserFolder(request.getUserPrincipal().toString());
            try {
                DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
                PortletFileUpload portletFileUpload = new PortletFileUpload(diskFileItemFactory);
                if (PortletFileUpload.isMultipartContent(request)) {
                    Iterator fileIt = portletFileUpload.parseRequest(request).iterator();
                    while (fileIt.hasNext()) {
                        FileItem fileItem = (FileItem) fileIt.next();
                        if (fileItem.getFieldName().equals("psmlFile")) {
                            synchronized (this) {
                                fileName = fileItem.getName();
                                usrFolder = getTempFolder(request);
                                path= System.getProperty("file.separator");
                                String filePath = usrFolder + path + fileItem.getName();
                                FileOutputStream out = new FileOutputStream(filePath);
                                out.write(fileItem.get());
                                out.close();
                            }
                        }else if(fileItem.isFormField() && fileItem.getFieldName().equalsIgnoreCase("importPath")){
                            destPath= fileItem.getString();
                        }  
                    }
                    fileType = fileExt(fileName);
                    if (fileType != null && !fileType.equals("")&& fileName != null && !fileName.equals("") && destPath != null && !destPath.equals("")) {
                        Folder folder = castorPageManager.getFolder(request.getUserPrincipal().toString());
                        if(fileType.equalsIgnoreCase("psml")){
                            Page source = folder.getPage(fileName);
                            Page page = pageManager.copyPage(source, destPath + "/" + fileName);
                            pageManager.updatePage(page);
                            success = true;
                        }else if(fileType.equalsIgnoreCase("link")){
                            Link source = folder.getLink(fileName);
                            Link page = pageManager.copyLink(source, destPath + "/" + fileName);
                            pageManager.updateLink(page);
                            success = true;
                        }else if(fileType.equalsIgnoreCase("zip")){
                            unzipfile(fileName,usrFolder + path,path);
                            folder = castorPageManager.getFolder(request.getUserPrincipal().toString());
                            importFolders(folder, request.getUserPrincipal().toString(), destPath);
                            success = true;
                        }
                    }
                }
                if (success){
                    request.getPortletSession().setAttribute("status",fileName);
                 }else{
                    request.getPortletSession().setAttribute("status","false"); 
                 }
            } catch (Exception e) {
                request.getPortletSession().setAttribute("status","false");
                //throw new PortletException("Error occured in file uplodad");
            }
        }

    }    
	private String fileExt(String fileName){
	    int extIndex = fileName.lastIndexOf(".");
        if(extIndex>0){
            return fileName.substring(extIndex+1, fileName.length());
        }
        return "";
    }
    private String getTempFolder(ActionRequest request) {
		String dir = System.getProperty("java.io.tmpdir");
		String path = System.getProperty("file.separator");
		File file = new File(dir + path + request.getUserPrincipal());
		file.mkdir();
		return dir + path + request.getUserPrincipal();
    }

    private static final void copyInputStream(InputStream in, OutputStream out)
    throws IOException
    {
      byte[] buffer = new byte[1024];
      int len;

      while((len = in.read(buffer)) >= 0)
        out.write(buffer, 0, len);

      in.close();
      out.close();
    }

    private boolean  unzipfile(String file,String destination,String sepreator) {
      Enumeration entries;
      String filePath="";
      try {
          ZipFile zipFile = new ZipFile(destination+sepreator+file);

        entries = zipFile.entries();

        while(entries.hasMoreElements()) {
          ZipEntry entry = (ZipEntry)entries.nextElement();
          filePath = destination+sepreator+entry.getName();
          createPath(filePath);
          copyInputStream(zipFile.getInputStream(entry),
             new BufferedOutputStream(new FileOutputStream(filePath)));
        }

        zipFile.close();
        return true;
      } catch (IOException ioe) {
        ioe.printStackTrace();
        return false;
      }
    }
    
    private void createPath(String filePath) {
        String parentPath="";
        File file = new File(filePath);
        File parent = new File(file.getParent());
        if (!parent.exists()) {
            parentPath = parent.getPath();
            createPath(parentPath);
            parent.mkdir();
        }
    }
    private Folder importFolders(Folder srcFolder,String userName,String destination) throws JetspeedException {
        Folder dstFolder = lookupFolder(srcFolder.getPath());
        dstFolder = pageManager.copyFolder(srcFolder,destination);
        pageManager.updateFolder(dstFolder);
        String newPath="";
        Iterator pages = srcFolder.getPages().iterator();
        while (pages.hasNext()) {
            Page srcPage = (Page) pages.next();
            Page dstPage = lookupPage(srcPage.getPath());
            newPath = destination+getRealPath(srcPage.getPath());
            dstPage = pageManager.copyPage(srcPage,newPath);
            pageManager.updatePage(dstPage);
        }

        Iterator links = srcFolder.getLinks().iterator();
        while (links.hasNext()) {
            Link srcLink = (Link) links.next();
            Link dstLink = lookupLink(srcLink.getPath());
            newPath = destination+getRealPath(srcLink.getPath());
            dstLink = pageManager.copyLink(srcLink, newPath);
            pageManager.updateLink(dstLink);
        }
        Iterator folders = srcFolder.getFolders().iterator();
        while (folders.hasNext()) {
            Folder folder = (Folder) folders.next();
            newPath = destination+getRealPath(folder.getPath());
            importFolders(folder,userName, newPath );
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
    private boolean cleanUserFolder( String userName) {
        boolean success = false;
        synchronized (this) {
            String tmpdir = System.getProperty("java.io.tmpdir");
            String path = System.getProperty("file.separator");
            String folder = tmpdir + path + userName; 
            File  dir = new File(folder);
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

}
