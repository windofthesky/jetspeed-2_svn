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

/**
 * This interface describes the portal-site menu option
 * elements constructed and returned to decorators.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public interface MenuOption extends MenuElement
{
    /**
     * FOLDER_OPTION_TYPE - type of folder menu option
     */
    String FOLDER_OPTION_TYPE = "folder";

    /**
     * PAGE_OPTION_TYPE - type of page menu option
     */
    String PAGE_OPTION_TYPE = "page";

    /**
     * LINK_OPTION_TYPE - type of link menu option
     */
    String LINK_OPTION_TYPE = "link";

    /**
     * getType - get type of menu option
     *
     * @return FOLDER_OPTION_TYPE, PAGE_OPTION_TYPE, or
     *         LINK_OPTION_TYPE
     */
    String getType();

    /**
     * getUrl - get url of menu option
     *
     * @return folder, page, or link url
     */
    String getUrl();

    /**
     * getTarget - get target for url of menu option
     *
     * @return url target
     */
    String getTarget();

    /**
     * getDefaultPage - get target for url of menu option
     *
     * @return url target
     */
    String getDefaultPage();
    
    /**
     * isHidden - get hidden state of menu option
     *
     * @return hidden state
     */
    boolean isHidden();

    /**
     * isSelected - return true if menu option is selected in
     *              the specified request context
     *
     * @param context request context
     * @return selected state
     */
    boolean isSelected(PortalSiteRequestContext context);
}
