/**
 * Created on Jan 15, 2004
 *
 * 
 * @author
 */
package org.apache.jetspeed.deployment;

import org.apache.jetspeed.cps.CommonService;

/**
 * <p>
 * AutoDeploymentService
 * </p>
 * <p>
 *   Simple service that watches and deploys
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface AutoDeploymentService extends CommonService
{
	String SERVICE_NAME = "autodeployment";
	

}
