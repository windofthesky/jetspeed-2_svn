/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
