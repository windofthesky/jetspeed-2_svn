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
package org.apache.jetspeed.tools.deploy;

import java.io.File;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.zip.ZipEntry;

import org.apache.commons.lang.ArrayUtils;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.security.JSSubject;
import org.apache.jetspeed.security.UserManager;

/**
 * @author vkumar <a href="vkumar@apache.org">Vivek Kumar</a>
 */
public class JetspeedDeployUtils
{
    PageManager pageManager = (PageManager) Jetspeed.getComponentManager().getComponent("org.apache.jetspeed.page.PageManager");
    UserManager userManager = (UserManager) Jetspeed.getComponentManager().getComponent("org.apache.jetspeed.security.UserManager");
    PageManager xmlPageManager = (PageManager) Jetspeed.getComponentManager().getComponent("org.apache.jetspeed.page.CastorPageManager");

    class ZipObject
    {
        public ZipObject()
        {
        }

        String parents[] = null;
        String name;

        /**
         * @return the parents
         */
        public String[] getParents()
        {
            return parents;
        }

        /**
         * @param parents
         *            the parents to set
         */
        public void setParents(String[] parents)
        {
            this.parents = parents;
        }

        /**
         * @return the name
         */
        public String getName()
        {
            return name;
        }

        /**
         * @param name
         *            the name to set
         */
        public void setName(String name)
        {
            this.name = name;
        }
    }

    public ZipObject formatZipEntryName(ZipEntry entry)
    {
        String tempName = null;
        String tempPath[] = null;
        ZipObject zipObj = this.new ZipObject();
        tempPath = entry.getName().split("/");
        if (tempPath.length >= 2)
        {
            tempName = tempPath[tempPath.length - 1];
        }
        else
        {
            tempName = tempPath[0];
        }
        zipObj.setName(tempName);
        tempName = entry.getName();
        tempPath = tempName.split("/");
        if (tempPath.length == 2)
        {
            tempPath = (String[]) ArrayUtils.remove(tempPath, 0);
            zipObj.setParents(tempPath);
        }
        else if (tempPath.length > 2)
        {
            tempPath = (String[]) ArrayUtils.remove(tempPath, 0);
            tempPath = (String[]) ArrayUtils.remove(tempPath, tempPath.length - 1);
            zipObj.setParents(tempPath);
        }
        else
        {
            zipObj.setParents(tempPath);
        }
        return zipObj;
    }

    public String getFullPath(String basePath, String paths[], String name)
    {
        String tempPath = "";
        for (String path : paths)
        {
            tempPath = tempPath + "/" + path;
        }
        tempPath = basePath + tempPath + "/" + name;
        return tempPath;
    }

    public void createPath(String basePath, String paths[])
    {
        String tempPath;
        File tempFile;
        if (paths.length >= 1)
        {
            tempPath = basePath + "/" + paths[0];
            tempFile = new File(tempPath);
            if (!tempFile.exists())
            {
                tempFile.mkdir();
            }
            for (int counter = 1; counter < paths.length; counter++)
            {
                tempPath = tempPath + "/" + paths[counter];
                tempFile = new File(tempPath);
                if (!tempFile.exists())
                {
                    tempFile.mkdir();
                }
            }
        }
    }

    private Folder importFolders(Folder srcFolder, String destination, boolean root) throws JetspeedException
    {
        Folder dstFolder = null;
        if (!root)
        {
            dstFolder = pageManager.copyFolder(srcFolder, destination);
            pageManager.updateFolder(dstFolder);
        }
        String newPath = "";
        Iterator pages = srcFolder.getPages().iterator();
        while (pages.hasNext())
        {
            Page srcPage = (Page) pages.next();
            newPath = destination + "/" + srcPage.getName();
            Page dstPage = pageManager.copyPage(srcPage, newPath);
            pageManager.updatePage(dstPage);
        }
        Iterator links = srcFolder.getLinks().iterator();
        while (links.hasNext())
        {
            Link srcLink = (Link) links.next();
            newPath = destination + "/" + srcLink.getName();
            Link dstLink = pageManager.copyLink(srcLink, newPath);
            pageManager.updateLink(dstLink);
        }
        Iterator folders = srcFolder.getFolders().iterator();
        while (folders.hasNext())
        {
            Folder folder = (Folder) folders.next();
            newPath = destination + "/" + folder.getName();
            importFolders(folder, newPath, false);
        }
        return dstFolder;
    }

    public void importJetspeedObjects(String importPsmlFolder)
    {
        String powerUser = "admin";
        try
        {
            JetspeedException pe = (JetspeedException) JSSubject.doAsPrivileged(userManager.getSubject(userManager.getUser(powerUser)), new PrivilegedAction()
            {
                public Object run()
                {
                    try
                    {
                        Folder srcFolder = xmlPageManager.getFolder("psml");
                        importFolders(srcFolder, "/", true);
                    }
                    catch (Exception e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    return null;
                }
            }, null);
        }
        catch (JetspeedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public boolean deleteDirectory(File path) {
        if( path.exists() ) {
          File[] files = path.listFiles();
          for(int i=0; i<files.length; i++) {
             if(files[i].isDirectory()) {
               deleteDirectory(files[i]);
             }
             else {
               files[i].delete();
             }
          }
        }
        return( path.delete() );
      }

}
