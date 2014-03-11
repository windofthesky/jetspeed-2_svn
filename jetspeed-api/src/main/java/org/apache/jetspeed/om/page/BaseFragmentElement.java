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

package org.apache.jetspeed.om.page;

import org.apache.jetspeed.om.preference.FragmentPreference;

import java.util.List;

/**
 * <p>A <code>BaseFragmentElement</code> is the basic element handled by the
 * aggregation engine to compose the final portal page. It represents a reserved
 * screen area whose layout is managed by a specified component. The fragment
 * also holds layout and rendering properties/preferences.</p>
 *
 * @version $Id:$
 */
public interface BaseFragmentElement extends BaseElement
{
    /**
     * skin standard layout property name
     */
    String SKIN_PROPERTY_NAME = "skin";

    /**
     * decorator standard layout property name
     */
    String DECORATOR_PROPERTY_NAME = "decorator";

    /**
     * state standard layout property name
     */
    String STATE_PROPERTY_NAME = "state";

    /**
     * mode standard layout property name
     */
    String MODE_PROPERTY_NAME = "mode";

    /**
     * row standard layout property name
     */
    String ROW_PROPERTY_NAME = "row";

    /**
     * column standard layout property name
     */
    String COLUMN_PROPERTY_NAME = "column";

    /**
     * sizes standard layout property name
     */
    String SIZES_PROPERTY_NAME = "sizes";

    /**
     * x coordinate standard layout property name
     */
    String X_PROPERTY_NAME = "x";

    /**
     * y coordinate standard layout property name
     */
    String Y_PROPERTY_NAME = "y";

    /**
     * z coordinate standard layout property name
     */
    String Z_PROPERTY_NAME = "z";

    /**
     * width standard layout property name
     */
    String WIDTH_PROPERTY_NAME = "width";

    /**
     * height standard layout property name
     */
    String HEIGHT_PROPERTY_NAME = "height";

    /**
     * user standard property scope
     */
    String USER_PROPERTY_SCOPE = FragmentProperty.USER_PROPERTY_SCOPE;

    /**
     * group standard property scope
     */
    String GROUP_PROPERTY_SCOPE = FragmentProperty.GROUP_PROPERTY_SCOPE;

    /**
     * role standard property scope
     */
    String ROLE_PROPERTY_SCOPE = FragmentProperty.ROLE_PROPERTY_SCOPE;

    /**
     * global standard property scope
     */
    String GLOBAL_PROPERTY_SCOPE = FragmentProperty.GLOBAL_PROPERTY_SCOPE;

    /**
     * group and role standard property scopes enabled flag
     */
    boolean GROUP_AND_ROLE_PROPERTY_SCOPES_ENABLED = FragmentProperty.GROUP_AND_ROLE_PROPERTY_SCOPES_ENABLED;

    /**
     * Sets the unique Id of this fragment. This id must be unique from the
     * complete portal and must be suitable as a unique key.
     *
     * @param fragmentId the unique id of this fragment.
     */
    void setId(String fragmentId);

    /**
     * Returns the name of the skin associated to this fragment
     * Property value is returned for the most specific scope
     * found, (i.e. user, group, role, or global scopes).
     */
    String getSkin();

    /**
     * Defines the skin for this fragment. This skin should be
     * known by the portal. The default, (GLOBAL), scope value is set
     * or removed.
     *
     * @param skinName the name of the new skin applied to this fragment
     */
    void setSkin(String skinName);

    /**
     * Defines the skin for this fragment. This skin should be
     * known by the portal. Setting the property value to null removes
     * the scoped property.
     *
     * @param scope the name of the property scope for setting
     * @param scopeValue the scope discriminator value, (unless scope is GLOBAL
     *                   or USER where the default user name is used if null)
     * @param skinName the name of the new skin applied to this fragment
     */
    void setSkin(String scope, String scopeValue, String skinName);

    /**
     * Returns the name of the decorator bound to this fragment
     * Property value is returned for the most specific scope
     * found, (i.e. user, group, role, or global scopes).
     */
    String getDecorator();

    /**
     * Defines the decorator for this fragment. This decorator should be
     * known by the portal. The default, (GLOBAL), scope value is set
     * or removed.
     *
     * @param decoratorName the name of the decorator applied to this fragment
     */
    void setDecorator(String decoratorName);

    /**
     * Defines the decorator for this fragment. This decorator should be
     * known by the portal. Setting the property value to null removes
     * the scoped property.
     *
     * @param scope the name of the property scope for setting 
     * @param scopeValue the scope discriminator value, (unless scope is GLOBAL
     *                   or USER where the default user name is used if null)
     * @param decoratorName the name of the decorator applied to this fragment
     */
    void setDecorator(String scope, String scopeValue, String decoratorName);

    /**
     * Returns the display state of this fragment. The state may have the
     * following values: "Normal","Minimized","Maximized","Hidden"
     * Property value is returned for the most specific scope
     * found, (i.e. user, group, role, or global scopes).
     */
    String getState();

    /**
     * Sets the display state of this fragment.
     * Valid states are: "Normal","Minimized","Maximized","Hidden"
     * The default, (GLOBAL), scope value is set or removed.
     *  
     * @param state the new fragment state
     */
    void setState(String state);

    /**
     * Sets the display state of this fragment.
     * Valid states are: "Normal","Minimized","Maximized","Hidden".
     * Setting the property value to null removes the scoped property.
     *
     * @param scope the name of the property scope for setting 
     * @param scopeValue the scope discriminator value, (unless scope is GLOBAL
     *                   or USER where the default user name is used if null)
     * @param state the new fragment state
     */
    void setState(String scope, String scopeValue, String state);

    /**
     * Returns the display mode of this fragment. The mode may have the
     * following values: "View","Edit","Help","Config","Print","Custom"
     * Property value is returned for the most specific scope
     * found, (i.e. user, group, role, or global scopes).
     */
    String getMode();

    /**
     * Sets the display mode of this fragment.
     * Valid modes are: "View","Edit","Help","Config","Print","Custom"
     * The default, (GLOBAL), scope value is set or removed.
     * 
     * @param mode the new fragment mode
     */
    void setMode(String mode);

    /**
     * Sets the display mode of this fragment.
     * Valid modes are: "View","Edit","Help","Config","Print","Custom".
     * Setting the property value to null removes the scoped property.
     *
     * @param scope the name of the property scope for setting 
     * @param scopeValue the scope discriminator value, (unless scope is GLOBAL
     *                   or USER where the default user name is used if null)
     * @param mode the new fragment mode
     */
    void setMode(String scope, String scopeValue, String mode);

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
     * @param propScope the name of the property scope to retrieve 
     * @param propScopeValue the scope discriminator value, (unless scope is GLOBAL
     *                       or USER where the default user name is used if null)
     * @return value
     */
    String getProperty(String propName, String propScope, String propScopeValue);
    
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
     * @param propScope the name of the property scope to retrieve 
     * @param propScopeValue the scope discriminator value, (unless scope is GLOBAL
     *                       or USER where the default user name is used if null)
     * @return int value
     */
    int getIntProperty(String propName, String propScope, String propScopeValue);
    
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
     * @param propScope the name of the property scope to retrieve 
     * @param propScopeValue the scope discriminator value, (unless scope is GLOBAL
     *                       or USER where the default user name is used if null)
     * @return float value
     */
    float getFloatProperty(String propName, String propScope, String propScopeValue);
    
    /**
     * Set named property value.
     *
     * @param propName property name
     * @param propScope the name of the property scope to retrieve 
     * @param propScopeValue the scope discriminator value, (unless scope is GLOBAL
     *                       or USER where the default user name is used if null)
     * @param propValue property value
     */
    void setProperty(String propName, String propScope, String propScopeValue, String propValue);
    
    /**
     * Set named int property value.
     *
     * @param propName property name
     * @param propScope the name of the property scope to retrieve 
     * @param propScopeValue the scope discriminator value, (unless scope is GLOBAL
     *                       or USER where the default user name is used if null)
     * @param propValue property value
     */
    void setProperty(String propName, String propScope, String propScopeValue, int propValue);
    
    /**
     * Set named float property value.
     *
     * @param propName property name
     * @param propScope the name of the property scope to retrieve 
     * @param propScopeValue the scope discriminator value, (unless scope is GLOBAL
     *                       or USER where the default user name is used if null)
     * @param propValue property value
     */
    void setProperty(String propName, String propScope, String propScopeValue, float propValue);
    
    /**
     * Get writable list of fragment property objects that
     * initially returns the set of properties for all scopes
     * found for the current user.
     *
     * @return list of FragmentProperty instances
     */
    List<FragmentProperty> getProperties();

    /**
     * Set collection of fragment property objects
     * 
     * @param properties list of FragmentPreference objects
     */
    void setProperties(List<FragmentProperty> properties);

    /**
     * Get layout row property.
     * Property value is returned for the most specific scope
     * found, (i.e. user, group, role, or global scopes).
     *
     * @return row layout property
     **/
    int getLayoutRow();

    /**
     * Set the layout row property.
     * The default, (GLOBAL), scope value is set or removed.
     * 
     * @param row
     */
    void setLayoutRow(int row);

    /**
     * Set the layout row property. Setting the property value to
     * a negative value removes the scoped property.
     *
     * @param scope the name of the property scope for setting 
     * @param scopeValue the scope discriminator value, (unless scope is GLOBAL
     *                   or USER where the default user name is used if null)
     * @param row
     */
    void setLayoutRow(String scope, String scopeValue, int row);

    /**
     * Get layout column property.
     * Property value is returned for the most specific scope
     * found, (i.e. user, group, role, or global scopes).
     *
     * @return column layout property
     **/
    int getLayoutColumn();

    /**
     * Set the layout column property.
     * The default, (GLOBAL), scope value is set or removed.
     * 
     * @param column
     */
    void setLayoutColumn(int column);
    
    /**
     * Set the layout column property. Setting the property value to
     * a negative value removes the scoped property.
     * 
     * @param scope the name of the property scope for setting 
     * @param scopeValue the scope discriminator value, (unless scope is GLOBAL
     *                   or USER where the default user name is used if null)
     * @param column
     */
    void setLayoutColumn(String scope, String scopeValue, int column);
    
    /**
     * Get layout sizes property, (i.e. "25%,75%").
     * Property value is returned for the most specific scope
     * found, (i.e. user, group, role, or global scopes).
     * 
     * @return sizes layout property
     **/
    String getLayoutSizes();
    
    /**
     * Set the layout sizes.
     * The default, (GLOBAL), scope value is set or removed.
     * 
     * @param sizes
     */
    void setLayoutSizes(String sizes);
    
    /**
     * Set the layout sizes. Setting the property value to null removes
     * the scoped property.
     * 
     * @param scope the name of the property scope for setting 
     * @param scopeValue the scope discriminator value, (unless scope is GLOBAL
     *                   or USER where the default user name is used if null)
     * @param sizes
     */
    void setLayoutSizes(String scope, String scopeValue, String sizes);
    
    /**
     * Get layout x coordinate property.
     * Property value is returned for the most specific scope
     * found, (i.e. user, group, role, or global scopes).
     *
     * @return the x coordinate value
     **/
    float getLayoutX();

    /**
     * Set the layout x coordinate property.
     * The default, (GLOBAL), scope value is set or removed.
     * 
     * @param x the coordinate value
     */
    void setLayoutX(float x);

    /**
     * Set the layout x coordinate property. Setting the property
     * value to a negative value removes the scoped property.
     *
     * @param scope the name of the property scope for setting 
     * @param scopeValue the scope discriminator value, (unless scope is GLOBAL
     *                   or USER where the default user name is used if null)
     * @param x the coordinate value
     */
    void setLayoutX(String scope, String scopeValue, float x);

    /**
     * Get layout y coordinate property.
     * Property value is returned for the most specific scope
     * found, (i.e. user, group, role, or global scopes).
     *
     * @return the y coordinate value
     **/
    float getLayoutY();

    /**
     * Set the layout y coordinate property.
     * The default, (GLOBAL), scope value is set or removed.
     * 
     * @param y the coordinate value
     */
    void setLayoutY(float y);

    /**
     * Set the layout y coordinate property. Setting the property
     * value to a negative value removes the scoped property.
     *
     * @param scope the name of the property scope for setting 
     * @param scopeValue the scope discriminator value, (unless scope is GLOBAL
     *                   or USER where the default user name is used if null)
     * @param y the coordinate value
     */
    void setLayoutY(String scope, String scopeValue, float y);

    /**
     * Get layout z coordinate property.
     * Property value is returned for the most specific scope
     * found, (i.e. user, group, role, or global scopes).
     *
     * @return the z coordinate value
     **/
    float getLayoutZ();

    /**
     * Set the layout z coordinate property.
     * The default, (GLOBAL), scope value is set or removed.
     * 
     * @param z the coordinate value
     */
    void setLayoutZ(float z);

    /**
     * Set the layout z coordinate property. Setting the property
     * value to a negative value removes the scoped property.
     *
     * @param scope the name of the property scope for setting 
     * @param scopeValue the scope discriminator value, (unless scope is GLOBAL
     *                   or USER where the default user name is used if null)
     * @param z the coordinate value
     */
    void setLayoutZ(String scope, String scopeValue, float z);

    /**
     * Get layout width property.
     * Property value is returned for the most specific scope
     * found, (i.e. user, group, role, or global scopes).
     *
     * @return the width value
     **/
    float getLayoutWidth();

    /**
     * Set the layout width property.
     * The default, (GLOBAL), scope value is set or removed.
     * 
     * @param width the value
     */
    void setLayoutWidth(float width);

    /**
     * Set the layout width property. Setting the property value
     * to a negative value removes the scoped property.
     *
     * @param scope the name of the property scope for setting 
     * @param scopeValue the scope discriminator value, (unless scope is GLOBAL
     *                   or USER where the default user name is used if null)
     * @param width the value
     */
    void setLayoutWidth(String scope, String scopeValue, float width);

    /**
     * Get layout height property.
     * Property value is returned for the most specific scope
     * found, (i.e. user, group, role, or global scopes).
     *
     * @return the height value
     **/
    float getLayoutHeight();

    /**
     * Set the layout height property.
     * The default, (GLOBAL), scope value is set or removed.
     * 
     * @param height the value
     */
    void setLayoutHeight(float height);

    /**
     * Set the layout height property. Setting the property value
     * to a negative value removes the scoped property.
     *
     * @param scope the name of the property scope for setting 
     * @param scopeValue the scope discriminator value, (unless scope is GLOBAL
     *                   or USER where the default user name is used if null)
     * @param height the value
     */
    void setLayoutHeight(String scope, String scopeValue, float height);

    /**
     * Get collection of fragment preference objects used
     * to initialize user preferences
     * 
     * @return list of FragmentPreference objects
     */
    List<FragmentPreference> getPreferences();

    /**
     * Set collection of fragment preference objects
     * 
     * @param preferences list of FragmentPreference objects
     */
    void setPreferences(List<FragmentPreference> preferences);
}
