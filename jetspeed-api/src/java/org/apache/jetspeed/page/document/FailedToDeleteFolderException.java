/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.page.document;


/**
 * <p>
 * FailedToDeleteFolderException
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 *
 */
public class FailedToDeleteFolderException extends NodeException
{

    /**
     * 
     */
    public FailedToDeleteFolderException()
    {
        super();
    }

    /**
     * @param message
     */
    public FailedToDeleteFolderException( String message )
    {
        super(message);
    }

    /**
     * @param nested
     */
    public FailedToDeleteFolderException( Throwable nested )
    {
        super(nested);
    }

    /**
     * @param msg
     * @param nested
     */
    public FailedToDeleteFolderException( String msg, Throwable nested )
    {
        super(msg, nested);
    }

}
