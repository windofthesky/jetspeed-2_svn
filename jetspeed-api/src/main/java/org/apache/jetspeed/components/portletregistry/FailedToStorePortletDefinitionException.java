/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.jetspeed.components.portletregistry;

import org.apache.jetspeed.om.portlet.PortletDefinition;

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
    private static final long serialVersionUID = -4999734419792110127L;

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
        this("Unable to store portlet definition "+portlet.getPortletName()+".  Reason: "+nested.toString(), nested);
     
    }
    
    public FailedToStorePortletDefinitionException( PortletDefinition portlet, String reason )
    {        
        this("Unable to store portlet definition "+portlet.getPortletName()+".  Resaon: "+reason);     
    }

}
