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

import java.util.Iterator;
import java.util.Stack;

import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.document.AbstractNode;

/**
 * @version $Id$
 */
public class PageImpl extends AbstractNode implements Page
{
    private Defaults defaults = new Defaults();

    private Fragment root = null;

    private int hashCode;

    public PageImpl()
    {
        // empty constructor
        super();
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

    /**
     * <p>
     * getType
     * </p>
     *
     * @see org.apache.jetspeed.om.page.Document#getType()
     * @return
     */
    public String getType()
    {       
        return DOCUMENT_TYPE;
    }
    /**
     * <p>
     * getUrl
     * </p>
     * Same as invoking <code>AbstractBaseElement.getId()</code> unless url explicitly set.
     *
     * @see org.apache.jetspeed.om.page.Document#getUrl()
     * @return
     */
    public String getUrl()
    {
        if (isUrlSet())
            return super.getUrl();
        return getId();
    }
}

