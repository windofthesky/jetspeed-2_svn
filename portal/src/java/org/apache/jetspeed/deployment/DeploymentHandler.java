/**
 * Created on Jan 14, 2004
 *
 * 
 * @author
 */
package org.apache.jetspeed.deployment;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.apache.commons.configuration.Configuration;

/**
 * <p>
 * DeploymentHandler
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface DeploymentHandler
{

	
	InputStream getAsStream() throws IOException;
	
	Reader getAsReader() throws IOException;
	
	void close() throws IOException;
	
	Configuration getConfiguration(String configPath) throws IOException;
		

}
