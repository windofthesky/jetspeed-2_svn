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
package org.apache.portals.applications.rss.servlets;

import java.io.InputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;


/**
 * SpringInitServlet
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class SpringInitServlet extends HttpServlet
{
    /**
     * Init Parameter: default spring configuration property
     */
    private static final String INITPARAM_SPRING_CONFIG = "spring-configuration";
    private static Object semaphore = new Object();

    /**
     * Spring Factory 
     */
    private static XmlBeanFactory springFactory = null;
    
    
    /**
     * Intialize Servlet.
     */
    public final void init( ServletConfig config ) throws ServletException
    {
        super.init(config);
        String springConfig = getInitParameter(INITPARAM_SPRING_CONFIG);       
        if (springConfig == null) { throw new ServletException("Spring Configuration file not specified"); }
        
        // load Spring
        try 
        {
            synchronized (semaphore)
            {
                if (null == springFactory)
                {
                    InputStream is = this.getServletContext().getResourceAsStream(springConfig);                    
                    springFactory = new XmlBeanFactory(is);
                    is.close();
                }
            }
         } 
         catch (Exception e) 
         {
             throw new ServletException("Failed to load spring configuration.", e);
         }   
        
    }
    
    public static final BeanFactory getSpringFactory()
    {
        return springFactory;
    }
    
}
