/*
 * Copyright 2006 The Apache Software Foundation.
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
package ${groupId};

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

/**
 * ApplicationPortlet
 *
 * @author <a href="mailto:"></a>
 * @version $Id:$
 */
public class ApplicationPortlet extends GenericVelocityPortlet
{
    /**
     * Class specific log instance.
     */
    private final static Log log = LogFactory.getLog(ApplicationPortlet.class);

    /**
     * ApplicationPortlet constructor.
     */
    public ApplicationPortlet()
    {
        super();
    }
    
    /**
     * Called by the portlet container to indicate to a portlet that the 
     * portlet is being placed into service.
     *
     * The portlet container calls the init
     * method exactly once after instantiating the portlet.
     * The init method must complete successfully
     * before the portlet can receive any requests.
     *
     * The portlet container cannot place the portlet into service
     * if the init method
     * 
     * - Throws a PortletException
     * - Does not return within a time period defined by the portlet container.
     *
     * @param config a PortletConfig object 
     *               containing the portlet's
     *               configuration and initialization parameters
     * @exception PortletException if an exception has occurred that
     *                             interferes with the portlet's normal
     *                             operation.
     * @exception UnavailableException if the portlet cannot perform
     *                                 the initialization at this time.
     *
     */
    public void init(PortletConfig config)
        throws PortletException
    {
        // save config and invoke init()
        super.init(config);
    }

    /**
     *
     * A convenience method which can be overridden so that there's no need
     * to call super.init(config).
     *
     * Instead of overriding init(PortletConfig), simply override
     * this method and it will be called by
     * GenericPortlet.init(PortletConfig config).
     * The PortletConfig object can still be retrieved via getPortletConfig. 
     *
     * @exception PortletException if an exception has occurred that
     *                             interferes with the portlet normal
     *                             operation.
     * @exception UnavailableException if the portlet is unavailable to perform init
     */    
    public void init()
        throws PortletException
    {
        getPortletContext().log("ApplicationPortlet.init() invoked...");
        log.debug("ApplicationPortlet.init() invoked...");

        super.init();
    }
    
    /**
     * Called by the portlet container to allow the portlet to process
     * an action request. This method is called if the client request was
     * originated by a URL created (by the portlet) with the 
     * RenderResponse.createActionURL() method.
     * 
     * Typically, in response to an action request, a portlet updates state 
     * based on the information sent in the action request parameters.
     * In an action the portlet may:
     * 
     * - issue a redirect
     * - change its window state
     * - change its portlet mode
     * - modify its persistent state
     * - set render parameters
     * 
     * 
     * A client request triggered by an action URL translates into one 
     * action request and many render requests, one per portlet in the portal page.
     * The action processing must be finished before the render requests
     * can be issued.
     *
     * @param request the action request
     * @param response the action response
     * @exception PortletException if the portlet has problems fulfilling the
     *                              request
     * @exception UnavailableException if the portlet is unavailable to process
     *                                  the action at this time
     * @exception PortletSecurityException if the portlet cannot fullfill this
     *                                      request because of security reasons
     * @exception IOException if the streaming causes an I/O problem
     */
    public void processAction(ActionRequest request, ActionResponse response)
        throws PortletException, IOException
    {
        getPortletContext().log("ApplicationPortlet.processAction() invoked...");
        log.debug("ApplicationPortlet.processAction() invoked...");

        // precess action from request parameters
        Enumeration requestParametersEnum = request.getParameterNames();
        while (requestParametersEnum.hasMoreElements())
        {
            String name = (String) requestParametersEnum.nextElement();
            String [] values = request.getParameterValues(name);

            // log.debug, redirect, or side effect various parameters, attributes,
            // and preferences
            if (name.equals("test-action") && (values.length == 1) && (values[0] != null))
            {
                getPortletContext().log("ApplicationPortlet.processAction() received action: "+name+" value="+values[0]);
                log.debug("ApplicationPortlet.processAction() received action: "+name+" value="+values[0]);
            }
            else if (name.equals("redirect-action") && (values.length == 1) && (values[0] != null))
            {
                getPortletContext().log("ApplicationPortlet.processAction() received action: "+name+" url="+values[0]);
                log.debug("ApplicationPortlet.processAction() received action: "+name+" url="+values[0]);
                if (values[0].startsWith("http://") || values[0].startsWith("https://"))
                {
                    response.sendRedirect(values[0]);
                    return ;
                }
            }
            else if (name.equals("edit-context-attributes-name-action") && (values.length == 1) && (values[0] != null))
            {
                String [] attributeValues = request.getParameterValues("edit-context-attributes-value-action");
                if ((attributeValues != null) && (attributeValues.length == 1) && (attributeValues[0] != null))
                {
                    getPortletContext().log("ApplicationPortlet.processAction() received action: "+name+", name="+values[0]+", value="+attributeValues[0]);
                    log.debug("ApplicationPortlet.processAction() received action: "+name+", name="+values[0]+", value="+attributeValues[0]);
                    getPortletContext().setAttribute(values[0], attributeValues[0]);
                }
                else
                {
                    getPortletContext().log("ApplicationPortlet.processAction() received action: "+name+", name="+values[0]);
                    log.debug("ApplicationPortlet.processAction() received action: "+name+", name="+values[0]);
                    getPortletContext().removeAttribute(values[0]);
                }
            }
            else if (name.equals("edit-preferences-name-action") && (values.length == 1) && (values[0] != null))
            {
                PortletPreferences prefs = request.getPreferences();
                String [] preferenceValues = request.getParameterValues("edit-preferences-value-action");
                if ((preferenceValues != null) && (preferenceValues.length == 1))
                {
                    preferenceValues = splitArrayValues(preferenceValues[0]);
                }
                if ((preferenceValues != null) && (preferenceValues.length > 0))
                {
                    getPortletContext().log("ApplicationPortlet.processAction() received action: "+name+", name="+values[0]+", value="+concatArrayValues(preferenceValues));
                    log.debug("ApplicationPortlet.processAction() received action: "+name+", name="+values[0]+", value="+concatArrayValues(preferenceValues));
                    if (! prefs.isReadOnly(values[0]))
                    {
                        prefs.setValues(values[0], preferenceValues);
                        try
                        {
                            prefs.store();
                        }
                        catch (Exception e)
                        {
                            getPortletContext().log("ApplicationPortlet.processAction() preferences store exception caught", e);
                            log.debug("ApplicationPortlet.processAction() preferences store exception caught", e);
                        }
                    }
                    else
                    {
                        getPortletContext().log("ApplicationPortlet.processAction() read only preference: "+values[0]);
                        log.debug("ApplicationPortlet.processAction() read only preference: "+values[0]);
                    }
                }
                else
                {
                    getPortletContext().log("ApplicationPortlet.processAction() received action: "+name+", name="+values[0]);
                    if (! prefs.isReadOnly(values[0]))
                    {
                        prefs.reset(values[0]);
                        try
                        {
                            prefs.store();
                        }
                        catch (Exception e)
                        {
                            getPortletContext().log("ApplicationPortlet.processAction() preferences store exception caught", e);
                            log.debug("ApplicationPortlet.processAction() preferences store exception caught", e);
                        }
                    }
                    else
                    {
                        getPortletContext().log("ApplicationPortlet.processAction() read only preference: "+values[0]);
                        log.debug("ApplicationPortlet.processAction() read only preference: "+values[0]);
                    }
                }
            }
            else if (name.equals("edit-session-application-attributes-name-action") && (values.length == 1) && (values[0] != null))
            {
                PortletSession session = request.getPortletSession(false);
                String [] attributeValues = request.getParameterValues("edit-session-application-attributes-value-action");
                if ((attributeValues != null) && (attributeValues.length == 1) && (attributeValues[0] != null))
                {
                    getPortletContext().log("ApplicationPortlet.processAction() received action: "+name+", name="+values[0]+", value="+attributeValues[0]);
                    log.debug("ApplicationPortlet.processAction() received action: "+name+", name="+values[0]+", value="+attributeValues[0]);
                    session.setAttribute(values[0], attributeValues[0], PortletSession.APPLICATION_SCOPE);
                }
                else
                {
                    getPortletContext().log("ApplicationPortlet.processAction() received action: "+name+", name="+values[0]);
                    log.debug("ApplicationPortlet.processAction() received action: "+name+", name="+values[0]);
                    session.removeAttribute(values[0], PortletSession.APPLICATION_SCOPE);
                }
            }
            else if (name.equals("edit-session-portlet-attributes-name-action") && (values.length == 1) && (values[0] != null))
            {
                PortletSession session = request.getPortletSession(false);
                String [] attributeValues = request.getParameterValues("edit-session-portlet-attributes-value-action");
                if ((attributeValues != null) && (attributeValues.length == 1) && (attributeValues[0] != null))
                {
                    getPortletContext().log("ApplicationPortlet.processAction() received action: "+name+", name="+values[0]+", value="+attributeValues[0]);
                    log.debug("ApplicationPortlet.processAction() received action: "+name+", name="+values[0]+", value="+attributeValues[0]);
                    session.setAttribute(values[0], attributeValues[0], PortletSession.PORTLET_SCOPE);
                }
                else
                {
                    getPortletContext().log("ApplicationPortlet.processAction() received action: "+name+", name="+values[0]);
                    log.debug("ApplicationPortlet.processAction() received action: "+name+", name="+values[0]);
                    session.removeAttribute(values[0], PortletSession.PORTLET_SCOPE);
                }
            }
            else if (name.equals("edit-request-attributes-name-action") && (values.length == 1) && (values[0] != null))
            {
                String [] attributeValues = request.getParameterValues("edit-request-attributes-value-action");
                if ((attributeValues != null) && (attributeValues.length == 1) && (attributeValues[0] != null))
                {
                    getPortletContext().log("ApplicationPortlet.processAction() received action: "+name+", name="+values[0]+", value="+attributeValues[0]);
                    log.debug("ApplicationPortlet.processAction() received action: "+name+", name="+values[0]+", value="+attributeValues[0]);
                    request.setAttribute(values[0], attributeValues[0]);
                }
                else
                {
                    getPortletContext().log("ApplicationPortlet.processAction() received action: "+name+", name="+values[0]);
                    log.debug("ApplicationPortlet.processAction() received action: "+name+", name="+values[0]);
                    request.removeAttribute(values[0]);
                }
            }
            else if (name.equals("set-render-parameters-name-action") && (values.length == 1) && (values[0] != null))
            {
                String [] parameterValues = request.getParameterValues("set-render-parameters-value-action");
                if ((parameterValues != null) && (parameterValues.length == 1))
                {
                    parameterValues = splitArrayValues(parameterValues[0]);
                }
                if ((parameterValues != null) && (parameterValues.length > 0))
                {
                    getPortletContext().log("ApplicationPortlet.processAction() received action: "+name+", name="+values[0]+", value="+concatArrayValues(parameterValues));
                    log.debug("ApplicationPortlet.processAction() received action: "+name+", name="+values[0]+", value="+concatArrayValues(parameterValues));
                    response.setRenderParameter(values[0], parameterValues);
                }
            }

            // propagate action request parameters as render parameters
            response.setRenderParameter((name.startsWith("ACTION-PARAMETER-") ? name : "ACTION-PARAMETER-"+name), values);
        }
    }
    
    /**
     * Called by the portlet container to allow the portlet to generate
     * the content of the response based on its current state.
     *
     * @param request the render request
     * @param response the render response
     * @exception PortletException if the portlet has problems fulfilling the
     *                             rendering request
     * @exception UnavailableException if the portlet is unavailable to
     *                                 perform render at this time
     * @exception PortletSecurityException if the portlet cannot fullfill this
     *                                     request because of security reasons
     * @exception java.io.IOException if the streaming causes an I/O problem
     */
    public void render(RenderRequest request, RenderResponse response)
        throws PortletException, IOException
    {
        // invoke getTitle() and doDispatch()
        super.render(request, response);
    }
    
    /**
     * Used by the render method to get the title.
     * 
     * The default implementation gets the title from the ResourceBundle
     * of the PortletConfig of the portlet. The title is retrieved
     * using the 'javax.portlet.title' resource name.
     * 
     * Portlets can overwrite this method to provide dynamic
     * titles (e.g. based on locale, client, and session information).
     * Examples are:
     * 
     * - language-dependant titles for multi-lingual portals
     * - shorter titles for WAP phones
     * - the number of messages in a mailbox portlet
     * 
     * @return the portlet title for this window
     */
    protected String getTitle(RenderRequest request)
    {
        // return "javax.portlet.title" resource using request locale
        return super.getTitle(request);
    }
    
    /**
     * The default implementation of this method routes the render request
     * to a set of helper methods depending on the current portlet mode the
     * portlet is currently in.
     * These methods are:
     * 
     * - doView for handling view requests
     * - doEdit for handling edit requests
     * - doHelp for handling help requests
     * 
     * If the window state of this portlet is minimized, this
     * method does not invoke any of the portlet mode rendering methods.
     * 
     * For handling custom portlet modes the portlet should override this
     * method.
     *
     * @param request the render request
     * @param response the render response
     * @exception PortletException if the portlet cannot fulfilling the request
     * @exception UnavailableException if the portlet is unavailable to perform
     *                                 render at this time
     * @exception PortletSecurityException if the portlet cannot fullfill this
     *                                     request because of security reasons
     * @exception java.io.IOException if the streaming causes an I/O problem
     *
     * @see #doView(RenderRequest, RenderResponse)
     * @see #doEdit(RenderRequest, RenderResponse)
     * @see #doHelp(RenderRequest, RenderResponse)
     */
    protected void doDispatch(RenderRequest request, RenderResponse response)
        throws PortletException, IOException
    {
        // invoke doView(), doEdit(), or doHelp() based on window state and portlet mode
        super.doDispatch(request, response);
    }

    /**
     * Helper method to serve up the mandatory view mode.
     * 
     * The default implementation throws an exception.
     *
     * @param request the portlet request
     * @param response the render response
     * @exception PortletException if the portlet cannot fulfilling the request
     * @exception UnavailableException if the portlet is unavailable to perform
     *                                 render at this time
     * @exception PortletSecurityException if the portlet cannot fullfill this
     *                                     request because of security reasons
     * @exception java.io.IOException if the streaming causes an I/O problem
     */
    public void doView(RenderRequest request, RenderResponse response)
        throws PortletException, IOException
    {
        getPortletContext().log("ApplicationPortlet.doView() invoked...");
        log.debug("ApplicationPortlet.doView() invoked...");

        // configure velocity context and invoke appropriate template
        Context context = getContext(request);
        context.put("portlet", this);
        super.doView(request,response);
    }

    /**
     * Helper method to serve up the edit mode.
     * 
     * The default implementation throws an exception.
     *
     * @param request the portlet request
     * @param response the render response
     * @exception PortletException if the portlet cannot fulfilling the request
     * @exception UnavailableException if the portlet is unavailable to perform
     *                                 render at this time
     * @exception PortletSecurityException if the portlet cannot fullfill this
     *                                     request because of security reasons
     * @exception java.io.IOException if the streaming causes an I/O problem
     */
    public void doEdit(RenderRequest request, RenderResponse response)
        throws PortletException, IOException
    {
        getPortletContext().log("ApplicationPortlet.doEdit() invoked...");
        log.debug("ApplicationPortlet.doEdit() invoked...");

        // dynamically set title
        response.setTitle("EDIT - Application Portlet Title");

        // configure velocity context and invoke appropriate template
        Context context = getContext(request);
        context.put("portlet", this);
        super.doEdit(request,response);
    }

    /**
     * Helper method to serve up the help mode.
     * 
     * The default implementation throws an exception.
     *
     * @param request the portlet request
     * @param response the render response
     * @exception PortletException if the portlet cannot fulfilling the request
     * @exception UnavailableException if the portlet is unavailable to perform
     *                                 render at this time
     * @exception PortletSecurityException if the portlet cannot fullfill this
     *                                     request because of security reasons
     * @exception java.io.IOException if the streaming causes an I/O problem
     */
    public void doHelp(RenderRequest request, RenderResponse response)
        throws PortletException, IOException
    {
        getPortletContext().log("ApplicationPortlet.doHelp() invoked...");
        log.debug("ApplicationPortlet.doHelp() invoked...");

        // dynamically set title
        response.setTitle("HELP - Application Portlet Title");

        // configure velocity context and invoke appropriate template
        Context context = getContext(request);
        context.put("portlet", this);
        super.doHelp(request,response);
    }

    /**
     * Called by the portlet container to indicate to a portlet that the
     * portlet is being taken out of service.  
     * 
     * Before the portlet container calls the destroy method, it should 
     * allow any threads that are currently processing requests within 
     * the portlet object to complete execution. To avoid
     * waiting forever, the portlet container can optionally wait for 
     * a predefined time before destroying the portlet object.
     *
     * This method enables the portlet to do the following:
     * 
     * - clean up any resources that it holds (for example, memory,
     * file handles, threads) 
     * - make sure that any persistent state is
     * synchronized with the portlet current state in memory.
     * 
     */
    public void destroy()
    {
        getPortletContext().log("ApplicationPortlet.destroy() invoked...");
        log.debug("ApplicationPortlet.destroy() invoked...");

        super.destroy();
    }

    // utilities

    /**
     * Convert String to String array.
     *
     * @param values string to split
     * @return split array of strings
     */
    public String [] splitArrayValues(String values)
    {
        if (values != null)
        {
            return values.split(",");
        }
        return null;
    }

    /**
     * Concatinate String array to String.
     *
     * @param values array of string to concatinate
     * @return concatinated string
     */
    public String concatArrayValues(String [] values)
    {
        if (values != null)
        {
            StringBuffer concat = new StringBuffer();
            if (values.length > 0)
            {
                concat.append(values[0]);
            }
            for (int i = 1; (i < values.length); i++)
            {
                concat.append(',');
                concat.append(values[i]);
            }
            return concat.toString();
        }
        return null;
    }

    /**
     * Concatinate String Enumeration to String.
     *
     * @param values enumeration of strings to concatinate
     * @return concatinated string
     */
    public String concatArrayValues(Enumeration values)
    {
        if (values != null)
        {
            StringBuffer concat = new StringBuffer();
            if (values.hasMoreElements())
            {
                concat.append(values.nextElement());
            }
            while (values.hasMoreElements())
            {
                concat.append(',');
                concat.append(values.nextElement());
            }
            return concat.toString();
        }
        return null;
    }
}
