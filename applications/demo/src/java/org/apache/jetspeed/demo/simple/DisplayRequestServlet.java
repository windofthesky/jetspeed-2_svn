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
package org.apache.jetspeed.demo.simple;

import org.apache.jetspeed.portlet.ServletPortlet;

/**
 * This class only exists to maintain the Help and View page names.  As soon
 * as the container/engine will retain the preferences this class can be
 * replaced by configuring portlet preferences.
 *
 * @version $Id$
 * @task Remove this class when the container/engine retain preferences
 */
public class DisplayRequestServlet extends ServletPortlet
{
    /** 
     * Default action page when preference does not exist
     *
     * @see org.apache.jetspeed.portlet.ServletPortlet#processAction
     */
    private static final String DEFAULT_ACTION_PAGE = null;

    /** 
     * Default custom page when preference does not exist
     *
     * @see org.apache.jetspeed.portlet.ServletPortlet#doCustom
     */
    private static final String DEFAULT_CUSTOM_PAGE = null;

    /** 
     * Default edit page when preference does not exist
     *
     * @see org.apache.jetspeed.portlet.ServletPortlet#doEdit
     */
    private static final String DEFAULT_EDIT_PAGE = null;

    /** 
     * Default help page when preference does not exist
     *
     * @see org.apache.jetspeed.portlet.ServletPortlet#doHelp
     */
    private static final String DEFAULT_HELP_PAGE = "/WEB-INF/demo/simple/DisplayRequestHelp.jsp";
    
    /** 
     * Default help page when preference does not exist
     *
     * @see org.apache.jetspeed.portlet.ServletPortlet#doView
     */

    private static final String DEFAULT_VIEW_PAGE = "/WEB-INF/demo/simple/DisplayRequest.jsp";
    
    /** 
     * Set default page values when class is created
     */
    public DisplayRequestServlet ()
    {
        setDefaultActionPage(DEFAULT_ACTION_PAGE);
        setDefaultCustomPage(DEFAULT_CUSTOM_PAGE);
        setDefaultEditPage(DEFAULT_EDIT_PAGE);
        setDefaultHelpPage(DEFAULT_HELP_PAGE);
        setDefaultViewPage(DEFAULT_VIEW_PAGE);
    }
}
