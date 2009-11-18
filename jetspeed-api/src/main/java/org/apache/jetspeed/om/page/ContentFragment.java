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
package org.apache.jetspeed.om.page;

import java.util.List;
import java.util.Map;

import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.decoration.Decoration;
import org.apache.jetspeed.layout.PageLayoutComponent;

/**
 * ContentFragment is a read-only version of the {@link org.apache.jetspeed.om.page.Fragment}
 * object for use in rendering. <code>Fragment</code> objects are persistent, single-instance 
 * metadata objects that should not be used to hold per-request content. ContentFragment
 * solves this by providing a parallel interface that can be used for rendering
 * requested content associated with the current user-request. 
 * 
 * @author weaver@apache.org
 *
 */
public interface ContentFragment
{
    /************** SecuredResource **************/

    /**
     * Check security access to fragment.
     *
     * @param actions list to be checked against in CSV string form
     * @throws SecurityException
     */
    void checkAccess(String actions) throws SecurityException;
    
    /************** BaseElement **************/

    /**
     * Returns the unique Id of this element. This id is guaranteed to be unique
     * from the complete portal and is suitable to be used as a unique key.
     *
     * @return the unique id of this element.
     */
    String getId();

    /**
     * Returns the title in the default Locale
     *
     * @return the page title
     */
    String getTitle();

    /**
     * Returns the short title in the default Locale
     *
     * @return the page short title
     */
    String getShortTitle();

    /************** BaseFragmentElement **************/

    /**
     * row standard layout property name
     */
    String ROW_PROPERTY_NAME = BaseFragmentElement.ROW_PROPERTY_NAME;

    /**
     * column standard layout property name
     */
    String COLUMN_PROPERTY_NAME = BaseFragmentElement.COLUMN_PROPERTY_NAME;

    /**
     * sizes standard layout property name
     */
    String SIZES_PROPERTY_NAME = BaseFragmentElement.SIZES_PROPERTY_NAME;

    /**
     * x coordinate standard layout property name
     */
    String X_PROPERTY_NAME = BaseFragmentElement.X_PROPERTY_NAME;

    /**
     * y coordinate standard layout property name
     */
    String Y_PROPERTY_NAME = BaseFragmentElement.Y_PROPERTY_NAME;

    /**
     * z coordinate standard layout property name
     */
    String Z_PROPERTY_NAME = BaseFragmentElement.Z_PROPERTY_NAME;

    /**
     * width standard layout property name
     */
    String WIDTH_PROPERTY_NAME = BaseFragmentElement.WIDTH_PROPERTY_NAME;

    /**
     * height standard layout property name
     */
    String HEIGHT_PROPERTY_NAME = BaseFragmentElement.HEIGHT_PROPERTY_NAME;

    /**
     * Returns the name of the skin associated to this fragment
     */
    String getSkin();

    /**
     * Returns the name of the decorator bound to this fragment
     */
    String getDecorator();

    /**
     * Returns the display state of this fragment. The state may have the
     * following values: "Normal","Minimized","Maximized","Hidden"
     */
    String getState();

    /**
     * Returns the display mode of this fragment. The mode may have the
     * following values: "View","Edit","Help","Config","Print","Custom"
     */
    String getMode();

    /**
     * getProperty
     *
     * Get named property value.
     *
     * @param propName property name
     * @return value
     */
    String getProperty(String propName);
    
    /**
     * getIntProperty
     * 
     * Get named property value as integer.
     *
     * @param propName property name
     * @return int value
     */
    int getIntProperty(String propName);
    
    /**
     * getFloatProperty
     * 
     * Get named property value as float.
     *
     * @param propName property name
     * @return float value
     */
    float getFloatProperty(String propName);
    
    /**
     * getProperties
     * 
     * Get writable Map of properties by name.
     *
     * @return properties map
     */
    Map getProperties();

    /**
     * get layout row property
     *
     * @return row layout property
     **/
    int getLayoutRow();

    /**
     * get layout column property
     *
     * @return column layout property
     **/
    int getLayoutColumn();

    /**
     * get layout sizes property, (i.e. "25%,75%")
     * 
     * @return sizes layout property
     **/
    String getLayoutSizes();
    
    /**
     * get layout x coordinate property
     *
     * @return the x coordinate value
     **/
    float getLayoutX();

    /**
     * get layout y coordinate property
     *
     * @return the y coordinate value
     **/
    float getLayoutY();

    /**
     * get layout z coordinate property
     *
     * @return the z coordinate value
     **/
    float getLayoutZ();

    /**
     * get layout width property
     *
     * @return the width value
     **/
    float getLayoutWidth();

    /**
     * get layout height property
     *
     * @return the height value
     **/
    float getLayoutHeight();

    /**
     * Get collection of fragment preference objects used
     * to initialize user preferences
     * 
     * @return list of FragmentPreference objects
     */
    List getPreferences();    
    
    /************** Fragment **************/
    
    /**
     * A fragment of type PORTLET is considered to be a compliant portlet
     * in the sense of the JSR 168.
     */
    String PORTLET = Fragment.PORTLET;

    /**
     * A fragment of type LAYOUT is a specific JSR 168 compliant portlet
     * that knows how to layout a Page and depends on the Jetspeed
     * layout service.
     */
    String LAYOUT = Fragment.LAYOUT;
    
    /**
     * Returns the administrative name of this fragment. This name should map
     * to a component name in the component repository defined by the type
     * attribute.
     * If the name is not mapped to any component, the fragment is discarded
     * from the rendering process, as well as any inner fragment.
     *
     * @return the administrative name
     */
    String getName();

    /**
     * Returns the type of the class bound to this fragment
     */
    String getType();

    /**
     * Provides a list of of child ContentFragment objects.
     * 
     * @return ContentFragment list
     */
    List getFragments();
    
    /************** ContentFragment **************/

    /**
     * 
     * <p>
     * getRenderedContent
     * </p>
     * <p>
     *   Returns the raw,undecorated content of this fragment.  If
     *   overridenContent has been set and portlet content has not,
     *   overridden content should be returned.
     * </p>
     *  
     * @return The raw,undecorated content of this fragment.
     * @throws java.lang.IllegalStateException if the content has not yet been set.
     */
    String getRenderedContent() throws IllegalStateException;

    /**
     * 
     * <p>
     * overrideRenderedContent
     * </p>
     * <p>
     * Can be used to store errors that may have occurred during the
     * rendering process.
     * </p>
     *
     * @param contnent
     */
    void overrideRenderedContent(String contnent);

    /**
     * @return the overridden content set by overrideRenderedContent
     */
    String getOverriddenContent();
    
    /**
     * 
     * <p>
     * setPortletContent
     * </p>
     *
     * @param portletContent
     */
    void setPortletContent(PortletContent portletContent);

    /**
     * Retrieve the content for this fragment
     * 
     * @return PortletContent
     */
    PortletContent getPortletContent();
    
    /**
     * Retrieves the actual <code>org.apache.jetspeed.decoration.decorator</code>
     * object for this content fragment.
     * 
     * TODO: Re-evaluate the naming as this is somewhat confusing
     * due to the existence of Fragment.getDecorator()
     * @return
     */
    Decoration getDecoration();
    
    /**
     * 
     * @param decoration
     */
    void setDecoration(Decoration decoration);
    
    /**
     * Checks if the content is instantly rendered from JPT.
     */
    boolean isInstantlyRendered();

    /**
     * Returns the PageLayoutComponent that generated this ContentPage
     * 
     * @return PageLayoutComponent instance.
     */
    PageLayoutComponent getPageLayoutComponent();
    
    /************** PageLayoutComponent Operations **************/

    /**
     * Add portlet to fragment with specified row and column returning
     * associated content fragment.
     * 
     * @param type portlet type
     * @param name portlet name
     * @param row fragment row position
     * @param column fragment column position
     * @return new content fragment added to fragment
     */
    ContentFragment addPortlet(String type, String name, int row, int column);
    
    /**
     * Add portlet to fragment returning associated content fragment.
     * 
     * @param type portlet type
     * @param name portlet name
     * @return new content fragment added to fragment
     */
    ContentFragment addPortlet(String type, String name);
    
    /**
     * Update fragment portlet decorator.
     *  
     * @param decoratorName portlet decorator name
     */
    void updateDecorator(String decoratorName);
    
    /**
     * Update fragment name.
     * 
     * @param name fragment name
     */
    void updateName(String name);

    /**
     * Update fragment layout position.
     * 
     * @param x fragment X coordinate or -1
     * @param y fragment Y coordinate or -1
     * @param z fragment Z level or -1
     * @param width fragment portlet width or -1
     * @param height fragment portlet height or -1
     */
    void updatePosition(float x, float y, float z, float width, float height);
    
    /**
     * Update preferences with new preferences set, accepting
     * Map of strings, string arrays, FragmentPreference or
     * PortletPreference. Existing preferences are removed and
     * replaced with the specified preferences.
     * 
     * @param preferences map of new preferences set.
     */
    void updatePreferences(Map preferences);

    /**
     * Update fragment row and column layout position.
     * 
     * @param row fragment row position
     * @param column fragment column position
     */
    void updateRowColumn(int row, int column);
    
    /**
     * Update fragment portlet state and/or mode.
     * 
     * @param portletState fragment portlet state or null
     * @param portletMode fragment portlet mode or null
     */
    void updateStateMode(String portletState, String portletMode);
}
