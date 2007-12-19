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
package org.apache.jetspeed.security.impl;

import java.util.prefs.Preferences;

import javax.security.auth.Subject;

import org.apache.jetspeed.security.User;

/**
 * <p>A user made of a {@link Subject} and the user {@link Preferences}.</p>
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class UserImpl implements User
{
    private Subject subject;
    private Preferences preferences;

    /**
     * <p>Default constructor.</p>
     */
    public UserImpl()
    {
    }
    
    /**
     * <p>{@link User} constructor given a subject and preferences.</p>
     * @param subject The subject.
     * @param preferences The preferences.
     */
    public UserImpl(Subject subject, Preferences preferences)
    {
        this.subject = subject;
        this.preferences = preferences;
    }

    /**
     * @see org.apache.jetspeed.security.User#getSubject()
     */
    public Subject getSubject()
    {
        return subject;
    }

    /**
     * @see org.apache.jetspeed.security.User#setSubject(javax.security.auth.Subject)
     */
    public void setSubject(Subject subject)
    {
        this.subject = subject;
    }

    /**
     * @see org.apache.jetspeed.security.User#getPreferences()
     */
    public Preferences getPreferences()
    {
        return preferences;
    }

    /**
     * @see org.apache.jetspeed.security.User#setPreferences(java.util.prefs.Preferences)
     */
    public void setPreferences(Preferences preferences)
    {
        this.preferences = preferences;
    }

    public Preferences getUserAttributes()
    {
        if (preferences != null)
        {
            return preferences.node(USER_INFO_PROPERTY_SET);
        }
        return null;
    }
}