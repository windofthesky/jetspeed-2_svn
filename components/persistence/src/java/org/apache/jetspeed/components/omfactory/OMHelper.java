/**
 * Created on Feb 16, 2004
 *
 * 
 * @author
 */
package org.apache.jetspeed.components.omfactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;


import org.picocontainer.Parameter;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.defaults.DefaultPicoContainer;



/**
 * <p>
 * OMHelper
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class OMHelper extends DefaultPicoContainer
{
	private Map config;
	
	private static OMHelper instance;
	
	public static OMFactory getOMFactory(String om)
	{
		return (OMFactory) instance.getComponentInstance(om);
	}
	
	public static boolean isInitialized()
	{
		return instance != null;
	}
	
	public OMHelper(Map config)
	{
		if(instance != null)
		{			
			throw new IllegalStateException("OMHelper has already been configured via its constructor.");
		}
		else
		{
			this.config = config;
		    instance = this;	
		    
		}
		
		
	}
    
    /**
     * @see org.picocontainer.Startable#start()
     */
    public void start()
    {
    	Iterator keys = this.config.keySet().iterator();
    	while(keys.hasNext())
    	{
    		String omKey = (String) keys.next();
    		String omConfigPath = (String) config.get(omKey);
			Properties props = new Properties();
			FileInputStream fis = null;
			try
            {
                fis = new FileInputStream(omConfigPath);
                props.load(fis);
                Parameter[] params = new Parameter[] {new ConstantParameter(props)};
                registerComponentImplementation(omKey, OMFactoryComponentImpl.class, params);
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            finally
            {
            	if(fis != null )
            	{
            		try
                    {
                        fis.close();
                    }
                    catch (IOException e1)
                    {
                       
                    }
            	}
            }
          
    	}
        
        super.start();
    }

}
