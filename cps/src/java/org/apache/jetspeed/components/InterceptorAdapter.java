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
package org.apache.jetspeed.components;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.DecoratingComponentAdapterFactory;
import org.picocontainer.defaults.ImplementationHidingComponentAdapter;
import org.picocontainer.defaults.InterfaceFinder;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.defaults.Swappable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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
    private final InterfaceFinder interfaceFinder = new InterfaceFinder();
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
                interfaceFinder.getInterfaces(
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
                if (method.equals(InterfaceFinder.hashCode))
                {
                    // Return the hashCode of ourself, as Proxy.newProxyInstance() may
                    // return cached proxies. We want a unique hashCode for each created proxy!
                    return new Integer(
                        System.identityHashCode(
                            DelegatingInvocationHandler.this));
                }
                if (method.equals(InterfaceFinder.equals))
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
        public Object __hotSwap(Object newSubject)
        {
            Object result = delegatedInstance;
            delegatedInstance = newSubject;
            return result;
        }
    }
}
