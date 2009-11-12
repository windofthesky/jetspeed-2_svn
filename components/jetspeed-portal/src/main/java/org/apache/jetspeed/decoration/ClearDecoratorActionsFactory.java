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
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.SecurityAccessController;
import org.apache.jetspeed.container.PortletWindow;

public class ClearDecoratorActionsFactory extends AbstractDecoratorActionsFactory
{
    private static final DecoratorActionTemplate PRINT_MODE_TEMPLATE = new DecoratorActionTemplate(JetspeedActions.PRINT_MODE);
    private static final DecoratorActionTemplate SOLO_ACTION_TEMPLATE = new DecoratorActionTemplate(JetspeedActions.SOLO_STATE);
    
    private final List supportedActions;
    private final List supportedSoloActions;
    
    public ClearDecoratorActionsFactory()
    {
        ArrayList list = new ArrayList();
        supportedActions = Collections.unmodifiableList(list);
        supportedSoloActions = Collections.unmodifiableList(list);
    }

    public List getSupportedActions(RequestContext rc, PortletApplication pa, PortletWindow pw, PortletMode cm,
                    WindowState ws, Decoration decoration)
    {
        // don't support any window state actions when in "solo" state
        return JetspeedActions.SOLO_STATE.equals(ws) ? supportedSoloActions : supportedActions;
    }
}
