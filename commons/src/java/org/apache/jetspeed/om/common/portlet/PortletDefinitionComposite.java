/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.om.common.portlet;

import java.io.Serializable;
import java.util.Collection;
import java.util.Locale;

import org.apache.jetspeed.om.common.ParameterComposite;
import org.apache.jetspeed.om.common.PreferenceComposite;
import org.apache.pluto.om.common.DescriptionSet;
import org.apache.pluto.om.common.Language;
import org.apache.pluto.om.common.LanguageSet;
import org.apache.pluto.om.common.ParameterSet;
import org.apache.pluto.om.common.SecurityRoleRefSet;
import org.apache.pluto.om.portlet.ContentType;
import org.apache.pluto.om.portlet.ContentTypeSet;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.portlet.PortletDefinitionCtrl;
import org.apache.pluto.om.common.PreferenceSet;

/**
 * 
 * PortletDefinitionComposite
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface PortletDefinitionComposite extends PortletDefinition, PortletDefinitionCtrl, Serializable
{
    void addLanguage(Language lang);

    void addContentType(ContentType cType);

    void setLanguageSet(LanguageSet languages);

    /**
     * The PreferenceSet is a collection user-defineable preferences
     * that this portlet can use to process its logic.
     * 
     * @param preferences
     */
    void setPreferenceSet(PreferenceSet preferences);

    void setInitParameterSet(ParameterSet parameters);

    void setContentTypeSet(ContentTypeSet contentTypes);

    void setInitSecurityRoleRefSet(SecurityRoleRefSet securityRefs);

    /**
     * Convenience method for directly adding init parameters
     * to this <code>PortletDefinition.</code>.  This has the
     * same affect as 
     * <code>((ParameterSetCtrl)PortletDefinition.getInitParamaterSet()).add()</code>
     * @param name Name of parameter to set
     * @param value new value of said parameter
     * @return ParameterComposite newly created parameter
     */
    ParameterComposite addInitParameter(String name, String value);

    /**
     * Same as <code>setInitParameter(name, title) plus allows a
     * description to inlcuded.
     * @see org.apache.jetspeed.om.common.portlet.PortletApplicationComposite#addInitParameter(java.lang.String, java.lang.String)
     * @param name Name of parameter to set
     * @param value new value of the parameter
     * @param DescriptionSet containing locale-specific descriptions of the parameter
     * @return ParameterComposite newly created parameter
     */
    ParameterComposite addInitParameter(String name, String value, DescriptionSet description);

    /**
     * Same as <code>setInitParameter(name, title) plus allows you 
     * to define one initial localized desription.
     * 
     * @see org.apache.jetspeed.om.common.portlet.PortletApplicationComposite#addInitParameter(java.lang.String, java.lang.String)
     * @param name Name of parameter to set
     * @param value new value of the parameter
     * @param description A description for this parameter
     * @param locale The locale the description
     * @return ParameterComposite newly created parameter
     */
    ParameterComposite addInitParameter(String name, String value, String description, Locale locale);

    /**
     * Setter for setting expiration cache time for this portlet     
     */
    void setExpirationCache(String cache);

    void setPortletApplicationDefinition(PortletApplicationDefinition pad);

    PreferenceComposite addPreference(String name, Collection values);

    void setPortletIdentifier(String portletIndentifier);

    String getPortletIdentifier();

    /**
     * A portlet's unique name is a string formed by the combination of a portlet's
     * unique within it's parent application plus the parent application's
     * unique name within the portlet container using ":" as a delimiter. 
     * <br/>
     * <strong>FORMAT: </strong> <i>application name</i>:<i>portlet name</i>
     * <br/>
     * <strong>EXAMPLE: </strong> com.myapp.portletApp1:weather-portlet
     * 
     
     * @return Name that uniquely indetifies this portlet within the container.  If
     * either the name of the portlet is <code>null</code> or this portlet has not
     * yet been assigned to an portlet application, <code>null</code> is returned.
     */
    String getUniqueName();

    /**
     * Returns localized text of this PortletDefinitions display name.
     * 
     * @param locale Locale to get the display name for
     * @return Localized text string of the display name or <code>null</code>
     * if no DisplayName exists for this locale
     */
    String getDisplayNameText(Locale locale);

    /**
     * Returns localized text of this PortletDefinitions description.
     * 
     * @param locale Locale to get the description for
     * @return Localized text string of the display name or <code>null</code>
     * if no Description exists for this locale
     */
    String getDescriptionText(Locale locale);
    
    void addDescription(Locale locale, String description);
    
	void addDisplayName(Locale locale, String displayName);
    
    

}
