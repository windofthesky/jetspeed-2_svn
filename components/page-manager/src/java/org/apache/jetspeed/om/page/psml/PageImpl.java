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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.jetspeed.om.folder.psml.MenuDefinitionImpl;
import org.apache.jetspeed.om.folder.MenuDefinition;
import org.apache.jetspeed.om.folder.MenuExcludeDefinition;
import org.apache.jetspeed.om.folder.MenuIncludeDefinition;
import org.apache.jetspeed.om.folder.MenuOptionsDefinition;
import org.apache.jetspeed.om.folder.MenuSeparatorDefinition;
import org.apache.jetspeed.om.folder.psml.MenuDefinitionImpl;
import org.apache.jetspeed.om.folder.psml.MenuExcludeDefinitionImpl;
import org.apache.jetspeed.om.folder.psml.MenuIncludeDefinitionImpl;
import org.apache.jetspeed.om.folder.psml.MenuOptionsDefinitionImpl;
import org.apache.jetspeed.om.folder.psml.MenuSeparatorDefinitionImpl;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;

/**
 * @version $Id$
 */
public class PageImpl extends DocumentImpl implements Page
{
    private DefaultsImpl defaults = new DefaultsImpl();

    private Fragment root = null;

    private int hashCode;

    /**
     * menuDefinitions - menu definitions for page
     */
    private List menuDefinitions;
    
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
        this.defaults.setDecorator(fragmentType, decoratorName);
    }

    public Fragment getRootFragment()
    {
        return this.root;
    }

    public void setRootFragment( Fragment root )
    {
        this.root = root;
        ((FragmentImpl)root).setPage(this);
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

    public Fragment removeFragmentById( String id )
    {
        // find fragment by id, tracking fragment parent
        Map parents = new HashMap();
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
                Fragment child = (Fragment)i.next();
                stack.push(child);
                parents.put(child, f);
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

        // remove fragment from parent/page root
        if (f != null)
        {
            Fragment parent = (Fragment)parents.get(f);
            if (parent != null)
            {
                if (parent.getFragments().remove(f))
                {
                    return f;
                }
            }
            else
            {
                if (f == root)
                {
                    root = null;
                    return f;
                }
            }
        }

        // not found or removed
        return null;
    }

    public List getFragmentsByName( String name )
    {
        List fragments = null;

        Stack stack = new Stack();
        if (getRootFragment() != null)
        {
            stack.push(getRootFragment());
        }

        Fragment f = (Fragment) stack.pop();

        while (f != null)
        {
            if ((f.getName() != null) && f.getName().equals(name))
            {
                if (fragments == null)
                {
                    fragments = new ArrayList(1);
                }
                fragments.add(f);
            }

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

        return fragments;
    }

    public DefaultsImpl getDefaults()
    {
        return this.defaults;
    }

    public void setDefaults( DefaultsImpl defaults )
    {
        this.defaults = defaults;
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
     * getMenuDefinitions - get list of menu definitions
     *
     * @return definition list
     */
    public List getMenuDefinitions()
    {
        return menuDefinitions;
    }

    /**
     * newMenuDefinition - creates a new empty menu definition
     *
     * @return a newly created MenuDefinition object for use in Page
     */
    public MenuDefinition newMenuDefinition()
    {
        return new MenuDefinitionImpl();
    }

    /**
     * newMenuExcludeDefinition - creates a new empty menu exclude definition
     *
     * @return a newly created MenuExcludeDefinition object for use in Page
     */
    public MenuExcludeDefinition newMenuExcludeDefinition()
    {
        return new MenuExcludeDefinitionImpl();
    }

    /**
     * newMenuIncludeDefinition - creates a new empty menu include definition
     *
     * @return a newly created MenuIncludeDefinition object for use in Page
     */
    public MenuIncludeDefinition newMenuIncludeDefinition()
    {
        return new MenuIncludeDefinitionImpl();
    }

    /**
     * newMenuOptionsDefinition - creates a new empty menu options definition
     *
     * @return a newly created MenuOptionsDefinition object for use in Page
     */
    public MenuOptionsDefinition newMenuOptionsDefinition()
    {
        return new MenuOptionsDefinitionImpl();
    }

    /**
     * newMenuSeparatorDefinition - creates a new empty menu separator definition
     *
     * @return a newly created MenuSeparatorDefinition object for use in Page
     */
    public MenuSeparatorDefinition newMenuSeparatorDefinition()
    {
        return new MenuSeparatorDefinitionImpl();
    }

    /**
     * setMenuDefinitions - set list of menu definitions
     *
     * @param definitions definition list
     */
    public void setMenuDefinitions(List definitions)
    {
        menuDefinitions = definitions;
    }

    /**
     * unmarshalled - notification that this instance has been
     *                loaded from the persistent store
     */
    public void unmarshalled()
    {
        // notify super class implementation
        super.unmarshalled();

        // propagate unmarshalled notification
        // to all menu definitions
        if (menuDefinitions != null)
        {
            Iterator menuIter = menuDefinitions.iterator();
            while (menuIter.hasNext())
            {
                ((MenuDefinitionImpl)menuIter.next()).unmarshalled();
            }
        }

        // propagate unmarshalled notification
        // to root fragment
        if (root != null)
        {
            ((FragmentImpl)root).unmarshalled();
        }

        // default title of pages to name
        if (getTitle() == null)
        {
            setTitle(getTitleName());
        }
    }

    /**
     * marshalling - notification that this instance is to
     *               be saved to the persistent store
     */
    public void marshalling()
    {
        // propagate marshalling notification
        // to root fragment
        if (root != null)
        {
            ((FragmentImpl)root).marshalling();
        }

        // propagate marshalling notification
        // to all menu definitions
        if (menuDefinitions != null)
        {
            Iterator menuIter = menuDefinitions.iterator();
            while (menuIter.hasNext())
            {
                ((MenuDefinitionImpl)menuIter.next()).marshalling();
            }
        }

        // notify super class implementation
        super.marshalling();
    }
}

