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

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Jetspeed Serialized (JS) SSOSite
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id: $
 */
public class JSSSOSite
{
    private String name;

    private String siteURL;

    private boolean allowUserSet;

    private boolean certificateRequired;
    
    private boolean challengeResponseAuthentication;
    
    private String realm;
    
    private boolean formAuthentication;
    
    private String formUserField;
    
    private String formPwdField;
    
    private JSSSOSiteRemoteUsers remoteUsers;

    public JSSSOSite()
    {
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getSiteURL()
    {
        return siteURL;
    }

    public void setSiteURL(String siteURL)
    {
        this.siteURL = siteURL;
    }

    public boolean isAllowUserSet()
    {
        return allowUserSet;
    }

    public void setAllowUserSet(boolean allowUserSet)
    {
        this.allowUserSet = allowUserSet;
    }

    public boolean isCertificateRequired()
    {
        return certificateRequired;
    }

    public void setCertificateRequired(boolean certificateRequired)
    {
        this.certificateRequired = certificateRequired;
    }

    public boolean isChallengeResponseAuthentication()
    {
        return challengeResponseAuthentication;
    }

    public void setChallengeResponseAuthentication(boolean challengeResponseAuthentication)
    {
        this.challengeResponseAuthentication = challengeResponseAuthentication;
    }

    public String getRealm()
    {
        return realm;
    }

    public void setRealm(String realm)
    {
        this.realm = realm;
    }

    public boolean isFormAuthentication()
    {
        return formAuthentication;
    }

    public void setFormAuthentication(boolean formAuthentication)
    {
        this.formAuthentication = formAuthentication;
    }

    public String getFormUserField()
    {
        return formUserField;
    }

    public void setFormUserField(String formUserField)
    {
        this.formUserField = formUserField;
    }

    public String getFormPwdField()
    {
        return formPwdField;
    }

    public void setFormPwdField(String formPwdField)
    {
        this.formPwdField = formPwdField;
    }

    public JSSSOSiteRemoteUsers getRemoteUsers()
    {
        return remoteUsers;
    }

    public void setRemoteUsers(JSSSOSiteRemoteUsers remoteUsers)
    {
        this.remoteUsers = remoteUsers;
    }

    public void addRemoteUser(JSSSOSiteRemoteUser remoteUser)
    {
        if (remoteUsers == null)
        {
            remoteUsers = new JSSSOSiteRemoteUsers();
        }
        remoteUsers.add(remoteUser);
    }

    /***************************************************************************
     * SERIALIZER
     */
    @SuppressWarnings("unused")
    private static final XMLFormat XML = new XMLFormat(JSSSOSite.class)
    {
        public void write(Object o, OutputElement xml) throws XMLStreamException
        {
            try
            {
                JSSSOSite g = (JSSSOSite) o;

                xml.setAttribute("name", g.getName());
                xml.setAttribute("siteURL", g.getSiteURL());
                xml.setAttribute("allowUserSet", g.isAllowUserSet());
                xml.setAttribute("certificateRequired", g.isCertificateRequired());
                xml.setAttribute("challengeResponseAuthentication", g.isChallengeResponseAuthentication());
                xml.setAttribute("realm", g.getRealm());
                xml.setAttribute("formAuthentication", g.isFormAuthentication());
                xml.setAttribute("formUserField", g.getFormUserField());
                xml.setAttribute("formPwdField", g.getFormPwdField());

                xml.add(g.getRemoteUsers());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        public void read(InputElement xml, Object o)
        {
            try
            {
                JSSSOSite g = (JSSSOSite) o;

                g.setName(StringEscapeUtils.unescapeHtml(xml.getAttribute("name", (String)null)));
                g.setSiteURL(StringEscapeUtils.unescapeHtml(xml.getAttribute("siteURL", (String)null)));
                g.setAllowUserSet(Boolean.parseBoolean(StringEscapeUtils.unescapeHtml(xml.getAttribute("allowUserSet", "false"))));
                g.setCertificateRequired(Boolean.parseBoolean(StringEscapeUtils.unescapeHtml(xml.getAttribute("certificateRequired", "false"))));
                g.setChallengeResponseAuthentication(Boolean.parseBoolean(StringEscapeUtils.unescapeHtml(xml.getAttribute("challengeResponseAuthentication", "false"))));
                g.setRealm(StringEscapeUtils.unescapeHtml(xml.getAttribute("realm", (String)null)));
                g.setFormAuthentication(Boolean.parseBoolean(StringEscapeUtils.unescapeHtml(xml.getAttribute("formAuthentication", "false"))));
                g.setFormUserField(StringEscapeUtils.unescapeHtml(xml.getAttribute("formUserField", (String)null)));
                g.setFormPwdField(StringEscapeUtils.unescapeHtml(xml.getAttribute("formPwdField", (String)null)));
                
                Object o1 = null;
                while (xml.hasNext())
                {
                    o1 = xml.getNext();
                    if (o1 instanceof JSSSOSiteRemoteUsers)
                    {
                        g.setRemoteUsers((JSSSOSiteRemoteUsers)o1);
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };
}
