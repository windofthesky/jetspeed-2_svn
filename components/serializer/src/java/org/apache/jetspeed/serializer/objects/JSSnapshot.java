/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.apache.jetspeed.serializer.objects;

import java.util.ArrayList;

import org.apache.commons.lang.StringEscapeUtils;

import javolution.xml.XMLBinding;
import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

public class JSSnapshot
{

    public static final int softwareVersion = 1;

    public static final int softwareSubVersion = 0;

    private String name;

    private int savedVersion;

    private int savedSubversion;

    private String dateCreated;

    private String dataSource;

    private String encryption;

    private JSMimeTypes mimeTypes;

    private JSMediaTypes mediaTypes;

    private JSClients clients;

    private JSCapabilities capabilities;

    private JSRoles roles;

    private JSGroups groups;

    private JSUsers users;

    private JSPermissions permissions;

    private JSProfilingRules rules;

    private String defaultRule;

    /**
     * check the software version and subvversion against the saved
     * version...and verify whether it is compatible...
     * 
     * @return the current software can process this file
     */
    public boolean checkVersion()
    {
        return true;
    }

    public JSSnapshot()
    {
        System.out.println("JSSnapshot Class created");
    }

    public JSSnapshot(String name)
    {
        this.name = name;
        mimeTypes = new JSMimeTypes();
        mediaTypes = new JSMediaTypes();
        clients = new JSClients();
        capabilities = new JSCapabilities();
        roles = new JSRoles();
        groups = new JSGroups();
        users = new JSUsers();
        permissions = new JSPermissions();
        rules = new JSProfilingRules();
    }

 
    /***************************************************************************
     * SERIALIZER
     */
    private static final XMLFormat XML = new XMLFormat(JSSnapshot.class)
    {

        public void write(Object o, OutputElement xml)
                throws XMLStreamException
        {
        	
            try
            {
                JSSnapshot g = (JSSnapshot) o;

                /** attributes here */

                xml.setAttribute("name", g.getName());

                /** named fields HERE */

                xml.add(String.valueOf(g.getSoftwareVersion()),
                        "softwareVersion");
                xml.add(String.valueOf(g.getSoftwareSubVersion()),
                        "softwareSubVersion");
                xml.add(g.getDefaultRule(), "default_rule", String.class);

                xml.add(g.encryption,"encryption",String.class);
                
                /** implicitly named (through binding) fields here */

                xml.add(g.getMimeTypes());
                xml.add(g.getMediaTypes());
                xml.add(g.getCapabilities());
                xml.add(g.getClients());
                
                xml.add(g.getRoles()); 
                xml.add(g.getGroups());
                xml.add(g.getUsers()); 
                
                xml.add(g.getPermissions());
                xml.add(g.getRules());

            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        public void read(InputElement xml, Object o)
        {
            try
            {
                JSSnapshot g = (JSSnapshot) o;
                g.name = StringEscapeUtils.unescapeHtml(xml.getAttribute("name", "unknown"));
                Object o1 = xml.get("softwareVersion",String.class);
                if (o1 instanceof String)
                    g.savedVersion = Integer.parseInt(((String) o1));
                o1 = xml.get("softwareSubVersion",String.class);
                if (o1 instanceof String)
                    g.savedSubversion = Integer.parseInt(((String) o1));
                o1 = xml.get("default_rule",String.class);
                if (o1 instanceof String) g.defaultRule = StringEscapeUtils.unescapeHtml((String) o1);
                o1 = xml.get("encryption",String.class);
                if (o1 instanceof String) g.encryption = StringEscapeUtils.unescapeHtml((String) o1);

                while (xml.hasNext())
                {
                    o1 = xml.getNext(); // mime

                    if (o1 instanceof JSMimeTypes)
                        g.mimeTypes = (JSMimeTypes) o1;
                    else if (o1 instanceof JSMediaTypes)
                        g.mediaTypes = (JSMediaTypes) o1;
                    else if (o1 instanceof JSClients)
                        g.clients = (JSClients) o1;
                    else if (o1 instanceof JSCapabilities)
                        g.capabilities = (JSCapabilities) o1;
                    else if (o1 instanceof JSRoles)
                        g.roles = (JSRoles) o1;
                    else if (o1 instanceof JSGroups)
                        g.groups = (JSGroups) o1;
                    else if (o1 instanceof JSUsers)
                        g.users = (JSUsers) o1;
                    else if (o1 instanceof JSPermissions)
                        g.permissions = (JSPermissions) o1;
                    else if (o1 instanceof JSProfilingRules)
                        g.rules = (JSProfilingRules) o1;
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };

    /**
     * @return Returns the name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return Returns the groups.
     */
    public JSGroups getGroups()
    {
        return groups;
    }

    /**
     * @param groups
     *            The groups to set.
     */
    public void setGroups(JSGroups groups)
    {
        this.groups = groups;
    }

    /**
     * @return Returns the roles.
     */
    public JSRoles getRoles()
    {
        return roles;
    }

    /**
     * @param roles
     *            The roles to set.
     */
    public void setRoles(JSRoles roles)
    {
        this.roles = roles;
    }

    /**
     * @return Returns the roles.
     */
    public JSUsers getUsers()
    {
        return users;
    }

    /**
     * @return Returns the encryption.
     */
    public String getEncryption()
    {
        return encryption;
    }

    /**
     * @param encryption
     *            The encryption to set.
     */
    public void setEncryption(String encryption)
    {
        this.encryption = encryption;
    }

    /**
     * @return Returns the softwareSubVersion.
     */
    public static int getSoftwareSubVersion()
    {
        return softwareSubVersion;
    }

    /**
     * @return Returns the softwareVersion.
     */
    public static int getSoftwareVersion()
    {
        return softwareVersion;
    }

    /**
     * @return Returns the capabilities.
     */
    public JSCapabilities getCapabilities()
    {
        return capabilities;
    }

    /**
     * @param capabilities
     *            The capabilities to set.
     */
    public void setCapabilities(JSCapabilities capabilities)
    {
        this.capabilities = capabilities;
    }

    /**
     * @return Returns the clients.
     */
    public JSClients getClients()
    {
        return clients;
    }

    /**
     * @param clients
     *            The clients to set.
     */
    public void setClients(JSClients clients)
    {
        this.clients = clients;
    }

    /**
     * @return Returns the dataSource.
     */
    public String getDataSource()
    {
        return dataSource;
    }

    /**
     * @param dataSource
     *            The dataSource to set.
     */
    public void setDataSource(String dataSource)
    {
        this.dataSource = dataSource;
    }

    /**
     * @return Returns the dateCreated.
     */
    public String getDateCreated()
    {
        return dateCreated;
    }

    /**
     * @param dateCreated
     *            The dateCreated to set.
     */
    public void setDateCreated(String dateCreated)
    {
        this.dateCreated = dateCreated;
    }

    /**
     * @return Returns the mediaTypes.
     */
    public JSMediaTypes getMediaTypes()
    {
        return mediaTypes;
    }

    /**
     * @param mediaTypes
     *            The mediaTypes to set.
     */
    public void setMediaTypes(JSMediaTypes mediaTypes)
    {
        this.mediaTypes = mediaTypes;
    }

    /**
     * @return Returns the mimeTypes.
     */
    public JSMimeTypes getMimeTypes()
    {
        return mimeTypes;
    }

    /**
     * @param mimeTypes
     *            The mimeTypes to set.
     */
    public void setMimeTypes(JSMimeTypes mimeTypes)
    {
        this.mimeTypes = mimeTypes;
    }

    /**
     * @return Returns the savedSubversion.
     */
    public int getSavedSubversion()
    {
        return savedSubversion;
    }

    /**
     * @param savedSubversion
     *            The savedSubversion to set.
     */
    public void setSavedSubversion(int savedSubversion)
    {
        this.savedSubversion = savedSubversion;
    }

    /**
     * @return Returns the savedVersion.
     */
    public int getSavedVersion()
    {
        return savedVersion;
    }

    /**
     * @param savedVersion
     *            The savedVersion to set.
     */
    public void setSavedVersion(int savedVersion)
    {
        this.savedVersion = savedVersion;
    }

    /**
     * @param name
     *            The name to set.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @param users
     *            The users to set.
     */
    public void setUsers(JSUsers users)
    {
        this.users = users;
    }

    /**
     * @return Returns the permissions.
     */
    public JSPermissions getPermissions()
    {
        return permissions;
    }

    /**
     * @param permissions
     *            The permissions to set.
     */
    public void setPermissions(JSPermissions permissions)
    {
        this.permissions = permissions;
    }

    /**
     * @return Returns the rules.
     */
    public JSProfilingRules getRules()
    {
        return rules;
    }

    /**
     * @param rules
     *            The rules to set.
     */
    public void setRules(JSProfilingRules rules)
    {
        this.rules = rules;
    }

    /**
     * @return Returns the defaultRule.
     */
    public String getDefaultRule()
    {
        return defaultRule;
    }

    /**
     * @param defaultRule
     *            The defaultRule to set.
     */
    public void setDefaultRule(String defaultRule)
    {
        this.defaultRule = defaultRule;
    }

}
