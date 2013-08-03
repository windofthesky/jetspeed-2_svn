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
/**
 * Created on Jan 16, 2004
 *
 * 
 * @author
 */
package org.apache.jetspeed.aggregator;

import org.apache.jetspeed.exception.JetspeedException;

/**
 * Represents an exception when trying to render a portlet, but the current user did not have sufficient security
 * access privileges to render the portlet. Thus an access denied exception is thrown.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 *
 */
public class PortletAccessDeniedException extends JetspeedException
{


    /**
     * 
     */
    public PortletAccessDeniedException()
    {
        super();
        
    }

    /**
     * @param message
     */
    public PortletAccessDeniedException(String message)
    {
        super(message);
        
    }

    /**
     * @param nested
     */
    public PortletAccessDeniedException(Throwable nested)
    {
        super(nested);
        
    }

    /**
     * @param msg
     * @param nested
     */
    public PortletAccessDeniedException(String msg, Throwable nested)
    {
        super(msg, nested);
        
    }

}
