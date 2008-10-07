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

/**
 * <p>
 * DeploymentHandler
 * </p>
 * <p> 
 *   Object representation of a deployment artifact of some type.
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface DeploymentHandler
{

	/**
	 * Gets the deployment artifacts content as a Stream
	 * @return
	 * @throws IOException
	 */
	InputStream getAsStream() throws IOException;
	
	Reader getAsReader() throws IOException;
	
	/**
	 * Closes any resources that may have been opend during the use
	 * of this ObjectHandler.
	 * @throws IOException
	 */
	void close() throws IOException;
	
	/**
	 * retreives the the configuration for this deployment artifact
	 * based on the artifact-relative <code>configPath</code>
	 * provided.
	 * @param configPath artifcat-relative path to the confiuration file
	 * @return Configuration of this artificat or <code>null</code> if the 
	 * configuration is not present in the artifcat.
	 * @throws IOException error opening the configuration
	 */
	InputStream getConfiguration(String configPath) throws IOException;
		

}
