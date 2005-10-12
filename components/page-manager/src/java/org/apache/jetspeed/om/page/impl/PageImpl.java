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

import java.util.List;

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
    private String skin;
    private String decorator;
    private String defaultFragmentDecorator;

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
        return null; //NYI
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#setRootFragment(org.apache.jetspeed.om.page.Fragment)
     */
    public void setRootFragment(Fragment fragment)
    {
        // NYI
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#getFragmentById(java.lang.String)
     */
    public Fragment getFragmentById(String id)
    {
        return null; // NYI
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
