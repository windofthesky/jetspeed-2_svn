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

package org.apache.jetspeed.om.page.psml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Stack;

import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;

/**
 * @version $Id$
 */
public class PageImpl extends AbstractBaseElement implements Page
{
    private Defaults defaults = new Defaults();

    private Fragment root = null;

    private Collection metadataFields = null;

    private int hashCode;

    private Folder parent;

    public PageImpl()
    {
        // empty constructor
    }

    /**
     * <p>
     * setId
     * </p>
     *
     * @see org.apache.jetspeed.om.page.psml.AbstractBaseElement#setId(java.lang.String)
     * @param id
     */
    public void setId( String id )
    {
        // Cheaper to generate the hash code now then every call to hashCode()
        hashCode = (Page.class.getName()+":"+id).hashCode();
        super.setId(id);
    }
    /**
     * <p>
     * getParent
     * </p>
     * 
     * @see org.apache.jetspeed.om.folder.ChildNode#getParent()
     * @return
     */
    public Folder getParent()
    {
        return parent;
    }

    /**
     * <p>
     * setParent
     * </p>
     * 
     * @see org.apache.jetspeed.om.folder.ChildNode#setParent(org.apache.jetspeed.om.folder.Folder)
     * @param parent
     */
    public void setParent( Folder parent )
    {
        this.parent = parent;
    }

    /**
     * <p>
     * equals
     * </p>
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     * @param obj
     * @return
     */
    public boolean equals( Object obj )
    {
        if (obj instanceof Page)
        {
            Page page = (Page) obj;
            return page != null && page.getId() != null && 
                   this.getId() != null && this.getId().equals(page.getId());
        }
        else
        {
            return false;
        }

    }

    /**
     * <p>
     * hashCode
     * </p>
     * 
     * @see java.lang.Object#hashCode()
     * @return
     */
    public int hashCode()
    {       
        return hashCode;
    }

    public String getDefaultSkin()
    {
        return this.defaults.getSkin();
    }

    public void setDefaultSkin( String skinName )
    {
        this.defaults.setSkin(skinName);
    }

    public String getDefaultDecorator( String fragmentType )
    {
        return this.defaults.getDecorator(fragmentType);
    }

    public void setDefaultDecorator( String decoratorName, String fragmentType )
    {
        this.defaults.setDecorator(decoratorName, fragmentType);
    }

    public Fragment getRootFragment()
    {
        return this.root;
    }

    public void setRootFragment( Fragment root )
    {
        this.root = root;
    }

    public Fragment getFragmentById( String id )
    {
        Stack stack = new Stack();
        if (getRootFragment() != null)
        {
            stack.push(getRootFragment());
        }

        Fragment f = (Fragment) stack.pop();

        while ((f != null) && (!(f.getId().equals(id))))
        {
            Iterator i = f.getFragments().iterator();

            while (i.hasNext())
            {
                stack.push(i.next());
            }

            if (stack.size() > 0)
            {
                f = (Fragment) stack.pop();
            }
            else
            {
                f = null;
            }
        }

        return f;
    }

    public Defaults getDefaults()
    {
        return this.defaults;
    }

    public void setDefaults( Defaults defaults )
    {
        this.defaults = defaults;
    }

    public Object clone() throws java.lang.CloneNotSupportedException
    {
        Object cloned = super.clone();

        // TBD: clone the inner content

        return cloned;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.om.page.Page#getMetadata()
     */
    public GenericMetadata getMetadata()
    {
        if (metadataFields == null)
        {
            metadataFields = new ArrayList();
        }

        GenericMetadata metadata = new PageMetadataImpl();
        metadata.setFields(metadataFields);
        return metadata;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.om.page.Page#setMetadata(org.apache.jetspeed.om.common.GenericMetadata)
     */
    public void setMetadata( GenericMetadata metadata )
    {
        this.metadataFields = metadata.getFields();
    }

    /**
     * This should only be used during castor marshalling
     * 
     * @see org.apache.jetspeed.om.page.Page#getMetadataFields()
     */
    public Collection getMetadataFields()
    {
        return metadataFields;
    }

    /**
     * This should only be used during castor unmarshalling
     * 
     * @see org.apache.jetspeed.om.page.Page#setMetadataFields(java.util.Collection)
     */
    public void setMetadataFields( Collection metadataFields )
    {
        this.metadataFields = metadataFields;
    }
    /**
     * <p>
     * getName
     * </p>
     *
     * @see org.apache.jetspeed.om.page.psml.AbstractBaseElement#getName()
     * @return
     */
    public String getName()
    {
        return getId();
    }
}

