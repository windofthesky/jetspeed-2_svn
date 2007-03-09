/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.components.persistence.store;

import org.apache.jetspeed.exception.JetspeedRuntimeException;

/**
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * 
 * Thrown when an unexpected problem arises while performing a 
 * persistence operation.
 *
 */
public class PersistenceStoreRuntimeExcpetion extends JetspeedRuntimeException
{

    /**
     * 
     */
    public PersistenceStoreRuntimeExcpetion()
    {
        super();
    }

    /**
     * @param message
     */
    public PersistenceStoreRuntimeExcpetion( String message )
    {
        super(message);
    }

    /**
     * @param nested
     */
    public PersistenceStoreRuntimeExcpetion( Throwable nested )
    {
        super(nested);
    }

    /**
     * @param msg
     * @param nested
     */
    public PersistenceStoreRuntimeExcpetion( String msg, Throwable nested )
    {
        super(msg, nested);
    }

}
