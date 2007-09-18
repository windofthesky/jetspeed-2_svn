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
package org.apache.jetspeed.decoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.om.common.portlet.PortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.SecurityAccessController;
import org.apache.pluto.om.window.PortletWindow;

public abstract class AbstractDecoratorActionsFactory implements DecoratorActionsFactory
{
    private static ThreadLocal actionResourcesMap = new ThreadLocal();
    private boolean editMaximizesOption = false;
    
    /**
     * When Edit is clicked, also maximize the window state
     * 
     * @param editMaxOption
     */
    public AbstractDecoratorActionsFactory()
    {
    }
    
    public List getDecoratorActions(RequestContext rc, PortletApplication pa, PortletWindow pw, PortletMode pm,
                    WindowState ws, Decoration decoration, List actionTemplates,PortletDefinitionComposite portlet, 
                    ContentFragment fragment,SecurityAccessController accessController)
    {
        DecoratorAction action;
        boolean checkConstraints=false;      
        ArrayList actions = new ArrayList();
        
        Iterator iter = actionTemplates.iterator();
        while (iter.hasNext())
        {
            checkConstraints = false;
            DecoratorActionTemplate template = (DecoratorActionTemplate)iter.next();
            //checking the constraints only on EDIT and HELP Action, as VIEW will taken care with portlet view.
            if (template.getAction().equals(JetspeedActions.EDIT) || template.getAction().equals(JetspeedActions.HELP)) 
                checkConstraints = true; 
            if (checkConstraints && checkSecurityConstraint(portlet,fragment,accessController,template.getAction()))
            {
                action = createAction(rc, pw, decoration,template );
                if ( action != null)
                {
                    actions.add(action);
                }
            }
            else if (!checkConstraints)
            {
                action = createAction(rc, pw, decoration,template );
                if ( action != null)
                {
                    actions.add(action);
                }
            }            
        }
        return actions;
    }
    
    public List getDecoratorActions(RequestContext rc, PortletApplication pa, PortletWindow pw, PortletMode pm,
             WindowState ws, Decoration decoration, List actionTemplates)
    {
        DecoratorAction action;
        ArrayList actions = new ArrayList();
        Iterator iter = actionTemplates.iterator();
        while (iter.hasNext())
        {
            action = createAction(rc, pw, decoration,(DecoratorActionTemplate)iter.next() );
            if ( action != null)
            {
                actions.add(action);
            }
        }
        return actions;
    }        

    protected DecoratorAction createAction(RequestContext rc, PortletWindow pw, Decoration decoration,
            DecoratorActionTemplate template)
    {
        String actionName = template.getAction();

        PortalURL portalURL = rc.getPortalURL();
        Boolean isAjaxRequest = (Boolean) rc
                .getAttribute(DecorationValve.IS_AJAX_DECORATION_REQUEST);

        WindowState ws;
        PortletMode pm;
        if (editMaximizesOption)
        {
            if (template.getAction().equals(JetspeedActions.EDIT))
            {
                ws = WindowState.MAXIMIZED;
                pm = template.getCustomMode();
            }
            else if (template.getAction().equals(JetspeedActions.VIEW))
            {
                ws = WindowState.NORMAL;
                pm = template.getCustomMode();                
            }
            else if (template.getAction().equals(JetspeedActions.NORMAL))
            {
                pm = PortletMode.VIEW;   
                ws = template.getCustomState();                
            }
            else
            {
                ws = template.getCustomState();
                pm = template.getCustomMode();
            }
        }
        else
        {
            ws = template.getCustomState();
            pm = template.getCustomMode();            
        }
        /////////////////////////////////////
        
        String actionURL = rc.getResponse().encodeURL(
                (isAjaxRequest == null) ? portalURL.createPortletURL(pw, pm, ws, portalURL.isSecure()).toString() 
                        : portalURL.createNavigationalEncoding(pw, pm, ws));

        String linkURL = decoration
                .getResource("images/" + actionName + ".gif");

        boolean customAction = (template.getMode() != null && !template
                .getMode().equals(template.getCustomMode()))
                || (template.getState() != null && !template.getState().equals(
                        template.getCustomState()));

        HashMap resourcesMap = (HashMap)actionResourcesMap.get();
        ResourceBundle bundle = DecoratorAction.getResourceBundle(rc.getLocale());
        String localizedName = null;
        
        if (resourcesMap == null)
        {
            resourcesMap = new HashMap();
            actionResourcesMap.set(resourcesMap);
            resourcesMap.put(DecoratorAction.RESOURCE_BUNDLE, bundle);
            localizedName = DecoratorAction.getResourceString(bundle, actionName, actionName);
            resourcesMap.put(actionName,localizedName);
        }
        else
        {
            localizedName = (String)resourcesMap.get(actionName);
            if (localizedName == null)
            {
                localizedName = DecoratorAction.getResourceString(bundle, actionName, actionName);
                resourcesMap.put(actionName,localizedName);
            }
        }
        return new DecoratorAction(actionName, localizedName, localizedName, linkURL, actionURL, customAction, template.getActionType());
    }
    
    //added for checkin the constraints on actions
    protected boolean checkSecurityConstraint(
            PortletDefinitionComposite portlet, ContentFragment fragment,
            SecurityAccessController accessController, String action)
    {
        if (fragment.getType().equals(ContentFragment.PORTLET))
        {
            if (accessController != null) 
            { 
                return accessController
                    .checkPortletAccess(portlet, JetspeedActions
                            .getContainerActionMask(action)); 
            }
        }
        return true;
    }    
    
    public void setMaximizeOnEdit(boolean maxOnEdit)
    {
        this.editMaximizesOption = maxOnEdit;
    }
    
    public boolean getMaximizeOnEdit()
    {
        return this.editMaximizesOption;
    }
    
}
