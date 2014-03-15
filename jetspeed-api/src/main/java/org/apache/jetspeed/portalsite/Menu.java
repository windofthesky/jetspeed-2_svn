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
package org.apache.jetspeed.portalsite;

import java.util.List;

/**
 * This interface describes the portal-site menu elements
 * constructed and returned to decorators.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public interface Menu extends MenuElement
{
    /**
     * getName - get name of menu
     *
     * @return menu name
     */
    String getName();

    /**
     * getUrl - get url of top level folder that defined
     *          menu options; only available for menus
     *          defined without multiple options, nested
     *          menus, or separators
     *
     * @return folder url
     */
    String getUrl();

    /**
     * isHidden - get hidden state of folder that defined
     *            menu options; only available for menus
     *            defined without multiple options, nested
     *            menus, or separators
     *
     * @return hidden state
     */
    boolean isHidden();

    /**
     * isSelected - return true if an option or nested
     *              menu within this menu are selected by
     *              the specified request context
     *
     * @param context request context
     * @return selected state
     */
    boolean isSelected(PortalSiteRequestContext context);

    /**
     * getElements - get ordered list of menu elements that
     *               are members of this menu; possibly contains
     *               options, nested menus, or separators
     *
     * @return menu elements list
     */
    List<MenuElement> getElements();

    /**
     * isEmpty - get empty state of list of menu elements
     *
     * @return menu elements list empty state
     */
    boolean isEmpty();

    /**
     * getSelectedElement - return selected option or nested
     *                      menu within this menu selected by
     *                      the specified request context
     *
     * @return selected menu element
     */
    MenuElement getSelectedElement(PortalSiteRequestContext context);
}
