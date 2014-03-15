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
package org.apache.jetspeed.page.document.impl;

import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.PageMetadataImpl;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.impl.BaseElementImpl;
import org.apache.jetspeed.om.page.impl.SecurityConstraintsImpl;
import org.apache.jetspeed.om.portlet.GenericMetadata;
import org.apache.jetspeed.om.portlet.LocalizedField;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.impl.DatabasePageManagerUtils;
import org.apache.ojb.broker.core.proxy.ProxyHelper;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * NodeImpl
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public abstract class NodeImpl extends BaseElementImpl implements Node
{
    private Integer parentId;
    private Node parent;
    private boolean hidden;
    private Collection<LocalizedField> metadataFields;
    private String path = Folder.PATH_SEPARATOR;
    private String subsite;
    private String user;
    private String role;
    private String group;
    private String mediatype;
    private String locale;
    private String extendedAttributeName;
    private String extendedAttributeValue;

    private PageMetadataImpl pageMetadata;
    private String logicalPath;

    public NodeImpl(SecurityConstraintsImpl constraints)
    {
        super(constraints);
    }

    /**
     * getCanonicalNodePath
     *
     * Format paths used to set and query NodeImpl instances.
     *
     * @param path specified path
     * @return canonical path
     */
    public static String getCanonicalNodePath(String path)
    {
        // validate and format path
        if ((path == null) || (path.length() == 0))
        {
            path = Folder.PATH_SEPARATOR;
        }
        if (!path.startsWith(Folder.PATH_SEPARATOR))
        {
            path = Folder.PATH_SEPARATOR + path;
        }
        if (path.endsWith(Folder.PATH_SEPARATOR) && !path.equals(Folder.PATH_SEPARATOR))
        {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    /**
     * newPageMetadata
     *
     * Construct page manager specific metadata implementation.
     *
     * @param fields mutable fields collection
     * @return page metadata
     */
    public PageMetadataImpl newPageMetadata(Collection<LocalizedField> fields)
    {
        // no metadata available by default
        return null;
    }

    /**
     * getPageMetadata
     *
     * Get page manager specific metadata implementation.
     *
     * @return page metadata
     */
    public PageMetadataImpl getPageMetadata()
    {
        if (pageMetadata == null)
        {
            if (metadataFields == null)
            {
                metadataFields = DatabasePageManagerUtils.createCollection();
            }
            pageMetadata = newPageMetadata(metadataFields);
        }
        return pageMetadata;
    }

    /**
     * defaultTitleFromName
     *
     * Compute default title from name.
     *
     * @return default title
     */
    protected String defaultTitleFromName()
    {
        // transform name to title
        String title = getName();
        if (title != null)
        {
            // strip extensions and default root folder name
            if ((getType() != null) && title.endsWith(getType()))
            {
                title = title.substring(0, title.length()-getType().length());
            }
            else if (title.equals(Folder.PATH_SEPARATOR))
            {
                title = "top";
            }
            // use space as word separator
            title = title.replace('_', ' ');
            title = title.replace('-', ' ');
            title = title.trim();
            // use title case for title words
            int wordIndex = -1;
            do
            {
                if (!Character.isTitleCase(title.charAt(wordIndex+1)))
                {
                    StringBuffer makeTitle = new StringBuffer();
                    makeTitle.append(title.substring(0, wordIndex+1));
                    makeTitle.append(Character.toTitleCase(title.charAt(wordIndex+1)));
                    makeTitle.append(title.substring(wordIndex+2));
                    title = makeTitle.toString();
                }
                wordIndex = title.indexOf(' ', wordIndex+1);
            }
            while (wordIndex != -1);
        }
        return title;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.impl.BaseElementImpl#getName()
     */
    public String getName()
    {
        // get name or compute from path
        String name = super.getName();
        if (name == null)
        {
            if (path != null)
            {
                if (!path.equals(Folder.PATH_SEPARATOR))
                {
                    name = path.substring(path.lastIndexOf(Folder.PATH_SEPARATOR) + 1);
                }
                else
                {
                    name = Folder.PATH_SEPARATOR;
                }
                super.setName(name);
            }
        }
        return name;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.impl.BaseElementImpl#setName(java.lang.String)
     */
    public void setName(String name)
    {
        // set path based on name
        if (name != null)
        {
            if (path != null)
            {
                // set path
                if (!name.equals(Folder.PATH_SEPARATOR))
                {
                    path = path.substring(0, path.lastIndexOf(Folder.PATH_SEPARATOR) + 1) + name;
                }
                else
                {
                    path = Folder.PATH_SEPARATOR;
                }

                // reset logicalPath
                logicalPath = null;
            }
            super.setName(name);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.impl.BaseElementImpl#getEffectivePageSecurity()
     */
    public PageSecurity getEffectivePageSecurity()
    {
        // by default, delegate to real parent node implementation
        NodeImpl parentNodeImpl = (NodeImpl)ProxyHelper.getRealObject(parent);
        if (parentNodeImpl != null)
        {
            return parentNodeImpl.getEffectivePageSecurity();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.impl.BaseElementImpl#checkConstraints(java.util.List, java.util.List, java.util.List, java.util.List, boolean, boolean)
     */
    public void checkConstraints(List<String> actions, List<String> userPrincipals, List<String> rolePrincipals, List<String> groupPrincipals, boolean checkNodeOnly, boolean checkParentsOnly) throws SecurityException
    {
        // check constraints in node hierarchy
        if (checkNodeOnly)
        {
            // check node constraints if available; otherwise,
            // recursively check parent constraints until
            // default constraints for node are checked
            SecurityConstraintsImpl constraintsImpl = (SecurityConstraintsImpl)getSecurityConstraints();
            if ((constraintsImpl != null) && !constraintsImpl.isEmpty())
            {
                constraintsImpl.checkConstraints(actions, userPrincipals, rolePrincipals, groupPrincipals, getEffectivePageSecurity());
            }
            else
            {
                NodeImpl parentNodeImpl = (NodeImpl)ProxyHelper.getRealObject(parent);
                if (parentNodeImpl != null)
                {
                    parentNodeImpl.checkConstraints(actions, userPrincipals, rolePrincipals, groupPrincipals, checkNodeOnly, false);
                }
            }
        }
        else
        {
            // check node constraints if available and not
            // to be skipped due to explicity granted access
            if (!checkParentsOnly)
            {
                SecurityConstraintsImpl constraintsImpl = (SecurityConstraintsImpl)getSecurityConstraints();
                if ((constraintsImpl != null) && !constraintsImpl.isEmpty())
                {
                    constraintsImpl.checkConstraints(actions, userPrincipals, rolePrincipals, groupPrincipals, getEffectivePageSecurity());
                }
            }

            // recursively check all parent constraints in hierarchy
            NodeImpl parentNodeImpl = (NodeImpl)ProxyHelper.getRealObject(parent);
            if (parentNodeImpl != null)
            {
                parentNodeImpl.checkConstraints(actions, userPrincipals, rolePrincipals, groupPrincipals, false, false);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.impl.BaseElementImpl#checkPermissions(java.lang.String, int, boolean, boolean)
     */
    public void checkPermissions(String path, int mask, boolean checkNodeOnly, boolean checkParentsOnly) throws SecurityException
    {
        // check granted node permissions unless the check is
        // to be skipped due to explicity granted access
        if (!checkParentsOnly)
        {
            super.checkPermissions(path, mask, true, false);
        }
        
        // if not checking node only, recursively check
        // all parent permissions in hierarchy
        if (!checkNodeOnly)
        {
            NodeImpl parentNodeImpl = (NodeImpl)ProxyHelper.getRealObject(parent);
            if (parentNodeImpl != null)
            {
                parentNodeImpl.checkPermissions(mask, false, false);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.impl.BaseElementImpl#getLogicalPermissionPath()
     */
    public String getLogicalPermissionPath()
    {
        // compute logical path if required
        if (logicalPath == null)
        {
            // check for path attributes
            if ((subsite != null) || (user != null) || (role != null) || (group != null) || (mediatype != null) ||
                (locale != null) || (extendedAttributeName != null) || (extendedAttributeValue != null))
            {
                // parse path, stripping reserved folders from path
                boolean skipAttribute = false;
                StringBuffer logicalPathBuffer = new StringBuffer();
                StringTokenizer pathElements = new StringTokenizer(path, Folder.PATH_SEPARATOR);
                while (pathElements.hasMoreTokens())
                {
                    // classify path element
                    String pathElement = pathElements.nextToken();
                    if (!skipAttribute)
                    {
                        if (!pathElement.startsWith(Folder.RESERVED_SUBSITE_FOLDER_PREFIX))
                        {
                            if (!pathElement.startsWith(Folder.RESERVED_FOLDER_PREFIX))
                            {
                                // append to logical path
                                logicalPathBuffer.append(Folder.PATH_SEPARATOR);
                                logicalPathBuffer.append(pathElement);
                            }
                            else
                            {
                                // skip next attribute path element
                                skipAttribute = true;
                            }
                        }
                    }
                    else
                    {
                        // attribute path element skipped
                        skipAttribute = false;
                    }
                }
                
                // set logical path
                if (logicalPathBuffer.length() > 0)
                {
                    logicalPath = logicalPathBuffer.toString();
                }
                else
                {
                    logicalPath = Folder.PATH_SEPARATOR;
                }
            }
            else
            {
                // no path attributes: logical path and physical path equivalent
                logicalPath = path;
            }
        }

        return logicalPath;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.impl.BaseElementImpl#getPhysicalPermissionPath()
     */
    public String getPhysicalPermissionPath()
    {
        // return path
        return path;
    }

    /**
     * getParentIdentity
     * 
     * Access the parent identity saved to facilitate cache management.
     * 
     * @return parent identity or null if not available
     */
    public Integer getParentIdentity()
    {
        return parentId;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#getParent()
     */
    public Node getParent()
    {
        return parent;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#setParent(org.apache.jetspeed.page.document.Node)
     */
    public void setParent(Node parent)
    {
        // set node parent
        this.parent = parent;

        // update path if required
        if (parent != null)
        {
            String parentPath = parent.getPath();
            if ((parentPath.equals(Folder.PATH_SEPARATOR) &&
                 (path.lastIndexOf(Folder.PATH_SEPARATOR) > 0)) ||
                (!parentPath.equals(Folder.PATH_SEPARATOR) &&
                 !parentPath.equals(path.substring(0, path.lastIndexOf(Folder.PATH_SEPARATOR)))))
            {
                // set path
                path = parentPath + Folder.PATH_SEPARATOR + getName();

                // reset logicalPath
                logicalPath = null;
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#getPath()
     */
    public String getPath()
    {
        // return path from attributes and base path
        return path;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#setPath(java.lang.String)
     */
    public void setPath(String path)
    {
        // set canonical node path
        this.path = getCanonicalNodePath(path);

        // reset logical path
        logicalPath = null;

        // parse and set informational attributes from path
        String attributeName = null;
        StringTokenizer pathElements = new StringTokenizer(this.path, Folder.PATH_SEPARATOR);
        while (pathElements.hasMoreTokens())
        {
            String pathElement = pathElements.nextToken();
            if (attributeName != null)
            {
                // set last attribute name with attribute value
                if (attributeName.startsWith(Folder.RESERVED_USER_FOLDER_NAME))
                {
                    user = pathElement.toLowerCase();
                }
                else if (attributeName.startsWith(Folder.RESERVED_ROLE_FOLDER_NAME))
                {
                    role = pathElement.toLowerCase();
                }
                else if (attributeName.startsWith(Folder.RESERVED_GROUP_FOLDER_NAME))
                {
                    group = pathElement.toLowerCase();
                }
                else if (attributeName.startsWith(Folder.RESERVED_MEDIATYPE_FOLDER_NAME))
                {
                    mediatype = pathElement.toLowerCase();
                }
                else if (attributeName.startsWith(Folder.RESERVED_LANGUAGE_FOLDER_NAME))
                {
                    if (locale != null)
                    {
                        // compose locale from language + country
                        locale = pathElement.toLowerCase() + "_" + locale;
                    }
                    else
                    {
                        locale = pathElement.toLowerCase();
                    }
                }
                else if (attributeName.startsWith(Folder.RESERVED_COUNTRY_FOLDER_NAME))
                {
                    if (locale != null)
                    {
                        // compose locale from language + country
                        locale = locale + "_" + pathElement.toLowerCase() ;
                    }
                    else
                    {
                        locale = pathElement.toLowerCase();
                    }
                }
                else if (attributeName.startsWith(Folder.RESERVED_FOLDER_PREFIX))
                {
                    extendedAttributeName = attributeName.substring(Folder.RESERVED_FOLDER_PREFIX.length());
                    extendedAttributeValue = pathElement.toLowerCase();
                }

                // reset attribute name
                attributeName = null;
            }
            else if (pathElement.startsWith(Folder.RESERVED_SUBSITE_FOLDER_PREFIX))
            {
                subsite = pathElement.substring(Folder.RESERVED_SUBSITE_FOLDER_PREFIX.length()).toLowerCase();
            }
            else if (pathElement.startsWith(Folder.RESERVED_FOLDER_PREFIX))
            {
                // save attribute name
                attributeName = pathElement.toLowerCase();
            }
        }

        // set name based on path
        if (!this.path.equals(Folder.PATH_SEPARATOR))
        {
            super.setName(this.path.substring(this.path.lastIndexOf(Folder.PATH_SEPARATOR) + 1));
        }
        else
        {
            super.setName(Folder.PATH_SEPARATOR);
        }

        // reset parent if required
        if (parent != null)
        {
            String parentPath = parent.getPath();
            if ((parentPath.equals(Folder.PATH_SEPARATOR) &&
                 (this.path.lastIndexOf(Folder.PATH_SEPARATOR) > 0)) ||
                (!parentPath.equals(Folder.PATH_SEPARATOR) &&
                 !parentPath.equals(this.path.substring(0, this.path.lastIndexOf(Folder.PATH_SEPARATOR)))))
            {
                parent = null;
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#getMetadata()
     */
    public GenericMetadata getMetadata()
    {
        return getPageMetadata();
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#getTitle(java.util.Locale)
     */
    public String getTitle(Locale locale)
    {
        // get title from metadata or use default title
        String title = getPageMetadata().getText("title", locale);
        if (title == null)
        {
            title = getTitle();
        }
        return title;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#getShortTitle(java.util.Locale)
     */
    public String getShortTitle(Locale locale)
    {
        // get short title from metadata or use title from metadata,
        // default short title, or default title
        String shortTitle = getPageMetadata().getText("short-title", locale);
        if (shortTitle == null)
        {
            shortTitle = getPageMetadata().getText("title", locale);
            if (shortTitle == null)
            {
                shortTitle = getShortTitle();
                if (shortTitle == null)
                {
                    shortTitle = getTitle();
                }
            }
        }
        return shortTitle;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#getType()
     */
    public abstract String getType();
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#getUrl()
     */
    public String getUrl()
    {
        return path;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#isHidden()
     */
    public boolean isHidden()
    {
        return hidden;
    }    

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#setHidden(boolean)
     */
    public void setHidden(boolean hidden)
    {
        this.hidden = hidden;
    }    
}
