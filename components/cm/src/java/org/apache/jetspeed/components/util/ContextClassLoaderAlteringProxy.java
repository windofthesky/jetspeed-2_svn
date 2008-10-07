/*
 * Created on Apr 25, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.components.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.picocontainer.MutablePicoContainer;


/**
 * @author Scott Weaver
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class ContextClassLoaderAlteringProxy implements InvocationHandler
{
    
    private ClassLoader alternateContextClassLoader;
    private Object obj;

    public ContextClassLoaderAlteringProxy(Object obj, ClassLoader alternateContextClassLoader)
    {
        this.alternateContextClassLoader = alternateContextClassLoader;
        this.obj = obj;
    }

    /* (non-Javadoc)
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try
        {
            Thread.currentThread().setContextClassLoader(alternateContextClassLoader);
            return method.invoke(obj, args);
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
    }

}
