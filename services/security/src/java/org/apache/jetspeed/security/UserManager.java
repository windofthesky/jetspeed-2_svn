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
package org.apache.jetspeed.security;

import java.util.Iterator;

import org.apache.jetspeed.cps.CommonPortletServices;

/**
 * Convenience static wrapper around {@link UserManagerService}.
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class UserManager
{

    /**
     * <p>Default Constructor.  This class contains only static
     * methods, hence users should not be allowed to instantiate it.</p>
     */
    public UserManager()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * <p>Returns the {@link UserManagerService}.</p>
     * @return The UserManagerService.
     */
    private static final UserManagerService getService()
    {
        return (UserManagerService) CommonPortletServices.getPortalService(UserManagerService.SERVICE_NAME);
    }

    /**
     * @see org.apache.jetspeed.security.UserManagerService#authenticate(java.lang.String, java.lang.String)
     */
    public static boolean authenticate(String username, String password)
    {
        return getService().authenticate(username, password);
    }

    /**
     * @see org.apache.jetspeed.security.UserManagerService#addUser(java.lang.String, java.lang.String)
     */
    public static void addUser(String username, String password) throws SecurityException
    {
        getService().addUser(username, password);
    }

    /**
     * @see org.apache.jetspeed.security.UserManagerService#removeUser(java.lang.String)
     */
    public static void removeUser(String username)
    {
        getService().removeUser(username);
    }

    /**
     * @see org.apache.jetspeed.security.UserManagerService#userExists(java.lang.String)
     */
    public boolean userExists(String username)
    {
        return getService().userExists(username);
    }

    /**
     * @see org.apache.jetspeed.security.UserManagerService#getUser(java.lang.String)
     */
    public User getUser(String username) throws SecurityException
    {
        return getService().getUser(username);
    }

    /**
     * @see org.apache.jetspeed.security.UserManagerService#getUsers(java.lang.String)
     */
    public Iterator getUsers(String filter)
    {
        return getService().getUsers(filter);
    }

    /**
     * @see org.apache.jetspeed.security.UserManagerService#setPassword(java.lang.String, java.lang.String)
     */
    public void setPassword(String username, String password) throws SecurityException
    {
        getService().setPassword(username, password);
    }

}
