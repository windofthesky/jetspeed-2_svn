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

import java.util.Iterator;
import java.util.Map;

/**
 * The PortletInfoEntry defines all the common description properties
 * for all the portlet related entries.
 *
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @version $Id$
 */
public interface PortletInfoEntry extends RegistryEntry
{

    /** @return the classname associated to this entry */
    public String getClassname();

    /** Sets the classname for this entry. This classname is used for
     *  instanciating the associated element
     *
     *  @param classname the classname used for instanciating the component
     *  associated with this entry
     */
    public void setClassname( String classname );

    /** @return an enumeration of this entry parameter names */
    public Iterator getParameterNames();

    /** Returns a map of parameter values keyed on the parameter names
     *  @return the parameter values map
     */
    public Map getParameterMap();

    /** Search for a named parameter and return the associated
     *  parameter object. The search is case sensitive.
     *
     *  @return the parameter object for a given parameter name
     *  @param name the parameter name to look for
     */
    public Parameter getParameter( String name );

    /** Adds a new parameter for this entry
     *  @param name the new parameter name
     *  @param value the new parameter value
     */
    public void addParameter( String name, String value );

    /** Adds a new parameter for this entry
     *  @param parameter the new parameter to add
     */
    public void addParameter( Parameter parameter );

    /** Removes all parameter values associated with the
     *  name
     *
     * @param name the parameter name to remove
     */
    public void removeParameter( String name );

    /**
     * Returns a list of the supported media type names
     *
     * @return an iterator on the supported media type names
     */
    public Iterator listMediaTypes();

    /**
     * Test if a given media type is supported by this entry.
     *
     * @param name the media type name to test for.
     * @return true is the media type is supported, false otherwise
     */
    public boolean hasMediaType(String name);

    /**
     * Add a new supported media type
     *
     * @param name the media type name to add.
     */
    public void addMediaType(String name);

    /**
     * Remove support for a given media type
     *
     * @param name the media type name to remove.
     */
    public void removeMediaType(String name);

    /** @return an enumeration of this entry tool names */
    public Iterator getToolNames();

    /** Returns a map of tool descriptors keyed on the tool names
     *  @return the tool descriptor map
     */
    public Map getToolMap();

    /** Search for a named tool and return the associated
     *  ToolDescriptor. The search is case sensitive.
     *
     *  @return the ToolDescriptor for a given name
     *  @param name the tool name to look for
     */
    public ToolDescriptor getTool( String name );

    /** Adds a new tool to this entry
     *  @param tool the new tool to add
     */
    public void addTool( ToolDescriptor tool );

    /** Removes the tool associated with the
     *  name
     *
     * @param name the name of the tool to remove
     */
    public void removeTool( String name );

}