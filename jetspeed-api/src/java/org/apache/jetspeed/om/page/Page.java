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

import java.util.Locale;

import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.folder.ChildNode;

/**
 * This interface represents a complete page document used by Jetspeed
 * to layout a user-customizable portal page.
 *
 * @version $Id$
 */
public interface Page extends BaseElement, java.io.Serializable, Cloneable, ChildNode
{
    
    /**
     * 
     * <p>
     * getTitle
     * </p>
     * 
     * @param locale
     * @return Title for the specified Locale
     */
    String getTitle(Locale locale);
    
	public GenericMetadata getMetadata();
	
	public void setMetadata(GenericMetadata metadata);

    /**
     * Returns the name of the default skin that applies to this
     * page. This name should reference an entry in the Skin
     * registry
     *
     * @return the page default skin name
     */
    public String getDefaultSkin();

    /**
     * Modifies the default skin for this page.
     * This new skin must reference an entry in the Skin
     * registry.
     * Additionnally, replacing the default skin will not affect any
     * children fragments with their own specific skins
     *
     * @param skinName the name of the new skin for the page
     */
    public void setDefaultSkin(String skinName);

    /**
     * Returns the name of the default decorator that applies in this page
     * to fragments of the specified type
     *
     * @param fragmentType the type of fragment considered
     * @return the decorator name for the selected type
     */
    public String getDefaultDecorator(String fragmentType);

    /**
     * Modifies the default decorator for the specified fragment type.
     *
     * @param decoratorName the name of the new decorator for the type
     * @param fragmentType the type of fragment considered
     */
    public void setDefaultDecorator(String decoratorName, String fragmentType);

    /**
     * Retrieves the top level fragment of this page. This Fragment should
     * never be null.
     *
     * @return the base Fragment object for this page.
     */
    public Fragment getRootFragment();

    /**
     * Sets the top level fragment of this page. This Fragment should
     * never be null.
     *
     * @return the base Fragment object for this page.
     */    
    public void setRootFragment(Fragment fragment);

    /**
     * Retrieves the fragment contained within this page, with the
     * specified Id.
     *
     * @param id the fragment id to look for
     * @return the found Fragment object or null if not found
     */
    public Fragment getFragmentById(String id);

    /**
     * Create a clone of this object
     */
    public Object clone()
        throws java.lang.CloneNotSupportedException;
}

