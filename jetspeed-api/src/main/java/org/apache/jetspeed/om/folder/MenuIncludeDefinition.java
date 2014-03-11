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

/**
 * This interface describes the object used to define
 * portal site menu included menus.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public interface MenuIncludeDefinition extends MenuDefinitionElement
{
    /**
     * getName - get menu name to nest or with options to include
     *
     * @return menu name
     */
    String getName();

    /**
     * setName - set menu name to nest or with options to include
     *
     * @param name menu name
     */
    void setName(String name);

    /**
     * isNest - get nesting for included menu
     *
     * @return nest options flag
     */
    boolean isNest();
    
    /**
     * setNest - set nesting for included menu
     *
     * @param nest nest menu flag
     */
    void setNest(boolean nest);    
}
