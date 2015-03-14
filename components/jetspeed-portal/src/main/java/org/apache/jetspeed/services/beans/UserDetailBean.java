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
package org.apache.jetspeed.services.beans;

import org.apache.jetspeed.security.Group;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.User;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DTO for user details data. The object will be transformed to JSON to be
 * transfered to the JS client.
 * 
 * @author <a href="mailto:joachim@wemove.com">Joachim Mueller</a>
 * 
 */
@XmlRootElement(name = "data")
public class UserDetailBean implements Serializable {

	private Map<String, String> infoMap;
	private Timestamp creationDate;
	private Timestamp modifiedDate;
	private boolean enabled = true;
	private boolean credentialUpdateRequired = false;
	private List<String> roles = null;
	private List<String> groups = null;
	private List<String> availableRoles = null;
	private List<String> availableGroups = null;
	private List<String> availableRules = null;
	private String rule = null;
	private static final long serialVersionUID = 1L;

	public UserDetailBean() {
	}

	public UserDetailBean(User user, PasswordCredential credential, List<Role> roles, List<Group> groups,
						  List<String> allRoles, List<String> allGroups, String rule, List<String> profilingRules) {
		this.name = user.getName();
		this.infoMap = user.getInfoMap();
		this.creationDate = user.getCreationDate();
		this.modifiedDate = user.getModifiedDate();
		this.enabled = user.isEnabled();
		this.availableRoles = allRoles;
		this.availableGroups = allGroups;
		this.availableRules = profilingRules;
		this.rule = rule;
		this.credentialUpdateRequired = credential.isUpdateRequired();
		for (Role role : roles) {
			this.roles = (this.roles == null ? new ArrayList<String>() : this.roles);
			this.roles.add(role.getName());
			if (availableRoles.contains(role.getName())) {
				availableRoles.remove(role.getName());
			}
		}
		for (Group group : groups) {
			this.groups = (this.groups == null ? new ArrayList<String>() : this.groups);
			this.groups.add(group.getName());
			if (availableGroups.contains(group.getName())) {
				availableGroups.remove(group.getName());
			}
		}
	}

	private String name;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the infoMap
	 */
	public Map<String, String> getInfoMap() {
		return infoMap;
	}

	/**
	 * @param infoMap
	 *            the infoMap to set
	 */
	public void setInfoMap(Map<String, String> infoMap) {
		this.infoMap = infoMap;
	}

	/**
	 * @return the creationDate
	 */
	public Timestamp getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate
	 *            the creationDate to set
	 */
	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the modifiedDate
	 */
	public Timestamp getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * @param modifiedDate
	 *            the modifiedDate to set
	 */
	public void setModifiedDate(Timestamp modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled
	 *            the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the groups
	 */
	public List<String> getGroups() {
		return groups;
	}

	/**
	 * @param groups
	 *            the groups to set
	 */
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	/**
	 * @return the roles
	 */
	public List<String> getRoles() {
		return roles;
	}

	/**
	 * @param roles
	 *            the roles to set
	 */
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	/**
	 * @return the availableRoles
	 */
	public List<String> getAvailableRoles() {
		return availableRoles;
	}

	/**
	 * @param availableRoles
	 *            the availableRoles to set
	 */
	public void setAvailableRoles(List<String> availableRoles) {
		this.availableRoles = availableRoles;
	}

	/**
	 * @return the availableGroups
	 */
	public List<String> getAvailableGroups() {
		return availableGroups;
	}

	/**
	 * @param availableGroups
	 *            the availableGroups to set
	 */
	public void setAvailableGroups(List<String> availableGroups) {
		this.availableGroups = availableGroups;
	}
	
	/**
	 * @return the credentialUpdateRequired
	 */
	public boolean isCredentialUpdateRequired() {
		return credentialUpdateRequired;
	}

	public List<String> getAvailableRules() {
		return availableRules;
	}

	public void setAvailableRules(List<String> availableRules) {
		this.availableRules = availableRules;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}
}
