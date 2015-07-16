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

import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.decoration.Decoration;
import org.apache.jetspeed.layout.PageLayoutComponent;
import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.preference.FragmentPreference;

import java.util.List;
import java.util.Map;

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
     * Get security constraints.
     *
     * @return security constraints for resource
     */
    SecurityConstraints getSecurityConstraints();
    
    /**
     * Create new security constraints.
     *
     * @return a newly created SecurityConstraints object
     */
    SecurityConstraints newSecurityConstraints();

    /**
     * Create new security constraint.
     *
     * @return a newly created SecurityConstraint object
     */
    SecurityConstraint newSecurityConstraint();

    /**
     * Check security access to fragment.
     *
     * @param actions list to be checked against in CSV string form
     * @throws SecurityException
     */
    void checkAccess(String actions) throws SecurityException;
    
    /************** BaseElement **************/

    /**
     * Returns the unique fully qualified id of this element. This id is guaranteed
     * to be unique within the portal and is suitable to be used as a key.
     *
     * @return the unique fully qualified id of this element.
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
     * skin standard layout property name
     */
    String SKIN_PROPERTY_NAME = BaseFragmentElement.SKIN_PROPERTY_NAME;

    /**
     * decorator standard layout property name
     */
    String DECORATOR_PROPERTY_NAME = BaseFragmentElement.DECORATOR_PROPERTY_NAME;

    /**
     * state standard layout property name
     */
    String STATE_PROPERTY_NAME = BaseFragmentElement.STATE_PROPERTY_NAME;

    /**
     * mode standard layout property name
     */
    String MODE_PROPERTY_NAME = BaseFragmentElement.MODE_PROPERTY_NAME;

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
     * user standard property scope
     */
    String USER_PROPERTY_SCOPE = BaseFragmentElement.USER_PROPERTY_SCOPE;

    /**
     * group standard property scope
     */
    String GROUP_PROPERTY_SCOPE = BaseFragmentElement.GROUP_PROPERTY_SCOPE;

    /**
     * role standard property scope
     */
    String ROLE_PROPERTY_SCOPE = BaseFragmentElement.ROLE_PROPERTY_SCOPE;

    /**
     * global standard property scope
     */
    String GLOBAL_PROPERTY_SCOPE = BaseFragmentElement.GLOBAL_PROPERTY_SCOPE;

    /**
     * group and role standard property scopes enabled flag
     */
    boolean GROUP_AND_ROLE_PROPERTY_SCOPES_ENABLED = BaseFragmentElement.GROUP_AND_ROLE_PROPERTY_SCOPES_ENABLED;

    /**
     * Returns the name of the skin associated to this fragment
     * Property value is returned for the most specific scope
     * found, (i.e. user, group, role, or global scopes).
     */
    String getSkin();

    /**
     * Returns the name of the decorator bound to this fragment
     * Property value is returned for the most specific scope
     * found, (i.e. user, group, role, or global scopes).
     */
    String getDecorator();

    /**
     * Returns the display state of this fragment. The state may have the
     * following values: "Normal","Minimized","Maximized","Hidden"
     * Property value is returned for the most specific scope
     * found, (i.e. user, group, role, or global scopes).
     */
    String getState();

    /**
     * Returns the display mode of this fragment. The mode may have the
     * following values: "View","Edit","Help","Config","Print","Custom"
     * Property value is returned for the most specific scope
     * found, (i.e. user, group, role, or global scopes).
     */
    String getMode();

    /**
     * Get named property value.
     * Property value is returned for the most specific scope
     * found, (i.e. user, group, role, or global scopes).
     *
     * @param propName property name
     * @return value
     */
    String getProperty(String propName);
    
    /**
     * Get named property value.
     *
     * @param propName property name
     * @param scope the name of the property scope to retrieve 
     * @param scopeValue the scope discriminator value, (unless scope is GLOBAL
     *                   or USER where the default user name is used if null)
     * @return value
     */
    String getProperty(String propName, String scope, String scopeValue);
    
    /**
     * Get named property value as integer.
     * Property value is returned for the most specific scope
     * found, (i.e. user, group, role, or global scopes).
     *
     * @param propName property name
     * @return int value
     */
    int getIntProperty(String propName);
    
    /**
     * Get named property value as integer.
     *
     * @param propName property name
     * @param scope the name of the property scope to retrieve 
     * @param scopeValue the scope discriminator value, (unless scope is GLOBAL
     *                   or USER where the default user name is used if null)
     * @return int value
     */
    int getIntProperty(String propName, String scope, String scopeValue);
    
    /**
     * Get named property value as float.
     * Property value is returned for the most specific scope
     * found, (i.e. user, group, role, or global scopes).
     *
     * @param propName property name
     * @return float value
     */
    float getFloatProperty(String propName);
    
    /**
     * Get named property value as float.
     *
     * @param propName property name
     * @param scope the name of the property scope to retrieve 
     * @param scopeValue the scope discriminator value, (unless scope is GLOBAL
     *                   or USER where the default user name is used if null)
     * @return float value
     */
    float getFloatProperty(String propName, String scope, String scopeValue);
    
    /**
     * Get read-only list of fragment property objects that
     * initially returns the set of properties for all scopes.
     *
     * @return list of FragmentProperty instances
     */
    List<FragmentProperty> getProperties();

    /**
     * Get named property value map. Property values are returned
     * for the most specific scope found, (i.e. user, group, role,
     * or global scopes).
     *
     * @return map of fragment property values
     */
    Map<String,String> getPropertiesMap();

    /**
     * Get layout row property.
     * Property value is returned for the most specific scope
     * found, (i.e. user, group, role, or global scopes).
     *
     * @return row layout property
     **/
    int getLayoutRow();

    /**
     * Get layout column property.
     * Property value is returned for the most specific scope
     * found, (i.e. user, group, role, or global scopes).
     *
     * @return column layout property
     **/
    int getLayoutColumn();

    /**
     * Get layout sizes property, (i.e. "25%,75%").
     * Property value is returned for the most specific scope
     * found, (i.e. user, group, role, or global scopes).
     * 
     * @return sizes layout property
     **/
    String getLayoutSizes();
    
    /**
     * Get layout x coordinate property.
     * Property value is returned for the most specific scope
     * found, (i.e. user, group, role, or global scopes).
     *
     * @return the x coordinate value
     **/
    float getLayoutX();

    /**
     * Get layout y coordinate property.
     * Property value is returned for the most specific scope
     * found, (i.e. user, group, role, or global scopes).
     *
     * @return the y coordinate value
     **/
    float getLayoutY();

    /**
     * Get layout z coordinate property.
     * Property value is returned for the most specific scope
     * found, (i.e. user, group, role, or global scopes).
     *
     * @return the z coordinate value
     **/
    float getLayoutZ();

    /**
     * Get layout width property.
     * Property value is returned for the most specific scope
     * found, (i.e. user, group, role, or global scopes).
     *
     * @return the width value
     **/
    float getLayoutWidth();

    /**
     * Get layout height property.
     * Property value is returned for the most specific scope
     * found, (i.e. user, group, role, or global scopes).
     *
     * @return the height value
     **/
    float getLayoutHeight();

    /**
     * Get read-only collection of fragment preference objects
     * used to initialize user preferences
     * 
     * @return list of FragmentPreference objects
     */
    List<FragmentPreference> getPreferences();
    
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
    List<ContentFragment> getFragments();

    /************** Fragment Reference **************/
    
    /**
     * Returns the id of the referenced fragment element.
     *
     * @return the referenced fragment id.
     */
    String getRefId();

    /************** ContentFragment **************/

    /**
     * A fragment of type PAGE is a place holder for a page fragment.
     */
    String PAGE = "page";

    /**
     * A fragment of type REFERENCE is a place holder for a fragment reference.
     */
    String REFERENCE = "reference";
    
    /**
     * Returns the unique fragment id of this element. This id is guaranteed to be
     * unique per fragment within the portal and is suitable to be used as a key.
     * Note that multiple content fragments can have the same fragment id since
     * a single fragment may be rendered multiple times within a page.
     *
     * @return the unique fragment id of this element.
     */
    String getFragmentId();

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
     * @param content
     */
    void overrideRenderedContent(String content);

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
    
    /**
     * Return template flag indicating whether this fragment
     * was originally merged from a page template
     * 
     * @return template flag
     */
    boolean isTemplate();
    
    /**
     * Return is locked flag indicating whether this fragment
     * was originally merged from a page template or fragment
     * definition or is transient.
     * 
     * @return locked flag
     */
    boolean isLocked();
    
    /************** PageLayoutComponent Operations **************/

    /**
     * Add portlet to fragment with specified row and column returning
     * associated content fragment. The default, (Global), scope row
     * and column values are set.
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
     * Update fragment portlet decorator. The default, (Global),
     * scope value is updated.
     *  
     * @param decoratorName portlet decorator name
     */
    void updateDecorator(String decoratorName);
    
    /**
     * Update fragment portlet decorator.
     *  
     * @param decoratorName portlet decorator name
     * @param scope the name of the property scope to update
     * @param scopeValue the scope discriminator value, (unless scope is GLOBAL
     *                   or USER where the default user name is used if null)
     */
    void updateDecorator(String decoratorName, String scope, String scopeValue);
    
    /**
     * Update fragment name.
     * 
     * @param name fragment name
     */
    void updateName(String name);

    /**
     * Update fragment layout position. The default, (Global),
     * scope values are updated.
     * 
     * @param x fragment X coordinate or -1
     * @param y fragment Y coordinate or -1
     * @param z fragment Z level or -1
     * @param width fragment portlet width or -1
     * @param height fragment portlet height or -1
     */
    void updatePosition(float x, float y, float z, float width, float height);

    /**
     * Update fragment layout position.
     * 
     * @param x fragment X coordinate or -1
     * @param y fragment Y coordinate or -1
     * @param z fragment Z level or -1
     * @param width fragment portlet width or -1
     * @param height fragment portlet height or -1
     * @param scope the name of the property scope to update 
     * @param scopeValue the scope discriminator value, (unless scope is GLOBAL
     *                   or USER where the default user name is used if null)
     */
    void updatePosition(float x, float y, float z, float width, float height, String scope, String scopeValue);
    
    /**
     * Update preferences with new preferences set, accepting
     * Map of strings, string arrays, FragmentPreference or
     * PortletPreference. Existing preferences are removed and
     * replaced with the specified preferences.
     * 
     * @param preferences map of new preferences set.
     */
    void updatePreferences(Map<String,?> preferences);

    /**
     * Update fragment property. The default, (Global), scope value
     * is updated.
     * 
     * @param propName fragment property name
     * @param propValue fragment property value
     */
    void updateProperty(String propName, String propValue);
    
    /**
     * Update fragment property.
     * 
     * @param propName fragment property name
     * @param propValue fragment property value
     * @param scope the name of the property scope to update 
     * @param scopeValue the scope discriminator value, (unless scope is GLOBAL
     *                   or USER where the default user name is used if null)
     */
    void updateProperty(String propName, String propValue, String scope, String scopeValue);
    
    /**
     * Update fragment reference reference id.
     * 
     * @param refId referenced fragment definition id
     */
    void updateRefId(String refId);

    /**
     * Update fragment row and column layout positions. The default, (Global),
     * scope values are updated.
     * 
     * @param row fragment row position
     * @param column fragment column position
     */
    void updateRowColumn(int row, int column);
    
    /**
     * Update fragment row and column layout positions.
     * 
     * @param row fragment row position
     * @param column fragment column position
     * @param scope the name of the property scope to update 
     * @param scopeValue the scope discriminator value, (unless scope is GLOBAL
     *                   or USER where the default user name is used if null)
     */
    void updateRowColumn(int row, int column, String scope, String scopeValue);
    
    /**
     * Update fragment security constraints.
     *
     * @param constraints security constraints for resource
     */
    void updateSecurityConstraints(SecurityConstraints constraints);
    
    /**
     * Update fragment portlet state and/or mode. The default, (Global),
     * scope values are updated.
     * 
     * @param portletState fragment portlet state or null
     * @param portletMode fragment portlet mode or null
     */
    void updateStateMode(String portletState, String portletMode);

    /**
     * Update fragment portlet state and/or mode.
     * 
     * @param portletState fragment portlet state or null
     * @param portletMode fragment portlet mode or null
     * @param scope the name of the property scope to update 
     * @param scopeValue the scope discriminator value, (unless scope is GLOBAL
     *                   or USER where the default user name is used if null)
     */
    void updateStateMode(String portletState, String portletMode, String scope, String scopeValue);

    long getRefreshRate();
    void setRefreshRate(long rate);
    String getRefreshFunction();
    void setRefreshFunction(String function);

    void reorderColumns(int max);

}
