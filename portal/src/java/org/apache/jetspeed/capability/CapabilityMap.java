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
package org.apache.jetspeed.capability;

import java.util.Iterator;


/**
 * This interface provides lookup features on the capabilities supported
 * by a client user agent.
 *
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
 * @version $Id$
 */
public interface CapabilityMap
{

    /** Handle HTML Table */
    public static final int HTML_TABLE = 0;

    /** Handle inline image display */
    public static final int HTML_IMAGE = 1;

    /** Handle form handling */
    public static final int HTML_FORM = 2;

    /** Handle frames */
    public static final int HTML_FRAME = 3;

    /** Handle client-side applet */
    public static final int HTML_JAVA = 17;
    public static final int HTML_JAVA1_0 = 4;
    public static final int HTML_JAVA1_1 = 5;
    public static final int HTML_JAVA1_2 = 6;

    /** Handle client-side javascript */
    public static final int HTML_JSCRIPT = 18;
    public static final int HTML_JSCRIPT1_0 = 7;
    public static final int HTML_JSCRIPT1_1 = 8;
    public static final int HTML_JSCRIPT1_2 = 9;

    /** Handle activex controls */
    public static final int HTML_ACTIVEX = 10;

    /** Handle CSS1 */
    public static final int HTML_CSS1 = 11;

    /** Handle CSS2 */
    public static final int HTML_CSS2 = 12;

    /** Handle CSSP */
    public static final int HTML_CSSP = 13;

    /** Handle XML */
    public static final int HTML_XML = 14;

    /** Handle XSL */
    public static final int HTML_XSL = 15;

    /** Handle DOM */
    public static final int HTML_DOM = 16;

    /**
    Returns the preferred MIME type for the current user-agent
    */
    public MimeType getPreferredType();

    /**
    Returns the preferred media type for the current user-agent
    */
    public String getPreferredMediaType();

    /**
     * Returns an ordered list of supported media-types, from most preferred
     * to least preferred
     */
    public Iterator listMediaTypes();

    /**
    Returns the user-agent string
    */
    public String getAgent();

    /**
    Checks to see if the current agent has the specified capability
    */
    public boolean hasCapability( int cap );

    /**
    Checks to see if the current agent has the specified capability
    */
    public boolean hasCapability( String capability );

    /**
    Get the mime types that this CapabilityMap supports.
    */
    public MimeType[] getMimeTypes();

    /**
    Return true if this CapabilityMap supports the given MimeType
    */
    public boolean supportsMimeType( MimeType mimeType );

    /**
     * Return true if this CapabilityMap supports the given media type
     *
     * @param media the name of a media type registered in the
     * MediaType regsitry
     *
     * @return true is the capabilities of this agent at least match those
     * required by the media type
     */
    public boolean supportsMediaType( String media );

    /**
    Create a map -> string representation
    */
    public String toString();

}

