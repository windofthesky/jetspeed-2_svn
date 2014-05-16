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

import org.apache.jetspeed.security.JetspeedPrincipal;

import java.sql.Timestamp;
import java.util.Collection;

/**
 */
public interface SSOPrincipal {

	/**
	 * addRemotePrincipal()
	 * @param principal
	 * Adds remote principal to the main (logged in) principal
	 */
	public void addRemotePrincipal(JetspeedPrincipal principal);
   /**
    * <p>
    * Getter for the principal id.
    * </p>
    * 
    * @return The principal id.
    */
   long getPrincipalId();

   /**
    * <p>
    * Setter for the principal id.
    * </p>
    * 
    * @param principalId The principal id.
    */
   void setPrincipalId(long principalId);

   /**
    * <p>
    * Getter for the principal classname.
    * </p>
    * 
    * @return The principal classname.
    */
   String getClassname();

   /**
    * <p>
    * Setter for the principal classname.
    * </p>
    * 
    * @param classname The principal classname.
    */
   void setClassname(String classname);

   /**
    * <p>
    * Getter for isMappingOnly.
    * </p>
    * 
    * @return The isMappingOnly.
    */
   boolean isMappingOnly();

   /**
    * <p>
    * Setter for isMappingOnly.
    * </p>
    * 
    * @param isMappingOnly The isMappingOnly.
    */
   void setMappingOnly(boolean isMappingOnly);

   /**
    * <p>
    * Getter for the principal full path.
    * </p>
    * <p>
    * The full path allows to retrieve the principal preferences from the
    * preferences services.
    * </p>
    * 
    * @return The principal full path.
    */
   String getFullPath();

   /**
    * <p>
    * Setter for the principal name.
    * </p>
    * <p>
    * The full path allows to retrieve the principal preferences from the
    * preferences services.
    * </p>
    * 
    * @param fullPath The principal full path.
    */
   void setFullPath(String fullPath);

   /**
    * <p>
    * Getter for the principal permissions.
    * </p>
    * 
    * @return The principal permissions.
    */
   Collection getPermissions();

   /**
    * <p>
    * Setter for the principal permissions.
    * </p>
    * 
    * @param permissions The principal permissions.
    */
   void setPermissions(Collection permissions);

   /**
    * <p>
    * Getter for creation date.
    * </p>
    * 
    * @return The creation date.
    */
   Timestamp getCreationDate();

   /**
    * <p>
    * Setter for the creation date.
    * </p>
    * 
    * @param creationDate The creation date.
    */
   void setCreationDate(Timestamp creationDate);

   /**
    * <p>
    * Getter for the modified date.
    * </p>
    * 
    * @return The modified date.
    */
   Timestamp getModifiedDate();

   /**
    * <p>
    * Setter for the modified date.
    * </p>
    * 
    * @param modifiedDate The modified date.
    */
   void setModifiedDate(Timestamp modifiedDate);

   /**
    * <p>Getter for the enabled state</p>
    * @return true if enabled
    */
   boolean isEnabled();
   
   /**
    * Setter for the enabled state</p>
    * @param enabled The enabled state
    */
   void setEnabled(boolean enabled);    
   
   /**
	 * Getter for the remotePrincipals.
	 */
	public Collection getRemotePrincipals();
	
	/**
	 *  Setter for the  remotePrincipals 
	 */
	public void setRemotePrincipals(Collection remotePrincipals) ;

	/**
	* Getter for the siteID.
	*/
	public int getSiteID();
	/**
	* Setter for thesiteID
	*/
	public void setSiteID(int siteID);
}
