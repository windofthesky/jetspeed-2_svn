/*
 * Created on Oct 22, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.components.portletregistry;

import org.apache.pluto.om.portlet.PortletDefinition;

/**
 * <p>
 * FailedToStorePortletDefinitionException
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class FailedToStorePortletDefinitionException extends RegistryException
{

    /**
     * 
     */
    public FailedToStorePortletDefinitionException()
    {
        super();
     
    }

    /**
     * @param message
     */
    public FailedToStorePortletDefinitionException( String message )
    {
        super(message);
     
    }

    /**
     * @param nested
     */
    public FailedToStorePortletDefinitionException( Throwable nested )
    {
        super(nested);
     
    }

    /**
     * @param msg
     * @param nested
     */
    public FailedToStorePortletDefinitionException( String msg, Throwable nested )
    {
        super(msg, nested);
     
    }
    
    public FailedToStorePortletDefinitionException( PortletDefinition portlet, Throwable nested )
    {        
        this("Unable to store portlet definition "+portlet.getName()+".  Reason: "+nested.toString(), nested);
     
    }
    
    public FailedToStorePortletDefinitionException( PortletDefinition portlet, String reason )
    {        
        this("Unable to store portlet definition "+portlet.getName()+".  Resaon: "+reason);     
    }

}
