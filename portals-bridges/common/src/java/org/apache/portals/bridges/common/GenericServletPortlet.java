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
package org.apache.portals.bridges.common;

import java.io.IOException;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * ServletPortlet will call a server, as defined by a xxxPage preference, for
 * the Action, Custom, Edit, Help, and View operations.  This allow the use of
 * existing servlets or JSPs in the portlet.  Since this is a very thin class,
 * it is up to the servlet, or JSP, to return properly formated content.  See
 * the JSR 168 for more information
 *
 * <pre>
 *  <!—- Portlet Preferences -->
 *  <portlet-preferences>
 *    <preference>
 *      <description>Action Servlet</description>
 *      <name>ActionPage</name>
 *      <value>/WEB-INF/action.do</value>
 *      <non-modifiable/>
 *    </preference>
 *    <preference>
 *      <description>Custom Servlet</description>
 *      <name>CustomPage</name>
 *      <value>/WEB-INF/custom.jsp</value>
 *      <non-modifiable/>
 *    </preference>
 *    <preference>
 *      <description>Edit Servlet</description>
 *      <name>EditPage</name>
 *      <value>/WEB-INF/edit.jsp</value>
 *      <non-modifiable/>
 *    </preference>
 *    <preference>
 *      <description>Help Servlet</description>
 *      <name>HelpPage</name>
 *      <value>/WEB-INF/help.jsp</value>
 *      <non-modifiable/>
 *    </preference>
 *    <preference>
 *      <description>View Servlet</description>
 *      <name>ViewPage</name>
 *      <value>/WEB-INF/view.jsp</value>
 *      <non-modifiable/>
 *    </preference>
 *  </portlet-preferences>
 *
 * @author  paul
 * @version $Id$
 */
public class GenericServletPortlet extends GenericPortlet
{

    /**
     * Name of portlet preference for Action page
     */
    public static final String PARAM_ACTION_PAGE = "ActionPage";

    /**
     * Name of portlet preference to allow the use of preferenecs to set pages
     */
    public static final String PARAM_ALLOW_PREFERENCES   = "AllowPreferences";

    /**
     * Name of portlet preference for Custom page
     */
    public static final String PARAM_CUSTOM_PAGE = "CustomPage";

    /**
     * Name of portlet preference for Edit page
     */
    public static final String PARAM_EDIT_PAGE   = "EditPage";

    /**
     * Name of portlet preference for Edit page
     */
    public static final String PARAM_HELP_PAGE   = "HelpPage";

    /**
     * Name of portlet preference for View page
     */
    public static final String PARAM_VIEW_PAGE   = "ViewPage";

    /**
     * Name of portlet preference for View page
     */
    public static final String PARAM_MAX_PAGE   = "MaxPage";
    
    /**
     * Allow preferences to be set by preferences.
     */
    private boolean allowPreferences = false;

    /**
     * Default URL for the action page.
     */
    private String defaultActionPage = null;

    /**
     * Default URL for the custom page.
     */
    private String defaultCustomPage = null;

    /**
     * Default URL for the edit page.
     */
    private String defaultEditPage = null;

    /**
     * Default URL for the help page.
     */
    private String defaultHelpPage = null;

    /**
     * Default URL for the view page.
     */
    private String defaultViewPage = null;

    /**
     * Creates a new instance of StrutsPortlet
     */
    public GenericServletPortlet()
    {
    }

    public void init(PortletConfig config)
    throws PortletException
    {
        super.init(config);
        this.defaultActionPage = config.getInitParameter(PARAM_ACTION_PAGE);
        this.defaultCustomPage = config.getInitParameter(PARAM_CUSTOM_PAGE);
        this.defaultEditPage = config.getInitParameter(PARAM_EDIT_PAGE);
        this.defaultViewPage = config.getInitParameter(PARAM_VIEW_PAGE);
        this.defaultHelpPage = config.getInitParameter(PARAM_HELP_PAGE);
        String allowPreferencesString = config.getInitParameter(PARAM_ALLOW_PREFERENCES);
        if (allowPreferencesString != null)
        {
            this.allowPreferences = new Boolean(allowPreferencesString).booleanValue();
        }

        if ((this.defaultActionPage == null) &&
        (this.defaultCustomPage == null) &&
        (this.defaultEditPage == null) &&
        (this.defaultViewPage == null) &&
        (this.defaultHelpPage == null) &&
        (this.allowPreferences == false) )
        {
            // This portlet is configured to do nothing!
            throw new PortletException("Portlet " + config.getPortletName() + " is incorrectly configured. No pages are defined.");
        }
    }

    /**
     * Execute the servlet as define by the init parameter or preference PARAM_ACTION_PAGE.  The value
     * if the parameter is a relative URL, i.e. /actionPage.jsp will execute the
     * JSP editPage.jsp in the portlet application's web app.  The action should
     * not generate any content.  The content will be generate by doCustom(),
     * doHelp() , doEdit(), or doView().
     *
     * See section PLT.16.2 of the JSR 168 Portlet Spec for more information
     * around executing a servlet or JSP in processAction()
     *
     * @see javax.portlet.GenericPortlet#processAction
     *
     * @task Need to be able to execute a servlet for the action
     */
    public void processAction(RenderRequest request, ActionResponse actionResponse)
    throws PortletException, IOException
    {
        String actionPage = this.defaultActionPage;
        if (this.allowPreferences == true)
        {
            PortletPreferences prefs = request.getPreferences();
            if (prefs != null)
            {
                actionPage = prefs.getValue(PARAM_ACTION_PAGE, this.defaultActionPage);
            }
        }


        if (actionPage != null)
        {
          /*
           * At this point the desire action should be execute.  See the @TASK.
           */
        }


        return;
    }

    /**
     * Execute the servlet as define by the init parameter or preference PARAM_EDIT_PAGE.  The value
     * if the parameter is a relative URL, i.e. /editPage.jsp will execute the
     * JSP editPage.jsp in the portlet application's web app.
     *
     * @see javax.portlet.GenericPortlet#doCustom
     */
    public void doCustom(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        String customPage = this.defaultCustomPage;
        if (this.allowPreferences == true)
        {
            PortletPreferences prefs = request.getPreferences();
            // allow ViewPage override by the request
            customPage = (String) request.getAttribute(PARAM_CUSTOM_PAGE);            
            
            if (prefs != null && customPage == null)
            {
                customPage = prefs.getValue(PARAM_CUSTOM_PAGE, this.defaultCustomPage);
            }
        }

        if (customPage != null)
        {
            PortletContext context = getPortletContext();
            PortletRequestDispatcher rd = context.getRequestDispatcher(customPage);
            rd.include(request, response);
        }
        return;
    }

    /**
     * Execute the servlet as define by the init parameter or preference PARAM_EDIT_PAGE.
     * The value if the parameter is a relative URL, i.e. /editPage.jsp will execute the
     * JSP editPage.jsp in the portlet application's web app.
     *
     * @see javax.portlet.GenericPortlet#doEdit
     */
    public void doEdit(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        String editPage = this.defaultEditPage;
        if (this.allowPreferences == true)
        {
            PortletPreferences prefs = request.getPreferences();
            if (prefs != null)
            {
                editPage = prefs.getValue(PARAM_EDIT_PAGE, this.defaultEditPage);
            }
        }

        if (editPage != null)
        {
            PortletContext context = getPortletContext();
            PortletRequestDispatcher rd = context.getRequestDispatcher(editPage);
            rd.include(request, response);
        }
        return;
    }

    /**
     * Execute the servlet as define by the init parameter or preference PARAM_HELP_PAGE.
     * The value if the parameter is a relative URL, i.e. /helpPage.jsp will exeute the
     * JSP helpPage.jsp in the portlet application's web app.
     *
     * @see javax.portlet.GenericPortlet#doView
     */
    public void doHelp(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        String helpPage = this.defaultHelpPage;
        if (this.allowPreferences == true)
        {
            PortletPreferences prefs = request.getPreferences();
            if (prefs != null)
            {
                helpPage = prefs.getValue(PARAM_HELP_PAGE, this.defaultHelpPage);
            }
        }

        if (helpPage != null)
        {
            PortletContext context = getPortletContext();
            PortletRequestDispatcher rd = context.getRequestDispatcher(helpPage);
            rd.include(request, response);
        }
        return;
    }

    /**
     * Execute the servlet as define by the init parameter or preference PARAM_VIEW_PAGE.
     * The value if the parameter is a relative URL, i.e. /viewPage.jsp will execute the
     * JSP viewPage.jsp in the portlet application's web app.
     *
     * @see javax.portlet.GenericPortlet#doView
     */
    public void doView(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        String viewPage = this.defaultViewPage;
        
         //	allow ViewPage override by the request
		String reqViewPage = (String) request.getAttribute(PARAM_VIEW_PAGE);
		if(reqViewPage != null)
		{
			viewPage = reqViewPage;
		}
		
        if (this.allowPreferences == true)
        {
            PortletPreferences prefs = request.getPreferences();

            
            if (prefs != null && reqViewPage == null)
            {
                viewPage = prefs.getValue(PARAM_VIEW_PAGE, this.defaultViewPage);
            }          
        }

        if (viewPage != null)
        {
            PortletContext context = getPortletContext();
            PortletRequestDispatcher rd = context.getRequestDispatcher(viewPage);
            rd.include(request, response);
        }
        return;
    }

    /**
     * Getter for property defaultViewPage.
     *
     * @return Value of property defaultViewPage.
     */
    public java.lang.String getDefaultViewPage()
    {
        return defaultViewPage;
    }

    /**
     * Setter for property defaultViewPage.
     *
     * @param defaultViewPage New value of property defaultViewPage.
     */
    public void setDefaultViewPage(java.lang.String defaultViewPage)
    {
        this.defaultViewPage = defaultViewPage;
    }

    /**
     * Getter for property defaultHelpPage.
     *
     * @return Value of property defaultHelpPage.
     */
    public java.lang.String getDefaultHelpPage()
    {
        return defaultHelpPage;
    }

    /**
     * Setter for property defaultHelpPage.
     *
     * @param defaultHelpPage New value of property defaultHelpPage.
     */
    public void setDefaultHelpPage(java.lang.String defaultHelpPage)
    {
        this.defaultHelpPage = defaultHelpPage;
    }

    /**
     * Getter for property defaultEditPage.
     *
     * @return Value of property defaultEditPage.
     */
    public java.lang.String getDefaultEditPage()
    {
        return defaultEditPage;
    }

    /**
     * Setter for property defaultEditPage.
     *
     * @param defaultEditPage New value of property defaultEditPage.
     */
    public void setDefaultEditPage(java.lang.String defaultEditPage)
    {
        this.defaultEditPage = defaultEditPage;
    }

    /**
     * Getter for property defaultCustomPage.
     *
     * @return Value of property defaultCustomPage.
     */
    public java.lang.String getDefaultCustomPage()
    {
        return defaultCustomPage;
    }

    /**
     * Setter for property defaultCustomPage.
     *
     * @param defaultCustomPage New value of property defaultCustomPage.
     */
    public void setDefaultCustomPage(java.lang.String defaultCustomPage)
    {
        this.defaultCustomPage = defaultCustomPage;
    }

    /**
     * Getter for property defaultActionPage.
     *
     * @return Value of property defaultActionPage.
     */
    public java.lang.String getDefaultActionPage()
    {
        return defaultActionPage;
    }

    /**
     * Setter for property defaultActionPage.
     *
     * @param defaultActionPage New value of property defaultActionPage.
     */
    public void setDefaultActionPage(java.lang.String defaultActionPage)
    {
        this.defaultActionPage = defaultActionPage;
    }

}
