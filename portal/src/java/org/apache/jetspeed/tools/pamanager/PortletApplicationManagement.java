/*
 * Created on Jun 7, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.tools.pamanager;

import org.apache.pluto.om.portlet.PortletDefinition;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver</a>
 *
 */
public interface PortletApplicationManagement extends Deployment, Registration // , Lifecycle
{	
    /**
     * 
     * <p>
     * clearPortletEntities
     * </p>
     * 
     * Removes ALL portlet entity and user preference information for a specific
     * {@link org.apache.pluto.om.portlet.PortletDefinition}. The removal 
     * <strong>IS PERMENANT</strong> short of restoring a image of persistence that
     * was taken BEFORE the removal occurred.
     *
     * @param portlet
     */
    void clearPortletEntities(PortletDefinition portlet);
}
