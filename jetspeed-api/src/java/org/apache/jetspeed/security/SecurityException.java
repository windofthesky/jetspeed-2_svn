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
package org.apache.jetspeed.security;

import org.apache.jetspeed.exception.JetspeedException;

/**
 * <p>Exception throwns by members of the security service.</p>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class SecurityException extends JetspeedException
{

    /** <p>Principal does not exist exception message.</p> */
    public static final String PRINCIPAL_DOES_NOT_EXIST = "The principal does not exist.";

    /** <p>User principal already exists exception message.</p> */
    public static final String USER_ALREADY_EXISTS = "The user already exists.";

    /** <p>User principal does not exist exception message.</p> */
    public static final String USER_DOES_NOT_EXIST = "The user does not exist.";

    /** <p>Role principal already exists exception message.</p> */
    public static final String ROLE_ALREADY_EXISTS = "The role already exists.";

    /** <p>Role principal does not exist exception message.</p> */
    public static final String ROLE_DOES_NOT_EXIST = "The role does not exist.";

    /** <p>Group principal already exists exception message.</p> */
    public static final String GROUP_ALREADY_EXISTS = "The group already exists.";

    /** <p>Group principal does not exist exception message.</p> */
    public static final String GROUP_DOES_NOT_EXIST = "The group does not exist.";

    /**
     * <p>Default Constructor.</p>
     */
    public SecurityException()
    {
        super();
    }

    /**
     * <p>Constructor with exception message.</p>
     * @param message The exception message.
     */
    public SecurityException(String message)
    {
        super(message);
    }

    /**
     * <p>Constructor with nested exception.</p>
     * @param nested Nested exception.
     */
    public SecurityException(Throwable nested)
    {
        super(nested);
    }

    /**
     * <p>Constructor with exception message and nested exception.</p>
     * @param msg The exception message.
     * @param nested Nested exception.
     */
    public SecurityException(String msg, Throwable nested)
    {
        super(msg, nested);
    }

}
