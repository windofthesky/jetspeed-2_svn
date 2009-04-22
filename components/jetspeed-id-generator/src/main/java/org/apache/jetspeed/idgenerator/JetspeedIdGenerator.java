/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.idgenerator;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple implementation of the IdGeneratorService.
 *
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class JetspeedIdGenerator implements IdGenerator
{
    private final static Logger log = LoggerFactory.getLogger(JetspeedIdGenerator.class);

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
