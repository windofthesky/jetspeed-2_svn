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

package org.apache.cornerstone.framework.api.core;

public class BaseException extends Exception
{
    public static final String REVISION = "$Revision$";

    public BaseException(String msg)
    {
        super(msg);
    }

    /**
     * Constructs with another exception as root cause.
     * @param rootCause root cause of this service exception
     */
    public BaseException(Throwable cause)
    {
        super(cause);
    }

    public BaseException(String msg, Throwable cause)
    {
        super(msg, cause);
    }

    /**
     * Gets root cause.
     * @return root cause.
     */
    public Throwable getCause()
    {
        Throwable cause = super.getCause();
        if (cause == null)
            return this;
        else if (cause instanceof BaseException)
            return ((BaseException) cause).getCause();
        else
            return cause;
    }
}