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
package org.apache.portals.bridges.frameworks;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.ReadOnlyException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;

import org.apache.commons.beanutils.BeanUtils;

import org.apache.portals.bridges.frameworks.model.ModelBean;
import org.apache.portals.bridges.frameworks.model.PortletApplicationModel;
import org.apache.portals.bridges.frameworks.spring.PortletApplicationModelImpl;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;

/**
 * SpringVelocityPortlet
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id: GenericFrameworkPortlet.java,v 1.1 2004/11/04 18:09:33 taylor
 *          Exp $
 */
public class GenericFrameworkPortlet extends GenericVelocityPortlet
{
    /**
     * Init Parameter: default spring configuration property
     */
    private static final String INITPARAM_SPRING_CONFIG = "spring-configuration";

    /**
     * Init Parameter: default velocity configuration property
     */
    private static final String INITPARAM_VALIDATOR_CONFIG = "validator-configuration";

    private static final String PREFS_SUFFIX = ".prefs";

    private static final String SESSION_ERROR_MESSAGES = "portals.bridges.framework.errors";
    /**
     * Action signature for calling velocity portlet actions
     */
    private static final Class[] VELOCITY_PORTLET_ACTION_SIGNATURE =
    { ActionRequest.class, ActionResponse.class};

    private static PortletApplicationModel model = null;

    private static Object semaphore = new Object();

    public GenericFrameworkPortlet()
    {
    }

    public void setExternalSupport(Map map)
    {
        model.setExternalSupport(map);
    }
    
    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
        String springConfig = this.getInitParameter(INITPARAM_SPRING_CONFIG);
        if (springConfig == null) { throw new PortletException("Spring Configuration file not specified"); }

        String validatorConfig = this.getInitParameter(INITPARAM_VALIDATOR_CONFIG);

        synchronized (semaphore)
        {
            if (null == model)
            {
                model = new PortletApplicationModelImpl(springConfig, validatorConfig);
                model.init(config);
            }
        }
    }

    /**
     * Invoke the velocity portlet pipeline: (1) determine the logical view (2)
     * restore state from Form to Bean (3) validate the bean -- or -- (2)
     * restore state from Form to Prefs
     * 
     * (4) execute the velocity action (5) forward to another view
     *  
     */
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException
    {
        // (1) Determine the current view
        String view = determineLogicalView(request);

        Object bean = null;
        ModelBean mb = model.getModelBean(view);

        if (mb.getBeanType() == ModelBean.PREFS_MAP)
        {
            // (2) restore state from Form to Prefs
            bean = formToPrefs(request, view, mb);
        }
        else
        {
            // (2) restore state from Form to Bean
            bean = formToBean(request, view, mb);
        }

        String forward = null;

        // (3) validate the bean
        ResourceBundle bundle = this.getPortletConfig().getResourceBundle(request.getLocale());
        Map errors = model.validate(bean, view, bundle);
        if (errors.isEmpty())
        {
            request.getPortletSession().removeAttribute(SESSION_ERROR_MESSAGES, PortletSession.PORTLET_SCOPE);
            
            // (4) execute the velocity action
            String action = request.getParameter(FrameworkConstants.BRIDGES_ACTION);
            if (null == action)
            {
                if (mb.getBeanType() == ModelBean.PREFS_MAP)
                {
                    // store prefs
                    storePreferences(request, (Map) bean);
                }

                forward = model.getForward(view, ForwardConstants.SUCCESS);
            }
            else
            {
                // call the specified action in the post params
                String actionForward = invokeVelocityPortletAction(action, request, response);
                forward = model.getForward(actionForward);
            }
        }
        else
        {
            // failed validation
            request.getPortletSession().setAttribute(SESSION_ERROR_MESSAGES, errors, PortletSession.PORTLET_SCOPE);
            forward = model.getForward(view, ForwardConstants.FAILURE);
        }

        // (5) forward to another view
        forwardToView(request, response, forward);

    }

    protected void forwardToView(ActionRequest request, ActionResponse response, String forward)
    {
        if (forward == null) { return; } // stay on same page

        String logicalView = null;
        PortletMode newMode = null;
        StringTokenizer tokenizer = new StringTokenizer(forward, ForwardConstants.DELIMITER);
        while (tokenizer.hasMoreTokens())
        {
            String token = tokenizer.nextToken();
            if (token.startsWith(ForwardConstants.MODE_PREFIX))
            {
                newMode = setPortletMode(response, token.substring(ForwardConstants.MODE_PREFIX.length()));
            }
            else if (token.startsWith(ForwardConstants.STATE_PREFIX))
            {
                setWindowState(response, token.substring(ForwardConstants.STATE_PREFIX.length()));
            }
            else
            {
                logicalView = token;
            }
        }
        if (logicalView != null)
        {
            setLogicalView(request, response, logicalView, newMode);
        }

    }

    private void setWindowState(ActionResponse response, String forward)
    {
        try
        {
            if (forward.equals(ForwardConstants.MAXIMIZED))
            {
                response.setWindowState(WindowState.MAXIMIZED);
            }
            else if (forward.equals(ForwardConstants.MINIMIZED))
            {
                response.setWindowState(WindowState.MINIMIZED);
            }
            else if (forward.equals(ForwardConstants.NORMAL))
            {
                response.setWindowState(WindowState.NORMAL);
            }
        }
        catch (WindowStateException e)
        {
        }
    }

    private PortletMode setPortletMode(ActionResponse response, String forward)
    {
        PortletMode mode = null;
        try
        {
            if (forward.equals(ForwardConstants.VIEW))
            {
                response.setPortletMode(PortletMode.VIEW);
                mode = PortletMode.VIEW;
            }
            else if (forward.equals(ForwardConstants.EDIT))
            {
                response.setPortletMode(PortletMode.EDIT);
                mode = PortletMode.EDIT;
            }
            else if (forward.equals(ForwardConstants.HELP))
            {
                response.setPortletMode(PortletMode.HELP);
                mode = PortletMode.HELP;
            }
        }
        catch (PortletModeException e)
        {
        }
        return mode;
    }

    protected void storePreferences(PortletRequest request, Map bean) throws IOException, PortletException
    {
        String key = "none";

        try
        {
            PortletPreferences prefs = request.getPreferences();
            Iterator it = bean.entrySet().iterator();
            while (it.hasNext())
            {
                Map.Entry entry = (Map.Entry) it.next();
                key = (String) entry.getKey();
                if (!prefs.isReadOnly(key))
                {
                    prefs.setValue(key, (String) entry.getValue());
                }
            }
            prefs.store();
        }
        catch (ReadOnlyException roe)
        {
            throw new PortletException("Failed to set preference " + key + ", value is readonly");
        }

    }

    /**
     * Get the current logical view based on velocity.view request parameter If
     * the request parameter is not found, fall back to init param
     * 
     * @param request
     * @return the current view
     * @throws PortletException
     */
    protected String determineLogicalView(PortletRequest request) throws PortletException
    {
        String view = null;
        if (request.getPortletMode().equals(PortletMode.VIEW))
        {
            view = request.getParameter(FrameworkConstants.VIEW_VIEW_MODE);
            if (view == null)
            {
                view = this.getDefaultViewPage();
            }
        }
        else if (request.getPortletMode().equals(PortletMode.EDIT))
        {
            view = request.getParameter(FrameworkConstants.VIEW_EDIT_MODE);
            if (view == null)
            {
                view = this.getDefaultEditPage();
            }
        }
        else if (request.getPortletMode().equals(PortletMode.HELP))
        {
            view = request.getParameter(FrameworkConstants.VIEW_HELP_MODE);
            if (view == null)
            {
                view = this.getDefaultHelpPage();
            }
        }
        if (null == view) { throw new PortletException("Portlet error: cant find view resource for portlet: "
                + this.getPortletName()); }
        return view;
    }

    protected void setLogicalView(ActionRequest request, ActionResponse response, String view, PortletMode newMode)
    {
        if (newMode == null)
        {
            if (request.getPortletMode().equals(PortletMode.VIEW))
            {
                if (view == null || view.equals(this.getDefaultViewPage()))
                {
                    // clear it
                    response.setRenderParameter(FrameworkConstants.VIEW_VIEW_MODE, view); //(String) null);
                }
                else
                {
                    response.setRenderParameter(FrameworkConstants.VIEW_VIEW_MODE, view);
                }
            }
            else if (view == null || request.getPortletMode().equals(PortletMode.EDIT))
            {
                if (view == null || view.equals(this.getDefaultEditPage()))
                {
                    // clear it
                    response.setRenderParameter(FrameworkConstants.VIEW_EDIT_MODE, view); //(String) null);
                }
                else
                {
                    response.setRenderParameter(FrameworkConstants.VIEW_EDIT_MODE, view);
                }
            }
            else if (view == null || request.getPortletMode().equals(PortletMode.HELP))
            {
                if (view == null || view.equals(this.getDefaultHelpPage()))
                {
                    response.setRenderParameter(FrameworkConstants.VIEW_HELP_MODE, view); //(String) null);
                }
                else
                {
                    response.setRenderParameter(FrameworkConstants.VIEW_HELP_MODE, view);
                }
            }
        }
        else
        {
            if (newMode.equals(PortletMode.VIEW))
            {
                if (view == null || view.equals(this.getDefaultViewPage()))
                {
                    // clear it
                    response.setRenderParameter(FrameworkConstants.VIEW_VIEW_MODE, view); //(String) null);
                }
                else
                {
                    response.setRenderParameter(FrameworkConstants.VIEW_VIEW_MODE, view);
                }
            }
            else if (newMode.equals(PortletMode.EDIT))
            {
                if (view == null || view.equals(this.getDefaultEditPage()))
                {
                    // clear it
                    response.setRenderParameter(FrameworkConstants.VIEW_EDIT_MODE, view); //(String) null);
                }
                else
                {
                    response.setRenderParameter(FrameworkConstants.VIEW_EDIT_MODE, view);
                }
            }
            else if (newMode.equals(PortletMode.HELP))
            {
                if (view == null || view.equals(this.getDefaultHelpPage()))
                {
                    response.setRenderParameter(FrameworkConstants.VIEW_HELP_MODE, view); //(String) null);
                }
                else
                {
                    response.setRenderParameter(FrameworkConstants.VIEW_HELP_MODE, view);
                }
            }
        }
    }

    protected Object formToBean(ActionRequest request, String view, ModelBean mb) throws PortletException
    {

        // try to get the bean from the session first
        Object bean = getBeanFromSession(request, mb); 
        if (bean == null)
        {
            bean = model.createBean(mb);
            if (bean == null) { throw new PortletException("Portlet Action error in creating bean for view: " + view); }
            putBeanInSession(request, mb, bean);
        }

        Map params = request.getParameterMap();
        try
        {
            BeanUtils.populate(bean, params);
        }
        catch (Exception e)
        {
            throw new PortletException("Portlet Action error in  populating bean: " + mb.getBeanName(), e);
        }
        return bean;
    }

    protected Object formToPrefs(ActionRequest request, String view, ModelBean mb) throws PortletException
    {
        Map params = request.getParameterMap();
        Map bean = (Map) request.getPortletSession().getAttribute(view + PREFS_SUFFIX);
        if (bean == null)
        {
            PortletPreferences prefs = request.getPreferences();

            bean = model.createPrefsBean(mb, prefs.getMap());

            request.getPortletSession().setAttribute(view + PREFS_SUFFIX, bean);
        }

        try
        {
            Iterator it = params.entrySet().iterator();
            while (it.hasNext())
            {
                Map.Entry entry = (Map.Entry) it.next();
                Object value = entry.getValue();
                String key = (String) entry.getKey();
                if (null == bean.get(key))
                {
                    continue;
                }
                if (value instanceof String)
                {
                    bean.put(key, value);
                }
                else if (value instanceof String[])
                {
                    bean.put(key, ((String[]) value)[0]);
                }
            }
        }
        catch (Exception e)
        {
            throw new PortletException("Portlet Action error in  populating bean: ", e);
        }
        return bean;
    }

    /**
     * Invokes a specific Velocity Portlet Action All portlet actions must have
     * the signature:
     * 
     * String methodName(ActionRequest request, ActionResponse response)
     * 
     * @param methodName
     */
    protected String invokeVelocityPortletAction(String methodName, ActionRequest request, ActionResponse response)
            throws PortletException
    {
        try
        {
            Method method = this.getClass().getMethod(methodName, VELOCITY_PORTLET_ACTION_SIGNATURE);
            Object[] parameters =
            { request, response};
            String result = (String) method.invoke(this, parameters);
            return result;
        }
        catch (Exception e)
        {
            throw new PortletException("Failed to invoke portlet action: " + methodName, e);
        }
    }

    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        doRender(request, response);
    }

    public void doHelp(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        doRender(request, response);
    }

    public void doEdit(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        doRender(request, response);
    }

    protected void doRender(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        String view = determineLogicalView(request);
        if (view == null) { throw new PortletException("Logical View not found: " + view); }

        String template = model.getTemplate(view);
        if (template == null) { throw new PortletException("Template not found for Logical View: " + view); }

        ModelBean mb = model.getModelBean(view);
        switch (mb.getBeanType())
        {
        case ModelBean.PREFS_MAP:
            preferencesToContext(request, view, mb);
            break;
        case ModelBean.POJO:
            beanToContext(request, view, mb);
            break;
        }
        putRequestVariable(request, FrameworkConstants.FORWARD_TOOL, new Forwarder(model, request, response));
        Map errors = (Map)request.getPortletSession().getAttribute(SESSION_ERROR_MESSAGES, PortletSession.PORTLET_SCOPE);
        if (errors != null)
        {            
            putRequestVariable(request, "ERRORS", errors);
        }
        request.setAttribute(FrameworkConstants.MODEL_TOOL, model);

        PortletContext context = getPortletContext();
        PortletRequestDispatcher rd = context.getRequestDispatcher(template);
        rd.include(request, response);
    }

    private void beanToContext(RenderRequest request, String view, ModelBean mb)
    {
        Object bean;
        
        String key = (String)request.getAttribute(mb.getLookupKey());
        if (key != null)
        {
            bean = model.lookupBean(mb, key);
        }
        else
        {
            bean = getBeanFromSession(request, mb);
        }
        if (bean == null)
        {
            bean = model.createBean(mb);
            if (bean == null) { return; }
            putBeanInSession(request, mb, bean);
        }
        putRequestVariable(request, mb.getBeanName(), bean);
    }

    private void preferencesToContext(RenderRequest request, String view, ModelBean mb)
    {
        Map bean = (Map) request.getPortletSession().getAttribute(view + PREFS_SUFFIX);
        if (bean == null)
        {
            PortletPreferences prefs = request.getPreferences();
            bean = model.createPrefsBean(mb, prefs.getMap());
            putBeanInSession(request, mb, bean);
        }
        putRequestVariable(request, FrameworkConstants.PREFS_VARIABLE, bean);
    }

    
    private Object getBeanFromSession(PortletRequest request, ModelBean mb)
    {
        return request.getPortletSession().getAttribute(makeModelBeanKey(mb));
    }

    private void putBeanInSession(PortletRequest request, ModelBean mb, Object bean)
    {
        if (bean instanceof Serializable)
        {
            request.getPortletSession().setAttribute(makeModelBeanKey(mb), bean);
        }
    }
    
    private String makeModelBeanKey(ModelBean mb)
    {
        return "ModelBean:" + mb.getBeanName();
    }
    
    /**
     * Specific for Velocity
     * 
     * @param name
     * @param value
     */
    protected void putRequestVariable(RenderRequest request, String name, Object value)
    {
        request.setAttribute(name, value);
    }

}