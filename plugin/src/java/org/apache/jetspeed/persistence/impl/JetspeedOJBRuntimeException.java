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
package org.apache.jetspeed.persistence.impl;

import org.apache.commons.lang.exception.NestableRuntimeException;


/**
 * Thrown when an unexpected error, unrelated to application 
 * logic, happens while using OJB.
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 */
public class JetspeedOJBRuntimeException extends NestableRuntimeException
{

    /**
     * 
     */
    public JetspeedOJBRuntimeException()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public JetspeedOJBRuntimeException(String arg0)
    {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public JetspeedOJBRuntimeException(Throwable arg0)
    {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     * @param arg1
     */
    public JetspeedOJBRuntimeException(String arg0, Throwable arg1)
    {
        super(arg0, arg1);
        // TODO Auto-generated constructor stub
    }

}
