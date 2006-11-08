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
package org.apache.jetspeed.components.jndi;

/**
 * <p>
 * Helper class to establish a jndi based datasource for commandline and maven based applications
 *  
 * </p>
 * 
 * 
 * @
 * @author <a href="mailto:hajo@bluesunrise.com">Hajo Birthelmer</a>
 * @version $ $
 *
 */

import org.apache.jetspeed.testhelpers.AbstractTestHelper;
import java.util.Map;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.jetspeed.components.SpringComponentManager;
import org.apache.jetspeed.components.datasource.BoundDBCPDatasourceComponent;
import org.apache.jetspeed.components.jndi.JNDIComponent;
import org.apache.jetspeed.components.jndi.TyrexJNDIComponent;

public class SpringJNDIStarter extends AbstractTestHelper
{

	   public static final String JNDI_DS_NAME = "jetspeed";
	    
	    protected BoundDBCPDatasourceComponent datasourceComponent;
	    protected JNDIComponent jndi;
	    String appRoot = null; 
	    String[] bootConfig = null;
        String[] appConfig = null;
        SpringComponentManager scm = null;
	
        
   /**
    * 
    * Create an instance with a given context  and the usual SpringComponent arguments 
    * @param context
    * @param appRoot root directory of the application
    * @param bootConfig (string-)list of files to process on boot
    * @param appConfig (string-)list of files to process as configuration 
    */
        public SpringJNDIStarter(Map context,String appRoot, String[] bootConfig, String[] appConfig)
	    {
	    	super(context);
	    	this.appRoot = appRoot;
	    	this.bootConfig = bootConfig;
	    	this.appConfig = appConfig;
	    }

/**
 * The main startup routine.
 * Establishes a JNDI connection based on the following System parameters:
 * <p> org.apache.jetspeed.database.url
 * <p> org.apache.jetspeed.database.driver
 * <p> org.apache.jetspeed.database.user
 * <p> org.apache.jetspeed.database.password
 * @throws Exception
 */
    public void setUp() throws Exception
    {
    	setupJNDI();
         scm = new SpringComponentManager(bootConfig, appConfig,  appRoot );
        }

    public void tearDown() throws Exception
    {
    	try
    	{
    		datasourceComponent.stop();
    	}
    	catch (Exception e)
    	{
    		System.out.println("datasourceComponent stop failed with " + e.getLocalizedMessage());
    		e.printStackTrace();
    	}
    	try
    	{
    		scm.stop();
    	}
    	catch (Exception e)
    	{
    		System.out.println("SpringComponentManager stop failed with " + e.getLocalizedMessage());
    		e.printStackTrace();
    	}
    	
    	try
    	{
    		jndi.unbindFromCurrentThread();
		}
		catch (Exception e)
		{
			System.out.println("JNDI  unbindFromCurrentThread failed with " + e.getLocalizedMessage());
			e.printStackTrace();
		}
    }
    
    
    
    
    public void setupJNDI() throws Exception
    {
        jndi = new TyrexJNDIComponent();
        String url = System.getProperty("org.apache.jetspeed.database.url");
        String driver = System.getProperty("org.apache.jetspeed.database.driver");
        String user = System.getProperty("org.apache.jetspeed.database.user");
        String password = System.getProperty("org.apache.jetspeed.database.password");
        datasourceComponent = new BoundDBCPDatasourceComponent(user, password, driver, url, 20, 5000,
                GenericObjectPool.WHEN_EXHAUSTED_GROW, true, JNDI_DS_NAME, jndi);
        datasourceComponent.start();
    }

	public SpringComponentManager getComponentManager()
	{
		return scm;
	}

    
    
}