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

import org.apache.jetspeed.om.folder.impl.StandardMenuDefinitionImpl;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.portalsite.view.AbstractSiteView;

/**
 * This class provides a menu definition for the standard
 * pages menu.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class StandardPagesMenuDefinition extends StandardMenuDefinitionImpl
{
    /**
     * StandardPagesMenuDefinition - constructor
     */
    public StandardPagesMenuDefinition()
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
        return AbstractSiteView.STANDARD_PAGES_MENU_NAME;
    }

    /**
     * getOptions - get comma separated menu options if not specified as elements
     *
     * @return option paths specification
     */
    public String getOptions()
    {
        return "*" + Page.DOCUMENT_TYPE;
    }

    /**
     * isRegexp - get regexp flag for interpreting specified option
     *
     * @return regexp flag
     */
    public boolean isRegexp()
    {
        return true;
    }

    /**
     * getSkin - get skin name for menu element
     *
     * @return skin name
     */
    public String getSkin()
    {
        return "tabs";
    }
}
