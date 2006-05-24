/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.components.portletentity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.decoration.Decoration;
import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.Fragment;

/**
 * <p>
 * ContentFramgentTestImpl
 * </p>
 * 
 * Dummy ContentFragment wrapper around Fragment as using the real ContentFragmentImpl would introduce a circular
 * dependency between the registry and page-manager components. Probably should be replaced by a Mock but I don't
 * know how to setup that quickly and the whole ContentFragment construction is bound to be replaced soon anyway...
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
class ContentFragmentTestImpl implements Fragment, ContentFragment
{
    private Fragment f;

    /**
     * @param f
     * @param list
     */
    public ContentFragmentTestImpl(Fragment f, HashMap list)
    {
        super();
        this.f = f;
    }

    /**
     * @param actions
     * @throws SecurityException
     */
    public void checkAccess(String actions) throws SecurityException
    {
        f.checkAccess(actions);
    }

    /**
     * @param actions
     * @throws SecurityException
     */
    public void checkConstraints(String actions) throws SecurityException
    {
        f.checkConstraints(actions);
    }

    /**
     * @param mask
     * @throws SecurityException
     */
    public void checkPermissions(int mask) throws SecurityException
    {
        f.checkPermissions(mask);
    }
        
    public SecurityConstraint newSecurityConstraint()
    {
        return f.newSecurityConstraint();
    }

    public SecurityConstraints newSecurityConstraints()
    {
        return f.newSecurityConstraints();
    }

    /** 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        return f.equals(obj);
    }

    /**
     * @return contraints enabled
     */
    public boolean getConstraintsEnabled()
    {
        return f.getConstraintsEnabled();
    }

    /**
     * @return decorator name
     */
    public String getDecorator()
    {
        return f.getDecorator();
    }

    /**
     * @return list of fragments
     */
    public List getFragments()
    {
        return f.getFragments();
    }

    /**
     * @return id
     */
    public String getId()
    {
        return f.getId();
    }

    /**
     * @return name
     */
    public String getName()
    {
        return f.getName();
    }

    /**
     * @return permissions enabled
     */
    public boolean getPermissionsEnabled()
    {
        return f.getPermissionsEnabled();
    }


    /**
     * @return security constraints
     */
    public SecurityConstraints getSecurityConstraints()
    {
        return f.getSecurityConstraints();
    }

    /**
     * @return  short title
     */
    public String getShortTitle()
    {
        return f.getShortTitle();
    }

    /**
     * @return skin name
     */
    public String getSkin()
    {
        return f.getSkin();
    }

    /**
     * @return state string
     */
    public String getState()
    {
        return f.getState();
    }

    /**
     * @return state string
     */
    public String getMode()
    {
        return f.getMode();
    }

    /**
     * @return title
     */
    public String getTitle()
    {
        return f.getTitle();
    }

    /**
     * @return type string
     */
    public String getType()
    {
        return f.getType();
    }

    /** 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return f.hashCode();
    }

    /**
     * @return if its a reference
     */
    public boolean isReference()
    {
        return f.isReference();
    }

    /**
     * @param decoratorName
     */
    public void setDecorator(String decoratorName)
    {
        f.setDecorator(decoratorName);
    }

    /**
     * @param name
     */
    public void setName(String name)
    {
        f.setName(name);
    }

    /**
     * @param constraints
     */
    public void setSecurityConstraints(SecurityConstraints constraints)
    {
        f.setSecurityConstraints(constraints);
    }

    /**
     * @param title
     */
    public void setShortTitle(String title)
    {
        f.setShortTitle(title);
    }

    /**
     * @param skinName
     */
    public void setSkin(String skinName)
    {
        f.setSkin(skinName);
    }

    /**
     * @param state
     */
    public void setState(String state)
    {
        f.setState(state);
    }

    /**
     * @param mode
     */
    public void setMode(String mode)
    {
        f.setMode(mode);
    }

    /**
     * @param title
     */
    public void setTitle(String title)
    {
        f.setTitle(title);
    }

    /**
     * @param type
     */
    public void setType(String type)
    {
        f.setType(type);
    }

    /** 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return f.toString();
    }

    /** 
     * @see org.apache.jetspeed.om.page.ContentFragment#getContentFragments()
     */
    public List getContentFragments()
    {
        return null;
    }

    /** 
     * @see org.apache.jetspeed.om.page.ContentFragment#getRenderedContent()
     */
    public String getRenderedContent() throws IllegalStateException
    {
        return null;
    }

    /** 
     * @see org.apache.jetspeed.om.page.ContentFragment#overrideRenderedContent(java.lang.String)
     */
    public void overrideRenderedContent(String contnent)
    {
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getOverriddenContent()
     */
    public String getOverriddenContent()
    {
        return null;
    }

    /** 
     * @see org.apache.jetspeed.om.page.ContentFragment#setPortletContent(org.apache.jetspeed.aggregator.PortletContent)
     */
    public void setPortletContent(PortletContent portletContent)
    {
    }

    /**
     * @see org.apache.jetspeed.om.page.Fragment#getLayoutRow()
     */
    public int getLayoutRow()
    {
        return -1;
    }

    /**
     * @see org.apache.jetspeed.om.page.Fragment#getLayoutColumn()
     */
    public int getLayoutColumn()
    {
        return -1;
    }

    /**
     * @see org.apache.jetspeed.om.page.Fragment#getLayoutSizes()
     */
    public String getLayoutSizes()
    {
        return null;
    }

    /**
     * @see org.apache.jetspeed.om.page.Fragment#getLayoutX()
     */
    public float getLayoutX()
    {
        return -1.0F;
    }

    /**
     * @see org.apache.jetspeed.om.page.Fragment#getLayoutY()
     */
    public float getLayoutY()
    {
        return -1.0F;
    }

    /**
     * @see org.apache.jetspeed.om.page.Fragment#getLayoutZ()
     */
    public float getLayoutZ()
    {
        return -1.0F;
    }

    /**
     * @see org.apache.jetspeed.om.page.Fragment#getLayoutWidth()
     */
    public float getLayoutWidth()
    {
        return -1.0F;
    }

    /**
     * @see org.apache.jetspeed.om.page.Fragment#getLayoutHeight()
     */
    public float getLayoutHeight()
    {
        return -1.0F;
    }

    /**
     * @see org.apache.jetspeed.om.page.Fragment#setLayoutRow(int)
     */
    public void setLayoutRow(int row)
    {            
    }

    /**
     * @see org.apache.jetspeed.om.page.Fragment#setLayoutColumn(int)
     */
    public void setLayoutColumn(int column)
    {            
    }

    /**
     * @see org.apache.jetspeed.om.page.Fragment#setLayoutSizes(java.lang.String)
     */
    public void setLayoutSizes(String sizes)
    {            
    }

    /**
     * @see org.apache.jetspeed.om.page.Fragment#setLayoutX(float)
     */
    public void setLayoutX(float x)
    {            
    }

    /**
     * @see org.apache.jetspeed.om.page.Fragment#setLayoutY(float)
     */
    public void setLayoutY(float y)
    {            
    }

    /**
     * @see org.apache.jetspeed.om.page.Fragment#setLayoutZ(float)
     */
    public void setLayoutZ(float z)
    {            
    }

    /**
     * @see org.apache.jetspeed.om.page.Fragment#setLayoutWidth(float)
     */
    public void setLayoutWidth(float width)
    {            
    }

    /**
     * @see org.apache.jetspeed.om.page.Fragment#setLayoutHeight(float)
     */
    public void setLayoutHeight(float height)
    {            
    }

    /**
     * @see org.apache.jetspeed.om.page.Fragment#getIntProperty(java.lang.String)
     */
    public int getIntProperty(String propName)
    {
        return -1;
    }

    /**
     * @see org.apache.jetspeed.om.page.Fragment#getFloatProperty(java.lang.String)
     */
    public float getFloatProperty(String propName)
    {
        return -1.0F;
    }

    /**
     * @see org.apache.jetspeed.om.page.Fragment#getPreferences()
     */
    public List getPreferences()
    {
        return null;
    }

    /**
     * @see org.apache.jetspeed.om.page.Fragment#setPreferences(java.util.List)
     */
    public void setPreferences(List preferences)
    {
    }

    /**
     * @see org.apache.jetspeed.om.page.Fragment#getProperties()
     */
    public Map getProperties()
    {
        return null;
    }

    /**
     * @see org.apache.jetspeed.om.page.Fragment#getProperty(java.lang.String)
     */
    public String getProperty(String propName)
    {
        return null;
    }

    public Decoration getDecoration()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void setDecoration(Decoration decoration)
    {
        // TODO Auto-generated method stub
            
    }        
}
