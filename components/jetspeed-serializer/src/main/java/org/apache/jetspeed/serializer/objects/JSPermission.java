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
package org.apache.jetspeed.serializer.objects;

import java.util.List;
import java.util.ArrayList;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Serialized Permission <permission type='folder' resource='/' actions='view,
 * edit'> <roles>admin, user</roles> <groups>dev</groups> <users>joe</users>
 * </permission>
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class JSPermission
{

	private String type;

	private String resource;

	private String actions;

	private long id;

	private List<JSPrincipal> roles = null;

	private List<JSPrincipal> groups = null;

	private List<JSPrincipal> users = null;

	private JSUserRoles roleString;

	private JSUserGroups groupString;

	private JSUserUsers userString;

	public static final String TYPE_FOLDER = "folder".intern();

	public static final String TYPE_FRAGMENT = "fragment".intern();

	public static final String TYPE_PAGE = "page".intern();

	public static final String TYPE_PORTALRESOURCE = "portalResource".intern();

	public static final String TYPE_PORTALRESOURCECOLLECTION = "portalResource"
			.intern();

	public static final String TYPE_PORTAL = "portal".intern();

	public static final String TYPE_UNKNOWN = "unknown".intern();

	public JSPermission()
	{
	}

	private String putTokens(List<JSPrincipal> _list)
	{
		if ((_list == null) || (_list.size() == 0))
			return "";
		
		boolean _start = true;
		StringBuffer _sb = new StringBuffer();
		
		for (JSPrincipal jsPrincipal : _list)
		{
			if (!_start)
				_sb.append(',');
			else
				_start = false;

			_sb.append(jsPrincipal.getName());
		}
		
		return _sb.toString();
	}

	/**
	 * @return Returns the actions.
	 */
	public String getActions()
	{
		return actions;
	}

	/**
	 * @param actions
	 *            The actions to set.
	 */
	public void setActions(String actions)
	{
		this.actions = actions;
	}

	/**
	 * @return Returns the groups.
	 */
	public List<JSPrincipal> getGroups()
	{
		return groups;
	}

	/**
	 * @param groups
	 *            The groups to set.
	 */
	public void setGroups(List<JSPrincipal> groups)
	{
		this.groups = groups;
	}

	/**
	 * @return Returns the resource.
	 */
	public String getResource()
	{
		return resource;
	}

	/**
	 * @param resource
	 *            The resource to set.
	 */
	public void setResource(String resource)
	{
		this.resource = resource;
	}

	/**
	 * @return Returns the roles.
	 */
	public List<JSPrincipal> getRoles()
	{
		return roles;
	}

	/**
	 * @param roles
	 *            The roles to set.
	 */
	public void setRoles(List<JSPrincipal> roles)
	{
		this.roles = roles;
	}

	/**
	 * @return Returns the type.
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * @param type
	 *            The type to set.
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 * @return Returns the users.
	 */
	public List<JSPrincipal> getUsers()
	{
		return users;
	}

	/**
	 * @param users
	 *            The users to set.
	 */
	public void setUsers(List<JSPrincipal> users)
	{
		this.users = users;
	}


	/**
	 * @return Returns the id.
	 */
	public long getId()
	{
		return id;
	}

	/**
	 * @param id
	 *            The id to set.
	 */
	public void setId(long id)
	{
		this.id = id;
	}

	public void addGroup(JSPrincipal group)
	{
		if (groups == null)
			groups = new ArrayList<JSPrincipal>();
		
		groups.add(group);
	}

	public void addRole(JSPrincipal role)
	{
		if (roles == null)
			roles = new ArrayList<JSPrincipal>();
		
		roles.add(role);
	}


	public void addUser(JSPrincipal user)
	{
		if (users == null)
			users = new ArrayList<JSPrincipal>();
		
		users.add(user);
	}


	/***************************************************************************
	 * SERIALIZER
	 */
	private static final XMLFormat XML = new XMLFormat(JSPermission.class)
	{
		public void write(Object o, OutputElement xml)
				throws XMLStreamException
		{
			try
			{
				JSPermission g = (JSPermission) o;
				xml.setAttribute("type", g.getType());
				xml.setAttribute("resource",g.getResource());
				xml.setAttribute("actions",g.getActions());
				g.groupString = new JSUserGroups(g.putTokens(g.getGroups()));
				g.roleString = new JSUserRoles(g.putTokens(g.getRoles())); 
				g.userString = new JSUserUsers(g.putTokens(g.getUsers())); 
				xml.add(g.roleString);
				xml.add(g.groupString);
				xml.add(g.userString);

			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		public void read(InputElement xml, Object o)
		{
			try
			{
				JSPermission g = (JSPermission) o;
				g.type = StringEscapeUtils.unescapeHtml(xml.getAttribute("type", "type_unknown"));
				g.resource = StringEscapeUtils.unescapeHtml(xml.getAttribute("resource", "resource_unknown"));
				g.actions = StringEscapeUtils.unescapeHtml(xml.getAttribute("actions", "unknown_actions"));
				
	               while (xml.hasNext())
	                {
	                    Object o1 = xml.getNext(); // mime

	                    if (o1 instanceof JSUserGroups)
	                        g.groupString = (JSUserGroups) o1;
	                    else if (o1 instanceof JSUserUsers)
	                        g.userString = (JSUserUsers) o1;
	                    else if (o1 instanceof JSUserRoles)
	                        g.roleString = (JSUserRoles) o1;
	                }
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}

	};

	public JSUserGroups getGroupString()
	{
		return groupString;
	}

	public JSUserRoles getRoleString()
	{
		return roleString;
	}

	public JSUserUsers getUserString()
	{
		return userString;
	}


	
}
