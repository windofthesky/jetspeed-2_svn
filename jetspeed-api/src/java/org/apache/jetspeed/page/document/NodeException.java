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
/*
 * Created on Sep 2, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.page.document;

import org.apache.jetspeed.exception.JetspeedException;

/**
 * <p>
 * NodeException
 * </p>
 * <p>
 * Note that these exceptions are assumed to be "unexpected" and/or
 * fatal; use NodeNotFoundException or other exceptions derived from
 * the base JetspeedException for "informational" exceptions.
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class NodeException extends JetspeedException
{

    /**
     * 
     */
    public NodeException()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public NodeException( String message )
    {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param nested
     */
    public NodeException( Throwable nested )
    {
        super(nested);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param msg
     * @param nested
     */
    public NodeException( String msg, Throwable nested )
    {
        super(msg, nested);
        // TODO Auto-generated constructor stub
    }

}
