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

package org.apache.cornerstone.framework.api.config;

import java.util.Properties;

/**
Interface for configurability which all configurable classes implement.
*/

public interface IConfigurable
{
    public static final String REVISION = "$Revision$";

    /**
     * Gets the value of property p in my configuration.
     * @param p name of configuration property.
     * @return value of configuration property.
     */
    public String getConfigProperty(String p);

    /**
     * Gets the value of property p1.p2 in my configuration.
     * @param p1 name of segement 1 of configuration property.
     * @param p2 name of segement 2 of configuration property.
     * @return value of configuration property
     */
    public String getConfigProperty(String p1, String p2);

    /**
     * Gets the value of property p1.p2.p3 in my configuration.
     * @param p1 name of segement 1 of configuration property.
     * @param p2 name of segement 2 of configuration property.
     * @param p3 name of segement 3 of configuration property.
     * @return value of configuration property
     */
    public String getConfigProperty(String p1, String p2, String p3);

    /**
     * Gets the value of property p1.p2.p3.p4 in my configuration.
     * @param p1 name of segement 1 of configuration property.
     * @param p2 name of segement 2 of configuration property.
     * @param p3 name of segement 3 of configuration property.
     * @param p4 name of segement 4 of configuration property.
     * @return value of configuration property
     */
    public String getConfigProperty(String p1, String p2, String p3, String p4);

    /**
     * Gets the value of property p in my configuration.
     * @param p name of configuration property.
     * @param defaultValue value used when configuration properties is
     *   missing
     * @return value of configuration property.
     */
    public String getConfigPropertyWithDefault(String p, String defaultValue);

    /**
     * Gets the value of property p in my configuration.
     * @param p1 name of segement 1 of configuration property.
     * @param p2 name of segement 2 of configuration property.
     * @param defaultValue value used when configuration properties is
     *   missing
     * @return value of configuration property.
     */
    public String getConfigPropertyWithDefault(String p1, String p2, String defaultValue);

    /**
     * Gets the value of property p in my configuration.
     * @param p1 name of segement 1 of configuration property.
     * @param p2 name of segement 2 of configuration property.
     * @param p3 name of segement 3 of configuration property.
     * @param defaultValue value used when configuration properties is
     *   missing
     * @return value of configuration property.
     */
    public String getConfigPropertyWithDefault(String p1, String p2, String p3, String defaultValue);

    /**
     * Gets the value of property p in my configuration.
     * @param p1 name of segement 1 of configuration property.
     * @param p2 name of segement 2 of configuration property.
     * @param p3 name of segement 3 of configuration property.
     * @param p4 name of segement 4 of configuration property.
     * @param defaultValue value used when configuration properties is
     *   missing
     * @return value of configuration property.
     */
    public String getConfigPropertyWithDefault(String p1, String p2, String p3, String p4, String defaultValue);

    /**
     * Overwrites configuration with another.
     * @param overwrites configuration used to overwrite.
     */
    public void overwriteConfig(Properties overwrites);
}