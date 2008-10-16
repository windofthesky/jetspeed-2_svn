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

import java.util.List;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.SecurityAccessController;
import org.apache.jetspeed.container.PortletWindow;

public interface DecoratorActionsFactory
{
    List getSupportedActions(RequestContext rc, PortletApplication pa, PortletWindow pw, PortletMode pm,
                    WindowState ws, Decoration decoration);

    List getDecoratorActions(RequestContext rc, PortletApplication pa, PortletWindow pw, PortletMode pm,
                        WindowState ws, Decoration decoration, List allowedActions, PortletDefinition portlet, ContentFragment fragment, SecurityAccessController accessController);
    
    /**
     * Maximize portlet window when going into edit mode
     * @param maxOnEdit
     */
    void setMaximizeOnEdit(boolean maxOnEdit);
    
    /**
     * Maximize portlet window when going into edit mode
     * 
     * @return
     */
    public boolean getMaximizeOnEdit();
    
    /**
     * Maximize portlet window when going into config mode
     * @param maxOnConfig
     */
    void setMaximizeOnConfig(boolean maxOnConfig);
    
    /**
     * Maximize portlet window when going into edit_defaults mode
     * 
     * @return
     */
    public boolean getMaximizeOnConfig();
    
    /**
     * Maximize portlet window when going into edit_defaults mode
     * @param maxOnEditDefaults
     */
    void setMaximizeOnEditDefaults(boolean maxOnEditDefaults);
    
    /**
     * Maximize portlet window when going into edit_defaults mode
     * 
     * @return
     */
    public boolean getMaximizeOnEditDefaults();
}