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

import org.apache.jetspeed.om.folder.impl.StandardMenuOptionsDefinitionImpl;

/**
 * This class provides a menu options definition for options
 * constructed directly from menu definitions.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class DefaultMenuOptionsDefinition extends StandardMenuOptionsDefinitionImpl
{
    /**
     * options - options path specification for menu
     */
    private String options;

    /**
     * depth - inclusion depth for menu options
     */
    private int depth;

    /**
     * paths - generate ordered paths flag for menu options
     */
    private boolean paths;

    /**
     * regexp - regexp flag for menu options
     */
    private boolean regexp;

    /**
     * profile - profile locator for menu options
     */
    private String profile;

    /**
     * order - comma separated regexp ordering patterns for menu options
     */
    private String order;

    /**
     * DefaultMenuOptionsDefinition - constructor
     */
    public DefaultMenuOptionsDefinition(String options, int depth, boolean paths, boolean regexp, String locatorName, String order)
    {
        super();
        this.options = options;
        this.depth = depth;
        this.paths = paths;
        this.regexp = regexp;
        this.profile = locatorName;
        this.order = order;
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
     * isPaths - get generate ordered path options
     *
     * @return paths options flag
     */
    public boolean isPaths()
    {
        return paths;
    }
    
    /**
     * isRegexp - get regexp flag for interpreting options
     *
     * @return regexp flag
     */
    public boolean isRegexp()
    {
        return regexp;
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

    /**
     * getOrder - get comma separated regexp ordering patterns
     *
     * @return ordering patterns list
     */
    public String getOrder()
    {
        return order;
    }
}
