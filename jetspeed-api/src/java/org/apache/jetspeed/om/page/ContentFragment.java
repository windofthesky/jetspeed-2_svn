/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.jetspeed.om.page;

import java.util.List;

import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.decoration.Decoration;


/**
 * 
 * ContentFragment provides a volatile wrapper interface for
 * actual {@link org.apache.jetspeed.om.page.Fragment} metadata
 * objects.  Since Fragments are cached and are not request specific
 * they cannot be used to store request-level content.  This is where
 * we use the <code>ContentFragment</code> to solve this problem.
 * 
 * @author weaver@apache.org
 *
 */
public interface ContentFragment extends Fragment
{
    /**
     * Provides a list of of child ContentFragments that wrap
     * the actual Fragment metadata objects.
     * @return
     */
   List getContentFragments();

    /**     
     * Overridden to make it clear to the implemetor the {@link List}
     * returned <strong>MUST</strong> ContentFragments and not
     * just regular {@link org.apache.jetspeed.om.page.Fragment}s
     *
     * @return a collection containing ContentFragment objects
     */
    public List getFragments();

    /**
     * 
     * <p>
     * getRenderedContent
     * </p>
     * <p>
     *   Returns the raw,undecorated content of this fragment.  If
     *   overridenContent has been set and portlet content has not,
     *   overridden content should be returned.
     * </p>
     *  
     * @return The raw,undecorated content of this fragment.
     * @throws java.lang.IllegalStateException if the content has not yet been set.
     */
    public String getRenderedContent() throws IllegalStateException;

    /**
     * 
     * <p>
     * overrideRenderedContent
     * </p>
     * <p>
     * Can be used to store errors that may have occurred during the
     * rendering process.
     * </p>
     *
     * @param contnent
     */
    public void overrideRenderedContent(String contnent);

    /**
     * @return the overridden content set by overrideRenderedContent
     */
    public String getOverriddenContent();
    /**
     * 
     * <p>
     * setPortletContent
     * </p>
     *
     * @param portletContent
     */
    public void setPortletContent(PortletContent portletContent);
    
    /**
     * Retrieves the actual <code>org.apache.jetspeed.decoration.decorator</code>
     * object for this content fragment.
     * 
     * TODO: Re-evaluate the naming as this is somewhat confusing
     * due to the existence of Fragment.getDecorator()
     * @return
     */
    Decoration getDecoration();
    
    /**
     * 
     * @param decoration
     */
    void setDecoration(Decoration decoration);
    
    /**
     * Checks if the content is instantly rendered from JPT.
     */
    public boolean isInstantlyRendered();

}
