/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.cornerstone.framework.mvc.controller;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.cornerstone.framework.action.BaseAction;
import org.apache.cornerstone.framework.api.action.ActionException;
import org.apache.cornerstone.framework.api.action.IAction;
import org.apache.cornerstone.framework.api.action.IActionManager;
import org.apache.cornerstone.framework.api.context.IContext;
import org.apache.cornerstone.framework.core.ClassUtil;
import org.apache.cornerstone.framework.mvc.action.ActionResult;
import org.apache.cornerstone.framework.mvc.action.AuthorizationFailedActionException;
import org.apache.cornerstone.framework.mvc.action.BasePresentationAction;
import org.apache.cornerstone.framework.singleton.SingletonManager;
import org.apache.cornerstone.framework.util.Util;
import org.apache.log4j.Logger;

public class ControllerServlet extends HttpServlet
{
    public static final String REVISION = "$Revision$";

    public static final String ACTION_MANAGER_CLASS_NAME = "actionManager.className";
    public static final String SESSION_CONTEXT_CLASS_NAME = "action.sessionContext.className";
    public static final String REQUEST_CONTEXT_CLASS_NAME = "action.requestContext.className";
    public static final String ERROR_PRESENTATION_TEMPLATE = "error.presentationTemplate";

    public static final String EXCEPTION = ControllerServlet.class.getName() + ".exception";
    public static final String ERROR_MESSAGE = ControllerServlet.class.getName() + ".errorMessage";
    public static final String MESSAGE_LIST = ControllerServlet.class.getName() + ".messageList";

    public void init(ServletConfig servletConfig) throws ServletException
    {
        super.init(servletConfig);

        _config = ClassUtil.getClassConfig(getClass());

        // action manager
        String actionManagerClassName = _config.getProperty(ACTION_MANAGER_CLASS_NAME);
        if (actionManagerClassName == null)
        {
            throw new ServletException(ACTION_MANAGER_CLASS_NAME + "undefined in " + getClass().getName() + ".properties");
        }
        _actionManager = (IActionManager) SingletonManager.getSingleton(actionManagerClassName);

        // action session context
        _sessionContextClassName = _config.getProperty(SESSION_CONTEXT_CLASS_NAME);
        if (_sessionContextClassName == null)
        {
            throw new ServletException(SESSION_CONTEXT_CLASS_NAME + "undefined in " + getClass().getName() + ".properties");
        }
        _requestContextClassName = _config.getProperty(REQUEST_CONTEXT_CLASS_NAME);
        if (_requestContextClassName == null)
        {
            throw new ServletException(REQUEST_CONTEXT_CLASS_NAME + "undefined in " + getClass().getName() + ".properties");
        }

        _errorPresentationTemplate = _config.getProperty(ERROR_PRESENTATION_TEMPLATE);
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        String presentationTemplate;
        try
        {
            HttpSession session = request.getSession(true);
    
            // create action
            String pathInfo = request.getPathInfo();
            String actionName = pathInfo.substring(1);
            IAction action = createAction(actionName);
    
            // creat contexts
            IContext actionContext = (IContext) createRequestContext(_requestContextClassName, request);
            IContext sessionContext = (IContext) createSessionContext(_sessionContextClassName, session);
            actionContext.setValue(BasePresentationAction.SESSION_CONTEXT, sessionContext);
    
            // set basic data on action context
            actionContext.setValue(BaseAction.USER_NAME, request.getRemoteUser());
            actionContext.setValue(BasePresentationAction.ACTION_NAME, actionName);
            actionContext.setValue(BasePresentationAction.REQUEST, request);
            actionContext.setValue(BasePresentationAction.RESPONSE, response);
    
            // copy parameters from query string into action context
            copyRequestParameters(request, actionContext);
    
            // copy session variables from session context to action context
            copySessionVariables(sessionContext, actionContext);
    
            // invoke action
            ActionResult result = invokeAction(action, actionContext);
    
            // copy session variable from action context to session context
            copySessionVariables(actionContext, sessionContext);

            List messageList = result.getMessageList();
            request.setAttribute(MESSAGE_LIST, messageList);

            presentationTemplate = result.getExitUrl();
        }
        catch(ServletException se)
        {
            Throwable rootCause = se.getRootCause();
            if (rootCause instanceof AuthorizationFailedActionException)
            {
                request.setAttribute(ERROR_MESSAGE, rootCause.getMessage());
                presentationTemplate = _errorPresentationTemplate;                
            }
            else
            {
                request.setAttribute(EXCEPTION, se);
                presentationTemplate = _errorPresentationTemplate;
            }
        }
    
        // forward to presentation template
        if (_Logger.isDebugEnabled()) _Logger.debug("forwarding to '" + presentationTemplate + "'");
        request.getRequestDispatcher(presentationTemplate).forward(request, response);
    }

    protected Object createRequestContext(String className, HttpServletRequest request) throws ServletException
    {
        try
        {
            Class[] paramTypes = {HttpServletRequest.class};
            Class c = Class.forName(className);
            Constructor cons = c.getConstructor(paramTypes);
            Object[] params = {request};
            return cons.newInstance(params);
        }
        catch(Exception e)
        {
            throw new ServletException("failed to create request context of class '" + className + "'", e);
        }
    }

    protected Object createSessionContext(String className, HttpSession session) throws ServletException
    {
        try
        {
            Class[] paramTypes = {HttpSession.class};
            Class c = Class.forName(className);
            Constructor cons = c.getConstructor(paramTypes);
            Object[] params = {session};
            return cons.newInstance(params);
        }
        catch(Exception e)
        {
            throw new ServletException("failed to create session context of class '" + className + "'", e);
        }
    }

    protected IAction createAction(String actionName) throws ServletException
    {
        IAction action = null;
        try
        {
            action = _actionManager.createActionByName(actionName);
        }
        catch (ActionException ae)
        {
            throw new ServletException("failed to create action '" + actionName + "'", ae);
        }

        if (action == null)
        {
            throw new ServletException("action '" + actionName + "' not found");
        }

        return action;
    }

    protected ActionResult invokeAction(IAction action, IContext actionContext) throws IOException, ServletException
    {
        try
        {
            if (_Logger.isDebugEnabled()) _Logger.debug("invoking action '" + action.getName() + "'");
            return (ActionResult) action.invoke(actionContext);
        }
        catch(ActionException ae)
        {
            throw new ServletException("failed to execute action", ae.getCause());
        }
    }

    protected void copySessionVariables(IContext fromContext, IContext toContext)
    {
        for (Iterator itr = fromContext.getNameSet().iterator(); itr.hasNext();)
        {
            String name = (String) itr.next();
            if (name.startsWith(BasePresentationAction.SESSION_VARIABLE_PREFIX))
            {
                Object value = fromContext.getValue(name);
                toContext.setValue(name, value);
                if (_Logger.isDebugEnabled()) _Logger.debug("copySessionVariables: '" + name + "' copied from " + fromContext.getClass().getName() + " to " + toContext.getClass().getName());
            }
        }
    }

    protected void copyRequestParameters(HttpServletRequest request, IContext context)
    {
        for (Enumeration e = request.getParameterNames(); e.hasMoreElements();)
        {
            String name = (String) e.nextElement();
            String[] values = request.getParameterValues(name);
            if (values.length == 1)
            {
                context.setValue(name, values[0]);
                if (_Logger.isDebugEnabled()) _Logger.debug("copyRequestParameters: " + name + "='" + values[0] + "'");
            }
            else
            {
                context.setValue(name, values);
                if (_Logger.isDebugEnabled()) _Logger.debug("copyRequestParameters: " + name + "='" + Util.convertArrayToStrings(values) + "'");
            }
        }
    }

    private static Logger _Logger = Logger.getLogger(ControllerServlet.class);

    protected Properties _config;
    protected IActionManager _actionManager;
    protected String _sessionContextClassName;
    protected String _requestContextClassName;
    protected String _errorPresentationTemplate;    
}