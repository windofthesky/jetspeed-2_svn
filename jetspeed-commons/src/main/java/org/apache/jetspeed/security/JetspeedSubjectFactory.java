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

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.Subject;

/**
 * @version $Id$
 *
 */
public class JetspeedSubjectFactory
{
    public static Subject createSubject(AuthenticatedUser user, Set<Principal> principals)
    {
        return createSubject(user.getUser(), user.getPublicCredentials(), user.getPrivateCredentials(), principals);
    }
    
    public static Subject createSubject(User user, Set<Object> publicCredentials, Set<Object> privateCredentials, Set<Principal> principals)
    {
        UserSubjectPrincipalImpl userPrincipal = new UserSubjectPrincipalImpl(user);
        Set<Principal> subjectPrincipals = principals == null || principals.isEmpty() ? new HashSet<Principal>() : new PrincipalsSet();
        subjectPrincipals.add(userPrincipal);
        subjectPrincipals.add(user);
        if (principals != null)
        {
            subjectPrincipals.addAll(principals);
        }
        Set<Object> pubCred = publicCredentials == null || publicCredentials.isEmpty() ? Collections.EMPTY_SET : new HashSet<Object>(publicCredentials);
        Set<Object> privCred = privateCredentials == null || privateCredentials.isEmpty() ? Collections.EMPTY_SET : new HashSet<Object>(privateCredentials);
        Subject subject = new Subject(true, subjectPrincipals, pubCred, privCred);
        userPrincipal.setSubject(subject);
        return subject;
    }
}
