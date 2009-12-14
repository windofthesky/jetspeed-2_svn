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
package org.apache.jetspeed.om.page;

import java.util.List;

/**
 * This interface represents a generic document with fragments.
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id:$
 */
public interface BaseFragmentsElement extends Document
{
    /**
     * Retrieves the top level fragment of this page. This Fragment should
     * never be null.
     *
     * @return the base Fragment object for this page.
     */
    BaseFragmentElement getRootFragment();

    /**
     * Sets the top level fragment of this page. This Fragment should
     * never be null.
     *
     * @return the base Fragment object for this page.
     */    
    void setRootFragment(BaseFragmentElement fragment);

    /**
     * Retrieves the fragment contained within this page, with the
     * specified Id.
     *
     * @param id the fragment id to look for
     * @return the found Fragment object or null if not found
     */
    BaseFragmentElement getFragmentById(String id);

    /**
     * Removes the fragment contained within this page, with the
     * specified Id.
     *
     * @param id the fragment id to remove
     * @return the removed Fragment object or null if not found
     */
    BaseFragmentElement removeFragmentById(String id);

    /**
     * Retrieves the fragments contained within this page, with the
     * specified name.
     *
     * @param name the fragment name to look for
     * @return the list of found Fragment objects or null if not found
     */
    List getFragmentsByName(String name);

    /**
     * Retrieves the fragments contained within this page, with the
     * specified interface.
     *
     * @param interfaceFilter the fragment interface to match or null for all
     * @return the list of found Fragment objects or null if not found
     */
    List getFragmentsByInterface(Class interfaceFilter);
}