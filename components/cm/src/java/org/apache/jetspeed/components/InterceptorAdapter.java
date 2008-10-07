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
package org.apache.jetspeed.components;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.ClassHierarchyIntrospector;
import org.picocontainer.defaults.ImplementationHidingComponentAdapter;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.defaults.Swappable;

/**
 * InterceptorAdaptor
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class InterceptorAdapter extends ImplementationHidingComponentAdapter // DecoratingComponentAdapterFactory
{
    public InterceptorAdapter(ComponentAdapter delegate)
    {
        super(delegate);
    }
    
    public Object getComponentInstance()
        throws
            PicoInitializationException,
            PicoIntrospectionException,
            AssignabilityRegistrationException,
            NotConcreteRegistrationException
    {
        Class[] interfaces;
        if (getDelegate().getComponentKey() instanceof Class
            && ((Class) getDelegate().getComponentKey()).isInterface())
        {
            // If the compo
            interfaces = new Class[] {(Class) getDelegate().getComponentKey()};
        }
        else
        {
            interfaces =
                ClassHierarchyIntrospector.getAllInterfaces(
                    getDelegate().getComponentImplementation());
        }
        Class[] swappableAugmentedInterfaces = new Class[interfaces.length + 1];
        swappableAugmentedInterfaces[interfaces.length] = Swappable.class;
        System.arraycopy(
            interfaces,
            0,
            swappableAugmentedInterfaces,
            0,
            interfaces.length);
        if (interfaces.length == 0)
        {
            throw new PicoIntrospectionException(
                "Can't hide implementation for "
                    + getDelegate().getComponentImplementation().getName()
                    + ". It doesn't implement any interfaces.");
        }
        final DelegatingInvocationHandler delegatingInvocationHandler =
            new DelegatingInvocationHandler(this);
        return Proxy.newProxyInstance(
            getClass().getClassLoader(),
            swappableAugmentedInterfaces,
            delegatingInvocationHandler);
    }
    private Object getDelegatedComponentInstance()
    {
        return super.getComponentInstance();
    }
    private class DelegatingInvocationHandler
        implements InvocationHandler, Swappable
    {
        private final InterceptorAdapter adapter;
        private Object delegatedInstance;
        public DelegatingInvocationHandler(InterceptorAdapter adapter)
        {
            this.adapter = adapter;
        }
        public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable
        {
            System.out.println("Invoking method: " + method);
            Class declaringClass = method.getDeclaringClass();
            if (declaringClass.equals(Object.class))
            {
                if (method.equals(ClassHierarchyIntrospector.hashCode))
                {
                    // Return the hashCode of ourself, as Proxy.newProxyInstance() may
                    // return cached proxies. We want a unique hashCode for each created proxy!
                    return new Integer(
                        System.identityHashCode(
                            DelegatingInvocationHandler.this));
                }
                if (method.equals(ClassHierarchyIntrospector.equals))
                    
                {
                    return new Boolean(proxy == args[0]);
                }
                // If it's any other method defined by Object, call on ourself.
                return method.invoke(DelegatingInvocationHandler.this, args);
            }
            else
                if (declaringClass.equals(Swappable.class))
                {
                    return method.invoke(this, args);
                }
                else
                {
                    if (delegatedInstance == null)
                    {
                        delegatedInstance = adapter.getDelegatedComponentInstance();
                    }
                    return method.invoke(delegatedInstance, args);
                }
        }
        public Object hotswap(Object newSubject)
        {
            Object result = delegatedInstance;
            delegatedInstance = newSubject;
            return result;
        }
    }
}
