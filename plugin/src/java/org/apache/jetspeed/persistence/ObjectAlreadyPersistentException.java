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
package org.apache.jetspeed.persistence;

import org.apache.commons.lang.exception.NestableException;


/**
 * 
 * ObjectAlreadyPersistentException
 * 
 * Thrown when an object, that is already persistent, is attempted
 * make persistent again, i.e. primary key conflict
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class ObjectAlreadyPersistentException extends NestableException
{

    /**
     * 
     */
    public ObjectAlreadyPersistentException()
    {
        super();
        
    }

    /**
     * @param message
     */
    public ObjectAlreadyPersistentException(String message)
    {
        super(message);
        
    }

    /**
     * @param nested
     */
    public ObjectAlreadyPersistentException(Throwable nested)
    {
        super(nested);        
    }

    /**
     * @param msg
     * @param nested
     */
    public ObjectAlreadyPersistentException(String msg, Throwable nested)
    {
        super(msg, nested);        
    }

}
