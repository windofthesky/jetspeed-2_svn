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

import org.apache.jetspeed.page.document.Node;

/**
 * <p>
 * Document
 * </p>
 * <p>
 * 
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public interface Document extends Node
{
    /**
     * Gets the version number
     * 
     * @return version number is a string composed of optionally point separated digits
     */
    String getVersion();
    
    /**
     * Sets the version number
     * 
     * @param versionNumber version number is a string composed of optional point separated digits
     */
    void setVersion(String versionNumber);
    
    /**
     * <p>
     * isDirty
     * </p>
     * <p>
     *  Whether this node is dirty, i.e. should be updated in the persistent store.
     * </p>
     * @param hidden flag
     */
    boolean isDirty();

    /**
     * <p>
     * setDirty
     * </p>
     * <p>
     *  Flag the node as dirty / clean, i.e. should be resp. should not be updated in the persistent store
     * </p>
     * @param hidden flag
     */

    void setDirty(boolean dirty);
    
}