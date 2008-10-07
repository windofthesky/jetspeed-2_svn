/**
 * Created on Dec 24, 2003
 *
 * 
 * @author
 */
package org.apache.jetspeed.deployment.fs;

import java.io.File;
import java.io.IOException;

import org.apache.jetspeed.deployment.DeploymentHandler;


/**
 * <p>
 * FSObjectHandler
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface FSObjectHandler extends DeploymentHandler
{
	String getPath();
	
	void setPath(String path);
	
	void setFile(File file) throws IOException;
	
	File getFile();
	

}
