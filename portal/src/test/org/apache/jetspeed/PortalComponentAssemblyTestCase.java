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
package org.apache.jetspeed;

import org.apache.jetspeed.components.ComponentAssemblyTestCase;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.File;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.Log4jFactory;
import org.apache.log4j.PropertyConfigurator;

/**
 * PortalComponentAssemblyTestCase
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PortalComponentAssemblyTestCase extends ComponentAssemblyTestCase
{
    private String log4jFile = "./src/webapp/WEB-INF/conf/Log4j.properties";
    
    public PortalComponentAssemblyTestCase(String name)
    {
        super(name);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.ComponentAssemblyTestCase#getBaseProject()
     */
    public String getBaseProject()
    {
        return "portal";
    }
    
    public void setUp()
    throws Exception
    {
        super.setUp();
        Properties p = new Properties();
        try
        {
            File baseDir = new File(System.getProperty("basedir"));
            if(baseDir.exists())
            {
                System.out.println("Finding logfile from basedir " + baseDir);
                File logFile = new File(baseDir, log4jFile);
                p.load(new FileInputStream(logFile));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        PropertyConfigurator.configure(p);
        
        System.getProperties().setProperty(LogFactory.class.getName(), Log4jFactory.class.getName());
        System.out.println("set props ok");
        
    }
    
}
