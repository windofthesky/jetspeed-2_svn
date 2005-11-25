/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.apache.portals.bridges.common.GenericServletPortlet;
import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;


/**
 * Abstract Tree Portlet
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */

public class AbstractPSMLTreePortlet extends GenericServletPortlet
{    
    protected PageManager pageManager;
    protected PSMLTreeLoader loader;
    /** the PSML Root, i.e "/" or "/_users/joe" **/
    protected String psmlRoot;

    /** image map for content type **/
    protected Map imageMap = new HashMap();
    
    protected String linkImage;
    protected String folderImage;
    protected String documentImage;
    protected String rootImage;
    protected String rootLabel;
    
    
    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
        
        pageManager = (PageManager) getPortletContext().getAttribute(CommonPortletServices.CPS_PAGE_MANAGER_COMPONENT);
        if (null == pageManager) { throw new PortletException(
                "Failed to find the Page Manager on portlet initialization"); }
        
        loader = new PSMLTreeLoader(pageManager, this);
        psmlRoot = config.getInitParameter("psmlRoot");
        if (psmlRoot == null)
        {
            psmlRoot = "/";
        }
        
        // Images        
        String extensions = config.getInitParameter("extensions");
        String images = config.getInitParameter("images");
        linkImage = config.getInitParameter("linkImage");
        folderImage = config.getInitParameter("folderImage");
        documentImage = config.getInitParameter("documentImage");
        rootImage = config.getInitParameter("rootImage");
        rootLabel = config.getInitParameter("rootLabel");

        if (extensions == null)
        {
            extensions = "text/html,text/plain,application/pdf";
        }
        if (images == null)
        {
            images = "html-document.gif, text-document.gif, pdficon.jpg";
        }
        if (linkImage == null)
        {
            linkImage = "link.gif";
        }
        if (folderImage == null)
        {
            folderImage = "folder.gif";
        }
        if (documentImage == null)
        {
            documentImage = "document.gif";
        }
        if (rootImage == null)
        {
            rootImage = "root.gif";
        }
        if (rootLabel == null)
        {
            rootLabel = "Bookshelf";
        }
        
        String[]ext = stringToArray(extensions, ", ");
        String[]img = stringToArray(images, ", ");
        int max = (ext.length > img.length) ? img.length : ext.length;
        for (int ix = 0; ix < max; ix++) 
        {
            imageMap.put(ext[ix], img[ix]);
        }
    }

    public static final String[] stringToArray(String str, String separators)
    {
        StringTokenizer tokenizer;
        String[] array = null;
        int count = 0;

        if (str == null)
            return array;

        if (separators == null)
            separators = ", ";

        tokenizer = new StringTokenizer(str, separators);
        if ((count = tokenizer.countTokens()) <= 0) {
            return array;
        }
        
        array = new String[count];
        
        int ix = 0;
        while (tokenizer.hasMoreTokens()) 
        {
            array[ix] = tokenizer.nextToken();
            ix++;
        }

        return array;
    }
    
    /**
     * @return Returns the linkImage.
     */
    public String getLinkImage()
    {
        return linkImage;
    }
    /**
     * @return Returns the folderImage.
     */
    public String getFolderImage(Folder folder)
    {
        if (folder.isReserved())
        {
            if (folder.getReservedType() == Folder.RESERVED_FOLDER_USERS)
                return "users.gif";
            else if (folder.getReservedType() == Folder.RESERVED_FOLDER_ROLES)
                return "roles.gif";
            else if (folder.getReservedType() == Folder.RESERVED_FOLDER_GROUPS)
                return "groups.gif";
            else if (folder.getReservedType() == Folder.RESERVED_FOLDER_SUBSITES)
                return "subsites.gif";
            else
                return "other.gif";
        }
        return folderImage;
    }
    
    public String getImageForContentType(String contentType)
    {
        String ct = (String)imageMap.get(contentType);
        if (ct == null)
            return documentImage;
        return ct;
    }
    /**
     * @return Returns the documentImage.
     */
    public String getDocumentImage()
    {
        return documentImage;
    }
    /**
     * @return Returns the rootImage.
     */
    public String getRootImage()
    {
        return rootImage;
    }
    /**
     * @return Returns the rootLabel.
     */
    public String getRootLabel()
    {
        return rootLabel;
    }
    
    public String getFolderTitle(Folder folder, Locale locale)
    {
        String title = folder.getTitle(locale);
        if (title == null)
            title = folder.getName();
        return title;
    }
    
    public String getPageTitle(Page page, Locale locale)
    {
        String title = page.getTitle(locale);
        if (title == null)
        {
            title = page.getName();
        }                
        return title;
    }
    
}
