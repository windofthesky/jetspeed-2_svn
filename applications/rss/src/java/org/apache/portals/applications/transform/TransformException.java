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
package org.apache.portals.applications.transform;


/**
 * TransformException
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class TransformException extends Exception
{
    /**
     * Constructs a new <code>TransformException</code> without specified detail
     * message.
     */
    public TransformException()
    {
    }

    /**
     * Constructs a new <code>TransformException</code> with specified detail
     * message.
     *
     * @param msg the error message.
     */
    public TransformException(String msg)
    {
        super(msg);
    }

    /**
     * Constructs a new <code>TransformException</code> with specified nested
     * <code>Throwable</code>.
     *
     * @param nested the exception or error that caused this exception
     *               to be thrown.
     */
    public TransformException(Throwable nested)
    {
        super(nested);
    }

    /**
     * Constructs a new <code>TransformException</code> with specified detail
     * message and nested <code>Throwable</code>.
     *
     * @param msg the error message.
     * @param nested the exception or error that caused this exception
     *               to be thrown.
     */
    public TransformException(String msg, Throwable nested)
    {
        super(msg, nested);
    }
}
