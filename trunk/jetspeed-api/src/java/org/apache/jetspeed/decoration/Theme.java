/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.jetspeed.decoration;

import java.util.Set;

import org.apache.jetspeed.om.page.Fragment;

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
    Set getStyleSheets();
    
    /**
     * Returns a a Decoration for the requested fragment.
     * 
     * @param fragment whose decoration we want to retrieve.
     * @return Decroration for this fragment.
     * 
     * @see Decoration
     * @see Fragment
     */
    Decoration getDecoration(Fragment fragment);
    
    /**
     * Returns the the top most, "root" layout fragment's
     * decoration. 
     * 
     * @return the the top most, "root" layout fragment's
     * decoration. 
     */
    LayoutDecoration getPageLayoutDecoration();
}
