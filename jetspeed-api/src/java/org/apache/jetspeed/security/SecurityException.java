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
import org.apache.jetspeed.i18n.KeyedMessage;

/**
 * <p>Exception throwns by members of the security service.</p>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class SecurityException extends JetspeedException
{
    /** <p>Principal does not exist exception message.</p> */
    public static final KeyedMessage PRINCIPAL_DOES_NOT_EXIST = new KeyedMessage("The principal {0} does not exist.");

    /** <p>Permission does not exist exception message.</p> */
    public static final KeyedMessage PERMISSION_DOES_NOT_EXIST = new KeyedMessage("The permission {0} does not exist.");
    
    /** <p>User principal already exists exception message.</p> */
    public static final KeyedMessage USER_ALREADY_EXISTS = new KeyedMessage("The user {0} already exists.");

    /** <p>User principal does not exist exception message.</p> */
    public static final KeyedMessage USER_DOES_NOT_EXIST = new KeyedMessage("The user {0} does not exist.");

    /** <p>Role principal already exists exception message.</p> */
    public static final KeyedMessage ROLE_ALREADY_EXISTS = new KeyedMessage("The role {0} already exists.");

    /** <p>Role principal does not exist exception message.</p> */
    public static final KeyedMessage ROLE_DOES_NOT_EXIST = new KeyedMessage("The role {0} does not exist.");

    /** <p>Group principal already exists exception message.</p> */
    public static final KeyedMessage GROUP_ALREADY_EXISTS = new KeyedMessage("The group {0} already exists.");

    /** <p>Group principal does not exist exception message.</p> */
    public static final KeyedMessage GROUP_DOES_NOT_EXIST = new KeyedMessage("The group {0} does not exist.");

    /** <p>Invalid password exception message.</p> */
    public static final KeyedMessage EMPTY_PARAMETER = new KeyedMessage("Invalid null or empty parameter {0}.");

    /** <p>Invalid password exception message.</p> */
    public static final KeyedMessage INVALID_PASSWORD = new KeyedMessage("Invalid password.");

    /** <p>Invalid new password exception message.</p> */
    public static final KeyedMessage INVALID_NEW_PASSWORD = new KeyedMessage("Invalid new password.");

    /** <p>Incorrect password exception message.</p> */
    public static final KeyedMessage INCORRECT_PASSWORD = new KeyedMessage("Incorrect password.");

    /** <p>Password required exception message.</p> */
    public static final KeyedMessage PASSWORD_REQUIRED = new KeyedMessage("Password required.");
    
    /** <p>Invalid authentication provider exception message.</p> */
    public static final KeyedMessage INVALID_AUTHENTICATION_PROVIDER = new KeyedMessage("Invalid authentication provider {0}.");    

    /** <p>Password already used exception message.</p> */
    public static final KeyedMessage PASSWORD_ALREADY_USED = new KeyedMessage("Password already used.");

    /** <p>The anonymous user is protected exception message.</p> */
    public static final KeyedMessage ANONYMOUS_USER_PROTECTED = new KeyedMessage("The user {0} is protected.");

    /** <p>The anonymous user is protected exception message.</p> */
    public static final KeyedMessage UNEXPECTED = new KeyedMessage("Unexpected security error at {0} from {1}: {2}");

    /**
     * <p>Default Constructor.</p>
     */
    public SecurityException()
    {
        super();
    }

    public SecurityException(Throwable t)
    {
        super(t);
    }
    
    /**
     * <p>Constructor with exception message.</p>
     * @param message The exception message.
     */
    public SecurityException(KeyedMessage typedMessage)
    {
        super(typedMessage);
    }

    /**
     * <p>Constructor with exception message and nested exception.</p>
     * @param msg The exception message.
     * @param nested Nested exception.
     */
    public SecurityException(KeyedMessage msg, Throwable nested)
    {
        super(msg, nested);
    }

}
