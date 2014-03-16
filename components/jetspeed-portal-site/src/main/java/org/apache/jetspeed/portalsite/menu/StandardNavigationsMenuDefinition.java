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
package org.apache.jetspeed.portalsite.menu;

import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.MenuDefinitionElement;
import org.apache.jetspeed.om.folder.impl.StandardMenuDefinitionImpl;
import org.apache.jetspeed.om.folder.impl.StandardMenuIncludeDefinitionImpl;
import org.apache.jetspeed.om.folder.impl.StandardMenuOptionsDefinitionImpl;
import org.apache.jetspeed.om.folder.impl.StandardMenuSeparatorDefinitionImpl;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.portalsite.view.AbstractSiteView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This class provides a menu definition for the standard
 * navigations menu.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class StandardNavigationsMenuDefinition extends StandardMenuDefinitionImpl
{
    /**
     * menuElements - ordered polymorphic list of menu option, nested
     *                menu, separator, include, and exclude definitions
     */
    private List<MenuDefinitionElement> menuElements;

    /**
     * StandardNavigationsMenuDefinition - constructor
     */
    public StandardNavigationsMenuDefinition()
    {
        super();
    }

    /**
     * getName - get menu name
     *
     * @return menu name
     */
    public String getName()
    {
        return AbstractSiteView.STANDARD_NAVIGATIONS_MENU_NAME;
    }

    /**
     * getMenuElements - get ordered list of menu options,
     *                   nested menus, separators, included
     *                   menu, and excluded menu elements
     *
     * @return element list
     */
    public synchronized List<MenuDefinitionElement> getMenuElements()
    {
        // instantiate menu elements if necessary
        if (menuElements == null)
        {
            menuElements = new ArrayList<MenuDefinitionElement>(4);
            menuElements.add(new StandardMenuSeparatorDefinitionImpl()
                {
                    /**
                     * getText - get default text for separator
                     *
                     * @return text
                     */
                    public String getText()
                    {
                        // use locale defaults
                        return getMenuSeparatorText(null, "menu.separator.folders");
                    }

                    /**
                     * getText - get locale specific text for separator from metadata
                     *
                     * @param locale preferred locale
                     * @return text
                     */
                    public String getText(Locale locale)
                    {
                        // use specified locale
                        return getMenuSeparatorText(locale, "menu.separator.folders");
                    }
                });
            menuElements.add(new StandardMenuOptionsDefinitionImpl()
                {
                    /**
                     * getOptions - get comma separated menu options
                     *
                     * @return option paths specification
                     */
                    public String getOptions()
                    {
                        return "." + Folder.PATH_SEPARATOR + "*" + Folder.PATH_SEPARATOR;
                    }

                    /**
                     * isRegexp - get regexp flag for interpreting option
                     *
                     * @return regexp flag
                     */
                    public boolean isRegexp()
                    {
                        return true;
                    }
                });
            menuElements.add(new StandardMenuIncludeDefinitionImpl()
                {
                    /**
                     * getName - get menu name to nest or with options to include
                     *
                     * @return menu name
                     */
                    public String getName()
                    {
                        return AbstractSiteView.CUSTOM_PAGE_NAVIGATIONS_MENU_NAME;
                    }
                });
            menuElements.add(new StandardMenuSeparatorDefinitionImpl()
                {
                    /**
                     * getText - get default text for separator
                     *
                     * @return text
                     */
                    public String getText()
                    {
                        // use locale defaults
                        return getMenuSeparatorText(null, "menu.separator.links");
                    }

                    /**
                     * getText - get locale specific text for separator from metadata
                     *
                     * @param locale preferred locale
                     * @return text
                     */
                    public String getText(Locale locale)
                    {
                        // use specified locale
                        return getMenuSeparatorText(locale, "menu.separator.links");
                    }
                });
            menuElements.add(new StandardMenuOptionsDefinitionImpl()
                {
                    /**
                     * getOptions - get comma separated menu options
                     *
                     * @return option paths specification
                     */
                    public String getOptions()
                    {
                        return Folder.PATH_SEPARATOR + "*" + Link.DOCUMENT_TYPE;
                    }

                    /**
                     * isRegexp - get regexp flag for interpreting option
                     *
                     * @return regexp flag
                     */
                    public boolean isRegexp()
                    {
                        return true;
                    }
                });
        }
        return menuElements;
    }

    /**
     * getSkin - get skin name for menu element
     *
     * @return skin name
     */
    public String getSkin()
    {
        return "left-navigations";
    }

    /**
     * getMenuSeparatorText - lookup resource bundle based on locale
     *                        and use to extract menu separator text
     *
     * @param locale preferred locale
     * @param key message key for text
     */
    protected String getMenuSeparatorText(Locale locale, String key)
    {
        try
        {
            // get resource bundle
            ResourceBundle bundle = null;
            if (locale != null)
            {
                bundle = ResourceBundle.getBundle("org.apache.jetspeed.portalsite.menu.resources.MenuSeparators",locale);
            }
            else
            {
                bundle = ResourceBundle.getBundle("org.apache.jetspeed.portalsite.menu.resources.MenuSeparators");
            }
            
            // lookup and return keyed message
            return bundle.getString(key);
        }
        catch (MissingResourceException mre)
        {
        }
        return null;
    }
}
