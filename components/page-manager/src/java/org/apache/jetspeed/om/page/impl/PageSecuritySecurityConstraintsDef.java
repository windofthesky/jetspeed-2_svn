/*
 * Copyright 2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.om.page.impl;

/**
 * PageSecuritySecurityConstraintsDef
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class PageSecuritySecurityConstraintsDef extends BaseSecurityConstraint
{
    private String name;

    /**
     * getName
     *
     * @return name of defined constraint
     */
    public String getName()
    {
        return name;
    }

    /**
     * setName
     *
     * @param name name of defined constraint
     */
    public void setName(String name)
    {
        this.name = name;
    }
}
