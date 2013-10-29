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
package org.apache.jetspeed.layout.impl;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.decoration.DecorationFactory;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.request.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Get Portal-wide themes lists 
 * (page decorators, portlet decorators, layouts, desktop-page-decorators, desktop-portlet-decorators)
 *
 * AJAX Parameters: 
 *    none 
 *    
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class GetThemesAction 
    extends BasePortletAction 
    implements AjaxAction, AjaxBuilder, Constants
{
    protected static final Logger log = LoggerFactory.getLogger(GetThemesAction.class);
    protected DecorationFactory decorationFactory;
    
    public GetThemesAction(String template, 
                           String errorTemplate,
                           DecorationFactory decorationFactory,
                           PortletActionSecurityBehavior securityBehavior)
    {
        super(template, errorTemplate, securityBehavior);
        this.decorationFactory = decorationFactory;
    }

    public boolean run( RequestContext requestContext, Map<String,Object> resultMap )
    {
        boolean success = true;
        String status = "success";
        try
        {
            resultMap.put( ACTION, "getthemes" );
            if (false == checkAccess( requestContext, JetspeedActions.VIEW ) )
            {
                    success = false;
                    resultMap.put( REASON, "Insufficient access to get themes" );
                    return success;
            }                     
            String type = getActionParameter(requestContext, TYPE );
            String format = getActionParameter(requestContext, FORMAT );
            if (format == null)
                format = "xml";
            if (type == null || type.equals( PAGE_DECORATIONS ) )
                resultMap.put( PAGE_DECORATIONS, decorationFactory.getPageDecorations( requestContext ) );
            if (type == null || type.equals( PORTLET_DECORATIONS ) )
                resultMap.put( PORTLET_DECORATIONS, decorationFactory.getPortletDecorations( requestContext ) );
            if (type == null || type.equals( LAYOUTS ) )
                resultMap.put( LAYOUTS, decorationFactory.getLayouts( requestContext ) );
            if (type == null || type.equals( DESKTOP_PAGE_DECORATIONS) )
                resultMap.put( DESKTOP_PAGE_DECORATIONS, decorationFactory.getDesktopPageDecorations( requestContext ) );
            if (type == null || type.equals( DESKTOP_PORTLET_DECORATIONS ) )
                resultMap.put( DESKTOP_PORTLET_DECORATIONS, decorationFactory.getDesktopPortletDecorations( requestContext ) );
            resultMap.put( TYPE, type );
            resultMap.put( FORMAT, format );
            resultMap.put( STATUS, status );
        } 
        catch (Exception e)
        {
            // Log the exception
            log.error( "exception while getting theme info", e );
            // Return a failure indicator
            success = false;
        }

        return success;
	}
    
    
}
