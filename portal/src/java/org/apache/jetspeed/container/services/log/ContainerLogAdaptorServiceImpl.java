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
package org.apache.jetspeed.container.services.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.LogConfigurationException;
import org.apache.jetspeed.cps.BaseCommonService;
import org.apache.jetspeed.cps.CPSInitializationException;
import org.apache.pluto.services.log.LogService;


/**
 * Implements the logging service adaptor for the Pluto container 
 * adapting Jetspeed logging service implemented in Commons to Pluto
 * 
 * NOTE: this implementation may have performance issues
 *       since everytime we call isSomethingEnabled, we must get a logger
 *       I recommend deprecated Pluto's logging container service and 
 *       this adaptor once we get the Pluto source in Apache's CVS        
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$  
 */
public class ContainerLogAdaptorServiceImpl
    extends BaseCommonService
    implements ContainerLogAdaptorService, LogService 
{
    private final static Log defaultLog = LogFactory.getLog(ContainerLogAdaptorServiceImpl.class);
    
    public ContainerLogAdaptorServiceImpl()
    {
    }

    /**
     * This is the early initialization method called by the 
     * <code>Service</code> framework
     * @param conf The <code>ServletConfig</code>
     * @exception throws a <code>InitializationException</code> if the service
     * fails to initialize
     */
    public void init() 
        throws CPSInitializationException 
    {
        defaultLog.info("ContainerLogAdaptor init");
        
        if (isInitialized()) 
        {
            return;        
        }

        // initialization done
        setInit(true);

     }

    /**
     * This is the shutdown method called by the 
     * Turbine <code>Service</code> framework
     */
    public void shutdown() 
    {
        defaultLog.info("Shutdown for ContainerLogAdaptor called ");
    }
    
    
    public boolean isDebugEnabled(String aComponent)
    {
        Log log = getLogger(aComponent);
        return log.isDebugEnabled();
    }
    
    public boolean isInfoEnabled(String aComponent)
    {
        Log log = getLogger(aComponent);
        return log.isInfoEnabled();
    }

    public boolean isWarnEnabled(String aComponent)
    {
        Log log = getLogger(aComponent);
        return log.isWarnEnabled();
    }

    public boolean isErrorEnabled(String aComponent)
    {
        Log log = getLogger(aComponent);
        return log.isErrorEnabled();        
    }

    public void debug (String aComponent, String aMessage)
    {
        if (isDebugEnabled(aComponent))
        {
            System.out.println ("DEBUG  " + aComponent + "   " + aMessage);
        }
    }

    public void debug (String aComponent, String aMessage, Throwable aThrowable)
    {
        Log log = getLogger(aComponent);
        log.debug(aMessage);
    }

    public void info (String aComponent, String aMessage)
    {
        Log log = getLogger(aComponent);
        log.info(aMessage);        
    }

    public void warn (String aComponent, String aMessage)
    {
        Log log = getLogger(aComponent);
        log.warn(aMessage);
    }

    public void error (String aComponent, String aMessage, Throwable aThrowable)
    {
        Log log = getLogger(aComponent);
        log.error(aMessage, aThrowable);
    }

    public void error (String aComponent, Throwable aThrowable)
    {
        Log log = getLogger(aComponent);
        log.error("An exception has been thrown:", aThrowable);
    }

    /**
     * Given a string class name returns a logger for that class, or if we can't find a logger for the class
     * the it returns the default logger for this class
     * 
     * @param className
     * @return Log The logger configured for the given class name or the default logger if failed to load class
     */
    private Log getLogger(String className)
    {
        Class classe = null;
        Log log = defaultLog;
        
        try
        {        
            classe = Class.forName(className);
            log = LogFactory.getLog(classe);
        }
        catch (ClassNotFoundException e)
        {
            // use the default logger
        }
        catch (LogConfigurationException e)
        {
            // use the default logger            
        }
        return log;        
    }
    
}
