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
package org.apache.jetspeed.tools.pamanager;

import org.apache.jetspeed.exception.JetspeedException;

/**
 * Occurs when anything unexpected happens within the Portlet Application Manager.
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:roger.ruttimann@earthlink.net">Roger Ruttimann</a> 
 * @version $Id: PortletApplicationException.java 185962 2004-03-08 01:03:33Z jford $
 **/

public class PortletApplicationException extends JetspeedException 
{

    public PortletApplicationException() 
    {
        super();
    }
    
    public PortletApplicationException(String message) 
    {
        super(message);
    }
    
    public PortletApplicationException(Throwable nested)
    {
        super(nested);
    }
    
    public PortletApplicationException(String msg, Throwable nested)
    {
        super(msg, nested);
    }
    


}

