/*
 * Copyright 2005 The Apache Software Foundation.
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
package org.apache.jetspeed.portalsite;

import java.util.Locale;

import org.apache.jetspeed.om.common.GenericMetadata;

/**
 * This interface describes common features of portal-site
 * menu elements constructed and returned to decorators.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public interface MenuElement
{
    /**
     * MENU_ELEMENT_TYPE - element type of menu elements
     */
    String MENU_ELEMENT_TYPE = "menu";

    /**
     * OPTION_ELEMENT_TYPE - element type of menu elements
     */
    String OPTION_ELEMENT_TYPE = "option";

    /**
     * SEPARATOR_ELEMENT_TYPE - element type of separator elements
     */
    String SEPARATOR_ELEMENT_TYPE = "separator";

    /**
     * getElementType - get type of menu element
     *
     * @return MENU_ELEMENT_TYPE, OPTION_ELEMENT_TYPE, or
     *         SEPARATOR_ELEMENT_TYPE
     */
    String getElementType();

    /**
     * getParentMenu - get menu that contains menu element 
     *
     * @return parent menu
     */    
    Menu getParentMenu();

    /**
     * getTitle - get default title for menu element
     *
     * @return title text
     */
    String getTitle();

    /**
     * getShortTitle - get default short title for menu element
     *
     * @return short title text
     */
    String getShortTitle();

    /**
     * getTitle - get locale specific title for menu element
     *            from metadata
     *
     * @param locale preferred locale
     * @return title text
     */
    String getTitle(Locale locale);

    /**
     * getShortTitle - get locale specific short title for menu
     *                 element from metadata
     *
     * @param locale preferred locale
     * @return short title text
     */
    String getShortTitle(Locale locale);

    /**
     * getMetadata - get generic metadata for menu element
     *
     * @return metadata
     */    
    GenericMetadata getMetadata();

    /**
     * getSkin - get skin name for menu element
     *
     * @return skin name
     */
    String getSkin();
}
