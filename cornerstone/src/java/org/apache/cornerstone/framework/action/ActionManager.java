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

package org.apache.cornerstone.framework.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.cornerstone.framework.api.action.ActionException;
import org.apache.cornerstone.framework.api.action.IAction;
import org.apache.cornerstone.framework.api.action.IActionDescriptor;
import org.apache.cornerstone.framework.api.action.IActionManager;
import org.apache.cornerstone.framework.api.action.InvalidActionException;
import org.apache.cornerstone.framework.api.factory.CreationException;
import org.apache.cornerstone.framework.api.factory.IFactory;
import org.apache.cornerstone.framework.api.registry.IRegistry;
import org.apache.cornerstone.framework.api.registry.IRegistryEntry;
import org.apache.cornerstone.framework.registry.RegistryPropertiesFactory;
import org.apache.cornerstone.framework.singleton.Singleton;
import org.apache.cornerstone.framework.singleton.SingletonManager;
import org.apache.log4j.Logger;

/**
Factory for getting all action instances.
*/

public class ActionManager extends Singleton implements IActionManager
{
    public static final String REVISION = "$Revision$";

    public static final String ACTION = "action";

    public static final String ACTION_REGISTRY_FACTORY_CLASS_NAME = "actionRegistryFactory.className";
    public static final String ACTION_REGISTRY_DOMAIN_NAME = "actionRegistry.domainName";
    public static final String DEFAULT_ACTION_REGISTRY_DOMAIN_NAME = ACTION;

    public static final String CLASS_NAME = "className";
    public static final String FACTORY_CLASS_NAME = "factoryClassName";
    public static final String NAME = "name";

    public static final String META = "_";
    public static final String META_CLASS_NAME = META + "." + CLASS_NAME;
    public static final String META_FACTORY_CLASS_NAME = META + "." + FACTORY_CLASS_NAME;
    public static final String META_NAME = META + "." + NAME;

    protected static ActionManager _Singleton = new ActionManager();

    private static Logger _Logger = Logger.getLogger(ActionManager.class);

    public static ActionManager getSingleton()
    {
        return _Singleton;
    }

    public ActionManager()
    {
        try
        {
            init();
            // TODO: change to use implementation registry
            _registry = (IRegistry) RegistryPropertiesFactory.getSingleton().createInstance();
        }
        catch (CreationException ce)
        {
            _Logger.error("failed to create instance of " + getClass().getName());
        }
    }

    /**
     * Gets new instance of action by its class name.
     * @param className class name of action
     * @return new instance of action class
     */
    public IAction createActionByClassName(String className) throws ActionException
    {
        try
        {
            Object myObj = Class.forName(className).newInstance();
            return (IAction) myObj;
        }
        catch (Exception e)
        {
            throw new ActionException("failed to create action instance (class=" + className + ")", e);
        }
    }

    /**
     * Gets new instance of action by its factory class name.
     * @param factoryClassName class name of action factory
     * @return new instance created by calling createInstance() on
     *   action factory.
     */
    public IAction createActionByFactoryClassName(String factoryClassName) throws ActionException
    {
        IFactory factory = (IFactory) SingletonManager.getSingleton(factoryClassName);
        if (factory != null)
        {
            try
            {
                return (IAction) factory.createInstance();
            }
            catch (CreationException ce)
            {
                throw new ActionException(ce.getCause());
            }
        }

        throw new ActionException("failed to create instance of factory class " + factoryClassName);
    }

    public IAction createActionByName(String logicalName) throws ActionException
    {
        if (_actionDescriptorMap != null)
        {
            IActionDescriptor sd = (IActionDescriptor) _actionDescriptorMap.get(logicalName);

            if (sd == null)
            {
                throw new ActionException("action '" + logicalName + "' not found in registry");
            }

            if (!sd.isValid())
            {
                throw new ActionException("action '" + logicalName + "' not valid");
            }
        }

        String actionRegistryDomainName = getActionRegistryDomainName();
        IRegistryEntry entry = _registry.getRegistryEntry(actionRegistryDomainName, logicalName);
        if (entry == null)
        {
            throw new ActionException("action '" + logicalName + "' not found in registry");
        }

        String factoryClassName = entry.getProperty(META_FACTORY_CLASS_NAME);
        if (factoryClassName != null)
        {
            IAction action = createActionByFactoryClassName(factoryClassName);
            action.overwriteConfig(entry.getProperties());
            return new LogicalAction(logicalName, action);
        }

        String className = entry.getProperty(META_CLASS_NAME);
        if (className != null)
        {
            IAction action = createActionByClassName(className);
            action.overwriteConfig(entry.getProperties());
            return new LogicalAction(logicalName, action);
        }

        throw new ActionException(
            "'"
                + META_CLASS_NAME
                + "' or '"
                + META_FACTORY_CLASS_NAME
                + "' not defined for action '"
                + logicalName
                + "' in registry");
    }

    public String getActionRegistryDomainName()
    {
        String actionRegistryDomainName = getConfigProperty(ACTION_REGISTRY_DOMAIN_NAME);
        if (actionRegistryDomainName == null)
            actionRegistryDomainName = DEFAULT_ACTION_REGISTRY_DOMAIN_NAME;

        return actionRegistryDomainName;
    }

    protected void init() throws CreationException
    {
        initActionRegistry();
        initActions();
    }

    /**
     * Initializes action registry.
     */
    protected void initActionRegistry() throws CreationException
    {
        // ActionRegistryPropertiesFactory.getSingleton().createInstance();
        String actionRegistryFactoryClassName = getConfigProperty(ACTION_REGISTRY_FACTORY_CLASS_NAME);
        IFactory actionRegistryFactory = (IFactory) SingletonManager.getSingleton(actionRegistryFactoryClassName);
        actionRegistryFactory.createInstance();
    }

    protected void initActions()
    {
        Set actionNameSet = _registry.getRegistryEntryNameSet(getActionRegistryDomainName());
        Map actionDescriptorMap = new HashMap();
        for (Iterator itr = actionNameSet.iterator(); itr.hasNext();)
        {
            String actionName = (String) itr.next();
            IAction action = null;
            IActionDescriptor sd = null;

            try
            {
                action = createActionByName(actionName);
                sd = action.getDescriptor();
                actionDescriptorMap.put(actionName, sd);
                _Logger.info("action '" + actionName + "' init OK");
            }
            catch (InvalidActionException ise)
            {
                _Logger.error("action '" + actionName + "' init failed: invalid", ise);
                sd = new ActionDescriptor();
                sd.setValid(false);
                actionDescriptorMap.put(actionName, sd);
            }
            catch (ActionException se)
            {
                _Logger.error("failed to get action '" + actionName + "'", se.getCause());
            }
        }

        _actionDescriptorMap = actionDescriptorMap;
    }

    protected IRegistry _registry;
    protected Map _actionDescriptorMap;
}