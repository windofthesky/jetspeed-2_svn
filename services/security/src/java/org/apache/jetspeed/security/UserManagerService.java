/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2004 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.security;

import java.util.Iterator;

import org.apache.jetspeed.cps.CommonService;

/**
 * <p>Describes the interface for managing users and provides access
 * to the {@link User}.</p>
 * @author <a href="mailto:david@sensova.com">David Le Strat</a>
 */
public interface UserManagerService extends CommonService
{
    /** <p>The name of the service.</p> */
    String SERVICE_NAME = "UserManager";

    /**
     * <p>Authenticate a user.</p>
     * @param username The user name.
     * @param password The user password.
     * @return Whether or not a user is authenticated.
     */
    boolean authenticate(String username, String password);

    /**
     * <p>Add a new user provided a username and password.</p>
     * @param username The user name.
     * @param password The password.
     * @throws Throws a security exception.
     */
    void addUser(String username, String password) throws SecurityException;

    /**
     * <p>Remove a user. If there is a {@link java.util.prefs.Preferences} node
     * for profile properties associated to this user, it will be removed as well.</p>
     * <p>{@link java.security.Permission} for this user will be removed as well.</p>
     * @param username The user name.
     */
    void removeUser(String username);

    /**
     * <p>Whether or not a user exists.</p>
     * @param username The user name.
     * @return Whether or not a user exists.
     */
    boolean userExists(String username);

    /**
     * <p>Get a {@link User} for a given username.</p>
     * @param username The username.
     * @return The {@link User}.
     * @throws Throws a security exception if the user cannot be found.
     */
    User getUser(String username) throws SecurityException;

    /**
     * <p>An iterator of {@link User} finding users matching the
     * corresponding filter criteria.</p>
     * @param filter The filter used to retrieve matching users.
     * @return The Iterator of {@link User}.
     */
    Iterator getUsers(String filter);

    /**
     * <p>Set the user password.</p>
     * @param username The user name.
     * @param password The password.
     * @throws Throws a security exception.
     * TODO This method should be changed to support multiple credentials.
     */
    void setPassword(String username, String password) throws SecurityException;
}
