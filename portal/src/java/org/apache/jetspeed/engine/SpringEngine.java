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
import org.apache.jetspeed.components.ComponentManager;
import org.apache.jetspeed.components.SpringComponentManager;

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
    protected ComponentManager initComponents( Configuration configuration ) throws IOException, ClassNotFoundException,
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
       
        ComponentManager cm = new SpringComponentManager((String[])configs.toArray(new String[configs.size()]), null);
        
        return cm;
    }

}