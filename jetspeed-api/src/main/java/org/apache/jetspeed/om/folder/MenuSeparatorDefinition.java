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
package org.apache.jetspeed.om.folder;

import org.apache.jetspeed.om.portlet.GenericMetadata;

import java.util.Locale;

/**
 * This interface describes the object used to define
 * portal site menu separators.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public interface MenuSeparatorDefinition extends MenuDefinitionElement
{
    /**
     * getSkin - get skin name for separator
     *
     * @return skin name
     */
    String getSkin();

    /**
     * setSkin - set skin name for separator
     *
     * @param name skin name
     */
    void setSkin(String name);

    /**
     * getTitle - get default title for separator
     *
     * @return title text
     */
    String getTitle();

    /**
     * setTitle - set default title for separator
     *
     * @param title title text
     */
    void setTitle(String title);

    /**
     * getText - get default text for separator
     *
     * @return text
     */
    String getText();

    /**
     * setText - set default text for separator
     *
     * @param text text
     */
    void setText(String text);

    /**
     * getTitle - get locale specific title for separator from metadata
     *
     * @param locale preferred locale
     * @return title text
     */
    String getTitle(Locale locale);

    /**
     * getText - get locale specific text for separator from metadata
     *
     * @param locale preferred locale
     * @return text
     */
    String getText(Locale locale);

    /**
     * getMetadata - get generic metadata instance for menu
     *
     * @return metadata instance
     */
    GenericMetadata getMetadata();
}
