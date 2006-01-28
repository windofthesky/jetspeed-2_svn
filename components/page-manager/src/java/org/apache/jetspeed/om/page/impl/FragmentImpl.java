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

import java.security.AccessController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.om.common.SecuredResource;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.preference.FragmentPreference;
import org.apache.jetspeed.om.preference.impl.FragmentPreferenceImpl;
import org.apache.jetspeed.security.FragmentPermission;

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
    private List preferences;

    private FragmentList fragmentsList;
    private FragmentPropertyMap propertiesMap;
    private FragmentPreferenceList fragmentPreferences;
    private PageImpl page;

    public FragmentImpl()
    {
        super(new FragmentSecurityConstraintsImpl());
    }

    /**
     * accessFragments
     *
     * Access mutable persistent collection member for List wrappers.
     *
     * @return persistent collection
     */
    List accessFragments()
    {
        // create initial collection if necessary
        if (fragments == null)
        {
            fragments = new ArrayList(4);
        }
        return fragments;
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
            preferences = new ArrayList(4);
        }
        return preferences;
    }

    /**
     * getPage
     *
     * Get page implementation that owns fragment.
     *
     * @return owning page implementation
     */
    PageImpl getPage()
    {
        return page;
    }

    /**
     * setPage
     *
     * Set page implementation that owns fragment and
     * propagate to all child fragments.
     *
     * @param page owning page implementation
     */
    void setPage(PageImpl page)
    {
        // set page implementation
        this.page = page;
        // propagate to children
        if (fragments != null)
        {
            Iterator fragmentsIter = fragments.iterator();
            while (fragmentsIter.hasNext())
            {
                ((FragmentImpl)fragmentsIter.next()).setPage(page);
            }
        }
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
    Fragment getFragmentById(String id)
    {
        // check for match
        if (getId().equals(id))
        {
            return this;
        }
        // match children
        if (fragments != null)
        {
            Iterator fragmentsIter = fragments.iterator();
            while (fragmentsIter.hasNext())
            {
                Fragment matchedFragment = ((FragmentImpl)fragmentsIter.next()).getFragmentById(id);
                if (matchedFragment != null)
                {
                    return matchedFragment;
                }
            }
        }
        return null;
    }

    /**
     * removeFragmentById
     *
     * Remove fragment with matching id from
     * child fragments.
     *
     * @param id fragment id to remove.
     * @return removed fragment
     */
    Fragment removeFragmentById(String id)
    {
        // remove from deep children
        if (fragments != null)
        {
            Iterator fragmentsIter = fragments.iterator();
            while (fragmentsIter.hasNext())
            {
                FragmentImpl fragment = (FragmentImpl)fragmentsIter.next();
                if (!fragment.getId().equals(id))
                {
                    Fragment removed = fragment.removeFragmentById(id);
                    if (removed != null)
                    {
                        return removed;
                    }
                }
                else
                {
                    fragmentsIter.remove();
                    return fragment;
                }
            }
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
                matchedFragments = new ArrayList(1);
            }
            matchedFragments.add(this);
        }
        // match children
        if (fragments != null)
        {
            Iterator fragmentsIter = fragments.iterator();
            while (fragmentsIter.hasNext())
            {
                List matchedChildFragments = ((FragmentImpl)fragmentsIter.next()).getFragmentsByName(name);
                if (matchedChildFragments != null)
                {
                    if (matchedFragments == null)
                    {
                        matchedFragments = matchedChildFragments;
                    }
                    else
                    {
                        matchedFragments.addAll(matchedChildFragments);
                    }
                }
            }
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
        List keys = new ArrayList(5);
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
        // delegate to page implementation
        if (page != null)
        {
            return page.getEffectivePageSecurity();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.impl.BaseElementImpl#getLogicalPermissionPath()
     */
    public String getLogicalPermissionPath()
    {
        // use page implementation path as base and append name
        if ((page != null) && (getName() != null))
        {
            return page.getLogicalPermissionPath() + Folder.PATH_SEPARATOR + getName();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.impl.BaseElementImpl#getPhysicalPermissionPath()
     */
    public String getPhysicalPermissionPath()
    {
        // use page implementation path as base and append name
        if ((page != null) && (getName() != null))
        {
            return page.getPhysicalPermissionPath() + Folder.PATH_SEPARATOR + getName();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.impl.BaseElementImpl#resetCachedSecurityConstraints()
     */
    public void resetCachedSecurityConstraints()
    {
        // propagate to super and sub fragments
        super.resetCachedSecurityConstraints();
        if (fragments != null)
        {
            Iterator fragmentsIter = fragments.iterator();
            while (fragmentsIter.hasNext())
            {
                ((FragmentImpl)fragmentsIter.next()).resetCachedSecurityConstraints();
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.impl.BaseElementImpl#checkPermissions(java.lang.String, int, boolean, boolean)
     */
    public void checkPermissions(String path, int mask, boolean checkNodeOnly, boolean checkParentsOnly) throws SecurityException
    {
        // always check for granted fragment permissions
        FragmentPermission permission = new FragmentPermission(path, mask);
        AccessController.checkPermission(permission);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#getConstraintsEnabled()
     */
    public boolean getConstraintsEnabled()
    {
        if (page != null)
        {
            return page.getConstraintsEnabled();
        }
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#getPermissionsEnabled()
     */
    public boolean getPermissionsEnabled()
    {
        if (page != null)
        {
            return page.getPermissionsEnabled();
        }
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
        // create and return mutable fragments collection
        // filtered by view access
        if (fragmentsList == null)
        {
            fragmentsList = new FragmentList(this);
        }
        return filterFragmentsByAccess(fragmentsList, true);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getProperty(java.lang.String)
     */
    public String getProperty(String propName)
    {
        return (String)getProperties().get(propName);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getIntProperty(java.lang.String)
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
     * @see org.apache.jetspeed.om.page.Fragment#getProperties()
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
        else
        {
            getProperties().remove(ROW_PROPERTY_NAME);
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
        else
        {
            getProperties().remove(COLUMN_PROPERTY_NAME);
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
        else
        {
            getProperties().remove(SIZES_PROPERTY_NAME);
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
     * @see org.apache.jetspeed.om.page.Fragment#getPreferences()
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
     * @see org.apache.jetspeed.om.page.Fragment#setPreferences(java.util.List)
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
     * filterFragmentsByAccess
     *
     * Filter fragments list for view access.
     *
     * @param nodes list containing fragments to check
     * @param mutable make returned list mutable
     * @return original list if all elements viewable, a filtered
     *         partial list, or null if all filtered for view access
     */
    List filterFragmentsByAccess(List fragments, boolean mutable)
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
                    fragment.checkAccess(JetspeedActions.VIEW);

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
                if (!filteredFragments.isEmpty())
                {
                    if (mutable)
                    {
                        return new FilteredFragmentList(this, filteredFragments);
                    }
                    else
                    {
                        return filteredFragments;
                    }
                }
                else
                {
                    return null;
                }
            }
        }
        return fragments;
    }
}
