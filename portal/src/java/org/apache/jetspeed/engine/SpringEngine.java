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
import java.io.IOException;
import java.util.ArrayList;

import javax.naming.NamingException;

import org.apache.commons.configuration.Configuration;
import org.apache.jetspeed.components.SpringComponentManager;
import org.apache.jetspeed.components.datasource.DatasourceComponent;
import org.apache.jetspeed.components.jndi.JNDIComponent;
import org.springframework.beans.factory.xml.XmlBeanFactory;

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
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NamingException
     */
    protected void initComponents( Configuration configuration ) throws IOException, ClassNotFoundException,
            NamingException
    {
        
         String relativeApplicationRoot = getRealPath("/");
         String absApplicationRoot = new File(relativeApplicationRoot).getCanonicalPath();
        // String absoluteApplicationRoot = new File(relativeApplicationRoot).getCanonicalPath();
        System.setProperty("applicationRoot", absApplicationRoot);
        ArrayList configs = new ArrayList();
        if (useInternalJNDI)
        {
            configs.add("file:///"+absApplicationRoot + configuration.getString("jetspeed.spring.datasource.xml",
                    "/WEB-INF/assembly/pooled-datasource-support.xml"));
        }
        configs.add("file:///"+absApplicationRoot + configuration.getString("jetspeed.spring.xml", "/WEB-INF/assembly/jetspeed-spring.xml"));
       
        componentManager = new SpringComponentManager((String[])configs.toArray(new String[configs.size()]), null);
    }

    protected void enableJNDI( Configuration configuration ) throws IOException
    {
        try
        {
            XmlBeanFactory dsBeanFactory = null;
            if (useInternalJNDI)
            {
                

                JNDIComponent jndi = (JNDIComponent) componentManager.getComponent(JNDIComponent.class.getName());
                if (jndi != null)
                {
                    DatasourceComponent ds = (DatasourceComponent) componentManager.getComponent(DatasourceComponent.class
                            .getName());
                    if (ds != null)
                    {
                        jndi.bindObject("comp/env/jdbc/jetspeed", ds.getDatasource());
                        jndi.bindToCurrentThread();
                    }
                }
                
            }
        }
        catch (NamingException e)
        {
            // skip for now
        }
    
      
    }

}