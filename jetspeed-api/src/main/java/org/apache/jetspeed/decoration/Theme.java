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
package org.apache.jetspeed.decoration;

import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.request.RequestContext;

import java.util.Collection;
import java.util.Set;

/**
 * Theme provides a simple aggregation of all of the decorations
 * within the current "page."
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 *
 */
public interface Theme 
{
    /**
     * 
     * @return Set of all of the stylesheets needed to properly
     * render of the decorations in this theme.
     */
    Set<String> getStyleSheets();
    
    /**
     * Returns a a Decoration for the requested fragment.
     * 
     * @param fragment whose decoration we want to retrieve.
     * @return Decoration for this fragment.
     * 
     * @see Decoration
     */
    Decoration getDecoration(ContentFragment fragment);
    
    /**
     * Get a list of portlet decoration names used by 
     * portlets on the current page.
     * 
     * @return unmodifiable list for portlet decoration names.
     * 
     * @see Decoration
     */
    Collection<String> getPortletDecorationNames();
    
    
    /**
     * Returns the the top most, "root" layout fragment's
     * decoration. 
     * 
     * @return the the top most, "root" layout fragment's
     * decoration. 
     */
    LayoutDecoration getPageLayoutDecoration();
    
    /**
     * Initialize a page theme
     * 
     * @param context
     */
    void init(ContentPage page, DecorationFactory decoration, RequestContext context);
    
    boolean isInvalidated();
    
    void setInvalidated(boolean flag);
}
