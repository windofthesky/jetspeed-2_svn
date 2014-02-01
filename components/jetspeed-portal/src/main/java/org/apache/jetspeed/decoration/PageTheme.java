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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Default implementation of <code>org.apache.jetspeed.decoration.Theme</code>
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 *
 * @see org.apache.jetspeed.decoration.Theme
 */
public class PageTheme implements Theme, Serializable
{
    private transient ContentPage page;
    private transient DecorationFactory decorationFactory;
    private transient RequestContext requestContext;
    private final Set<String> styleSheets;
    private final LayoutDecoration layoutDecoration;
    private final Map fragmentDecorations;
    private final Collection<String> portletDecorationNames;
    private boolean invalidated = false;
        
    public PageTheme(ContentPage page, DecorationFactory decorationFactory, RequestContext requestContext)
    {
        this.page = page;
        this.decorationFactory = decorationFactory;
        this.requestContext = requestContext;
        this.styleSheets = new LinkedHashSet();
        this.fragmentDecorations = new HashMap();
        
        boolean isDesktopEnabled = decorationFactory.isDesktopEnabled( requestContext );
        HashMap namesMap = new HashMap();
        this.layoutDecoration = (LayoutDecoration)setupFragmentDecorations( page.getRootFragment(), true, namesMap, isDesktopEnabled );
        
        if ( isDesktopEnabled )
        {
            String defaultDesktopPortletDecoration = decorationFactory.getDefaultDesktopPortletDecoration();
            if ( defaultDesktopPortletDecoration != null && defaultDesktopPortletDecoration.length() > 0 )
            {
                if ( namesMap.get( defaultDesktopPortletDecoration ) == null )
                {
                    namesMap.put( defaultDesktopPortletDecoration, defaultDesktopPortletDecoration );
                }
            }
        }
        if (!namesMap.containsKey(this.layoutDecoration.getName()))
        {
            namesMap.put(this.layoutDecoration.getName(), this.layoutDecoration.getName());
            Decoration decoration = decorationFactory.getPortletDecoration(this.layoutDecoration.getName(), requestContext);
            if (decoration.getStyleSheet() != null)
                this.styleSheets.add(decoration.getStyleSheet());
            if (decoration.getStyleSheetPortal() != null)
                this.styleSheets.add(decoration.getStyleSheetPortal());            
        }
        this.portletDecorationNames = Collections.unmodifiableCollection( new ArrayList( namesMap.keySet() ) );
    }

    /**
     * setupFragmentDecorations
     *
     * Setup styleSheets and fragmentDecorations from all fragments
     * in page, including nested fragments.
     *
     * @param fragment page fragment
     * @return fragment decoration
     */
    private Decoration setupFragmentDecorations( ContentFragment fragment, boolean isRootLayout, HashMap portletDecorationNames, boolean isDesktopEnabled )
    {
        // setup fragment decorations
        Decoration decoration = decorationFactory.getDecoration( page, fragment, requestContext );
        
        fragmentDecorations.put( fragment.getId(), decoration );
        boolean isPortlet = ( ! isRootLayout && fragment.getType().equals( ContentFragment.PORTLET ) );
        
        if ( isPortlet || isRootLayout )
        {
        	String commonStyleSheet = decoration.getStyleSheet();
            if ( commonStyleSheet != null )
            {
                styleSheets.add( commonStyleSheet );
            }
            if ( isDesktopEnabled )
            {
                String desktopStyleSheet = decoration.getStyleSheetDesktop();
                if ( desktopStyleSheet != null )
                {
                    styleSheets.add( desktopStyleSheet );
                }
            }
            else
            {
                String portalStyleSheet = decoration.getStyleSheetPortal();
                if ( portalStyleSheet != null )
                {
                    styleSheets.add( portalStyleSheet );
                }
            }
            
        	if ( isPortlet )
        	{
        		portletDecorationNames.put( decoration.getName(), decoration.getName() );
        	}
        }
        
        // setup nested fragment decorations
        List fragments = fragment.getFragments();
        if ( ( fragments != null ) && ! fragments.isEmpty() )
        {
            Iterator fragmentsIter = fragments.iterator();
            while ( fragmentsIter.hasNext() )
            {
                setupFragmentDecorations( (ContentFragment)fragmentsIter.next(), false, portletDecorationNames, isDesktopEnabled );
            }
        }

        // return decoration; used to save page layout decoration
        return decoration;
    }

    public Set<String> getStyleSheets()
    {
        return styleSheets;
    }

    public Decoration getDecoration( ContentFragment fragment )
    {
        return (Decoration) fragmentDecorations.get( fragment.getId() );
    }
    
    public Collection<String> getPortletDecorationNames()
    {
        return portletDecorationNames;    // is unmodifiable
    }
    
    public LayoutDecoration getPageLayoutDecoration()
    {
        return layoutDecoration;
    }

    public void init(ContentPage page, DecorationFactory decoration, RequestContext context)
    {
        this.page = page;
        this.decorationFactory = decoration;
        this.requestContext = context;        
    }
    
    public ContentPage getPage()
    {
        return page;
    }    

    public ContentPage getContentPage()
    {
        return page;
    }

    public boolean isInvalidated()
    {
        return this.invalidated;
    }
    
    public void setInvalidated(boolean flag)
    {
        this.invalidated = flag;
    }
}
