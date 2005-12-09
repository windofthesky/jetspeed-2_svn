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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.request.RequestContext;

/**
 * Default implementation of <code>org.apache.jetspeed.decoration.Theme</code>
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 *
 * @see org.apache.jetspeed.decoration.Theme
 */
public class PageTheme implements Theme
{
    private final Page page;
    private final DecorationFactory decorationFactory;
    private final RequestContext requestContext;
    private final Set styleSheets;
    private final LayoutDecoration layoutDecoration;
    private final Map fragmentDecorations;
    
    public PageTheme(Page page, DecorationFactory decorationFactory, RequestContext requestContext)
    {
        this.page = page;
        this.decorationFactory = decorationFactory;
        this.requestContext = requestContext;
        this.styleSheets = new LinkedHashSet();
        this.fragmentDecorations = new HashMap();
        
        Fragment rootFragment = page.getRootFragment();
        layoutDecoration = (LayoutDecoration) decorationFactory.getDecoration(page, rootFragment, requestContext);
        styleSheets.add(layoutDecoration.getStyleSheet());
        fragmentDecorations.put(rootFragment.getId(), layoutDecoration);
        
        Iterator fragments = rootFragment.getFragments().iterator();
        
        while(fragments.hasNext())
        {
            Fragment fragment = (Fragment) fragments.next();
            Decoration decoration = decorationFactory.getDecoration(page, fragment, requestContext);
            styleSheets.add(decoration.getStyleSheet());
            fragmentDecorations.put(fragment.getId(), decoration);
        }
        
    }

    public Set getStyleSheets()
    {
        return styleSheets;
    }

    public Decoration getDecoration(Fragment fragment)
    {
        return (Decoration) fragmentDecorations.get(fragment.getId());
    }
    
    public LayoutDecoration getPageLayoutDecoration()
    {
        return layoutDecoration;
    }

}
