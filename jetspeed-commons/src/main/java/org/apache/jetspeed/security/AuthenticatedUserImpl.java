/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import java.util.HashSet;
import java.util.Set;


/**
 * @version $Id$
 *
 */
public class AuthenticatedUserImpl implements AuthenticatedUser
{
    private User user;
    private Set<Object> publicCredentials;
    private Set<Object> privateCredentials;
    
    public AuthenticatedUserImpl(User user, UserCredential privateCredential)
    {
        this.user = user;
        this.privateCredentials = new HashSet<Object>(1);
        this.privateCredentials.add(privateCredential);
    }
    
    public AuthenticatedUserImpl(User user, Set<Object> publicCredentials, Set<Object> privateCredentials)
    {
        this.user = user;
        this.publicCredentials = publicCredentials;
        this.privateCredentials = privateCredentials;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.spi.AuthenticatedUser#getPrivateCredentials()
     */
    public Set<Object> getPrivateCredentials()
    {
        return privateCredentials;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.spi.AuthenticatedUser#getPublicCredentials()
     */
    public Set<Object> getPublicCredentials()
    {
        return publicCredentials;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.spi.AuthenticatedUser#getUser()
     */
    public User getUser()
    {
        return user;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.spi.AuthenticatedUser#getUserName()
     */
    public String getUserName()
    {
        return user.getName();
    }
}
