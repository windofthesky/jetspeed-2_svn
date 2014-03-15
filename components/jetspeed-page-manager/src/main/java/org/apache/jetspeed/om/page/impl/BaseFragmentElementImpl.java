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

import org.apache.jetspeed.om.page.BaseFragmentElement;
import org.apache.jetspeed.om.page.BaseFragmentValidationListener;
import org.apache.jetspeed.om.page.FragmentProperty;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.preference.FragmentPreference;
import org.apache.jetspeed.page.FragmentPropertyList;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.impl.DatabasePageManagerUtils;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerAware;

import java.security.AccessController;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

/**
 * BaseFragmentElementImpl
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public abstract class BaseFragmentElementImpl extends BaseElementImpl implements BaseFragmentElement, PersistenceBrokerAware
{
    private String ojbConcreteClass = getClass().getName();
    private String fragmentId;
    private String skinProperty;
    private String decoratorProperty;
    private String stateProperty;
    private String modeProperty;
    private int layoutRowProperty = -1;
    private int layoutColumnProperty = -1;
    private String layoutSizesProperty;
    private float layoutXProperty = -1.0F;
    private float layoutYProperty = -1.0F;
    private float layoutZProperty = -1.0F;
    private float layoutWidthProperty = -1.0F;
    private float layoutHeightProperty = -1.0F;
    private List<FragmentPreferenceImpl> preferences;

    private FragmentPropertyList fragmentProperties;
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
    List<FragmentPreferenceImpl> accessPreferences()
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
    public BaseFragmentsElementImpl getBaseFragmentsElement()
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
    List<BaseFragmentElement> getFragmentsByName(String name)
    {
        List<BaseFragmentElement> matchedFragments = null;
        // check for match
        if ((getName() != null) && getName().equals(name))
        {
            matchedFragments = new ArrayList<BaseFragmentElement>();
            matchedFragments.add(this);
        }
        return matchedFragments;
    }

    /**
     * getFragmentsByInterface
     *
     * Retrieve fragments with matching interface including
     * this and child fragments.
     *
     * @param interfaceFilter fragment interface to retrieve or null for all.
     * @return list of matched fragments
     */
    List<BaseFragmentElement> getFragmentsByInterface(Class interfaceFilter)
    {
        List<BaseFragmentElement> matchedFragments = null;
        // check for match
        if ((interfaceFilter == null) || interfaceFilter.isInstance(this))
        {
            matchedFragments = new ArrayList<BaseFragmentElement>();
            matchedFragments.add(this);
        }
        return matchedFragments;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.impl.BaseElementImpl#getPageManager()
     */
    public PageManager getPageManager()
    {
        return ((baseFragmentsElement != null) ? baseFragmentsElement.getPageManager() : null);
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
        // get standard property
        return getProperty(SKIN_PROPERTY_NAME);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setSkin(java.lang.String)
     */
    public void setSkin(String skinName)
    {
        // set standard global property
        this.skinProperty = skinName;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setSkin(java.lang.String, java.lang.String, java.lang.String)
     */
    public void setSkin(String propScope, String propScopeValue, String skinName)
    {
        // set standard property
        setProperty(SKIN_PROPERTY_NAME, propScope, propScopeValue, skinName);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getDecorator()
     */
    public String getDecorator()
    {
        // get standard property
        return getProperty(DECORATOR_PROPERTY_NAME);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setDecorator(java.lang.String)
     */
    public void setDecorator(String decoratorName)
    {
        // set standard global property
        this.decoratorProperty = decoratorName;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setDecorator(java.lang.String, java.lang.String, java.lang.String)
     */
    public void setDecorator(String propScope, String propScopeValue, String decoratorName)
    {
        // set standard property
        setProperty(DECORATOR_PROPERTY_NAME, propScope, propScopeValue, decoratorName);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getState()
     */
    public String getState()
    {
        // get standard property
        return getProperty(STATE_PROPERTY_NAME);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setState(java.lang.String)
     */
    public void setState(String state)
    {
        // set standard global property
        this.stateProperty = state;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setState(java.lang.String, java.lang.String, java.lang.String)
     */
    public void setState(String propScope, String propScopeValue, String state)
    {
        // set standard property
        setProperty(STATE_PROPERTY_NAME, propScope, propScopeValue, state);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getMode()
     */
    public String getMode()
    {
        // get standard property
        return getProperty(MODE_PROPERTY_NAME);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setMode(java.lang.String)
     */
    public void setMode(String mode)
    {
        // set standard global property
        this.modeProperty = mode;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setMode(java.lang.String, java.lang.String, java.lang.String)
     */
    public void setMode(String propScope, String propScopeValue, String mode)
    {
        // set standard property
        setProperty(MODE_PROPERTY_NAME, propScope, propScopeValue, mode);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getProperty(java.lang.String)
     */
    public String getProperty(String propName)
    {
        // scoped property values
        String [] userValue = new String[1];
        String [] groupValue = new String[1];
        String [] roleValue = new String[1];
        String [] globalValue = new String[1];

        // get property values from properties list
        FragmentPropertyImpl.getFragmentProperty(propName, getProperties(), userValue, groupValue, roleValue, globalValue);

        // override global property value members if not found in scoped properties
        if ((userValue[0] == null) && (groupValue[0] == null) && (roleValue[0] == null))
        {
            if (SKIN_PROPERTY_NAME.equals(propName))
            {
                globalValue[0] = skinProperty;
            }
            else if (DECORATOR_PROPERTY_NAME.equals(propName))
            {
                globalValue[0] = decoratorProperty;
            }
            else if (STATE_PROPERTY_NAME.equals(propName))
            {
                globalValue[0] = stateProperty;
            }
            else if (MODE_PROPERTY_NAME.equals(propName))
            {
                globalValue[0] = modeProperty;
            }
            else if (ROW_PROPERTY_NAME.equals(propName))
            {
                globalValue[0] = ((layoutRowProperty >= 0) ? Integer.toString(layoutRowProperty) : null);
            }
            else if (COLUMN_PROPERTY_NAME.equals(propName))
            {
                globalValue[0] = ((layoutColumnProperty >= 0) ? Integer.toString(layoutColumnProperty) : null);
            }
            else if (SIZES_PROPERTY_NAME.equals(propName))
            {
                globalValue[0] = layoutSizesProperty;
            }
            else if (X_PROPERTY_NAME.equals(propName))
            {
                globalValue[0] = ((layoutXProperty >= 0) ? Float.toString(layoutXProperty) : null);
            }
            else if (Y_PROPERTY_NAME.equals(propName))
            {
                globalValue[0] = ((layoutYProperty >= 0) ? Float.toString(layoutYProperty) : null);
            }
            else if (Z_PROPERTY_NAME.equals(propName))
            {
                globalValue[0] = ((layoutZProperty >= 0) ? Float.toString(layoutZProperty) : null);
            }
            else if (WIDTH_PROPERTY_NAME.equals(propName))
            {
                globalValue[0] = ((layoutWidthProperty >= 0) ? Float.toString(layoutWidthProperty) : null);
            }
            else if (HEIGHT_PROPERTY_NAME.equals(propName))
            {
                globalValue[0] = ((layoutHeightProperty >= 0) ? Float.toString(layoutHeightProperty) : null);
            }
        }

        // return most specifically scoped property value
        return ((userValue[0] != null) ? userValue[0] : ((groupValue[0] != null) ? groupValue[0] : ((roleValue[0] != null) ? roleValue[0] : globalValue[0])));
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getProperty(java.lang.String, java.lang.String, java.lang.String)
     */
    public String getProperty(String propName, String propScope, String propScopeValue)
    {
        // lookup global property value members
        if (propScope == null)
        {
            if (SKIN_PROPERTY_NAME.equals(propName))
            {
                return skinProperty;
            }
            else if (DECORATOR_PROPERTY_NAME.equals(propName))
            {
                return decoratorProperty;
            }
            else if (STATE_PROPERTY_NAME.equals(propName))
            {
                return stateProperty;
            }
            else if (MODE_PROPERTY_NAME.equals(propName))
            {
                return modeProperty;
            }
            else if (ROW_PROPERTY_NAME.equals(propName))
            {
                return ((layoutRowProperty >= 0) ? Integer.toString(layoutRowProperty) : null);
            }
            else if (COLUMN_PROPERTY_NAME.equals(propName))
            {
                return ((layoutColumnProperty >= 0) ? Integer.toString(layoutColumnProperty) : null);
            }
            else if (SIZES_PROPERTY_NAME.equals(propName))
            {
                return layoutSizesProperty;
            }
            else if (X_PROPERTY_NAME.equals(propName))
            {
                return ((layoutXProperty >= 0) ? Float.toString(layoutXProperty) : null);
            }
            else if (Y_PROPERTY_NAME.equals(propName))
            {
                return ((layoutYProperty >= 0) ? Float.toString(layoutYProperty) : null);
            }
            else if (Z_PROPERTY_NAME.equals(propName))
            {
                return ((layoutZProperty >= 0) ? Float.toString(layoutZProperty) : null);
            }
            else if (WIDTH_PROPERTY_NAME.equals(propName))
            {
                return ((layoutWidthProperty >= 0) ? Float.toString(layoutWidthProperty) : null);
            }
            else if (HEIGHT_PROPERTY_NAME.equals(propName))
            {
                return ((layoutHeightProperty >= 0) ? Float.toString(layoutHeightProperty) : null);
            }
        }

        // default user scope value
        if ((propScope != null) && propScope.equals(USER_PROPERTY_SCOPE) && (propScopeValue == null))
        {
            propScopeValue = FragmentPropertyImpl.getCurrentUserScopeValue();
        }

        // find specified scoped property value
        FragmentProperty fragmentProperty = FragmentPropertyImpl.findFragmentProperty(propName, propScope, propScopeValue, getProperties());
        if (fragmentProperty != null)
        {
            return fragmentProperty.getValue();
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getIntProperty(java.lang.String)
     */
    public int getIntProperty(String propName)
    {
        String propValue = getProperty(propName);
        if (propValue != null)
        {
            return Integer.parseInt(propValue);
        }
        return -1;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getIntProperty(java.lang.String, java.lang.String, java.lang.String)
     */
    public int getIntProperty(String propName, String propScope, String propScopeValue)
    {
        // lookup global property value members
        if (propScope == null)
        {
            if (ROW_PROPERTY_NAME.equals(propName))
            {
                return layoutRowProperty;
            }
            else if (COLUMN_PROPERTY_NAME.equals(propName))
            {
                return layoutColumnProperty;
            }
        }

        // default user scope value
        if ((propScope != null) && propScope.equals(USER_PROPERTY_SCOPE) && (propScopeValue == null))
        {
            propScopeValue = FragmentPropertyImpl.getCurrentUserScopeValue();
        }

        // find specified scoped property value
        FragmentProperty fragmentProperty = FragmentPropertyImpl.findFragmentProperty(propName, propScope, propScopeValue, getProperties());
        if (fragmentProperty != null)
        {
            return Integer.parseInt(fragmentProperty.getValue());
        }
        return -1;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getFloatProperty(java.lang.String)
     */
    public float getFloatProperty(String propName)
    {
        String propValue = getProperty(propName);
        if (propValue != null)
        {
            return Float.parseFloat(propValue);
        }
        return -1.0F;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getFloatProperty(java.lang.String, java.lang.String, java.lang.String)
     */
    public float getFloatProperty(String propName, String propScope, String propScopeValue)
    {
        // lookup global property value members
        if (propScope == null)
        {
            if (X_PROPERTY_NAME.equals(propName))
            {
                return layoutXProperty;
            }
            else if (Y_PROPERTY_NAME.equals(propName))
            {
                return layoutYProperty;
            }
            else if (Z_PROPERTY_NAME.equals(propName))
            {
                return layoutZProperty;
            }
            else if (WIDTH_PROPERTY_NAME.equals(propName))
            {
                return layoutWidthProperty;
            }
            else if (HEIGHT_PROPERTY_NAME.equals(propName))
            {
                return layoutHeightProperty;
            }
        }

        // default user scope value
        if ((propScope != null) && propScope.equals(USER_PROPERTY_SCOPE) && (propScopeValue == null))
        {
            propScopeValue = FragmentPropertyImpl.getCurrentUserScopeValue();
        }

        // find specified scoped property value
        FragmentProperty fragmentProperty = FragmentPropertyImpl.findFragmentProperty(propName, propScope, propScopeValue, getProperties());
        if (fragmentProperty != null)
        {
            return Float.parseFloat(fragmentProperty.getValue());
        }
        return -1.0F;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setProperty(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void setProperty(String propName, String propScope, String propScopeValue, String propValue)
    {
        // set global property value members
        if (propScope == null)
        {
            if (SKIN_PROPERTY_NAME.equals(propName))
            {
                skinProperty = propValue;
                return;
            }
            else if (DECORATOR_PROPERTY_NAME.equals(propName))
            {
                decoratorProperty = propValue;
                return;
            }
            else if (STATE_PROPERTY_NAME.equals(propName))
            {
                stateProperty = propValue;
                return;
            }
            else if (MODE_PROPERTY_NAME.equals(propName))
            {
                modeProperty = propValue;
                return;
            }
            else if (ROW_PROPERTY_NAME.equals(propName))
            {
                layoutRowProperty = ((propValue != null) ? Integer.parseInt(propValue) : -1);
                return;
            }
            else if (COLUMN_PROPERTY_NAME.equals(propName))
            {
                layoutColumnProperty = ((propValue != null) ? Integer.parseInt(propValue) : -1);
                return;
            }
            else if (SIZES_PROPERTY_NAME.equals(propName))
            {
                layoutSizesProperty = propValue;
                return;
            }
            else if (X_PROPERTY_NAME.equals(propName))
            {
                layoutXProperty = ((propValue != null) ? Integer.parseInt(propValue) : -1.0F);
                return;
            }
            else if (Y_PROPERTY_NAME.equals(propName))
            {
                layoutYProperty = ((propValue != null) ? Integer.parseInt(propValue) : -1.0F);
                return;
            }
            else if (Z_PROPERTY_NAME.equals(propName))
            {
                layoutZProperty = ((propValue != null) ? Integer.parseInt(propValue) : -1.0F);
                return;
            }
            else if (WIDTH_PROPERTY_NAME.equals(propName))
            {
                layoutWidthProperty = ((propValue != null) ? Integer.parseInt(propValue) : -1.0F);
                return;
            }
            else if (HEIGHT_PROPERTY_NAME.equals(propName))
            {
                layoutHeightProperty = ((propValue != null) ? Integer.parseInt(propValue) : -1.0F);
                return;
            }
        }
        
        // default user scope value
        if ((propScope != null) && propScope.equals(USER_PROPERTY_SCOPE) && (propScopeValue == null))
        {
            propScopeValue = FragmentPropertyImpl.getCurrentUserScopeValue();
        }

        // find specified scoped property value
        FragmentProperty fragmentProperty = FragmentPropertyImpl.findFragmentProperty(propName, propScope, propScopeValue, getProperties());

        // add, set, or remove property
        if (propValue != null)
        {
            if (fragmentProperty == null)
            {
                fragmentProperty = new FragmentPropertyImpl();
                fragmentProperty.setName(propName);
                fragmentProperty.setScope(propScope);
                fragmentProperty.setScopeValue(propScopeValue);
                fragmentProperty.setValue(propValue);
                getProperties().add(fragmentProperty);
            }
            else
            {
                fragmentProperty.setValue(propValue);
            }
        }
        else if (fragmentProperty != null)
        {
            getProperties().remove(fragmentProperty);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setProperty(java.lang.String, java.lang.String, java.lang.String, int)
     */
    public void setProperty(String propName, String propScope, String propScopeValue, int propValue)
    {
        // set global property value members
        if (propScope == null)
        {
            if (ROW_PROPERTY_NAME.equals(propName))
            {
                layoutRowProperty = propValue;
                return;
            }
            else if (COLUMN_PROPERTY_NAME.equals(propName))
            {
                layoutColumnProperty = propValue;
                return;
            }
        }

        // update scoped property
        setProperty(propName, propScope, propScopeValue, Integer.toString(propValue));
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setProperty(java.lang.String, java.lang.String, java.lang.String, float)
     */
    public void setProperty(String propName, String propScope, String propScopeValue, float propValue)
    {
        // set global property value members
        if (propScope == null)
        {
            if (X_PROPERTY_NAME.equals(propName))
            {
                layoutXProperty = propValue;
                return;
            }
            else if (Y_PROPERTY_NAME.equals(propName))
            {
                layoutYProperty = propValue;
                return;
            }
            else if (Z_PROPERTY_NAME.equals(propName))
            {
                layoutZProperty = propValue;
                return;
            }
            else if (WIDTH_PROPERTY_NAME.equals(propName))
            {
                layoutWidthProperty = propValue;
                return;
            }
            else if (HEIGHT_PROPERTY_NAME.equals(propName))
            {
                layoutHeightProperty = propValue;
                return;
            }
        }
        
        // update scoped property
        setProperty(propName, propScope, propScopeValue, Float.toString(propValue));
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getProperties()
     */
    public List<FragmentProperty> getProperties()
    {
        // get properties for this fragment from page manager
        // if fragment is not newly constructed
        if (getIdentity() != 0)
        {
            PageManager pageManager = getPageManager();
            if (pageManager != null)
            {
                FragmentPropertyList properties = pageManager.getFragmentPropertyManager().getFragmentPropertyList(this, fragmentProperties);
                fragmentProperties = null;
                return properties;
            }
        }
        // create transient properties list place holder
        if (fragmentProperties == null)
        {
            fragmentProperties = new FragmentPropertyListImpl(this);
        }
        return fragmentProperties;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setProperties(java.util.List)
     */
    public void setProperties(List<FragmentProperty> properties)
    {
        // set properties by replacing existing
        // entries with new elements if new collection
        // is specified
        List<FragmentProperty> fragmentProperties = getProperties();
        if (properties != fragmentProperties)
        {
            // replace all properties
            fragmentProperties.clear();
            if (properties != null)
            {
                fragmentProperties.addAll(properties);
            }
        }
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
        // set standard global int property
        layoutRowProperty = row;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setLayoutRow(java.lang.String, java.lang.String, int)
     */
    public void setLayoutRow(String scope, String scopeValue, int row)
    {
        // set standard global int property
        setProperty(ROW_PROPERTY_NAME, scope, scopeValue, row);
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
        // set standard global int property
        layoutColumnProperty = column;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setLayoutColumn(java.lang.String, java.lang.String, int)
     */
    public void setLayoutColumn(String scope, String scopeValue, int column)
    {
        // set standard global int property
        setProperty(COLUMN_PROPERTY_NAME, scope, scopeValue, column);
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
        // set standard global string property
        layoutSizesProperty = sizes;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setLayoutSizes(java.lang.String, java.lang.String, java.lang.String)
     */
    public void setLayoutSizes(String scope, String scopeValue, String sizes)
    {
        // set standard global string property
        setProperty(SIZES_PROPERTY_NAME, scope, scopeValue, sizes);
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
        // set standard global float property
        layoutXProperty = x;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setLayoutX(java.lang.String, java.lang.String, float)
     */
    public void setLayoutX(String scope, String scopeValue, float x)
    {
        // set standard global float property
        setProperty(X_PROPERTY_NAME, scope, scopeValue, x);
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
        // set standard global float property
        layoutYProperty = y;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setLayoutY(java.lang.String, java.lang.String, float)
     */
    public void setLayoutY(String scope, String scopeValue, float y)
    {
        // set standard global float property
        setProperty(Y_PROPERTY_NAME, scope, scopeValue, y);
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
        // set standard global float property
        layoutZProperty = z;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setLayoutZ(java.lang.String, java.lang.String, float)
     */
    public void setLayoutZ(String scope, String scopeValue, float z)
    {
        // set standard global float property
        setProperty(Z_PROPERTY_NAME, scope, scopeValue, z);
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
        // set standard global float property
        layoutWidthProperty = width;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setLayoutWidth(java.lang.String, java.lang.String, float)
     */
    public void setLayoutWidth(String scope, String scopeValue, float width)
    {
        // set standard global float property
        setProperty(WIDTH_PROPERTY_NAME, scope, scopeValue, width);
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
        // set standard global float property
        layoutHeightProperty = height;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setLayoutHeight(java.lang.String, java.lang.String, float)
     */
    public void setLayoutHeight(String scope, String scopeValue, float height)
    {
        // set standard global float property
        setProperty(HEIGHT_PROPERTY_NAME, scope, scopeValue, height);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getPreferences()
     */
    public List<FragmentPreference> getPreferences()
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
    public void setPreferences(List<FragmentPreference> preferences)
    {
        // set preferences by replacing existing
        // entries with new elements if new collection
        // is specified
        List<FragmentPreference> fragmentPreferences = getPreferences();
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
    
    /* (non-Javadoc)
     * @see org.apache.ojb.broker.PersistenceBrokerAware#afterDelete(org.apache.ojb.broker.PersistenceBroker)
     */
    public void afterDelete(PersistenceBroker broker)
    {
    }

    /* (non-Javadoc)
     * @see org.apache.ojb.broker.PersistenceBrokerAware#afterInsert(org.apache.ojb.broker.PersistenceBroker)
     */
    public void afterInsert(PersistenceBroker broker)
    {
        // notify page manager of fragment insert so that fragment
        // properties can be inserted as part of the insert operation
        PageManager pageManager = getPageManager();
        if (pageManager != null)
        {
            pageManager.getFragmentPropertyManager().updateFragmentPropertyList(this, PageManager.ALL_PROPERTY_SCOPE, fragmentProperties);
            fragmentProperties = null;
        }
    }

    /* (non-Javadoc)
     * @see org.apache.ojb.broker.PersistenceBrokerAware#afterLookup(org.apache.ojb.broker.PersistenceBroker)
     */
    public void afterLookup(PersistenceBroker broker)
    {
    }

    /* (non-Javadoc)
     * @see org.apache.ojb.broker.PersistenceBrokerAware#afterUpdate(org.apache.ojb.broker.PersistenceBroker)
     */
    public void afterUpdate(PersistenceBroker broker)
    {
        // notify page manager of fragment update so that fragment
        // properties can be updated as part of the update operation
        PageManager pageManager = getPageManager();
        if (pageManager != null)
        {
            pageManager.getFragmentPropertyManager().updateFragmentPropertyList(this, PageManager.ALL_PROPERTY_SCOPE, fragmentProperties);
            fragmentProperties = null;
        }
    }

    /* (non-Javadoc)
     * @see org.apache.ojb.broker.PersistenceBrokerAware#beforeDelete(org.apache.ojb.broker.PersistenceBroker)
     */
    public void beforeDelete(PersistenceBroker broker)
    {
        // notify page manager of fragment delete so that fragment
        // properties can be removed as part of the remove operation
        PageManager pageManager = getPageManager();
        if (pageManager != null)
        {
            pageManager.getFragmentPropertyManager().removeFragmentPropertyList(this, fragmentProperties);
            fragmentProperties = null;
        }
    }

    /* (non-Javadoc)
     * @see org.apache.ojb.broker.PersistenceBrokerAware#beforeInsert(org.apache.ojb.broker.PersistenceBroker)
     */
    public void beforeInsert(PersistenceBroker broker)
    {
    }

    /* (non-Javadoc)
     * @see org.apache.ojb.broker.PersistenceBrokerAware#beforeUpdate(org.apache.ojb.broker.PersistenceBroker)
     */
    public void beforeUpdate(PersistenceBroker broker)
    {
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
