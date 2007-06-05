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
package org.apache.jetspeed.login;


/**
 * LoginConstants
 *
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public final class LoginConstants
{
    public final static String USERNAME    = "org.apache.jetspeed.login.username";
    public final static String PASSWORD    = "org.apache.jetspeed.login.password";
    public final static String DESTINATION = "org.apache.jetspeed.login.destination";
    public final static String RETRYCOUNT  = "org.apache.jetspeed.login.retrycount";
    public final static String ERRORCODE   = "org.apache.jetspeed.login.errorcode";
    public final static String LOGIN_CHECK = "org.apache.jetspeed.login.check";
    
    public final static Integer ERROR_UNKNOWN_USER = new Integer(1);
    public final static Integer ERROR_INVALID_PASSWORD = new Integer(2);
    public final static Integer ERROR_USER_DISABLED = new Integer(3);
    public final static Integer ERROR_FINAL_LOGIN_ATTEMPT = new Integer(4);
    public final static Integer ERROR_CREDENTIAL_DISABLED = new Integer(5);
    public final static Integer ERROR_CREDENTIAL_EXPIRED = new Integer(6);
}
