/*
 * Copyright 2000-2004 The Apache Software Foundation.
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

package org.apache.jetspeed.om.page.psml;

import java.security.AccessController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.jetspeed.om.common.SecuredResource;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.security.FragmentPermission;

/**
 * @version $Id$
 */
public class FragmentImpl extends AbstractBaseElement implements Fragment, java.io.Serializable
{

    private String type = null;

    private String state = null;

    private String decorator = null;

    private String skin = null;

    private List fragments = new Vector();

    private List propertiesList = new Vector();
    
    private List preferences = new ArrayList();
    
    private Map propertiesMap = new HashMap();

    private String name;

    private FragmentList fragmentsList;

    private PageImpl page;

    /**
     * <p>
     * Default Constructor.
     * </p>
     */
    public FragmentImpl()
    {
    }

    public String getType()
    {
        return this.type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public String getState()
    {
        return this.state;
    }

    public void setState( String state )
    {
        this.state = state;
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

    public boolean isReference()
    {
        return false;
    }

    List accessFragments()
    {
        return fragments;
    }

    public List getFragments()
    {
        // create and return mutable fragments collection
        // filtered by view access
        if (fragmentsList == null)
        {
            fragmentsList = new FragmentList(this);
        }
        return filterFragmentsByAccess(fragmentsList);
    }

    public Vector getPropertiesList()
    {
        return (Vector) this.propertiesList;
    }
    
    /**
     * @see org.apache.jetspeed.om.page.Fragment#getProperty(java.lang.String)
     */
    public String getProperty(String propName)
    {
        return (String)propertiesMap.get(propName);
    }
    
    /**
     * @see org.apache.jetspeed.om.page.Fragment#getIntProperty(java.lang.String)
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
     * @see org.apache.jetspeed.om.page.Fragment#getProperties()
     */
    public Map getProperties()
    {
        return propertiesMap;
    }

    /**
     * @see org.apache.jetspeed.om.page.Fragment#getLayoutRow()
     */
    public int getLayoutRow()
    {
        String prop = (String)propertiesMap.get(ROW_PROPERTY_NAME);
        if (prop != null)
        {
            return Integer.parseInt(prop);
        }
        return -1;
    }

    /**
     * @see org.apache.jetspeed.om.page.Fragment#setLayoutRow(int)
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
     * @see org.apache.jetspeed.om.page.Fragment#getLayoutColumn()
     */
    public int getLayoutColumn()
    {
        String prop = (String)propertiesMap.get(COLUMN_PROPERTY_NAME);
        if (prop != null)
        {
            return Integer.parseInt(prop);
        }
        return -1;
    }

    /**
     * @see org.apache.jetspeed.om.page.Fragment#setLayoutColumn(int)
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
     * @see org.apache.jetspeed.om.page.Fragment#getLayoutSizes()
     */
    public String getLayoutSizes()
    {
        return (String)propertiesMap.get(SIZES_PROPERTY_NAME);
    }
    
    /**
     * @see org.apache.jetspeed.om.page.Fragment#setLayoutSizes(java.lang.String)
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
        if (obj != null && obj instanceof Fragment)
        {
            Fragment aFragment = (Fragment) obj;
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
            return (Fragment.class.getName() + ":" + getId()).hashCode();
        }
        else
        {
            return super.hashCode();
        }
    }

    /**
     * <p>
     * getName
     * </p>
     * 
     * @see org.apache.jetspeed.om.page.Fragment#getName()
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * <p>
     * setName
     * </p>
     * 
     * @see org.apache.jetspeed.om.page.Fragment#setName(java.lang.String)
     * @param name
     */
    public void setName( String name )
    {
        this.name = name;

    }

    /**
     * <p>
     * getPreferences
     * </p>
     * 
     * @see org.apache.jetspeed.om.page.Fragment#getPreferences()
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
    
    PageImpl getPage()
    {
        return page;
    }

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

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.psml.AbstractElementImpl#getEffectivePageSecurity()
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
     * @see org.apache.jetspeed.om.page.psml.AbstractElementImpl#getLogicalPermissionPath()
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
     * @see org.apache.jetspeed.om.page.psml.AbstractBaseElementImpl#getPhysicalPermissionPath()
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
     * @see org.apache.jetspeed.om.page.psml.AbstractElementImpl#checkPermissions(java.lang.String, java.lang.String, boolean, boolean)
     */
    public void checkPermissions(String path, String actions, boolean checkNodeOnly, boolean checkParentsOnly) throws SecurityException
    {
        // always check for granted fragment permissions
        FragmentPermission permission = new FragmentPermission(path, actions);
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

    /**
     * unmarshalled - notification that this instance has been
     *                loaded from the persistent store
     */
    public void unmarshalled()
    {
        // notify super class implementation
        super.unmarshalled();

        // propagate unmarshalled notification
        // to all fragments
        Iterator fragmentIter = fragments.iterator();
        while (fragmentIter.hasNext())
        {
            ((FragmentImpl)fragmentIter.next()).unmarshalled();
        }

        // load the properties map from list
        propertiesMap.clear();
        Iterator propsIter = propertiesList.iterator();
        while (propsIter.hasNext())
        {
            PropertyImpl prop = (PropertyImpl) propsIter.next();
            propertiesMap.put(prop.getName(), prop.getValue());
        }
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

        // propagate marshalling notification
        // to all fragments
        Iterator fragmentIter = fragments.iterator();
        while (fragmentIter.hasNext())
        {
            ((FragmentImpl)fragmentIter.next()).marshalling();
        }

        // notify super class implementation
        super.marshalling();
    }

    /**
     * filterFragmentsByAccess
     *
     * Filter fragments list for view access.
     *
     * @param nodes list containing fragments to check
     * @return original list if all elements viewable, a filtered
     *         partial list, or null if all filtered for view access
     */
    List filterFragmentsByAccess(List fragments)
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
                if (!filteredFragments.isEmpty())
                {
                    return new FilteredFragmentList(this, filteredFragments);
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
