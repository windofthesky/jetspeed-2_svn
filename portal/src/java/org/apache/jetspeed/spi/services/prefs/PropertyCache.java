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
package org.apache.jetspeed.spi.services.prefs;

import java.io.Serializable;
import java.util.Map;

/**
 * <p>Simple cache used to retrieve properties.
 * The key idea behind the caching facility it to store
 * a map of property: propertyKeyMap/value where:</p>
 * <ul>
 *      <li><code>propertyKeyMap</code> is a map of:
 *          <ul>
 *              <li><code>PROPERTYKEY_NAME</code>: the property
 *              key name.</li>
 *              <li><code>PROPERTYKEY_TYPE</code>: the property
 *              key type.</li>
 *              <li><code>PROPERTYSET_NAME</code>: the property
 *              set name.</li>
 *          </ul>
 *      </li>
 *      <li><code>value</code>: The property value.</li>
 * </ul>
 *
 * TODO Identify the lifecycle scope of the cache object.
 *
 * @author <a href="david@sensova.com">David Le Strat</a>
 */
public interface PropertyCache extends Serializable
{

    /** The property key name. */
    String PROPERTYKEY_NAME = "propertyKeyName";

    /** The property key type. */
    String PROPERTYKEY_TYPE = "propertyKeyType";

    /** The property set name. */
    String PROPERTYSET_NAME = "propertySetName";

    /**
     * <p>Add the propertyKeyMap / property value pair to the cache map.</p>
     * @param propertyKeyMap The property key map.
     * @param value The property value.
     */
    void add(Map propertyKeyMap, Object value);

    /**
     * <p>Get the entire <code>Map</code> that backs the cache.</p>
     * @return The cache map.
     */
    Map getMap();

    /**
     * <p>Remove a property from the cache.</p>
     * @param propertyKeyMap The property key map.
     */
    void remove(Map propertyKeyMap);
}
