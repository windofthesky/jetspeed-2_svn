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
package org.apache.jetspeed.om.page.impl;

import java.security.AccessController;
import java.security.Permission;
import java.util.List;
import java.util.Map;

import org.apache.jetspeed.om.page.BaseFragmentElement;
import org.apache.jetspeed.om.page.BaseFragmentValidationListener;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.page.impl.DatabasePageManagerUtils;

/**
 * BaseFragmentElementImpl
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public abstract class BaseFragmentElementImpl extends BaseElementImpl implements BaseFragmentElement
{
    private String ojbConcreteClass = getClass().getName();
    private String fragmentId;
    private String skin;
    private String decorator;
    private String state;
    private String mode;
    private int layoutRowProperty = -1;
    private int layoutColumnProperty = -1;
    private String layoutSizesProperty;
    private float layoutXProperty = -1.0F;
    private float layoutYProperty = -1.0F;
    private float layoutZProperty = -1.0F;
    private float layoutWidthProperty = -1.0F;
    private float layoutHeightProperty = -1.0F;
    private String extendedPropertyName1;
    private String extendedPropertyValue1;
    private String extendedPropertyName2;
    private String extendedPropertyValue2;
    private List preferences;

    private FragmentPropertyMap propertiesMap;
    private FragmentPreferenceList fragmentPreferences;
    private BaseFragmentsElementImpl baseFragmentsElement;

    public BaseFragmentElementImpl()
    {
        super(new FragmentSecurityConstraintsImpl());
    }
    
    /**
     * accessPreferences
     *
     * Access mutable persistent collection member for List wrappers.
     *
     * @return persistent collection
     */
    List accessPreferences()
    {
        // create initial collection if necessary
        if (preferences == null)
        {
            preferences = DatabasePageManagerUtils.createList();
        }
        return preferences;
    }

    /**
     * getBaseFragmentsElement
     *
     * Get base fragments implementation that owns fragment.
     *
     * @return owning base fragments implementation
     */
    BaseFragmentsElementImpl getBaseFragmentsElement()
    {
        return baseFragmentsElement;
    }

    /**
     * setBaseFragmentsElement
     *
     * Set base fragments implementation that owns fragment.
     *
     * @param baseFragmentsElement owning base fragments implementation
     */
    void setBaseFragmentsElement(BaseFragmentsElementImpl baseFragmentsElement)
    {
        // set base fragments implementation
        this.baseFragmentsElement = baseFragmentsElement;
    }

    /**
     * getFragmentById
     *
     * Retrieve fragment with matching id from
     * this or child fragments.
     *
     * @param id fragment id to retrieve.
     * @return matched fragment
     */
    public BaseFragmentElement getFragmentById(String id)
    {
        // check for match
        if (getId().equals(id))
        {
            return this;
        }
        return null;
    }

    /**
     * getFragmentsByName
     *
     * Retrieve fragments with matching name including
     * this and child fragments.
     *
     * @param name fragment name to retrieve.
     * @return list of matched fragments
     */
    List getFragmentsByName(String name)
    {
        List matchedFragments = null;
        // check for match
        if ((getName() != null) && getName().equals(name))
        {
            if (matchedFragments == null)
            {
                matchedFragments = DatabasePageManagerUtils.createList();
            }
            matchedFragments.add(this);
        }
        return matchedFragments;
    }

    /**
     * getPropertyMemberKeys
     *
     * Get valid explicit property member keys.
     *
     * @return list of property member keys with values
     */
    List getPropertyMemberKeys()
    {
        List keys = DatabasePageManagerUtils.createList();
        if (layoutRowProperty >= 0)
        {
            keys.add(ROW_PROPERTY_NAME);
        }
        if (layoutColumnProperty >= 0)
        {
            keys.add(COLUMN_PROPERTY_NAME);
        }
        if (layoutSizesProperty != null)
        {
            keys.add(SIZES_PROPERTY_NAME);
        }
        if (layoutXProperty >= 0.0F)
        {
            keys.add(X_PROPERTY_NAME);
        }
        if (layoutYProperty >= 0.0F)
        {
            keys.add(Y_PROPERTY_NAME);
        }
        if (layoutZProperty >= 0.0F)
        {
            keys.add(Z_PROPERTY_NAME);
        }
        if (layoutWidthProperty >= 0.0F)
        {
            keys.add(WIDTH_PROPERTY_NAME);
        }
        if (layoutHeightProperty >= 0.0F)
        {
            keys.add(HEIGHT_PROPERTY_NAME);
        }
        if ((extendedPropertyName1 != null) && (extendedPropertyValue1 != null))
        {
            keys.add(extendedPropertyName1);
        }
        if ((extendedPropertyName2 != null) && (extendedPropertyValue2 != null))
        {
            keys.add(extendedPropertyName2);
        }
        return keys;
    }

    /**
     * getPropertyMember
     *
     * Get explicit property member.
     *
     * @param key property name
     * @return property setting
     */
    String getPropertyMember(String key)
    {
        // set fragment explicit property member
        if (key.equals(ROW_PROPERTY_NAME))
        {
            if (layoutRowProperty >= 0)
            {
                return String.valueOf(layoutRowProperty);
            }
        }
        else if (key.equals(COLUMN_PROPERTY_NAME))
        {
            if (layoutColumnProperty >= 0)
            {
                return String.valueOf(layoutColumnProperty);
            }
        }
        else if (key.equals(SIZES_PROPERTY_NAME))
        {
            return layoutSizesProperty;
        }
        else if (key.equals(X_PROPERTY_NAME))
        {
            if (layoutXProperty >= 0.0F)
            {
                return String.valueOf(layoutXProperty);
            }
        }
        else if (key.equals(Y_PROPERTY_NAME))
        {
            if (layoutYProperty >= 0.0F)
            {
                return String.valueOf(layoutYProperty);
            }
        }
        else if (key.equals(Z_PROPERTY_NAME))
        {
            if (layoutZProperty >= 0.0F)
            {
                return String.valueOf(layoutZProperty);
            }
        }
        else if (key.equals(WIDTH_PROPERTY_NAME))
        {
            if (layoutWidthProperty >= 0.0F)
            {
                return String.valueOf(layoutWidthProperty);
            }
        }
        else if (key.equals(HEIGHT_PROPERTY_NAME))
        {
            if (layoutHeightProperty >= 0.0F)
            {
                return String.valueOf(layoutHeightProperty);
            }
        }
        else if (key.equals(extendedPropertyName1))
        {
            return extendedPropertyValue1;
        }
        else if (key.equals(extendedPropertyName2))
        {
            return extendedPropertyValue2;
        }
        return null;
    }

    /**
     * setPropertyMember
     *
     * Set explicit property member.
     *
     * @param key property name
     * @param value property setting
     */
    void setPropertyMember(String key, String value)
    {
        // set fragment explicit property member
        if (key.equals(ROW_PROPERTY_NAME))
        {
            layoutRowProperty = Integer.parseInt(value);
        }
        else if (key.equals(COLUMN_PROPERTY_NAME))
        {
            layoutColumnProperty = Integer.parseInt(value);
        }
        else if (key.equals(SIZES_PROPERTY_NAME))
        {
            layoutSizesProperty = value;
        }
        else if (key.equals(X_PROPERTY_NAME))
        {
            layoutXProperty = Float.parseFloat(value);
        }
        else if (key.equals(Y_PROPERTY_NAME))
        {
            layoutYProperty = Float.parseFloat(value);
        }
        else if (key.equals(Z_PROPERTY_NAME))
        {
            layoutZProperty = Float.parseFloat(value);
        }
        else if (key.equals(WIDTH_PROPERTY_NAME))
        {
            layoutWidthProperty = Float.parseFloat(value);
        }
        else if (key.equals(HEIGHT_PROPERTY_NAME))
        {
            layoutHeightProperty = Float.parseFloat(value);
        }
        else if (key.equals(extendedPropertyName1))
        {
            extendedPropertyValue1 = value;
        }
        else if (key.equals(extendedPropertyName2))
        {
            extendedPropertyValue2 = value;
        }
        else if (extendedPropertyName1 == null)
        {
            extendedPropertyName1 = key;
            extendedPropertyValue1 = value;
        }
        else if (extendedPropertyName2 == null)
        {
            extendedPropertyName2 = key;
            extendedPropertyValue2 = value;
        }
        else
        {
            throw new RuntimeException("Unable to set fragment property " + key + ", extended properties already used.");
        }
    }

    /**
     * clearPropertyMember
     *
     * Clear explicit property member.
     *
     * @param key property name
     */
    void clearPropertyMember(String key)
    {
        if (key.equals(ROW_PROPERTY_NAME))
        {
            layoutRowProperty = -1;
        }
        else if (key.equals(COLUMN_PROPERTY_NAME))
        {
            layoutColumnProperty = -1;
        }
        else if (key.equals(SIZES_PROPERTY_NAME))
        {
            layoutSizesProperty = null;
        }
        else if (key.equals(X_PROPERTY_NAME))
        {
            layoutXProperty = -1.0F;
        }
        else if (key.equals(Y_PROPERTY_NAME))
        {
            layoutYProperty = -1.0F;
        }
        else if (key.equals(Z_PROPERTY_NAME))
        {
            layoutZProperty = -1.0F;
        }
        else if (key.equals(WIDTH_PROPERTY_NAME))
        {
            layoutWidthProperty = -1.0F;
        }
        else if (key.equals(HEIGHT_PROPERTY_NAME))
        {
            layoutHeightProperty = -1.0F;
        }
        else if (key.equals(extendedPropertyName1))
        {
            extendedPropertyName1 = null;
            extendedPropertyValue1 = null;
        }
        else if (key.equals(extendedPropertyName2))
        {
            extendedPropertyName2 = null;
            extendedPropertyValue2 = null;
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.impl.BaseElementImpl#getEffectivePageSecurity()
     */
    public PageSecurity getEffectivePageSecurity()
    {
        // delegate to base fragments implementation
        if (baseFragmentsElement != null)
        {
            return baseFragmentsElement.getEffectivePageSecurity();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.impl.BaseElementImpl#checkPermissions(java.lang.String, int, boolean, boolean)
     */
    public void checkPermissions(String path, int mask, boolean checkNodeOnly, boolean checkParentsOnly) throws SecurityException
    {
        // always check for granted fragment permissions
        AccessController.checkPermission((Permission)pf.newPermission(pf.FRAGMENT_PERMISSION, path, mask));
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#getConstraintsEnabled()
     */
    public boolean getConstraintsEnabled()
    {
        // delegate to base fragments implementation
        if (baseFragmentsElement != null)
        {
            return baseFragmentsElement.getConstraintsEnabled();
        }
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#getPermissionsEnabled()
     */
    public boolean getPermissionsEnabled()
    {
        // delegate to base fragments implementation
        if (baseFragmentsElement != null)
        {
            return baseFragmentsElement.getPermissionsEnabled();
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.impl.BaseElementImpl#getId()
     */
    public String getId()
    {
        if (fragmentId != null)
        {
            return fragmentId;
        }
        return super.getId();
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setId(java.lang.String)
     */
    public void setId(String fragmentId)
    {
        this.fragmentId = fragmentId;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getSkin()
     */
    public String getSkin()
    {
        return skin;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setSkin(java.lang.String)
     */
    public void setSkin(String skinName)
    {
        this.skin = skinName;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getDecorator()
     */
    public String getDecorator()
    {
        return decorator;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setDecorator(java.lang.String)
     */
    public void setDecorator(String decoratorName)
    {
        this.decorator = decoratorName;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getState()
     */
    public String getState()
    {
        return state;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setState(java.lang.String)
     */
    public void setState(String state)
    {
        this.state = state;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getMode()
     */
    public String getMode()
    {
        return mode;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setMode(java.lang.String)
     */
    public void setMode(String mode)
    {
        this.mode = mode;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getProperty(java.lang.String)
     */
    public String getProperty(String propName)
    {
        return (String)getProperties().get(propName);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getIntProperty(java.lang.String)
     */
    public int getIntProperty(String propName)
    {
        String propValue = (String)getProperties().get(propName);
        if (propValue != null)
        {
            return Integer.parseInt(propValue);
        }
        return -1;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getFloatProperty(java.lang.String)
     */
    public float getFloatProperty(String propName)
    {
        String propValue = (String)getProperties().get(propName);
        if (propValue != null)
        {
            return Float.parseFloat(propValue);
        }
        return -1.0F;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getProperties()
     */
    public Map getProperties()
    {
        // initialize and return writable properties map
        if (propertiesMap == null)
        {
            propertiesMap = new FragmentPropertyMap(this);
        }
        return propertiesMap;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getLayoutRow()
     */
    public int getLayoutRow()
    {
        // get standard int property
        return getIntProperty(ROW_PROPERTY_NAME);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setLayoutRow(int)
     */
    public void setLayoutRow(int row)
    {
        // set standard int property
        if (row >= 0)
        {
            getProperties().put(ROW_PROPERTY_NAME, String.valueOf(row));
        }
        else
        {
            getProperties().remove(ROW_PROPERTY_NAME);
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getLayoutColumn()
     */
    public int getLayoutColumn()
    {
        // get standard int property
        return getIntProperty(COLUMN_PROPERTY_NAME);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setLayoutColumn(int)
     */
    public void setLayoutColumn(int column)
    {
        // set standard int property
        if (column >= 0)
        {
            getProperties().put(COLUMN_PROPERTY_NAME, String.valueOf(column));
        }
        else
        {
            getProperties().remove(COLUMN_PROPERTY_NAME);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getLayoutSizes()
     */
    public String getLayoutSizes()
    {
        // get standard string property
        return getProperty(SIZES_PROPERTY_NAME);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setLayoutSizes(java.lang.String)
     */
    public void setLayoutSizes(String sizes)
    {
        // set standard string property
        if (sizes != null)
        {
            getProperties().put(SIZES_PROPERTY_NAME, sizes);
        }
        else
        {
            getProperties().remove(SIZES_PROPERTY_NAME);
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getLayoutX()
     */
    public float getLayoutX()
    {
        // get standard float property
        return getFloatProperty(X_PROPERTY_NAME);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setLayoutX(float)
     */
    public void setLayoutX(float x)
    {
        // set standard float property
        if (x >= 0.0F)
        {
            getProperties().put(X_PROPERTY_NAME, String.valueOf(x));
        }
        else
        {
            getProperties().remove(X_PROPERTY_NAME);
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getLayoutY()
     */
    public float getLayoutY()
    {
        // get standard float property
        return getFloatProperty(Y_PROPERTY_NAME);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setLayoutY(float)
     */
    public void setLayoutY(float y)
    {
        // set standard float property
        if (y >= 0.0F)
        {
            getProperties().put(Y_PROPERTY_NAME, String.valueOf(y));
        }
        else
        {
            getProperties().remove(Y_PROPERTY_NAME);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getLayoutZ()
     */
    public float getLayoutZ()
    {
        // get standard float property
        return getFloatProperty(Z_PROPERTY_NAME);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setLayoutZ(float)
     */
    public void setLayoutZ(float z)
    {
        // set standard float property
        if (z >= 0.0F)
        {
            getProperties().put(Z_PROPERTY_NAME, String.valueOf(z));
        }
        else
        {
            getProperties().remove(Z_PROPERTY_NAME);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getLayoutWidth()
     */
    public float getLayoutWidth()
    {
        // get standard float property
        return getFloatProperty(WIDTH_PROPERTY_NAME);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setLayoutWidth(float)
     */
    public void setLayoutWidth(float width)
    {
        // set standard float property
        if (width >= 0.0F)
        {
            getProperties().put(WIDTH_PROPERTY_NAME, String.valueOf(width));
        }
        else
        {
            getProperties().remove(WIDTH_PROPERTY_NAME);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getLayoutHeight()
     */
    public float getLayoutHeight()
    {
        // get standard float property
        return getFloatProperty(HEIGHT_PROPERTY_NAME);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setLayoutHeight(float)
     */
    public void setLayoutHeight(float height)
    {
        // set standard float property
        if (height >= 0.0F)
        {
            getProperties().put(HEIGHT_PROPERTY_NAME, String.valueOf(height));
        }
        else
        {
            getProperties().remove(HEIGHT_PROPERTY_NAME);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getPreferences()
     */
    public List getPreferences()
    {
        // return mutable preferences list
        // by using list wrapper to manage
        // element uniqueness
        if (fragmentPreferences == null)
        {
            fragmentPreferences = new FragmentPreferenceList(this);
        }
        return fragmentPreferences;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setPreferences(java.util.List)
     */
    public void setPreferences(List preferences)
    {
        // set preferences by replacing existing
        // entries with new elements if new collection
        // is specified
        List fragmentPreferences = getPreferences();
        if (preferences != fragmentPreferences)
        {
            // replace all preferences
            fragmentPreferences.clear();
            if (preferences != null)
            {
                fragmentPreferences.addAll(preferences);
            }
        }
    }
    
    /**
     * Validate fragment using specified validation listener.
     * 
     * @param validationListener validation listener
     * @return validated flag
     */
    protected boolean validateFragments(BaseFragmentValidationListener validationListener)
    {
        // validate fragment using validation listener
        return validationListener.validate(this);
    }
}
