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

import java.util.Locale;

import org.apache.jetspeed.om.folder.MenuSeparatorDefinition;
import org.apache.jetspeed.om.portlet.GenericMetadata;

/**
 * This abstract class implements the menu separator definition
 * interface in a default manner to allow derived classes to
 * easily describe standard menu definitions.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public abstract class StandardMenuSeparatorDefinitionImpl implements MenuSeparatorDefinition
{
    /**
     * StandardMenuSeparatorDefinitionImpl - constructor
     */
    public StandardMenuSeparatorDefinitionImpl()
    {
    }

    /**
     * getSkin - get skin name for separator
     *
     * @return skin name
     */
    public String getSkin()
    {
        return null;
    }

    /**
     * setSkin - set skin name for separator
     *
     * @param name skin name
     */
    public void setSkin(String name)
    {
        throw new RuntimeException("StandardMenuSeparatorDefinitionImpl instance immutable");        
    }

    /**
     * getTitle - get default title for separator
     *
     * @return title text
     */
    public String getTitle()
    {
        return null;
    }

    /**
     * setTitle - set default title for separator
     *
     * @param title title text
     */
    public void setTitle(String title)
    {
        throw new RuntimeException("StandardMenuSeparatorDefinitionImpl instance immutable");        
    }

    /**
     * getText - get default text for separator
     *
     * @return text
     */
    public String getText()
    {
        return null;
    }

    /**
     * setText - set default text for separator
     *
     * @param text text
     */
    public void setText(String text)
    {
        throw new RuntimeException("StandardMenuSeparatorDefinitionImpl instance immutable");        
    }

    /**
     * getTitle - get locale specific title for separator from metadata
     *
     * @param locale preferred locale
     * @return title text
     */
    public String getTitle(Locale locale)
    {
        return getTitle();
    }

    /**
     * getText - get locale specific text for separator from metadata
     *
     * @param locale preferred locale
     * @return text
     */
    public String getText(Locale locale)
    {
        return getText();
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
}
