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
package org.apache.jetspeed.rewriter;

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
