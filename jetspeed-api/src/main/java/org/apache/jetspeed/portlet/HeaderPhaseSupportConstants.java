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
package org.apache.jetspeed.portlet;

public interface HeaderPhaseSupportConstants
{

    /**
     * An attribute name of head element contributed to the portal page aggregation by a portlet.
     * The value of this attribute can be used by portal to merge contents of each contributed head element
     * into centralized element(s).
     * <BR/>
     * For example, if a contributed head element has an attribute value, 'dojo.require', with text content, 'dojo.lang.*'
     * and another contributed element has the same attribute value, 'dojo.require', with text content, 'dojo.event.*', then
     * a portal can merge those contents into a single script element for optimization.
     */
    public static final String HEAD_ELEMENT_CONTRIBUTION_MERGE_HINT_ATTRIBUTE = "org.apache.portals.portal.page.head.element.contribution.merge.hint";

    /**
     * An attribute value for key hint which can be used in head elements merging by portal.  
     */
    public static final String HEAD_ELEMENT_CONTRIBUTION_MERGE_HINT_KEY_DOJO_REQUIRE = "dojo.require";

    /**
     * An ID attribute value which can be used in head elements merging by portal.  
     */
    public static final String HEAD_ELEMENT_CONTRIBUTION_ELEMENT_ID_DOJO_LIBRARY_INCLUDE = "dojo.library.include";
    
}
