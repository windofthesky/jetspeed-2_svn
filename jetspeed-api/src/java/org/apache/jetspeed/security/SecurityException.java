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

    /** <p>Permission does not exist exception message.</p> */
    public static final String PERMISSION_DOES_NOT_EXIST = "The permission does not exist.";
    
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

    /** <p>Invalid password exception message.</p> */
    public static final String INVALID_PASSWORD = "Invalid password.";

    /** <p>Invalid new password exception message.</p> */
    public static final String INVALID_NEW_PASSWORD = "Invalid new password.";

    /** <p>Incorrect password exception message.</p> */
    public static final String INCORRECT_PASSWORD = "Incorrect password.";

    /** <p>Password required exception message.</p> */
    public static final String PASSWORD_REQUIRED = "Password required.";
    
    /** <p>Invalid authentication provider exception message.</p> */
    public static final String INVALID_AUTHENTICATION_PROVIDER = "Invalid authentication provider.";    

    /** <p>Password already used exception message.</p> */
    public static final String PASSWORD_ALREADY_USED = "Password already used.";

    /** <p>The anonymous user is protected exception message.</p> */
    public static final String ANONYMOUS_USER_PROTECTED = "The anonymous user is protected.";

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
