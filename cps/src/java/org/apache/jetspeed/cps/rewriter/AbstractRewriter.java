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
package org.apache.jetspeed.cps.rewriter;

import java.io.Reader;
import java.io.Writer;

/**
 * AbstractRewriter
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public abstract class AbstractRewriter implements Rewriter
{
    private String baseUrl = null;
    private boolean useProxy = false; 
        
    /* (non-Javadoc)
     * @see org.apache.jetspeed.syndication.services.crawler.rewriter.Rewriter#rewrite(ParserAdaptor, java.io.Reader)
     */
    public void parse(ParserAdaptor adaptor, Reader reader) throws RewriterException
    {
        adaptor.parse(this, reader);        
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.syndication.services.crawler.rewriter.Rewriter#rewrite(ParserAdaptor, java.io.Reader, java.io.Writer)
     */
    public void rewrite(ParserAdaptor adaptor, Reader reader, Writer writer) throws RewriterException
    {
        adaptor.rewrite(this, reader, writer);        
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.syndication.services.crawler.rewriter.Rewriter#rewriteUrl(java.lang.String, java.lang.String, java.lang.String)
     */
    public abstract String rewriteUrl(
        String url,
        String tag,
        String attribute);
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.syndication.services.crawler.rewriter.Rewriter#setBaseUrl(java.lang.String)
     */
    public void setBaseUrl(String base)
    {
        this.baseUrl = base;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.syndication.services.crawler.rewriter.Rewriter#getBaseUrl()
     */
    public String getBaseUrl()
    {
        return baseUrl;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.syndication.services.crawler.rewriter.Rewriter#getUseProxy()
     */
    public boolean getUseProxy()
    {
        return useProxy;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.syndication.services.crawler.rewriter.Rewriter#setUseProxy(boolean)
     */
    public void setUseProxy(boolean useProxy)
    {
        this.useProxy = useProxy;        
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.syndication.services.crawler.rewriter.Rewriter#enterSimpleTagEvent(java.lang.String, MutableAttributes)
     */
    public boolean enterSimpleTagEvent(String tag, MutableAttributes attrs)
    {
        return true;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.syndication.services.crawler.rewriter.Rewriter#exitSimpleTagEvent(java.lang.String, MutableAttributes)
     */
    public String exitSimpleTagEvent(String tag, MutableAttributes attrs)
    {
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.syndication.services.crawler.rewriter.Rewriter#enterStartTagEvent(java.lang.String, MutableAttributes)
     */
    public boolean enterStartTagEvent(String tag, MutableAttributes attrs)
    {
        return true;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.syndication.services.crawler.rewriter.Rewriter#exitStartTagEvent(java.lang.String, MutableAttributes)
     */
    public String exitStartTagEvent(String tag, MutableAttributes attrs)
    {
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.syndication.services.crawler.rewriter.Rewriter#enterEndTagEvent(java.lang.String)
     */
    public boolean enterEndTagEvent(String tag)
    {
        return true;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.syndication.services.crawler.rewriter.Rewriter#exitEndTagEvent(java.lang.String)
     */
    public String exitEndTagEvent(String tag)
    {
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.syndication.services.crawler.rewriter.Rewriter#enterText(char[], int)
     */
    public boolean enterText(char[] values, int param)
    {
        return true;
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.syndication.services.crawler.rewriter.Rewriter#convertTagEvent(java.lang.String, MutableAttributes)
     */
    public void enterConvertTagEvent(String tag, MutableAttributes attrs)
    {
    }
}
