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

import org.apache.jetspeed.om.folder.MenuExcludeDefinition;

/**
 * This abstract class implements the menu exclude definition
 * interface in a default manner to allow derived classes to
 * easily describe standard menu definitions.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public abstract class StandardMenuExcludeDefinitionImpl implements MenuExcludeDefinition
{
    /**
     * StandardMenuExcludeDefinitionImpl - constructor
     */
    public StandardMenuExcludeDefinitionImpl()
    {
    }

    /**
     * getName - get menu name with options to exclude
     *
     * @return menu name
     */
    public String getName()
    {
        return null;
    }

    /**
     * setName - set menu name with options to exclude
     *
     * @param name menu name
     */
    public void setName(String name)
    {
        throw new RuntimeException("StandardMenuExcludeDefinitionImpl instance immutable");
    }
}
