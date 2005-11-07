/*
 * Copyright 2005 The Apache Software Foundation.
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
package org.apache.jetspeed.page.document.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.PageMetadataImpl;
import org.apache.jetspeed.om.page.impl.BaseElementImpl;
import org.apache.jetspeed.om.page.impl.SecurityConstraintsImpl;
import org.apache.jetspeed.page.document.Node;

/**
 * NodeImpl
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public abstract class NodeImpl extends BaseElementImpl implements Node
{
    private Node parent;
    private boolean hidden;
    private Collection metadataFields;
    private NodeAttributes attributes = new NodeAttributes();

    private PageMetadataImpl pageMetadata;

    public NodeImpl(SecurityConstraintsImpl constraints)
    {
        super(constraints);
    }

    /**
     * isRootNode
     *
     * Test whether node attributes base path is a root node
     * path, regardless of parent setting.
     *
     * @return root node flag
     */
    public boolean isRootNode()
    {
        return attributes.getPath().equals(Folder.PATH_SEPARATOR);
    }

    /**
     * newPageMetadata
     *
     * Construct page manager specific metadata implementation.
     *
     * @param fields mutable fields collection
     * @return page metadata
     */
    public PageMetadataImpl newPageMetadata(Collection fields)
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
                metadataFields = new ArrayList(4);
            }
            pageMetadata = newPageMetadata(metadataFields);
        }
        return pageMetadata;
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
            String path = attributes.getPath();
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
            String path = attributes.getPath();
            if (path != null)
            {
                if (!name.equals(Folder.PATH_SEPARATOR))
                {
                    attributes.setPath(path.substring(0, path.lastIndexOf(Folder.PATH_SEPARATOR) + 1) + name);
                }
                else
                {
                    attributes.setPath(Folder.PATH_SEPARATOR);
                }
            }

            super.setName(name);
        }
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
        // cast to check type
        this.parent = (NodeImpl)parent;

        // update path if required
        if (parent != null)
        {
            String parentPath = parent.getPath();
            String path = getPath();
            if ((parentPath.equals(Folder.PATH_SEPARATOR) &&
                 (path.lastIndexOf(Folder.PATH_SEPARATOR) > 0)) ||
                (!parentPath.equals(Folder.PATH_SEPARATOR) &&
                 !parentPath.equals(path.substring(0, path.lastIndexOf(Folder.PATH_SEPARATOR)))))
            {
                setPath(parentPath + Folder.PATH_SEPARATOR + getName());
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#getPath()
     */
    public String getPath()
    {
        // return path from attributes and base path
        return attributes.getCanonicalPath();
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#setPath(java.lang.String)
     */
    public void setPath(String path)
    {
        // set attributes and base path
        attributes.setCanonicalPath(path);

        // set name based on path
        path = attributes.getPath();
        if (!path.equals(Folder.PATH_SEPARATOR))
        {
            super.setName(path.substring(path.lastIndexOf(Folder.PATH_SEPARATOR) + 1));
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
                 (path.lastIndexOf(Folder.PATH_SEPARATOR) > 0)) ||
                (!parentPath.equals(Folder.PATH_SEPARATOR) &&
                 !parentPath.equals(path.substring(0, path.lastIndexOf(Folder.PATH_SEPARATOR)))))
            {
                setParent(null);
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
        return getPath();
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
