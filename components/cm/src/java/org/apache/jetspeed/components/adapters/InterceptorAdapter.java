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

import java.lang.reflect.Proxy;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.ImplementationHidingComponentAdapter;
import org.picocontainer.defaults.InterfaceFinder;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.defaults.Swappable;

/**
 * InterceptorAdaptor
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */
public class InterceptorAdapter extends ImplementationHidingComponentAdapter // DecoratingComponentAdapterFactory
{
    private Class delegationStrategyClass;
    
    public InterceptorAdapter( ComponentAdapter delegate, Class delegationStrategyClass )
    {
        super(delegate);
        this.delegationStrategyClass = delegationStrategyClass;
    }
    private final InterfaceFinder interfaceFinder = new InterfaceFinder();

    public Object getComponentInstance() throws PicoInitializationException,
            PicoIntrospectionException, AssignabilityRegistrationException,
            NotConcreteRegistrationException
    {
        Class[] interfaces;
        if (getDelegate().getComponentKey() instanceof Class
                && ((Class) getDelegate().getComponentKey()).isInterface())
        {
            // If the compo
            interfaces = new Class[]{(Class) getDelegate().getComponentKey()};
        }
        else
        {
            interfaces = interfaceFinder.getInterfaces(getDelegate()
                    .getComponentImplementation());
        }
        Class[] swappableAugmentedInterfaces = new Class[interfaces.length + 1];
        swappableAugmentedInterfaces[interfaces.length] = Swappable.class;
        System.arraycopy(interfaces, 0, swappableAugmentedInterfaces, 0,
                interfaces.length);
        if (interfaces.length == 0)
        {
            throw new PicoIntrospectionException(
                    "Can't hide implementation for "
                            + getDelegate().getComponentImplementation()
                                    .getName()
                            + ". It doesn't implement any interfaces.");
        }
        
        final DelegationStrategy delegationStrategy;
        try
        {
            delegationStrategy = (DelegationStrategy) delegationStrategyClass.newInstance();
            delegationStrategy.setAdapter(this);
        }
        catch (Exception e)
        {
            throw new PicoInitializationException("Error while creating new DelegationStartegy instance: "+e.toString(), e);
        }
        
        return Proxy.newProxyInstance(getClass().getClassLoader(),
                swappableAugmentedInterfaces, delegationStrategy);
    }

    Object getDelegatedComponentInstance()
    {
        return super.getComponentInstance();
    }
}