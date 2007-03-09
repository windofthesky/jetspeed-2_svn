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
package org.apache.jetspeed.page;

import org.apache.jetspeed.page.document.NodeException;

/**
 * <p>
 * FolderNotRemovedException
 * </p>
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 *
 */
public class FolderNotRemovedException extends NodeException
{

    /**
     * 
     */
    public FolderNotRemovedException()
    {
        super();
    }

    /**
     * @param message
     */
    public FolderNotRemovedException(String message)
    {
        super(message);
    }

    /**
     * @param nested
     */
    public FolderNotRemovedException(Throwable nested)
    {
        super(nested);
    }

    /**
     * @param msg
     * @param nested
     */
    public FolderNotRemovedException(String msg, Throwable nested)
    {
        super(msg, nested);
    }

}
