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

import org.apache.jetspeed.om.registry.ClientEntry;
import org.apache.jetspeed.om.registry.ClientRegistry;
import org.apache.jetspeed.services.registry.JetspeedRegistry;
import org.apache.jetspeed.services.registry.RegistryService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
This class describes various browsers capabilities and provides the
ability to query them.

FIXME: the implementation should change to be configuration file based and
handle more browsers.

@author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
@version $Id$
*/
public class CapabilityMapFactory
{

    public static final String DEFAULT_AGENT = "Mozilla/4.0";

    public static final String AGENT_XML = "agentxml/1.0";
    
    public static final String CONTENT_ENCODING_KEY = "content.defaultencoding";
    public static final String DEFAULT_CONTENT_ENCODING_KEY = "US-ASCII";    
        
    private static final Log log = LogFactory.getLog(CapabilityMapFactory.class);

  
    /**
    Returns the map corresponding to the given user-agent
    
    @param useragent a user-agent string in the HTTP User-agent format
    @return the map corresponding to the user-agent
    */
    public static CapabilityMap getCapabilityMap(String useragent)
    {
        CapabilityMap map = null;

        if (useragent == null)
        {
            useragent = DEFAULT_AGENT;
        }

        ClientRegistry registry =
            (ClientRegistry) JetspeedRegistry.get(RegistryService.CLIENT);
        ClientEntry entry = registry.findEntry(useragent);

        if (entry == null)
        {
            if (useragent.equals(DEFAULT_AGENT))
            {
                log.error("CapabilityMap: Default agent not found in Client Registry !");
            } 
            else
            {
                if (log.isDebugEnabled())
                {
                    log.debug(
                        "CapabilityMap: useragent "
                            + useragent
                            + "unknown, falling back to default");
                }
                map = getDefaultCapabilityMap();
            }
        } 
        else
        {
            // TODO: pool this resource
            map = new BaseCapabilityMap(useragent, entry);
        }

        if (log.isDebugEnabled())
        {
            log.debug(
                "CapabilityMap: User-agent: "
                    + useragent
                    + " mapped to "
                    + map);
        }

        return map;
    }

    /**
    Returns the map corresponding to the given user-agent
    
    @param useragent a user-agent string in the HTTP User-agent format
    @return the map corresponding to the user-agent
    */
    public static CapabilityMap getDefaultCapabilityMap()
    {
        return getCapabilityMap(DEFAULT_AGENT);
    }
}
