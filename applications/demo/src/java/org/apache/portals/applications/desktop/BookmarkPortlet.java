/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.portals.applications.desktop;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;

import org.apache.portals.bridges.common.GenericServletPortlet;


/**
 * BookmarkPortlet
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */
public class BookmarkPortlet extends GenericServletPortlet
{
    public void processAction (ActionRequest request, ActionResponse actionResponse)
        throws PortletException, java.io.IOException 
    {
        String removeName = request.getParameter("remove");     
        if (removeName!=null) 
        { // remove
            PortletPreferences prefs = request.getPreferences();
            prefs.reset(removeName);     
            prefs.store();
        }
        String add = request.getParameter("add");           
        if (add!=null) 
        { // add                     
            PortletPreferences prefs = request.getPreferences();
            prefs.setValue(request.getParameter("name"),                           
                    request.getParameter("value"));     
            prefs.store();                          
        }
    }
}
