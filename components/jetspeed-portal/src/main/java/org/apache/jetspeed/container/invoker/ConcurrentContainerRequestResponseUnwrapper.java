/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.container.invoker;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.UndeclaredThrowableException;
import net.sf.cglib.transform.impl.UndeclaredThrowableStrategy;

/**
 * ConcurrentContainerRequestResponseUnwrapper
 *
 * @author <a href="mailto:woonsan@apache.org">Woonsan Ko</a>
 * @version $Id$
 */
public class ConcurrentContainerRequestResponseUnwrapper implements ContainerRequestResponseUnwrapper
{
    private Class<?> proxySuperClass;
    private Class<?> [] proxyConstructorArgTypes;
    private Object [] proxyConstructorArgs;
    private String [] attributableProperties;
    
    private Enhancer enhancer;
    
    public Class<?> getProxySuperClass()
    {
        return proxySuperClass;
    }

    public void setProxySuperClass(Class<?> proxySuperClass)
    {
        this.proxySuperClass = proxySuperClass;
    }

    public Class<?>[] getProxyConstructorArgTypes()
    {
        return proxyConstructorArgTypes;
    }

    public void setProxyConstructorArgTypes(Class<?>[] proxyConstructorArgTypes)
    {
        this.proxyConstructorArgTypes = proxyConstructorArgTypes;
    }

    public Object[] getProxyConstructorArgs()
    {
        return proxyConstructorArgs;
    }

    public void setProxyConstructorArgs(Object[] proxyConstructorArgs)
    {
        this.proxyConstructorArgs = proxyConstructorArgs;
    }
    
    public String[] getAttributableProperties()
    {
        return attributableProperties;
    }

    public void setAttributableProperties(String[] attributableProperties)
    {
        this.attributableProperties = attributableProperties;
    }

    public ServletRequest unwrapContainerRequest(HttpServletRequest containerRequest)
    {
        ServletRequest request = containerRequest;
        
        while (request instanceof HttpServletRequestWrapper)
        {
            request = ((HttpServletRequestWrapper) request).getRequest();
        }
        
        if (enhancer == null)
        {
            ConcurrentRequestMethodInterceptor interceptor = new ConcurrentRequestMethodInterceptor(request);
            
            if (attributableProperties != null)
            {
                interceptor.setAttributableProperties(attributableProperties);
            }
            
            enhancer = new Enhancer();
            enhancer.setSuperclass(proxySuperClass != null ? proxySuperClass : request.getClass());
            enhancer.setStrategy(new UndeclaredThrowableStrategy(UndeclaredThrowableException.class));
            enhancer.setInterceptDuringConstruction(false);
            enhancer.setCallback(interceptor);
        }
        
        if (proxyConstructorArgTypes != null && proxyConstructorArgTypes.length > 0)
        {
            request = (ServletRequest) enhancer.create(proxyConstructorArgTypes, proxyConstructorArgs);
        }
        else
        {
            request = (ServletRequest) enhancer.create();
        }
        
        return request;
    }
    
    public ServletResponse unwrapContainerResponse(HttpServletResponse containerResponse)
    {
        if (containerResponse instanceof HttpServletResponseWrapper)
        {
            return ((HttpServletResponseWrapper) containerResponse).getResponse();
        }
        
        return containerResponse;
    }
}
