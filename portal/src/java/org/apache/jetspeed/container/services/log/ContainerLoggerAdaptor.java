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

import org.apache.pluto.services.log.Logger;
import org.apache.commons.logging.Log;

/**
 * ContainerLoggerAdaptor
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class ContainerLoggerAdaptor implements Logger
{
    private Log log = null;

    public ContainerLoggerAdaptor(Log log) 
    {
        this.log = log;
    }
    
    /* (non-Javadoc)
     * @see org.apache.pluto.services.log.Logger#isDebugEnabled()
     */
    public boolean isDebugEnabled()
    {
        return log.isDebugEnabled();
    }
    
    /* (non-Javadoc)
     * @see org.apache.pluto.services.log.Logger#isInfoEnabled()
     */
    public boolean isInfoEnabled()
    {
        return log.isInfoEnabled();
    }
    
    /* (non-Javadoc)
     * @see org.apache.pluto.services.log.Logger#isWarnEnabled()
     */
    public boolean isWarnEnabled()
    {
        return log.isWarnEnabled();
    }
    
    /* (non-Javadoc)
     * @see org.apache.pluto.services.log.Logger#isErrorEnabled()
     */
    public boolean isErrorEnabled()
    {
        return log.isErrorEnabled();
    }
    
    /* (non-Javadoc)
     * @see org.apache.pluto.services.log.Logger#debug(java.lang.String)
     */
    public void debug(String aMessage)
    {
        log.debug(aMessage);    
    }
    
    /* (non-Javadoc)
     * @see org.apache.pluto.services.log.Logger#debug(java.lang.String, java.lang.Throwable)
     */
    public void debug(String aMessage, Throwable aThrowable)
    {
        log.debug(aMessage, aThrowable);
    }
    
    /* (non-Javadoc)
     * @see org.apache.pluto.services.log.Logger#info(java.lang.String)
     */
    public void info(String aMessage)
    {
        log.info(aMessage);
    }
    
    /* (non-Javadoc)
     * @see org.apache.pluto.services.log.Logger#warn(java.lang.String)
     */
    public void warn(String aMessage)
    {
        log.warn(aMessage);
    }
    
    /* (non-Javadoc)
     * @see org.apache.pluto.services.log.Logger#error(java.lang.String)
     */
    public void error(String aMessage)
    {
        log.error(aMessage);
    }
    
    /* (non-Javadoc)
     * @see org.apache.pluto.services.log.Logger#error(java.lang.String, java.lang.Throwable)
     */
    public void error(String aMessage, Throwable aThrowable)
    {
        log.error(aMessage, aThrowable);
    }
    
    /* (non-Javadoc)
     * @see org.apache.pluto.services.log.Logger#error(java.lang.Throwable)
     */
    public void error(Throwable aThrowable)
    {
        log.error(aThrowable);
    }
    
}
