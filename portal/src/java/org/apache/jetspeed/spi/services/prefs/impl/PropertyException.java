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
package org.apache.jetspeed.spi.services.prefs.impl;

import org.apache.jetspeed.exception.JetspeedException;

/**
 * <p>Exception throwns if a property or its set definition
 * cannot be found or already exists.</p>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class PropertyException extends JetspeedException
{

    /** Property set definition not found exception message. */
    public static final String PROPERTYSET_DEFINITION_NOT_FOUND
                        = "The specified property set definition does not exist.";
    /** Property set definition already exists exception message. */
    public static final String  PROPERTYSET_DEFINITION_ALREADY_EXISTS
                        = "The specified property set definition already exists.";
    /** Property key not found exception message. */
    public static final String PROPERTYKEY_NOT_FOUND
                        = "The specified property key does not exist.";
    /** Property key already exists exception message. */
    public static final String PROPERTYKEY_ALREADY_EXISTS
                        = "The specified property key already exists.";

    /**
     * <p>Default Constructor.</p>
     */
    public PropertyException()
    {
        super();
    }

    /**
     * <p>Constructor with exception message.</p>
     * @param message The exception message.
     */
    public PropertyException(String message)
    {
        super(message);
    }

    /**
     * <p>Constructor with nested exception.</p>
     * @param nested Nested exception.
     */
    public PropertyException(Throwable nested)
    {
        super(nested);
    }

    /**
     * <p>Constructor with exception message and nested exception.</p>
     * @param msg The exception message.
     * @param nested Nested exception.
     */
    public PropertyException(String msg, Throwable nested)
    {
        super(msg, nested);
    }

}
