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
package org.apache.jetspeed.page;

import org.apache.jetspeed.exception.JetspeedException;

/**
 * <p>
 * FolderNotUpdatedException
 * </p>
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 *
 */
public class FolderNotUpdatedException extends JetspeedException
{

    /**
     * 
     */
    public FolderNotUpdatedException()
    {
        super();
    }

    /**
     * @param message
     */
    public FolderNotUpdatedException(String message)
    {
        super(message);
    }

    /**
     * @param nested
     */
    public FolderNotUpdatedException(Throwable nested)
    {
        super(nested);
    }

    /**
     * @param msg
     * @param nested
     */
    public FolderNotUpdatedException(String msg, Throwable nested)
    {
        super(msg, nested);
    }

}
