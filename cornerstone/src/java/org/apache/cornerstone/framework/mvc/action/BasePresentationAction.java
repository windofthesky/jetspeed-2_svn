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

package org.apache.cornerstone.framework.mvc.action;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.StringTokenizer;
import org.apache.cornerstone.framework.action.BaseAction;
import org.apache.cornerstone.framework.api.action.ActionException;
import org.apache.cornerstone.framework.api.action.IAction;
import org.apache.cornerstone.framework.api.context.IContext;
import org.apache.cornerstone.framework.bean.helper.BeanHelper;
import org.apache.cornerstone.framework.bean.visitor.BeanJSConverter;
import org.apache.cornerstone.framework.bean.visitor.BeanPrinter;
import org.apache.cornerstone.framework.constant.Constant;
import org.apache.cornerstone.framework.init.Cornerstone;
import org.apache.log4j.Logger;

public abstract class BasePresentationAction extends BaseAction
{
    public static final String REVISION = "$Revision$";

    public static final String CLASS_VARIABLE_AUTHORIZER_DEFINED = "authorizerDefined";
    public static final String CONFIG_AUTHORIZER_NAME = "authorizer.name";
    public static final String CONFIG_MESSAGE_AUTHORIZATION_FAILED = "message.authorizationFailed";

    public static final String CONFIG_PRESENTATION_BEAN_TRANSIENT = "presentationBean.transient";

    public static final String ACTION_NAME = BasePresentationAction.class.getName() + ".actionName";
    public static final String REQUEST = BasePresentationAction.class.getName() + ".request";
    public static final String RESPONSE = BasePresentationAction.class.getName() + ".response";
    public static final String PREVIOUS_RESULT = BasePresentationAction.class.getName() + ".previousResult";

    public static final String MODEL_CHANGES = BasePresentationAction.class.getName() + ".modelChanges";

    public static final String EXIT = "exit";
    public static final String CONFIG_EXIT_DEFAULT = EXIT + ".default";
    public static final String CONFIG_EXIT_ERROR = EXIT + ".error";

    public static final String SESSION_CONTEXT = "sessionContext";

    public static final String JS_SUFFIX = ".js";
    public static final String TEMPLATE_SUFFIX = ".template";

    public static final String SESSION_VARIABLE_PREFIX = "session:";
    public static final String ACTION_PREFIX = "action:";

    public static final String ACTION_VARIABLE_PBEAN = SESSION_VARIABLE_PREFIX + "presentation.bean";
    public static final String ACTION_VARIABLE_PBEAN_JS = SESSION_VARIABLE_PREFIX + "presentation.bean" + JS_SUFFIX;
    public static final String ACTION_VARIABLE_USER_PROFILE = SESSION_VARIABLE_PREFIX + "userProfile";

    public static final String CONFIG_PARAMS =
        CONFIG_EXIT_DEFAULT +
        Constant.COMMA + CONFIG_EXIT_ERROR +
        Constant.COMMA + CONFIG_AUTHORIZER_NAME +
        Constant.COMMA + CONFIG_MESSAGE_AUTHORIZATION_FAILED +
        Constant.COMMA + CONFIG_PRESENTATION_BEAN_TRANSIENT;

    public static final String INVOKE_DIRECT_INPUTS = "";
    public static final String INVOKE_DIRECT_OUTPUT = ACTION_VARIABLE_PBEAN + ".defaultOutput";

    /**
     * Transient means the pbean shouldn't be set in session
     * @return
     */
    public boolean isPresentationBeanTransient()
    {
        if (_presentationBeanTransient == null)
        {
            String presentationBeanTransient = getConfigPropertyWithDefault(CONFIG_PRESENTATION_BEAN_TRANSIENT, "false");
            _presentationBeanTransient = Boolean.valueOf(presentationBeanTransient);
        }
        return _presentationBeanTransient == Boolean.TRUE;
    }

    /* (non-Javadoc)
     * @see com.cisco.salesit.framework.action.core.BaseAction#invokeStart(com.cisco.salesit.framework.common.core.IContext)
     */
    protected void invokeStart(IContext context) throws ActionException
    {
        if (!isAuthorized(context))
        {
            String authorizationFailedMessage = getConfigProperty(CONFIG_MESSAGE_AUTHORIZATION_FAILED);
            throw new AuthorizationFailedActionException(authorizationFailedMessage);
        }

        super.invokeStart(context);

        // play back model changes
        Object pbean = context.getValue(ACTION_VARIABLE_PBEAN);
        if (pbean == null)
        {
            _Logger.info("setModelChange: pbean=null");
            return;
        }

        String modelChanges = (String) context.getValue(MODEL_CHANGES);
        if (modelChanges != null)
        for (StringTokenizer st = new StringTokenizer(modelChanges, ","); st.hasMoreTokens();)
        {
            String pair = null;
            try
            {
                pair = URLDecoder.decode(st.nextToken(), Constant.UTF8);
            }
            catch (UnsupportedEncodingException e)
            {
                _Logger.error(e);
                continue;
            }
            int equal = pair.indexOf('=');
            String modelSource = pair.substring(0, equal);
            int firstDot = modelSource.indexOf('.');
            String propertyPath = modelSource.substring(firstDot + 1);
            String propertyValue = pair.substring(equal + 1);
            BeanHelper.getSingleton().setProperty(pbean, propertyPath, propertyValue);
            _Logger.debug("setModelChange: (" + pbean + ")." + propertyPath + "=" + BeanHelper.getSingleton().getProperty(pbean, propertyPath));
        }
    }

    /* (non-Javadoc)
     * @see com.cisco.salesit.framework.action.core.BaseAction#invokeEnd(com.cisco.salesit.framework.common.core.IContext, java.lang.Object)
     */
    protected Object invokeEnd(IContext context, Object r) throws ActionException
    {
        ActionResult result = (ActionResult) r;

        Object pbean = super.invokeEnd(context, result.getPresentationBean());
        if (!isPresentationBeanTransient())
            context.setValue(ACTION_VARIABLE_PBEAN, pbean);

        // convert to JavaScript format
        long startTime = System.currentTimeMillis();
        String pbeanJS = BeanJSConverter.convertToJS(pbean);
        long elapsedTime = System.currentTimeMillis() - startTime;
        context.setValue(ACTION_VARIABLE_PBEAN_JS, pbeanJS);

        if (_Logger.isDebugEnabled())
        {
            String pbeanJSPrint = BeanPrinter.getPrintString(pbean);
            if (pbeanJSPrint.length() > 4096)
                pbeanJSPrint = pbeanJSPrint.substring(1, 4096) + " ...";
            _Logger.debug("[" + elapsedTime + "ms] " + ACTION_VARIABLE_PBEAN_JS + "=" + pbeanJSPrint);
        }

        String exitName = result.getExitName();
        String exitValue = getConfigProperty(exitName);
        if (exitValue == null)
            throw new ActionException("exit '" + exitName + "' undefined in action '" + getName() + "'");

        if (isExitNameAction(exitValue))
        {
            String actionName = exitValue.substring(ACTION_PREFIX.length());
            IAction action = Cornerstone.getActionManager().createActionByName(actionName);
            _Logger.debug("chaining to action '" + actionName + "'");
            context.setValue(PREVIOUS_RESULT, result);
            return action.invoke(context);
        }
        else
        {
            ActionResult previousResult = (ActionResult) context.getValue(PREVIOUS_RESULT);
            result.combinePreviousResult(previousResult);
            result.setExitUrl(exitValue);
            return result;
        }
    }

    protected Object exit(String exitName)
    {
        return exit(exitName, null, null);
    }

    protected Object exit(String exitName, Object pbean)
    {
        return exit(exitName, pbean, null);
    }

    protected Object exit(String exitName, Object pbean, String message)
    {
        ActionResult result = new ActionResult();
        result.setExitName(exitName);
        result.setPresentationBean(pbean);
        if (message != null) result.addMessage(message);
        return result;
    }

    protected boolean isExitNameAction(String exitName)
    {
        return exitName.startsWith(ACTION_PREFIX);
    }

//    protected HttpServletRequest getRequest()
//    {
//        return (HttpServletRequest) _context.getValue(REQUEST);
//    }
//
//    protected HttpServletResponse getResponse()
//    {
//        return (HttpServletResponse) _context.getValue(RESPONSE);
//    }
//
//    protected IContext getSessionContext()
//    {
//        return (IContext) _context.getValue(SESSION_CONTEXT);
//    }

    protected boolean isAuthorized(IContext context) throws ActionException
    {
        IAction authorizer = getAuthorizer();
        if (authorizer != null)
        {
            Boolean isAuthorized = (Boolean) authorizer.invoke(context);
            return isAuthorized.booleanValue();
        }
        else
        {
            return true;
        }
    }

    protected IAction getAuthorizer() throws ActionException
    {
        Boolean authorizerDefined = (Boolean) getClassVariable(CLASS_VARIABLE_AUTHORIZER_DEFINED);
        if (authorizerDefined == null)
        {
            String authorizerName = getConfigProperty(CONFIG_AUTHORIZER_NAME); 
            authorizerDefined = (authorizerName == null) ? Boolean.FALSE : Boolean.TRUE;                
            setClassVariable(CLASS_VARIABLE_AUTHORIZER_DEFINED, authorizerDefined);
        }

        if (authorizerDefined == Boolean.TRUE)
        {
            String authorizerName = getConfigProperty(CONFIG_AUTHORIZER_NAME);
            // create every time because actions are not reentrant 
            IAction authorizer = Cornerstone.getActionManager().createActionByName(authorizerName);
            return authorizer;
        }
        else
        {
            return null;
        }
    }

    private static Logger _Logger = Logger.getLogger(BasePresentationAction.class);
    protected Boolean _presentationBeanTransient;
}