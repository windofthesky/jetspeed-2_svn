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
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.apache.jetspeed.om.page.Defaults;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.document.impl.NodeImpl;

/**
 * PageImpl
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class PageImpl extends NodeImpl implements Page
{
    private List fragments;
    private String skin;
    private String decorator;
    private String defaultFragmentDecorator;
    private String subsite;
    private String principal;
    private String principalType;
    private String mediatype;
    private String locale;
    private String extendedLocatorName;
    private String extendedLocatorValue;

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
        return null; // NYI
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#getDefaultDecorator(java.lang.String,java.lang.String)
     */
    public void setDefaultDecorator(String decoratorName, String fragmentType)
    {
        // NYI
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#getRootFragment()
     */
    public Fragment getRootFragment()
    {
        // get singleton fragment
        if ((fragments != null) && !fragments.isEmpty())
        {
            return (Fragment)fragments.get(0);
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#setRootFragment(org.apache.jetspeed.om.page.Fragment)
     */
    public void setRootFragment(Fragment fragment)
    {
        // delete existing fragments if required
        if ((fragments != null) && !fragments.isEmpty())
        {
            Iterator removeIter = fragments.iterator();
            while (removeIter.hasNext())
            {
                removeIter.next();
                removeIter.remove();
            }
        }

        // add new singleton fragment
        if (fragment != null)
        {
            if (fragments == null)
            {
                fragments = new ArrayList(1);
            }
            fragments.add(fragment);
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
            // push any fragment fragments onto the local stack
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
     * @see org.apache.jetspeed.om.page.Page#getDefaults()
     */
    public Defaults getDefaults()
    {
        return null; // NYI
    }    
}
