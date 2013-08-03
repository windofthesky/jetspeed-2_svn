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
package org.apache.jetspeed.administration;

/**
 * Pluggable password generator service for auto-creating passwords in Jetspeed Administration. A simple password
 * generator is provided by Jetspeed. This service can be wired in through Spring and replaced by your own algorithm.
 *
 * @version $Id$
 */
public interface PasswordGenerator
{
    /**
     * Call this method to generate a new password following the credential policy of the Password Generator service
     *
     * @return a newly generated password
     */
    String generatePassword();
}