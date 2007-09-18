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
import java.util.Collections;
import java.util.List;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.om.common.portlet.PortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.SecurityAccessController;
import org.apache.pluto.om.window.PortletWindow;
import org.apache.jetspeed.administration.PortalConfiguration;
import org.apache.jetspeed.administration.PortalConfigurationConstants;

public class CustomDecoratorActionsFactory extends AbstractDecoratorActionsFactory
{
    private static final DecoratorActionTemplate ABOUT_MODE_TEMPLATE = new DecoratorActionTemplate(JetspeedActions.ABOUT_MODE);
    private static final DecoratorActionTemplate EDIT_DEFAULTS_MODE_TEMPLATE = new DecoratorActionTemplate(JetspeedActions.EDIT_DEFAULTS_MODE);
    //private static final DecoratorActionTemplate PREVIEW_MODE_TEMPLATE = new DecoratorActionTemplate(JetspeedActions.PREVIEW_MODE);
    private static final DecoratorActionTemplate PRINT_MODE_TEMPLATE = new DecoratorActionTemplate(JetspeedActions.PRINT_MODE);
    private static final DecoratorActionTemplate SOLO_ACTION_TEMPLATE = new DecoratorActionTemplate(JetspeedActions.SOLO_STATE);
    
    private final List supportedActions;
    private final List supportedSoloActions;
    
    private PortalConfiguration configuration;
    
    public CustomDecoratorActionsFactory()
    {
        this(null);
    }
    
    public CustomDecoratorActionsFactory(PortalConfiguration configuration)
    {
        this.configuration = configuration;
        
        ArrayList list = new ArrayList(JetspeedActions.getStandardPortletModes());
        list.add(JetspeedActions.ABOUT_MODE);
        list.add(JetspeedActions.EDIT_DEFAULTS_MODE);
        //list.add(JetspeedActions.PREVIEW_MODE);
        list.add(JetspeedActions.PRINT_MODE);
        list.addAll(JetspeedActions.getStandardWindowStates());
        list.add(JetspeedActions.SOLO_STATE);
        supportedActions = Collections.unmodifiableList(list);
        
        list = new ArrayList(JetspeedActions.getStandardPortletModes());
        list.add(JetspeedActions.PRINT_MODE);
        supportedSoloActions = Collections.unmodifiableList(list);
    }

    public List getSupportedActions(RequestContext rc, PortletApplication pa, PortletWindow pw, PortletMode cm,
                    WindowState ws, Decoration decoration)
    {
        // don't support any window state actions when in "solo" state
        return JetspeedActions.SOLO_STATE.equals(ws) ? supportedSoloActions : supportedActions;
    }
    
    public List getDecoratorActions(RequestContext rc, PortletApplication pa, PortletWindow pw, PortletMode pm,
                    WindowState ws, Decoration decoration, List actionTemplates, 
                    PortletDefinitionComposite portlet, ContentFragment fragment, SecurityAccessController accessController)
    {
        int printModeIndex = actionTemplates.indexOf(PRINT_MODE_TEMPLATE);
        int soloStateIndex = actionTemplates.indexOf(SOLO_ACTION_TEMPLATE);
        
        if ( printModeIndex != -1 && soloStateIndex != -1 )
        {
            // merge "solo" state with "print" mode
            DecoratorActionTemplate soloStateTemplate = (DecoratorActionTemplate)actionTemplates.remove(soloStateIndex);
            DecoratorActionTemplate printActionTemplate = (DecoratorActionTemplate)actionTemplates.get(printModeIndex);
            printActionTemplate.setState(soloStateTemplate.getState());
            printActionTemplate.setCustomState((soloStateTemplate.getCustomState()));
        }
        else if ( soloStateIndex != -1 )
        {
            // don't provide "solo" action separately without "print" mode
            actionTemplates.remove(soloStateIndex);
        }
        // else if (printModeIndex != -1)
        //   support switching to different modes once in "solo" state, even back to "print"
        
        String adminRoleName = "admin";
        
        if (this.configuration != null)
        {
            adminRoleName = this.configuration.getString(PortalConfigurationConstants.ROLES_DEFAULT_ADMIN, adminRoleName);
        }
        
        // Remove editDefaultsMode if the user does not have admin role.
        int editDefaultsModeIndex = actionTemplates.indexOf(EDIT_DEFAULTS_MODE_TEMPLATE);
        if (editDefaultsModeIndex != -1)
        {
            if (!rc.getRequest().isUserInRole(adminRoleName))
            {
                actionTemplates.remove(editDefaultsModeIndex);
            }
        }
        
        return super.getDecoratorActions(rc,pa,pw,pm,ws,decoration,actionTemplates, portlet,  fragment, accessController);
    }
    
    protected DecoratorAction createAction(RequestContext rc, PortletWindow pw, Decoration decoration,
                    DecoratorActionTemplate template)
    {
        DecoratorAction action = super.createAction(rc,pw,decoration,template);
        if ( template.getState() != null && JetspeedActions.SOLO_STATE.equals(template.getState()))
        {
            // "solo" opens in a new popup winodw
            action.setTarget("_blank");
        }
        return action;
    }
}
