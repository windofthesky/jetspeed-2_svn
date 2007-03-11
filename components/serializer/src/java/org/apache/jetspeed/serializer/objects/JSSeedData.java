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

import java.util.ArrayList;

import org.apache.commons.lang.StringEscapeUtils;

import javolution.xml.XMLBinding;
import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

public class JSSeedData extends JSSnapshot
{

    public static final int softwareVersion = 1;

    public static final int softwareSubVersion = 0;

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
    
    /**
     * @return Returns the softwareSubVersion.
     */
    public int getSoftwareSubVersion()
    {
        return softwareSubVersion;
    }

    /**
     * @return Returns the softwareVersion.
     */
    public int getSoftwareVersion()
    {
        return softwareVersion;
    }


    public JSSeedData()
    {
    	super();
        System.out.println("JSSeedData Class created");
    }

    public JSSeedData(String name)
    {
        super();
        
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
    protected static final XMLFormat XML = new XMLFormat(JSSeedData.class)
    {

        public void write(Object o, OutputElement xml)
                throws XMLStreamException
        {
        	
            try
            {

                JSSnapshot.XML.write(o,xml);

                JSSeedData g = (JSSeedData) o;
                
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
            	JSSnapshot.XML.read(xml, o); // Calls parent read.
                JSSeedData g = (JSSeedData) o;
                Object o1 = xml.get("default_rule",String.class);
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
