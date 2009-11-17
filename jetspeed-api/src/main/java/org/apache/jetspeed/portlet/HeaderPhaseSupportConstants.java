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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This interface defines some constants for attribute name or value of contributed head elements.
 * This constant values are used by Jetspeed portal to optimize the head elements aggregation.
 * <P>
 * Jetspeed can look up 'id' attribute value not to include redundant head element.
 * Also, Jetspeed can look up 'org.apache.portals.portal.page.head.element.contribution.merge.hint' attribute value
 * to merge all the text content of elements containing 'org.apache.portals.portal.page.head.element.contribution.merge.hint'
 * attribute with same value.
 * </P>
 * <P>
 * For example, if the following elements are contributed by a portlet,
 * <XMP>
 * <script id="header.dojo.library.include" language="JavaScript" src="/script/dojo/dojo.js"></script>
 * <script language="JavaScript" org.apache.portals.portal.page.head.element.contribution.merge.hint="header.dojo.requires">
 * dojo.require("dojo.io.*");
 * </script>
 * </XMP>
 * and, if the following elements are contributed by another portlet,
 * <XMP>
 * <script id="header.dojo.library.include" language="JavaScript" src="/script/dojo/dojo.js"></script>
 * <script language="JavaScript" org.apache.portals.portal.page.head.element.contribution.merge.hint="header.dojo.requires">
 * dojo.require("dojo.lang.*");
 * </script>
 * </XMP>
 * then, the result aggregated elements are to be like the following:
 * <XMP>
 * <script id="header.dojo.library.include" language="JavaScript" src="/script/dojo/dojo.js"></script>
 * <script language="JavaScript" org.apache.portals.portal.page.head.element.contribution.merge.hint="header.dojo.requires">
 * dojo.require("dojo.io.*");
 * dojo.require("dojo.lang.*");
 * </script>
 * </XMP>
 * </P>
 * 
 * @version $Id$
 */
public interface HeaderPhaseSupportConstants
{

    /**
     * An ID attribute value for dojo library inclusion which can be used in head elements merging by portal.  
     */
    public static final String HEAD_ELEMENT_CONTRIBUTION_ELEMENT_ID_DOJO_LIBRARY_INCLUDE = "header.dojo.library.include";
    
    /**
     * An ID attribute value for YUI library inclusion which can be used in head elements merging by portal.  
     */
    public static final String HEAD_ELEMENT_CONTRIBUTION_ELEMENT_ID_YUI_LIBRARY_INCLUDE = "header.yui.library.include";

    /**
     * ID attribute values set for script libraries included by the container.
     */
    public static final Set<String> CONTAINER_HEAD_ELEMENT_CONTRIBUTION_ELEMENT_ID_SET = 
        Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(new String [] { HEAD_ELEMENT_CONTRIBUTION_ELEMENT_ID_DOJO_LIBRARY_INCLUDE, HEAD_ELEMENT_CONTRIBUTION_ELEMENT_ID_YUI_LIBRARY_INCLUDE})));
    
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
     * An attribute value for key hint to aggregate dojo configuration which can be used in head elements merging by portal.  
     */
    public static final String HEAD_ELEMENT_CONTRIBUTION_MERGE_HINT_KEY_DOJO_CONFIG = "header.dojo.config";
    
    /**
     * An attribute value for key hint to aggregate dojo require statements which can be used in head elements merging by portal.  
     */
    public static final String HEAD_ELEMENT_CONTRIBUTION_MERGE_HINT_KEY_DOJO_REQUIRES = "header.dojo.requires";

}
