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

import org.apache.jetspeed.om.common.DublinCore;
import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.portlet.PortletDefinitionList;
import org.apache.pluto.om.servlet.WebApplicationDefinition;

/**
 * MutablePortletApplication
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
/**
 *
 * MutablePortletApplication
 *
 * Extends the <code>org.apache.pluto.om.portlet.PortletApplicationDefinition</code>
 * interface adding mutator methods for those attributes that do not have them
 * so as to make manipulating the portlet OM easier.
 * It has additional methods to make it easier to use within Jetspeed.
 *
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface MutablePortletApplication extends PortletApplicationDefinition, Serializable
{
    GenericMetadata getMetadata();
    
    void setMetadata(GenericMetadata metadata);
    
    void setWebApplicationDefinition(WebApplicationDefinition wad);

    void setName(String name);

    String getName();

    void addPortletDefinition(PortletDefinition pd);

    Collection getPortletDefinitions();

    PortletDefinition getPortletDefinitionByName(String name);

    void setPortletDefinitionList(PortletDefinitionList portlets);

    void setApplicationIdentifier(String applicationIndentifier);

    String getApplicationIdentifier();

    /**
     * @return
     */
    String getDescription();

    /**
     * @param string
     */
    void setDescription(String string);

    /**
     * @param objectID
     */
    void setId(String objectID);

    /**
     *
     * @param version
     */
    void setVersion(String version);

    /**
     * Sets the Portlet Application type.
     * Valid values are:
     * <p>
     *      {@link MutablePortletApplication#WEBAPP} - A standard web application, stored in the web application
     *               server's web application space.
     * <p>
     *      {@link MutablePortletApplication#LOCAL} - A local portlet application stored within Jetspeed's web application.
     * <p>
     * @param type The type of portlet application.
     */
    void setApplicationType(int type);

    /**
     * Gets the Portlet Application type.
     * Valid values are:
     * <p>
     *      {@link MutablePortletApplication#WEBAPP} - A standard web application, stored in the web application
     *               server's web application space.
     * <p>
     *      {@link MutablePortletApplication#LOCAL} - A local portlet application stored within Jetspeed's web application.
     * <p>
     * @return The type of portlet application.
     */
    int getApplicationType();

    /**
     * Marks this application as a standard web application,
     * stored in the web application server's web application space.
     */
    public static final int WEBAPP = 0;

    /**
     * Marks this application as a LOCAL portlet application,
     * stored in Jetspeed managed portlet application space.
     */
    public static final int LOCAL = 1;
}
