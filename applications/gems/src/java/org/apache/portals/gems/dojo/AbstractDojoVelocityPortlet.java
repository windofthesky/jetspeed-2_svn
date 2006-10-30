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
package org.apache.portals.gems.dojo;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.headerresource.HeaderResource;
import org.apache.jetspeed.portlet.PortletHeaderRequest;
import org.apache.jetspeed.portlet.PortletHeaderResponse;
import org.apache.jetspeed.portlet.SupportsHeaderPhase;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;

/**
 * Abstract DOJO portlet for inserting in cross context dojo widget includes
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public abstract class AbstractDojoVelocityPortlet extends GenericVelocityPortlet implements SupportsHeaderPhase 
{    
    /*
     * Class specific logger.
     */
    private final static Log log = LogFactory.getLog(AbstractDojoVelocityPortlet.class);

    /*
     * Portlet constructor.
     */
    public AbstractDojoVelocityPortlet() 
    {
        super();
    }

    /*
     * Include Dojo and Turbo header content using header resource component.
     *
     * @param request render request
     * @param response render response
     */    
    public void doHeader( PortletHeaderRequest request, PortletHeaderResponse response )
    throws PortletException
    {
        // use header resource component to ensure header logic is included only once
        HeaderResource headerResource = response.getHeaderResource();

        headerResource.dojoEnable();
        includeHeaderContent( headerResource );
    }
    
    protected void includeHeaderContent( HeaderResource headerResource )
    {
        // do nothing - intended for derived classes
    }
    
    protected void appendHeaderText(StringBuffer headerInfoText, String header)
    {
        headerInfoText.append("dojo.require('" + header + "');\r\n");
    }
}
