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

import java.util.Locale;

import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.impl.BaseElementImpl;
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
    private NodeAttributes attributes;

    public NodeImpl()
    {
        super();
        attributes = new NodeAttributes();
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
     * @see org.apache.jetspeed.page.document.Node#getmetadata()
     */
    public GenericMetadata getMetadata()
    {
        return null; // NYI
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#getTitle(java.util.Locale)
     */
    public String getTitle(Locale locale)
    {
        return null; // NYI
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#getShortTitle(java.util.Locale)
     */
    public String getShortTitle(Locale locale)
    {
        return null; // NYI
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#getType()
     */
    public String getType()
    {
        return null; // NYI
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#getUrl()
     */
    public String getUrl()
    {
        return null; // NYI
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#isHidden()
     */
    public boolean isHidden()
    {
        return false; // NYI
    }    
}
