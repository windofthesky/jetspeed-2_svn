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
 * Created on Jan 13, 2004
 *
 * 
 * @author
 */
package org.apache.jetspeed.deployment.simpleregistry;

import org.apache.jetspeed.exception.JetspeedException;

/**
 * <p>
 * SimpleRegistryException
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class SimpleRegistryException extends JetspeedException
{

    /**
     * 
     */
    public SimpleRegistryException()
    {
        super();        
    }

    /**
     * @param message
     */
    public SimpleRegistryException(String message)
    {
        super(message);        
    }

    /**
     * @param nested
     */
    public SimpleRegistryException(Throwable nested)
    {
        super(nested);        
    }

    /**
     * @param msg
     * @param nested
     */
    public SimpleRegistryException(String msg, Throwable nested)
    {
        super(msg, nested);        
    }

}
