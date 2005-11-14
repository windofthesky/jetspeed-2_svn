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

/**
 * This interface represents a complete page document used by Jetspeed
 * to layout a user-customizable portal page.
 *
 * @version $Id$
 */
public interface Page extends Document, java.io.Serializable, Cloneable
{
    String DOCUMENT_TYPE = ".psml";
    
    /**
     * Returns the name of the default skin that applies to this
     * page. This name should reference an entry in the Skin
     * registry
     *
     * @return the page default skin name
     */
    String getDefaultSkin();

    /**
     * Modifies the default skin for this page.
     * This new skin must reference an entry in the Skin
     * registry.
     * Additionnally, replacing the default skin will not affect any
     * children fragments with their own specific skins
     *
     * @param skinName the name of the new skin for the page
     */
    void setDefaultSkin(String skinName);

    /**
     * Returns the name of the default decorator that applies in this page
     * to fragments of the specified type
     *
     * @param fragmentType the type of fragment considered
     * @return the decorator name for the selected type
     */
    String getDefaultDecorator(String fragmentType);

    /**
     * Modifies the default decorator for the specified fragment type.
     *
     * @param decoratorName the name of the new decorator for the type
     * @param fragmentType the type of fragment considered
     */
    void setDefaultDecorator(String decoratorName, String fragmentType);

    /**
     * Retrieves the top level fragment of this page. This Fragment should
     * never be null.
     *
     * @return the base Fragment object for this page.
     */
    Fragment getRootFragment();

    /**
     * Sets the top level fragment of this page. This Fragment should
     * never be null.
     *
     * @return the base Fragment object for this page.
     */    
    void setRootFragment(Fragment fragment);

    /**
     * Retrieves the fragment contained within this page, with the
     * specified Id.
     *
     * @param id the fragment id to look for
     * @return the found Fragment object or null if not found
     */
    Fragment getFragmentById(String id);

    /**
     * Retrieves the fragments contained within this page, with the
     * specified name.
     *
     * @param name the fragment name to look for
     * @return the list of found Fragment objects or null if not found
     */
    List getFragmentsByName(String name);

    /**
     * Create a clone of this object
     */
    Object clone() throws java.lang.CloneNotSupportedException;

    /**
     * getMenuDefinitions - get list of menu definitions
     *
     * @return definition list
     */
    List getMenuDefinitions();

    /**
     * setMenuDefinitions - set list of menu definitions
     *
     * @param definitions definition list
     */
    void setMenuDefinitions(List definitions);    
}

