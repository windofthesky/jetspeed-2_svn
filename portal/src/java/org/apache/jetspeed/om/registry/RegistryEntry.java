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
package org.apache.jetspeed.om.registry;

// Java imports
import org.apache.jetspeed.om.SecurityReference;

/** RegistryEntry is the base interface that objects must implement in order
 * to be used with the Registry service.
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
 * @version $Id$
 */
public interface RegistryEntry
{
    /**
     * @return the id of this entry. This value should be unique within its
     * registry class.
     */
    public long getId();

    /**
     * @return the name of this entry. This value should be unique within its
     * registry class.
     */
    public String getName();

    /**
     * Changes the name of this entry
     * @param name the new name for this entry
     */
    public void setName(String name);

    /**
     * @return the entry title in the default locale for this entry, if set
     */
    public String getTitle();

    /**
     * Sets the title of the portlet entry
     * @param title the new title for the entry
     */
    public void setTitle(String title);

    /**
     * @return the entry description in the default locale for this entry, if set
     */
    public String getDescription();

    /**
     * Sets the description for the portlet entry
     * @param description the new description for the entry
     */
    public void setDescription(String description);

    /**
     * @return the security properties for this entry
     */
    public Security getSecurity();

    /**
     * Set the security properties for this entry
     * @param security the new security properties
     */
    public void setSecurity(Security security);

    /**
     * @return the metainfo properties for this entry
     */
    public MetaInfo getMetaInfo();

    /**
     * Set the metainfo properties for this entry
     * @param metainfo the new metainfo properties
     */
    public void setMetaInfo(MetaInfo metainfo);


    /**
     * Test if this entry should be visible in a list of the registry contents
     * @return true if the entry should be hidden
     */
    public boolean isHidden();

    /** Modify the visibility status of this entry
     * @param hidden the new status. If true, the entry will not be displayed in
     * a registry list
     */
    public void setHidden(boolean hidden);

    /** Getter for property securityRef.
     * @return Value of property securityRef.
     */
    public SecurityReference getSecurityRef();

    /** Setter for property securityRef.
     * @param securityRef New value of property securityRef.
     */
    public void setSecurityRef(SecurityReference securityRef);

}
