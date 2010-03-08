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

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.impl.StandardMenuDefinitionImpl;
import org.apache.jetspeed.portalsite.view.AbstractSiteView;

/**
 * This class provides a menu definition for the standard
 * back menu.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class StandardBackMenuDefinition extends StandardMenuDefinitionImpl
{
    /**
     * StandardBackMenuDefinition - constructor
     */
    public StandardBackMenuDefinition()
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
        return AbstractSiteView.STANDARD_BACK_MENU_NAME;
    }

    /**
     * getOptions - get comma separated menu options if not specified as elements
     *
     * @return option paths specification
     */
    public String getOptions()
    {
        // parent folder
        return ".." + Folder.PATH_SEPARATOR;
    }

    /**
     * getSkin - get skin name for menu element
     *
     * @return skin name
     */
    public String getSkin()
    {
        return "breadcrumbs";
    }

    /**
     * getTitle - get default title for menu
     *
     * @return title text
     */
    public String getTitle()
    {
        // use locale defaults
        return getMenuTitleText(null, getTitleResourceKey());
    }

    /**
     * getTitle - get locale specific title for menu from metadata
     *            protocol, with or without falback enabled
     *
     * @param locale preferred locale
     * @param fallback whether to return default title
     * @return title text
     */
    protected String getTitle(Locale locale, boolean fallback)
    {
        // use specified locale or fallback if locale specific title not defined
        String title = getMenuTitleText(locale, getTitleResourceKey());
        if (title != null)
        {
            return title;
        }
        return super.getTitle(locale, fallback);
    }

    /**
     * getTitleResourceKey - get resource key used to lookup menu titles
     *
     * @return resource key
     */
    protected String getTitleResourceKey()
    {
        return "menu.title.back";
    }

    /**
     * getMenuTitleText - lookup resource bundle based on locale
     *                    and use to extract menu title text
     *
     * @param locale preferred locale
     * @param key message key for text
     */
    protected String getMenuTitleText(Locale locale, String key)
    {
        try
        {
            // get resource bundle
            ResourceBundle bundle = null;
            if (locale != null)
            {
                bundle = ResourceBundle.getBundle("org.apache.jetspeed.portalsite.menu.resources.MenuTitles",locale);
            }
            else
            {
                bundle = ResourceBundle.getBundle("org.apache.jetspeed.portalsite.menu.resources.MenuTitles");
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
