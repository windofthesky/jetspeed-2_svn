/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.om.page.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.decoration.Decoration;
import org.apache.jetspeed.layout.PageLayoutComponent;
import org.apache.jetspeed.layout.impl.PageLayoutComponentUtils;
import org.apache.jetspeed.om.page.BaseFragmentsElement;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.FragmentReference;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.preference.FragmentPreference;
import org.apache.pluto.container.PortletPreference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Immutable content fragment implementation.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class ContentFragmentImpl implements ContentFragment, PageLayoutComponentUtils
{
    private static final Logger log = LoggerFactory.getLogger(ContentFragmentImpl.class);

    private PageLayoutComponent pageLayoutComponent;
    private String id;
    private Page page;
    private BaseFragmentsElement definition;
    private Fragment fragment;
    private FragmentReference reference;
    private boolean instantlyRendered;
    private boolean locked;
    
    private StringBuffer overriddenContent;
    private PortletContent portletContent;
    private Decoration decoration;

    private String decorator;
    private Map properties;
    private List fragments;
    private String mode;
    private String name;
    private List preferences;
    private String shortTitle;
    private String skin;
    private String state;
    private String title;
    private String type;

    /**
     * Construct new dynamic content fragment with
     * a transiently computed id.
     */
    public ContentFragmentImpl()
    {
        this.id = Integer.toHexString(System.identityHashCode(this));
    }
    
    /**
     * Construct new dynamic content fragment.
     * 
     * @param id content fragment id
     */
    public ContentFragmentImpl(String id)
    {
        this.id = id;
    }
    
    /**
     * Construct new dynamic content fragment with rendering flag.
     *
     * @param id content fragment id
     * @param instantlyRendered rendering flag, (rendering implies locked)
     */
    public ContentFragmentImpl(String id, boolean instantlyRendered)
    {
        this.id = id;
        this.instantlyRendered = instantlyRendered;
        this.locked = instantlyRendered;
    }
    
    /**
     * Construct new content fragment with PSML fragment.
     * 
     * @param pageLayoutComponent PageLayoutComponent instance
     * @param id content fragment id
     * @param page PSML page
     * @param definition PSML page, page template, or fragment definition
     * @param fragment PSML fragment
     * @param reference PSML page fragment reference
     * @param locked locked flag
     */
    public ContentFragmentImpl(PageLayoutComponent pageLayoutComponent, String id, Page page, BaseFragmentsElement definition, Fragment fragment, FragmentReference reference, boolean locked)
    {
        this.pageLayoutComponent = pageLayoutComponent;
        this.id = id;
        this.page = page;
        this.definition = definition;
        this.fragment = fragment;
        this.reference = reference;
        this.locked = locked;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#checkAccess(java.lang.String)
     */
    public void checkAccess(String actions) throws SecurityException
    {
        // check access against underlying fragment
        if (fragment != null)
        {
            fragment.checkAccess(actions);
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#isInstantlyRendered()
     */
    public boolean isInstantlyRendered()
    {
        return instantlyRendered;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o)
    {
        if (o instanceof ContentFragmentImpl)
        {
            ContentFragmentImpl cfi = (ContentFragmentImpl)o;
            return (((id == null) && (cfi.id == null)) || ((id != null) && id.equals(cfi.id)));
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getDecoration()
     */
    public Decoration getDecoration()
    {
        return decoration;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getDecorator()
     */
    public String getDecorator()
    {
        return decorator;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getFloatProperty(java.lang.String)
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
     * @see org.apache.jetspeed.om.page.ContentFragment#getFragments()
     */
    public List getFragments()
    {
        if (fragments == null)
        {
            fragments = new ArrayList();
        }
        return fragments;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getId()
     */
    public String getId()
    {
        return id;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getIntProperty(java.lang.String)
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
     * @see org.apache.jetspeed.om.page.ContentFragment#getLayoutColumn()
     */
    public int getLayoutColumn()
    {
        return getIntProperty(COLUMN_PROPERTY_NAME);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getLayoutHeight()
     */
    public float getLayoutHeight()
    {
        return getFloatProperty(HEIGHT_PROPERTY_NAME);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getLayoutRow()
     */
    public int getLayoutRow()
    {
        return getIntProperty(ROW_PROPERTY_NAME);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getLayoutSizes()
     */
    public String getLayoutSizes()
    {
        return getProperty(SIZES_PROPERTY_NAME);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getLayoutWidth()
     */
    public float getLayoutWidth()
    {
        return getFloatProperty(WIDTH_PROPERTY_NAME);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getLayoutX()
     */
    public float getLayoutX()
    {
        return getFloatProperty(X_PROPERTY_NAME);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getLayoutY()
     */
    public float getLayoutY()
    {
        return getFloatProperty(Y_PROPERTY_NAME);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getLayoutZ()
     */
    public float getLayoutZ()
    {
        return getFloatProperty(Z_PROPERTY_NAME);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getMode()
     */
    public String getMode()
    {
        return mode;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getName()
     */
    public String getName()
    {
        return name;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getOverriddenContent()
     */
    public String getOverriddenContent()
    {
        return ((overriddenContent != null) ? overriddenContent.toString() : null);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getPageLayoutComponent()
     */
    public PageLayoutComponent getPageLayoutComponent()
    {
        return pageLayoutComponent;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getPortletContent()
     */
    public PortletContent getPortletContent()
    {
        return this.portletContent;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getPreferences()
     */
    public List getPreferences()
    {
        if (preferences == null)
        {
            preferences = new ArrayList();
        }
        return preferences;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getProperties()
     */
    public Map getProperties()
    {
        if (properties == null)
        {
            properties = new HashMap();
        }
        return properties;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getProperty(java.lang.String)
     */
    public String getProperty(String propName)
    {
        return (String)getProperties().get(propName);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getRenderedContent()
     */
    public String getRenderedContent() throws IllegalStateException
    {       
        if (overriddenContent != null)
        {
            return overriddenContent.toString();
        }
        
        if (portletContent != null)
        {
            synchronized (portletContent)
            {
                if (portletContent.isComplete())
                {
                    return portletContent.getContent();
                }
                else
                {
                    try
                    {
                        log.debug("Waiting on content for Fragment " + getId());
                        portletContent.wait();
                        return portletContent.getContent();
                    }
                    catch (InterruptedException e)
                    {
                        return e.getMessage();
                    }
                    finally
                    {
                        log.debug("Been notified that Faragment " + getId() + " is complete");
                    }
                }
            }
        }
        else
        {
            throw new IllegalStateException("You cannot invoke getRenderedContent() until the content has been set.");
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getShortTitle()
     */
    public String getShortTitle()
    {
        return shortTitle;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getSkin()
     */
    public String getSkin()
    {
        return skin;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getState()
     */
    public String getState()
    {
        return state;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getTitle()
     */
    public String getTitle()
    {
        return title;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getType()
     */
    public String getType()
    {
        return type;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return ((id != null) ? id.hashCode() : 0);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#isLocked()
     */
    public boolean isLocked()
    {
        return locked;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#overrideRenderedContent(java.lang.String)
     */
    public void overrideRenderedContent(String content)
    {
        if ( content != null )
        {
            if (overriddenContent == null)
            {
                overriddenContent = new StringBuffer();
            }
            // prevent repeated storing of the same error message
            if (!content.equals(overriddenContent.toString()))
            {
                overriddenContent.append(content);
            }
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#setDecoration(org.apache.jetspeed.decoration.Decoration)
     */
    public void setDecoration(Decoration decoration)
    {
        this.decoration = decoration;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#setPortletContent(org.apache.jetspeed.aggregator.PortletContent)
     */
    public void setPortletContent(PortletContent portletContent)
    {
        this.portletContent = portletContent;        
    }    

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#addPortlet(java.lang.String, java.lang.String, int, int)
     */
    public ContentFragment addPortlet(String type, String name, int row, int column)
    {
        if (pageLayoutComponent != null)
        {
            // delegate to page layout component
            return pageLayoutComponent.addPortlet(this, type, name, row, column);
        }
        else
        {
            // perform locally only
            ContentFragmentImpl newContentFragmentImpl = new ContentFragmentImpl();
            newContentFragmentImpl.setType(type);
            newContentFragmentImpl.setType(name);
            if (!Utils.isNull(row))
            {
                newContentFragmentImpl.setLayoutRow(row);
            }
            if (!Utils.isNull(column))
            {
                newContentFragmentImpl.setLayoutColumn(column);
            }            
            getFragments().add(newContentFragmentImpl);
            return newContentFragmentImpl;
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#addPortlet(java.lang.String, java.lang.String)
     */
    public ContentFragment addPortlet(String type, String name)
    {
        if (pageLayoutComponent != null)
        {
            // delegate to page layout component
            return pageLayoutComponent.addPortlet(this, type, name);
        }
        else
        {
            // perform locally only
            ContentFragmentImpl newContentFragmentImpl = new ContentFragmentImpl();
            newContentFragmentImpl.setType(type);
            newContentFragmentImpl.setType(name);
            getFragments().add(newContentFragmentImpl);
            return newContentFragmentImpl;
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#updateDecorator(java.lang.String)
     */
    public void updateDecorator(String decoratorName)
    {
        if (pageLayoutComponent != null)
        {
            // delegate to page layout component
            pageLayoutComponent.updateDecorator(this, decoratorName);
        }
        else
        {
            // perform locally only
            decoratorName = (!Utils.isNull(decoratorName) ? decoratorName : null);
            setDecorator(decoratorName);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#updateName(java.lang.String)
     */
    public void updateName(String name)
    {
        if (pageLayoutComponent != null)
        {
            // delegate to page layout component
            pageLayoutComponent.updateName(this, name);
        }
        else
        {
            // perform locally only
            setName(name);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#updatePosition(float, float, float, float, float)
     */
    public void updatePosition(float x, float y, float z, float width, float height)
    {
        if (pageLayoutComponent != null)
        {
            // delegate to page layout component
            pageLayoutComponent.updatePosition(this, x, y, z, width, height);
        }
        else
        {
            // perform locally only
            if (!Utils.isNull(x))
            {
                setLayoutX(x);
            }
            if (!Utils.isNull(y))
            {
                setLayoutY(y);
            }
            if (!Utils.isNull(z))
            {
                setLayoutZ(z);
            }
            if (!Utils.isNull(width))
            {
                setLayoutWidth(width);
            }
            if (!Utils.isNull(height))
            {
                setLayoutWidth(height);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#updatePreferences(java.util.Map)
     */
    public void updatePreferences(Map preferences)
    {
        if (pageLayoutComponent != null)
        {
            // delegate to page layout component
            pageLayoutComponent.updatePreferences(this, preferences);
        }
        else
        {
            // perform locally only
            setPreferences(preferences);
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#updateRowColumn(int, int)
     */
    public void updateRowColumn(int row, int column)
    {
        if (pageLayoutComponent != null)
        {
            // delegate to page layout component
            pageLayoutComponent.updateRowColumn(this, row, column);
        }
        else
        {
            // perform locally only            
            if (!Utils.isNull(row))
            {
                setLayoutRow(row);
            }
            if (!Utils.isNull(column))
            {
                setLayoutColumn(column);
            }
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#updateStateMode(java.lang.String, java.lang.String)
     */
    public void updateStateMode(String portletState, String portletMode)
    {
        if (pageLayoutComponent != null)
        {
            // delegate to page layout component
            pageLayoutComponent.updateStateMode(this, portletState, portletMode);
        }
        else
        {
            // perform locally only            
            if (!Utils.isNull(portletState))
            {
                setState(portletState);
            }
            if (!Utils.isNull(portletMode))
            {
                setMode(portletMode);
            }            
        }
    }

    /**
     * Get content fragment by definition.
     * 
     * @param definition content fragment definition
     * @return content fragment
     */
    public ContentFragmentImpl getFragmentByDefinition(BaseFragmentsElement definition)
    {
        if (getDefinition() == definition)
        {
            return this;
        }
        Iterator fragmentIter = getFragments().iterator();
        while (fragmentIter.hasNext())
        {
            ContentFragmentImpl childFragment = (ContentFragmentImpl)fragmentIter.next();
            ContentFragmentImpl fragment = childFragment.getFragmentByDefinition(definition);
            if (fragment != null)
            {
                return fragment;
            }
        }
        return null;
    }

    /**
     * Get content fragment by id.
     * 
     * @param id content fragment id
     * @return content fragment
     */
    public ContentFragmentImpl getFragmentById(String id)
    {
        return getFragmentById(id, null);
    }

    /**
     * Get content fragment and parent by id.
     * 
     * @param id content fragment id
     * @param parent returned parent content fragment
     * @return content fragment
     */
    public ContentFragmentImpl getFragmentById(String id, ContentFragmentImpl [] parentFragment)
    {
        if (getId().equals(id))
        {
            return this;
        }
        Iterator fragmentIter = getFragments().iterator();
        while (fragmentIter.hasNext())
        {
            ContentFragmentImpl childFragment = (ContentFragmentImpl)fragmentIter.next();
            ContentFragmentImpl fragment = childFragment.getFragmentById(id, parentFragment);
            if (fragment != null)
            {
                if ((parentFragment != null) && (parentFragment[0] == null))
                {
                    parentFragment[0] = this;
                }
                return fragment;
            }
        }
        return null;
    }

    /**
     * Get content fragment by underlying PSML Fragment id.
     * 
     * @param id PSML fragment id
     * @return content fragment
     */
    public ContentFragmentImpl getFragmentByFragmentId(String id)
    {
        if (fragment != null && fragment.getId().equals(id))
        {
            return this;
        }
        Iterator fragmentIter = getFragments().iterator();
        while (fragmentIter.hasNext())
        {
            ContentFragmentImpl childFragment = (ContentFragmentImpl)fragmentIter.next();
            ContentFragmentImpl fragment = childFragment.getFragmentByFragmentId(id);
            if (fragment != null)
            {
                return fragment;
            }
        }
        return null;
    }

    /**
     * Get content fragment nesting level.
     * 
     * @param fragmentId fragment id to find
     * @param level nesting level fragment
     * @return nesting level or -1 if not found
     */
    public int getFragmentNestingLevel(String fragmentId, int level)
    {
        if (fragmentId.equals(id))
        {
            return level;
        }
        int childLevel = level+1;
        Iterator fragmentIter = getFragments().iterator();
        while (fragmentIter.hasNext())
        {
            ContentFragmentImpl childFragment = (ContentFragmentImpl)fragmentIter.next();
            int fragmentNestingLevel = childFragment.getFragmentNestingLevel(fragmentId, childLevel);
            if (fragmentNestingLevel != -1)
            {
                return fragmentNestingLevel;
            }
        }        
        return -1;
    }

    /**
     * Get all content fragments with name.
     * 
     * @param name content fragment name
     * @return list of content fragments
     */
    public List getFragmentsByName(String name)
    {
        List fragments = null;
        if (getName().equals(name))
        {
            if (fragments == null)
            {
                fragments = new ArrayList();
            }
            fragments.add(this);
        }
        Iterator fragmentIter = getFragments().iterator();
        while (fragmentIter.hasNext())
        {
            ContentFragmentImpl childFragment = (ContentFragmentImpl)fragmentIter.next();
            List childFragments = childFragment.getFragmentsByName(name);
            if (childFragments != null)
            {
                if (fragments == null)
                {
                    fragments = childFragments;
                }
                else
                {
                    fragments.addAll(childFragments);
                }
            }
        }
        return fragments;
    }
    
    /**
     * Get content page PSML page.
     * 
     * @return the PSML page
     */
    public Page getPage()
    {
        return page;
    }

    /**
     * Get content fragment PSML page, page template, or
     * fragment definition.
     * 
     * @return the PSML definition
     */
    public BaseFragmentsElement getDefinition()
    {
        return definition;
    }

    /**
     * Get content fragment PSML fragment.
     * 
     * @return the fragment
     */
    public Fragment getFragment()
    {
        return fragment;
    }

    /**
     * Get content fragment PSML page reference fragment.
     * 
     * @return the reference fragment
     */
    public FragmentReference getReference()
    {
        return reference;
    }

    /**
     * Initialize content fragment.
     * 
     * @param pageLayoutComponent PageLayoutComponent instance
     * @param page PSML page
     * @param definition PSML page, page template, or fragment definition
     * @param fragment PSML fragment
     * @param reference PSML page fragment reference
     * @param locked locked flag
     */
    public void initialize(PageLayoutComponent pageLayoutComponent, Page page, BaseFragmentsElement definition, Fragment fragment, FragmentReference reference, boolean locked)
    {
        this.pageLayoutComponent = pageLayoutComponent;
        this.page = page;
        this.definition = definition;
        this.fragment = fragment;
        this.reference = reference;
        this.locked = locked;
    }

    /**
     * Remove content fragment by id.
     * 
     * @param id content fragment id
     * @return content fragment
     */
    public ContentFragmentImpl removeFragmentById(String id)
    {
        ContentFragmentImpl removed = null;
        Iterator fragmentIter = getFragments().iterator();
        while ((removed == null) && fragmentIter.hasNext())
        {
            ContentFragmentImpl childFragment = (ContentFragmentImpl)fragmentIter.next();
            if (childFragment.getId().equals(id))
            {
                fragmentIter.remove();
                removed = childFragment;
            }
            else
            {
                removed = childFragment.removeFragmentById(id);
            }
        }
        return removed;
    }

    /**
     * Set content fragment decorator.
     * 
     * @param decorator the decorator to set
     */
    public void setDecorator(String decorator)
    {
        this.decorator = decorator;
    }

    /**
     * Set int property.
     * 
     * @param name property name
     * @param value int property value
     */
    public void setIntProperty(String name, int value)
    {
        if (value >= 0)
        {
            getProperties().put(name, String.valueOf(value));
        }
        else
        {
            getProperties().remove(name);            
        }
    }

    /**
     * Set float property.
     * 
     * @param name property name
     * @param value float property value
     */
    public void setFloatProperty(String name, float value)
    {
        if (value >= 0)
        {
            getProperties().put(name, String.valueOf(value));
        }
        else
        {
            getProperties().remove(name);            
        }
    }

    /**
     * Set layout column property.
     * 
     * @param column column property value
     */
    public void setLayoutColumn(int column)
    {
        setIntProperty(COLUMN_PROPERTY_NAME, column);
    }

    /**
     * Set layout height property.
     * 
     * @param height height property value
     */
    public void setLayoutHeight(float height)
    {
        setFloatProperty(HEIGHT_PROPERTY_NAME, height);
    }

    /**
     * Set layout row property.
     * 
     * @param row row property value
     */
    public void setLayoutRow(int row)
    {
        setIntProperty(ROW_PROPERTY_NAME, row);
    }

    /**
     * Set layout width property.
     * 
     * @param width width property value
     */
    public void setLayoutWidth(float width)
    {
        setFloatProperty(WIDTH_PROPERTY_NAME, width);
    }

    /**
     * Set layout x property.
     * 
     * @param x x property value
     */
    public void setLayoutX(float x)
    {
        setFloatProperty(X_PROPERTY_NAME, x);
    }

    /**
     * Set layout y property.
     * 
     * @param y y property value
     */
    public void setLayoutY(float y)
    {
        setFloatProperty(Y_PROPERTY_NAME, y);
    }

    /**
     * Set layout z property.
     * 
     * @param z z property value
     */
    public void setLayoutZ(float z)
    {
        setFloatProperty(Z_PROPERTY_NAME, z);
    }

    /**
     * Set content fragment mode.
     * 
     * @param mode the mode to set
     */
    public void setMode(String mode)
    {
        this.mode = mode;
    }

    /**
     * Set content fragment name.
     * 
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }
    
    /**
     * Set preferences with new preferences set, accepting
     * Map of strings, string arrays, FragmentPreference or
     * PortletPreference.
     * 
     * @param preferences map of new preferences set.
     */
    public void setPreferences(Map preferences)
    {
        this.preferences.clear();
        if (preferences != null)
        {
            Iterator preferencesIter = preferences.entrySet().iterator();
            while (preferencesIter.hasNext())
            {
                Map.Entry preferencesEntry = (Map.Entry)preferencesIter.next();
                String name = (String)preferencesEntry.getKey();
                Object values = preferencesEntry.getValue();
                ContentFragmentPreferenceImpl preference = null;
                if (values instanceof String)
                {
                    preference = new ContentFragmentPreferenceImpl(name, false, Arrays.asList(new String[]{(String)values}));
                }
                else if (values instanceof String [])
                {
                    preference = new ContentFragmentPreferenceImpl(name, false, Arrays.asList((String [])values));
                }
                else if (values instanceof FragmentPreference)
                {
                    FragmentPreference fragmentPreference = (FragmentPreference)values;
                    preference = new ContentFragmentPreferenceImpl(name, fragmentPreference.isReadOnly(), fragmentPreference.getValueList());
                }
                else if (values instanceof PortletPreference)
                {
                    PortletPreference portletPreference = (PortletPreference)values;
                    preference = new ContentFragmentPreferenceImpl(name, portletPreference.isReadOnly(), Arrays.asList(portletPreference.getValues()));
                }
                else
                {
                    throw new IllegalArgumentException("Unexpected preference value type");
                }
                this.preferences.add(preference);
            }
        }        
    }

    /**
     * Set content fragment short title.
     * 
     * @param shortTitle the shortTitle to set
     */
    public void setShortTitle(String shortTitle)
    {
        this.shortTitle = shortTitle;
    }

    /**
     * Set content fragment skin.
     * 
     * @param skin the skin to set
     */
    public void setSkin(String skin)
    {
        this.skin = skin;
    }

    /**
     * Set content fragment state.
     * 
     * @param state the state to set
     */
    public void setState(String state)
    {
        this.state = state;
    }

    /**
     * Set content fragment title.
     * 
     * @param title the title to set
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Set content fragment type.
     * 
     * @param type the type to set
     */
    public void setType(String type)
    {
        this.type = type;
    }
    
    /**
     * Set content fragment id.
     * 
     * @param id the id to set
     */
    public void setId(String id)
    {
        this.id = id;
    }
}
        