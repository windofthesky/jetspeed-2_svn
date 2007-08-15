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

import org.apache.jetspeed.om.folder.MenuDefinition;
import org.apache.jetspeed.om.folder.MenuExcludeDefinition;
import org.apache.jetspeed.om.folder.MenuIncludeDefinition;
import org.apache.jetspeed.om.folder.MenuOptionsDefinition;
import org.apache.jetspeed.om.folder.MenuSeparatorDefinition;

/**
 * This interface represents a complete page document used by Jetspeed
 * to layout a user-customizable portal page.
 *
 * @version $Id$
 */
public interface Page extends Document, java.io.Serializable
{
    String DOCUMENT_TYPE = ".psml";
    
    /**
     * Returns the name of the default skin that applies to this
     * page.
     *
     * @return the page default skin name
     */
    String getSkin();

    /**
     * Modifies the skin for this page.
     *
     * @param skinName the name of the new skin for the page
     */
    void setSkin(String skinName);

    /**
     * Returns the name of the default decorator as set here or
     * in parent folders that applies in this page to fragments
     * of the specified type.
     *
     * @param fragmentType the type of fragment considered
     * @return the decorator name for the selected type
     */
    String getEffectiveDefaultDecorator(String fragmentType);

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
     * Removes the fragment contained within this page, with the
     * specified Id.
     *
     * @param id the fragment id to remove
     * @return the removed Fragment object or null if not found
     */
    Fragment removeFragmentById(String id);

    /**
     * Retrieves the fragments contained within this page, with the
     * specified name.
     *
     * @param name the fragment name to look for
     * @return the list of found Fragment objects or null if not found
     */
    List getFragmentsByName(String name);

    /**
     * getMenuDefinitions - get list of menu definitions
     *
     * @return definition list
     */
    List getMenuDefinitions();

    /**
     * newMenuDefinition - creates a new empty menu definition
     *
     * @return a newly created MenuDefinition object for use in Page
     */
    MenuDefinition newMenuDefinition();

    /**
     * newMenuExcludeDefinition - creates a new empty menu exclude definition
     *
     * @return a newly created MenuExcludeDefinition object for use in Page
     */
    MenuExcludeDefinition newMenuExcludeDefinition();

    /**
     * newMenuIncludeDefinition - creates a new empty menu include definition
     *
     * @return a newly created MenuIncludeDefinition object for use in Page
     */
    MenuIncludeDefinition newMenuIncludeDefinition();

    /**
     * newMenuOptionsDefinition - creates a new empty menu options definition
     *
     * @return a newly created MenuOptionsDefinition object for use in Page
     */
    MenuOptionsDefinition newMenuOptionsDefinition();

    /**
     * newMenuSeparatorDefinition - creates a new empty menu separator definition
     *
     * @return a newly created MenuSeparatorDefinition object for use in Page
     */
    MenuSeparatorDefinition newMenuSeparatorDefinition();

    /**
     * setMenuDefinitions - set list of menu definitions
     *
     * @param definitions definition list
     */
    void setMenuDefinitions(List definitions);    
}

