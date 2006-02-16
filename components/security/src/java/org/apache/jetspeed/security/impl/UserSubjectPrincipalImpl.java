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
package org.apache.jetspeed.security.impl;

import javax.security.auth.Subject;

import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.UserSubjectPrincipal;

/**
 * <p>{@link UserPrincipal} interface implementation.</p>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: UserPrincipalImpl.java 331065 2005-11-06 03:40:32Z dlestrat $
 */
public class UserSubjectPrincipalImpl extends UserPrincipalImpl implements UserSubjectPrincipal
{

    /** The serial version uid. */
    private static final long serialVersionUID = 4134905654850335230L;
    protected Subject subject;

    /**
     * <p>The user principal constructor.</p>
     * @param userName The user principal name.
     */
    public UserSubjectPrincipalImpl(String userName, Subject subject)
    {
        super(userName);   
        this.subject = subject;
    }

    
    public Subject getSubject()
    {
        return subject;
    }

}
