/**
 * Created on Dec 24, 2003
 *
 * 
 * @author
 */
package org.apache.jetspeed.deployment.fs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * <p>
 * FSObjectHandler
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface FSObjectHandler
{
	String getPath();
	
	void setPath(String path);
	
	void setFile(File file);
	
	InputStream getAsStream() throws IOException;
	
	Reader getAsReader() throws IOException;
	
	void close() throws IOException;

}
