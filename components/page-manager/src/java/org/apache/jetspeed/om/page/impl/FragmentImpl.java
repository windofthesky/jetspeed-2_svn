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

import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Property;

/**
 * FragmentImpl
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class FragmentImpl extends BaseElementImpl implements Fragment
{
    private String type;
    private String skin;
    private String decorator;
    private String state;

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getType()
     */
    public String getType()
    {
        return type;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#setType(java.lang.String)
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getSkin()
     */
    public String getSkin()
    {
        return skin;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#setSkin(java.lang.String)
     */
    public void setSkin(String skinName)
    {
        this.skin = skin;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getDecorator()
     */
    public String getDecorator()
    {
        return decorator;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#setDecorator(java.lang.String)
     */
    public void setDecorator(String decoratorName)
    {
        this.decorator = decoratorName;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getState()
     */
    public String getState()
    {
        return state;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#setState(java.lang.String)
     */
    public void setState(String state)
    {
        this.state = state;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getFragments()
     */
    public List getFragments()
    {
        return null; // NYI
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getLayoutProperties()
     */
    public List getLayoutProperties()
    {
        return null; // NYI
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getProperties(java.lang.String)
     */
    public List getProperties(String layoutName)
    {
        return null; // NYI
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getPropertyValue(java.lang.String,java.lang.String)
     */
    public String getPropertyValue(String layout, String propName)
    {
        return null; // NYI
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getPropertyValue(java.lang.String,java.lang.String,java.lang.String)
     */
    public void setPropertyValue(String layout, String propName, String value)
    {
        // NYI
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#addProperty(org.apache.jetspeed.om.page.Property)
     */
    public void addProperty(Property p)
    {
        // NYI
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#removeProperty(org.apache.jetspeed.om.page.Property)
     */
    public void removeProperty(Property p)
    {
        // NYI
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#clearProperties(java.lang.String)
     */
    public void clearProperties(String layoutName)
    {
        // NYI
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#isReference()
     */
    public boolean isReference()
    {
        return false; // NYI
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#clone()
     */
    public Object clone() throws CloneNotSupportedException
    {
        return null; // NYI
    }    
}
