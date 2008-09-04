/*	
 * Licensed to the Apache Software Foundation (ASF) under one or more&#13;
 * contributor license agreements.  See the NOTICE file distributed with&#13;
 * this work for additional information regarding copyright ownership.&#13;
 * The ASF licenses this file to You under the Apache License, Version 2.0&#13;
 * (the "License"); you may not use this file except in compliance with&#13;
 * the License.  You may obtain a copy of the License at&#13;
 * &#13;
 *      http://www.apache.org/licenses/LICENSE-2.0&#13;
 * &#13;
 * Unless required by applicable law or agreed to in writing, software&#13;
 * distributed under the License is distributed on an "AS IS" BASIS,&#13;
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.&#13;
 * See the License for the specific language governing permissions and&#13;
 * limitations under the License.&#13;
 */
package org.apache.jetspeed.security.impl;

import java.util.List;

import org.apache.jetspeed.security.DependentPrincipalException;
import org.apache.jetspeed.security.JetspeedPermission;
import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalAssociationHandler;
import org.apache.jetspeed.security.JetspeedPrincipalManager;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.PrincipalNotFoundException;
import org.apache.jetspeed.security.PrincipalNotRemovableException;
import org.apache.jetspeed.security.spi.JetspeedPrincipalAccessManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalPermissionStorageManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalStorageManager;

/**
 * @version $Id$
 *
 */
public abstract class BaseJetspeedPrincipalManager implements JetspeedPrincipalManager {

	private JetspeedPrincipalAccessManager jetspeedPrincipalAccessManager;
	private JetspeedPrincipalAssociationHandler jetspeedPrincipalAssociationHandler;
	private JetspeedPrincipalStorageManager jetspeedPrincipalStorageManager;
	private JetspeedPrincipalPermissionStorageManager jetspeedPrincipalPermissionStorageManager;	
	
	public BaseJetspeedPrincipalManager() {
		super();		
	}

	public BaseJetspeedPrincipalManager(JetspeedPrincipalStorageManager jetspeedPrincipalStorageManager,JetspeedPrincipalPermissionStorageManager jetspeedPrincipalPermissionStorageManager)
	{
		this.jetspeedPrincipalStorageManager = jetspeedPrincipalStorageManager;
		this.jetspeedPrincipalPermissionStorageManager = jetspeedPrincipalPermissionStorageManager;
	}
	
	public void addAssociationHandler(JetspeedPrincipalAssociationHandler jpah) {
		this.jetspeedPrincipalAssociationHandler = jpah;
	}
	
	public void setAccessManager(JetspeedPrincipalAccessManager pam) {

		this.jetspeedPrincipalAccessManager = pam;
	}

	public List<JetspeedPrincipal> getAssociatedFrom(String principalName, String associationName) {
		return jetspeedPrincipalAccessManager.getAssociatedFrom(principalName, getPrincipalType(), associationName);
	}
	public List<String> getAssociatedNamesFrom(String principalName, String associationName) {
		return jetspeedPrincipalAccessManager.getAssociatedNamesFrom(principalName, getPrincipalType(), associationName);
	}

	public List<String> getAssociatedNamesTo(String principalName, String associationName) {
		return jetspeedPrincipalAccessManager.getAssociatedNamesTo(principalName, getPrincipalType(), associationName);
	}

	public List<JetspeedPrincipal> getAssociatedTo(String principalName, String associationName) {
		return jetspeedPrincipalAccessManager.getAssociatedTo(principalName, getPrincipalType(), associationName);
	}

	public JetspeedPrincipal getPrincipal(String name) {
		return jetspeedPrincipalAccessManager.getPrincipal(name, getPrincipalType());
	}

	public List<String> getPrincipalNames(String nameFilter) {
		return jetspeedPrincipalAccessManager.getPrincipalNames(nameFilter, getPrincipalType());
	}

	public JetspeedPrincipalType getPrincipalType() {
		return null;
	}

	public List<JetspeedPrincipal> getPrincipals(String nameFilter) {
		return jetspeedPrincipalAccessManager.getPrincipals(nameFilter, getPrincipalType());
	}

	public boolean principalExists(String name) {
		return false;
	}

	public void grantPermission(JetspeedPrincipal principal, JetspeedPermission permission) {
		jetspeedPrincipalPermissionStorageManager.grantPermission(principal, permission);		
	}

	public void revokeAll(JetspeedPrincipal principal) {
		jetspeedPrincipalPermissionStorageManager.revokeAll(principal);
	}

	public void revokePermission(JetspeedPrincipal principal, JetspeedPermission permission) {
		jetspeedPrincipalPermissionStorageManager.revokePermission(principal, permission);
	}

	public void removePrincipal(String name) throws PrincipalNotFoundException, PrincipalNotRemovableException, DependentPrincipalException {
		JetspeedPrincipal principal = jetspeedPrincipalAccessManager.getPrincipal(name, getPrincipalType());		
		if (principal == null) throw new PrincipalNotFoundException();		
		jetspeedPrincipalStorageManager.removePrincipal (principal);	
	}
}
