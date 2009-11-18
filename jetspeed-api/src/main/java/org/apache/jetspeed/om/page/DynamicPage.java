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

import java.io.Serializable;

/**
 * This interface represents a dynamic page document used by Jetspeed
 * to define a user-customizable portal page.
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id:$
 */
public interface DynamicPage extends BaseConcretePageElement, Serializable
{
    String DOCUMENT_TYPE = ".dpsml";

    /**
     * Get the page type name that applies to this page.
     *
     * @return the page type name name
     */
    String getPageType();

    /**
     * Set the page type name for this page.
     *
     * @param pageType the name of the page type for the page
     */
    void setPageType(String pageType);
}
