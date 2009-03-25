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
import java.util.Map;

import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.decoration.Decoration;
import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.page.ContentFragment;

/**
 * @version $Id$
 *
 */
public class MockContentFragment implements ContentFragment
{
    private static final long serialVersionUID = 8967937534977844599L;
    private String id;
    
    public MockContentFragment(String id)
    {
        this.id = id;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getContentFragments()
     */
    public List getContentFragments()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getDecoration()
     */
    public Decoration getDecoration()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getFragments()
     */
    public List getFragments()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getOverriddenContent()
     */
    public String getOverriddenContent()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getPortletContent()
     */
    public PortletContent getPortletContent()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getRenderedContent()
     */
    public String getRenderedContent() throws IllegalStateException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#isInstantlyRendered()
     */
    public boolean isInstantlyRendered()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#overrideRenderedContent(java.lang.String)
     */
    public void overrideRenderedContent(String contnent)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#setDecoration(org.apache.jetspeed.decoration.Decoration)
     */
    public void setDecoration(Decoration decoration)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#setPortletContent(org.apache.jetspeed.aggregator.PortletContent)
     */
    public void setPortletContent(PortletContent portletContent)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getDecorator()
     */
    public String getDecorator()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getFloatProperty(java.lang.String)
     */
    public float getFloatProperty(String propName)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getIntProperty(java.lang.String)
     */
    public int getIntProperty(String propName)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getLayoutColumn()
     */
    public int getLayoutColumn()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getLayoutHeight()
     */
    public float getLayoutHeight()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getLayoutRow()
     */
    public int getLayoutRow()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getLayoutSizes()
     */
    public String getLayoutSizes()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getLayoutWidth()
     */
    public float getLayoutWidth()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getLayoutX()
     */
    public float getLayoutX()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getLayoutY()
     */
    public float getLayoutY()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getLayoutZ()
     */
    public float getLayoutZ()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getMode()
     */
    public String getMode()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getName()
     */
    public String getName()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getPreferences()
     */
    public List getPreferences()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getProperties()
     */
    public Map getProperties()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getProperty(java.lang.String)
     */
    public String getProperty(String propName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getSkin()
     */
    public String getSkin()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getState()
     */
    public String getState()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getType()
     */
    public String getType()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#isReference()
     */
    public boolean isReference()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#setDecorator(java.lang.String)
     */
    public void setDecorator(String decoratorName)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#setLayoutColumn(int)
     */
    public void setLayoutColumn(int column)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#setLayoutHeight(float)
     */
    public void setLayoutHeight(float height)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#setLayoutRow(int)
     */
    public void setLayoutRow(int row)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#setLayoutSizes(java.lang.String)
     */
    public void setLayoutSizes(String sizes)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#setLayoutWidth(float)
     */
    public void setLayoutWidth(float width)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#setLayoutX(float)
     */
    public void setLayoutX(float x)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#setLayoutY(float)
     */
    public void setLayoutY(float y)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#setLayoutZ(float)
     */
    public void setLayoutZ(float z)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#setMode(java.lang.String)
     */
    public void setMode(String mode)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#setName(java.lang.String)
     */
    public void setName(String name)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#setPreferences(java.util.List)
     */
    public void setPreferences(List preferences)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#setSkin(java.lang.String)
     */
    public void setSkin(String skinName)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#setState(java.lang.String)
     */
    public void setState(String state)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#setType(java.lang.String)
     */
    public void setType(String type)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseElement#getId()
     */
    public String getId()
    {
        return id;
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
