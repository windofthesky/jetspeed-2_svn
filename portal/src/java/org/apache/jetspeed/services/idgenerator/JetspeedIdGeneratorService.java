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
package org.apache.jetspeed.services.idgenerator;


import org.apache.fulcrum.BaseService;
import org.apache.fulcrum.InitializationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Simple implementation of the IdGeneratorService.
 *
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @version $Id$
 */
public class JetspeedIdGeneratorService extends BaseService
    implements IdGeneratorService
{
    private final static Log log = LogFactory.getLog(JetspeedIdGeneratorService.class);

    // configuration keys
    private final static String CONFIG_COUNTER_START = "counter.start";
    private final static String CONFIG_PEID_PREFIX = "peid.prefix";
    private final static String CONFIG_PEID_SUFFIX = "peid.suffix";

    // default configuration values
    private final static long DEFAULT_CONFIG_COUNTER_START = 0x10000;
    private final static String DEFAULT_CONFIG_PEID_PREFIX = "P-";
    private final static String DEFAULT_CONFIG_PEID_SUFFIX = "";

    // configuration parameters
    private static String peidPrefix = null;
    private static String peidSuffix = null;

    protected static long idCounter;

    /**
     * This is the early initialization method called by the 
     * <code>Service</code> framework
     * @param conf The <code>ServletConfig</code>
     * @exception throws a <code>InitializationException</code> if the service
     * fails to initialize
     */
    public void init() 
        throws InitializationException 
    {
        log.info( "JetspeedIdGeneratorService init");
        
        if (isInitialized()) 
        {
            return;        
        }

        initConfiguration();        

        // initialization done
        setInit(true);

     }

    /**
     * This is the shutdown method called by the 
     * Turbine <code>Service</code> framework
     */
    public void shutdown() 
    {
        log.info( "Shutdown for JetspeedIdGeneratorService called. idCounter = "
             + idCounter + " (" + Long.toHexString(idCounter) + ")" ); 
    }

    /**
     * Loads the configuration parameters for this service from the
     * JetspeedResources.properties file.
     *
     * @exception throws a <code>InitializationException</code> if the service
     * fails to initialize
     */
    private void initConfiguration() throws InitializationException
    {
        peidPrefix = getConfiguration().getString( CONFIG_PEID_PREFIX, DEFAULT_CONFIG_PEID_PREFIX );
        peidSuffix = getConfiguration().getString( CONFIG_PEID_SUFFIX, DEFAULT_CONFIG_PEID_SUFFIX );
        synchronized(JetspeedIdGeneratorService.class)
        {
            idCounter = getConfiguration().getLong( CONFIG_COUNTER_START, DEFAULT_CONFIG_COUNTER_START );
        }
        
   }
    /** Creates new JetspeedIdGeneratorService */
    public JetspeedIdGeneratorService() 
    {
    }

    /**
     * Generate a Unique PEID
     * @return Unique PEID
     */
    public String getNextPeid()
    {
        long newid;

        synchronized(JetspeedIdGeneratorService.class)
        {
            newid = idCounter++;
        }
        
        return peidPrefix + Long.toHexString(System.currentTimeMillis()) + "-" 
               + Long.toHexString(newid) + peidSuffix;
    }
    
}