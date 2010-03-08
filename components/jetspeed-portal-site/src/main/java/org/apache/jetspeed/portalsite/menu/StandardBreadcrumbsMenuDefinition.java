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

import org.apache.jetspeed.portalsite.view.AbstractSiteView;

/**
 * This class provides a menu definition for the standard
 * breadcrumbs menu.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class StandardBreadcrumbsMenuDefinition extends StandardBackMenuDefinition
{
    /**
     * StandardBreadcrumbsMenuDefinition - constructor
     */
    public StandardBreadcrumbsMenuDefinition()
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
        return AbstractSiteView.STANDARD_BREADCRUMBS_MENU_NAME;
    }

    /**
     * getOptions - get comma separated menu options if not specified as elements
     *
     * @return option paths specification
     */
    public String getOptions()
    {
        // current page
        return AbstractSiteView.CURRENT_PAGE_PATH;
    }

    /**
     * isPaths - get generate ordered path options for specified options
     *
     * @return paths options flag
     */
    public boolean isPaths()
    {
        return true;
    }
    
    /**
     * getTitleResourceKey - get resource key used to lookup menu titles
     *
     * @return resource key
     */
    protected String getTitleResourceKey()
    {
        return "menu.title.breadcrumbs";
    }
}
