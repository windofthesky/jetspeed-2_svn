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

import java.util.Vector;

/**
 * <P>
 * The <CODE>ClientEntry</CODE> interface represents one client inside
 * of the client registry. It is accessed by the portlet container
 * to get information about the clients.
 * </P>
 *
 * @author <a href="shesmer@raleigh.ibm.com">Stephan Hesmer</a>
 * @author <a href="raphael@apache.org">Raphaël Luta</a>
 */
public interface ClientEntry extends RegistryEntry
{
    /**
    * Set Client ID -- Assigns the Client ID
    * @param id
    */
    public void setClientId(int id);
 
    
       /**
    * Get Client ID
    * @return Client ID
    */
    public int getClientId();
       
    /**
     * Returns the pattern parameter of this client. The pattern is used
     * to match a client to the user agent used to access the portal. If
     * the pattern matches the user agent string, this client is recognized
     * as the one the user is currently working with.
     *
     * @return the pattern of this client
     */
    public String getUseragentpattern();

    /**
     * Sets the pattern used to match the user agent.
     *
     * @param useragentpattern
     *               the new pattern
     */
    public void setUseragentpattern(String useragentpattern);

    /**
     * Returns the manufacturer of this client
     *
     * @return the manufacturer of this client
     */
    public String getManufacturer();

    /**
     * Sets the new manufacturer of this client
     *
     * @param name   the new manufacturer
     */
    public void setManufacturer(String name);

    /**
     * Returns the model of this client
     *
     * @return the model of this client
     */
    public String getModel();

    /**
     * Sets the new model of this client
     *
     * @param name   the new model
     */
    public void setModel(String name);

    /**
     * Returns the version of this client
     *
     * @return the version of this client
     */
    public String getVersion();

    /**
     * Sets the new version of this client
     *
     * @param name   the new version
     */
    public void setVersion(String name);

    /**
     * Returns all supported mimetypes as <CODE>MimeTypeMap</CODE>.
     * The <CODE>MimeTypeMap</CODE> contains all mimetypes in decreasing
     * order of importance.
     *
     * @return the MimeTypeMap
     * @see MimeTypeMap
     */
    public Vector getMimetypes();
    
    /**
     * Set MimeTypes
     * @param mimetypes
     */
    public void setMimetypes(Vector mimetypes);

    /**
     * Returns all supported capablities as <CODE>CapabilityMap</CODE>.
     * The <CODE>CapabilityMap</CODE> contains all capabilities in arbitrary
     * order.
     *
     * @return the CapabilityMap
     * @see CapabilityMap
     */
    public Vector getCapabilities();
    public void setCapabilities(Vector capabilities);

}
