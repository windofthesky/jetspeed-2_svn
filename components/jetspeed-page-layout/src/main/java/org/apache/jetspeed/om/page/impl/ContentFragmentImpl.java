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

import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.decoration.Decoration;
import org.apache.jetspeed.layout.PageLayoutComponent;
import org.apache.jetspeed.layout.impl.PageLayoutComponentUtils;
import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.page.BaseFragmentElement;
import org.apache.jetspeed.om.page.BaseFragmentsElement;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.FragmentProperty;
import org.apache.jetspeed.om.page.FragmentReference;
import org.apache.jetspeed.om.preference.FragmentPreference;
import org.apache.pluto.container.PortletPreference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private BaseFragmentsElement pageOrTemplate;
    private BaseFragmentsElement definition;
    private BaseFragmentElement fragment;
    private BaseFragmentsElement referenceDefinition;
    private FragmentReference reference;
    private boolean instantlyRendered;
    private boolean template;
    private boolean locked;
    
    private StringBuffer overriddenContent;
    private PortletContent portletContent;
    private Decoration decoration;

    private List properties;
    private List fragments;
    private String name;
    private List preferences;
    private String shortTitle;
    private String title;
    private String type;
    private SecurityConstraints constraints;
    private String refId;

    private long refreshRate = -1;
    private String refreshFunction = null;

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
     * @param pageOrTemplate PSML page or template
     * @param definition PSML page, page template, or fragment definition
     * @param fragment PSML fragment
     * @param referenceDefinition PSML page or page template
     * @param reference PSML fragment reference
     * @param template template flag
     * @param locked locked flag
     */
    public ContentFragmentImpl(PageLayoutComponent pageLayoutComponent, String id, BaseFragmentsElement pageOrTemplate, BaseFragmentsElement definition, BaseFragmentElement fragment, BaseFragmentsElement referenceDefinition, FragmentReference reference, boolean template, boolean locked)
    {
        this.pageLayoutComponent = pageLayoutComponent;
        this.id = id;
        this.pageOrTemplate = pageOrTemplate;
        this.definition = definition;
        this.fragment = fragment;
        this.referenceDefinition = referenceDefinition;
        this.reference = reference;
        this.template = template;
        this.locked = locked;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getSecurityConstraints()
     */
    public SecurityConstraints getSecurityConstraints()
    {
        return constraints;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#newSecurityConstraint()
     */
    public SecurityConstraint newSecurityConstraint()
    {
        return new ContentSecurityConstraint(true, null, null, null, null);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#newSecurityConstraints()
     */
    public SecurityConstraints newSecurityConstraints()
    {
        return new ContentSecurityConstraints(true, null, null, null);
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
        return getProperty(DECORATOR_PROPERTY_NAME);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getFloatProperty(java.lang.String)
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
     * @see org.apache.jetspeed.om.page.ContentFragment#getFloatProperty(java.lang.String, java.lang.String, java.lang.String)
     */
    public float getFloatProperty(String propName, String scope, String scopeValue)
    {
        String propValue = getProperty(propName, scope, scopeValue);
        if (propValue != null)
        {
            return Float.parseFloat(propValue);
        }
        return -1.0F;        
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getFragmentId()
     */
    public String getFragmentId()
    {
        return ((fragment != null) ? fragment.getId() : null);
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
        String propValue = getProperty(propName);
        if (propValue != null)
        {
            return Integer.parseInt(propValue);
        }
        return -1;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getIntProperty(java.lang.String, java.lang.String, java.lang.String)
     */
    public int getIntProperty(String propName, String scope, String scopeValue)
    {
        String propValue = getProperty(propName, scope, scopeValue);
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
        return getProperty(MODE_PROPERTY_NAME);
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
    public List<FragmentPreference> getPreferences()
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
    public List<FragmentProperty> getProperties()
    {
        if (properties == null)
        {
            properties = new ArrayList();
        }
        return properties;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getPropertiesMap()
     */
    public Map<String,String> getPropertiesMap()
    {
        // get property names
        Set<String> propertyNames = new HashSet<String>();
        Iterator propertiesIter = getProperties().iterator();
        while (propertiesIter.hasNext())
        {
            FragmentProperty fragmentProperty = (FragmentProperty)propertiesIter.next();
            propertyNames.add(fragmentProperty.getName());
        }
        
        // construct and return properties map 
        Map<String,String> propertiesMap = new HashMap<String,String>();
        Iterator propertyNamesIter = propertyNames.iterator();
        while (propertyNamesIter.hasNext())
        {
            String propertyName = (String)propertyNamesIter.next();
            String propertyValue = getProperty(propertyName);
            if (propertyValue != null)
            {
                propertiesMap.put(propertyName, propertyValue);
            }
        }
        return propertiesMap;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getProperty(java.lang.String)
     */
    public String getProperty(String propName)
    {
        // scoped property values
        String userValue = null;
        String groupValue = null;
        String roleValue = null;
        String globalValue = null;

        // iterate through properties list to determine most specific
        // property value; assumes properties are already filtered
        // for current user user, group, and role scopes
        Iterator propertiesIter = getProperties().iterator();
        while ((userValue == null) && propertiesIter.hasNext())
        {
            FragmentProperty fragmentProperty = (FragmentProperty)propertiesIter.next();
            if (fragmentProperty.getName().equals(propName))
            {
                String fragmentPropertyScope = fragmentProperty.getScope();
                if (fragmentPropertyScope != null)
                {
                    if (fragmentPropertyScope.equals(USER_PROPERTY_SCOPE))
                    {
                        userValue = fragmentProperty.getValue();
                    }
                    else if (GROUP_AND_ROLE_PROPERTY_SCOPES_ENABLED)
                    {
                        if (groupValue == null)
                        {
                            if (fragmentPropertyScope.equals(GROUP_PROPERTY_SCOPE))
                            {
                                groupValue = fragmentProperty.getValue();
                            }
                            else if (roleValue == null)
                            {
                                if (fragmentPropertyScope.equals(ROLE_PROPERTY_SCOPE))
                                {
                                    roleValue = fragmentProperty.getValue();
                                }
                            }
                        }
                    }
                }
                else if ((groupValue == null) && (roleValue == null) && (globalValue == null))
                {
                    globalValue = fragmentProperty.getValue();
                }
            }
        }

        // return most specifically scoped property value
        return ((userValue != null) ? userValue : ((groupValue != null) ? groupValue : ((roleValue != null) ? roleValue : globalValue)));
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getProperty(java.lang.String, java.lang.String, java.lang.String)
     */
    public String getProperty(String propName, String scope, String scopeValue)
    {
        // iterate through properties list to get property value
        Iterator propertiesIter = getProperties().iterator();
        while (propertiesIter.hasNext())
        {
            FragmentProperty fragmentProperty = (FragmentProperty)propertiesIter.next();
            if (fragmentProperty.getName().equals(propName))
            {
                // compare scopes
                String fragmentPropertyScope = fragmentProperty.getScope();
                if ((fragmentPropertyScope == null) && (scope == null))
                {
                    return fragmentProperty.getValue();                    
                }
                else if ((fragmentPropertyScope != null) && fragmentPropertyScope.equals(scope))
                {
                    // default user scope value
                    if ((scopeValue == null) && scope.equals(USER_PROPERTY_SCOPE))
                    {
                        scopeValue = Utils.getCurrentUserScopeValue();
                    }
                    // compare scope values
                    String fragmentPropertyScopeValue = fragmentProperty.getScopeValue();
                    if (((fragmentPropertyScopeValue == null) && (scopeValue == null)) ||
                        ((fragmentPropertyScopeValue != null) && fragmentPropertyScopeValue.equals(scopeValue)))
                    {
                        return fragmentProperty.getValue();                        
                    }
                }
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getRefId()
     */
    public String getRefId()
    {
        return refId;
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
        return getProperty(SKIN_PROPERTY_NAME);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getState()
     */
    public String getState()
    {
        return getProperty(STATE_PROPERTY_NAME);
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
     * @see org.apache.jetspeed.om.page.ContentFragment#isTemplate()
     */
    public boolean isTemplate()
    {
        return template;
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
        updateDecorator(decoratorName, null, null);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#updateDecorator(java.lang.String, java.lang.String, java.lang.String)
     */
    public void updateDecorator(String decoratorName, String scope, String scopeValue)
    {
        if (pageLayoutComponent != null)
        {
            // delegate to page layout component
            pageLayoutComponent.updateDecorator(this, decoratorName, scope, scopeValue);
        }
        else
        {
            // perform locally only
            decoratorName = (!Utils.isNull(decoratorName) ? decoratorName : null);
            setDecorator(scope, scopeValue, decoratorName);
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
        updatePosition(x, y, z, width, height, null, null);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#updatePosition(float, float, float, float, float, java.lang.String, java.lang.String)
     */
    public void updatePosition(float x, float y, float z, float width, float height, String scope, String scopeValue)
    {
        if (pageLayoutComponent != null)
        {
            // delegate to page layout component
            pageLayoutComponent.updatePosition(this, x, y, z, width, height, scope, scopeValue);
        }
        else
        {
            // perform locally only
            if (!Utils.isNull(x))
            {
                setLayoutX(scope, scopeValue, x);
            }
            if (!Utils.isNull(y))
            {
                setLayoutY(scope, scopeValue, y);
            }
            if (!Utils.isNull(z))
            {
                setLayoutZ(scope, scopeValue, z);
            }
            if (!Utils.isNull(width))
            {
                setLayoutWidth(scope, scopeValue, width);
            }
            if (!Utils.isNull(height))
            {
                setLayoutWidth(scope, scopeValue, height);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#updatePreferences(java.util.Map)
     */
    public void updatePreferences(Map<String,?> preferences)
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
     * @see org.apache.jetspeed.om.page.ContentFragment#updateProperty(java.lang.String, java.lang.String)
     */
    public void updateProperty(String propName, String propValue)
    {
        updateProperty(propName, propValue, null, null);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#updateProperty(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void updateProperty(String propName, String propValue, String scope, String scopeValue)
    {
        if (pageLayoutComponent != null)
        {
            // delegate to page layout component
            pageLayoutComponent.updateProperty(this, propName, propValue, scope, scopeValue);
        }
        else
        {
            // perform locally only            
            setProperty(propName, scope, scopeValue, propValue);
        }        
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#updateRefId(java.lang.String)
     */
    public void updateRefId(String refId)
    {
        if (pageLayoutComponent != null)
        {
            // delegate to page layout component
            pageLayoutComponent.updateRefId(this, refId);
        }
        else
        {
            // perform locally only            
            setRefId(refId);
        }                
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#updateRowColumn(int, int)
     */
    public void updateRowColumn(int row, int column)
    {
        updateRowColumn(row, column, null, null);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#updateRowColumn(int, int, java.lang.String, java.lang.String)
     */
    public void updateRowColumn(int row, int column, String scope, String scopeValue)
    {
        if (pageLayoutComponent != null)
        {
            // delegate to page layout component
            pageLayoutComponent.updateRowColumn(this, row, column, scope, scopeValue);
        }
        else
        {
            // perform locally only            
            if (!Utils.isNull(row))
            {
                setLayoutRow(scope, scopeValue, row);
            }
            if (!Utils.isNull(column))
            {
                setLayoutColumn(scope, scopeValue, column);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#updateSecurityConstraints(org.apache.jetspeed.om.common.SecurityConstraints)
     */
    public void updateSecurityConstraints(SecurityConstraints constraints)
    {
        if (pageLayoutComponent != null)
        {
            // delegate to page layout component
            pageLayoutComponent.updateSecurityConstraints(this, constraints);
        }
        else
        {
            // perform locally only
            setSecurityConstraints(constraints);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#updateStateMode(java.lang.String, java.lang.String)
     */
    public void updateStateMode(String portletState, String portletMode)
    {
        updateStateMode(portletState, portletMode, null, null);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#updateStateMode(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void updateStateMode(String portletState, String portletMode, String scope, String scopeValue)
    {
        if (pageLayoutComponent != null)
        {
            // delegate to page layout component
            pageLayoutComponent.updateStateMode(this, portletState, portletMode, scope, scopeValue);
        }
        else
        {
            // perform locally only            
            if (!Utils.isNull(portletState))
            {
                setState(scope, scopeValue, portletState);
            }
            if (!Utils.isNull(portletMode))
            {
                setMode(scope, scopeValue, portletMode);
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
        if (this.definition == definition)
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
     * @param parentFragment returned parent content fragment
     * @return content fragment
     */
    public ContentFragmentImpl getFragmentById(String id, ContentFragmentImpl [] parentFragment)
    {
        if (this.id.equals(id))
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
     * @param nonTemplate return non-template nodes only
     * @return content fragment
     */
    public ContentFragmentImpl getFragmentByFragmentId(String id, boolean nonTemplate)
    {
        if ((fragment != null) && fragment.getId().equals(id) && (!nonTemplate || !template))
        {
            return this;
        }
        Iterator fragmentIter = getFragments().iterator();
        while (fragmentIter.hasNext())
        {
            ContentFragmentImpl childFragment = (ContentFragmentImpl)fragmentIter.next();
            ContentFragmentImpl fragment = childFragment.getFragmentByFragmentId(id, nonTemplate);
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
     * @param nonTemplate return non-template nodes only
     * @return list of content fragments
     */
    public List getFragmentsByName(String name, boolean nonTemplate)
    {
        List fragments = null;
        if ((this.name != null) && this.name.equals(name) && (!nonTemplate || !template))
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
            List childFragments = childFragment.getFragmentsByName(name, nonTemplate);
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
     * Get first non-template layout fragment.
     * 
     * @return non-template fragment
     */
    public ContentFragment getNonTemplateLayoutFragment()
    {
        if ((type != null) && type.equals(ContentFragment.LAYOUT) && !template)
        {
            return this;
        }
        Iterator fragmentIter = getFragments().iterator();
        while (fragmentIter.hasNext())
        {
            ContentFragment nonTemplateLayoutFragment = ((ContentFragmentImpl)fragmentIter.next()).getNonTemplateLayoutFragment();
            if (nonTemplateLayoutFragment != null)
            {
                return nonTemplateLayoutFragment;
            }
        }        
        return null;
    }

    /**
     * Get content page PSML page.
     * 
     * @return the PSML page
     */
    public BaseFragmentsElement getPageOrTemplate()
    {
        return pageOrTemplate;
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
    public BaseFragmentElement getFragment()
    {
        return fragment;
    }

    /**
     * Get content fragment PSML reference fragment definition.
     * 
     * @return the reference fragment definition
     */
    public BaseFragmentsElement getReferenceDefinition()
    {
        return referenceDefinition;
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
     * @param pageOrTemplate PSML page or template
     * @param definition PSML page, page template, or fragment definition
     * @param fragment PSML fragment
     * @param reference definition PSML page or page template
     * @param reference PSML fragment reference
     * @param template template flag
     * @param locked locked flag
     */
    public void initialize(PageLayoutComponent pageLayoutComponent, BaseFragmentsElement pageOrTemplate, BaseFragmentsElement definition, BaseFragmentElement fragment, BaseFragmentsElement referenceDefinition, FragmentReference reference, boolean template, boolean locked)
    {
        this.pageLayoutComponent = pageLayoutComponent;
        this.pageOrTemplate = pageOrTemplate;
        this.definition = definition;
        this.fragment = fragment;
        this.referenceDefinition = referenceDefinition;
        this.reference = reference;
        this.template = template;
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
     * Set global content fragment decorator.
     * 
     * @param decorator the decorator to set
     */
    public void setDecorator(String decorator)
    {
        setProperty(DECORATOR_PROPERTY_NAME, null, null, decorator);
    }

    /**
     * Set content fragment decorator.
     * 
     * @param scope property scope
     * @param scopeValue property scope value
     * @param decorator the decorator to set
     */
    public void setDecorator(String scope, String scopeValue, String decorator)
    {
        setProperty(DECORATOR_PROPERTY_NAME, scope, scopeValue, decorator);
    }

    /**
     * Set int property.
     * 
     * @param name property name
     * @param scope property scope
     * @param scopeValue property scope value
     * @param value int property value
     */
    public void setIntProperty(String name, String scope, String scopeValue, int value)
    {
        setProperty(name, scope, scopeValue, ((value >= 0) ? String.valueOf(value) : null));
    }

    /**
     * Set float property.
     * 
     * @param name property name
     * @param scope property scope
     * @param scopeValue property scope value
     * @param value float property value
     */
    public void setFloatProperty(String name, String scope, String scopeValue, float value)
    {
        setProperty(name, scope, scopeValue, ((value >= 0.0F) ? String.valueOf(value) : null));
    }

    /**
     * Set global layout column property.
     * 
     * @param column column property value
     */
    public void setLayoutColumn(int column)
    {
        setIntProperty(COLUMN_PROPERTY_NAME, null, null, column);
    }

    /**
     * Set layout column property.
     * 
     * @param scope property scope
     * @param scopeValue property scope value
     * @param column column property value
     */
    public void setLayoutColumn(String scope, String scopeValue, int column)
    {
        setIntProperty(COLUMN_PROPERTY_NAME, scope, scopeValue, column);
    }

    /**
     * Set global layout height property.
     * 
     * @param height height property value
     */
    public void setLayoutHeight(float height)
    {
        setFloatProperty(HEIGHT_PROPERTY_NAME, null, null, height);
    }

    /**
     * Set layout height property.
     * 
     * @param scope property scope
     * @param scopeValue property scope value
     * @param height height property value
     */
    public void setLayoutHeight(String scope, String scopeValue, float height)
    {
        setFloatProperty(HEIGHT_PROPERTY_NAME, scope, scopeValue, height);
    }

    /**
     * Set global layout sizes property.
     * 
     * @param sizes sizes property value
     */
    public void setLayoutSizes(String sizes)
    {
        setProperty(SIZES_PROPERTY_NAME, null, null, sizes);
    }

    /**
     * Set layout sizes property.
     * 
     * @param scope property scope
     * @param scopeValue property scope value
     * @param sizes sizes property value
     */
    public void setLayoutSizes(String scope, String scopeValue, String sizes)
    {
        setProperty(SIZES_PROPERTY_NAME, scope, scopeValue, sizes);
    }

    /**
     * Set global layout row property.
     * 
     * @param row row property value
     */
    public void setLayoutRow(int row)
    {
        setIntProperty(ROW_PROPERTY_NAME, null, null, row);
    }

    /**
     * Set layout row property.
     * 
     * @param scope property scope
     * @param scopeValue property scope value
     * @param row row property value
     */
    public void setLayoutRow(String scope, String scopeValue, int row)
    {
        setIntProperty(ROW_PROPERTY_NAME, scope, scopeValue, row);
    }

    /**
     * Set global layout width property.
     * 
     * @param width width property value
     */
    public void setLayoutWidth(float width)
    {
        setFloatProperty(WIDTH_PROPERTY_NAME, null, null, width);
    }

    /**
     * Set layout width property.
     * 
     * @param scope property scope
     * @param scopeValue property scope value
     * @param width width property value
     */
    public void setLayoutWidth(String scope, String scopeValue, float width)
    {
        setFloatProperty(WIDTH_PROPERTY_NAME, scope, scopeValue, width);
    }

    /**
     * Set global layout x property.
     * 
     * @param x x property value
     */
    public void setLayoutX(float x)
    {
        setFloatProperty(X_PROPERTY_NAME, null, null, x);
    }

    /**
     * Set layout x property.
     * 
     * @param scope property scope
     * @param scopeValue property scope value
     * @param x x property value
     */
    public void setLayoutX(String scope, String scopeValue, float x)
    {
        setFloatProperty(X_PROPERTY_NAME, scope, scopeValue, x);
    }

    /**
     * Set global layout y property.
     * 
     * @param y y property value
     */
    public void setLayoutY(float y)
    {
        setFloatProperty(Y_PROPERTY_NAME, null, null, y);
    }

    /**
     * Set layout y property.
     * 
     * @param scope property scope
     * @param scopeValue property scope value
     * @param y y property value
     */
    public void setLayoutY(String scope, String scopeValue, float y)
    {
        setFloatProperty(Y_PROPERTY_NAME, scope, scopeValue, y);
    }

    /**
     * Set global layout z property.
     * 
     * @param z z property value
     */
    public void setLayoutZ(float z)
    {
        setFloatProperty(Z_PROPERTY_NAME, null, null, z);
    }

    /**
     * Set layout z property.
     * 
     * @param scope property scope
     * @param scopeValue property scope value
     * @param z z property value
     */
    public void setLayoutZ(String scope, String scopeValue, float z)
    {
        setFloatProperty(Z_PROPERTY_NAME, scope, scopeValue, z);
    }

    /**
     * Set property.
     * 
     * @param propName property name
     * @param scope property scope
     * @param scopeValue property scope value
     * @param value property value
     */
    public void setProperty(String propName, String scope, String scopeValue, String value)
    {
        // iterate through properties list to find property
        FragmentProperty fragmentProperty = null;
        Iterator propertiesIter = getProperties().iterator();
        while (propertiesIter.hasNext())
        {
            FragmentProperty findFragmentProperty = (FragmentProperty)propertiesIter.next();
            if (findFragmentProperty.getName().equals(propName))
            {
                // compare scopes
                String findFragmentPropertyScope = findFragmentProperty.getScope();
                if ((scope == null) && (findFragmentPropertyScope == null))
                {
                    fragmentProperty = findFragmentProperty;
                    break;
                }
                else if ((findFragmentPropertyScope != null) && findFragmentPropertyScope.equals(scope))
                {
                    // default user scope value
                    if ((scopeValue == null) && scope.equals(USER_PROPERTY_SCOPE))
                    {
                        scopeValue = Utils.getCurrentUserScopeValue();
                    }
                    // compare scope values                    
                    String findFragmentPropertyScopeValue = findFragmentProperty.getScopeValue();
                    if ((findFragmentPropertyScopeValue != null) && findFragmentPropertyScopeValue.equals(scopeValue))
                    {
                        fragmentProperty = findFragmentProperty;
                        break;
                    }
                }
            }
        }
        
        // add, set, or remove property
        if (fragmentProperty != null)
        {
            // remove old property setting
            getProperties().remove(fragmentProperty);                
        }
        if (value != null)
        {
            // default user scope value
            if ((scopeValue == null) && (scope != null) && scope.equals(USER_PROPERTY_SCOPE))
            {
                scopeValue = Utils.getCurrentUserScopeValue();
            }
            // add new property
            getProperties().add(new ContentFragmentPropertyImpl(propName, scope, scopeValue, value));
        }
    }

    /**
     * Set global content fragment mode.
     * 
     * @param mode the mode to set
     */
    public void setMode(String mode)
    {
        setProperty(MODE_PROPERTY_NAME, null, null, mode);
    }

    /**
     * Set content fragment mode.
     * 
     * @param scope property scope
     * @param scopeValue property scope value
     * @param mode the mode to set
     */
    public void setMode(String scope, String scopeValue, String mode)
    {
        setProperty(MODE_PROPERTY_NAME, scope, scopeValue, mode);
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
        getPreferences().clear();
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
                    List<String> prefValueList = null;
                    String [] prefValues = portletPreference.getValues();
                    if (prefValues != null)
                    {
                        prefValueList = Arrays.asList(prefValues);
                    }
                    preference = new ContentFragmentPreferenceImpl(name, portletPreference.isReadOnly(), prefValueList);
                }
                else
                {
                    throw new IllegalArgumentException("Unexpected preference value type");
                }
                getPreferences().add(preference);
            }
        }        
    }
    
    /**
     * Set reference fragment id.
     * 
     * @param refId reference id
     */
    public void setRefId(String refId)
    {
        this.refId = refId;
    }
    
    /**
     * Set content security constraints.
     * 
     * @param constraints security constraints
     */
    public void setSecurityConstraints(SecurityConstraints constraints)
    {
        this.constraints = constraints;
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
     * Set global content fragment skin.
     * 
     * @param skin the skin to set
     */
    public void setSkin(String skin)
    {
        setProperty(SKIN_PROPERTY_NAME, null, null, skin);
    }

    /**
     * Set content fragment skin.
     * 
     * @param scope property scope
     * @param scopeValue property scope value
     * @param skin the skin to set
     */
    public void setSkin(String scope, String scopeValue, String skin)
    {
        setProperty(SKIN_PROPERTY_NAME, scope, scopeValue, skin);
    }

    /**
     * Set global content fragment state.
     * 
     * @param state the state to set
     */
    public void setState(String state)
    {
        setProperty(STATE_PROPERTY_NAME, null, null, state);
    }

    /**
     * Set content fragment state.
     * 
     * @param scope property scope
     * @param scopeValue property scope value
     * @param state the state to set
     */
    public void setState(String scope, String scopeValue, String state)
    {
        setProperty(STATE_PROPERTY_NAME, scope, scopeValue, state);
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

    public long getRefreshRate() {
        return refreshRate;
    }

    public void setRefreshRate(long rate) {
        this.refreshRate = rate;
    }

    public String getRefreshFunction() {
        return this.refreshFunction;
    }

    public void setRefreshFunction(String function) {
        this.refreshFunction = function;
    }

    @Override
    public void reorderColumns(int max) {
        if (pageLayoutComponent != null)
        {
            // delegate to page layout component
            pageLayoutComponent.reorderColumns(this, max);
        }
    }
}
