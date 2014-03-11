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
package org.apache.jetspeed.om.folder.impl;

import org.apache.jetspeed.om.folder.MenuDefinition;
import org.apache.jetspeed.om.folder.MenuDefinitionElement;
import org.apache.jetspeed.om.portlet.GenericMetadata;

import java.util.List;
import java.util.Locale;

/**
 * This abstract class implements the menu definition interface
 * in a default manner to allow derived classes to easily describe
 * standard menu definitions supported natively by the portal site
 * component.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public abstract class StandardMenuDefinitionImpl implements MenuDefinition
{
    /**
     * StandardMenuDefinitionImpl - constructor
     */
    public StandardMenuDefinitionImpl()
    {
    }

    /**
     * getName - get menu name
     *
     * @return menu name
     */
    public String getName()
    {
        return null;
    }

    /**
     * setName - set menu name
     *
     * @param name menu name
     */
    public void setName(String name)
    {
        throw new RuntimeException("StandardMenuDefinitionImpl instance immutable");
    }

    /**
     * getOptions - get comma separated menu options if not specified as elements
     *
     * @return option paths specification
     */
    public String getOptions()
    {
        return null;
    }

    /**
     * setOptions - set comma separated menu options if not specified as elements
     *
     * @param options option paths specification
     */
    public void setOptions(String options)
    {
        throw new RuntimeException("StandardMenuDefinitionImpl instance immutable");
    }

    /**
     * getDepth - get depth of inclusion for folder menu options
     *
     * @return inclusion depth
     */
    public int getDepth()
    {
        return 0;
    }

    /**
     * setDepth - set depth of inclusion for folder menu options
     *
     * @param depth inclusion depth
     */
    public void setDepth(int depth)
    {
        throw new RuntimeException("StandardMenuDefinitionImpl instance immutable");
    }

    /**
     * isPaths - get generate ordered path options for specified options
     *
     * @return paths options flag
     */
    public boolean isPaths()
    {
        return false;
    }
    
    /**
     * setPaths - set generate ordered path options for specified options
     *
     * @param paths paths options flag
     */
    public void setPaths(boolean paths)
    {
        throw new RuntimeException("StandardMenuDefinitionImpl instance immutable");
    }
    
    /**
     * isRegexp - get regexp flag for interpreting specified options
     *
     * @return regexp flag
     */
    public boolean isRegexp()
    {
        return false;
    }

    /**
     * setRegexp - set regexp flag for interpreting specified options
     *
     * @param regexp regexp flag
     */
    public void setRegexp(boolean regexp)
    {
        throw new RuntimeException("StandardMenuDefinitionImpl instance immutable");
    }

    /**
     * getProfile - get profile locator used to filter specified options
     *
     * @return profile locator name
     */
    public String getProfile()
    {
        return null;
    }

    /**
     * setProfile - set profile locator used to filter specified options
     *
     * @param locatorName profile locator name
     */
    public void setProfile(String locatorName)
    {
        throw new RuntimeException("StandardMenuDefinitionImpl instance immutable");
    }

    /**
     * getOrder - get comma separated regexp ordering patterns for options
     *
     * @return ordering patterns list
     */
    public String getOrder()
    {
        return null;
    }

    /**
     * setOrder - set comma separated regexp ordering patterns for options
     *
     * @param order ordering patterns list
     */
    public void setOrder(String order)
    {
        throw new RuntimeException("StandardMenuDefinitionImpl instance immutable");
    }

    /**
     * getSkin - get skin name for menu
     *
     * @return skin name
     */
    public String getSkin()
    {
        return null;
    }

    /**
     * setSkin - set skin name for menu
     *
     * @param name skin name
     */
    public void setSkin(String name)
    {
        throw new RuntimeException("StandardMenuDefinitionImpl instance immutable");
    }

    /**
     * getTitle - get default title for menu
     *
     * @return title text
     */
    public String getTitle()
    {
        // fallback to getName()
        return getName();
    }

    /**
     * setTitle - set default title for menu
     *
     * @param title title text
     */
    public void setTitle(String title)
    {
        throw new RuntimeException("StandardMenuDefinitionImpl instance immutable");
    }

    /**
     * getShortTitle - get default short title for menu
     *
     * @return short title text
     */
    public String getShortTitle()
    {
        // fallback to getTitle()
        return getTitle();
    }

    /**
     * setShortTitle - set default short title for menu
     *
     * @param title short title text
     */
    public void setShortTitle(String title)
    {
        throw new RuntimeException("StandardMenuDefinitionImpl instance immutable");
    }

    /**
     * getTitle - get locale specific title for menu from metadata
     *
     * @param locale preferred locale
     * @return title text
     */
    public String getTitle(Locale locale)
    {
        // fallback to getTitle()
        return getTitle(locale, true);
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
        // fallback to getTitle() if enabled
        if (fallback)
        {
            return getTitle();
        }
        return null;
    }

    /**
     * getShortTitle - get locale specific short title for menu from metadata
     *
     * @param locale preferred locale
     * @return short title text
     */
    public String getShortTitle(Locale locale)
    {
        // fallback to getTitle(Locale)
        String title = getTitle(locale, false);

        // fallback to getShortTitle()
        if (title == null)
        {
            title = getShortTitle();
        }
        return title;
    }

    /**
     * getMetadata - get generic metadata instance for menu
     *
     * @return metadata instance
     */
    public GenericMetadata getMetadata()
    {
        return null;
    }

    /**
     * getMenuElements - get ordered list of menu options,
     *                   nested menus, separators, included
     *                   menu, and excluded menu elements
     *
     * @return element list
     */
    public List<MenuDefinitionElement> getMenuElements()
    {
        return null;
    }

    /**
     * setMenuElements - set ordered list of menu options
     *
     * @param elements element list
     */
    public void setMenuElements(List<MenuDefinitionElement> elements)
    {
        throw new RuntimeException("StandardMenuDefinitionImpl instance immutable");
    }
}
