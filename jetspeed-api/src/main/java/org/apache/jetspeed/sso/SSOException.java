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

package org.apache.jetspeed.sso;

import org.apache.jetspeed.exception.JetspeedException;

/**
* <p>Exception throwns by members of the sso service.</p>
*
* @author <a href="mailto:rogerrut	@apache.org">Roger Ruttimann</a>
*/

public class SSOException extends JetspeedException {

   /** <p>Adding the credentials to the request failed.</p> */
   public static final String BASIC_AUTHENTICATION_ADD_FAILED = "Adding the credentials to the request failed.";

   /** <p>The site could not be created because a site with the same name exists.</p> */
   public static final String SITE_ALREADY_EXISTS = "The site could not be created because a site with the same name exists.";

   /** <p>The site could not be created because a site with the same name exists.</p> */
   public static final String SITE_COULD_NOT_BE_CREATED = "The site could not be created.";

   /** <p>The site has no Single Sign On credentails attached.</p> */
   public static final String NO_CREDENTIALS_FOR_SITE = "The site has no Single Sign On credentails attached.";

   /** <p>Adding the credentials for site failed.</p> */
   public static final String FAILED_ADDING_CREDENTIALS_FOR_SITE = "Adding the credential for site failed.";
   
   /** <p>Removing the credential for site failed.</p> */
   public static final String FAILED_REMOVING_CREDENTIALS_FOR_SITE = "Removing the credential for site failed.";
   
   /** <p>Failed to store site info in database.</p> */
   public static final String FAILED_STORING_SITE_INFO_IN_DB = "Failed to store site info in database.";
   
   /** <p>Requested principal doesn't exist in Principal store.</p> */
   public static final String REQUESTED_PRINCIPAL_DOES_NOT_EXIST = "Requested principal doesn't exist in Principal store.";
   
   /** <p>Could not remove Principla from SITE mapping table.</p> */
   public static final String FAILED_REMOVING_PRINCIPAL_FROM_MAPPING_TABLE_FOR_SITE = "Could not remove Principal from SITE mapping table.";
   
   /** <p>Could not add Principal from SITE mapping table.</p> */
   public static final String FAILED_ADDING_PRINCIPAL_TO_MAPPING_TABLE_FOR_SITE = "Could not add Principal from SITE mapping table.";
   
   /** <p>Site/principal has remote principal. Calll update.</p> */
   public static final String REMOTE_PRINCIPAL_EXISTS_CALL_UPDATE = "Remote principal for site/principal already exists. Call update instead";
   
   /**
    * <p>Default Constructor.</p>
    */
   public SSOException()
   {
       super();
   }

   /**
    * <p>Constructor with exception message.</p>
    * @param message The exception message.
    */
   public SSOException(String message)
   {
       super(message);
   }

   /**
    * <p>Constructor with nested exception.</p>
    * @param nested Nested exception.
    */
   public SSOException(Throwable nested)
   {
       super(nested);
   }

   /**
    * <p>Constructor with exception message and nested exception.</p>
    * @param msg The exception message.
    * @param nested Nested exception.
    */
   public SSOException(String msg, Throwable nested)
   {
       super(msg, nested);
   }

}

