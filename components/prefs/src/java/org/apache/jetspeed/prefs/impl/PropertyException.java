/* Copyright 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.prefs.impl;

import org.apache.jetspeed.exception.JetspeedException;

/**
 * <p>Exception throwns if a property or its set definition
 * cannot be found or already exists.</p>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class PropertyException extends JetspeedException
{

    /** Preferences node not found exception message. */
    public static final String NODE_NOT_FOUND
                        = "The specified preferences node does not exist.";
    /** Preferences node already exists exception message. */
    public static final String  NODE_ALREADY_EXISTS
                        = "The specified preferences node already exists.";
    /** Property key not found exception message. */
    public static final String PROPERTYKEY_NOT_FOUND
                        = "The specified property key does not exist.";
    /** Property key already exists exception message. */
    public static final String PROPERTYKEY_ALREADY_EXISTS
                        = "The specified property key already exists.";

    /**
     * <p>Default Constructor.</p>
     */
    public PropertyException()
    {
        super();
    }

    /**
     * <p>Constructor with exception message.</p>
     * @param message The exception message.
     */
    public PropertyException(String message)
    {
        super(message);
    }

    /**
     * <p>Constructor with nested exception.</p>
     * @param nested Nested exception.
     */
    public PropertyException(Throwable nested)
    {
        super(nested);
    }

    /**
     * <p>Constructor with exception message and nested exception.</p>
     * @param msg The exception message.
     * @param nested Nested exception.
     */
    public PropertyException(String msg, Throwable nested)
    {
        super(msg, nested);
    }

}
