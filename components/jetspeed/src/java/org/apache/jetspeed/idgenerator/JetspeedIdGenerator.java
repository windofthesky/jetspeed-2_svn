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
package org.apache.jetspeed.idgenerator;



import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.picocontainer.Startable;

/**
 * Simple implementation of the IdGeneratorService.
 *
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class JetspeedIdGenerator implements IdGenerator, Startable
{
    private final static Log log = LogFactory.getLog(JetspeedIdGenerator.class);

    // default configuration values
    private final static long DEFAULT_CONFIG_COUNTER_START = 0x10000;
    private final static String DEFAULT_CONFIG_PEID_PREFIX = "P-";
    private final static String DEFAULT_CONFIG_PEID_SUFFIX = "";

    // configuration parameters
    private String peidPrefix = null;
    private String peidSuffix = null;
    protected long idCounter;

    public JetspeedIdGenerator()
    {
        this.idCounter = DEFAULT_CONFIG_COUNTER_START;
        this.peidPrefix = DEFAULT_CONFIG_PEID_PREFIX;
        this.peidSuffix = DEFAULT_CONFIG_PEID_SUFFIX;         
    }

    public JetspeedIdGenerator(long counterStart)
    {
        this.idCounter = counterStart;
        this.peidPrefix = DEFAULT_CONFIG_PEID_PREFIX;
        this.peidSuffix = DEFAULT_CONFIG_PEID_SUFFIX; 
    }

    public JetspeedIdGenerator(long counterStart, String prefix, String suffix)
    {
        this.idCounter = counterStart;
        this.peidPrefix = prefix;
        this.peidSuffix = suffix; 
    }
    
    public void start() 
    {
        log.info( "Start JetspeedIdGenerator");        
     }

    public void stop() 
    {
        log.info( "Shutdown for JetspeedIdGenerator called. idCounter = "
             + idCounter + " (" + Long.toHexString(idCounter) + ")" ); 
    }

    /**
     * Generate a Unique PEID
     * @return Unique PEID
     */
    public String getNextPeid()
    {
        long newid;

        synchronized(JetspeedIdGenerator.class)
        {
            newid = idCounter++;
        }
        
        return peidPrefix + Long.toHexString(System.currentTimeMillis()) + "-" 
               + Long.toHexString(newid) + peidSuffix;
    }
    
}
