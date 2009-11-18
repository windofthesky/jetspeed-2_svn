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

import java.security.AccessController;
import java.security.Permission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.jetspeed.idgenerator.IdGenerator;
import org.apache.jetspeed.om.page.BaseFragmentElement;
import org.apache.jetspeed.om.page.BaseFragmentValidationListener;
import org.apache.jetspeed.om.page.PageSecurity;

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

    private List propertiesList = new ArrayList();
    
    private List preferences = new ArrayList();
    
    private Map propertiesMap = new HashMap();

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

    public String getState()
    {
        return this.state;
    }

    public void setState( String state )
    {
        this.state = state;
    }

    public String getMode()
    {
        return this.mode;
    }

    public void setMode( String mode )
    {
        this.mode = mode;
    }

    public String getDecorator()
    {
        return this.decorator;
    }

    public void setDecorator( String decoratorName )
    {
        this.decorator = decoratorName;
    }

    public String getSkin()
    {
        return this.skin;
    }

    public void setSkin( String skin )
    {
        this.skin = skin;
    }

    public List getPropertiesList()
    {
        return (List) this.propertiesList;
    }
    
    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getProperty(java.lang.String)
     */
    public String getProperty(String propName)
    {
        return (String)propertiesMap.get(propName);
    }
    
    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getIntProperty(java.lang.String)
     */
    public int getIntProperty(String propName)
    {
        String prop = (String)propertiesMap.get(propName);
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
        String prop = (String)propertiesMap.get(propName);
        if (prop != null)
        {
            return Float.parseFloat(prop);
        }
        return -1.0F;
    }
    
    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getProperties()
     */
    public Map getProperties()
    {
        return propertiesMap;
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
        if (row >= 0)
        {
            propertiesMap.put(ROW_PROPERTY_NAME, String.valueOf(row));
        }
        else
        {
            propertiesMap.remove(ROW_PROPERTY_NAME);
        }
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
        if (column >= 0)
        {
            propertiesMap.put(COLUMN_PROPERTY_NAME, String.valueOf(column));
        }
        else
        {
            propertiesMap.remove(COLUMN_PROPERTY_NAME);
        }
    }
    
    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#getLayoutSizes()
     */
    public String getLayoutSizes()
    {
        return (String)propertiesMap.get(SIZES_PROPERTY_NAME);
    }
    
    /**
     * @see org.apache.jetspeed.om.page.BaseFragmentElement#setLayoutSizes(java.lang.String)
     */
    public void setLayoutSizes(String sizes)
    {
        if (sizes != null)
        {
            propertiesMap.put(SIZES_PROPERTY_NAME, sizes);
        }
        else
        {
            propertiesMap.remove(SIZES_PROPERTY_NAME);
        }
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
        if (x >= 0.0F)
        {
            propertiesMap.put(X_PROPERTY_NAME, String.valueOf(x));
        }
        else
        {
            propertiesMap.remove(X_PROPERTY_NAME);
        }
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
        if (y >= 0.0F)
        {
            propertiesMap.put(Y_PROPERTY_NAME, String.valueOf(y));
        }
        else
        {
            propertiesMap.remove(Y_PROPERTY_NAME);
        }
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
        if (z >= 0.0F)
        {
            propertiesMap.put(Z_PROPERTY_NAME, String.valueOf(z));
        }
        else
        {
            propertiesMap.remove(Z_PROPERTY_NAME);
        }
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
        if (width >= 0.0F)
        {
            propertiesMap.put(WIDTH_PROPERTY_NAME, String.valueOf(width));
        }
        else
        {
            propertiesMap.remove(WIDTH_PROPERTY_NAME);
        }
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
        if (height >= 0.0F)
        {
            propertiesMap.put(HEIGHT_PROPERTY_NAME, String.valueOf(height));
        }
        else
        {
            propertiesMap.remove(HEIGHT_PROPERTY_NAME);
        }
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
     * @param name
     */
    public List getPreferences()
    {
        return preferences;
    }

    public void setPreferences(List preferences)
    {
        this.preferences = preferences;  
    } 
    
    AbstractBaseFragmentsElement getBaseFragmentsElement()
    {
        return baseFragmentsElement;
    }

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

        // load the properties map from list
        propertiesMap.clear();
        Iterator propsIter = propertiesList.iterator();
        while (propsIter.hasNext())
        {
            PropertyImpl prop = (PropertyImpl) propsIter.next();
            propertiesMap.put(prop.getName(), prop.getValue());
        }
        
        return dirty;
    }

    /**
     * marshalling - notification that this instance is to
     *               be saved to the persistent store
     */
    public void marshalling()
    {
        // update the properties list from the map
        // if change/edit detected
        boolean changed = (propertiesMap.size() != propertiesList.size());
        if (!changed)
        {
            Iterator propsIter = propertiesList.iterator();
            while (!changed && propsIter.hasNext())
            {
                PropertyImpl prop = (PropertyImpl) propsIter.next();
                changed = (prop.getValue() != propertiesMap.get(prop.getName()));
            }
        }
        if (changed)
        {
            propertiesList.clear();
            Iterator propsIter = propertiesMap.entrySet().iterator();
            while (propsIter.hasNext())
            {
                Map.Entry prop = (Map.Entry) propsIter.next();
                PropertyImpl listProp = new PropertyImpl();
                listProp.setName((String)prop.getKey());
                listProp.setValue((String)prop.getValue());
                propertiesList.add(listProp);
            }
        }

        // notify super class implementation
        super.marshalling();
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
