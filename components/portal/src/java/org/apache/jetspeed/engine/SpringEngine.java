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
package org.apache.jetspeed.engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.apache.commons.configuration.Configuration;
import org.apache.jetspeed.components.ComponentManager;
import org.apache.jetspeed.components.SpringComponentManager;
import org.apache.jetspeed.components.factorybeans.ServletConfigFactoryBean;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * <p>
 * SpringEngine
 * </p>
 * <p>
 * 
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public class SpringEngine extends AbstractEngine
{

    /**
     * <p>
     * initComponents
     * </p>
     * 
     * @see org.apache.jetspeed.engine.AbstractEngine#initComponents(org.apache.commons.configuration.Configuration)
     * @param configuration
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NamingException
     */
    protected ComponentManager initComponents( Configuration configuration, ServletConfig servletConfig ) throws IOException, ClassNotFoundException,
            NamingException
    {
        
        ServletConfigFactoryBean.setServletConfig(servletConfig);
        String relativeApplicationRoot = getRealPath("/");
        String absApplicationRoot = new File(relativeApplicationRoot).getCanonicalPath();        
        System.setProperty("applicationRoot", absApplicationRoot);        
        
        final String assemblyDir = configuration.getString("assembly.dir","/WEB-INF/assembly");
        final String assemblyFileExtension = configuration.getString("assembly.extension",".xml");
        
        XmlWebApplicationContext bootCtx = new XmlWebApplicationContext();
        ServletContext servletContext = servletConfig.getServletContext();
        bootCtx.setServletContext(servletContext);
        bootCtx.setConfigLocations(new String[] {"/WEB-INF/assembly/boot/*.xml"});
        bootCtx.refresh();
        
        XmlWebApplicationContext appCtx = new XmlWebApplicationContext();
        appCtx.setParent(bootCtx);
        appCtx.setServletContext(servletContext);
        appCtx.setConfigLocations(new String[] {assemblyDir+"/*"+assemblyFileExtension});
        appCtx.refresh();
        
        SpringComponentManager cm = new SpringComponentManager(appCtx);
        servletConfig.getServletContext().setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, cm.getApplicationContext());
        
        return cm;
    }    
   

}