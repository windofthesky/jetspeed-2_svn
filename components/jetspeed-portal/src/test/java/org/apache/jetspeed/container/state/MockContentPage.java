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

package org.apache.jetspeed.container.state;

import java.util.List;
import java.util.Locale;

import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.folder.MenuDefinition;
import org.apache.jetspeed.om.folder.MenuExcludeDefinition;
import org.apache.jetspeed.om.folder.MenuIncludeDefinition;
import org.apache.jetspeed.om.folder.MenuOptionsDefinition;
import org.apache.jetspeed.om.folder.MenuSeparatorDefinition;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.portlet.GenericMetadata;
import org.apache.jetspeed.page.document.Node;

/**
 * @version $Id$
 *
 */
public class MockContentPage implements ContentPage
{
    private static final long serialVersionUID = -7530808434879113408L;

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#getContentFragmentById(java.lang.String)
     */
    public ContentFragment getContentFragmentById(String id)
    {
        return new MockContentFragment(id);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#getContentFragmentsByName(java.lang.String)
     */
    public List getContentFragmentsByName(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#getFragmentById(java.lang.String)
     */
    public Fragment getFragmentById(String id)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#getFragmentsByName(java.lang.String)
     */
    public List getFragmentsByName(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#getRootContentFragment()
     */
    public ContentFragment getRootContentFragment()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#getRootFragment()
     */
    public Fragment getRootFragment()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#setRootContentFragment(org.apache.jetspeed.om.page.ContentFragment)
     */
    public void setRootContentFragment(ContentFragment frag)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#getDefaultDecorator(java.lang.String)
     */
    public String getDefaultDecorator(String fragmentType)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#getEffectiveDefaultDecorator(java.lang.String)
     */
    public String getEffectiveDefaultDecorator(String fragmentType)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#getMenuDefinitions()
     */
    public List getMenuDefinitions()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#getSkin()
     */
    public String getSkin()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#newMenuDefinition()
     */
    public MenuDefinition newMenuDefinition()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#newMenuExcludeDefinition()
     */
    public MenuExcludeDefinition newMenuExcludeDefinition()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#newMenuIncludeDefinition()
     */
    public MenuIncludeDefinition newMenuIncludeDefinition()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#newMenuOptionsDefinition()
     */
    public MenuOptionsDefinition newMenuOptionsDefinition()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#newMenuSeparatorDefinition()
     */
    public MenuSeparatorDefinition newMenuSeparatorDefinition()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#removeFragmentById(java.lang.String)
     */
    public Fragment removeFragmentById(String id)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#setDefaultDecorator(java.lang.String, java.lang.String)
     */
    public void setDefaultDecorator(String decoratorName, String fragmentType)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#setMenuDefinitions(java.util.List)
     */
    public void setMenuDefinitions(List definitions)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#setRootFragment(org.apache.jetspeed.om.page.Fragment)
     */
    public void setRootFragment(Fragment fragment)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#setSkin(java.lang.String)
     */
    public void setSkin(String skinName)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Document#getVersion()
     */
    public String getVersion()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Document#isDirty()
     */
    public boolean isDirty()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Document#setDirty(boolean)
     */
    public void setDirty(boolean dirty)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Document#setVersion(java.lang.String)
     */
    public void setVersion(String versionNumber)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#getMetadata()
     */
    public GenericMetadata getMetadata()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#getName()
     */
    public String getName()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#getParent()
     */
    public Node getParent()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#getPath()
     */
    public String getPath()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#getShortTitle(java.util.Locale)
     */
    public String getShortTitle(Locale locale)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#getTitle(java.util.Locale)
     */
    public String getTitle(Locale locale)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#getType()
     */
    public String getType()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#getUrl()
     */
    public String getUrl()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#isHidden()
     */
    public boolean isHidden()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#setHidden(boolean)
     */
    public void setHidden(boolean hidden)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#setParent(org.apache.jetspeed.page.document.Node)
     */
    public void setParent(Node parent)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#setPath(java.lang.String)
     */
    public void setPath(String path)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseElement#getId()
     */
    public String getId()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseElement#getShortTitle()
     */
    public String getShortTitle()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseElement#getTitle()
     */
    public String getTitle()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseElement#setShortTitle(java.lang.String)
     */
    public void setShortTitle(String title)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseElement#setTitle(java.lang.String)
     */
    public void setTitle(String title)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#checkAccess(java.lang.String)
     */
    public void checkAccess(String actions) throws SecurityException
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#checkConstraints(java.lang.String)
     */
    public void checkConstraints(String actions) throws SecurityException
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#checkPermissions(int)
     */
    public void checkPermissions(int mask) throws SecurityException
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#getConstraintsEnabled()
     */
    public boolean getConstraintsEnabled()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#getPermissionsEnabled()
     */
    public boolean getPermissionsEnabled()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#getSecurityConstraints()
     */
    public SecurityConstraints getSecurityConstraints()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#newSecurityConstraint()
     */
    public SecurityConstraint newSecurityConstraint()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#newSecurityConstraints()
     */
    public SecurityConstraints newSecurityConstraints()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#setSecurityConstraints(org.apache.jetspeed.om.common.SecurityConstraints)
     */
    public void setSecurityConstraints(SecurityConstraints constraints)
    {
        // TODO Auto-generated method stub
    }
}
