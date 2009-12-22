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
package org.apache.jetspeed.layout.impl;

import java.security.AccessController;
import java.security.Principal;

import javax.security.auth.Subject;

import org.apache.jetspeed.security.JSSubject;
import org.apache.jetspeed.security.SubjectHelper;
import org.apache.jetspeed.security.User;

/**
 * Page layout component utilities.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id:$
 */
public interface PageLayoutComponentUtils
{
    public static class Utils
    {
        /**
         * Test string for null or empty value.
         * 
         * @param value string value to test
         * @return null or empty flag
         */
        public static boolean isNull(String value)
        {
            return ((value == null) || (value.length() == 0));
        }

        /**
         * Test integer null value.
         * 
         * @param value integer value to test
         * @return null flag
         */
        public static boolean isNull(int value)
        {
            return (value < 0);
        }

        /**
         * Test float null value.
         * 
         * @param value float value to test
         * @return null flag
         */
        public static boolean isNull(float value)
        {
            return (value < 0);
        }

        /**
         * Lookup current default user scope value.
         * 
         * @return current user principal name
         */
        public static String getCurrentUserScopeValue()
        {
            // lookup current user principal using subject
            Subject subject = JSSubject.getSubject(AccessController.getContext());
            if (subject != null)
            {
                Principal userPrincipal = SubjectHelper.getBestPrincipal(subject, User.class);
                if (userPrincipal != null)
                {
                    return userPrincipal.getName();
                }
            }
            return null;
        }
    }
}
