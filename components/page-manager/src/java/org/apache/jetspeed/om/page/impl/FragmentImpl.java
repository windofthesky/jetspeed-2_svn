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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.jetspeed.om.common.SecuredResource;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;

/**
 * FragmentImpl
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class FragmentImpl extends BaseElementImpl implements Fragment
{
    private List fragments;
    private String type;
    private String skin;
    private String decorator;
    private String state;
    private int layoutRowProperty = -1;
    private int layoutColumnProperty = -1;
    private String layoutSizesProperty;
    private String extendedPropertyName1;
    private String extendedPropertyValue1;
    private String extendedPropertyName2;
    private String extendedPropertyValue2;

    private Map properties;

    public FragmentImpl()
    {
        super(new FragmentSecurityConstraintsImpl());
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#getPermissionsEnabled()
     */
    public boolean getPermissionsEnabled()
    {
        // permission support disabled since path addressing
        // not supported yet at the fragment level within pages
        return false;
    }
    
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
        this.skin = skinName;
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
        // mutable fragments collection must be defined... note
        // that this collection is only mutable if user has full
        // access rights to all fragments; otherwise, a copy of
        // the list will be returned and any modifications to the
        // set of fragments in the collection will not be preserved
        if (fragments == null)
        {
            fragments = new ArrayList();
        }
        return filterFragmentsByAccess(fragments);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getProperty(java.lang.String)
     */
    public String getProperty(String propName)
    {
        if (properties != null)
        {
            return (String)properties.get(propName);
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getIntProperty(java.lang.String)
     */
    public int getIntProperty(String propName)
    {
        if (properties != null)
        {
            String propValue = (String)properties.get(propName);
            if (propValue != null)
            {
                return Integer.parseInt(propValue);
    }
        }
        return -1;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getProperties()
     */
    public Map getProperties()
    {
        // initialize and return writable properties map
        if (properties == null)
        {
            properties = new HashMap(4);
    }
        return properties;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getLayoutRow()
     */
    public int getLayoutRow()
    {
        // get standard int property
        return getIntProperty(ROW_PROPERTY_NAME);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#setLayoutRow(int)
     */
    public void setLayoutRow(int row)
    {
        // set standard int property
        if (row >= 0)
        {
            getProperties().put(ROW_PROPERTY_NAME, String.valueOf(row));
    }
        else if (properties != null)
        {
            properties.remove(ROW_PROPERTY_NAME);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getLayoutColumn()
     */
    public int getLayoutColumn()
    {
        // get standard int property
        return getIntProperty(COLUMN_PROPERTY_NAME);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#setLayoutColumn(int)
     */
    public void setLayoutColumn(int column)
    {
        // set standard int property
        if (column >= 0)
        {
            getProperties().put(COLUMN_PROPERTY_NAME, String.valueOf(column));
        }
        else if (properties != null)
        {
            properties.remove(COLUMN_PROPERTY_NAME);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getLayoutSizes()
     */
    public String getLayoutSizes()
    {
        // get standard string property
        return getProperty(SIZES_PROPERTY_NAME);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#setLayoutSizes(java.lang.String)
     */
    public void setLayoutSizes(String sizes)
    {
        // set standard string property
        if (sizes != null)
        {
            getProperties().put(SIZES_PROPERTY_NAME, sizes);
    }
        else if (properties != null)
        {
            properties.remove(SIZES_PROPERTY_NAME);
        }
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

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getPreferences()
     */
    public List getPreferences()
    {
        return null; // NYI
    }
    
    /* (non-Javadoc)
     * @see org.apache.ojb.broker.PersistenceBrokerAware#beforeUpdate(org.apache.ojb.broker.PersistenceBroker)
     */
    public void beforeUpdate(PersistenceBroker broker) throws PersistenceBrokerException
    {
        // propagate to super
        super.beforeUpdate(broker);

        // update concrete fields with properties
        layoutRowProperty = -1;
        layoutColumnProperty = -1;
        layoutSizesProperty = null;
        extendedPropertyName1 = null;
        extendedPropertyValue1 = null;
        extendedPropertyName2 = null;
        extendedPropertyValue2 = null;
        if ((properties != null) && !properties.isEmpty())
        {
            Iterator propsIter = properties.entrySet().iterator();
            while (propsIter.hasNext())
            {
                Map.Entry prop = (Map.Entry)propsIter.next();
                String propName = (String)prop.getKey();
                String propValue = (String)prop.getValue();
                if (propValue != null)
                {
                    if (propName.equals(ROW_PROPERTY_NAME))
                    {
                        layoutRowProperty = Integer.parseInt(propValue);
                    }
                    else if (propName.equals(COLUMN_PROPERTY_NAME))
                    {
                        layoutColumnProperty = Integer.parseInt(propValue);
                    }
                    else if (propName.equals(SIZES_PROPERTY_NAME))
                    {
                        layoutSizesProperty = propValue;
                    }
                    else if (extendedPropertyName1 == null)
                    {
                        extendedPropertyName1 = propName;
                        extendedPropertyValue1 = propValue;
                    }
                    else if (extendedPropertyName2 == null)
                    {
                        extendedPropertyName2 = propName;
                        extendedPropertyValue2 = propValue;
                    }
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.ojb.broker.PersistenceBrokerAware#afterLookup(org.apache.ojb.broker.PersistenceBroker)
     */
    public void afterLookup(PersistenceBroker broker) throws PersistenceBrokerException
    {
        // propagate to super
        super.afterLookup(broker);

        // load properties from concrete fields
        if (layoutRowProperty >= 0)
        {
            getProperties().put(ROW_PROPERTY_NAME, String.valueOf(layoutRowProperty));
        }
        if (layoutColumnProperty >= 0)
        {
            getProperties().put(COLUMN_PROPERTY_NAME, String.valueOf(layoutColumnProperty));
        }
        if (layoutSizesProperty != null)
        {
            getProperties().put(SIZES_PROPERTY_NAME, layoutSizesProperty);
        }
        if ((extendedPropertyName1 != null) && (extendedPropertyValue1 != null))
        {
            getProperties().put(extendedPropertyName1, extendedPropertyValue1);
        }
        if ((extendedPropertyName2 != null) && (extendedPropertyValue2 != null))
        {
            getProperties().put(extendedPropertyName2, extendedPropertyValue2);
        }
    }

    /**
     * filterFragmentsByAccess
     *
     * Filter fragments list for view access.
     *
     * @param nodes list containing fragments to check
     * @return checked subset of nodes
     */
    private static List filterFragmentsByAccess(List fragments)
    {
        if ((fragments != null) && !fragments.isEmpty())
        {
            // check permissions and constraints, filter fragments as required
            List filteredFragments = null;
            Iterator checkAccessIter = fragments.iterator();
            while (checkAccessIter.hasNext())
            {
                Fragment fragment = (Fragment)checkAccessIter.next();
                try
                {
                    // check access
                    fragment.checkAccess(SecuredResource.VIEW_ACTION);

                    // add to filteredFragments fragments if copying
                    if (filteredFragments != null)
                    {
                        // permitted, add to filteredFragments fragments
                        filteredFragments.add(fragment);
                    }
                }
                catch (SecurityException se)
                {
                    // create filteredFragments fragments if not already copying
                    if (filteredFragments == null)
                    {
                        // not permitted, copy previously permitted fragments
                        // to new filteredFragments node set with same comparator
                        filteredFragments = new ArrayList(fragments.size());
                        Iterator copyIter = fragments.iterator();
                        while (copyIter.hasNext())
                        {
                            Fragment copyFragment = (Fragment)copyIter.next();
                            if (copyFragment != fragment)
                            {
                                filteredFragments.add(copyFragment);
                            }
                            else
                            {
                                break;
                            }
                        }
                    }
                }
            }

            // return filteredFragments fragments if generated
            if (filteredFragments != null)
            {
                return filteredFragments;
            }
        }
        return fragments;
    }
}
