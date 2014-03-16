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

package org.apache.jetspeed.om.page.psml;

import org.apache.jetspeed.idgenerator.IdGenerator;
import org.apache.jetspeed.om.page.BaseFragmentElement;
import org.apache.jetspeed.om.page.BaseFragmentValidationListener;
import org.apache.jetspeed.om.page.FragmentProperty;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.preference.FragmentPreference;

import java.security.AccessController;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

/**
 * AbstractBaseFragmentElement
 *
 * @version $Id:$
 */
public abstract class AbstractBaseFragmentElement extends AbstractBaseElement implements BaseFragmentElement
{
    private static final long serialVersionUID = 1L;

    private String state = null;

    private String mode = null;

    private String decorator = null;

    private String skin = null;

    private List<FragmentProperty> propertyImpls = new ArrayList<FragmentProperty>();
    
    private List<FragmentPreference> preferences = new ArrayList<FragmentPreference>();
    
    private String name;

    private AbstractBaseFragmentsElement baseFragmentsElement;

    private boolean dirty = false;
    
    /**
     * <p>
     * Default Constructor.
     * </p>
     */
    public AbstractBaseFragmentElement()
    {
    }

    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getState()
     */
    public String getState()
    {
        return getProperty(STATE_PROPERTY_NAME);
    }

    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setState(java.lang.String)
     */
    public void setState( String state )
    {
        this.state = state;
    }

    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setState(java.lang.String, java.lang.String, java.lang.String)
     */
    public void setState(String scope, String scopeValue, String state)
    {
        setProperty(STATE_PROPERTY_NAME, scope, scopeValue, state);
    }

    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getMode()
     */
    public String getMode()
    {
        return getProperty(MODE_PROPERTY_NAME);
    }

    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setMode(java.lang.String)
     */
    public void setMode( String mode )
    {
        this.mode = mode;
    }

    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setMode(java.lang.String, java.lang.String, java.lang.String)
     */
    public void setMode(String scope, String scopeValue, String mode)
    {
        setProperty(MODE_PROPERTY_NAME, scope, scopeValue, mode);
    }

    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getDecorator()
     */
    public String getDecorator()
    {
        return getProperty(DECORATOR_PROPERTY_NAME);
    }

    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setDecorator(java.lang.String)
     */
    public void setDecorator( String decoratorName )
    {
        this.decorator = decoratorName;
    }

    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setDecorator(java.lang.String, java.lang.String, java.lang.String)
     */
    public void setDecorator(String scope, String scopeValue, String decorator)
    {
        setProperty(DECORATOR_PROPERTY_NAME, scope, scopeValue, decorator);
    }

    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getSkin()
     */
    public String getSkin()
    {
        return getProperty(SKIN_PROPERTY_NAME);
    }

    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setSkin(java.lang.String)
     */
    public void setSkin( String skin )
    {
        this.skin = skin;
    }

    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setSkin(java.lang.String, java.lang.String, java.lang.String)
     */
    public void setSkin(String scope, String scopeValue, String skin)
    {
        setProperty(SKIN_PROPERTY_NAME, scope, scopeValue, skin);
    }

    /**
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
        PropertyImpl.getFragmentProperty(propName, getProperties(), userValue, groupValue, roleValue, globalValue);

        // override global property value members if not found in scoped properties
        if ((userValue[0] == null) && (groupValue[0] == null) && (roleValue[0] == null))
        {
            if (propName.equals(STATE_PROPERTY_NAME))
            {
                globalValue[0] = state;
            }
            else if (propName.equals(MODE_PROPERTY_NAME))
            {
                globalValue[0] = mode;                    
            }
            else if (propName.equals(DECORATOR_PROPERTY_NAME))
            {
                globalValue[0] = decorator;                    
            }
            else if (propName.equals(SKIN_PROPERTY_NAME))
            {
                globalValue[0] = skin;                                        
            }
        }

        // return most specifically scoped property value
        return ((userValue[0] != null) ? userValue[0] : ((groupValue[0] != null) ? groupValue[0] : ((roleValue[0] != null) ? roleValue[0] : globalValue[0])));
    }
    
    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getProperty(java.lang.String, java.lang.String, java.lang.String)
     */
    public String getProperty(String propName, String propScope, String propScopeValue)
    {
        // lookup global property value members
        if (propScope == null)
        {
            if (propName.equals(STATE_PROPERTY_NAME))
            {
                return state;
            }
            else if (propName.equals(MODE_PROPERTY_NAME))
            {
                return mode;                    
            }
            else if (propName.equals(DECORATOR_PROPERTY_NAME))
            {
                return decorator;                    
            }
            else if (propName.equals(SKIN_PROPERTY_NAME))
            {
                return skin;                                        
            }
        }
        
        // default user scope value
        if ((propScope != null) && propScope.equals(USER_PROPERTY_SCOPE) && (propScopeValue == null))
        {
            propScopeValue = PropertyImpl.getCurrentUserScopeValue();
        }

        // find specified scoped property value
        FragmentProperty fragmentProperty = PropertyImpl.findFragmentProperty(propName, propScope, propScopeValue, propertyImpls);
        if (fragmentProperty != null)
        {
            return fragmentProperty.getValue();
        }
        return null;
    }
    
    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getIntProperty(java.lang.String)
     */
    public int getIntProperty(String propName)
    {
        String prop = getProperty(propName);
        if (prop != null)
        {
            return Integer.parseInt(prop);
        }
        return -1;
    }
    
    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getIntProperty(java.lang.String, java.lang.String, java.lang.String)
     */
    public int getIntProperty(String propName, String propScope, String propScopeValue)
    {
        String prop = getProperty(propName, propScope, propScopeValue);
        if (prop != null)
        {
            return Integer.parseInt(prop);
        }
        return -1;
    }
    
    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getFloatProperty(java.lang.String)
     */
    public float getFloatProperty(String propName)
    {
        String prop = getProperty(propName);
        if (prop != null)
        {
            return Float.parseFloat(prop);
        }
        return -1.0F;
    }
    
    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getFloatProperty(java.lang.String, java.lang.String, java.lang.String)
     */
    public float getFloatProperty(String propName, String propScope, String propScopeValue)
    {
        String prop = getProperty(propName, propScope, propScopeValue);
        if (prop != null)
        {
            return Float.parseFloat(prop);
        }
        return -1.0F;
    }

    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setProperty(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void setProperty(String propName, String propScope, String propScopeValue, String propValue)
    {
        // set global property value members
        if (propScope == null)
        {
            if (propName.equals(STATE_PROPERTY_NAME))
            {
                state = propValue;
                return;
            }
            else if (propName.equals(MODE_PROPERTY_NAME))
            {
                mode = propValue;
                return;                    
            }
            else if (propName.equals(DECORATOR_PROPERTY_NAME))
            {
                decorator = propValue;
                return;                    
            }
            else if (propName.equals(SKIN_PROPERTY_NAME))
            {
                skin = propValue;
                return;                                        
            }
        }
        
        // default user scope value
        if ((propScope != null) && propScope.equals(USER_PROPERTY_SCOPE) && (propScopeValue == null))
        {
            propScopeValue = PropertyImpl.getCurrentUserScopeValue();
        }

        // find specified scoped property value
        FragmentProperty fragmentProperty = PropertyImpl.findFragmentProperty(propName, propScope, propScopeValue, propertyImpls);

        // add, set, or remove property
        if (propValue != null)
        {
            if (fragmentProperty == null)
            {
                fragmentProperty = new PropertyImpl();
                fragmentProperty.setName(propName);
                fragmentProperty.setScope(propScope);
                fragmentProperty.setScopeValue(propScopeValue);
                fragmentProperty.setValue(propValue);
                propertyImpls.add(fragmentProperty);
            }
            else
            {
                fragmentProperty.setValue(propValue);
            }
        }
        else if (fragmentProperty != null)
        {
            propertyImpls.remove(fragmentProperty);
        }
    }
    
    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setProperty(java.lang.String, java.lang.String, java.lang.String, int)
     */
    public void setProperty(String propName, String propScope, String propScopeValue, int propValue)
    {
        setProperty(propName, propScope, propScopeValue, ((propValue >= 0) ? String.valueOf(propValue) : null));
    }
    
    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setProperty(java.lang.String, java.lang.String, java.lang.String, float)
     */
    public void setProperty(String propName, String propScope, String propScopeValue, float propValue)
    {
        setProperty(propName, propScope, propScopeValue, ((propValue >= 0.0F) ? String.valueOf(propValue) : null));
    }
    
    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getProperties()
     */
    public List<FragmentProperty> getProperties()
    {
        return new PropertiesList(PropertyImpl.filterFragmentProperties(propertyImpls), propertyImpls);
    }

    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setProperties(java.util.List)
     */
    public void setProperties(List<FragmentProperty> properties)
    {
        // get and remove all filtered properties and replace
        // with new specified properties
        List<FragmentProperty> propertiesList = getProperties();
        propertiesList.clear();
        propertiesList.addAll(properties);
    } 
    
    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getLayoutRow()
     */
    public int getLayoutRow()
    {
        return getIntProperty(ROW_PROPERTY_NAME);
    }

    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setLayoutRow(int)
     */
    public void setLayoutRow(int row)
    {
        setProperty(ROW_PROPERTY_NAME, null, null, row);
    }
    
    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setLayoutRow(java.lang.String, java.lang.String, int)
     */
    public void setLayoutRow(String scope, String scopeValue, int row)
    {
        setProperty(ROW_PROPERTY_NAME, scope, scopeValue, row);
    }
    
    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getLayoutColumn()
     */
    public int getLayoutColumn()
    {
        return getIntProperty(COLUMN_PROPERTY_NAME);
    }

    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setLayoutColumn(int)
     */
    public void setLayoutColumn(int column)
    {
        setProperty(COLUMN_PROPERTY_NAME, null, null, column);
    }
    
    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setLayoutColumn(java.lang.String, java.lang.String, int)
     */
    public void setLayoutColumn(String scope, String scopeValue, int column)
    {
        setProperty(COLUMN_PROPERTY_NAME, scope, scopeValue, column);
    }
    
    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getLayoutSizes()
     */
    public String getLayoutSizes()
    {
        return getProperty(SIZES_PROPERTY_NAME);
    }
    
    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setLayoutSizes(java.lang.String)
     */
    public void setLayoutSizes(String sizes)
    {
        setProperty(SIZES_PROPERTY_NAME, null, null, sizes);
    }

    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setLayoutSizes(java.lang.String, java.lang.String, java.lang.String)
     */
    public void setLayoutSizes(String scope, String scopeValue, String sizes)
    {
        setProperty(SIZES_PROPERTY_NAME, scope, scopeValue, sizes);
    }

    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getLayoutX()
     */
    public float getLayoutX()
    {
        return getFloatProperty(X_PROPERTY_NAME);
    }

    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setLayoutX(float)
     */
    public void setLayoutX(float x)
    {
        setProperty(X_PROPERTY_NAME, null, null, x);
    }
    
    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setLayoutX(java.lang.String, java.lang.String, float)
     */
    public void setLayoutX(String scope, String scopeValue, float x)
    {
        setProperty(X_PROPERTY_NAME, scope, scopeValue, x);
    }
    
    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getLayoutY()
     */
    public float getLayoutY()
    {
        return getFloatProperty(Y_PROPERTY_NAME);
    }

    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setLayoutY(float)
     */
    public void setLayoutY(float y)
    {
        setProperty(Y_PROPERTY_NAME, null, null, y);
    }

    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setLayoutY(java.lang.String, java.lang.String, float)
     */
    public void setLayoutY(String scope, String scopeValue, float y)
    {
        setProperty(Y_PROPERTY_NAME, scope, scopeValue, y);
    }

    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getLayoutZ()
     */
    public float getLayoutZ()
    {
        return getFloatProperty(Z_PROPERTY_NAME);
    }

    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setLayoutZ(float)
     */
    public void setLayoutZ(float z)
    {
        setProperty(Z_PROPERTY_NAME, null, null, z);
    }

    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setLayoutZ(java.lang.String, java.lang.String, float)
     */
    public void setLayoutZ(String scope, String scopeValue, float z)
    {
        setProperty(Z_PROPERTY_NAME, scope, scopeValue, z);
    }

    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getLayoutWidth()
     */
    public float getLayoutWidth()
    {
        return getFloatProperty(WIDTH_PROPERTY_NAME);
    }

    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setLayoutWidth(float)
     */
    public void setLayoutWidth(float width)
    {
        setProperty(WIDTH_PROPERTY_NAME, null, null, width);
    }

    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setLayoutWidth(java.lang.String, java.lang.String, float)
     */
    public void setLayoutWidth(String scope, String scopeValue, float width)
    {
        setProperty(WIDTH_PROPERTY_NAME, scope, scopeValue, width);
    }

    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getLayoutHeight()
     */
    public float getLayoutHeight()
    {
        return getFloatProperty(HEIGHT_PROPERTY_NAME);
    }

    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setLayoutHeight(float)
     */
    public void setLayoutHeight(float height)
    {
        setProperty(HEIGHT_PROPERTY_NAME, null, null, height);
    }

    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setLayoutHeight(java.lang.String, java.lang.String, float)
     */
    public void setLayoutHeight(String scope, String scopeValue, float height)
    {
        setProperty(HEIGHT_PROPERTY_NAME, scope, scopeValue, height);
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
        boolean isEqual = false;
        if (obj != null && obj instanceof BaseFragmentElement)
        {
            BaseFragmentElement aFragment = (BaseFragmentElement) obj;
            if ((null != aFragment.getId()) && (null != getId()) && (getId().equals(aFragment.getId())))
            {
                isEqual = true;
            }
        }
        return isEqual;
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
        if (getId() != null)
        {
            return (BaseFragmentElement.class.getName() + ":" + getId()).hashCode();
        }
        else
        {
            return super.hashCode();
        }
    }

    /**
     * <p>
     * getPreferences
     * </p>
     * 
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getPreferences()
     */
    public List<FragmentPreference> getPreferences()
    {
        return preferences;
    }

    /**
     * <p>
     * setPreferences
     * </p>
     * 
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setPreferences(java.util.List)
     * @param preferences
     */
    public void setPreferences(List<FragmentPreference> preferences)
    {
        if (preferences == null)
        {
            preferences = new ArrayList<FragmentPreference>();
        }
        this.preferences = preferences;  
    } 
    
    /**
     * Get owning base fragments element.
     * 
     * @return base fragments element
     */
    public AbstractBaseFragmentsElement getBaseFragmentsElement()
    {
        return baseFragmentsElement;
    }

    /**
     * Set owning base fragments element.
     * 
     * @param baseFragmentsElement fragments element
     */
    void setBaseFragmentsElement(AbstractBaseFragmentsElement baseFragmentsElement)
    {
        // set base fragments implementation
        this.baseFragmentsElement = baseFragmentsElement;
        if (dirty){
            baseFragmentsElement.setDirty(dirty);   
        }        
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.psml.AbstractElementImpl#getEffectivePageSecurity()
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
     * @see org.apache.jetspeed.om.page.psml.AbstractElementImpl#checkPermissions(java.lang.String, int, boolean, boolean)
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
    
    /**
     * Castor raw properties collection member access.
     * 
     * @return properties collection
     */
    public List<FragmentProperty> getPropertyImpls()
    {
        return propertyImpls;
    }

    /**
     * Castor raw properties collection member access.
     * 
     * @param propertiesImpls properties collection
     */
    public void setPropertyImpls(List<FragmentProperty> propertiesImpls)
    {
        this.propertyImpls = propertyImpls;  
    }
    
    /**
     * Castor raw property member access.
     * 
     * @return property value
     */
    public String getStatePropertyField()
    {
        return state;
    }

    /**
     * Castor raw property member access.
     * 
     * @param state property value
     */
    public void setStatePropertyField(String state)
    {
        this.state = state;
    }

    /**
     * Castor raw property member access.
     * 
     * @return property value
     */
    public String getModePropertyField()
    {
        return mode;
    }

    /**
     * Castor raw property member access.
     * 
     * @param mode property value
     */
    public void setModePropertyField(String mode)
    {
        this.mode = mode;
    }

    /**
     * Castor raw property member access.
     * 
     * @return property value
     */
    public String getDecoratorPropertyField()
    {
        return decorator;
    }

    /**
     * Castor raw property member access.
     * 
     * @param decorator property value
     */
    public void setDecoratorPropertyField(String decorator)
    {
        this.decorator = decorator;
    }

    /**
     * Castor raw property member access.
     * 
     * @return property value
     */
    public String getSkinPropertyField()
    {
        return skin;
    }

    /**
     * Castor raw property member access.
     * 
     * @param skin property value
     */
    public void setSkinPropertyField(String skin)
    {
        this.skin = skin;
    }
    
    /**
     * unmarshalled - notification that this instance has been
     *                loaded from the persistent store
     * @param generator id generator
     * @return dirty flag
     */
    public boolean unmarshalled(IdGenerator generator)
    {
        // notify super class implementation
        boolean dirty = super.unmarshalled(generator);
        
        // generate id if required
        if (getId() == null)
        {
            setId(generator.getNextPeid());
            dirty = true;
        }

        return dirty;
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
