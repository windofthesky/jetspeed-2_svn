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

import java.util.Iterator;

/**
 * <p>Describes the interface for managing users and provides access
 * to the {@link User}.</p>
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public interface UserManager
{

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
     * @throws Throws a security exception.
     */
    void removeUser(String username) throws SecurityException;

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
     * <p>Current implementation only allows for getting back all users with
     * "" as a fitler.</p>
     * TODO Complete filter implementation.
     * 
     * @param filter The filter used to retrieve matching users.
     * @return The Iterator of {@link User}.
     */
    Iterator getUsers(String filter) throws SecurityException;

    /**
     * <p>Set the user password.</p>
     * @param username The user name.
     * @param oldPassword The old password.
     * @param newPassword The new password.
     * @throws Throws a security exception.
     */
    void setPassword(String username, String oldPassword, String newPassword) throws SecurityException;
}
