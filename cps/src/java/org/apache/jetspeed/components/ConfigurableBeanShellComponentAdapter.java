
package org.apache.jetspeed.components;

import org.apache.commons.configuration.Configuration;
import org.nanocontainer.script.bsh.BeanShellComponentAdapter;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;

/**
 * <p>
 * ConfigurableBeanShellComponentAdapter
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class ConfigurableBeanShellComponentAdapter extends BeanShellComponentAdapter implements ComponentAdapter
{
	
	private Configuration conf;



    /**
     * @param arg0
     * @param arg1
     * @param arg2
     */
    public ConfigurableBeanShellComponentAdapter(Object arg0, Class arg1, Parameter[] arg2, Configuration conf)
    {
        super(arg0, arg1, arg2);
        this.conf = conf;
    }
    
	public Configuration getConfiguration()
	{
		return conf;
	}



    

}
