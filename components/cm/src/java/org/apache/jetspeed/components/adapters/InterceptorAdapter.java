/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.jetspeed.components.adapters;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.DecoratingComponentAdapter;
import org.picocontainer.defaults.NotConcreteRegistrationException;

import com.thoughtworks.proxy.factory.StandardProxyFactory;
import com.thoughtworks.proxy.toys.hotswap.HotSwapping;
import com.thoughtworks.proxy.toys.multicast.ClassHierarchyIntrospector;

/**
 * InterceptorAdaptor
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */
public class InterceptorAdapter extends DecoratingComponentAdapter
{
    private Class delegationStrategyClass;
    protected StandardProxyFactory proxyFactory;

    public InterceptorAdapter( ComponentAdapter delegate, Class delegationStrategyClass )
    {
        super(delegate);
        this.delegationStrategyClass = delegationStrategyClass;
        this.proxyFactory = new StandardProxyFactory();
    }

    public Object getComponentInstance() throws PicoInitializationException, PicoIntrospectionException,
            AssignabilityRegistrationException, NotConcreteRegistrationException
    {

        Class[] proxyTypes;
        if (getComponentKey() instanceof Class && proxyFactory.canProxy((Class) getComponentKey()))
        {
            proxyTypes = new Class[]{(Class) getComponentKey()};
        }
        else
        {
            proxyTypes = ClassHierarchyIntrospector.addIfClassProxyingSupportedAndNotObject(
                    getComponentImplementation(), getComponentImplementation().getInterfaces(), proxyFactory);
        }

        final DelegationStrategy delegationStrategy;
        try
        {
            delegationStrategy = (DelegationStrategy) delegationStrategyClass.newInstance();
            delegationStrategy.setAdapter(this.getDelegate());
            return HotSwapping.object(proxyTypes, proxyFactory, delegationStrategy, true);
        }
        catch (Exception e)
        {
            throw new PicoInitializationException("Error while creating new DelegationStartegy instance: "
                    + e.toString(), e);
        }

        
    }
}