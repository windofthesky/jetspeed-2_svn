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

import java.util.List;
import java.util.Map;

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
     * Sets the unique Id of this fragment. This id must be unique from the
     * complete portal and must be suitable as a unique key.
     *
     * @param fragmentId the unique id of this fragment.
     */
    void setId(String fragmentId);

    /**
     * Returns the name of the skin associated to this fragment
     */
    String getSkin();

    /**
     * Defines the skin for this fragment. This skin should be
     * known by the portal.
     *
     * @param skinName the name of the new skin applied to this fragment
     */
    void setSkin(String skinName);

    /**
     * Returns the name of the decorator bound to this fragment
     */
    String getDecorator();

    /**
     * Defines the decorator for this fragment. This decorator should be
     * known by the portal.
     *
     * @param decoratorName the name of the decorator applied to this fragment
     */
    void setDecorator(String decoratorName);

    /**
     * Returns the display state of this fragment. The state may have the
     * following values: "Normal","Minimized","Maximized","Hidden"
     */
    String getState();

    /**
     * Sets the display state of this fragment.
     * Valid states are: "Normal","Minimized","Maximized","Hidden"
     *
     * @param state the new fragment state
     */
    void setState(String state);

    /**
     * Returns the display mode of this fragment. The mode may have the
     * following values: "View","Edit","Help","Config","Print","Custom"
     */
    String getMode();

    /**
     * Sets the display mode of this fragment.
     * Valid modes are: "View","Edit","Help","Config","Print","Custom"
     *
     * @param mode the new fragment mode
     */
    void setMode(String mode);

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
     * set the layout row property
     *
     * @param row
     */
    void setLayoutRow(int row);

    /**
     * get layout column property
     *
     * @return column layout property
     **/
    int getLayoutColumn();

    /**
     * set the layout column property
     * 
     * @param column
     */
    void setLayoutColumn(int column);
    
    /**
     * get layout sizes property, (i.e. "25%,75%")
     * 
     * @return sizes layout property
     **/
    String getLayoutSizes();
    
    /**
     * set the layout sizes
     * 
     * @param sizes
     */
    void setLayoutSizes(String sizes);
    
    /**
     * get layout x coordinate property
     *
     * @return the x coordinate value
     **/
    float getLayoutX();

    /**
     * set the layout x coordinate property
     *
     * @param x the coordinate value
     */
    void setLayoutX(float x);

    /**
     * get layout y coordinate property
     *
     * @return the y coordinate value
     **/
    float getLayoutY();

    /**
     * set the layout y coordinate property
     *
     * @param y the coordinate value
     */
    void setLayoutY(float y);

    /**
     * get layout z coordinate property
     *
     * @return the z coordinate value
     **/
    float getLayoutZ();

    /**
     * set the layout z coordinate property
     *
     * @param z the coordinate value
     */
    void setLayoutZ(float z);

    /**
     * get layout width property
     *
     * @return the width value
     **/
    float getLayoutWidth();

    /**
     * set the layout width property
     *
     * @param width the value
     */
    void setLayoutWidth(float width);

    /**
     * get layout height property
     *
     * @return the height value
     **/
    float getLayoutHeight();

    /**
     * set the layout height property
     *
     * @param height the value
     */
    void setLayoutHeight(float height);

    /**
     * Get collection of fragment preference objects used
     * to initialize user preferences
     * 
     * @return list of FragmentPreference objects
     */
    List getPreferences();    

    /**
     * Set collection of fragment preference objects
     * 
     * @param preferences list of FragmentPreference objects
     */
    void setPreferences(List preferences);
}
