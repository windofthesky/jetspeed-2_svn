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

/**
 * This class provides a menu definition for default menus
 * constructed from folders within menus with depth expansion
 * specified.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class DefaultMenuDefinition extends StandardMenuDefinitionImpl
{
    /**
     * options - options path specification for menu
     */
    private String options;

    /**
     * depth - inclusion depth for menu
     */
    private int depth;

    /**
     * profile - profile locator for menu and its elements
     */
    private String profile;

    /**
     * DefaultMenuDefinition - constructor
     */
    public DefaultMenuDefinition(String options, int depth, String locatorName)
    {
        super();
        this.options = options;
        this.depth = depth;
        this.profile = locatorName;
    }

    /**
     * getOptions - get comma separated menu options if not specified as elements
     *
     * @return option paths specification
     */
    public String getOptions()
    {
        return options;
    }

    /**
     * getDepth - get depth of inclusion for folder menu options
     *
     * @return inclusion depth
     */
    public int getDepth()
    {
        return depth;
    }

    /**
     * getProfile - get profile locator used to filter specified options
     *
     * @return profile locator name
     */
    public String getProfile()
    {
        return profile;
    }
}
