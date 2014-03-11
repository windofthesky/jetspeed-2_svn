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

import java.io.Serializable;
import java.util.List;

/**
 * <p>A <code>Fragment</code> is the basic element handled by the aggregation
 * engine to compose the final portal page. It represents a reserved screen
 * area whose layout is managed by a specified component.</p>
 * <p>The component that is responsible for the layout policy of the fragment
 * is defined by two properties:<p>
 * <ul>
 *   <li><b>type</b>: defines the general class of layout component, enabling
 *       the engine to retrieve its exact definition from its component
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
public interface Fragment extends BaseFragmentElement, Serializable
{
    /**
     * A fragment of type PORTLET is considered to be a compliant portlet
     * in the sense of the JSR 168.
     */
    String PORTLET = "portlet";

    /**
     * A fragment of type LAYOUT is a specific JSR 168 compliant portlet
     * that knows how to layout a Page and depends on the Jetspeed
     * layout service.
     */
    String LAYOUT = "layout";

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
     * Binds an administrative name to this fragment
     *
     * @param name the administrative name
     */
    void setName(String name);

    /**
     * Returns the type of the class bound to this fragment
     */
    String getType();

    /**
     * Binds a type to this fragment
     *
     * @param type the type
     */
    void setType(String type);
    
    /**
     * Returns all fragments used in this node. This may be
     * a page fragment or even directly a portlet fragment
     *
     * @return a collection containing BaseFragmentElement objects
     */
    List<BaseFragmentElement> getFragments();
    
    /**
     * Retrieves the fragment contained within this fragment, with the
     * specified Id.
     *
     * @param id the fragment id to look for
     * @return the found Fragment object or null if not found
     */
    BaseFragmentElement getFragmentById(String id);

    /**
     * Removes the fragment contained within this fragment, with the
     * specified Id.
     *
     * @param id the fragment id to remove
     * @return the removed Fragment object or null if not found
     */
    BaseFragmentElement removeFragmentById(String id);
}
