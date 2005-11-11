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
package org.apache.jetspeed.om.page.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageMetadataImpl;
import org.apache.jetspeed.page.document.impl.DocumentImpl;

/**
 * PageImpl
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class PageImpl extends DocumentImpl implements Page
{
    private Collection fragment;
    private String skin;
    private String defaultLayoutDecorator;
    private String defaultPortletDecorator;

    public PageImpl()
    {
        super(new PageSecurityConstraintsImpl());
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.impl.NodeImpl#newPageMetadata(java.util.Collection)
     */
    public PageMetadataImpl newPageMetadata(Collection fields)
    {
        PageMetadataImpl pageMetadata = new PageMetadataImpl(PageMetadataLocalizedFieldImpl.class);
        pageMetadata.setFields(fields);
        return pageMetadata;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#getDefaultSkin()
     */
    public String getDefaultSkin()
    {
        return skin;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#setDefaultSkin(java.lang.String)
     */
    public void setDefaultSkin(String skinName)
    {
        this.skin = skinName;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#getDefaultDecorator(java.lang.String)
     */
    public String getDefaultDecorator(String fragmentType)
    {
        // retrieve supported decorator types
        if (fragmentType != null)
        {
            if (fragmentType.equals(Fragment.LAYOUT))
            {
                return defaultLayoutDecorator; 
            }
            if (fragmentType.equals(Fragment.PORTLET))
            {
                return defaultPortletDecorator; 
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#getDefaultDecorator(java.lang.String,java.lang.String)
     */
    public void setDefaultDecorator(String decoratorName, String fragmentType)
    {
        // save supported decorator types
        if (fragmentType != null)
        {
            if (fragmentType.equals(Fragment.LAYOUT))
            {
                defaultLayoutDecorator = decoratorName; 
            }
            if (fragmentType.equals(Fragment.PORTLET))
            {
                defaultPortletDecorator = decoratorName; 
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#getRootFragment()
     */
    public Fragment getRootFragment()
    {
        // get singleton fragment; no access checks to
        // be made for root fragment
        if ((fragment != null) && !fragment.isEmpty())
        {
            return (Fragment)fragment.iterator().next();
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#setRootFragment(org.apache.jetspeed.om.page.Fragment)
     */
    public void setRootFragment(Fragment fragment)
    {
        // delete existing fragments if required
        if ((this.fragment != null) && !this.fragment.isEmpty())
        {
            this.fragment.clear();
        }

        // add new singleton fragment
        if (fragment != null)
        {
            if (this.fragment == null)
            {
                this.fragment = new ArrayList(1);
            }
            this.fragment.add(fragment);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#getFragmentById(java.lang.String)
     */
    public Fragment getFragmentById(String id)
    {
        // search for fragment recursively from
        // root singleton fragment using a local stack
        Stack stack = new Stack();
        Fragment fragment = getRootFragment();
        while ((fragment != null) && !fragment.getId().equals(id))
        {
            // push any fragment fragments onto the local stack;
            // note that this set is already filtered for access
            List fragments = fragment.getFragments();
            if (!fragments.isEmpty())
            {
                Iterator pushIter = fragments.iterator();
                while (pushIter.hasNext())
                {
                    stack.push(pushIter.next());
                }
            }

            // pop next fragment from local stack if available
            if (stack.size() > 0)
            {
                fragment = (Fragment) stack.pop();
            }
            else
            {
                fragment = null;
            }
        }
        return fragment;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#clone()
     */
    public Object clone() throws CloneNotSupportedException
    {
        return null; // NYI
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#getMenuDefinitions()
     */
    public List getMenuDefinitions()
    {
        return null; // NYI
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#setMenuDefinitions(java.util.List)
     */
    public void setMenuDefinitions(List definitions)
    {
        // NYI
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#getType()
     */
    public String getType()
    {
        return DOCUMENT_TYPE;
    }
}
