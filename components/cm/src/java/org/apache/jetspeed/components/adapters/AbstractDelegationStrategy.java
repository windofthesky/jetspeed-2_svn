/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.components.adapters;

import java.lang.reflect.Method;

import org.picocontainer.defaults.InterfaceFinder;
import org.picocontainer.defaults.Swappable;

public abstract class AbstractDelegationStrategy implements DelegationStrategy
{

    private InterceptorAdapter adapter;
    
    public Object invoke( Object proxy, Method method, Object[] args )
            throws Throwable
    {
        if(adapter == null)
        {
            throw new IllegalStateException("InterceptorAdapter cannot be null.  You must call setAdapter() before calling invoke().");
        }
        
        //System.out.println("Invoking method: " + method);
        Class declaringClass = method.getDeclaringClass();
        if (declaringClass.equals(Object.class))
        {
            if (method.equals(InterfaceFinder.hashCode))
            {
                // Return the hashCode of ourself, as Proxy.newProxyInstance()
                // may
                // return cached proxies. We want a unique hashCode for each
                // created proxy!
                return new Integer(System
                        .identityHashCode(AbstractDelegationStrategy.this));
            }
            if (method.equals(InterfaceFinder.equals))
            {
                return new Boolean(proxy == args[0]);
            }
            // If it's any other method defined by Object, call on ourself.
            return method.invoke(AbstractDelegationStrategy.this, args);
        }
        else if (declaringClass.equals(Swappable.class))
        {
            return method.invoke(this, args);
        }
        else
        {
            if (getDelegatedInstance() == null)
            {
                setDelegatedInstance(adapter.getDelegatedComponentInstance());
            }
            return method.invoke(getDelegatedInstance(), args);
        }
    }

    public Object __hotSwap( Object newSubject )
    {
        Object result = getDelegatedInstance();
        setDelegatedInstance(newSubject);
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.components.DelegationStrategy#setAdapter(org.picocontainer.ComponentAdapter)
     */
    public void setAdapter( InterceptorAdapter adapter )
    {
        this.adapter = adapter;

    }

    protected abstract Object getDelegatedInstance();
   
    protected abstract void setDelegatedInstance( Object instance );
}