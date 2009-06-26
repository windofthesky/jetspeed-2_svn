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

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.jetspeed.serializer.JetspeedSerializedData;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

public class JSSnapshot implements JetspeedSerializedData
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
    
    private JSRoles oldRoles;

    private JSGroups oldGroups;

    private JSUsers oldUsers;
    
    private JSPrincipals principals;
    
    private JSPrincipalAssociations principalAssociations;

    private JSPermissions permissions;

    private JSProfilingRules rules;

    private String defaultRule;

    private JSApplications applications;
    
    private JSSecurityDomains securityDomains;

    private JSSSOSites ssoSites;

    
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
        mimeTypes = new JSMimeTypes();
        mediaTypes = new JSMediaTypes();
        clients = new JSClients();
        capabilities = new JSCapabilities();
        oldRoles = new JSRoles();
        oldGroups = new JSGroups();
        oldUsers = new JSUsers();
        principals = new JSPrincipals();
        principalAssociations = new JSPrincipalAssociations();
        permissions = new JSPermissions();
        rules = new JSProfilingRules();
        applications = new JSApplications();
        securityDomains=new JSSecurityDomains();
        ssoSites = new JSSSOSites();
    }

    public JSSnapshot(String name)
    {
        this();
        setName(name);
    }

    /**
     * @return Returns the name.
     */
    public String getName()
    {
        return name;
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
     * @return Returns the groups from old format.
     */
    public JSGroups getOldGroups()
    {
        return oldGroups;
    }

    /**
     * @param groups from old format
     *            The groups to set.
     */
    public void setOldGroups(JSGroups oldGroups)
    {
        this.oldGroups = oldGroups;
    }
    
    /**
     * @return Returns the roles from old format.
     */
    public JSRoles getOldRoles()
    {
        return oldRoles;
    }

    /**
     * @param roles from old format
     *            The roles to set.
     */
    public void setOldRoles(JSRoles oldRoles)
    {
        this.oldRoles = oldRoles;
    }
    
    /**
     * @return Returns the users from old format.
     */
    public JSUsers getOldUsers()
    {
        return oldUsers;
    }
    
    /**
     * @return Returns the jetspeed principals.
     */
    public JSPrincipals getPrincipals()
    {
        return principals;
    }
    
    public void setPrincipalAssociations(JSPrincipalAssociations principalAssociations)
    {
        this.principalAssociations = principalAssociations;
    }
    
    public JSPrincipalAssociations getPrincipalAssociations()
    {
        return this.principalAssociations;
    }
    
    public void addPrincipalAssociation(JSPrincipalAssociation jsPrincipalAssociation)
    {
        this.principalAssociations.add(jsPrincipalAssociation);
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
    

    public JSApplications getApplications()
    {
        return applications;
    }

    /**
     * @param applications
     *            The applications to set.
     */
    public void setApplications(JSApplications applications)
    {
        this.applications = applications;
    }

    public JSSecurityDomains getSecurityDomains()
    {
        return securityDomains;
    }
    
    public void setSecurityDomains(JSSecurityDomains securityDomains)
    {
        this.securityDomains = securityDomains;
    }

    /**
     * @return Returns the SSOSites.
     */
    public JSSSOSites getSSOSites()
    {
        return ssoSites;
    }

    /**
     * @param sites
     *            The SSO sites to set.
     */
    public void setSSOSites(JSSSOSites sites)
    {
        this.ssoSites = sites;
    }


    /***************************************************************************
     * SERIALIZER
     */
    protected static final XMLFormat XML = new XMLFormat(JSSnapshot.class)
    {

        public void write(Object o, OutputElement xml) throws XMLStreamException
        {

            try
            {
                JSSnapshot g = (JSSnapshot) o;

                /** attributes here */

                xml.setAttribute("name", g.getName());

                /** named fields HERE */

                xml.add(String.valueOf(g.getSoftwareVersion()), "softwareVersion");
                xml.add(String.valueOf(g.getSoftwareSubVersion()), "softwareSubVersion");
                if ( g.getDefaultRule() != null )
                {
                    xml.add(g.getDefaultRule(), "default_rule", String.class);
                }

                if ( g.getEncryption() != null )
                {
                    xml.add(g.getEncryption(),"encryption",String.class);
                }
                
                /** implicitly named (through binding) fields here */
                if ( !g.getMimeTypes().isEmpty() )
                {
                    xml.add(g.getMimeTypes());
                }
                if ( !g.getMediaTypes().isEmpty() )
                {
                    xml.add(g.getMediaTypes());
                }
                if ( !g.getCapabilities().isEmpty() )
                {
                    xml.add(g.getCapabilities());
                }
                if ( !g.getClients().isEmpty() )
                {
                    xml.add(g.getClients());
                }
                if ( !g.getOldRoles().isEmpty() )
                {
                    xml.add(g.getOldRoles());
                }
                if ( !g.getOldGroups().isEmpty() )
                {
                    xml.add(g.getOldGroups());
                }
                if ( !g.getOldUsers().isEmpty() )
                {
                    xml.add(g.getOldUsers());
                }
                if ( !g.getPrincipals().isEmpty() )
                {
                    xml.add(g.getPrincipals());
                }
                if ( !g.getPrincipalAssociations().isEmpty() )
                {
                    xml.add(g.getPrincipalAssociations());
                }
                if ( !g.getPermissions().isEmpty() )
                {
                    xml.add(g.getPermissions());
                }
                if ( !g.getRules().isEmpty() )
                {
                    xml.add(g.getRules());
                }
                if ( !g.getApplications().isEmpty() )
                {
                    xml.add(g.getApplications());
                }
                if ( !g.getSecurityDomains().isEmpty() )
                {
                    xml.add(g.getSecurityDomains());
                }
                if ( !g.getSSOSites().isEmpty() )
                {
                    xml.add(g.getSSOSites());
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                if ( e instanceof XMLStreamException )
                {
                    throw (XMLStreamException)e;
                }
                throw new XMLStreamException(e);
            }
        }

        public void read(InputElement xml, Object o) throws XMLStreamException
        {
            try
            {
                JSSnapshot g = (JSSnapshot) o;
                g.name = StringEscapeUtils.unescapeHtml(xml.getAttribute("name", "unknown"));
                Object o1 = xml.get("softwareVersion", String.class);                
                if (o1 != null && o1 instanceof String)
                {
                    g.savedVersion = Integer.parseInt(((String) o1));
                }
                o1 = xml.get("softwareSubVersion", String.class);
                if (o1 != null && o1 instanceof String)
                {
                    g.savedSubversion = Integer.parseInt(((String) o1));
                }
                o1 = xml.get("default_rule",String.class);
                if (o1 != null && o1 instanceof String)
                {
                    g.defaultRule = StringEscapeUtils.unescapeHtml((String) o1);
                }
                o1 = xml.get("encryption",String.class);
                if (o1 != null && o1 instanceof String)
                {
                    g.encryption = StringEscapeUtils.unescapeHtml((String) o1);
                }
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
                        g.oldRoles = (JSRoles) o1;
                    else if (o1 instanceof JSGroups)
                        g.oldGroups = (JSGroups) o1;
                    else if (o1 instanceof JSUsers)
                        g.oldUsers = (JSUsers) o1;
                    else if (o1 instanceof JSPrincipals)
                    {
                        g.principals = (JSPrincipals) o1;
                    }
                    else if (o1 instanceof JSPrincipalAssociations)
                        g.principalAssociations = (JSPrincipalAssociations) o1;
                    else if (o1 instanceof JSPermissions)
                        g.permissions = (JSPermissions) o1;
                    else if (o1 instanceof JSProfilingRules)
                        g.rules = (JSProfilingRules) o1;
                    else if (o1 instanceof JSApplications)
                        g.applications = (JSApplications) o1;
                    else if (o1 instanceof JSSecurityDomains)
                        g.securityDomains = (JSSecurityDomains) o1;
                    else if (o1 instanceof JSSSOSites)
                        g.ssoSites = (JSSSOSites) o1;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                if ( e instanceof XMLStreamException )
                {
                    throw (XMLStreamException)e;
                }
                throw new XMLStreamException(e);
            }
        }
    };
}
