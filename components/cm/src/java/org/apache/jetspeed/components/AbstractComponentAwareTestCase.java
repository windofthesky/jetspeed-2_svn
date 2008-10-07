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
package org.apache.jetspeed.components;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.Log4jFactory;
import org.apache.log4j.PropertyConfigurator;

import org.picocontainer.MutablePicoContainer;

import junit.framework.TestCase;

/**
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public abstract class AbstractComponentAwareTestCase extends TestCase
{
    private ComponentManager ncm;
    private MutablePicoContainer container;
    
    /**
     * @return Returns the ncm.
     */
    public ComponentManager getComponentManager()
    {
        return ncm;
    }

    /**
     * @param ncm The ncm to set.
     */
    public void setComponentManager(ComponentManager ncm)
    {
        this.ncm = ncm;
    }



    /**
     * @return Returns the container.
     */
    public MutablePicoContainer getContainer()
    {
        return container;
    }

    /**
     * @param container The container to set.
     */
    public void setContainer(MutablePicoContainer container)
    {
        this.container = container;
    }

    

    
    public String getApplicationRoot()
    {
        return System.getProperty("org.apache.jetspeed.application_root", "./");
    }
    

    /**
     * 
     */
    public AbstractComponentAwareTestCase()
    {
        super();
        // TODO Auto-generated constructor stub
    }
    /**
     * @param arg0
     */
    public AbstractComponentAwareTestCase( String arg0 )
    {
        super(arg0);
        // TODO Auto-generated constructor stub
    }
}
