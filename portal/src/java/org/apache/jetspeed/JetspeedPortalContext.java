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
package org.apache.jetspeed;

import java.util.HashMap;
import org.apache.commons.configuration.Configuration;
import org.apache.jetspeed.engine.Engine;

/**
 * Implementation of Portal Context associated with running thread of the engine
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 */
public class JetspeedPortalContext implements PortalContext
{
    /**
     * The engine associated with this context.
     */
    private Engine engine = null;

    /**
     * Runtime attributes.
     */
    private HashMap attributes = new HashMap();

    /**
     * Configuration state
     */
    private Configuration configuration = null;

    /**
     * The base from which the Jetspped application will operate.
     */
    private String applicationRoot;


    public JetspeedPortalContext(Engine engine)
    {
        this.engine = engine;
    }

    private JetspeedPortalContext()
    {
    }

    // ------------------------------------------------------------------------
    //  A C C E S S O R S 
    // ------------------------------------------------------------------------

    /**
     * Returns the configuration properties for this Jetspeed engine context.
     *
     * @return a <code>Configuration</code> containing the configuration properties for this Jetspeed context.
     */
    public Configuration getConfiguration()
    {
        return configuration;
    }

    public String getConfigurationProperty(String key)
    {
        return configuration.getString(key);
    }

    /**
     * Set the configuration properties for this Jetspeed engine context.
     *
     * @param configuration - the configuration properties
     */
    public void setConfiguration(Configuration configuration)
    {
        this.configuration = configuration;
    }

    /**
     * Returns the application root for this Jetspeed engine context.
     *
     * @return a <code>String</code> containing the application root path for this Jetspeed context.
     */
    public String getApplicationRoot()
    {
        return applicationRoot;
    }

    /**
     * Sets the application root path for this Jetspeed engine context.
     *
     * @param applicationRoot - the applicationRoot path on the file system.
     */
    public void setApplicationRoot(String applicationRoot)
    {
        this.applicationRoot = applicationRoot;
    }

    /**
     * Returns the engine associated with this context.
     *
     * @return an <code>Engine</code> associated with this context
     */
    public Engine getEngine()
    {
        return this.engine;
    }

    /**
     * Returns the engine attribute with the given name, or null if there is no attribute by that name.
     *
     * @return an <code>Object</code> containing the value of the attribute, or null if no attribute exists matching the given name
     */
    public Object getAttribute(String name)
    {
        return attributes.get(name);
    }


    /**
     * Binds an object to a given attribute name in this servlet context.
     *
     * @param  name - a <code>String</code> specifying the name of the attribute
     * @param value - an <code>Object</code> representing the attribute to be bound
     */
    public void setAttribute(String name, Object value)
    {
        attributes.put(name, value);
    }


}