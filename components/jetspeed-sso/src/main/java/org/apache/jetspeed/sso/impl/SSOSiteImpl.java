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

package org.apache.jetspeed.sso.impl;

import org.apache.jetspeed.sso.SSOSite;

/**
 * SSOSiteImpl Class holding information about the Site and credentials for
 * Single Sign on SSO. OJB will map the database entries into this class
 * 
 * @author <a href="mailto:rogerrut@apache.org">Roger Ruttimann</a>
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */

public class SSOSiteImpl implements SSOSite
{

    // Private member for OJB mapping
    private int id;

    private String name;

    private String url;

    private boolean isAllowUserSet;

    private boolean isCertificateRequired;

    private boolean challengeResponseAuthentication;

    /* Realm used to do ChallengeResponse Authentication */
    private String realm;

    private boolean isFormAuthentication;

    /*
     * Names of fields for User and Password values. The names are up to the
     * application developer and therefore it must be configurable for SSO
     */
    private String formUserField;

    private String formPwdField;

    private Long securityDomainId;

    /**
	 * 
	 */
    public SSOSiteImpl()
    {
        super();

    }
    
    public SSOSiteImpl(String name, String url)
    {
        this();
        this.name=name;
        this.url=url;
    }
    
    /**
     * @return Returns the isAllowUserSet.
     */
    public boolean isAllowUserSet()
    {
        return isAllowUserSet;
    }

    /**
     * @param isAllowUserSet
     *            The isAllowUserSet to set.
     */
    public void setAllowUserSet(boolean isAllowUserSet)
    {
        this.isAllowUserSet = isAllowUserSet;
    }

    /**
     * @return Returns the isCertificateRequired.
     */
    public boolean isCertificateRequired()
    {
        return isCertificateRequired;
    }

    /**
     * @param isCertificateRequired
     *            The isCertificateRequired to set.
     */
    public void setCertificateRequired(boolean isCertificateRequired)
    {
        this.isCertificateRequired = isCertificateRequired;
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
     * @return Returns the siteId.
     */
    public int getId()
    {
        return id;
    }

    /**
     * @return sets the id of the site
     */
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * @return Returns the siteURL.
     */
    public String getURL()
    {
        return url;
    }

    /**
     * @param siteURL
     *            The siteURL to set.
     */
    public void setURL(String url)
    {
        this.url = url;
    }

    /**
     * Define the Authentication methods. Supported are: Challenge Response and
     * From based
     */
    /**
     * Form authentication requires two fields that hold the credential
     * information for the request.
     */
    public void setFormAuthentication(String formUserField, String formPwdField)
    {
        // Set the fields for Form Authentication and clear other authentication
        // methods

    }
   
    public String getFormPwdField()
    {
        return formPwdField;
    }

    public void setFormPwdField(String formPwdField)
    {
        this.formPwdField = formPwdField;
    }

    public String getFormUserField()
    {
        return formUserField;
    }

    public void setFormUserField(String formUserField)
    {
        this.formUserField = formUserField;
    }

    public boolean isFormAuthentication()
    {
        return isFormAuthentication;
    }

    public void setFormAuthentication(boolean isFormAuthentication)
    {
        this.isFormAuthentication = isFormAuthentication;
    }

    public void configFormAuthentication(String formUserField,
            String formPwdField)
    {
        this.isFormAuthentication = true;
        this.setChallengeResponseAuthentication(false);

        this.formPwdField = formPwdField;
        this.formUserField = formUserField;
    }

    public void setRealm(String realm)
    {
        this.realm = realm;
    }

    public String getRealm()
    {
        return this.realm;
    }

    public Long getSecurityDomainId()
    {
        return securityDomainId;
    }

    public void setSecurityDomainId(Long securityDomainId)
    {
        this.securityDomainId = securityDomainId;
    }

    
    public boolean isChallengeResponseAuthentication()
    {
        return challengeResponseAuthentication;
    }

    
    public void setChallengeResponseAuthentication(
            boolean challengeResponseAuthentication)
    {
        this.challengeResponseAuthentication = challengeResponseAuthentication;
    }

}
