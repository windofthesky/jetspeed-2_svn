/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.jetspeed.engine;

import java.io.File;
import java.io.IOException;

import javax.naming.NamingException;

import org.apache.commons.configuration.Configuration;
import org.apache.jetspeed.components.PicoComponentManager;
import org.apache.jetspeed.components.datasource.DatasourceComponent;
import org.apache.jetspeed.components.jndi.JNDIComponent;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;

/**
 * Jetspeed Engine implementation
 * 
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor </a>
 * @version $Id$
 */
public class PicoEngine extends AbstractEngine implements Engine
{
    protected void initComponents( Configuration configuration )
            throws IOException, ClassNotFoundException, NamingException
    {
        String applicationRoot = getRealPath("/");
        String assemblyScript = configuration.getString(
                "jetspeed.root.assembly", "/WEB-INF/assembly/jetspeed.groovy");

        File containerAssembler = new File(applicationRoot + assemblyScript);
        ObjectReference bootContainerRef = new SimpleReference();
        MutablePicoContainer bootContainer = new DefaultPicoContainer();        
        bootContainer.registerComponentInstance("portal_configuration", configuration);
              
        componentManager = new  PicoComponentManager(containerAssembler, bootContainer, "PORTAL_SCOPE");
        
        try
        {
            if (useInternalJNDI)
            {
                JNDIComponent jndi = (JNDIComponent) componentManager
                        .getComponent(JNDIComponent.class);
                if (jndi != null)
                {
                    DatasourceComponent ds = (DatasourceComponent) componentManager
                            .getComponent(DatasourceComponent.class);
                    if (ds != null)
                    {
                        jndi.bindObject("comp/env/jdbc/jetspeed", ds
                                .getDatasource());
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