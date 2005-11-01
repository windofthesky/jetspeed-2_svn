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

package org.apache.jetspeed.om.page;

import java.util.List;
import java.util.Map;

/**
 * <p>A <code>Fragment</code> is the basic element handled by the aggregation
 * engine to compose the final portal page. It represents a reserved screen
 * area whose layout is managed by a specified component.</p>
 * <p>The component that is responsible for the layout policy of the fragment
 * is defined by two properties:<p>
 * <ul>
 *   <li><b>type</b>: defines the general class of layout component, enabling
 *       the engine to retrieve its exact defintion from its component
 *       repository.
 *   </li>
 *   <li><b>name</b>: this is the exact name of the component. This name must
 *       be unique within a portal instance for a specific component type.
 *   </li>
 * </ul>
 * <p>In addition to specifying the component responsible for the layout,
 * the fragment also stores contextual information used for rendering:</p>
 * <p>Finally the fragment also holds layout and rendering properties that
 *   may be used by a parent fragment to layout all its inner fragments in
 *   an appropriate fashion.</p>
 *
 * @version $Id$
 */
public interface Fragment extends BaseElement, Cloneable, java.io.Serializable
{
    /**
     * A fragment of type PORTLET is considered to be a compliant portlet
     * in the sense of the JSR 168.
     */
     public String PORTLET = "portlet";

    /**
     * A fragment of type LAYOUT is a specific JSR 168 compliant portlet
     * that knows how to layout a Page and depends on the Jetspeed
     * layout service.
     */
     public String LAYOUT = "layout";

    /**
     * row standard layout property name
     */
     public String ROW_PROPERTY_NAME = "row";

    /**
     * column standard layout property name
     */
     public String COLUMN_PROPERTY_NAME = "column";

    /**
     * widths standard layout property name
     */
     public String SIZES_PROPERTY_NAME = "sizes";

    /**
     * Returns the administrative name of this fragment. This name should map
     * to a component name in the component repository defined by the type
     * attribute.
     * If the name is not mapped to any component, the fragment is discarded
     * from the rendering process, as well as any inner fragment.
     *
     * @return the administrative name
     */
    public String getName();

    /**
     * Binds an administrative name to this fragment
     *
     * @param name the administrative name
     */
    public void setName(String name);

    /**
     * Returns the type of the class bound to this fragment
     */
    public String getType();

    /**
     * Binds a type to this fragment
     *
     * @param type the type
     */
    public void setType(String type);

    /**
     * Returns the name of the skin associated to this fragment
     */
    public String getSkin();

    /**
     * Defines the skin for this fragment. This skin should be
     * known by the portal.
     *
     * @param skinName the name of the new skin applied to this fragment
     */
    public void setSkin(String skinName);

    /**
     * Returns the name of the decorator bound to this fragment
     */
    public String getDecorator();

    /**
     * Defines the decorator for this fragment. This decorator should be
     * known by the portal.
     *
     * @param decoratorName the name of the decorator applied to this fragment
     */
    public void setDecorator(String decoratorName);

    /**
     * Returns the display state of this fragment. This state may have the
     * following values:
     * "Normal","Minimized","Maximized","Hidden".
     */
    public String getState();

    /**
     * Sets the display state of this fragment.
     * Valid states are: "Normal","Minimzed","Maximized","Hidden"
     *
     * @param decoratorName the name of the decorator applied to this fragment
     */
    public void setState(String state);

    /**
     * Returns all fragments used in this node. This may be
     * a page fragment or even directly a portlet fragment
     *
     * @return a collection containing Fragment objects
     */
    public List getFragments();

    /**
     * getProperty
     *
     * Get named property value.
     *
     * @param propName property name
     * @return value
     */
    public String getProperty(String propName);
    
    /**
     * getIntProperty
     *
     * Get named property value as integer.
     *
     * @param propName property name
     * @return int value
     */
    public int getIntProperty(String propName);
    
    /**
     * getProperties
     *
     * Get writable Map of properties by name.
     *
     * @return properties map
     */
    public Map getProperties();

    /**
     * get layout row property
     *
     * @return row layout property
     **/
    public int getLayoutRow();
    
    /**
     * set the layout row property
     * 
     * @param row
     */
    public void setLayoutRow(int row);
    
    /**
     * get layout column property
     *
     * @return column layout property
     **/
    public int getLayoutColumn();

    /**
     * set the layout column property
     * 
     * @param column
     */
    public void setLayoutColumn(int column);
    
    /**
     * get layout sizes property, (i.e. "25%,75%")
     *
     * @return sizes layout property
     **/
    public String getLayoutSizes();

    /**
     * set the layout sizes
     * 
     * @param sizes
     */
    public void setLayoutSizes(String sizes);
    
    /**
     * Test if this fragment is actually a reference to an external fragment.
     *
     * @return true is this element is a reference
     */
    public boolean isReference();

    /**
     * Creates a clone of this object
     */
    public Object clone()
        throws java.lang.CloneNotSupportedException;
    
}
