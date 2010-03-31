/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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
    /** The serial version uid. */
    private static final long serialVersionUID = -8823877029853488430L;

    /** <p>Principal does not exist exception message.</p> */
    public static final KeyedMessage PRINCIPAL_DOES_NOT_EXIST = new KeyedMessage("The principal {0} does not exist.");

    /** <p>Principal already exists exception message.</p> */
    public static final KeyedMessage PRINCIPAL_ALREADY_EXISTS = new KeyedMessage("The principal {0} already exists.");
    
    /** <p>Principal is read only exception message.</p> */
    public static final KeyedMessage PRINCIPAL_IS_READ_ONLY = new KeyedMessage("The principal {0} is read-only.");

    /** <p>Principal updating failed exception message.</p> */
    public static final KeyedMessage PRINCIPAL_UPDATE_FAILURE = new KeyedMessage("Failed to update principal {0}.");
    
    /** <p>Principal is not removable exception message.</p> */
    public static final KeyedMessage PRINCIPAL_NOT_REMOVABLE = new KeyedMessage("The principal {0} cannot be removed.");
    
    /** <p>Principal has one or more dependents. */
    public static final KeyedMessage DEPENDENT_PRINCIPAL_EXISTS = new KeyedMessage("The principal {0} has one or more required {1} {2} associations.");
    
    /** <p>Principal association required exception message.</p> */
    public static final KeyedMessage PRINCIPAL_ASSOCIATION_REQUIRED = new KeyedMessage("A {0} {1} association is required.");
    
    /** <p>Principal association singular for same types exception message.</p> */
    public static final KeyedMessage PRINCIPAL_ASSOCIATION_SINGULAR = new KeyedMessage("Only one {0} association is allowed from principal {1} to another {2}.");
    
    /** <p>Principal association dominant for same types exception message.</p> */
    public static final KeyedMessage PRINCIPAL_ASSOCIATION_DOMINANT = new KeyedMessage("Only one {0} association is allowed to principal {1} from another {2}.");
    
    /** <p>Principal association singular for mixed types exception message.</p> */
    public static final KeyedMessage PRINCIPAL_ASSOCIATION_SINGULAR_MIXED = new KeyedMessage("Only one {0} association is allowed from principal {1} to a {2}.");
    
    /** <p>Principal association dominant for mixed types exception message.</p> */
    public static final KeyedMessage PRINCIPAL_ASSOCIATION_DOMINANT_MIXED = new KeyedMessage("Only one {0} association is allowed to principal {1} from a {2}.");
    
    /** <p>Principal association already exist with principal</p> */
    public static final KeyedMessage PRINCIPAL_ASSOCIATION_ALREADY_EXISTS = new KeyedMessage("Principal {0} already has a {1} association with {2}.");
    
    /** <p>Principal association is not supported exception message.</p> */
    public static final KeyedMessage PRINCIPAL_ASSOCIATION_UNSUPPORTED = new KeyedMessage("A {0} {1} association is not supported.");
    
    /** <p>Permission does not exist exception message.</p> */
    public static final KeyedMessage PERMISSION_DOES_NOT_EXIST = new KeyedMessage("The permission {0} does not exist.");
    
    /** <p>Permission already exists exception message.</p> */
    public static final KeyedMessage PERMISSION_ALREADY_EXISTS = new KeyedMessage("The permission {0} already exists.");
    
    /**
     * <p>User principal already exists exception message.</p>
     * @deprecated use {@link #PRINCIPAL_ALREADY_EXISTS} with method {@link KeyedMessage#createScoped(String, Object)} instead
     */
    public static final KeyedMessage USER_ALREADY_EXISTS = new KeyedMessage("The user {0} already exists.");

    /**
     * <p>User principal does not exist exception message.</p>
     * @deprecated use {@link #PRINCIPAL_DOES_NOT_EXIST} with method {@link KeyedMessage#createScoped(String, Object)} instead
     */
    public static final KeyedMessage USER_DOES_NOT_EXIST = new KeyedMessage("The user {0} does not exist.");

    /** <p>Role principal already exists exception message.</p>
     * @deprecated use {@link #PRINCIPAL_ALREADY_EXIST} with method {@link KeyedMessage#createScoped(String, Object)} instead
     */
    public static final KeyedMessage ROLE_ALREADY_EXISTS = new KeyedMessage("The role {0} already exists.");

    /** 
     * <p>Role principal does not exist exception message.</p>
     * @deprecated use {@link #PRINCIPAL_DOES_NOT_EXIST} with method {@link KeyedMessage#createScoped(String, Object)} instead
     */
    public static final KeyedMessage ROLE_DOES_NOT_EXIST = new KeyedMessage("The role {0} does not exist.");

    /** 
     * <p>Group principal already exists exception message.</p>
     * @deprecated use {@link #PRINCIPAL_ALREADY_EXIST} with method {@link KeyedMessage#createScoped(String, Object)} instead
     */
    public static final KeyedMessage GROUP_ALREADY_EXISTS = new KeyedMessage("The group {0} already exists.");

    /**
     * <p>Group principal does not exist exception message.</p>
     * @deprecated use {@link #PRINCIPAL_DOES_NOT_EXIST} with method {@link KeyedMessage#createScoped(String, Object)} instead
     */
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
    
    /** <p>The uid is invalid.</p> */
    public static final KeyedMessage INVALID_UID = new KeyedMessage("The uid cannot contain any regular expression meta-characters or be null or be empty.");

    /** <p>The dn is invalid.</p> */
    public static final KeyedMessage INVALID_DN = new KeyedMessage("The dn cannot be null or empty.");
    
    public static final KeyedMessage UNKNOWN_PRINCIPAL_TYPE = new KeyedMessage("Unknown Principal Type provided: {0}");
    
    public static final KeyedMessage ATTRIBUTE_IS_READ_ONLY = new KeyedMessage("The principal attribute {0} is read only.");
    
    public static final KeyedMessage ATTRIBUTE_IS_REQUIRED = new KeyedMessage("The principal attribute {0} is required.");
    
    public static final KeyedMessage ATTRIBUTES_ARE_READ_ONLY = new KeyedMessage("The principal attributes are read only.");

    public static final KeyedMessage ATTRIBUTES_NOT_EXTENDABLE = new KeyedMessage("Adding new principal attributes is not supported.");

    public static final KeyedMessage SECURITY_DOMAIN_EXISTS = new KeyedMessage("The security domain {0} already exists.");

    public static final KeyedMessage SECURITY_DOMAIN_DOES_NOT_EXIST = new KeyedMessage("The security domain {0} does not exist.");

    public static final KeyedMessage SECURITY_DOMAIN_NOT_REMOVABLE = new KeyedMessage("The security domain {0} could not be removed.");

    public static final KeyedMessage SECURITY_DOMAIN_UPDATE_FAILURE = new KeyedMessage("Failed to update security domain {0}.");

    public static final KeyedMessage DEFAULT_SECURITY_DOMAIN_DOES_NOT_EXIST = new KeyedMessage("The default security domain does not exist.");

    /** <p>Entity association attribute undefined</p> */
    public static final KeyedMessage ENTITY_ATTRIBUTE_UNDEFINED = new KeyedMessage("The attribute {0} is undefined for entity {1}.");
    
    /** <p>Entity association attribute undefined</p> */
    public static final KeyedMessage ENTITY_ATTRIBUTE_MULTIVALUE_UNSUPPORTED = new KeyedMessage("The attribute {0} for entity {1} doesn't support multivalues.");    
    
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
