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
package org.apache.jetspeed.om.registry.base;

import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.om.portlet.PortletDefinitionList;
import org.apache.pluto.om.servlet.WebApplicationDefinition;



/**
 *
 * @author  paul
 * @deprecated use org.apache.jetspeed.om.common.Base.BaseApplication
 */
public class BaseApplication implements PortletApplicationDefinition
{
    
    /** Holds value of property name. */
    private String name;
    
    /** Holds value of property url. */
    private String url;
    
    /** Holds value of property version. */
    private String version;
    
    /** Creates a new instance of BaseApplication */
    public BaseApplication()
    {
    }
    
    /** 
     * Getter for property name.
     * @return Value of property name.
     *
     */
    public String getName()
    {
        return this.name;
    }
    
    /**
     * Setter for property name.
     * @param name New value of property name.
     *
     */
    public void setName(String name)
    {
        this.name = name;
    }
    
    /**
     * Getter for property url.
     * @return Value of property url.
     *
     */
    public String getUrl()
    {
        return this.url;
    }
    
    /**
     * Setter for property url.
     * @param url New value of property url.
     *
     */
    public void setUrl(String url)
    {
        this.url = url;
    }
    
    /**
     * Getter for property version.
     * @return Value of property version.
     *
     */
    public String getVersion()
    {
        return this.version;
    }
    
    /**
     * Setter for property version.
     * @param version New value of property version.
     *
     */
    public void setVersion(String version)
    {
        this.version = version;
    }
    
    /**
     * @see org.apache.pluto.om.portlet.PortletApplicationDefinition#getId()
     */
    public ObjectID getId()
    {
        return null;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletApplicationDefinition#getPortletDefinitionList()
     */
    public PortletDefinitionList getPortletDefinitionList()
    {
        return null;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletApplicationDefinition#getWebApplicationDefinition()
     */
    public WebApplicationDefinition getWebApplicationDefinition()
    {
        return null;
    }

}
