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
package org.apache.jetspeed.ajax;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * Performs invocation of the actual AJAX request and returns
 * a result object to converted into XML.
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 *
 */
public class AJAXServiceImpl implements AJAXService, BeanFactoryAware
{

    private Map serviceToBeans;

    private BeanFactory beanFactory;
    private VelocityEngine engine;

    public AJAXServiceImpl(Map serviceToBeans)
    {
        this.serviceToBeans = serviceToBeans;
        
    }

    public AJAXResponse processRequest(AJAXRequest request)
            throws AJAXException
    {
        final String serviceName = request.getServiceName();
        final String methodName = request.getMethodName();
        final String templateName = request.getServletRequest().getServletPath();

        final String mappedServiceName = (serviceName+"."+methodName).trim();
        try
        {
            if(engine == null)
            {
                engine = new VelocityEngine();
                Properties props = new Properties();
                props.load(request.getContext().getResourceAsStream("/WEB-INF/velocity.properties"));
                engine.init();
            }
            
            
            if(!serviceToBeans.containsKey(mappedServiceName))
            {
                throw new AJAXException("There is no AJAX service named '"+mappedServiceName+"' defined.  "+ 
                        "Please make sure that your ajax.xml is set up correctly.");
            }
            
            String beanId = ((String)serviceToBeans.get(mappedServiceName)).trim();
            Object targetService = beanFactory.getBean(beanId);
            final List parameters = request.getParameters();
            Method method = targetService.getClass().getMethod(methodName, getTypes(parameters));
            Object result = method.invoke(targetService, getValues(parameters));
            Context context = new VelocityContext();
            context.put("ajaxRequest", request);
            context.put("result", result);            
            
            final InputStream templateResource = request.getContext().getResourceAsStream(templateName);
            
            if(templateResource == null)
            {
                request.getServletResponse().sendError(404, templateName+" ajax template could not be found.");
                throw new IOException(templateName+" does not exist");
            }
            Reader template = new InputStreamReader(templateResource);
            
            return new AJAXResponseImpl(context, engine, template, request.getServletResponse().getWriter());
        }
        catch(AJAXException ae)
        {
            throw ae;
        }
        catch (Exception e)
        {
            throw new AJAXException("Unable to process service" + mappedServiceName + ": " + e.getMessage(), e);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
     */
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException
    {
        this.beanFactory = beanFactory;
    }

    protected Class[] getTypes(List objects)
    {
        Class[] args = new Class[objects.size()];
        Iterator itr = objects.iterator();
        int i = 0;
        while (itr.hasNext())
        {
            args[i] = ((AJAXRequestImpl.AJAXParameter)itr.next()).getValue().getClass();
            i++;
        }
        return args;
    }
    
    protected Object[] getValues(List objects)
    {
        Object[] args = new Object[objects.size()];
        Iterator itr = objects.iterator();
        int i = 0;
        while (itr.hasNext())
        {
            args[i] = ((AJAXRequestImpl.AJAXParameter)itr.next()).getValue();
            i++;
        }
        return args;
    }

}
